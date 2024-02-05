package goojeans.harulog.chat.domain.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * 채팅방에 유저 추가 요청 DTO
 */
@Getter
@AllArgsConstructor
public class AddUserRequest {
    private List<String> userNicknames;
}
