package goojeans.harulog.user.service;

import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.request.FollowRequest;
import goojeans.harulog.user.domain.dto.response.FollowInfo;
import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.FollowId;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.FollowRepository;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class FollowServiceTest {

    @Spy
    @InjectMocks
    FollowServiceImpl followService;

    @Mock
    UserRepository userRepository;

    @Mock
    FollowRepository followRepository;
    @Mock
    SecurityUtils securityUtils;

    private String testString1 = "test1";
    private String testString2 = "test2";
    private String testString3 = "test3";
    private Long testId1 = 1L;
    private Long testId2 = 2L;
    private Long testId3 = 3L;
    private Users user1;
    private Users user2;
    private Users user3;
    private Follow follow1;
    private Follow follow2;
    private Follow follow3;


    @BeforeEach
    void beforeEach() {
        user1 = Users.builder()
                .id(testId1)
                .email(testString1)
                .nickname(testString1)
                .userName(testString1)
                .password(testString1)
                .socialType(SocialType.HARU)
                .userRole(UserRole.USER)
                .build();
        user2 = Users.builder()
                .id(testId2)
                .email(testString2)
                .nickname(testString2)
                .userName(testString2)
                .password(testString2)
                .socialType(SocialType.HARU)
                .userRole(UserRole.USER)
                .build();
        user3 = Users.builder()
                .id(testId3)
                .email(testString3)
                .nickname(testString3)
                .userName(testString3)
                .password(testString3)
                .socialType(SocialType.HARU)
                .userRole(UserRole.USER)
                .build();

        follow1 = Follow.builder()
                .follower(user1)
                .following(user2)
                .build();
        follow2 = Follow.builder()
                .follower(user2)
                .following(user3)
                .build();
        follow3 = Follow.builder()
                .follower(user1)
                .following(user3)
                .build();
        user1.addFollowing(follow1);
        user1.addFollowing(follow3);
        user2.addFollowing(follow2);
        user2.addFollower(follow1);
        user3.addFollower(follow1);
        user3.addFollower(follow3);

        //user1 -> user2, user2 -> user3, user1 -> user3
    }


    @Test
    @DisplayName("팔로워 리스트 보여주기")
    void getFollowerList() {
        //Given
        doReturn(Optional.of(user3)).when(userRepository).findByNickname(testString3);
        doReturn(Optional.of(user2)).when(userRepository).findByNickname(testString2);

        //When
        Response<List<FollowInfo>> followerList1 = followService.getFollowerList(testString3);
        Response<List<FollowInfo>> followerList2 = followService.getFollowerList(testString2);

        //Then
        assertThat(followerList1.getData().size()).isEqualTo(2);
        assertThat(followerList2.getData().size()).isEqualTo(1);

        verify(userRepository, times(1)).findByNickname(testString3);
        verify(userRepository, times(1)).findByNickname(testString2);

    }

    @Test
    @DisplayName("내 팔로워 리스트")
    void myFollowerList() {
        //Given
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId3)
                .nickname(testString3)
                .username(testString3)
                .password(testString3)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(List.of(follow2, follow3)).when(followRepository).findFollowerByUserId(testId3);

        //When
        Response<List<FollowInfo>> followerList = followService.getMyFollowerList();

        //Then
        assertThat(followerList.getData().size()).isEqualTo(2);

        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(followRepository, times(1)).findFollowerByUserId(testId3);

    }


    @Test
    @DisplayName("팔로잉 리스트 보여주기")
    void getFollowingList() {
        //Given
        doReturn(Optional.of(user1)).when(userRepository).findByNickname(testString1);
        doReturn(Optional.of(user3)).when(userRepository).findByNickname(testString3);

        //When
        Response<List<FollowInfo>> followingList1 = followService.getFollowingList(testString3);
        Response<List<FollowInfo>> followingList2 = followService.getFollowingList(testString1);

        //Then
        assertThat(followingList1.getData().size()).isEqualTo(0);
        assertThat(followingList2.getData().size()).isEqualTo(2);

        verify(userRepository, times(1)).findByNickname(testString3);
        verify(userRepository, times(1)).findByNickname(testString1);

    }


    @Test
    @DisplayName("내 팔로잉 리스트")
    void myFollowingList() {
        //Given
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId1)
                .nickname(testString1)
                .username(testString1)
                .password(testString1)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(List.of(follow1, follow3)).when(followRepository).findFollowingByUserId(testId1);

        //When
        Response<List<FollowInfo>> followingList = followService.getMyFollowingList();

        //Then
        assertThat(followingList.getData().size()).isEqualTo(2);

        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(followRepository, times(1)).findFollowingByUserId(testId1);

    }

    @Test
    @DisplayName("팔로우 하기")
    void follow() {
        //Given
        FollowRequest followRequest = new FollowRequest(testString1);

        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId3)
                .nickname(testString3)
                .username(testString3)
                .password(testString3)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(Optional.of(user3)).when(userRepository).findByNickname(testString3);
        doReturn(Optional.of(user1)).when(userRepository).findByNickname(testString1);

        //When
        Response<Void> follow = followService.follow(followRequest);

        //Then
        assertThat(follow.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());

        verify(userRepository, times(1)).findByNickname(testString1);
        verify(userRepository, times(1)).findByNickname(testString3);

    }

    @Test
    @DisplayName("팔로우 취소")
    void followingDelete() {
        //Given
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId1)
                .nickname(testString1)
                .username(testString1)
                .password(testString1)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
//        doReturn(Optional.of(user1)).when(userRepository).findUsersByNickname(testString1);
        doReturn(Optional.of(user2)).when(userRepository).findUsersByNickname(testString2);
        doNothing().when(followRepository).deleteFollow(new FollowId(user1.getId(), user2.getId()));

        //When
        Response<Void> response = followService.followingDelete(testString2);

        //Then
        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());

        verify(followRepository, times(1)).deleteFollow(new FollowId(user1.getId(), user2.getId()));
//        verify(userRepository, times(1)).findUsersByNickname(testString1);
        verify(userRepository, times(1)).findUsersByNickname(testString2);

    }

    @Test
    @DisplayName("팔로워 삭제")
    void followerDelete() {
        //Given
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId1)
                .nickname(testString1)
                .username(testString1)
                .password(testString1)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
//        doReturn(Optional.of(user1)).when(userRepository).findUsersByNickname(testString1);
        doReturn(Optional.of(user2)).when(userRepository).findUsersByNickname(testString2);
        doNothing().when(followRepository).deleteFollow(new FollowId(user1.getId(), user2.getId()));

        //When
        Response<Void> response = followService.followingDelete(testString2);

        //Then
        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());

        verify(followRepository, times(1)).deleteFollow(new FollowId(user1.getId(), user2.getId()));
//        verify(userRepository, times(1)).findUsersByNickname(testString1);
        verify(userRepository, times(1)).findUsersByNickname(testString2);

    }

}
