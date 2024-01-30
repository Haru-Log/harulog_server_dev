package goojeans.harulog.user.domain.dto.request;

import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationFoam {

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String userName;

    @NotNull
    private String nickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private UserRole userRole;

}
