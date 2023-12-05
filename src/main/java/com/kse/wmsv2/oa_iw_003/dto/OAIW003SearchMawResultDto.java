package com.kse.wmsv2.oa_iw_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAIW003SearchMawResultDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAIW003SearchResult2Dto> resultData;
}
