package com.kse.wmsv2.oc_cs_006.dto;

import lombok.Data;

@Data
public class OCCS006BondInBillDto {
    /** 日付1 yyyy/MM/dd HH:mm */
    private String dateyyyyMMddHHmm;
    /** 日付2 yyyy/MM/dd HH:mm:ss */
    private String dateyyyyMMddHHmmss;
    /** 日付3 yyyyMMddHHmmss */
    private String dateyyyyMMddHHmmss2;
    /** 日付4 yyyy.MM.dd */
    private String dateyyyyMMdd;
    /** 搬入伝票番号 */
    private String bondInBillNo;
    /** H/W */
    private String bondedWareHouseCd;
    /** A/G */
    private String reportPlanPersonCd;
    /** CWBNO */
    private String cwbNo;
    /** PCS */
    private String totalPiece;
    /** GPCS */
    private String carryingPiece;
    /** WT（KGM） */
    private String carryingWeight;
    /** PORT */
    private String origin;
    /** DST */
    private String fltDestination;
    /** J */
    private String totalWeight;
    /** B.O */
    private String agentDiv;
    /** SPC */
    private String specialCargoSign;
    /** COMMODITY */
    private String spsDocClassCd;
    /** A/L */
    private String airLine;
    /** AWBNO */
    private String awbNo;
}
