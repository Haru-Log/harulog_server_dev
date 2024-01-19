package goojeans.harulog.category.repository;

import goojeans.harulog.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByCategoryId(Long categoryId);

    Optional<Category> findByCategoryName(String CategoryName);
}
