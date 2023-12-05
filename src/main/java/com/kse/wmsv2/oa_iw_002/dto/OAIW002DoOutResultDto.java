package com.kse.wmsv2.oa_iw_002.dto;

import java.util.List;
import lombok.Data;
@Data
public class OAIW002DoOutResultDto {
    String message;
    String errorMessage;
    boolean result;
    boolean reSearchErr;
    List<OAIW002SearchResultDto> gridDataList;
    String awbNoCnt;
    String outFileCnt;
}
