package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003InsertAiInventoryDao {
    // 蔵置場所
    private String bondedWareHouseCd;

    // CWB
    private String cwbNo;

    // CWB枝番
    private String cwbNoDeptCd;

    // 作業開始時間
    private String workStartTime;

    // 作業終了時間
    private String workEndTime;

    // 改装仕分区分
    private String remodelingFlg;

    // 対象区分
    private String objectFlg;

    // 到着便名1
    private String flt1;

    // 到着便名2
    private String flt2;

    // Origin
    private String origin;

    // AWB
    private String awbNo;

    // BWB
    private String bwbNo;

    // スキャン個数
    private String scanPicec;

    // データ個数
    private String dataPicec;

    // データ重量
    private String dataWeight;

    // 仕分区分名
    private String classifyClassName;

    // 特別手配名01
    private String specialPrepareName01;

    // 特別手配名02
    private String specialPrepareName02;

    // 特別手配名03
    private String specialPrepareName03;

    // 特別手配名04
    private String specialPrepareName04;

    // 特別手配名05
    private String specialPrepareName05;

    // 特別手配名06
    private String specialPrepareName06;

    // 特別手配名07
    private String specialPrepareName07;

    // 特別手配名08
    private String specialPrepareName08;

    // 特別手配名09
    private String specialPrepareName09;

    // 特別手配名10
    private String specialPrepareName10;

    // オーバー区分
    private String overFlg;

    // ハンディID
    private String handyTerminalId;

    // 登録者
    private String regUserCd;

    // システム登録日
    private String regDate;

    // 更新者
    private String updateUserCd;

    // システム更新日
    private String updateDate;





}
