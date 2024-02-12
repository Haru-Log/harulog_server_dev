package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.domain.dto.Response;

public interface MessageService {

    // 채팅방 들어가기
    Response<MessageListDTO> roomIn(String roomId, String userNickname);

    // 채팅방 나가기
    Response<Void> roomOut(String roomId, String userNickname);

    // 채팅 메세지 전송
    MessageDTO sendMessage(String roomId, String userNickname, String content);
}