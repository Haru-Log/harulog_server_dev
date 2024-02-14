package goojeans.harulog.challenge.domain.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeLeaveRequest {

    @NotNull
    private Long challengeId;
}
