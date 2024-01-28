package goojeans.harulog.chat.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.io.Serializable;

/**
 * ChatRoomUser에 대한 복합키 클래스
 */
@Embeddable
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomUserId implements Serializable {

    @NotNull
    private String chatRoomId;

    @NotNull
    private Long userId;
}
