package goojeans.harulog.user;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
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
            .build();

    private Challenge testChallenge;
    private ChallengeUser testChallengeUser;

    @BeforeEach
    void beforeEach() {

        em.persist(testUser);

        //chatroom 생성
        ChatRoom testChatroom = new ChatRoom();
        em.persist(testChatroom);

        //category 찾아오기
        String jpql = "select c from Category c where c.categoryName = :name";
        Category findCategory = em.createQuery(jpql, Category.class)
                .setParameter("name", "운동")
                .getSingleResult();

        //challenge 생성
        testChallenge = Challenge.builder()
                .challengeContent(testString)
                .challengeGoal(testString)
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
                .nickname(testString)
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
    @DisplayName("아이디로 유저 & 게시물 & 챌린지 개수 정보 가져오기")
    void findUserById() {
        //Given
        String jpql = "select c from Category c where c.categoryName='운동'";
        Category testCategory = em.createQuery(jpql, Category.class).getSingleResult();

        Post testPost = Post.builder()
                .category(testCategory)
                .content("test")
                .activityTime(1)
                .imgUrl("test")
                .build();
        testPost.addUser(testUser);

        em.persist(testPost);

        //When
        Users findUser = repository.findUsersById(testId1).stream()
                .findAny()
                .orElseThrow(() -> new RuntimeException("no such entity"));

        //Then
        assertThat(findUser).isEqualTo(testUser);
        assertThat(findUser.getPosts()).hasSize(1);

        String nativeQuery = "delete from post where post_id = :pid";
        em.createNativeQuery(nativeQuery)
                .setParameter("pid", testPost.getId())
                .executeUpdate();
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
        assertThat(findAll).hasSize(1);

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