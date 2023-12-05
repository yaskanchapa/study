package com.kse.wmsv2.oa_ew_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW003SearchWarehouseResultDto {

    String termNo;
    String bondedWarehouseCd;
    String regUserCd;
    String Message;
    String errorMessage;
    boolean result;
    List<OAEW003SearchWarehouseResultListDto> resultData;
}
