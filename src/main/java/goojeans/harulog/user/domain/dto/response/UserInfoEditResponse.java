package goojeans.harulog.user.domain.dto.response;

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

}
