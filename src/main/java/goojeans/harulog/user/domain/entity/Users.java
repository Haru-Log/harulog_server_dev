package goojeans.harulog.user.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import goojeans.harulog.challenge.domain.entity.ChallengeUser;
import goojeans.harulog.chat.domain.entity.ChatRoomUser;
import goojeans.harulog.domain.entity.BaseEntity;
import goojeans.harulog.user.util.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
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
                )
        }
)
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

    @OneToMany(mappedBy = "following", fetch = FetchType.LAZY)
    private List<Follow> followings;

    @OneToMany(mappedBy = "follower", fetch = FetchType.LAZY)
    private List<Follow> followers;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<ChallengeUser> challengeUsers = new HashSet<>();

    // [채팅방 목록]을 위한 양방향 매핑
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChatRoomUser> chatRoomUsers;
}