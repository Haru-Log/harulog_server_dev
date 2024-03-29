package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.chat.domain.dto.MessageDTO;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static goojeans.harulog.chat.util.ChatRoomType.*;
import static goojeans.harulog.chat.util.MessageType.ENTER;
import static goojeans.harulog.chat.util.MessageType.EXIT;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatRoomUserServiceImpl implements ChatRoomUserService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    // 입장, 퇴장 메세지 저장
    private final MessageRepository messageRepository;

    // 입장, 퇴장 메세지 전송
    private final RabbitTemplate rabbitTemplate;

    // 채팅방 - 유저 binding, unbinding
    private final RabbitMQConfig rabbitMQConfig;


    // 기본형
    // 채팅방id, 유저 닉네임으로 채팅방-유저 조회
    @Override
    public ChatRoomUser findChatRoomUser(String roomId, Long userId) {
        return chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
    }

    // 채팅방, 유저로 채팅방-유저 조회
    @Override
    public ChatRoomUser findChatRoomUser(ChatRoom room, Users user) {
        return chatRoomUserRepository.findByChatRoomAndUser(room, user)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
    }

    /**
     * 채팅방에 유저 추가
     * 1. 유저 추가
     * 2. 입장 메세지 전송
     * 3. DM방 일때 유저가 추가되어, 2명 초과가 되면 Group 채팅방으로 변경
     */
    @Override
    public void addUser(ChatRoom room, Users user){
        ChatRoomUser cru = ChatRoomUser.create(room, user);
        chatRoomUserRepository.save(cru);

        // 입장 메세지 전송
        sendEnterMessage(room, user);

        // DM방 일때 유저가 추가되어 2명 초과이면 Group 채팅방으로 변경
        if (room.getType() == DM && room.getUsers().size() + 1 > 2) {
            room.setType(GROUP);
            chatRoomRepository.save(room);
        }
    }

    /**
     * 채팅방에 유저 여러명 추가
     * 1. 유저 추가
     * 2. 입장 메세지 전송
     * 3. DM방 일때 유저가 추가 ->  2명 초과이면 Group 채팅방으로 변경
     */
    @Override
    public void addUsers(ChatRoom room, List<Users> users) {
        users.forEach(user -> chatRoomUserRepository.save(ChatRoomUser.create(room, user)));

        // 입장 메세지 전송
        users.forEach(user -> sendEnterMessage(room, user));

        // DM방 일때 유저가 추가되어 2명 초과이면 Group 채팅방으로 변경
        if (room.getType() == DM && room.getUsers().size() + users.size() > 2) {
            room.setType(GROUP);
            chatRoomRepository.save(room);
        }
    }

    /**
     * 채팅방에 유저 삭제
     * - 그룹 채팅방이면서 유저가 1명 빠져서, 2명 이하로 되면 DM 채팅방으로 변경
     * - 채팅방에 유저가 없으면 채팅방 삭제
     */
    @Override
    public void deleteUser(ChatRoomUser cru) {
        ChatRoom room = cru.getChatRoom();
        Users user = cru.getUser();

        // 채팅방에 참여하고 있는 유저 수
        int remain = chatRoomUserRepository.findUserByChatroomId(room.getId()).size();

        // 채팅방에 유저가 없으면 채팅방 삭제
        if (remain - 1 <= 0) {
            log.info("채팅방에 사람이 없습니다.");
            // 채팅방 exchange 삭제
            rabbitMQConfig.deleteExchange(room.getId());
            // 채팅방 삭제
            chatRoomRepository.delete(room);
        }
        // 그룹 채팅방이면서 유저가 1명 빠져서, 2명 이하로 되면 DM 채팅방으로 변경
        if (room.getType() == GROUP && remain - 1 <= 2) {
            room.setType(DM);
            chatRoomRepository.save(room);
        }

        // 퇴장 메세지 전송
        sendExitMessage(room, user);

        // 채팅방에서 유저 삭제
        chatRoomUserRepository.delete(cru);
    }


    // 채팅방에 유저 1명 추가
    @Override
    public Response<Void> addUser(String roomId, String userNickname) {

        ChatRoom chatRoom = findChatRoom(roomId);
        Users user = findUser(userNickname);

        // 채팅방에 유저 추가
        addUser(chatRoom, user);

        return Response.ok();
    }

    // 채팅방에 유저 여러명 추가
    @Override
    public Response<Void> addUsers(String roomId, List<String> usersNickname) {

        ChatRoom chatRoom = findChatRoom(roomId);
        List<Users> users = usersNickname.stream()
                .map(this::findUser)
                .toList();

        // 채팅방에 유저 추가
        addUsers(chatRoom, users);

        return Response.ok();
    }

    @Override
    public Response<Void> deleteUserRequest(String roomId, String userNickname) {
        ChatRoom chatRoom = findChatRoom(roomId);

        // 채팅방이 Challenge이면, Challenge 채팅방에서는 유저가 나가지 못하도록 함
        if (chatRoom.getType() == CHALLENGE) {
            throw new BusinessException(ResponseCode.CHALLENGE_CHATROOM_USER_CANNOT_LEAVE);
        }
        return deleteUser(roomId, userNickname);
    }



    @Override
    public Response<Void> deleteUser(String roomId, String userNickname) {

        // 유저 닉네임으로 유저 조회
        Users user = findUser(userNickname);

        // 채팅방-유저 조회
        ChatRoomUser cru = findChatRoomUser(roomId, user.getId());

        // 채팅방에 유저 삭제
        deleteUser(cru);

        return Response.ok();
    }

    /**
     * 채팅방에 참여하고 있는 유저 조회
     * @return [유저 닉네임, 이미지url] 리스트
     */
    @Override
    public Response<List<ChatUserDTO>> getUsers(String roomId) {
        List<Users> userList = chatRoomUserRepository.findUserByChatroomId(roomId);

        return Response.ok(userList.stream()
                .map(ChatUserDTO::of)
                .toList());
    }

    /**
     * 유저가 참여하고 있는 채팅방 조회
     */
    @Override
    public Response<List<ChatRoomDTO>> getChatRooms(Long userId) {

        // 유저 id로 채팅방-유저 조회 (최신순)
        List<ChatRoomUser> cruList = chatRoomUserRepository.findByUserId(userId);
        List<ChatRoomDTO> chatRoomDTOList = cruList.stream()
                .map(ChatRoomDTO::of)
                .toList();
        return Response.ok(chatRoomDTOList);
    }

    /**
     * 입장 메세지 생성, 저장 및 전송
     */
    @Override
    public void sendEnterMessage(ChatRoom room, Users user) {

        // 입장 메세지 생성 및 저장
        String content = user.getNickname() + "님이 들어오셨습니다.";
        Message message = Message.create(room, user, ENTER, content);
        messageRepository.save(message);

        // 채팅방 Exchange에 입장 메세지 전송
        rabbitTemplate.convertAndSend("chatroom." + room.getId(), "", MessageDTO.of(message));
    }

    /**
     * 퇴장 메세지 생성, 저장 및 전송
     */
    @Override
    public void sendExitMessage(ChatRoom room, Users user) {

        // 퇴장 메세지 생성 및 저장
        String content = user.getNickname() + "님이 나가셨습니다.";
        Message message = Message.create(room, user, EXIT, content);
        messageRepository.save(message);

        // 채팅방 Exchange에 퇴장 메세지 전송
        rabbitTemplate.convertAndSend("chatroom." + room.getId(), "", MessageDTO.of(message));
    }

    /**
     * 유저, 채팅방, 채팅방-유저 권한 조회
     */
    private Users findUser(String userNickname) {
        return userRepository.findUsersByNickname(userNickname)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }
}
