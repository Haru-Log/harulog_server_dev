package goojeans.harulog.chat.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageListDTO {
    private String roomId;                                  // 채팅방 ID
    private int userCount;                                  // 채팅방에 참여하고 있는 유저 수
    private List<MessageDTO> messages;  // 채팅방 메세지 목록

    public static MessageListDTO of(String roomId, int userCount, List<MessageDTO> messages) {
        return new MessageListDTO(roomId, userCount, messages);
    }
}
