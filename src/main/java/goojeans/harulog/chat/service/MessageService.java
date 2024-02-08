package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.domain.dto.Response;

public interface MessageService {

    // 채팅방 입장 및 메세지 조회
    Response<MessageListDTO> getMessages(String roomId, String userNickname);

    // 채팅방 입장 메세지
    MessageDTO entry(String roomId, String userNickname);

    // 메세지 전송
    MessageDTO send(String roomId, String userNickname, String content);

    // 채팅방 퇴장 메세지
    MessageDTO exit(String roomId, String userNickname);
}
