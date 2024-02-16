package goojeans.harulog.admin.dto.response;

import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.user.domain.dto.response.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPostResponse {

    private PageInfo pageInfo;
    private List<PostInfos> content;

    public static AdminPostResponse from(Page<Post> postPage) {

        PageInfo pageInfo = PageInfo.builder()
                .number(postPage.getNumber())
                .size(postPage.getSize())
                .totalPages(postPage.getTotalPages())
                .totalElements(postPage.getTotalElements())
                .build();

        List<PostInfos> postInfos = postPage.getContent().stream().map(PostInfos::from)
                .toList();

        return new AdminPostResponse(pageInfo, postInfos);

    }
}
