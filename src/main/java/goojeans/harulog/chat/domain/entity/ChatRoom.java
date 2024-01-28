package goojeans.harulog.chat.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
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

    // 채팅방 생성 - 정적 팩토리 메서드
    public static ChatRoom create(String name) {
        return ChatRoom.builder()
                .id(java.util.UUID.randomUUID().toString())
                .name(name)
                .build();
    }
}
