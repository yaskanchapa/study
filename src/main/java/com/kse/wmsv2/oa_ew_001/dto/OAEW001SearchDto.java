package com.kse.wmsv2.oa_ew_001.dto;

import lombok.Data;

@Data
public class OAEW001SearchDto {
    String bondInBillNo;      //搬入伝票番号
    String arrivalDate;        //対査日
    String cwbNo;              // HAWB番号
    String awbNo;              //MAWB No
    String bondInBillIssueDate;      //搬伝発行日
    String bondInBillEnd;          //搬伝発行
    String bondInBillNotEnd;          //搬伝発行
    String bilMakeEnd;             //BIL伝票
    String bilMakeNotEnd;             //BIL伝票
    String userAuthorityCd;   //　　業務権限：トークンの実装が不明のため要確認
    String userDepartmentCd;
}

