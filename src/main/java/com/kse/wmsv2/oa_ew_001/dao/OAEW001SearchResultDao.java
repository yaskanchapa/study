package com.kse.wmsv2.oa_ew_001.dao;

import lombok.Data;

@Data
public class OAEW001SearchResultDao {

    String cCheck;      // check
    String bilMake;    // Bil
    String bondInBill;   // 搬伝発行
    String bondInBillNo;   // 搬入伝票番号
    String awbNo;      // MAWBNo
    String cwbNo;              // HAWB番号
    String cwbCount;    // HAWB件数
    String carryingPiece;    // 個数
    String carryingWeight;   // 重量
    String arrivalDate;      // 対査日
    String bondInBillIssueDate;  // 搬伝発行日
    String userDepartmentCd;


}
