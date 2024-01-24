package goojeans.harulog.domain;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * status: 원하는 status code 를 기입합니다. ex) 200, 400
 * code: 커스텀 코드
 * message: 메세지
 * httpStatus: ResponseEntity 에서 사용할 HttpsStatus 를 입력합니다. ex) 200번이면 HttpStatus.OK
 */
@Getter
public enum ResponseCode {

    /**
     * Guide: User 엔티티의 경우 USER_BAD_REQUEST, Chat 엔티티의 경우 CHAT_BAD_REQUEST 형식으로 엔티티 이름을 앞에다 붙여줄 것.
     */
    SUCCESS(200, "COM-000", "OK", HttpStatus.OK);

    final Integer status;
    final String code;
    final String message;
    final HttpStatus httpStatus;


    ResponseCode(Integer status, String code, String message, HttpStatus httpStatus) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

}
