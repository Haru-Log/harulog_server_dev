package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatRoomDTO {
    private Long id;
    private String name;

    public static ChatRoomDTO of(ChatRoom chatRoom){
        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getName());
    }
}
