package goojeans.harulog.chat.repository;


import goojeans.harulog.chat.domain.entity.Message;

import java.util.List;

public interface CustomMessageRepository {

    // 채팅방 메세지 조회 (이전 메세지)
    List<Message> findBeforeMessagesWithPagination(String roomId, Long lastMessageId, int limit);

    // 채팅방 메세지 조회 (이후 메세지)
    List<Message> findAfterMessagesWithPagination(String roomId, Long lastMessageId, int limit);

    // 채팅방 메세지 조회 (마지막 메세지 포함해서 이후 메세지)
    List<Message> findAfterMessagesWithPaginationIncludeLastMessage(String roomId, Long lastMessageId, int limit);
}
