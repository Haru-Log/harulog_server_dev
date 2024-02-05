package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.request.AddUserRequest;
import goojeans.harulog.chat.service.ChatRoomUserService;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomUserController {

    private final ChatRoomUserService chatRoomUserService;
    private final SecurityUtils securityUtils;

    // 유저 추가
    @PostMapping("/chats/{roomId}/users")
    public ResponseEntity<?> addUser(
            @PathVariable("roomId") String roomId,
            @RequestBody AddUserRequest addUserRequest
    ){
        chatRoomUserService.addUser(roomId, addUserRequest.getUserNicknames());
        return ResponseEntity.ok(Response.ok());
    }

    // 채팅방 참여 유저 조회
    @GetMapping("/chats/{roomId}/users")
    public ResponseEntity<?> getUsers(@PathVariable("roomId") String roomId){
        return ResponseEntity.ok(chatRoomUserService.getUsers(roomId));
    }

    // 유저가 참여하고 있는 채팅방 조회
    // 채팅방의 마지막 메세지를 기준으로 최신순으로 정렬해서 반환.
    @GetMapping("/user/chats")
    public ResponseEntity<?> getChatRooms(){
        String userNickname = securityUtils.getCurrentUserInfo().getNickname();
        return ResponseEntity.ok(chatRoomUserService.getChatRooms(userNickname));
    }
}
