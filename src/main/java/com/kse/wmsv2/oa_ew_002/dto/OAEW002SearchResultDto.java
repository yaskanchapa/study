package com.kse.wmsv2.oa_ew_002.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW002SearchResultDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAEW002SelectContainer1Dto> resultRowData;
}
