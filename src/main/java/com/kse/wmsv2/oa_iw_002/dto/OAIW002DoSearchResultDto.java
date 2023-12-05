package com.kse.wmsv2.oa_iw_002.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAIW002DoSearchResultDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAIW002SearchResultDto> resultData;
}
