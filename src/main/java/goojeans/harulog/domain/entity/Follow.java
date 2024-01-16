package goojeans.harulog.domain.entity;

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
public class Follow {

    @EmbeddedId
    private FollowId id;

    @MapsId("followerId")
    @ManyToOne
    @JoinColumn(name = "follower_id")
    private User following;

    @MapsId("followingId")
    @ManyToOne
    @JoinColumn(name = "following_id")
    private User follower;

}
