package goojeans.harulog.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.response.LoginSuccessResponse;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import goojeans.harulog.user.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final LoginService loginService;
    private final RabbitMQConfig rabbitMQConfig;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.cookie.expiration}")
    private Integer COOKIE_EXPIRATION;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) {
        String email = extractUsername(authentication);

        Users user = userRepository.findUsersByEmail(email).stream()
                .findAny()
                .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

        Authentication authenticate = jwtTokenProvider.createAuthentication(user);

        String accessToken = jwtTokenProvider.generateAccessToken(authenticate);
        String refreshToken = jwtTokenProvider.generateRefreshToken();

        LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse(user.getNickname(), user.getUserRole());

        //TODO: 로그인 시 DB 접근 2 select
        user.updateRefreshToken(refreshToken);
        loginService.updateRefreshToken(user);

        response.setHeader("Authorization", accessToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try {
            String responseBody = objectMapper.writeValueAsString(Response.ok(loginSuccessResponse));
            response.getWriter().write(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .httpOnly(true)
                .maxAge(COOKIE_EXPIRATION)
                .secure(true)
                .build();

        response.setHeader("Set-Cookie", cookie.toString());

        SecurityContextHolder.getContext().setAuthentication(authenticate);

        // 로그인에 성공했을 때, 유저에게 amqp Queue 할당
        rabbitMQConfig.createQueue(user.getNickname());

        log.info("로그인에 성공하였습니다. 이메일 : {}", email);
        log.info("AccessToken 만료 기간 : {}분", accessTokenExpiration / 1000 / 60);
    }

    private String extractUsername(Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return userDetails.getUsername();
    }
}

