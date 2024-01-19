package goojeans.harulog.user.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class FollowId implements Serializable {

    private Long followerId;
    private Long followingId;

}
