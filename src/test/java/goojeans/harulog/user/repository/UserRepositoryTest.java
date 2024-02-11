package goojeans.harulog.user.repository;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

    @Autowired
    private UserRepository repository;
    @Autowired
    private EntityManager em;

    private Long testId1;
    private Long testChallengeId;
    private final String testString = "test";

    private final Users testUser = Users.builder()
            .userName(testString)
            .email(testString)
            .nickname(testString)
            .password(testString)
            .socialType(SocialType.HARU)
            .refreshToken(testString)
            .build();

    private Challenge testChallenge;
    private ChallengeUser testChallengeUser;

    @BeforeEach
    void beforeEach() {

        em.persist(testUser);

        //chatroom 생성
        ChatRoom testChatroom = ChatRoom.createDM();
        em.persist(testChatroom);

        //category 찾아오기
        String jpql = "select c from Category c where c.categoryName = :name";
        Category findCategory = em.createQuery(jpql, Category.class)
                .setParameter("name", "운동")
                .getSingleResult();

        //challenge 생성
        testChallenge = Challenge.builder()
                .challengeContent(testString)
                .challengeGoal(3)
                .challengeTitle(testString)
                .submission(testString)
                .imageUrl(testString)
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now())
                .chatroom(testChatroom)
                .category(findCategory)
                .build();

        em.persist(testChallenge);

        testChallengeId = testChallenge.getChallengeId();

        testChallengeUser = ChallengeUser.create(testUser, testChallenge);

        //양방향 맵핑
        testUser.addChallengeUser(testChallengeUser);

        em.persist(testChallengeUser);
        testId1 = testUser.getId();
    }

    @AfterEach
    void afterEach() {
        if (em.find(Users.class, testId1) != null){
            userHardDelete(testId1, testChallengeId);
        }
    }

    @Test
    @DisplayName("엔티티 저장하기")
    void saveUser() {
        //Given
        Users newUser = Users.builder()
                .socialType(SocialType.HARU)
                .userName(testString)
                .password(testString)
                .nickname("test2")
                .email("testString")
                .contactNumber(testString)
                .build();

        //When
        Users save = repository.save(newUser);

        //Then
        Users findUser = em.find(Users.class, save.getId());

        assertThat(findUser).isEqualTo(save);

        userHardDelete(findUser.getId(), null);
    }

    @Test
    @DisplayName("아이디로 유저만 찾아오기")
    void findById() {
        try {
            //When
            Users findUser = repository.findById(testId1).stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("no such entity"));

            //Then
            assertThat(findUser).isEqualTo(testUser);
        } catch (RuntimeException e) {
            Assertions.fail(e.getMessage());
        }

    }

    @Test
    @DisplayName("이메일로 유저 & 팔로워 팔로잉 수 가져오기")
    void findUserById() {
        //Given
        String followerString = "follower";
        String followingString = "following";

        Users from = Users.builder()
                .email(followerString)
                .userName(followerString)
                .socialType(SocialType.HARU)
                .password(followerString)
                .imageUrl(followerString)
                .userRole(UserRole.USER)
                .nickname(followerString)
                .build();

        Users to = Users.builder()
                .email(followingString)
                .userName(followingString)
                .socialType(SocialType.HARU)
                .password(followingString)
                .imageUrl(followingString)
                .userRole(UserRole.USER)
                .nickname(followingString)
                .build();

        em.persist(from);
        em.persist(to);

        Follow follow = Follow.builder()
                .follower(from)
                .following(to)
                .build();

        from.addFollowing(follow);
        to.addFollower(follow);

        //When
        Users findUser = repository.findByNickname(to.getNickname()).stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("no such entity"));

        //Then
        assertThat(findUser).isEqualTo(to);
        assertThat(findUser.getFollowers()).hasSize(1);
        assertThat(findUser.getFollowings()).hasSize(0);

    }

    @Test
    @DisplayName("전체 유저 리스트로 찾아오기")
    void findAll() {
        //When
        List<Users> findAll = repository.findAll();
        for (Users user : findAll) {
            log.info("user name={}", user.getUserName());
        }
        //Then
        assertThat(findAll).hasSize(2);

    }

    @Test
    @DisplayName("이메일과 유저 이름으로 엔티티 찾기")
    void findUsersByEmailAndUserName() {
        try {
            Users findUser = repository.findUsersByEmailAndUserName(testString, testString).stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("no such entity"));
            assertThat(findUser).isEqualTo(testUser);
        } catch (Exception e) {
            Assertions.fail();
        }

    }

    @Test
    @DisplayName("엔티티 삭제")
    void deleteUser() {
        repository.deleteById(testId1);
        Users findUser = em.find(Users.class, testId1);
        assertThat(findUser).isNull();
    }

    @Test
    @DisplayName("유저 닉네임으로 검색")
    void searchUser() {
        String searchKeyword = "t";
        List<Users> users = repository.searchUsersByNickname(searchKeyword);

        assertThat(users).hasSize(1);
    }

    @Test
    @DisplayName("유저 닉네임으로 찾아오기")
    void findUserByNickname() {
        try {
            Users findUser = repository.findUsersByNickname(testString).stream()
                    .findAny()
                    .orElseThrow(() -> new RuntimeException("no such entity"));

            assertThat(findUser).isEqualTo(testUser);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("유저 이메일로 찾아오기")
    void findUsersByEmail() {
        try {
            Users findUser = repository.findUsersByEmail(testString).stream().findAny()
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            assertThat(findUser).isEqualTo(testUser);

        } catch (BusinessException e){
            Assertions.fail();
        }
    }

    @Test
    @DisplayName("닉네임으로 리프레쉬 토큰 업데이트")
    void updateRefreshToken() {
        // Given
        String updateToken = "test for update";

        // When
        repository.updateRefreshToken(updateToken, testString);

        // Then
        // 영속성 컨텍스트에 남아있지 않고 새로 찾아온 엔티티 체크
        em.flush();
        em.clear();

        Users findUser = em.find(Users.class, testId1);

        assertThat(findUser.getRefreshToken()).isEqualTo(updateToken);
    }

    @Test
    @DisplayName("소셜 아이디로 유저 찾기")
    void findUserBySocialId() {
        //Given
        String socialTest = "testSocial";
        Long socialId = 1L;
        Users socialUser = Users.builder()
                .userName(testString)
                .userRole(UserRole.GUEST)
                .nickname(socialTest)
                .socialType(SocialType.KAKAO)
                .email(socialTest)
                .password(socialTest)
                .socialId(socialId)
                .build();
        em.persist(socialUser);

        //When
        Users users = repository.findUsersBySocialId(socialId).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        //Then
        assertThat(users).isEqualTo(socialUser);

    }

    /**
     * method that helps hard delete user from users table with id.
     */
    private void userHardDelete(Long userId, Long challengeId) {
        em.createNativeQuery("delete from challenge_user where challenge_id=:challengeId and user_id=:userId")
                .setParameter("userId", userId)
                .setParameter("challengeId", challengeId)
                .executeUpdate();
        em.createNativeQuery("delete from challenge where challenge_id=:challengeId")
                .setParameter("challengeId", challengeId)
                .executeUpdate();
        em.createNativeQuery("delete from users where user_id = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}