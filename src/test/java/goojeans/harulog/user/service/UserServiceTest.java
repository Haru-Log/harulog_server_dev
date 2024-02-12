package goojeans.harulog.user.service;

import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.JwtUserDetail;
import goojeans.harulog.user.domain.dto.request.DeleteUserRequest;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.dto.request.UpdatePasswordRequest;
import goojeans.harulog.user.domain.dto.request.UpdateUserInfoRequest;
import goojeans.harulog.user.domain.dto.response.MyPageInfoResponse;
import goojeans.harulog.user.domain.dto.response.UserInfoEditResponse;
import goojeans.harulog.user.domain.entity.Follow;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.JwtTokenProvider;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock
    SecurityUtils securityUtils;
    @Mock
    PasswordEncoder passwordEncoder;

    private String testString = "test";
    private Long testId = 1L;
    private String encoded = "adfa";

    private JwtUserDetail jwtUserDetail;
    private Users testUser;

    @BeforeEach
    void beforeEach() {
        testUser = Users.builder()
                .id(testId)
                .email(testString)
                .userName(testString)
                .nickname(testString)
                .password(encoded)
                .userRole(UserRole.USER)
                .socialType(SocialType.HARU)
                .build();
        jwtUserDetail = JwtUserDetail.userDetailBuilder()
                .email(testString)
                .id(testId)
                .password(encoded)
                .username(testString)
                .role(UserRole.USER)
                .contactNumber(testString)
                .introduction(testString)
                .createdAt(LocalDateTime.now())
                .imageUrl(testString)
                .nickname(testString)
                .socialType(SocialType.HARU)
                .authorities(Collections.singleton(new SimpleGrantedAuthority("USER")))
                .build();
    }

    @Test
    @DisplayName("회원 가입")
    void signUp() {

        //Given
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(testString)
                .nickname(testString)
                .password(testString)
                .userName(testString)
                .build();

        doReturn(Optional.empty()).when(userRepository).findUsersByEmail(testString);
        doReturn(Optional.empty()).when(userRepository).findUsersByNickname(testString);
        doReturn(testUser).when(userRepository).save(any(Users.class));

        //When
        Response<Void> response = userService.signUp(signUpRequest);

        //Then
        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
        verify(userRepository, times(1)).findUsersByEmail(testString);
        verify(userRepository, times(1)).findUsersByNickname(testString);
        verify(userRepository, times(1)).save(any(Users.class));

    }

    @Test
    @DisplayName("정보 수정 화면에서의 유저 정보 접근")
    void getUserInfoForEdit() {
        //Given
        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();

        //When
        Response<UserInfoEditResponse> response = userService.getUserInfoForEdit();


        //Then
        assertThat(response.getData().getEmail()).isEqualTo(jwtUserDetail.getEmail());
        verify(securityUtils, times(1)).getCurrentUserInfo();

    }

    @Test
    @DisplayName("유저 정보 업데이트")
    void updateUserInfo() {
        //Given
        String updateString = "update";
        String newToken = "fdsahjfkldshjaklfds";
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken("", "", List.of(new SimpleGrantedAuthority("USER")));

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(Optional.of(testUser)).when(userRepository).findById(testId);
        doReturn(auth).when(jwtTokenProvider).createAuthentication(testUser);
        doReturn(newToken).when(jwtTokenProvider).generateAccessToken(auth);

        UpdateUserInfoRequest request = UpdateUserInfoRequest.builder()
                .nickname(updateString)
                .introduction(updateString)
                .contactNumber(updateString)
                .build();

        //When
        String response = userService.updateUserInfo(request);

        //Then
        assertThat(response).isEqualTo(newToken);
        assertThat(testUser.getNickname()).isEqualTo(updateString);
        assertThat(testUser.getIntroduction()).isEqualTo(updateString);
        assertThat(testUser.getContactNumber()).isEqualTo(updateString);

        verify(userRepository, times(1)).findById(testId);

    }

    @Test
    @DisplayName("유저 비밀번호 업데이트")
    void updatePassword() {
        //Given
        String updateString = "update";

        UpdatePasswordRequest request = UpdatePasswordRequest.builder()
                .beforePassword(testString)
                .afterPassword(updateString)
                .build();

        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(Optional.of(testUser)).when(userRepository).findById(testId);
        doReturn(updateString).when(passwordEncoder).encode(updateString);
        doReturn(true).when(passwordEncoder).matches(testString, encoded);

        //When
        Response<Void> response = userService.updatePassword(request);

        //Then
        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());
        assertThat(testUser.getPassword()).isEqualTo(updateString);

        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(userRepository, times(1)).findById(testId);
        verify(passwordEncoder, times(1)).matches(testString, encoded);

    }

    @Test
    @DisplayName("회원 탈퇴")
    void delete() {
        //Given
        doReturn(jwtUserDetail).when(securityUtils).getCurrentUserInfo();
        doReturn(Optional.of(testUser)).when(userRepository).findById(testId);
        doNothing().when(userRepository).delete(testUser);

        //When
        Response<Void> response = userService.delete(new DeleteUserRequest(testString));

        //Then
        verify(securityUtils, times(1)).getCurrentUserInfo();
        verify(userRepository, times(1)).findById(testId);
        verify(userRepository, times(1)).delete(testUser);

        assertThat(response.getCode()).isEqualTo(ResponseCode.SUCCESS.getCode());

    }

    @Test
    @DisplayName("마이페이지 유저 정보 가져오기")
    void getMyPageUserInfo() {
        //Given
        testUser.getFollowers().add(new Follow());

        doReturn(Optional.of(testUser)).when(userRepository).findByNickname(testUser.getNickname());

        //When
        Response<MyPageInfoResponse> response = userService.getMyPageUserInfo(testUser.getNickname());

        //Then
        assertThat(response.getData()).isEqualTo(MyPageInfoResponse.entityToResponse(testUser));
        verify(userRepository, times(1)).findByNickname(testUser.getNickname());

    }
}
