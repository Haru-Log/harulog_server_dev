package goojeans.harulog.domain.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Embeddable
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ChallengeUserPK implements Serializable {

    //TODO: user 엔티티 매핑
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name= "user_id", nullable = false)
//    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "challenge_id", nullable = false)
    private Challenge challenge;
}
