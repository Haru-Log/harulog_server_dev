package goojeans.harulog.chat.domain.dto;

import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MessageDTO {

    private String senderName;
    private String imageUrl;
    private MessageType type;
    private String content;
    private LocalDateTime createdAt;

    public static MessageDTO of(Message message) {
        return new MessageDTO(
                message.getSender().getNickname(),
                message.getSender().getImageUrl(),
                message.getType(),
                message.getContent(),
                LocalDateTime.now()
        );
    }
}
