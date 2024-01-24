package goojeans.harulog.domain;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException{

    private final ResponseCode errorCode;

    public BusinessException(ResponseCode errorCode) {
        this.errorCode = errorCode;
    }
}
