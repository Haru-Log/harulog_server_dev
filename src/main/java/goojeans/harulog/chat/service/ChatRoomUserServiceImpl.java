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
    public Response<Void> create(Long roomId, Long userId) {

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NOT_FOUND));
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        chatRoomUserRepository.save(ChatRoomUser.create(chatRoom, user));

        return Response.ok();
    }

    @Override
    public Response<Void> delete(Long roomId, Long userId) {

        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByChatRoomIdAndUserId(roomId, userId)
                .orElseThrow(() -> new BusinessException(ResponseCode.CHAT_NO_PERMISSION));
        chatRoomUserRepository.delete(chatRoomUser);

        return Response.ok();

    }

    @Override
    public Response<List<ChatUserDTO>> findByChatRoom(Long roomId) {

        List<Users> userList = chatRoomUserRepository.findUserByChatroomId(roomId);
        List<ChatUserDTO> userDTOList = userList.stream()
                .map(user -> ChatUserDTO.of(user))
                .toList();

        return Response.ok(userDTOList);
    }

    @Override
    public Response<List<ChatRoomDTO>> findByUser(Long userId) {

        List<ChatRoom> chatRoomList = chatRoomUserRepository.findChatRoomsByUserId(userId);
        List<ChatRoomDTO> chatRoomDTOList = chatRoomList.stream()
                .map(chatRoom -> ChatRoomDTO.of(chatRoom))
                .toList();

        return Response.ok(chatRoomDTOList);
    }
}
