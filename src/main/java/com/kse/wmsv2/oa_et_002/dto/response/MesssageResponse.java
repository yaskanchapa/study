package com.kse.wmsv2.oa_et_002.dto.response;

import lombok.Data;

@Data
public class MesssageResponse {

    private String message;

    public MesssageResponse(String message) {
        this.message = message;
    }
    
}