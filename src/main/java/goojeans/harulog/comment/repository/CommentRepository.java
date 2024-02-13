package goojeans.harulog.comment.repository;

import goojeans.harulog.comment.domain.entity.Comment;
import goojeans.harulog.post.domain.entity.Post;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    @Transactional
    void deleteAllByPost(Post post);
    Optional<Comment> findByPostAndId(Post post, Long id);
    Comment findByDepth(int depth);
    List<Comment> findByParentId(Long parentId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countCommentsByPostId(@Param("postId") Long postId);

}
