package com.kse.wmsv2.common.exception.handler;

import com.kse.wmsv2.oa_it_002.dto.ValidationDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<?> handleValidationErrors(BindException exception) {
        BindingResult result = exception.getBindingResult();
        List<FieldError> fieldErrors = result.getFieldErrors();
        List<ValidationDto> listValidationDto = new ArrayList<ValidationDto>();
        for (FieldError error : fieldErrors ) {
            ValidationDto validationDto = new ValidationDto(error.getField(), error.getDefaultMessage());
            listValidationDto.add(validationDto);
        } 
        return new ResponseEntity<>(listValidationDto, HttpStatus.BAD_REQUEST);
    }

}
