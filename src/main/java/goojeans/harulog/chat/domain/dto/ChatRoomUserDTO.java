package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.user.domain.entity.Users;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomUserDTO {
    private String chatroomId;
    private Long userId;

    public static ChatRoomUserDTO of(ChatRoom chatRoom, Users user){
        return new ChatRoomUserDTO(
                chatRoom.getId(),
                user.getId()
        );
    }
}
