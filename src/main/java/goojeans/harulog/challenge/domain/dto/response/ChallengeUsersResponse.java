package goojeans.harulog.challenge.domain.dto.response;

import goojeans.harulog.challenge.util.ChallengeRole;
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

    //TODO: 성공한 날짜 수 내려주기
//    private int days;
}
