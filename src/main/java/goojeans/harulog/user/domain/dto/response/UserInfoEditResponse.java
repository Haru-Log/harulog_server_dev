package goojeans.harulog.user.domain.dto.response;

import goojeans.harulog.user.util.SocialType;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoEditResponse {

    private String imageUrl;
    private String userName;
    private String nickname;
    private String email;
    private String contactNumber;
    private String createdAt;
    private String introduction;
    private SocialType socialType;

}
