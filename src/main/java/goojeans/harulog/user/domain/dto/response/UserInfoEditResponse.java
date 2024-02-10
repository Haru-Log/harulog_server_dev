package goojeans.harulog.user.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import goojeans.harulog.user.util.SocialType;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
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
