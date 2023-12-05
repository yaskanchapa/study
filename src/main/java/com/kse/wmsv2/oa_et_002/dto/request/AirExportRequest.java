package com.kse.wmsv2.oa_et_002.dto.request;

import lombok.Data;

@Data
public class AirExportRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    private Integer objectDate;

    /** 日付 */
    private String truckNoDate;

    /** 積込港 */
    private String depPort;

    /** 仕向地 */
    private String destination;

    /** 許可区分 */
    private String permitClassCd;

    /** 提出有無 */
    private String permitClass;

    /** シャトル/トラック便（開始） */
    private String linkTruckNOFrom;

    /** シャトル/トラック便（終了） */
    private String linkTruckNOTo;

    /** HAWB No. */
    private String cWBNo;

    /** MAWB No. */
    private String aWBNo;

    /** 代理店 */
    private String agent;

    /** 蔵置場所 */
    private String bondedWareHouseCd;

    /** 対査確認・未 */
    private Integer arrival01;

    /** 対査確認・済 */
    private Integer arrival02;

    /** BIL作成・未 */
    private Integer bIL01;

    /** BIL作成・済 */
    private Integer bIL02;

    /** 搬前申告・未 */
    private Integer reserveDeclara01;

    /** 搬前申告・済 */
    private Integer reserveDeclara02;

    /** 積付・未 */
    private Integer consolidation01;

    /** 積付・済 */
    private Integer consolidation02;

    /** CDB作成・未 */
    private Integer cDB01;

    /** CDB作成・済 */
    private Integer cDB02;

    /** EDA作成・未 */
    private Integer eDA01;

    /** EDA作成・済 */
    private Integer eDA02;

    /** 搬後申告・未 */
    private Integer declara01;

    /** 搬後申告・済 */
    private Integer declara02;

    /** HDF作成・未 */
    private Integer hDF01;

    /** HDF作成・済 */
    private Integer hDF02;

    /** CDB戻り・未 */
    private Integer cDBre01;

    /** CDB戻り・済 */
    private Integer cDBre02;

    /** MEC/EDC作成・未 */
    private Integer mECEDC01;

     /** MEC/EDC作成・済 */
     private Integer mECEDC02;

      /** 許可・未 */
    private Integer permission01;

     /** 許可・済 */
     private Integer permission02;

      /** ULM作成・未 */
    private Integer uLM01;

     /** ULM作成・済 */
     private Integer uLM02;

      /** Live */
    private Integer lA01;

     /** Archive */
     private Integer lA02;

      /** 申告可 */
    private Integer possible01;

    /** 申告不可 */
    private Integer possible02;

    /** EDA */
    private Integer eSAFlg01;

    /** MEC */
    private Integer eSAFlg02;

    /** 大額 */
    private Integer largeSmallFlg01;

    /** 少額 */
    private Integer largeSmallFlg02;

    private String operator;

    private String authorityCd;

    private String departmentCd;

    /**
     * Set page for get data.
     * 
     * @param adminSearch AdminSearch
     * @return page number
     */
    public static AirExportRequest setPaging(AirExportRequest airExportRequest) {
        if (airExportRequest.getPage() != null && airExportRequest.getItemPerPage() != null) {
            airExportRequest.setPage((airExportRequest.getPage() - 1) * airExportRequest.getItemPerPage());
        }
        return airExportRequest;
    }
}