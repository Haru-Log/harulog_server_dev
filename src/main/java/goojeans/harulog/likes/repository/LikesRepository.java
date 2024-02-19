package goojeans.harulog.likes.repository;

import goojeans.harulog.likes.domain.entity.Likes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LikesRepository extends JpaRepository<Likes, Integer> {

    @Modifying
    @Query(value = "INSERT INTO likes(post_id, user_id, created_at) VALUES (:postId, :userId, CURRENT_TIMESTAMP)", nativeQuery = true)
    void insertLikes(@Param("postId") Long postId, @Param("userId") Long userId);



    @Modifying
    @Query("DELETE FROM likes l WHERE l.post.id = :postId AND l.user.id = :userId")
    void unLike(@Param("postId") Long postId, @Param("userId") Long userId);


    @Query("SELECT COUNT(l) FROM likes l WHERE l.post.id = :postId")
    int countLikesByPostId(@Param("postId") Long postId);

    boolean existsByPostIdAndUserId(Long postId, Long userId);

}