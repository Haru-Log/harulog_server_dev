package goojeans.harulog.category.domain.entity;

import goojeans.harulog.challenge.domain.entity.Challenge;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotNull
    @Column(unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Challenge> challengeList;

    //양방향 연관관계 편의 메서드
    public void addChallenge(Challenge challenge) {
        if (!this.categoryName.equals(challenge.getCategory().categoryName)) {
            challenge.getCategory().challengeList.remove(challenge);
        }
        this.challengeList.add(challenge);
        challenge.assignToCategory(this);
    }
}
