package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.chat.repository.MessageRepository;
import goojeans.harulog.chat.util.MessageType;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;

    // 채팅방 전체 메세지 조회
    public Response<List<MessageDTO>> getMessages(String roomId, String userNickname){
        log.trace("MessageServiceImpl.getMessages : " + roomId);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        checkPermission(roomId, userNickname);

        // 채팅방 메세지 조회
        List<Message> messages = messageRepository.findByChatRoomId(roomId);
        List<MessageDTO> result = messages.stream()
                .map(message -> MessageDTO.of(message))
                .toList();

        return Response.ok(result);
    }

    // 채팅방 구독 여부 확인
    @Override
    public boolean existSubscribe(String roomId, String userNickname) {
        Users user = findUser(userNickname);
        return chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId()).isPresent();
    }

    // 채팅방 구독 & 입장 메세지 반환
    @Override
    public MessageDTO subscribe(String roomId, String userNickname) {

        ChatRoom room = findChatRoom(roomId);
        Users user = findUser(userNickname);

        // 채팅방 구독
        chatRoomUserRepository.save(ChatRoomUser.create(room, user));

        // 입장 메세지 생성 및 저장
        String content = user.getNickname() + "님이 입장하셨습니다.";
        Message message = Message.create(room, user, MessageType.ENTER, content);
        messageRepository.save(message);

        return MessageDTO.of(message);
    }

    // 메세지 전송
    @Override
    public MessageDTO send(String roomId, String userNickname, String content) {
        log.trace("MessageServiceImpl.send : " + roomId + ", " + userNickname + ", " + content);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser find = checkPermission(roomId, userNickname);

        Message message = Message.create(find.getChatRoom(), find.getUser(), MessageType.TALK, content);
        messageRepository.save(message);

        return MessageDTO.of(message);
    }

    // 채팅방 퇴장
    @Override
    public MessageDTO unsubscribe(String roomId, String userNickname) {
        log.trace("MessageServiceImpl.exit : " + roomId + ", " + userNickname);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser find = checkPermission(roomId, userNickname);

        // 채팅방 구독 취소
        chatRoomUserRepository.delete(find);

        String content = find.getUser().getNickname() + "님이 나가셨습니다.";
        Message message = Message.create(find.getChatRoom(), find.getUser(), MessageType.EXIT, content);
        messageRepository.save(message);

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
