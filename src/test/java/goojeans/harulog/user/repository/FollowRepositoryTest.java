package goojeans.harulog.user.repository;

import goojeans.harulog.config.QuerydslConfig;
import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
@Import(QuerydslConfig.class)
public class FollowRepositoryTest {

    @Autowired
    FollowRepository repository;
    @Autowired
    EntityManager em;

    public final String testString = "test";

    private final Users user1 = createUser("test1", "test1@test");
    private final Users user2 = createUser("test2", "test2@test");
    private final Users user3 = createUser("test3", "test3@test");

    private Follow follow1;
    private Follow follow2;
    private Follow follow3;

    @BeforeEach
    void beforeEach() {

        em.persist(user1);
        em.persist(user2);
        em.persist(user3);

        //user1 -> user2, user2 -> user1, user3 -> user1
        //user1 2/1  user2 1/1  user3 1/0
        follow1 = Follow.of(user1, user2);
        follow2 = Follow.of(user2, user1);
        follow3 = Follow.of(user3, user1);

        em.persist(follow1);
        em.persist(follow2);
        em.persist(follow3);

    }

    @AfterEach
    void afterEach() {

        deleteFollow(follow1);
        deleteFollow(follow2);
        deleteFollow(follow3);

        deleteUser(user1);
        deleteUser(user2);
        deleteUser(user3);
    }


    @Test
    @DisplayName("팔로우")
    void follow() {
        //Given
        Follow newFollow = Follow.of(user1, user3);

        //When
        Follow save = repository.save(newFollow);

        //Then
        Follow findFollow = em.find(Follow.class, newFollow.getId());
        assertThat(findFollow).isEqualTo(save);

        deleteFollow(findFollow);
    }

    @Test
    @DisplayName("팔로워 찾기")
    void findFollower() {

        //When
        List<Follow> user1Follower = repository.findFollowerByUserId(user1.getId());
        List<Follow> user2Follower = repository.findFollowerByUserId(user2.getId());
        List<Follow> user3Follower = repository.findFollowerByUserId(user3.getId());

        //Then
        assertThat(user1Follower).hasSize(2);
        assertThat(user2Follower).hasSize(1);
        assertThat(user3Follower).hasSize(0);
    }

    @Test
    @DisplayName("팔로잉 찾기")
    void findFollow() {

        //When
        List<Follow> user1Following = repository.findFollowingByUserId(user1.getId());
        List<Follow> user2Following = repository.findFollowingByUserId(user2.getId());
        List<Follow> user3Following = repository.findFollowingByUserId(user3.getId());

        //Then
        assertThat(user1Following).hasSize(1);
        assertThat(user2Following).hasSize(1);
        assertThat(user3Following).hasSize(1);
    }

    @Test
    @DisplayName("팔로우 삭제(취소)")
    void deleteFollowing() {

        //When
        repository.deleteFollow(follow3.getId());

        //Then
        String jpql = "select f from Follow f where f.following.id=:id";
        List<Follow> result = em.createQuery(jpql, Follow.class)
                .setParameter("id", user1.getId())
                .getResultList();

        assertThat(result).hasSize(1);

    }


    private Users createUser(String nickname, String email) {
        return Users.builder()
                .email(email)
                .userName(testString)
                .nickname(nickname)
                .password(testString)
                .contactNumber(testString)
                .imageUrl(testString)
                .socialType(SocialType.HARU)
                .build();
    }

    private void deleteFollow(Follow follow) {
        Follow find = em.find(Follow.class, follow.getId());
        if (find != null){
            String jpql = "delete from follow where follower_id=:follower and following_id=:following";
            em.createNativeQuery(jpql)
                    .setParameter("follower", follow.getFollower().getId())
                    .setParameter("following", follow.getFollowing().getId())
                    .executeUpdate();
        }

    }

    private void deleteUser(Users user) {
        String jpql = "delete from Users u where u.id=:id";
        em.createQuery(jpql)
                .setParameter("id", user.getId())
                .executeUpdate();
    }

}
