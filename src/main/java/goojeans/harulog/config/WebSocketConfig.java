package goojeans.harulog.config;

import goojeans.harulog.chat.util.ChatInboundInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final ChatInboundInterceptor chatInboundInterceptor;

    @Value("${spring.rabbitmq.host}") String host;
    @Value("${spring.rabbitmq.username}") String username;
    @Value("${spring.rabbitmq.password}") String password;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지 브로커가 해당 prefix가 붙은 요청을 가로채서 처리한다.
        // rabbitmq
        registry.setApplicationDestinationPrefixes("/app") // publisher
                .enableStompBrokerRelay("/queue", "/topic", "/exchange", "/amq/topic","/amq/queue")
                .setRelayHost(host)
                .setRelayPort(61613)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);
    }

    // 웹소켓 핸드셰이크 커넥션 생성할 경로
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // todo: Postman 테스트용 - 배포 시 반드시 주석처리.
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*"); // 클라이언트에서 웹소켓서버에 요청 시 모든 요청 허용.(CORS)

        // sockJS 사용
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*")
                .withSockJS(); // SockJS를 사용하기 위한 설정

    }

    // STOMP 메세지 인터셉터
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(chatInboundInterceptor);
    }
}
