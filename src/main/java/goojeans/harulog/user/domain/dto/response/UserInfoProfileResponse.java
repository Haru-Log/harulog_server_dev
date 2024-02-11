package goojeans.harulog.user.domain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoProfileResponse {

    private String imageUrl;
    private String userName;
    private String nickname;
    private Integer followerCount;
    private Integer followingCount;

}
