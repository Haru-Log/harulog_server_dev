package goojeans.harulog.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "challenge_user")
public class ChallengeUser extends BaseEntity{

    @EmbeddedId
    @Column(name = "challenge_user_PK")
    private ChallengeUserPK challengeUserPK;

    @Enumerated(EnumType.STRING)
    private ChallengeRole role;
}
