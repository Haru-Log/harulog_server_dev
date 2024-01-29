package goojeans.harulog.user.domain.dto;

import goojeans.harulog.user.util.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class JwtUserDetail extends User {

    private final String email;
    private final String nickname;
    private final String imageUrl;
    private final String roles;

    @Builder(builderMethodName = "userDetailBuilder")
    public JwtUserDetail(String username, String password, String email, String imageUrl, UserRole role, Collection<? extends GrantedAuthority> authorities, String nickname) {
        super(username, password, authorities);
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
        this.roles = role.getRole();
    }

}
