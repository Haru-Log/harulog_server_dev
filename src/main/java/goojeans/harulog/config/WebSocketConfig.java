package goojeans.harulog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 해당 prefix가 붙은 요청을 가로채서 처리한다.
        // @MessageMapping("/app/**")
        registry.setApplicationDestinationPrefixes("/app"); // publisher

        // 해당 prefix를 붙인 요청을 메시지 브로커로 전달한다.
        // todo: rabbitmq로 바꾸기
        registry.enableSimpleBroker("/topic", "/queue"); // subscriber
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // todo: Postman 테스트용 - 배포 시 반드시 주석처리.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*"); // 클라이언트에서 웹소켓서버에 요청 시 모든 요청 허용.(CORS)

        // sockJS 사용
//        registry.addEndpoint("/ws")
//                .setAllowedOrigins("*")
//                .withSockJS();// SockJS를 사용하기 위한 설정
    }
}
