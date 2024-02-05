package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.domain.dto.Response;

public interface MessageService {

    // 채팅방 메세지 조회
    public Response<MessageListDTO> getMessages(String roomId, String userNickname);

    // 채팅방 구독 여부 확인
    public boolean existSubscribe(String roomId, String userNickname);

    // 채팅방 입장 여부 확인
    public boolean isEntered(String roomId, String userNickname);

    // 채팅방 구독
    public void subscribe(String roomId, String userNickname);

    // 채팅방 입장
    public MessageDTO enter(String roomId, String userNickname);

    // 메세지 전송
    public MessageDTO send(String roomId, String userNickname, String content);

    // 클라이언트 채팅방 구독 취소
    public MessageDTO exit(String roomId, String userNickname);
}
