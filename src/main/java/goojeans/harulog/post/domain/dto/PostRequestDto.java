package goojeans.harulog.post.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    private String content;
    private String categoryName;
    private String imgUrl;
    private int activityTime;
}
