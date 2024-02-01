package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface ChatRoomUserService {

    // 채팅방에 유저 추가
    public Response<Void> addUser(String roomId, String userNickname);

    public Response<Void> addUser(String roomId, List<String> usersNickname);

    // 채팅방에 유저 삭제
    public Response<Void> deleteUser(String roomId, String userNickname);

    // 채팅방에 참여하고 있는 유저 조회
    public Response<List<ChatUserDTO>> getUsers(String roomId);

    // 유저가 참여하고 있는 채팅방 조회
    public Response<List<ChatRoomDTO>> getChatRooms(String userNickname);
}
