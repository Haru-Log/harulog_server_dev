package goojeans.harulog.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
@Component
public class OAuthLoginFailureHandler implements AuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            String responseBody = objectMapper.writeValueAsString(Response.fail(ResponseCode.USER_LOGIN_FAIL));
            response.getWriter().write(responseBody);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("소셜 로그인에 실패했습니다. 에러 메시지 : {}", exception.getMessage());
    }
}
