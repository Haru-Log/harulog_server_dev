package goojeans.harulog.user.domain.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGoalResponse {

    private String categoryName;
    private Integer userGoal;


    public static UserGoalResponse of(String categoryName, Integer userGoal) {
        return new UserGoalResponse(categoryName, userGoal);
    }
}
