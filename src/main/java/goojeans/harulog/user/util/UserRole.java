package goojeans.harulog.user.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    GUEST("GUEST"), USER("USER"), ADMIN("ADMIN"), PREMIUM("PREMIUM");

    private final String role;
}
