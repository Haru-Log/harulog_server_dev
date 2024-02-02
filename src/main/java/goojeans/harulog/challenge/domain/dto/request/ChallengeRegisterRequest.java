package goojeans.harulog.challenge.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeRegisterRequest {

    @NotNull
    private String challengeTitle;
    @NotNull
    private String challengeContent;
    @NotNull
    private int challengeGoal;
    @NotNull
    private String submission;
    @NotNull
    private LocalDateTime startDate;
    @NotNull
    private LocalDateTime endDate;
    @NotNull
    private String categoryName;
}
