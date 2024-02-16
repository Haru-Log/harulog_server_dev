package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.service.ChatImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class ChatImageController {

    private final ChatImageService chatImageService;

    @PostMapping("/{roomId}/image")
    ResponseEntity<?> uploadImageInChat(@PathVariable("roomId") String roomId, @RequestPart("image") MultipartFile image) throws IOException {

        return ResponseEntity.ok(chatImageService.uploadChatImage(image, roomId));
    }
}
