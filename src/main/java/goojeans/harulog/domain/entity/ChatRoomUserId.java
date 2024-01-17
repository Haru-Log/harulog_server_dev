package goojeans.harulog.domain.entity;

import jakarta.persistence.Column;
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
    private Long chatRoomId;

    @NotNull
    private Long userId;
}
