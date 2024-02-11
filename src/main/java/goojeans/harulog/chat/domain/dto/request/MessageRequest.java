package goojeans.harulog.chat.domain.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("content")
    private String content; // 메세지 내용
}
