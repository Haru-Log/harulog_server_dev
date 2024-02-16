package goojeans.harulog.post.repository;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.config.QuerydslConfig;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Transactional
@Slf4j
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QuerydslConfig.class)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Autowired
    EntityManager em;

    private String testString1 = "test1";
    private String testString2 = "test2";

    @BeforeEach
    void beforeEach() {
        Users testUser1 = Users.builder()
                .email(testString1)
                .nickname(testString1)
                .socialType(SocialType.HARU)
                .userName(testString1)
                .userRole(UserRole.USER)
                .password(testString1)
                .build();

        Users testUser2 = Users.builder()
                .email(testString2)
                .nickname(testString2)
                .socialType(SocialType.HARU)
                .userName(testString2)
                .userRole(UserRole.USER)
                .password(testString2)
                .build();

        em.persist(testUser1);
        em.persist(testUser2);

        Category category1 = em.find(Category.class, 1L);
        Category category2 = em.find(Category.class, 2L);

        Post post1 = Post.builder()
                .content(testString1)
                .user(testUser1)
                .category(category1)
                .build();

        Post post2 = Post.builder()
                .content(testString2)
                .user(testUser2)
                .category(category2)
                .build();

        em.persist(post1);
        em.persist(post2);

    }

    @Test
    @DisplayName("카테고리와 유저 정보 같이 찾아오기")
    void findAllForAdmin() {
        //Given
        PageRequest pageRequest = PageRequest.of(0, 10);

        //When
        Page<Post> page = postRepository.findAllWithCategoryAndUser(pageRequest);

        //Then
        assertThat(page.getContent()).hasSize(2);

    }

}
