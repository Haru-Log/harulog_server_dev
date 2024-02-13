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
     * 채팅방 들어가기
     * @return 채팅방 메세지 전체 조회. (todo: 커서 기반 페이징)
     */
    @Override
    public Response<MessageListDTO> roomIn(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.roomIn : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        checkPermission(roomId, userNickname);

        // 채팅방 - 유저 binding
        rabbitMQConfig.binding(roomId, userNickname);

        // 채팅방 메세지 조회
        List<Message> messages = messageRepository.findByChatRoomId(roomId);
        List<MessageDTO> result = messages.stream()
                .map(MessageDTO::of)
                .toList();

        int userCount = chatRoomRepository.findById(roomId).get().getUsers().size();

        return Response.ok(MessageListDTO.of(roomId, userCount, result));
    }

    /**
     * 채팅방 나가기
     * 1. 참여 유저인지 확인
     * 2. 마지막 읽은 메세지 id 저장
     * 3. 채팅방-유저 UNBINDING
     */
    @Override
    public Response<Void> roomOut(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.roomOut : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser cru = checkPermission(roomId, userNickname);

        // 마지막 메세지 id 저장
        cru.setLastReadMessageId(messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(roomId).getId());

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