package goojeans.harulog.admin.dto.response;

import goojeans.harulog.post.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostInfos {

    private Long id;
    private String categoryName;
    private String content;
    private String nickname;

    public static PostInfos from(Post post) {
        return PostInfos.builder()
                .id(post.getId())
                .nickname(post.getUser().getNickname())
                .content(post.getContent())
                .categoryName(post.getContent())
                .build();
    }

}
