package com.kse.wmsv2.common.exception;

import com.kse.wmsv2.common.error.Error;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;


@NoArgsConstructor
public class BaseException extends RuntimeException {

    @Getter
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    @Getter
    private Error error;



    public BaseException(Error error, HttpStatus status) {
        this.error = error;
        this.status = status;
    }

    public BaseException(Error error) {
        this.error = error;
    }



    public BaseException(String message) {
        super(message);
    }

    public BaseException(Throwable cause) {
        super(cause);
    }
}
