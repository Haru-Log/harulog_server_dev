package goojeans.harulog.category.domain.entity;

import goojeans.harulog.challenge.domain.entity.Challenge;
import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @NotNull
    @Column(unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<Challenge> challengeList = new ArrayList<>();

    //양방향 연관관계 편의 메서드
    public void addChallenge(Challenge challenge) {
        if (!this.categoryName.equals(challenge.getCategory().categoryName)) {
            challenge.getCategory().challengeList.remove(challenge);
        }
        this.challengeList.add(challenge);
        challenge.assignToCategory(this);
    }
}
