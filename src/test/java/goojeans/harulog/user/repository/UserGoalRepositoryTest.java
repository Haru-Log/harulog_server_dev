package goojeans.harulog.user.repository;


import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.config.QuerydslConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.UserGoalId;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
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
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class)
public class UserGoalRepositoryTest {

    @Autowired
    UserGoalRepository repository;
    @Autowired
    EntityManager em;

    public final String testString = "test";

    private final Users user1 = createUser("test1", "test1@test");
    private final Users user2 = createUser("test2", "test2@test");

    private Category category1;
    private Category category2;

    private UserGoal userGoal1;
    private UserGoal userGoal2;


    @BeforeEach
    void beforeEach() {

        em.persist(user1);
        em.persist(user2);

        category1 = findCategory("운동");
        category2 = findCategory("독서");

        userGoal1 = UserGoal.of(user1, category1, 60);
        userGoal2 = UserGoal.of(user1, category2, 30);

        em.persist(userGoal1);
        em.persist(userGoal2);

    }

    @Test
    @DisplayName("유저 목표 생성")
    void createUserGoal() {
        //Given
        UserGoal userGoal3 = UserGoal.of(user2, category1, 80);

        //When
        UserGoal save = repository.save(userGoal3);

        //Then
        UserGoal findGoal = em.find(UserGoal.class, save.getUserGoalId());
        assertThat(findGoal).isEqualTo(save);

    }

    @Test
    @DisplayName("특정 카테고리 유저 목표 가져오기")
    void findUserGoal() {

        try {
            //When
            UserGoal findGoal1 = repository.findById(new UserGoalId(user1.getId(), category1.getCategoryId()))
                    .stream()
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ResponseCode.SUCCESS));

            UserGoal findGoal2 = repository.findById(new UserGoalId(user1.getId(), category2.getCategoryId()))
                    .stream()
                    .findAny()
                    .orElseThrow(() -> new BusinessException(ResponseCode.SUCCESS));
            //Then
            assertThat(findGoal1).isEqualTo(userGoal1);
            assertThat(findGoal2).isEqualTo(userGoal2);

        } catch (BusinessException e) {
            Assertions.fail();
        }

    }

    @Test
    @DisplayName("특정 유저의 목표 리스트 가져오기")
    void findAllGoalByUserId() {
        //When
        List<UserGoal> findGoals = repository.findUserGoalsByUserId(user1.getId());

        //Then
        assertThat(findGoals).hasSize(2);
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

    private Category findCategory(String name) {
        String jpql = "select c from Category c where c.categoryName=:name";

        return em.createQuery(jpql, Category.class)
                .setParameter("name", name)
                .getSingleResult();
    }

    private void deleteUser(Users user) {
        String jpql = "delete from Users u where u.id=:id";
        em.createQuery(jpql)
                .setParameter("id", user.getId())
                .executeUpdate();
    }

}
