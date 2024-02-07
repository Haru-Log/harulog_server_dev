package goojeans.harulog.user.domain.dto;

import java.util.Map;

public abstract class OAuth2UserInfo {

    protected Map<String, Object> attributes;

    public OAuth2UserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public abstract Long getId(); // 카카오 - "id"

    public abstract String getNickname();

    public abstract String getImageUrl();

    public abstract String getEmail();
}
