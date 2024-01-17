package goojeans.harulog.domain.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import goojeans.harulog.util.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity{

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

    //TODO: 순환 참조 일어나지 않는지 확인 필요
    @OneToMany(mappedBy = "following")
    private List<Follow> followings;

    @OneToMany(mappedBy = "follower")
    private List<Follow> followers;

    // [채팅방 목록]을 위한 양방향 매핑
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ChatRoomUser> chatRoomUsers;
}
