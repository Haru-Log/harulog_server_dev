package goojeans.harulog.post.repository;

import goojeans.harulog.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Long> {
    List<Post> findByUserId(Long userId);

    @Query("SELECT p FROM post p LEFT JOIN likes l ON p.id = l.post.id GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Post> findPostsOrderByLikes();

    @Query("SELECT p FROM post p LEFT JOIN p.likes l WHERE p.category.categoryId = :categoryId GROUP BY p.id ORDER BY COUNT(l.id) DESC")
    List<Post> findPostsByCategoryOrderByLikes(@Param("categoryId") Long categoryId);

    @Query("select p from post p where p.user.id =:userId and p.createdAt >= :today")
    List<Post> findByUserIdAndToday(@Param("userId") Long userId, @Param("today") LocalDateTime today);

    @Query("SELECT p FROM post p LEFT JOIN p.likes l WHERE l.user.id = :userId ORDER BY p.createdAt DESC")
    List<Post> findPostsByUserOrderByCreatedAtDesc(@Param("userId") Long userId);

    @Query("SELECT p FROM post p " +
            "JOIN Follow f ON p.user.id = f.following.id " +
            "WHERE f.follower.id = :userId " +
            "ORDER BY p.createdAt DESC")
    List<Post> findPostsByFollowersOrderByCreatedAtDesc(@Param("userId") Long userId);

}


