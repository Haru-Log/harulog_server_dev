package goojeans.harulog.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class ChatRoom extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;
}
