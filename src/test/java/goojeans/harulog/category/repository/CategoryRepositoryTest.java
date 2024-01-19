package goojeans.harulog.category.repository;

import goojeans.harulog.category.domain.entity.Category;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CategoryRepositoryTest {

    @Autowired
    CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 이름으로 조회하기")
    void getCategory() {
        Category category = categoryRepository.findByCategoryName("운동").orElse(null);

        Assertions.assertThat(category).isNotNull();
        Assertions.assertThat(category.getCategoryName()).isEqualTo("운동");
    }
}