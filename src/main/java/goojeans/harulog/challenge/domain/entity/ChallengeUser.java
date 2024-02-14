package goojeans.harulog.challenge.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.challenge.util.ChallengeRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE challenge_user SET active_status = 'DELETED' WHERE (challenge_id = ? AND user_id = ?)")
@SQLRestriction("active_status = 'ACTIVE'")
@Table(name = "challenge_user")
public class ChallengeUser extends BaseEntity {

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
    private Users user;

    @Enumerated(EnumType.STRING)
    @NotNull
    private ChallengeRole role;

    //정적 팩토리 메서드
    public static ChallengeUser create(Users user, Challenge challenge) {
        ChallengeUserPK challengeUserPK = new ChallengeUserPK(user.getId(), challenge.getChallengeId());

        ChallengeUser challengeUser = new ChallengeUser();
        challengeUser.challengeUserPK = challengeUserPK;
        challengeUser.challenge = challenge;
        challengeUser.user = user;
        challengeUser.role = ChallengeRole.PARTICIPANT;

        return challengeUser;
    }

    public void assignToChallenge(Challenge challenge) {
        this.challenge = challenge;
    }

    public void updateRole() {
        if (this.role == ChallengeRole.LEADER) {
            this.role = ChallengeRole.PARTICIPANT;
        } else {
            this.role = ChallengeRole.LEADER;
        }
    }

    // 연관 관계 편의 메서드
    public void addUser(Users user) {
        if (this.user != user) {
            this.user = user;
        }
        if (!user.getChallengeUsers().contains(this)) {
            user.addChallengeUser(this);
        }
    }
}