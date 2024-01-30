package goojeans.harulog.user.repository;

import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long> {

    public Optional<Users> findUsersByEmailAndUserName(String email, String userName);

    public Optional<Users> findUsersByNickname(String nickname);

    //TODO: paging (cursor or offset)
    /**
     * Based on input username, find user by cursor pagination with like.
     * @param nickname : input username
     * @return user list which matches with input. Ordered by username.
     */
    @Query("select u from Users u where u.nickname like :nickname%")
    public List<Users> searchUsersByNickname(@Param("nickname")String nickname);


    @EntityGraph(attributePaths = {"challengeUsers", "posts"})
    Optional<Users> findUsersById(Long id);

    public Optional<Users> findUsersByEmail(String email);

    @Modifying
    @Query("update Users u set u.refreshToken=:refreshToken where u.nickname=:nickname")
    public void updateRefreshToken(@Param("refreshToken") String refreshToken, @Param("nickname") String nickname);
}
