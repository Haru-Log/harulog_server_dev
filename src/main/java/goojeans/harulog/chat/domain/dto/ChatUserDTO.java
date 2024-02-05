package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 채팅방에 참여한 유저 정보를 담는 DTO
 */
@Getter
@AllArgsConstructor
public class ChatUserDTO {
    private String nickname;
    private String profileImage;

    public static ChatUserDTO of(Users user) {
        return new ChatUserDTO(
                user.getNickname(),
                user.getImageUrl()
        );
    }
}
