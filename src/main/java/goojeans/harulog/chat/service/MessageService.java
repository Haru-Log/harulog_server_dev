package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface MessageService {

    // 채팅방 메세지 조회
    public Response<List<MessageDTO>> getMessages(String roomId);

    // 클라이언트 채팅방 입장
    public Response<MessageDTO> enter(String roomId, Long userId);

    // 메세지 전송
    public Response<MessageDTO> send(String roomId, Long userId, String content);

    // 클라이언트 채팅방 퇴장
    public Response<MessageDTO> exit(String roomId, Long userId);
}
