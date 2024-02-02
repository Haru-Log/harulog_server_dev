package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.util.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomDTO {
    private String roomId;
    private String roomName;
    private ChatRoomType roomType;
    private String imageUrl;
    private LocalDateTime updatedAt;

    public static ChatRoomDTO of(ChatRoom chatRoom){
        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getType(),
                chatRoom.getImageUrl(),
                chatRoom.getUpdatedAt()
        );
    }

    public static ChatRoomDTO of(ChatRoom chatRoom, String imageUrl){
        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getName(),
                chatRoom.getType(),
                imageUrl,
                chatRoom.getUpdatedAt()
        );
    }
}
