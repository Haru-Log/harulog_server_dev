package goojeans.harulog.category.repository;

import goojeans.harulog.category.domain.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.categoryName = :name")
    Category findByCategoryName(@Param("name") String CategoryName);
}
