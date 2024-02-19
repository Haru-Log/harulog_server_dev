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
public class PostLikeResponseDto {
    private Long id;
    private String content;
    private String imgUrl;
    private String profileImg;
    private String categoryName;
    private int activityTime;
    private String nickname;
    private int goal;
    private boolean likedByUser;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private int likeCount;
    private int commentCount;
    private List<CommentResponseDto> commentList = new ArrayList<>();


    public PostLikeResponseDto(Post post, List<CommentResponseDto> commentResponseDtos, int commentCount, int likeCount, boolean like) {
        this.id = post.getId();
        this.content = post.getContent();
        this.profileImg = post.getUser().getImageUrl();
        this.imgUrl = post.getImgUrl();
        this.activityTime = post.getActivityTime();
        this.categoryName = post.getCategory().getCategoryName();
        this.nickname = post.getUser().getNickname();
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.createdAt = post.getCreatedAt();
        this.updateAt = post.getUpdatedAt();
        this.commentList = commentResponseDtos;
        this.goal = post.getGoal();
        this.likedByUser = like;
    }

}
