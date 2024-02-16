package goojeans.harulog.chat.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.util.ChatRoomType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatRoomDTO {
    private String roomId;
    private ChatRoomType roomType;
    private String challengeName;
    private String challengeImageUrl;
    private Integer unreadCount;
    private LocalDateTime updatedAt;
    private List<ChatUserDTO> users;

    public static ChatRoomDTO of(ChatRoomUser cru){
        ChatRoom chatRoom = cru.getChatRoom();

        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getType(),
                chatRoom.getName(),
                chatRoom.getImageUrl(),
                cru.getUnreadMessageCount(),
                chatRoom.getUpdatedAt(),
                chatRoom.getUsers().stream()
                        .map(ChatUserDTO::of)
                        .toList()
        );
    }

    public static ChatRoomDTO of(ChatRoom chatRoom) {

        List<ChatUserDTO> userDTOs = chatRoom.getUsers().stream()
                .map(ChatUserDTO::of)
                .toList();

        return new ChatRoomDTO(
                chatRoom.getId(),
                chatRoom.getType(),
                chatRoom.getName(),
                chatRoom.getImageUrl(),
                null,
                chatRoom.getUpdatedAt(),
                userDTOs
        );
    }
}
