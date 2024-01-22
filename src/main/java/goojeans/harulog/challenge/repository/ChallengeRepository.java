package goojeans.harulog.challenge.repository;

import goojeans.harulog.challenge.domain.entity.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeRepository extends JpaRepository<Challenge, Long> {

    Optional<Challenge> findByChallengeId(Long challengeId);

    @Query("select c from Challenge c where c.challengeTitle like %:challengeTitle%")
    List<Challenge> findAllByChallengeTitle(@Param("challengeTitle") String challengeTitle);

    @Query("select c from Challenge c where c.category.categoryName = :categoryName")
    List<Challenge> findAllByCategoryName(@Param("categoryName") String categoryName);

    @Query("select c from Challenge c join fetch c.challengeUserList cu where cu.challengeUserPK.userId = :userId")
    List<Challenge> findAllByUserId(@Param("userId") Long userId);
}
