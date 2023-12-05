package com.kse.wmsv2.common.exception.exceptions;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class InternalServerErrorException extends BaseException {

  /**
     * Constructor.
     * 
     * @param error Error object
     */
    public InternalServerErrorException(Error error) {
      super(error, HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
