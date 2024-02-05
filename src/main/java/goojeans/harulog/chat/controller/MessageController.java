package goojeans.harulog.chat.controller;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.request.MessageRequest;
import goojeans.harulog.chat.service.MessageService;
import goojeans.harulog.user.util.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 웹소켓 채팅 메세지 관련 API
 * (websocket) 입장, 메세지 전송, 퇴장
 * (http) 메세지 조회
 */
@Slf4j
@Transactional
@RestController
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate template;
    private final MessageService messageService;
    private final SecurityUtils securityUtils;

    // 채팅방 구독 및 입장 메세지 전송
    @SubscribeMapping("/chat/{roomId}/subscribe") // "/app/chat/{roomId}/subscribe"
    public void subscribe(
            @DestinationVariable("roomId") String roomId,
            Authentication authentication
    ) {
        String nickname = authentication.getName();
        log.trace("MessageController.subscribe : " + roomId + ", " + nickname);

        // 구독되어 있지 않다면 구독
        if(messageService.existSubscribe(roomId, nickname)){
            messageService.subscribe(roomId, nickname);
        }

        // 입장한 적이 없으면, 입장 메세지 전송
        if(!messageService.isEntered(roomId, nickname)){
            MessageDTO enterMessage = messageService.enter(roomId, nickname);
            template.convertAndSend("/topic/chat/" + roomId, enterMessage);
        }
    }

    // 채팅방 메세지 조회
    @GetMapping("/api/chats/{roomId}/ms")
    public ResponseEntity<?> enter(
            @PathVariable("roomId") String roomId
    ) {
        log.trace("MessageController.enter : " + roomId);
        String nickname = securityUtils.getCurrentUserInfo().getNickname();

        return ResponseEntity.ok(messageService.getMessages(roomId, nickname));
    }

    // 메세지 전송
    @MessageMapping("/chat/{roomId}/send") // "/app/chat/{roomId}/send"
    @SendTo("/topic/chat/{roomId}")
    public MessageDTO send(
            @DestinationVariable("roomId") String roomId,
            MessageRequest messageRequest
    ) {
        log.trace("MessageController.send : " + roomId + ", " + messageRequest.getContent());
        return messageService.send(roomId, messageRequest.getSender(), messageRequest.getContent());
    }

    // 채팅방 퇴장
    @MessageMapping("/chat/{roomId}/exit")
    public void exit(
            @DestinationVariable("roomId") String roomId,
            Authentication authentication
    ) {
        String nickname = authentication.getName();

        // 사용자가 채팅방에 구독되어있는지 확인
        boolean isSubscribed = messageService.existSubscribe(roomId, nickname);

        // 구독되어있다면 구독 취소 후, 퇴장 메세지 전송
        if (isSubscribed) {
            MessageDTO exitMessage = messageService.exit(roomId, nickname);
            template.convertAndSend("/topic/chat/" + roomId, exitMessage);
        }
    }
}
