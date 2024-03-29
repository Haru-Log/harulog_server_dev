package goojeans.harulog.challenge.repository;

import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.domain.entity.ChallengeUserPK;
import goojeans.harulog.challenge.util.ChallengeRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, ChallengeUserPK> {

    @Query("select cu from ChallengeUser cu where cu.challengeUserPK.userId =:userId and cu.challengeUserPK.challengeId=:challengeId")
    Optional<ChallengeUser> findChallengeUserByUserAndChallenge(@Param("userId") Long userId, @Param("challengeId") Long challengeId);

    @Query("select cu from ChallengeUser cu where cu.challengeUserPK.challengeId =:challengeId and cu.role =:role")
    ChallengeUser findChallengeUserByRole(@Param("challengeId") Long challengeId,@Param("role") ChallengeRole role);

    //softdelete된 ChallengeUser 조회
    @Query(value = "SELECT * FROM challenge_user WHERE active_status = 'DELETED' AND user_id = :userId AND challenge_id = :challengeId", nativeQuery = true)
    Optional<ChallengeUser> findDeletedChallengeUser(@Param("userId") Long userId, @Param("challengeId") Long challengeId);
}
