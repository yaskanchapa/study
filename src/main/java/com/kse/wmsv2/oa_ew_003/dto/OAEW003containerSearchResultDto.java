package com.kse.wmsv2.oa_ew_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW003containerSearchResultDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAEW003containerSearchResultListDto> resultData;
}
