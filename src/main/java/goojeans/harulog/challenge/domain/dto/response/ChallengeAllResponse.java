package goojeans.harulog.challenge.domain.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeAllResponse {

    private Long challengeId;

    private String challengeTitle;

    private String categoryName;

    private int challengeUserCount;

    private String imageUrl;
}
