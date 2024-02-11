package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.MessageDTO;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MessageController {

    private final MessageService messageService;
    private final RabbitTemplate rabbitTemplate;
    private final SecurityUtils securityUtils;

    /**
     * 채팅방 in (= 바인딩, 메세지 조회) (!= 채팅방 입장)
     */
    @GetMapping("/chats/{roomId}/in")
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
    @GetMapping("/chats/{roomId}/out")
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
