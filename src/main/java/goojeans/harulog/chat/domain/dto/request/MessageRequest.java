package goojeans.harulog.chat.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import goojeans.harulog.chat.util.MessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 메세지 전송 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    @JsonProperty("sender")
    private String sender; // 유저 닉네임

    @JsonProperty("messageType")
    private MessageType messageType; // 메세지 타입 (IMAGE, TALK)

    @JsonProperty("content")
    private String content; // 메세지 내용
}
