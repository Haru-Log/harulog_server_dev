package goojeans.harulog.user.service;

import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.request.SignUpRequest;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.util.SocialType;
import goojeans.harulog.user.util.UserRole;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Spy
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    private String testString = "test";

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

        Users testUser = Users.builder()
                .email(testString)
                .userName(testString)
                .nickname(testString)
                .password(testString)
                .userRole(UserRole.USER)
                .socialType(SocialType.HARU)
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



}
