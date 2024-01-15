package goojeans.harulog.domain.entity;

import jakarta.persistence.*;
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

    private String challengeTitle;

    private String challengeContent;

    private String challengeGoal;

    private String submission;

    private Timestamp startDate;

    private Timestamp endDate;

    //TODO: category 엔티티 매핑
//    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "category_id")
//    private Category categoryId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatroom;

    @OneToMany(mappedBy = "challengeUserPK")
    private List<ChallengeUser> challengeUserList;
}
