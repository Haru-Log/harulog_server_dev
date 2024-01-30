package goojeans.harulog.post.domain.repository;

import goojeans.harulog.post.domain.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post,Long> {

    List<Post> findByUserId(Long userId);
}
