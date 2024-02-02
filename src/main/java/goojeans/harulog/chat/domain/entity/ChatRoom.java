package goojeans.harulog.chat.domain.entity;

import goojeans.harulog.chat.util.ChatRoomType;
import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE chatroom SET active_status= 'DELETED' WHERE chatroom_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
@Table(name = "chatroom")
public class ChatRoom extends BaseEntity {

    @Id
    @Column(name = "chatroom_id")
    private String id; // uuid

    @Column(name = "chatroom_name")
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type")
    @NotNull
    private ChatRoomType type;

    // todo: 챌린지 이미지 update
    @Column(name = "chatroom_image_url")
    private String imageUrl;

    // 채팅방 생성 - 정적 팩토리 메서드
    public static ChatRoom createChallenge(String name, String imageUrl) {
        return ChatRoom.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .type(ChatRoomType.CHALLENGE)
                .imageUrl(imageUrl)
                .build();
    }

    public static ChatRoom createDM() {
        return ChatRoom.builder()
                .id(java.util.UUID.randomUUID().toString())
                .type(ChatRoomType.DM)
                .build();
    }
}
