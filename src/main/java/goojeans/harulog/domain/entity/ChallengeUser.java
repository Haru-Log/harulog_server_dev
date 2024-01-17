package goojeans.harulog.domain.entity;

import goojeans.harulog.util.ChallengeRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @MapsId("challengeId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    @NotNull
    private Challenge challenge;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChallengeRole role;
}
