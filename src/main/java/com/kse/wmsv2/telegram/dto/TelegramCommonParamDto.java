package com.kse.wmsv2.telegram.dto;

import lombok.Data;

@Data
public class TelegramCommonParamDto {
    String place;
    String businessCd;
    String reportCustomsSpecialistUserCd;
    String fileName;
    int recordCnt;
}
