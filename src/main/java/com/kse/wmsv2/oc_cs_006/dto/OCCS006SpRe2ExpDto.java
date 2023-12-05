package com.kse.wmsv2.oc_cs_006.dto;

import lombok.Data;

@Data
public class OCCS006SpRe2ExpDto {
    /** イメージクラス名*/
    private String imageClassName;

    // 日付部
    /** 日付1 */
    private String dateyyyyMMddHHmm;
    /** 日付2 */
    private String dateyyyyMMddHHmmss;
    /** 日付3 */
    private String dateyyyyMMddHHmmss2;
    /** 日付4 */
    private String dateyyyyMMdd;

    //　ヘッダー部
    /** 輸入・輸出クラス */
    private String impExpClass;
    /** 申告先種別 */
    private String customsKindCd;
    /** 識別符号 */
    private String discernmentMark;
    /** 区分 */
    private String reportClass;
    /** あて先税関 */
    private String reportDepCd;
    /** 提出先 */
    private String reportDivisionCd;
    /** 申告年月日 */
    private String reportDate;
    /** 申告番号 */
    private String reportNo;

    // ボディー部
    /** 申告条件 */
    private String reportCondition;
    /** 搬入 */
    private String carryingIn;
    /** 輸出者1 */
    private String expCustomerName1;
    /** 輸出者2 */
    private String expCustomerName2;
    /** 輸出者3 */
    private String expCustomerName3;
    /** 住所1 */
    private String expCustomerAdd1;
    /** 住所2 */
    private String expCustomerAdd2;
    /** 住所3 */
    private String expCustomerAdd3;
    /** 住所4 */
    private String expCustomerAdd4;
    /** 住所5 */
    private String expCustomerAdd5;
    /** 住所6 */
    private String expCustomerAdd6;
    /** 電話 */
    private String expCustomerTel;
    /** 税関事務管理人1 */
    private String customsOfficeJanitorName1;
    /** 税関事務管理人2 */
    private String customsOfficeJanitorName2;
    /** 税関事務管理人3 */
    private String customsOfficeJanitorName3;
    /** 税関事務管理人4 */
    private String customsOfficeJanitorName4;
    /** 税関事務管理人5 */
    private String customsOfficeJanitorName5;
    /** 税関事務管理人6 */
    private String customsOfficeJanitorName6;
    /** 仕向人1 */
    private String consigneeNo1;
    /** 仕向人2 */
    private String consigneeNo2;
    /** 仕向人3 */
    private String consigneeName;
    /** 住所1 */
    private String consigneeAdd1;
    /** 住所2 */
    private String consigneeAdd2;
    /** 住所3 */
    private String consigneeAdd3;
    /** 住所4 */
    private String consigneeAdd4;
    /** 住所5 */
    private String consigneeAdd5;
    /** 住所6 */
    private String consigneeAdd6;
    /** 国コード */
    private String consigneeCountry;
    /** 代理人1 */
    private String agent1;
    /** 代理人2 */
    private String agent2;
    /** 通関士コード */
    private String reportCustomsSpecialistUserCd;
    /** 検査立会者 */
    private String inspectionWitness;
    /** HAWB番号先頭3桁 */
    private String cwbNo1;
    /** HAWB番号 */
    private String cwbNo2;
    /** 最終仕向地1 */
    private String lastDestinationCd1;
    /** 最終仕向地2 */
    private String lastDestinationCd2;
    /** 積込港1 */
    private String depPortCd1;
    /** 積込港2 */
    private String depPortCd2;
    /** 貨物個数 */
    private String carryingPiece;
    /** 貨物重量1 */
    private String carryingWeight;
    /** 保税地域1 */
    private String customsPlaceCd1;
    /** 保税地域2 */
    private String customsPlaceCd2;
    /** 蔵置税関1 */
    private String bondedWareHouseCustomsCd1;
    /** 蔵置税関2 */
    private String bondedWareHouseCustomsCd2;
    /** 通貨レート1 */
    private String currencyRate1;
    /** 通貨レート2 */
    private String currencyRate2;
    /** ＦＯＢ価格1 */
    private String fobAmo1;
    /** ＦＯＢ価格2 */
    private String fobAmo2;

    /** 品名 */
    private String item;
    /** 申告価格1 */
    private String reportValue1;
    /** 申告価格2 */
    private String reportValue2;
    /** 記事 */
    private String news1;
    /** 荷主セクションコード */
    private String merchantSectionCode;
    /** 荷主Ｒｅｆ Ｎｏ． */
    private String shipperReferenceNumber;
    /** 社内整理番号 */
    private String inHouseRefNumber;
    /** 利用者整理番号 */
    private String userRefNumber;
    /** 輸出者(入力)1 */
    private String expCustomerNameInput1;
    /** 輸出者(入力)2 */
    private String expCustomerNameInput2;

}
