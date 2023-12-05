package com.kse.wmsv2.common.dto;

import lombok.Data;

@Data
public class HawbLogicalLockResDto {
    boolean result; // 処理結果
    String errMessage; // エラーメッセージ
    String awbNo; // MAWB No.
    String cwbNo; // HAWB No.
    String userCd; // ユーザコード
    String lockDateTime; // ロックした日時(YYYY-MM-DD hh:mm:ss)
}
