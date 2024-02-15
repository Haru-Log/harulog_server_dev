package goojeans.harulog.chat.repository;

import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.chat.domain.entity.ChatRoomUserId;
import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserId> {

    Optional<ChatRoomUser> findByChatRoomIdAndUserId(String chatRoomId, Long userId);

    Optional<ChatRoomUser> findByChatRoomAndUser(ChatRoom chatRoom, Users user);

    List<ChatRoomUser> findByChatRoomId(String chatRoomId);

    // 유저 ID 기반으로 ChatRoomUser 리스트 조회
    @EntityGraph(attributePaths = {"chatRoom", "user"})
    @Query("SELECT cru FROM ChatRoomUser cru WHERE cru.user.id = :userId AND cru.activeStatus = 'ACTIVE' ORDER BY cru.chatRoom.updatedAt DESC")
    List<ChatRoomUser> findByUserId(@Param("userId") Long userId);

    // 채팅방 ID를 기반으로 참여하고 있는 유저 목록 조회
    @Query("SELECT u FROM ChatRoomUser cru JOIN FETCH Users u ON cru.user = u WHERE cru.activeStatus = 'ACTIVE' AND cru.chatRoom.id = :roomId")
    List<Users> findUserByChatroomId(@Param("roomId") String roomId);
}
