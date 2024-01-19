package goojeans.harulog.user.domain.entity;

import goojeans.harulog.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
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
    private Users following;

    @MapsId("followingId")
    @ManyToOne
    @JoinColumn(name = "following_id")
    private Users follower;

}
