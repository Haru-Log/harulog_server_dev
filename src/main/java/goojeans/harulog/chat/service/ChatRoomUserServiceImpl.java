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

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomUserServiceImpl implements ChatRoomUserService{

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
                    .map(userNickname -> findUser(userNickname))
                    .toList();

            users.stream()
                    .forEach(user -> chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user)));

            return Response.ok();
    }

    @Override
    public Response<Void> deleteUser(String roomId, String userNickname) {

        Users user = findUser(userNickname);
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, user.getId())
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
        chatRoomUserRepository.delete(chatRoomUser);

        return Response.ok();
    }

    @Override
    public Response<List<ChatUserDTO>> getUsers(String roomId) {

        List<Users> userList = chatRoomUserRepository.findUserByChatroomId(roomId);
        List<ChatUserDTO> userDTOList = userList.stream()
                .map(user -> ChatUserDTO.of(user))
                .toList();

        return Response.ok(userDTOList);
    }

    @Override
    public Response<List<ChatRoomDTO>> getChatRooms(String userNickname) {

        Users user = findUser(userNickname);
        List<ChatRoom> chatRoomList = chatRoomUserRepository.findChatRoomsByUserNickName(userNickname);
        List<ChatRoomDTO> chatRoomDTOList = chatRoomList.stream()
                .map(chatRoom -> ChatRoomDTO.of(chatRoom))
                .toList();

        return Response.ok(chatRoomDTOList);
    }

    private Users findUser(String userNickname){
        return userRepository.findUsersByNickname(userNickname)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));
    }

    private ChatRoom findChatRoom(String roomId){
        return chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHATROOM_NOT_FOUND));
    }
}
