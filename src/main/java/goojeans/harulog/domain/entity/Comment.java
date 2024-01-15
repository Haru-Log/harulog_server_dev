package goojeans.harulog.domain.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.EAGER)
    private Post post;

    @Column(length = 100, nullable = false)
    private String content;
}
