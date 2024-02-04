package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.ChatRoomDTO;
import goojeans.harulog.chat.service.ChatRoomService;
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
public class ChatRoomController {

    private final SecurityUtils securityUtils;
    private final ChatRoomService chatRoomService;
    private final ChatRoomUserService chatRoomUserService;

    // 채팅방 생성 및 채팅방 생성한 유저 추가
    @PostMapping("/chats")
    public ResponseEntity<?> create(){
        log.trace("ChatRoomController.create");

        Response<ChatRoomDTO> response = chatRoomService.createDM(); // DM 채팅방 생성
        String userNickname = securityUtils.getCurrentUserInfo().getNickname();

        chatRoomUserService.addUser(response.getData().getRoomId(), userNickname); // 채팅방에 "나" 추가
        return ResponseEntity.ok(response);
    }

    // 채팅방 삭제
    @DeleteMapping("/chats/{roomId}")
    public ResponseEntity<?> delete(@PathVariable("roomId") String roomId){
        log.trace("ChatRoomController.delete : " + roomId);
        return ResponseEntity.ok(chatRoomService.delete(roomId));
    }
}
