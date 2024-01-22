package goojeans.harulog.post.domain.entity;


import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.comment.domain.entity.Comment;
import goojeans.harulog.domain.entity.*;
import goojeans.harulog.likes.domain.entity.Likes;
import goojeans.harulog.user.domain.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "post")
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes;

    @Column(nullable = false)
    private String content;

    @NotNull
    private int activityTime;

    private String imgUrl;
}
