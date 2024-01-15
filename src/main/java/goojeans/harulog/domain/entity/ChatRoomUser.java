package goojeans.harulog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "ChatRoom_User")
public class ChatRoomUser {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_user_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;

    // todo: User Entity 생성 후, 사용자-채팅방 관계 매핑 예정.
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "user_id")
//    private User user;
//
//    public ChatRoomUser(ChatRoom chatRoom, User user) {
//        this.chatRoom = chatRoom;
//        this.user = user;
//    }
}
