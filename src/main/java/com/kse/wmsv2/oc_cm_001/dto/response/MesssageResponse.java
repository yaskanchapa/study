package com.kse.wmsv2.oc_cm_001.dto.response;

import lombok.Data;

@Data
public class MesssageResponse {

    private String message;

    public MesssageResponse(String message) {
        this.message = message;
    }
    
}