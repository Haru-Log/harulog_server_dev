package goojeans.harulog.user.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserInfoRequest {

    private String nickname;
    private String contactNumber;
    private String introduction;

}
