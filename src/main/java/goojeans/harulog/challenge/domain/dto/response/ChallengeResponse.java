package goojeans.harulog.challenge.domain.dto.response;

import goojeans.harulog.challenge.domain.entity.Challenge;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChallengeResponse {

        private Long challengeId;
        private String challengeTitle;
        private String challengeContent;
        private int challengeGoal;
        private String submission;
        private String imageUrl;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private String categoryName;
        private String chatRoomId;
        private List<ChallengeUsersResponse> challengeUserList;

        public static ChallengeResponse of(Challenge challenge, List<ChallengeUsersResponse> challengeUsers) {
            return new ChallengeResponse(
                    challenge.getChallengeId(),
                    challenge.getChallengeTitle(),
                    challenge.getChallengeContent(),
                    challenge.getChallengeGoal(),
                    challenge.getSubmission(),
                    challenge.getImageUrl(),
                    challenge.getStartDate(),
                    challenge.getEndDate(),
                    challenge.getCategory().getCategoryName(),
                    challenge.getChatroom().getId(),
                    challengeUsers);
        }
}
