package goojeans.harulog.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import goojeans.harulog.domain.ResponseCode;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response<T> {
    @NotNull
    private Integer status;
    @NotNull
    private String code;
    @NotNull
    private String message;
    private T data;

    /**
     * 성공 응답에 대한 정적 팩토리 메서드
     */
    public static <T> Response<T> ok (T data) {
        Response<T> response = new Response<T>();
        response.status = ResponseCode.SUCCESS.getStatus();
        response.code = ResponseCode.SUCCESS.getCode();
        response.message = ResponseCode.SUCCESS.getMessage();
        response.data = data;
        return response;
    }

    /**
     * 실패 응답에 대한 정적 팩토리 메서드
     */
    public static <T> Response<T> fail (ResponseCode responseCode) {
        Response<T> response = new Response<T>();
        response.status = responseCode.getStatus();
        response.code = responseCode.getCode();
        response.message = responseCode.getMessage();
        return response;
    }
}
