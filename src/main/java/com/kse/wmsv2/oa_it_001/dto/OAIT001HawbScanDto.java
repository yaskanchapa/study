package com.kse.wmsv2.oa_it_001.dto;

import java.util.List;

import lombok.Data;

@Data
public class OAIT001HawbScanDto {

    private List<OAIT001ItemTextDto> listCondition;
    private List<OAIT001ItemTextDto> listSubject;
    private List<OAIT001ItemTextDto> listConfirm;
    private List<OAIT001ItemTextDto> listCustomsBroker;
    private List<OAIT001ItemTextDto> listCustomsClearanceStatus;
    private List<OAIT001ItemTextDto> listDataList;
    private List<OAIT001HawbRowInfoDto> listHawbRowInfoDto;
    List<String> messageList;
    private Boolean checkResult;
    public OAIT001HawbScanDto() {
    }

}