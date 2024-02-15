package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.domain.dto.ChatUserDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.entity.Users;

import java.util.List;

public interface ChatRoomUserService {

    // 기본형
    // 채팅방id, 유저 닉네임으로 채팅방 유저 조회
    ChatRoomUser findChatRoomUser(String roomId, Long userId);
    ChatRoomUser findChatRoomUser(ChatRoom room, Users user);

    // 채팅방에 유저 추가
    void addUser(ChatRoom room, Users user);

    // 채팅방에 유저 여러명 추가
    void addUsers(ChatRoom room, List<Users> users);

    // 채팅방에 유저 삭제
    void deleteUser(ChatRoomUser cru);

    // 응답형
    // 채팅방에 유저 추가
    Response<Void> addUser(String roomId, String userNickname);
    Response<Void> addUsers(String roomId, List<String> usersNickname);

    // Controller에서 deleteUser 호출
    Response<Void> deleteUserRequest(String roomId, String userNickname);

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
