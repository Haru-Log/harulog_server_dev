package goojeans.harulog.user.repository;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.UserGoalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalId> {

    @Query("select ug from UserGoal ug join fetch ug.category c where ug.user.id=:userId")
    public List<UserGoal> findUserGoalsByUserId(@Param("userId") Long userId);

    Optional<UserGoal> findUserGoalByUserIdAndCategory(Long userId, Category category);

}
