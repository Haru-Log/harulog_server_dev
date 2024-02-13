package goojeans.harulog.post.domain.dto;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.user.domain.entity.UserGoal;
import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostResponseDto {

    private Long id;
    private String content;
    private String imgUrl;
    private int activityTime;
    private String nickname;
    private int goal;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResponseDto> commentList = new ArrayList<>();
    private int likeCount;
    private int commentCount;

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos){
        this.id = post.getId();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.activityTime = post.getActivityTime();;
        this.nickname = post.getUser().getNickname();
        this.createdAt = post.getCreatedAt();
        this.commentList = commentResponseDtos;
        this.goal =post.getGoal();
    }
    public PostResponseDto(Post post){
        this.id = post.getId();
        this.content = post.getContent();
        this.activityTime = post.getActivityTime();
        this.createdAt = post.getCreatedAt();
        this.goal = post.getGoal();
    }

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos, int commentCount, int likeCount) {
        this.id = post.getId();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.activityTime = post.getActivityTime();
        this.nickname = post.getUser().getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = post.getCreatedAt();
        this.commentList = commentResponseDtos;
        this.goal = post.getGoal();
    }


}
