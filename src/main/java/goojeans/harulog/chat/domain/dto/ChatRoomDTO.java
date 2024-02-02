package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.util.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomDTO {
    private String roomId;
    private ChatRoomType roomType;
    private String challengeName;
    private String challengeImageUrl;
    private List<ChatUserDTO> users;
    private LocalDateTime updatedAt;

    public static ChatRoomDTO of(ChatRoom chatRoom) {
        List<ChatUserDTO> userDTOs = chatRoom.getUsers().stream()
                .map(ChatUserDTO::of)
                .toList();

        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getType(),
                chatRoom.getName(),
                chatRoom.getImageUrl(),
                userDTOs,
                chatRoom.getUpdatedAt()
        );
    }
}
