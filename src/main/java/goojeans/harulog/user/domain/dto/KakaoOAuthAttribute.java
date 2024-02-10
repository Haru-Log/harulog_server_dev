package goojeans.harulog.user.domain.dto;

import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

@Getter
public class KakaoOAuthAttribute {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2UserInfo oauth2UserInfo;
    private PasswordEncoder passwordEncoder;

    @Builder
    private KakaoOAuthAttribute(String nameAttributeKey, OAuth2UserInfo oauth2UserInfo) {
        this.nameAttributeKey = nameAttributeKey;
        this.oauth2UserInfo = oauth2UserInfo;
    }

    public KakaoOAuthAttribute(String nameAttributeKey) {
        this.nameAttributeKey = nameAttributeKey;
    }

    public static KakaoOAuthAttribute of(String nameAttributeKey, Map<String, Object> attribute) {
        return KakaoOAuthAttribute.builder()
                .nameAttributeKey(nameAttributeKey)
                .oauth2UserInfo(new KakaoOAuthUserInfo(attribute))
                .build();
    }

    public Users toEntity(OAuth2UserInfo kakaoOAuthUserInfo) {
        return Users.builder()
                .socialType(SocialType.KAKAO)
                .email(kakaoOAuthUserInfo.getEmail())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .userName("kakao_" + UUID.randomUUID().toString().substring(1, 9))
                .nickname(kakaoOAuthUserInfo.getNickname() + UUID.randomUUID().toString().substring(1, 9))
                .imageUrl(kakaoOAuthUserInfo.getImageUrl())
                .socialId(kakaoOAuthUserInfo.getId())
                .userRole(UserRole.GUEST)
                .build();
    }
}
