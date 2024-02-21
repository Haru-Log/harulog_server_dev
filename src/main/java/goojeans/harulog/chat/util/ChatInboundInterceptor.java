package goojeans.harulog.chat.util;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.util.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatInboundInterceptor implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        log.info("요청 : {}", accessor.getCommand() + " : " + accessor.getDestination());

        /**
         * CONNECT
         * 1. jwt 토큰 검증
         */
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String token = accessor.getFirstNativeHeader("Authorization");
            log.info("token : {}", token);

            // jwt 토큰 검증
            Authentication authentication = getAuthentication(token);
            accessor.setUser(authentication);
        }

        /**
         * SEND
         * 채팅방에 메세지 보내기 전,
         * todo 1. 유저가 채팅방에 참여한 유저인지 확인 -> 권한 없으면 에러
         */

        return message;
    }

    private Authentication getAuthentication(String token){
        try{
            token = token.substring("Bearer ".length()); // "Bearer " 제거

            Claims claims = jwtTokenProvider.getClaim(token).stream()
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_UNAUTHORIZED));

            return jwtTokenProvider.getAuthentication(claims);
        }catch (Exception e){
            throw new BusinessException(ResponseCode.USER_UNAUTHORIZED);
        }
    }
}
