package goojeans.harulog.user.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserGoalsRequest {

    private Long userId;
    @NotNull
    private List<UpdateGoalsDto> updateGoalsList;

    public static UpdateUserGoalsRequest of(Long userId, List<UpdateGoalsDto> updateGoalsList) {
        return new UpdateUserGoalsRequest(userId, updateGoalsList);
    }
}
