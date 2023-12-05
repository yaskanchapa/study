package com.kse.wmsv2.common.exception.exceptions;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends BaseException {

    /**
     * Constructor.
     * 
     * @param error Error object
     */
    public NotFoundException(Error error) {
        super(error, HttpStatus.NOT_FOUND);
    }
}
