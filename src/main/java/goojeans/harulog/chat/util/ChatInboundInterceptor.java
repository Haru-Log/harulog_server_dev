package goojeans.harulog.chat.util;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatInboundInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // STOMP의 CONNECT 요청을 처리하고, 해당 요청의 유효성을 검증
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            if (token == null) {
                throw new BusinessException(ResponseCode.USER_NOT_FOUND);
            }

            // 토큰에서 Authentication 객체를 추출하여 accessor에 저장
            Claims claims = jwtTokenProvider.getClaim(token).stream()
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_UNAUTHORIZED));

            Authentication authentication = jwtTokenProvider.getAuthentication(claims);
            accessor.setUser(authentication);
        }
        return message;
    }


}
