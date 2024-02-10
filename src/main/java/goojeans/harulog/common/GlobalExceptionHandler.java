package goojeans.harulog.common;

import goojeans.harulog.domain.BusinessException;
import goojeans.harulog.domain.ResponseCode;
import goojeans.harulog.domain.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Response<?>> handleValidateException(MethodArgumentNotValidException e) {
        log.error("validate fail = {}", e.getMessage());
        return new ResponseEntity<>(Response.fail(ResponseCode.VALIDATION_FAIL), ResponseCode.VALIDATION_FAIL.getHttpStatus());
    }

    // WebSocket 연결 실패
    @ExceptionHandler(ConnectException.class)
    public ResponseEntity<Response<?>> handleConnectException(ConnectException e) {
        log.error("connect fail = {} : {}", e.getMessage(), "WebSocket 연결을 사용하려면 WebSocketConfig와 application.yaml 설정을 확인헤주세요.");

        return new ResponseEntity<>(Response.fail(ResponseCode.CONNECT_FAIL), ResponseCode.CONNECT_FAIL.getHttpStatus());
    }
}
