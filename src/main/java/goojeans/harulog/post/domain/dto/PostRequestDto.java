package goojeans.harulog.post.domain.dto;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostRequestDto {
    @NotNull
    private String content;
    @NotNull
    private String categoryName;

    private String imgUrl;

    @NotNull
    private int activityTime;
}
