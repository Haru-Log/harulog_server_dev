package goojeans.harulog.challenge.domain.entity;

import goojeans.harulog.category.domain.entity.Category;
import goojeans.harulog.chat.domain.entity.ChatRoom;
import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;

@Entity
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
    private String challengeGoal;

    @NotNull
    private String submission;

    @NotNull
    private Timestamp startDate;

    @NotNull
    private Timestamp endDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    @NotNull
    private ChatRoom chatroom;

    @OneToMany(mappedBy = "challenge")
    private List<ChallengeUser> challengeUserList;
}
