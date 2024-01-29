package goojeans.harulog.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtTokenInfo {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static JwtTokenInfo create(String grantType, String accessToken, String refreshToken) {
        return new JwtTokenInfo(grantType, accessToken, refreshToken);
    }
}
