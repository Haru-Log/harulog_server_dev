package goojeans.harulog.user.domain.dto.response;

import goojeans.harulog.user.domain.entity.Users;
import lombok.*;

import java.time.LocalDateTime;

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
    private LocalDateTime createdAt;
    private String contactNumber;
    private String email;

    public static MyPageInfoResponse entityToResponse(Users user) {

        return MyPageInfoResponse.builder()
                .imageUrl(user.getImageUrl())
                .userName(user.getUserName())
                .nickname(user.getNickname())
                .introduction(user.getIntroduction())
                .followerCount(user.getFollowers().size())
                .followingCount(user.getFollowings().size())
                .createdAt(user.getCreatedAt())
                .email(user.getEmail())
                .contactNumber(user.getUserName())
                .build();
    }

}
