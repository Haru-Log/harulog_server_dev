package goojeans.harulog.challenge.domain.dto.response;

import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import jakarta.validation.constraints.NotNull;
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

        @NotNull
        private Long challengeId;
        @NotNull
        private String challengeTitle;
        @NotNull
        private String challengeContent;
        @NotNull
        private int challengeGoal;
        @NotNull
        private String submission;
        @NotNull
        private String imageUrl;
        @NotNull
        private LocalDateTime startDate;
        @NotNull
        private LocalDateTime endDate;
        @NotNull
        private String categoryName;
        @NotNull
        private String chatRoomId;
        @NotNull
        private List<ChallengeUser> challengeUserList;

        public static ChallengeResponse of(Challenge challenge) {
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
                    challenge.getChallengeUserList());
        }
}
