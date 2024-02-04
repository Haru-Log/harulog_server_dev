package goojeans.harulog.chat.domain.dto.request;

import lombok.Getter;

@Getter
public class MessageRequest {
    private String sender; // 유저 닉네임
    private String content; // 메세지 내용
}
