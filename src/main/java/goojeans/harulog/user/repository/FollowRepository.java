package goojeans.harulog.user.repository;

import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {

    @Query("select f from Follow f where f.following.id=:userId")
    public List<Follow> findFollowerByUserId(@Param("userId") Long userId);

    @Query("select f from Follow f where f.follower.id=:userId")
    public List<Follow> findFollowingByUserId(@Param("userId") Long userId);

    //TODO: 추후에 soft delete 로 전환
    @Modifying
    @Query("delete from Follow f where f.id=:followId")
    public void deleteFollow(@Param("followId") FollowId followId);

}
