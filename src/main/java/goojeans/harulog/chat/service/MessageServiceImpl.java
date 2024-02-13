package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
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

import java.time.LocalDateTime;
import java.util.List;

import static goojeans.harulog.chat.util.MessageType.*;

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
    public List<Message> getMessagesBefore(ChatRoom chatroom, Long lastReadMessageId) {
        return messageRepository.findBeforeMessagesWithPagination(chatroom.getId(), lastReadMessageId, 30);
    }

    // 이후 메세지 조회 : 오름차순 (과거부터)
    @Override
    public List<Message> getMessagesAfter(ChatRoom chatroom, Long lastReadMessageId) {
        return messageRepository.findAfterMessagesWithPagination(chatroom.getId(), lastReadMessageId, 30);
    }

    /**
     * 채팅방 메세지 조회 : 스크롤 올릴 때
     * todo: 더이상 조회할 메세지가 없을 때 처리
     */
    @Override
    public Response<MessageListDTO> getMessagesBeforeResponse(String roomId, Long lastReadMessageId) {
        log.trace("scroll up : " + roomId + ", " + lastReadMessageId + " 이전 메세지 조회");

        ChatRoom chatRoom = findChatRoom(roomId);

        // 마지막으로 읽은 메세지부터 30개씩 조회
        List<Message> messages = getMessagesBefore(chatRoom, lastReadMessageId);
        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        // 채팅방에 참여하고 있는 유저 수
        int userCount = chatRoom.getUsers().size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    /**
     * 채팅방 메세지 조회 : 스크롤 내릴 때
     * todo: 더이상 조회할 메세지가 없을 때 처리
     */
    @Override
    public Response<MessageListDTO> getMessagesAfterResponse(String roomId, Long lastReadMessageId) {
        log.trace("scroll down : " + roomId + ", " + lastReadMessageId + " 이후 메세지 조회");

        ChatRoom chatRoom = findChatRoom(roomId);

        // 마지막으로 읽은 메세지부터 30개씩 조회
        List<Message> messages = getMessagesAfter(chatRoom, lastReadMessageId);
        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        // 채팅방에 참여하고 있는 유저 수
        int userCount = chatRoom.getUsers().size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    /**
     * 채팅방 들어가기
     *
     * 1. 참여 유저인지 확인
     * 2. 채팅방 - 유저 binding
     * 3. 마지막으로 읽은 메세지부터 30개씩 조회
     * @return 마지막으로 읽었던 메세지부터 30개씩 조회
     */
    @Override
    public Response<MessageListDTO> roomIn(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.roomIn : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser cru = checkPermission(roomId, userNickname);

        // 채팅방 - 유저 binding
        rabbitMQConfig.binding(roomId, userNickname);

        // 채팅방 메세지 조회
        List<Message> messages = getMessagesAfter(cru.getChatRoom(), cru.getLastReadMessageId());
        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        // 채팅방에 참여하고 있는 유저 수
        int userCount = cru.getChatRoom().getUsers().size();

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
    public MessageDTO sendMessage(String roomId, String userNickname, String content) {
        log.trace("MessageServiceImpl.sendMessage : " + content + ", [" + roomId + " : " + userNickname + "]");

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser find = checkPermission(roomId, userNickname);

        Message message = Message.create(find.getChatRoom(), find.getUser(), TALK, content);
        messageRepository.save(message);

        // 채팅방 업데이트 시간 변경 : 오로지 "채팅 메세지"에 한정. enter, exit 메세지는 제외
        find.getChatRoom().setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(find.getChatRoom());

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