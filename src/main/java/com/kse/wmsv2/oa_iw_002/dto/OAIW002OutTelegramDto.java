package com.kse.wmsv2.oa_iw_002.dto;

import lombok.Data;

@Data
public class OAIW002OutTelegramDto { // OUT電文作成パラメータ
    String awbNo;
    String cwbNo;
    String bwbNo;
    String cwbNoDeptCd;
    String carryOutDate;
    String collectTrader;
    String otherWareHouse;
    String cargoOutPiece;
    String dataPiece;
    String customPlaceCd;
    String bondedWarehouseCd;
    String currentCargoStatusCd;
    String sortCwbNo;
    String sortCwbNo2;
}
