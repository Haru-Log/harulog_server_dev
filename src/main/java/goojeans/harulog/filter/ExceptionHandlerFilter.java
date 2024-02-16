package goojeans.harulog.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try{
            filterChain.doFilter(request, response);
        } catch (BusinessException e){
            setBusinessErrorResponse(response, e.getErrorCode());
        } catch (RuntimeException | IOException e){
            setExceptionResponse(response, e);
        }
    }

    private void setBusinessErrorResponse(HttpServletResponse response, ResponseCode errorCode) {

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Response<Object> errorResponse = Response.fail(errorCode);

        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            log.error(e.getMessage());
        }

    }

    private void setExceptionResponse(HttpServletResponse response, Exception exception) {

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        Response<Object> errorResponse = Response.fail(ResponseCode.FILTER_FAIL);

        log.error("error occur = {}", exception.getMessage());

        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            log.error("IO Exception on setting exception = {}", e.getMessage());
        }

    }

}
