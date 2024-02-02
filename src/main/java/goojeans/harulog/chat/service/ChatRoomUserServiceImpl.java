package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.repository.ChatRoomRepository;
import goojeans.harulog.chat.repository.ChatRoomUserRepository;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static goojeans.harulog.chat.util.ChatRoomType.DM;
import static goojeans.harulog.chat.util.ChatRoomType.GROUP;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomUserServiceImpl implements ChatRoomUserService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    @Override
    public Response<Void> addUser(String roomId, String userNickname) {

        ChatRoom chatRoom = findChatRoom(roomId);
        Users user = findUser(userNickname);

        chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user));

        return Response.ok();
    }

    @Override
    public Response<Void> addUser(String roomId, List<String> usersNickname) {

        ChatRoom chatRoom = findChatRoom(roomId);
        List<Users> users = usersNickname.stream()
                .map(this::findUser)
                .toList();

        users.forEach(user -> chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user)));


        // DM 채팅방이면서 유저가 2명 초과이면 그룹 채팅방으로 변경
        if (chatRoom.getType() == DM && !users.isEmpty()) {
            chatRoom.setType(GROUP);
            chatRoomRepository.save(chatRoom);
        }

        return Response.ok();
    }

    @Override
    public Response<Void> deleteUser(String roomId, String userNickname) {

        ChatRoomUser chatRoomUser = findChatRoomUser(roomId, userNickname);
        chatRoomUserRepository.delete(chatRoomUser);

        return Response.ok();
    }

    @Override
    public Response<List<ChatUserDTO>> getUsers(String roomId) {
        List<Users> userList = chatRoomUserRepository.findUserByChatroomId(roomId);

        return Response.ok(userList.stream()
                .map(ChatUserDTO::of)
                .toList());
    }

    @Override
    public Response<List<ChatRoomDTO>> getChatRooms(String userNickname) {

        List<ChatRoom> chatRoomList = chatRoomUserRepository.findChatRoomsByUserNickName(userNickname);
        List<ChatRoomDTO> chatRoomDTOList = chatRoomList.stream()
                .map(ChatRoomDTO::of)
                .toList();

        return Response.ok(chatRoomDTOList);
    }

    private Users findUser(String userNickname) {
        return userRepository.findUsersByNickname(userNickname)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(String roomId) {
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }

    private ChatRoomUser findChatRoomUser(String roomId, String userNickname) {
        Users user = findUser(userNickname);
        findChatRoom(roomId);
        return chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
    }
}
