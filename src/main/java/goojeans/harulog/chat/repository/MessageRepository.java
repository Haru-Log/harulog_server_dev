package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>, CustomMessageRepository {

    Message findTopByChatRoomIdOrderByCreatedAtDesc(String roomId); // 채팅방의 마지막 메세지 조회

}
