package com.kse.wmsv2.common.exception.exceptions;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends BaseException {

  /**
   * Constructor.
   *
   * @param error Error object
   */
  public BadRequestException(Error error) {
    super(error, HttpStatus.BAD_REQUEST);
//    super(error, HttpStatus.OK);
  }


}
