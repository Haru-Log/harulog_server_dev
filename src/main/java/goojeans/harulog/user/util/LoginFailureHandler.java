package goojeans.harulog.user.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
public class LoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

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

        log.info("로그인에 실패했습니다. 메시지 : {}", exception.getMessage());
    }
}
