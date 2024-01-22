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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ChallengeRepositoryTest {

    @Autowired
    private ChallengeRepository challengeRepository;

    @Autowired
    private EntityManager em;

    private Challenge challenge1, challenge2, challenge3;
    private Users user1, user2, user3;

    @BeforeEach
    void BeforeEach() {
        Category category1 = em.find(Category.class, 1L);
        Category category2 = em.find(Category.class, 2L);

        ChatRoom chatRoom1 = createChatRoom();
        ChatRoom chatRoom2 = createChatRoom();
        ChatRoom chatRoom3 = createChatRoom();

        challenge1 = createChallenge("Test Challenge1", category1, chatRoom1);
        challenge2 = createChallenge("Test Challenge2", category1, chatRoom2);
        challenge3 = createChallenge("Test Challenge3", category2, chatRoom3);

        user1 = createUser("user1@test.com");
        user2 = createUser("user2@test.com");
        user3 = createUser("user3@test.com");
    }

    @Test
    @DisplayName("챌린지 Id로 챌린지 조회하기")
    void findByChallengeId() {

        Challenge findChallenge = challengeRepository.findByChallengeId(challenge1.getChallengeId()).orElse(null);
        assert findChallenge != null;
        Assertions.assertThat(findChallenge.getChallengeTitle()).isEqualTo("Test Challenge1");
    }

    @Test
    @DisplayName("모든 챌린지 조회하기")
    void findAll() {
        List<Challenge> challenges = challengeRepository.findAll();
        Assertions.assertThat(challenges).hasSize(3);
    }

    @Test
    @DisplayName("챌린지 제목으로 챌린지 조회하기")
    void findAllByChallengeTitle() {
        List<Challenge> challenges1 = challengeRepository.findAllByChallengeTitle("Test");
        List<Challenge> challenges2 = challengeRepository.findAllByChallengeTitle("Test Challenge2");

        Assertions.assertThat(challenges1).hasSize(3);
        Assertions.assertThat(challenges2).hasSize(1);
    }

    @Test
    @DisplayName("카테고리로 챌린지 조회하기")
    void findAllByCategoryName() {
        Category category = em.find(Category.class, 1L);
        List<Challenge> challenges = challengeRepository.findAllByCategoryName(category.getCategoryName());

        Assertions.assertThat(challenges).hasSize(2);
    }

    @Test
    @DisplayName("챌린지 내 참여자 수 조회하기")
    void getChallengeUserCount() {
        //challenge1 - user1 & user2, challenge2 - user3
        ChallengeUser challengeUser1 = ChallengeUser.create(user1, challenge1);
        ChallengeUser challengeUser2 = ChallengeUser.create(user2, challenge1);
        ChallengeUser challengeUser3 = ChallengeUser.create(user3, challenge2);

        challenge1.addChallengeUser(challengeUser1);
        challengeRepository.save(challenge1);

        challenge1.addChallengeUser(challengeUser2);
        challengeRepository.save(challenge2);

        challenge2.addChallengeUser(challengeUser3);
        challengeRepository.save(challenge3);

        log.info("challengeId = {}, UserId = {}", challenge1.getChallengeId(), user1.getId());
        log.info("challengeUserPK(challengeId, UserId) = {}, {}", challengeUser1.getChallengeUserPK().getChallengeId(), challengeUser1.getChallengeUserPK().getUserId());

        Assertions.assertThat(challenge1.getChallengeUserList()).hasSize(2);
        Assertions.assertThat(challenge2.getChallengeUserList()).hasSize(1);
    }

    @Test
    @DisplayName("한 사람이 여러 챌린지 참여하기")
    void joinChallenges() {
        //user1 - challenge1, challenge2
        ChallengeUser challengeUser1 = ChallengeUser.create(user1, challenge1);
        ChallengeUser challengeUser2 = ChallengeUser.create(user1, challenge2);

        challenge1.addChallengeUser(challengeUser1);
        challengeRepository.save(challenge1);

        challenge2.addChallengeUser(challengeUser2);
        challengeRepository.save(challenge2);

        Assertions.assertThat(challenge1.getChallengeUserList()).hasSize(1);
        Assertions.assertThat(user1.getChallengeUsers()).hasSize(2);

        List<Challenge> findChallenges = challengeRepository.findAllByUserId(user1.getId());
        Assertions.assertThat(findChallenges).hasSize(2);
    }

    @Test
    @DisplayName("챌린지 삭제 시 챌린지 참여자 함께 삭제되기")
    void deleteChallenge() {
        //challenge1에 user1, user2 존재
        ChallengeUser challengeUser1 = ChallengeUser.create(user1, challenge1);
        ChallengeUser challengeUser2 = ChallengeUser.create(user2, challenge1);

        challenge1.addChallengeUser(challengeUser1);
        challengeRepository.save(challenge1);

        challenge1.addChallengeUser(challengeUser2);
        challengeRepository.save(challenge1);

        challengeRepository.delete(challenge1);

        Challenge findChallenge = em.find(Challenge.class, challenge1.getChallengeId());
        ChallengeUser findChallengeUser1 = em.find(ChallengeUser.class, challengeUser1.getChallengeUserPK());
        ChallengeUser findChallengeUser2 = em.find(ChallengeUser.class, challengeUser2.getChallengeUserPK());

        Assertions.assertThat(findChallenge).isNull();
        Assertions.assertThat(findChallengeUser1).isNull();
        Assertions.assertThat(findChallengeUser2).isNull();
    }

    Users createUser(String email) {
        Users user = Users.builder()
                .email(email)
                .password("test")
                .userName("test")
                .nickname("test")
                .socialType(SocialType.HARU)
                .build();
        em.persist(user);

        return user;
    }

    private ChatRoom createChatRoom() {
        ChatRoom chatRoom = ChatRoom.builder()
                .build();
        em.persist(chatRoom);

        return chatRoom;
    }

    Challenge createChallenge(String challengeTitle, Category category, ChatRoom chatRoom) {
        Challenge challenge = Challenge.builder()
                .challengeTitle(challengeTitle)
                .challengeContent("test")
                .challengeGoal("test success")
                .submission("test submit")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusDays(1))
                .category(category)
                .chatroom(chatRoom)
                .build();

        return challengeRepository.save(challenge);
    }
}