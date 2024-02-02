package goojeans.harulog.common;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    //BusinessException 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Response<?>> handleBusinessException(BusinessException e) {

        //TODO: 로그 레벨 변경
        log.info("Exception code = {}, message = {}", e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        return new ResponseEntity<>(Response.fail(e.getErrorCode()), e.getErrorCode().getHttpStatus());
    }
}
