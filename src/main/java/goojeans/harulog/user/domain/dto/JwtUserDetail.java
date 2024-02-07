package goojeans.harulog.user.domain.dto;

import goojeans.harulog.user.util.UserRole;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
public class JwtUserDetail extends User {

    private final Long id;
    private final String email;
    private final String nickname;
    private final String imageUrl;
    private final UserRole roles;
    private final String introduction;
    private final LocalDateTime createdAt;

    @Builder(builderMethodName = "userDetailBuilder")
    public JwtUserDetail(Long id, String username, String password, String email, String imageUrl, UserRole role,
                         Collection<? extends GrantedAuthority> authorities, String nickname, String introduction, LocalDateTime createdAt) {
        super(username, password, authorities);
        this.id = id;
        this.nickname = nickname;
        this.email = email;
        this.imageUrl = imageUrl;
        this.roles = role;
        this.introduction = introduction;
        this.createdAt = createdAt;
    }

}
