package goojeans.harulog.chat.domain.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class AddUserRequest {
    private List<String> userNicknames;
}
