package goojeans.harulog.likes.domain.entity;


import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.post.domain.entity.Post;
import jakarta.persistence.*;
import lombok.*;

@Entity(name = "likes")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class Likes extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
}
