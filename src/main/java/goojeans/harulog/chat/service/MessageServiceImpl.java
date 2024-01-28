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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService{

    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;
    private final MessageRepository messageRepository;

    // 채팅방 전체 메세지 조회
    public Response<List<MessageDTO>> getMessages(String roomId){
        log.trace("MessageServiceImpl.getMessages : " + roomId);

        // 채팅방 있는지 확인
        findChatRoom(roomId);

        // 채팅방 메세지 조회
        List<Message> messages = messageRepository.findByChatRoomId(roomId);
        List<MessageDTO> result = messages.stream()
                .map(message -> MessageDTO.of(message))
                .toList();

        return Response.ok(result);
    }

    // 채팅방 입장
    @Override
    public Response<MessageDTO> enter(String roomId, Long userId) {
        log.trace("MessageServiceImpl.enter : " + roomId + ", " + userId);

        // 유저 정보 찾기
        Users user = findUser(userId);
        // 채팅방 찾기
        ChatRoom chatRoom = findChatRoom(roomId);

        Optional<ChatRoomUser> optional = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId);

        if(optional.isPresent()){ // 유저가 이미 채팅방에 참여해 있음.
            return Response.ok();

        } else { // 유저가 채팅방에 참여하지 않았으므로, 채팅방에 추가
            chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user));

            // 입장 메세지 생성 및 저장
            String content = user.getNickname() + "님이 입장하셨습니다.";
            Message message = Message.create(chatRoom, user, MessageType.ENTER, content);
            messageRepository.save(message);

            return Response.ok(MessageDTO.of(message));
        }
    }

    // 메세지 전송
    @Override
    public Response<MessageDTO> send(String roomId, Long userId, String content) {
        log.trace("MessageServiceImpl.send : " + roomId + ", " + userId + ", " + content);

        // 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
        ChatRoomUser find = checkPermission(roomId, userId);

        Message message = Message.create(find.getChatRoom(), find.getUser(), MessageType.TALK, content);
        messageRepository.save(message);

        return Response.ok(MessageDTO.of(message));
    }

    // 채팅방 퇴장
    @Override
    public Response<MessageDTO> exit(String roomId, Long userId) {
        log.trace("MessageServiceImpl.exit : " + roomId + ", " + userId);

        // 유저 정보 찾기
        Users user = findUser(userId);
        // 채팅방 찾기
        ChatRoom chatRoom = findChatRoom(roomId);
        Optional<ChatRoomUser> find = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId);

        if(find.isEmpty()){ // 이미 퇴장한 유저
            return Response.ok();

        } else {
            chatRoomUserRepository.delete(find.get());

            String content = user.getNickname() + "님이 퇴장하셨습니다.";
            Message message = Message.create(chatRoom, user, MessageType.EXIT, content);
            messageRepository.save(message);

            return Response.ok(MessageDTO.of(message));
        }
    }

    private Users findUser(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(String roomId){
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }

    private ChatRoomUser checkPermission(String roomId, Long userId){
        findUser(userId);
        findChatRoom(roomId);
        return chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
    }
}
