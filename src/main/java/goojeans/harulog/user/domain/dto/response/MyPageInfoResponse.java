package goojeans.harulog.user.domain.dto.response;

import goojeans.harulog.user.domain.entity.Users;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyPageInfoResponse {

    private String imageUrl;
    private String userName;
    private String nickname;
    private String introduction;
    private Integer followerCount;
    private Integer followingCount;

    public static MyPageInfoResponse entityToResponse(Users user) {

        return MyPageInfoResponse.builder()
                .imageUrl(user.getImageUrl())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .followerCount(user.getFollowers().size())
                .followingCount(user.getFollowings().size())
                .build();
    }

}
