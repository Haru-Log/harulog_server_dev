package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.request.LastMessageRequest;
import goojeans.harulog.chat.domain.dto.request.MessageRequest;
import goojeans.harulog.chat.service.MessageService;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chats")
public class MessageController {

    private final MessageService messageService;
    private final RabbitTemplate rabbitTemplate;
    private final SecurityUtils securityUtils;

    /**
     * scroll up : 이전 메세지 조회
     */
    @PostMapping("/{roomId}/scroll-up")
    public ResponseEntity<?> scrollUp(
            @PathVariable("roomId") String roomId,
            @Validated @RequestBody LastMessageRequest lastMessageRequest
    ) {
        log.info("scroll up: {}", roomId);

        return ResponseEntity.ok(messageService.getMessagesBeforeResponse(roomId, lastMessageRequest.getMessageId()));
    }

    /**
     * scroll down : 이후 메세지 조회
     */
    @PostMapping("/{roomId}/scroll-down")
    public ResponseEntity<?> scrollDown(
            @PathVariable("roomId") String roomId,
            @Validated @RequestBody LastMessageRequest lastMessageRequest
    ) {
        log.info("scroll down: {}", roomId);

        return ResponseEntity.ok(messageService.getMessagesAfterResponse(roomId, lastMessageRequest.getMessageId()));
    }

    /**
     * 채팅방 in (= 바인딩, 메세지 조회) (!= 채팅방 입장)
     * 마지막 읽었던 메세지 기준으로 메세지 조회
     */
    @GetMapping("/{roomId}/in")
    public ResponseEntity<?> roomIn(
            @PathVariable("roomId") String roomId
    ) {
        log.info("room in: {}", roomId);
        String nickname = securityUtils.getCurrentUserInfo().getNickname();

        return ResponseEntity.ok(messageService.roomIn(roomId, nickname));
    }

    /**
     * 채팅방 out (= 언바인딩)
     */
    @GetMapping("/{roomId}/out")
    public ResponseEntity<?> roomOut(
            @PathVariable("roomId") String roomId
    ) {
        log.info("room out: {}", roomId);
        String nickname = securityUtils.getCurrentUserInfo().getNickname();

        messageService.roomOut(roomId, nickname);
        return ResponseEntity.ok(Response.ok("채팅방을 나갑니다."));
    }

    /**
     * 메세지 전송
     */
    @MessageMapping("/chat/{roomId}/send") // "/app/chat/{roomId}/send"
    public void send(
            @DestinationVariable("roomId") String roomId,
            @Payload MessageRequest messageRequest
    ) {
        log.trace("sendMessage: {}", messageRequest);
        MessageDTO dto = messageService.sendMessage(roomId, messageRequest.getSender(), messageRequest.getContent());

        // 메세지 전송
        rabbitTemplate.convertAndSend("chatroom."+roomId,"", dto);
    }
}
