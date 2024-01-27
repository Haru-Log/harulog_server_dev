package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 임시 UserDTO
 * todo: UserDTO 만들어지면 교체 예정
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
