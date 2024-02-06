package goojeans.harulog.user.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserGoalRequest {

    private Long userId;
    @NotNull
    private String categoryName;
    @NotNull
    private Integer goal;

    public static UpdateUserGoalRequest of(Long userId, String categoryName, Integer goal) {
        return new UpdateUserGoalRequest(userId, categoryName, goal);
    }
}
