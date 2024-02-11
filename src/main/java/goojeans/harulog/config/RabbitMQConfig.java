package goojeans.harulog.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.springframework.amqp.core.Binding.DestinationType.QUEUE;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    private final AmqpAdmin amqpAdmin;

    /**
     * 채팅방 exchange 생성
     * @param roomId
     */
    public void createFanoutExchange(String roomId){
        log.info("CREATE FANOUT_EXCHANGE : " + roomId);
        amqpAdmin.declareExchange(new FanoutExchange("chatroom."+roomId, true, false));
    }

    /**
     * 채팅방 - 유저 queue 생성
     * @param userNickname
     */
    public void createQueue(String userNickname){
        log.info("CREATE QUEUE : " + userNickname);
        amqpAdmin.declareQueue(new Queue("user."+userNickname, true, false, false));
    }

    /**
     * 채팅방 - 유저 binding
     * @param roomId
     * @param userNickname
     */
    public void binding(String roomId, String userNickname){
        String queueName = "user."+userNickname;
        String exchangeName = "chatroom."+roomId;

        log.info("BINDING : " + queueName + " TO " + exchangeName);

        amqpAdmin.declareBinding(new Binding(queueName, QUEUE, exchangeName, "", null));
    }

    // exchange 삭제
    public void deleteExchange(String roomId){
        log.info("DELETE EXCHANGE : " + roomId);
        amqpAdmin.deleteExchange("chatroom."+roomId);
    }

    // queue 삭제
    public void deleteQueue(String userNickname){
        log.info("DELETE QUEUE : " + userNickname);
        amqpAdmin.deleteQueue("user."+userNickname);
    }

    /**
     * 채팅방 - 유저 unbinding -> 더 이상 메세지를 받지 않음
     * but 메세지 저장은 되고 있음.
     * @param roomId
     * @param userNickname
     */
    public void unBinding(String roomId, String userNickname){
        String queueName = "user."+userNickname;
        String exchangeName = "chatroom."+roomId;

        log.info("UNBINDING : " + queueName + " TO " + exchangeName);

        amqpAdmin.removeBinding(new Binding(queueName, QUEUE, exchangeName, "", null));
    }

    /**
     * 메세지 변환기 설정
     * SEND 메소드에서 rabbitTemplate.convertAndSend(...)를 호출할 때
     * MessageDTO 객체가 JSON으로 자동 변환되어 전송됨.
     */
    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(converter());
        return rabbitTemplate;
    }
    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }
}
