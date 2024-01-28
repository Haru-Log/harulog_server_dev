package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.util.MessageType;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager em;

    private String test = "test";
    private Users user1;
    private ChatRoom chatRoom1;

    @BeforeEach
    void setUp() {
        user1 = Users.builder()
                .userName(test)
                .email("test1")
                .nickname(test)
                .password(test)
                .socialType(SocialType.HARU)
                .build();

        chatRoom1 = ChatRoom.create(test);

        em.persist(user1);
        em.persist(chatRoom1);
        em.flush();
    }

    @Test
    @DisplayName("메세지 생성")
    void create() {

        // given
        Message message = Message.builder()
                .chatRoom(chatRoom1)
                .sender(user1)
                .type(MessageType.ENTER)
                .content(test)
                .build();

        // when
        Message savedMessage = messageRepository.save(message);

        // then
        Assertions.assertThat(savedMessage).isNotNull();
        Assertions.assertThat(savedMessage.getId()).isEqualTo(message.getId());

    }

    @Test
    @DisplayName("채팅방 전체 메세지 조회")
    void findByChatroomId() {

        // given
        Message message1 = Message.builder()
                .chatRoom(chatRoom1)
                .sender(user1)
                .type(MessageType.ENTER)
                .content(test)
                .build();

        Message message2 = Message.builder()
                .chatRoom(chatRoom1)
                .sender(user1)
                .type(MessageType.TALK)
                .content(test)
                .build();

        messageRepository.save(message1);
        messageRepository.save(message2);

        // when
        List<Message> messages = messageRepository.findByChatRoomId(chatRoom1.getId());

        // then
        Assertions.assertThat(messages.size()).isEqualTo(2);

    }
}