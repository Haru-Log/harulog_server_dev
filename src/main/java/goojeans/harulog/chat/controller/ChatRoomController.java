package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    // 채팅방 생성
    @PostMapping("/chats")
    public ResponseEntity<?> create(){
        return ResponseEntity.ok(chatRoomService.createDM());
    }

    // 채팅방 삭제
    @DeleteMapping("/chats/{roomId}")
    public ResponseEntity<?> delete(@PathVariable("roomId") String roomId){
        return ResponseEntity.ok(chatRoomService.delete(roomId));
    }
}
