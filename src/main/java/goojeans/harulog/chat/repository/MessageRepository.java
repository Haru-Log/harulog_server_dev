package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    // 채팅방 전체 메세지 조회
    // todo: 커서 방식으로 변경 예정 -> 후순위
    public List<Message> findByChatRoomId(String roomId);
}
