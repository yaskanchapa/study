package com.kse.wmsv2.common.exception.handler;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(value = 1)
@Slf4j
public class BaseExceptionHandler {


    @ExceptionHandler(value = BaseException.class)
    public ResponseEntity<?> exceptions(BaseException exception) {
        Error err = exception.getError();

        return new ResponseEntity<>(err, exception.getStatus());

    }

}
