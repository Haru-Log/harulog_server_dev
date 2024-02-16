package goojeans.harulog.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatroom_user")
@SQLDelete(sql = "UPDATE chatroom_user SET active_status= 'DELETED' WHERE (chatroom_id = ? AND user_id = ?)")
@SQLRestriction("active_status <> 'DELETED'")
public class ChatRoomUser extends BaseEntity {

    @EmbeddedId
    @Column(name = "chatroom_user_id")
    private ChatRoomUserId id;

    @MapsId("chatRoomId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    @NotNull
    private ChatRoom chatRoom;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference // 순환 참조 방지
    @NotNull
    private Users user;

    @Builder.Default
    @Setter
    @NotNull
    private Long lastReadMessageId = 0L; // 마지막으로 읽은 메세지 ID

    @Builder.Default
    @Setter
    @NotNull
    private Integer unreadMessageCount = 0; // 읽지 않은 메세지 개수

    // 정적 팩토리 메서드
    public static ChatRoomUser create(ChatRoom chatRoom, Users user) {
        ChatRoomUserId chatRoomUserId = new ChatRoomUserId(chatRoom.getId(), user.getId());

        return ChatRoomUser.builder()
                .id(chatRoomUserId)
                .chatRoom(chatRoom)
                .user(user)
                .build();
    }
}
