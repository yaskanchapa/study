package com.kse.wmsv2.telegram.dto;

import lombok.Data;

@Data
public class OutTelegramParamDto {
    String awbNo;
    String cwbNo;
    String cwbNoDeptCd;
    String BondedWarehouseCd;
    String carryOutDate;
    String collectTrader;
    String otherWareHouse;
    //搬出時刻
    String bondedOutHHmm;
}
