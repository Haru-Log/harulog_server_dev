package goojeans.harulog.post.domain.dto.response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class YearlyPostDto {

    @NotNull
    private LocalDateTime date;

    @NotNull
    private String categoryName;

    @NotNull
    private int activityTime;
}
