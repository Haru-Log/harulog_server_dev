package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
        return new MessageDTO(
                message.getId(),
                message.getSender().getNickname(),
                message.getSender().getImageUrl(),
                message.getType(),
                message.getContent(),
                message.getCreatedAt().toString()
        );
    }
}
