package com.kse.wmsv2.oa_iw_002.dto;
import lombok.Data;
@Data
public class OAIW002SelectAiCargoOutHead2Dto {
    String bondedWarehouseCd;
    String ftl1;
    String ftl2;
    String awbNo;
    String cargoOutDate;
    int cwbCount;
    int scanCwbCount;
    int overCwbCount;
    String scanPiece;
    String dataPiece;
    String dataWeight;
    String regUserCd;
    String regDate;
    String updateUserCd;
    String updateDate;
}
