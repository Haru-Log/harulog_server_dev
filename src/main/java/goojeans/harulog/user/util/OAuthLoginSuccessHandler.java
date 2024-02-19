package goojeans.harulog.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import goojeans.harulog.config.RabbitMQConfig;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import goojeans.harulog.user.domain.dto.CustomOAuth2User;
import goojeans.harulog.user.domain.dto.response.LoginSuccessResponse;
import goojeans.harulog.user.domain.entity.Users;
import goojeans.harulog.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@Transactional
@RequiredArgsConstructor
public class OAuthLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RabbitMQConfig rabbitMQConfig;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.cookie.expiration}")
    private Integer COOKIE_EXPIRATION;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try{

            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            Users user = oAuth2User.getUser();

            Authentication authentication1 = jwtTokenProvider.createAuthentication(user);

            String accessToken = jwtTokenProvider.generateAccessToken(authentication1);
            response.addHeader("Authorization", accessToken);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json");

            LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse(user.getNickname(), user.getUserRole());

            try {
                String responseBody = objectMapper.writeValueAsString(Response.ok(loginSuccessResponse));
                response.getWriter().write(responseBody);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            String refreshToken = jwtTokenProvider.generateRefreshToken();

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                    .path("/")
                    .httpOnly(true)
                    .maxAge(COOKIE_EXPIRATION)
                    .secure(true)
                    .build();

            response.setHeader("Set-Cookie", cookie.toString());

            Users findUser = userRepository.findUsersByEmail(user.getEmail())
                    .orElseThrow(() -> new BusinessException(ResponseCode.USER_NOT_FOUND));

            // 로그인에 성공했을 때, 유저에게 amqp Queue 할당
            rabbitMQConfig.createQueue(findUser.getNickname());

            // User 의 Role 이 GUEST 일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
            if(user.getUserRole() == UserRole.GUEST) {
                findUser.updateUserRole(UserRole.USER);
                //TODO: redirect 추가
//            response.sendRedirect("oauth2/sign-up");
            }

        } catch (Exception e) {
            throw new BusinessException(ResponseCode.USER_UNAUTHORIZED);
        }
    }
}
