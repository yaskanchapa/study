package com.kse.wmsv2.oa_iw_002.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAIW002DoOutBusinessDto {
    int awbNoCnt;
    int outFileCnt;
    List<OAIW002SearchResultDto> resultDtoList;

    boolean reSearchResult;
}
