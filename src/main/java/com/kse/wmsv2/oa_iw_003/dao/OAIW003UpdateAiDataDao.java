package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003UpdateAiDataDao {
    // AWB
    private String awbNo;

    // CWB
    private String cwbNo;

    // CWB枝番
    private String cwbNoDeptCd;

    // 搬入仕分区分名
    private String inClassifyClassName;

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

    // 更新者
    private String updateUserCd;

    // システム更新日
    private String updateDate;

    // ログインユーザーの蔵置場所
    private String bondedWareHouseCd;


}
