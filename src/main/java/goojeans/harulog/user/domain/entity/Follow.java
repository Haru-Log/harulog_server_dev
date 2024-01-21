package goojeans.harulog.user.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
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
@SQLDelete(sql = "UPDATE follow SET active_status = 'DELETED' WHERE (follwer_id = ? AND follwing_id = ?)")
@SQLRestriction("active_status <> 'DELETED'")
public class Follow extends BaseEntity {

    @EmbeddedId
    private FollowId id;

    @MapsId("followerId")
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private Users follower;

    @MapsId("followingId")
    @ManyToOne
    @JoinColumn(name = "following_id")
    private Users following;

    @Builder
    public static Follow create(Users follower, Users following) {
        Follow follow = new Follow();
        follow.id = new FollowId(follower.getId(), following.getId());
        follow.follower = follower;
        follow.following = following;
        return follow;
    }

}
