package goojeans.harulog.user.domain.dto.response;

import goojeans.harulog.user.util.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginSuccessResponse {

    private String nickname;
    private UserRole userRole;

}
