package com.kse.wmsv2.common.dto;

import lombok.Data;

@Data
public class HawbLogicalLockReqDto {
    String awbNo; // MAWB No.
    String cwbNo; // HAWB No.
    String userCd; // ユーザコード
    boolean override; // オーバーライドフラグ、Trueの場合ロックを上書きする。
}
