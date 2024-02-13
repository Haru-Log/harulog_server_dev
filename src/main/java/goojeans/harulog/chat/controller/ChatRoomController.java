package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.request.AddUserRequest;
import goojeans.harulog.chat.service.ChatRoomService;
import goojeans.harulog.chat.service.ChatRoomUserService;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 채팅방 생성, 삭제
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ChatRoomController {

    private final SecurityUtils securityUtils;
    private final ChatRoomService chatRoomService;
    private final ChatRoomUserService chatRoomUserService;

    // 채팅방 생성
    @PostMapping("/chats")
    public ResponseEntity<?> createChatroom(
            @Validated @RequestBody AddUserRequest addUserRequest
    ) {
        log.trace("ChatRoomController.create");

        String me = securityUtils.getCurrentUserInfo().getNickname();
        List<String> users = addUserRequest.getUserNicknames();
        users.add(me);

        return ResponseEntity.ok(chatRoomService.createChatRoom(users));
    }

    // 채팅방 삭제
    @DeleteMapping("/chats/{roomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable("roomId") String roomId) {
        log.trace("ChatRoomController.delete : " + roomId);
        return ResponseEntity.ok(chatRoomService.deleteChatRoom(roomId));
    }
}
