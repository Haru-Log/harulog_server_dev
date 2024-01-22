package goojeans.harulog.challenge.repository;

import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.challenge.domain.entity.ChallengeUserPK;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeUserRepository extends JpaRepository<ChallengeUser, ChallengeUserPK> {

}
