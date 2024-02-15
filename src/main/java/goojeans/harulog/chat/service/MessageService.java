package goojeans.harulog.chat.service;

import goojeans.harulog.chat.domain.dto.MessageDTO;
import goojeans.harulog.chat.domain.dto.MessageListDTO;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.domain.dto.Response;

import java.util.List;

public interface MessageService {

    // 메세지 id로부터 이전 메세지 30개씩 조회
    List<Message> getMessagesBefore(ChatRoom chatroom, Long lastReadMessageId);

    // 메세지 id로부터 이후 메세지 30개씩 조회
    List<Message> getMessagesAfter(ChatRoom chatroom, Long lastReadMessageId);

    // 채팅방 메세지 조회 : 스크롤 올릴 때
    Response<MessageListDTO> getMessagesBeforeResponse(String roomId, Long lastReadMessageId);

    // 채팅방 메세지 조회 : 스크롤 내릴 때
    Response<MessageListDTO> getMessagesAfterResponse(String roomId, Long lastReadMessageId);

    // 스크롤 up/down 시 메세지 더이상 없는 경우 Exception 처리
    void checkMessageList(List<Message> messages);

    // 채팅방 들어가기 + 마지막으로 읽은 메세지부터 30개씩 조회
    Response<MessageListDTO> roomIn(String roomId, String userNickname);

    // 채팅방 나가기
    Response<Void> roomOut(String roomId, String userNickname);

    // 채팅 메세지 전송
    MessageDTO sendMessage(String roomId, String userNickname, String content);
}