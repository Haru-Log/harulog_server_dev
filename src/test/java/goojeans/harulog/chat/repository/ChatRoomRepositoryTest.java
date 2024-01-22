package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.util.ActiveStatus;
import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("채팅방 생성")
    void create() {
            // given
            ChatRoom chatRoom = new ChatRoom();

            // when
            ChatRoom saved = chatRoomRepository.save(chatRoom);

            // then
            Assertions.assertThat(saved.getId()).isEqualTo(chatRoom.getId());
    }

    @Test
    @DisplayName("채팅방 삭제")
    void delete(){

        // given
        ChatRoom chatRoom = new ChatRoom();
        ChatRoom saved = chatRoomRepository.save(chatRoom);

        // when
        chatRoomRepository.delete(saved);
        em.flush(); //변경사항을 db에 즉시 적용.

        // then
        ChatRoom deleted = (ChatRoom) em.createNativeQuery("SELECT * FROM chatroom WHERE chatroom_id = :id", ChatRoom.class)
                .setParameter("id", saved.getId())
                .getSingleResult();
        Assertions.assertThat(deleted).isNotNull();
        Assertions.assertThat(deleted.getActiveStatus()).isEqualTo(ActiveStatus.DELETED);
    }
}