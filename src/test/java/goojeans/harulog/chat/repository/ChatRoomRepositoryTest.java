package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ChatRoomRepositoryTest {

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Test
    @DisplayName("채팅방 생성")
    void saveChatRoom() {
            // given
            ChatRoom chatRoom = new ChatRoom();

            // when
            ChatRoom saved = chatRoomRepository.save(chatRoom);

            // then
            Assertions.assertThat(saved.getId()).isEqualTo(1L);
    }

}