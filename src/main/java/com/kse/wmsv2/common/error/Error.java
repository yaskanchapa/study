package com.kse.wmsv2.common.error;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Error {
    
    private String code;
    private String message;

    /**
     * Constructor.
     *
     * @param code    Error code
     * @param message Error message
     */
    @Builder
    public Error(String code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Constructor.
     *
     * @param code Error code
     */
    public Error(String code) {
        this.code = code;
    }
}