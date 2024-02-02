package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface MessageService {

    // 채팅방 메세지 조회
    public Response<List<MessageDTO>> getMessages(String roomId, String userNickname);

    // 채팅방 구독 여부 확인
    public boolean existSubscribe(String roomId, String userNickname);

    // 클라이언트 채팅방 입장
    public MessageDTO subscribe(String roomId, String userNickname);

    // 메세지 전송
    public MessageDTO send(String roomId, String userNickname, String content);

    // 클라이언트 채팅방 구독 취소
    public MessageDTO unsubscribe(String roomId, String userNickname);
}
