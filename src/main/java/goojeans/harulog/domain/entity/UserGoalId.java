package goojeans.harulog.domain.entity;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
public class UserGoalId implements Serializable {

    private Long userId;
    private Long categoryId;

}
