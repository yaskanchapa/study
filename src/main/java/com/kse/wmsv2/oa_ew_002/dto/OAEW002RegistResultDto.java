package com.kse.wmsv2.oa_ew_002.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW002RegistResultDto {
    String message;
    String errorMessage;
    boolean result;
    boolean isNew;
    OAEW002SelectContainer1Dto newRowData;
}
