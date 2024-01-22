package goojeans.harulog.challenge.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EqualsAndHashCode
public class ChallengeUserPK implements Serializable {

    private Long challengeId;

    private Long userId;
}
