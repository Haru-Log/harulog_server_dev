package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageDTO {

    private Long messageId;
    private String senderName;
    private String imageUrl;
    private MessageType type;
    private String content;
    private String createdAt;

    public static MessageDTO of(Message message) {
        String time = message.getCreatedAt() != null ? message.getCreatedAt().toString() : LocalDateTime.now().toString();

        return new MessageDTO(
                message.getId(),
                message.getSender().getNickname(),
                message.getSender().getImageUrl(),
                message.getType(),
                message.getContent(),
                time
        );
    }
}
