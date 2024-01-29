package goojeans.harulog.challenge.domain.entity;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE challenge SET active_status = 'DELETED' WHERE challenge_id = ?")
@SQLRestriction("active_status = 'ACTIVE'")
public class Challenge extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "challenge_id")
    private Long challengeId;

    @NotNull
    private String challengeTitle;

    @NotNull
    private String challengeContent;

    @NotNull
    private int challengeGoal;

    @NotNull
    private String submission;

    private String imageUrl;

    @NotNull
    private LocalDateTime startDate;

    @NotNull
    private LocalDateTime endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "chatroom_id")
    @NotNull
    private ChatRoom chatroom;

    @OneToMany(mappedBy = "challenge", cascade = CascadeType.ALL)
    @Builder.Default
    private List<ChallengeUser> challengeUserList = new ArrayList<>();

    //연관관계 편의 메서드
    public void addChallengeUser(ChallengeUser challengeUser) {
        //ChallengeUserList를 가지고 있는 Challenge, User에 모두 ChallengeUser 추가
        this.challengeUserList.add(challengeUser);
        challengeUser.getUser().getChallengeUsers().add(challengeUser);

        challengeUser.assignToChallenge(this);
    }

    public void removeChallengeUser(ChallengeUser challengeUser) {
        //ChallengeUserList를 가지고 있는 Challenge, User에 모두 ChallengeUser 제거
        this.challengeUserList.remove(challengeUser);
        challengeUser.getUser().getChallengeUsers().remove(challengeUser);
    }

    public void updateChallengeTitle(String title) {
        this.challengeTitle = title;
    }

    public void updateChallengeContent(String content) {
        this.challengeContent = content;
    }

    public void updateChallengeGoal(int goal) {
        this.challengeGoal = goal;
    }

    public void updateSubmission(String submission) {
        this.submission = submission;
    }

    public void updateImage(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void assignToCategory(Category category) {
        this.category = category;
    }
}