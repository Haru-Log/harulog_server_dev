package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUserId;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    // 유저가 참여하고 있는 채팅방 조회
    public List<ChatRoomUser> findByUser(Users user);

    // 채팅방에 참여하고 있는 유저 조회
    public List<ChatRoomUser> findByChatRoom(ChatRoom chatroom);
}
