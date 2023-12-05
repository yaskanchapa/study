package com.kse.wmsv2.oa_et_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAET003SearchResultDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAET003SearchResultListDto> resultData;
}
