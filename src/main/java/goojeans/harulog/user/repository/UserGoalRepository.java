package goojeans.harulog.user.repository;

import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.UserGoalId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserGoalRepository extends JpaRepository<UserGoal, UserGoalId> {

    @Query("select ug from UserGoal ug where ug.user.id=:userId")
    public List<UserGoal> findUserGoalsByUserId(@Param("userId") Long userId);

}
