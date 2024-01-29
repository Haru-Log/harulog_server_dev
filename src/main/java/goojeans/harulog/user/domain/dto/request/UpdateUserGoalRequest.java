package goojeans.harulog.user.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserGoalRequest {

    private String nickname;
    @NotNull
    private String categoryName;
    @NotNull
    private Integer goal;

    public static UpdateUserGoalRequest of(String nickname, String categoryName, Integer goal) {
        return new UpdateUserGoalRequest(nickname, categoryName, goal);
    }
}
