package goojeans.harulog.admin.dto.response;

import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserInfos {

    private Long id;
    private String nickname;
    private String imageUrl;
    private String userName;

    public static UserInfos from(Users users) {
        return UserInfos.builder()
                .id(users.getId())
                .imageUrl(users.getImageUrl())
                .userName(users.getUserName())
                .nickname(users.getNickname())
                .build();
    }

}
