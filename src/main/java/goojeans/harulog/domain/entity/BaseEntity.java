package goojeans.harulog.domain.entity;


import goojeans.harulog.util.ActiveStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity에서 생성일, 수정일을 자동으로 관리하기 위한 추상 클래스
 */
@MappedSuperclass
public abstract class BaseEntity {
    @CreationTimestamp // Insert 시 자동으로 값을 채워줌
    private LocalDateTime createdAt;
    @UpdateTimestamp // Update 시 자동으로 값을 채워줌.
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private ActiveStatus activeStatus = ActiveStatus.ACTIVE;
}
