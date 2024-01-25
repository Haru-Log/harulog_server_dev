package goojeans.harulog.user.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.category.domain.entity.Category;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE user_goal SET active_status = 'DELETED' WHERE (user_id = ? AND category_id = ?)")
@SQLRestriction("active_status <> 'DELETED'")
public class UserGoal extends BaseEntity {

    @EmbeddedId
    private UserGoalId userGoalId;

    @MapsId("userId")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @MapsId("categoryId")
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String goal;

    @Builder
    public static UserGoal of(Users user, Category category, String goal) {
        UserGoal userGoal = new UserGoal();
        userGoal.userGoalId = new UserGoalId(user.getId(), category.getCategoryId());
        userGoal.user = user;
        userGoal.category = category;
        userGoal.goal = goal;
        return userGoal;
    }

}
