package com.kse.wmsv2.oa_iw_005.dto;


import lombok.Data;

@Data
public class OAIW005SearchDto {
    // searchOption RadioBox
    private String searchOption;
    // search Date
    private String targetStartDate;

    private String targetEndDate;
    // 蔵置場
    private String cargoName;
    // 到着便名1
    private String currentArrFlt1;
    // 到着便名2
    private String currentArrFlt2;
    // オリジン
    private String org;
    // MAWB番号
    private String awbNo;
    // HAWBNo
    private String cwbNo;
    // 借物STS
    private String cargoStatus;
    // 仕分条件
    private String inClassifyClassName;
    // INV HAWB件数
    private String invHwbNo;
    // INV 個数 マッチ
    private String invCargoPiece;
    // INV アンマッチ
    private String invMatch;
    // ロケーション1(要確認)
    private String locateFrom;
    // ロケーション2(要確認)
    private String locateTo;
    // 申告区分
    private String permitNormal;
    // 申告区分
    private String permitMatch;
    // 許可区分
    private String permitClassCd;
}
