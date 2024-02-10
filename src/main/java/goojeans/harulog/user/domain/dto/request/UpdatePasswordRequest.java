package goojeans.harulog.user.domain.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePasswordRequest {

    private String beforePassword;
    private String afterPassword;

}
