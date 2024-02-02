package goojeans.harulog.chat.domain.entity;

import goojeans.harulog.chat.util.ChatRoomType;
import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "chatroom_type")
    @NotNull
    @Setter
    private ChatRoomType type;

    @Column(name = "chatroom_name")
    private String name;

    // todo: 챌린지 이미지 update
    @Column(name = "chatroom_image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Column(name = "chatroom_users")
    @Builder.Default
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

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

    // 채팅방 참여 유저 반환
    public List<Users> getUsers() {
        return chatRoomUsers.stream()
                .map(ChatRoomUser::getUser)
                .toList();
    }
}
