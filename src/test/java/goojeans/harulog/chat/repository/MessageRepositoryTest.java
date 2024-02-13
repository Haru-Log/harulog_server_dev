package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.util.MessageType;
import goojeans.harulog.config.QuerydslConfig;
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
import org.springframework.context.annotation.Import;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({QuerydslConfig.class})
class MessageRepositoryTest {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private EntityManager em;

    private String test = "test";
    private Users user1;
    private ChatRoom chatRoom1;

    private Message message1, message2, message3, message4, message5;

    @BeforeEach
    void setUp() {
        // 유저 생성
        user1 = Users.builder().userName(test).email("test1").nickname("test1").password(test).socialType(SocialType.HARU).build();
        em.persist(user1);

        // 채팅방 생성
        chatRoom1 = ChatRoom.createDM();
        em.persist(chatRoom1);

        // 메세지 생성
        message1 = Message.builder().chatRoom(chatRoom1).sender(user1).content(test).build();
        message2 = Message.builder().chatRoom(chatRoom1).sender(user1).content(test).build();
        message3 = Message.builder().chatRoom(chatRoom1).sender(user1).content(test).build();
        message4 = Message.builder().chatRoom(chatRoom1).sender(user1).content(test).build();
        message5 = Message.builder().chatRoom(chatRoom1).sender(user1).content(test).build();

        em.persist(message1);
        em.persist(message2);
        em.persist(message3);
        em.persist(message4);
        em.persist(message5);
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
    @DisplayName("마지막 메세지 id 조회")
    void findTopByChatRoomIdOrderByCreatedAtDesc() {

        // given - setUp

        // when
        Message lastMessage = messageRepository.findTopByChatRoomIdOrderByCreatedAtDesc(chatRoom1.getId());

        // then
        Assertions.assertThat(lastMessage).isNotNull();
        Assertions.assertThat(lastMessage.getId()).isEqualTo(message5.getId());
    }

    @Test
    @DisplayName("채팅방 메세지 조회 (이전 메세지)")
    void findBeforeMessagesWithPagination() {

        // given - setUp

        // when
        int limit = 3;
        Long lastMessageId = message5.getId();
        messageRepository.findBeforeMessagesWithPagination(chatRoom1.getId(), lastMessageId, limit);

        // then
        Assertions.assertThat(messageRepository.findBeforeMessagesWithPagination(chatRoom1.getId(), lastMessageId, limit)).hasSize(3);
    }

    @Test
    @DisplayName("채팅방 메세지 조회 (이후 메세지)")
    void findAfterMessagesWithPagination() {

        // given - setUp

        // when
        int limit = 3;
        Long lastMessageId = message1.getId();
        messageRepository.findAfterMessagesWithPagination(chatRoom1.getId(), lastMessageId, limit);

        // then
        Assertions.assertThat(messageRepository.findAfterMessagesWithPagination(chatRoom1.getId(), lastMessageId, limit)).hasSize(3);
    }
}