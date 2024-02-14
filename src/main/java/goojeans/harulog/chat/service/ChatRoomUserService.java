package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;

import java.util.List;

public interface ChatRoomUserService {

    // 채팅방에 유저 추가
    Response<Void> addUser(String roomId, String userNickname);

    Response<Void> addUser(String roomId, List<String> usersNickname);

    Response<Void> addUser(ChatRoom room, Users user);
    Response<Void> addUser(ChatRoom room, List<Users> users);

    // 채팅방에 유저 삭제
    Response<Void> deleteUser(String roomId, String userNickname);

    // 채팅방에 참여하고 있는 유저 조회
    Response<List<ChatUserDTO>> getUsers(String roomId);

    // 유저가 참여하고 있는 채팅방 조회
    Response<List<ChatRoomDTO>> getChatRooms(String userNickname);

    // 입장 메세지 생성, 저장 및 전송
    void sendEnterMessage(ChatRoom room, Users user);

    // 퇴장 메세지 생성, 저장 및 전송
    void sendExitMessage(ChatRoom room, Users user);
}
