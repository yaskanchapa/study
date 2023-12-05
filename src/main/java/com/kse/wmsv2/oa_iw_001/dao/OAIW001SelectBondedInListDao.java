package com.kse.wmsv2.oa_iw_001.dao;

import lombok.Data;

@Data
public class OAIW001SelectBondedInListDao {
    String arrPortDate; // 登録年月日
    String awbNo;
    String arrFtl1;
    String arrFtl2;
    String bondedWarehouseCd;
    String origin;
    boolean batchTargetFlg; // バッチ・対象
    boolean batchNotTargetFlg; // バッチ・対象外
    boolean unMatchHawbCntFlg; // アンマッチ・HAWB件数
    boolean unMatchPieceFlg; // アンマッチ・個数
    boolean matchFlg; // マッチ
}
