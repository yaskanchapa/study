package com.kse.wmsv2.oa_it_002.dto;

import lombok.Data;

@Data
public class ValidationDto {

    String field;

    String message;

    public ValidationDto(String field, String message) {
        this.field = field;
        this.message = message;
    }
}
