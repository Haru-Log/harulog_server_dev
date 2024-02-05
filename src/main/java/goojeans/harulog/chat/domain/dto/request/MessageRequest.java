package goojeans.harulog.chat.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 메세지 전송 요청 DTO
 */
@Getter
@AllArgsConstructor
public class MessageRequest {
    private String sender; // 유저 닉네임
    private String content; // 메세지 내용
}
