package goojeans.harulog.chat.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import goojeans.harulog.chat.domain.entity.Message;
import goojeans.harulog.chat.domain.entity.QMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CustomMessageRepositoryImpl implements CustomMessageRepository{

    private final JPAQueryFactory queryFactory;

    // 채팅방 메세지 조회 (이전 메세지) : 내림차순 (최신~과거)
    @Override
    public List<Message> findBeforeMessagesWithPagination(String roomId, Long lastMessageId, int limit) {
        QMessage qMessage = QMessage.message;
        return queryFactory.selectFrom(qMessage)    // selectFrom 메소드는 조회할 엔티티를 지정합니다.
                .where(qMessage.chatRoom.id.eq(roomId), // where 조건을 사용하여 쿼리의 조건을 지정합니다. 채팅방의 ID가 ==(equal) roomId와 같은 메시지를 조회합니다.
                        qMessage.id.lt(lastMessageId)) // && 메시지의 ID가 주어진 lastMessageId보다 작은 경우만 조회합니다. (커서 기반 페이징) (lt: less than)
                .orderBy(qMessage.id.desc())    // 조회된 메시지를 메시지 ID의 내림차순으로 정렬 (최신 메시지가 먼저 조회되도록)
                .limit(limit)   // limit 메소드를 사용하여 조회할 메시지의 최대 개수를 제한
                .fetch();       // fetch 메소드를 호출하여 쿼리를 실행하고 결과를 List<Message> 형태로 반환합니다.
    }

    // 채팅방 메세지 조회 (이후 메세지) : 오름차순 (과거~최신)
    @Override
    public List<Message> findAfterMessagesWithPagination(String roomId, Long lastMessageId, int limit) {
        QMessage qMessage = QMessage.message;
        return queryFactory.selectFrom(qMessage)
                .where(qMessage.chatRoom.id.eq(roomId),
                        qMessage.id.gt(lastMessageId)) // 메시지의 ID가 주어진 lastMessageId보다 큰 경우만 조회합니다. (gt: greater than)
                .orderBy(qMessage.id.asc()) // 조회된 메시지를 메시지 ID의 오름차순으로 정렬 (과거 메시지가 먼저 조회되도록)
                .limit(limit)
                .fetch();
    }

    // 채팅방 메세지 조회 (마지막 메세지 포함해서 이후 메세지) : 오름차순 (과거~최신)
    @Override
    public List<Message> findAfterMessagesWithPaginationIncludeLastMessage(String roomId, Long lastMessageId, int limit) {
        QMessage qMessage = QMessage.message;
        return queryFactory.selectFrom(qMessage)
                .where(qMessage.chatRoom.id.eq(roomId),
                        qMessage.id.goe(lastMessageId)) // 메시지의 ID가 주어진 lastMessageId보다 크거나 같은 경우만 조회합니다. (goe: greater than or equal)
                .orderBy(qMessage.id.asc())
                .limit(limit)
                .fetch();
    }
}
