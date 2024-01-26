package goojeans.harulog.challenge.repository;

import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.domain.entity.ChallengeUserPK;
import goojeans.harulog.user.domain.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, ChallengeUserPK> {

    @Query("select cu from ChallengeUser cu where cu.challengeUserPK.userId =:userId and cu.challengeUserPK.challengeId=:challengeId")
    Optional<ChallengeUser> findChallengeUserByUserAndChallenge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);
}
