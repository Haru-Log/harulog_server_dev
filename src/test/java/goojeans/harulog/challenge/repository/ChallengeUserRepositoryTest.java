package goojeans.harulog.challenge.repository;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChallengeUserRepositoryTest {

    @Autowired
    private ChallengeUserRepository challengeUserRepository;

    @Autowired
    private EntityManager em;

    @Test
    @DisplayName("유저,챌린지로 챌린지 유저 조회하기")
    void findChallengeUserByUser() {

        Category category = em.find(Category.class, 1L);

        Users user = Users.builder()
                .email("test@test.com")
                .password("test")
                .userName("test")
                .nickname("tester")
                .socialType(SocialType.HARU)
                .build();

        em.persist(user);

        Challenge challenge = Challenge.builder()
                .challengeTitle("test challenge")
                .challengeContent("test")
                .challengeGoal(3)
                .submission("test submit")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .category(category)
                .chatroom(new ChatRoom())
                .build();

        em.persist(challenge);

        ChallengeUser challengeUser = ChallengeUser.create(user, challenge);
        challenge.addChallengeUser(challengeUser);

        ChallengeUser findChallengeUser = challengeUserRepository.findChallengeUserByUserAndChallenge(user.getId(), challenge.getChallengeId()).orElse(null);

        Assertions.assertThat(findChallengeUser).isNotNull();
        Assertions.assertThat(findChallengeUser.getChallenge().getChallengeId()).isEqualTo(challenge.getChallengeId());
    }
}