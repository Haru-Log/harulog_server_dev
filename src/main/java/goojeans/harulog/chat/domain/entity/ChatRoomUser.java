package goojeans.harulog.chat.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import goojeans.harulog.domain.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@Table(name = "ChatRoom_User")
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomUser {

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
}
