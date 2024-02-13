package goojeans.harulog.user.domain.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserGoalResponse {

    private String categoryName;
    private Integer userGoal;
    private LocalDateTime updatedAt;


    public static UserGoalResponse of(String categoryName, Integer userGoal, LocalDateTime updatedAt) {
        return new UserGoalResponse(categoryName, userGoal, updatedAt);
    }
}
