package goojeans.harulog.user.domain.entity;

import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.post.domain.entity.Post;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import java.util.HashSet;
import java.util.Set;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "Email",
                        columnNames = {"email"}
                ),
                @UniqueConstraint(
                        name = "Nickname",
                        columnNames = {"nickname"}
                )

        }
)
@SQLDelete(sql = "UPDATE users SET active_status = 'DELETED' WHERE user_id = ?")
@SQLRestriction("active_status <> 'DELETED'")
public class Users extends BaseEntity {

    @Id @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String userName;

    @NotNull
    private String nickname;

    @NotNull
    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String contactNumber;

    private String imageUrl;

    private UserRole userRole;

    private String refreshToken;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<ChallengeUser> challengeUsers = new HashSet<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private Set<Post> posts = new HashSet<>();

    // 연관 관계 편의 메서드
    public void addChallengeUser(ChallengeUser challengeUser) {
        this.challengeUsers.add(challengeUser);
        if (challengeUser.getUser() != this) {
            challengeUser.addUser(this);
        }
    }

    //연관 관계 편의 메서드
    public void addPost(Post post) {
        this.posts.add(post);
        if (post.getUser() != this) {
            post.addUser(this);
        }
    }

    public void updateRefreshToken(String updateToken) {
        this.refreshToken = updateToken;
    }
}