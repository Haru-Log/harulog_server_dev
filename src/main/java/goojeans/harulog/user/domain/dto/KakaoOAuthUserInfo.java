package goojeans.harulog.user.domain.dto;

import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class KakaoOAuthUserInfo extends OAuth2UserInfo {
    public KakaoOAuthUserInfo(Map<String, Object> attribute) {
        super(attribute);
    }

    @Override
    public Long getId() {
        return (Long) attributes.get("id");
    }

    @Override
    public String getNickname() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        if (profile == null) return null;

        return (String) profile.get("nickname");
    }

    @Override
    public String getImageUrl() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) return null;

        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        if (profile == null) return null;

        return (String) profile.get("thumbnail_image_url");
    }

    @Override
    public String getEmail() {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        if (kakaoAccount == null) return null;

        boolean isEmailVerified = (boolean) kakaoAccount.get("is_email_verified");
        boolean isEmailValid = (boolean) kakaoAccount.get("is_email_valid");

        if (!isEmailValid || !isEmailVerified) return null;

        return (String) kakaoAccount.get("email");
    }
}
