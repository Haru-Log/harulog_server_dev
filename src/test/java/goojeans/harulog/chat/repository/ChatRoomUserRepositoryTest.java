package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUserId;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.domain.ActiveStatus;
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
class ChatRoomUserRepositoryTest {

    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;

    @Autowired
    private EntityManager em;

    private String test = "test";
    private Users user1, user2;
    private ChatRoom chatRoom1, chatRoom2;

    /**
     * 채팅방, 유저 정보 만들기.
     */
    @BeforeEach
    void setUp(){
        chatRoom1 = new ChatRoom();
        chatRoom2 = new ChatRoom();
        user1 = Users.builder()
                .userName(test)
                .email("test1")
                .nickname(test)
                .password(test)
                .socialType(SocialType.HARU)
                .build();

        user2 = Users.builder()
                .userName(test)
                .email("test2")
                .nickname(test)
                .password(test)
                .socialType(SocialType.HARU)
                .build();

        em.persist(chatRoom1);
        em.persist(chatRoom2);
        em.persist(user1);
        em.persist(user2);
        em.flush();
    }

    @Test
    @DisplayName("유저가 참여하고 있는 채팅방 목록 조회")
    void findByUser() {
        // given
        ChatRoomUser chatRoomUser1 = ChatRoomUser.create(chatRoom1, user1);
        ChatRoomUser chatRoomUser2 = ChatRoomUser.create(chatRoom2, user1);

        chatRoomUserRepository.save(chatRoomUser1);
        chatRoomUserRepository.save(chatRoomUser2);


        // when
        List<ChatRoomUser> findChatRoomByUser = chatRoomUserRepository.findByUser(user1);

        // then
        Assertions.assertThat(findChatRoomByUser.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("채팅방에 참여하고 있는 유저 목록 조회")
    void findByChatRoom() {

        // given
        ChatRoomUser chatRoomUser1 = ChatRoomUser.create(chatRoom1, user1);
        ChatRoomUser chatRoomUser2 = ChatRoomUser.create(chatRoom1, user2);

        chatRoomUserRepository.save(chatRoomUser1);
        chatRoomUserRepository.save(chatRoomUser2);

        // when
        List<ChatRoomUser> findUsersByChatRoom = chatRoomUserRepository.findByChatRoom(chatRoom1);

        //then
        Assertions.assertThat(findUsersByChatRoom.size()).isEqualTo(2);

    }

    @Test
    @DisplayName("채팅방-유저 삭제 : 유저가 채팅방에서 퇴장")
    void delete(){

        // given
        ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .id(new ChatRoomUserId(chatRoom1.getId(), user1.getId()))
                .chatRoom(chatRoom1)
                .user(user1)
                .build();

        ChatRoomUser saved = chatRoomUserRepository.save(chatRoomUser);

        // when
        chatRoomUserRepository.delete(saved);

        // then

        /**
         *  1. chatroom_user가 soft delete되면 검색 안되는지 확인.
         */
        ChatRoomUser find = em.find(ChatRoomUser.class, saved.getId());
        Assertions.assertThat(find).isNull();

        /**
         *  2. chatroom_user 가 soft delete 되었는지 확인
         */
        ChatRoomUser deleted = (ChatRoomUser) em.createNativeQuery("SELECT * FROM chatroom_user WHERE chatroom_id = :chatroom_id AND user_id =:user_id", ChatRoomUser.class)
                .setParameter("chatroom_id", saved.getChatRoom().getId())
                .setParameter("user_id",saved.getUser().getId())
                .getSingleResult();
        Assertions.assertThat(deleted).isNotNull();
        Assertions.assertThat(deleted.getActiveStatus()).isEqualTo(ActiveStatus.DELETED);

    }

    @Test
    @DisplayName("유저가 삭제되면 채팅방-유저도 같이 삭제")
    void deleteChatRoomUserByUser(){

        // todo: Users pr되면 같이 수정.

    }
}