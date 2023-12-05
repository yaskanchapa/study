package com.kse.wmsv2.oa_iw_002.dao;
import lombok.Data;
@Data
public class OAIW002SelectBondedOutListDao {
    String carryOutDate;
    String bondedWarehouseCd;
    String arrFtl1;
    String arrFtl2;
    String awbNo;
    boolean unMatchHawbCntFlg;
    boolean unMatchPieceFlg;
    boolean matchFlg;
    boolean noPermitAwbHiddenFlg;
    String origin;
}
