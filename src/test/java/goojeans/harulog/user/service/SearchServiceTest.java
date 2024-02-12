package goojeans.harulog.user.service;

import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.response.SearchResponse;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SecurityUtils;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Spy
    @InjectMocks
    private SearchServiceImpl service;

    @Mock
    private UserRepository userRepository;
    @Mock
    private SecurityUtils securityUtils;

    private String testString1 = "test1";
    private String testString2 = "test2";
    private String testString3 = "test3";
    private Long testId1 = 1L;
    private Long testId2 = 2L;
    private Long testId3 = 3L;

    private Users user1 = Users.builder()
            .id(testId1)
            .email(testString1)
            .userName(testString1)
            .nickname(testString1)
            .password(testString1)
            .userRole(UserRole.USER)
            .socialType(SocialType.HARU)
            .build();
    private Users user2 = Users.builder()
            .id(testId2)
            .email(testString2)
            .userName(testString2)
            .nickname(testString2)
            .password(testString2)
            .userRole(UserRole.USER)
            .socialType(SocialType.HARU)
            .build();
    private Users user3 = Users.builder()
            .id(testId3)
            .email(testString3)
            .userName(testString3)
            .nickname(testString3)
            .password(testString3)
            .userRole(UserRole.USER)
            .socialType(SocialType.HARU)
            .build();

    @Test
    @DisplayName("전체 리스트에서 찾기")
    void searchOnAll() {
        //Given
        String content = "test";
        Page<Users> page = new PageImpl<>(List.of(user1, user2, user3));

        doReturn(page).when(userRepository).findByNicknameStartingWith(eq(content), any(Pageable.class));

        //When
        Response<SearchResponse> response = service.searchOnAll(content, 0);

        //Then
        assertThat(response.getData().getContents()).hasSize(3);

    }

    @Test
    @DisplayName("팔로워 중에 찾기")
    void searchOnFollower() {
        //Given
        String content = "test";
        Page<Users> page = new PageImpl<>(List.of(user2, user3));
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId1)
                .email(testString1)
                .username(testString1)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("user")))
                .password(testString1)
                .nickname(testString1)
                .role(UserRole.USER)
                .build();

        doReturn(page).when(userRepository).findUserOnFollowers(eq(testId1), eq(content), any(Pageable.class));
        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();

        //When
        Response<SearchResponse> response = service.searchOnFollowers(content, 0);

        //Then
        assertThat(response.getData().getContents()).hasSize(2);
        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(userRepository, times(1)).findUserOnFollowers(eq(testId1), eq(content), any(Pageable.class));

    }

    @Test
    @DisplayName("팔로잉 중에 찾기")
    void searchOnFollowing() {
        //Given
        String content = "test";
        Page<Users> page = new PageImpl<>(List.of(user2, user3));
        JwtUserDetail jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .id(testId1)
                .email(testString1)
                .username(testString1)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("user")))
                .password(testString1)
                .nickname(testString1)
                .role(UserRole.USER)
                .build();

        doReturn(page).when(userRepository).findUserOnFollowings(eq(testId1), eq(content), any(Pageable.class));
        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();

        //When
        Response<SearchResponse> response = service.searchOnFollowings(content, 0);

        //Then
        assertThat(response.getData().getContents()).hasSize(2);
        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(userRepository, times(1)).findUserOnFollowings(eq(testId1), eq(content), any(Pageable.class));

    }

}
