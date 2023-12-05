package com.kse.wmsv2.telegram.dto;

import lombok.Data;

@Data
public class TelegramOutResultDto {
    String awbNo;
    String cwbNo;
    String cwbNoDeptCd;
    String agent;
    String carryOutFlg;
    String payKind;
    String additionalCharge;
    String diffDays;
}
