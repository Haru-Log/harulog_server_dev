package goojeans.harulog.chat.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.chat.util.MessageType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    @NotNull
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private Users sender; // 보낸 사람

    @Enumerated(EnumType.STRING)
    @NotNull
    @Builder.Default
    private MessageType type = MessageType.TALK;

    @NotNull
    private String content;

    public static Message create(ChatRoom chatRoom, Users sender, String content){
        return Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .build();
    }
    public static Message create(ChatRoom chatRoom, Users sender, MessageType type, String content) {
        return Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .type(type)
                .content(content)
                .build();
    }
}