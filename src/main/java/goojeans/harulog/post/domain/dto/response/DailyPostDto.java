package goojeans.harulog.post.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DailyPostDto {

    private String categoryName;

    private Long userGoal;

    private LocalDateTime goalUpdatedAt;

    private Long achievement;

    public void updateAchievement(long achievement) {
        this.achievement = achievement;
    }
}
