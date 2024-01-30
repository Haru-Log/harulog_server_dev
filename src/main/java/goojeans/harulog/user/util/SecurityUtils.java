package goojeans.harulog.user.util;

import goojeans.harulog.user.domain.dto.JwtUserDetail;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public JwtUserDetail getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof JwtUserDetail) {
            return (JwtUserDetail) principal;
        } else {
            return null;
        }
    }
}
