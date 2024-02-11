package goojeans.harulog.user.domain.dto.response;

import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FollowInfo {

    private String nickname;
    private String imageUrl;
    private String userName;

    public static FollowInfo entityToResponse(Users user) {
        return FollowInfo.builder()
                .nickname(user.getNickname())
                .imageUrl(user.getImageUrl())
                .userName(user.getUserName())
                .build();
    }
}
