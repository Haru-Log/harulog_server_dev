package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.chat.domain.dto.request.MessageRequest;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
import goojeans.harulog.chat.util.MessageType;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 메세지 조회
 * 채팅방에 들어가기(in), 나가기(out), 메세지 전송
 * (입장, 퇴장 메세지는 채팅방에 유저가 추가되고 삭제 될 때 -> ChatRoomUserService에서 구현)
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;
    private final RabbitMQConfig rabbitMQConfig;


    /**
     * 보통 메세지 조회할 때 2-30개씩 조회 하는 것이 일반적이어서 -> 30개씩 조회
     */

    // 이전 메세지 조회 : 내림차순 (최신부터)
    @Override
    public List<Message> getMessagesBefore(ChatRoom chatroom, Long lastReadMessageId, int size) {
        return messageRepository.findBeforeMessagesWithPagination(chatroom.getId(), lastReadMessageId, size);
    }

    // 이후 메세지 조회 : 오름차순 (과거부터)
    @Override
    public List<Message> getMessagesAfter(ChatRoom chatroom, Long lastReadMessageId, int size) {
        return messageRepository.findAfterMessagesWithPagination(chatroom.getId(), lastReadMessageId, size);
    }

    // 마지막 메세지 포함해서 이후 메세지 조회 : 오름차순 (과거부터)
    @Override
    public List<Message> getMessagesAfterIncludeLastMessage(ChatRoom chatroom, Long lastReadMessageId, int size) {
        return messageRepository.findAfterMessagesWithPaginationIncludeLastMessage(chatroom.getId(), lastReadMessageId, size);
    }

    /**
     * 채팅방 메세지 조회 : 스크롤 올릴 때
     * 1. 채팅방 찾기
     * 2. 프론트에게 받은 메세지 id로부터 이전 메세지 30개씩 조회
     */
    @Override
    public Response<MessageListDTO> getMessagesBeforeResponse(String roomId, Long lastReadMessageId) {
        log.trace("scroll up : " + roomId + ", " + lastReadMessageId + " 이전 메세지 조회");

        ChatRoom chatRoom = findChatRoom(roomId);

        // 마지막으로 읽은 메세지부터 20개씩 조회
        List<Message> messages = getMessagesBefore(chatRoom, lastReadMessageId, 20);

        // 더이상 조회할 메세지가 없을 때 에러 처리 (but 200)
        checkMessageList(messages);

        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        // 채팅방에 참여하고 있는 유저 수
        int userCount = chatRoom.getUsers().size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    /**
     * 채팅방 메세지 조회 : 스크롤 내릴 때
     * 1. 채팅방 찾기
     * 2. 프론트에게 받은 메세지 id로부터 이후 메세지 30개씩 조회
     */
    @Override
    public Response<MessageListDTO> getMessagesAfterResponse(String roomId, Long lastReadMessageId) {
        log.trace("scroll down : " + roomId + ", " + lastReadMessageId + " 이후 메세지 조회");

        ChatRoom chatRoom = findChatRoom(roomId);

        // 마지막으로 읽은 메세지부터 30개씩 조회
        List<Message> messages = getMessagesAfter(chatRoom, lastReadMessageId, 20);

        // 더이상 조회할 메세지가 없을 때 에러 처리 (but 200)
        checkMessageList(messages);

        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        // 채팅방에 참여하고 있는 유저 수
        int userCount = chatRoom.getUsers().size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    // 조회한 메세지가 없으면 에러 (but 200)
    @Override
    public void checkMessageList(List<Message> messages) {

        if(messages.isEmpty()){
            throw new BusinessException(ResponseCode.NO_MORE_MESSAGE);
        }
    }

    /**
     * 채팅방 들어가기
     * 1. 참여 유저인지 확인
     * 2. 채팅방 - 유저 binding
     * 3. 마지막으로 읽은 메세지 이전 20개 조회
     * 4. 마지막으로 읽은 메세지부터 30개씩 조회
     * @return 마지막으로 읽었던 메세지부터 30개씩 조회
     */
    @Override
    public Response<MessageListDTO> roomIn(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.roomIn : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser cru = checkPermission(roomId, userNickname);

        // 채팅방 - 유저 binding
        rabbitMQConfig.binding(roomId, userNickname);

        // 채팅방 메세지 조회 : 마지막으로 읽은 메세지 이전 5개 조회
        List<Message> beforeMessages = getMessagesBefore(cru.getChatRoom(), cru.getLastReadMessageId(), 5);
        Collections.reverse(beforeMessages);
        List<MessageDTO> result = beforeMessages.stream()
                .map(MessageDTO::of)
                .collect(Collectors.toList()); // 변경 가능한 리스트를 만듭니다.

        // 채팅방 메세지 조회 : 마지막으로 읽은 메세지부터 30개씩 조회
        List<Message> messages = getMessagesAfterIncludeLastMessage(cru.getChatRoom(), cru.getLastReadMessageId(), 30);
        result.addAll(messages.stream()
                .map(MessageDTO::of)
                .collect(Collectors.toList())); // 여기도 변경 가능한 리스트를 만듭니다.

        // 채팅방에 참여하고 있는 유저 수
        int userCount = chatRoomUserRepository.findByChatRoomId(roomId).size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    /**
     * 채팅방 나가기
     * 1. 참여 유저인지 확인
     * 2. 마지막 읽은 메세지 id 저장
     * 3. 채팅방-유저 UNBINDING
     */
    @Transactional
    @Override
    public Response<Void> roomOut(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.roomOut : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser cru = checkPermission(roomId, userNickname);

        // 마지막 메세지 id 저장
        cru.setLastReadMessageId(messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId).getId());
        cru.setUnreadMessageCount(0);    // 읽지 않은 메세지 개수 초기화
        chatRoomUserRepository.save(cru);

        // 채팅방-유저 UNBINDING
        rabbitMQConfig.unBinding(roomId, userNickname);

        return Response.ok();
    }

    /**
     * 메세지 전송
     * @return 전송한 메세지를 DTO로 반환
     */
    @Transactional // 메세지 전송 시, 채팅방 업데이트 시간 변경
    @Override
    public MessageDTO sendMessage(String roomId, MessageRequest messageRequest) {
        String userNickname = messageRequest.getSender();
        MessageType messageType = messageRequest.getMessageType();
        String content = messageRequest.getContent();

        log.trace("MessageServiceImpl.sendMessage : " + content + ", [" + roomId + " : " + userNickname + "]");

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser find = checkPermission(roomId, userNickname);

        Message message = Message.create(find.getChatRoom(), find.getUser(), messageType, content);
        messageRepository.save(message);

        /**
         * 채팅방에 마지막 메세지 id 변경 : 오로지 "채팅 메세지"에 한정. enter, exit 메세지는 제외
         * 1. 채팅방에 마지막 메세지 id 변경
         * 2. 채팅방에 참여하고 있는 모든 ChatRoomUser의 lastReadMessageId를 기준으로 쌓인 메세지 개수 +1
         */
        find.getChatRoom().setLastMessageId(message.getId());
        chatRoomRepository.save(find.getChatRoom());

        // 채팅방에 참여하고 있는 모든 ChatRoomUser의 lastReadMessageId를 기준으로 쌓인 메세지 개수 +1
        List<ChatRoomUser> cruList = chatRoomUserRepository.findByChatRoomId(roomId);
        cruList.forEach(cru -> {
            cru.setUnreadMessageCount(cru.getUnreadMessageCount() + 1);
            chatRoomUserRepository.save(cru);
        });

        return MessageDTO.of(message);
    }

    private Users findUser(String userNickname){
        return userRepository.findUsersByNickname(userNickname)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(String roomId){
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }

    private ChatRoomUser checkPermission(String roomId, String userNickname){
        Users user = findUser(userNickname);
        findChatRoom(roomId);
        return chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
    }
}