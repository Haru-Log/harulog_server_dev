package goojeans.harulog.user.domain.dto.request;

import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SignUpRequest {

    @NotNull
    @Email
    private String email;
    @NotNull
    private String userName;
    @NotNull
    private String nickname;
    @NotNull
    private String password;

    public Users toEntity() {
        return Users.builder()
                .userRole(UserRole.USER)
                .email(email)
                .password(password)
                .nickname(nickname)
                .socialType(SocialType.HARU)
                .userName(userName)
                .build();
    }
}
