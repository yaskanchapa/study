package com.kse.wmsv2.oa_iw_002.dao;
import lombok.Data;
@Data
public class OAIW002SelectAiCargoOutData1Dao {
    String bondedWarehouseCd;
    String arrFtl1;
    String arrFtl2;
    String awbNo;
    String carryOutDate;
    String inClassifyClassName;
    boolean unMatchHawbCntFlg;
    boolean unMatchPieceFlg;
    boolean matchFlg;
}
