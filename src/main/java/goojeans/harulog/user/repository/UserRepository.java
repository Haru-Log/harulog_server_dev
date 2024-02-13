package goojeans.harulog.user.repository;

import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    Optional<Users> findUsersByEmailAndUserName(String email, String userName);

    Optional<Users> findUsersByNickname(String nickname);

    Page<Users> findByNicknameStartingWith(String nickname, Pageable pageable);

    @Query("select f.follower from Follow f where f.following.id=:userId and f.follower.nickname like :nickname%")
    Page<Users> findUserOnFollowers(@Param("userId") Long userId, @Param("nickname") String nickname, Pageable pageable);

    @Query("select f.following from Follow f where f.follower.id=:userId and f.following.nickname like :nickname%")
    Page<Users> findUserOnFollowings(@Param("userId") Long userId, @Param("nickname") String nickname, Pageable pageable);

    @EntityGraph(attributePaths = {"followers", "followings"})
    Optional<Users> findByNickname(String nickname);

    Optional<Users> findUsersByEmail(String email);

    @Modifying
    @Query("update Users u set u.refreshToken=:refreshToken where u.nickname=:nickname")
    void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("nickname") String nickname);

    Optional<Users> findUsersBySocialId(Long socialId);

    Optional<Users> findUsersById(Long id);


}
