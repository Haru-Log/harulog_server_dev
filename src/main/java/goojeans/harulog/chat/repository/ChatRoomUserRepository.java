package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUserId;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    public Optional<ChatRoomUser> findByChatRoomIdAndUserId(String chatRoomId, Long userId);

    // 유저 ID를 기반으로 참여하고 있는 채팅방 목록 조회
    @Query("SELECT c FROM ChatRoomUser cu JOIN FETCH ChatRoom c on cu.chatRoom = c WHERE cu.user.id = :userId")
    public List<ChatRoom> findChatRoomsByUserId(@Param("userId") Long userId);

    // 채팅방 ID를 기반으로 참여하고 있는 유저 목록 조회
    @Query("SELECT u FROM ChatRoomUser cu JOIN FETCH Users u ON cu.user = u WHERE cu.chatRoom.id = :roomId")
    public List<Users> findUserByChatroomId(@Param("roomId") String roomId);
}
