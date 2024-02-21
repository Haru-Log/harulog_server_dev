package goojeans.harulog.post.domain.entity;


import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.comment.domain.entity.Comment;
import goojeans.harulog.domain.entity.*;
import goojeans.harulog.likes.domain.entity.Likes;
import goojeans.harulog.post.domain.dto.PostRequestDto;
import goojeans.harulog.user.domain.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE post SET active_status = 'DELETED' WHERE post_id = ? AND active_status <> 'DELETED'")
@SQLRestriction("active_status <> 'DELETED'")
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
    @NotNull
    private Category category;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;


    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Likes> likes;

    @Column(nullable = false)
    private String content;

    private int goal;

    private int activityTime;

    private String imgUrl;

    public void addUser(Users user) {
        if (this.user != user) {
            this.user = user;
        }
        if (!user.getPosts().contains(this)){
            user.addPost(this);
        }
    }

    public Post(PostRequestDto postRequestDto, Users user, Category category, int userGoal) {
        this.user = user;
        this.content = postRequestDto.getContent();
        this.activityTime = postRequestDto.getActivityTime();
        this.imgUrl = postRequestDto.getImgUrl();
        this.goal = userGoal;
        this.category = category;
    }



    public void update(PostRequestDto postRequestDto){
        this.activityTime = postRequestDto.getActivityTime();
        this.content = postRequestDto.getContent();
    }

    public void updateImage(String imageUrl) {
        this.imgUrl = imageUrl;
    }

}
