package goojeans.harulog.user.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

    public void addFollowing(Users user) {
        if (this.following != user) {
            this.following = user;
        }
        if (!user.getFollowers().contains(this)) {
            user.addFollower(this);
        }
    }

    public void addFollower(Users user) {
        if (this.follower != user) {
            this.follower = user;
        }
        if (!user.getFollowings().contains(this)) {
            user.addFollowing(this);
        }
    }

    @Builder
    public static Follow of(Users follower, Users following) {
        Follow follow = new Follow();
        follow.id = new FollowId(follower.getId(), following.getId());
        follow.follower = follower;
        follow.following = following;
        return follow;
    }

}
