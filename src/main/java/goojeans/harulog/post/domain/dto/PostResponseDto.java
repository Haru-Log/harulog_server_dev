package goojeans.harulog.post.domain.dto;


import goojeans.harulog.comment.domain.dto.CommentResponseDto;
import goojeans.harulog.post.domain.entity.Post;
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
    private String profileImg;
    private String imgUrl;
    private int activityTime;
    private String nickname;
    private String categoryName;
    private int goal;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private List<CommentResponseDto> commentList = new ArrayList<>();
    private int likeCount;
    private int commentCount;

    public PostResponseDto(Post post){
        this.id = post.getId();
        this.content = post.getContent();
        this.categoryName = post.getCategory().getCategoryName();
        this.activityTime = post.getActivityTime();
        this.createdAt = post.getCreatedAt();
        this.updateAt = post.getUpdatedAt();
        this.goal = post.getGoal();
        this.nickname = post.getUser().getNickname();
    }

    public PostResponseDto(Post post, List<CommentResponseDto> commentResponseDtos, int commentCount, int likeCount) {
        this.id = post.getId();
        this.content = post.getContent();
        this.profileImg = post.getUser().getImageUrl();
        this.imgUrl = post.getImgUrl();
        this.categoryName = post.getCategory().getCategoryName();
        this.activityTime = post.getActivityTime();
        this.nickname = post.getUser().getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = post.getCreatedAt();
        this.updateAt = post.getUpdatedAt();
        this.commentList = commentResponseDtos;
        this.goal = post.getGoal();
    }

    public PostResponseDto(Post post, int commentCount, int likeCount){
        this.id = post.getId();
        this.content = post.getContent();
        this.imgUrl = post.getImgUrl();
        this.categoryName = post.getCategory().getCategoryName();
        this.activityTime = post.getActivityTime();
        this.nickname= post.getUser().getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.goal = post.getGoal();
        this.createdAt = post.getCreatedAt();
        this.updateAt = post.getUpdatedAt();
    }


}
