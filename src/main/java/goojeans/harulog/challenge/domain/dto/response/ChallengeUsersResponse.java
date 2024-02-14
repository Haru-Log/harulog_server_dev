package goojeans.harulog.challenge.domain.dto.response;

import goojeans.harulog.challenge.util.ChallengeRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChallengeUsersResponse {

    private Long userId;

    private String nickname;

    private String imageUrl;

    private ChallengeRole role;

    private boolean success;
}
