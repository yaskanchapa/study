package com.kse.wmsv2.oc_cs_006.dto;

import lombok.Data;

@Data
public class OCCS006HdImpDto {
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
    /** 代表税番 */
    private String taxNo;
    /** 申告種別1 */
    private String reportKindCd1;
    /** 申告種別2 */
    private String reportKindCd2;
    /** 区分 */
    private String reportClass;
    /** あて先税関 */
    private String reportDepCd;
    /** 部門 */
    private String reportDivisionCd;
    /** 申告年月日 */
    private String reportDate;
    /** 申告番号 */
    private String reportNo;

    // ボディー部
    /** 申告条件 */
    private String reportCondition;
    /** 申告予定年月日 */
    private String reportPlanDate;
    /** 本申告 */
    private String declaration;
    /** 輸入者1 */
    private String impCustomerName1;
    /** 輸入者2 */
    private String impCustomerName2;
    /** 輸入者3 */
    private String impCustomerName3;
    /** 住所1 */
    private String impCustomerAdd1;
    /** 住所2 */
    private String impCustomerAdd2;
    /** 住所3 */
    private String impCustomerAdd3;
    /** 住所4 */
    private String impCustomerAdd4;
    /** 住所5 */
    private String impCustomerAdd5;
    /** 電話 */
    private String impCustomerTel;
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
    /** 輸入取引者1 */
    private String impDealingsName1;
    /** 輸入取引者2 */
    private String impDealingsName2;
    /** 輸入取引者3 */
    private String impDealingsName3;
    /** 仕出人1 */
    private String expCustomerName1;
    /** 仕出人2 */
    private String expCustomerName2;
    /** 仕出人3 */
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
    /** 輸出の委託者 */
    private String author;
    /** 代理人1 */
    private String representativeName1;
    /** 代理人2 */
    private String representativeName2;
    /** 通関士コード */
    private String reportCustomsSpecialistUserCd;
    /** 検査立会者 */
    private String inspectionWitness;
    /** ＡＷＢ番号先頭3桁 */
    private String cwbNo1;
    /** ＡＷＢ番号 */
    private String cwbNo2;
    /** ＭＡＷＢ番号先頭3桁 */
    private String awbNo1;
    /** ＭＡＷＢ番号 */
    private String awbNo2;
    /** 取卸港1 */
    private String getPort1;
    /** 取卸港2 */
    private String getPort2;
    /** 積出地1 */
    private String shipmentCd1;
    /** 積出地2 */
    private String shipmentCd2;
    /** 積載機名 */
    private String loadingPlanFLT;
    /** 入港年月日 */
    private String arrPortDate;
    /** 蔵置税関1 */
    private String customsCd1;
    /** 蔵置税関2 */
    private String customsCd2;
    /** 貨物個数 */
    private String carryingPiece;
    /** 保税地域1 */
    private String customsPlaceCd1;
    /** 保税地域2 */
    private String customsPlaceCd2;
    /** 貨物重量1 */
    private String carryingWeight1;
    /** 貨物重量2 */
    private String carryingWeight2;
    /** 搬入予定1 */
    private String bondedWareHouseCurDate1;
    /** 搬入予定2 */
    private String bondedWareHouseCurDate2;
    /** 最初蔵入年月日 */
    private String firstCarBondedDate;
    /** 貿易形態別符号 */
    private String tradeTypeMark;
    /** 調査用符号 */
    private String customsExamMark;
    /** 貿易管理 */
    private String tradeCont;
    /** 輸入承認証 */
    private String impRecoAttachDisc ;
    /** 関税法70条関係許可承認1 */
    private String customsLaw1;
    /** 関税法70条関係許可承認2 */
    private String customsLaw2;
    /** 関税法70条関係許可承認3 */
    private String customsLaw3;
    /** 関税法70条関係許可承認4 */
    private String customsLaw4;
    /** 関税法70条関係許可承認5 */
    private String customsLaw5;
    /** 共通管理番号 */
    private String otherMiniContNo;
    /** 食品1 */
    private String fhProofDisc1;
    /** 食品2 */
    private String fhProofDisc2;
    /** 植防1 */
    private String plantPEDisc1;
    /** 植防2 */
    private String plantPEDisc2;
    /** 動検1 */
    private String animalQuaraDisc1;
    /** 動検2 */
    private String animalQuaraDisc2;
    /** 輸入承認証番号等1 */
    private String otherLawRecNo1;
    /** 輸入承認証番号等2 */
    private String otherLawRecNo2;
    /** 輸入承認証番号等3 */
    private String otherLawRecNo3;
    /** 輸入承認証番号等4 */
    private String otherLawRecNo4;
    /** 輸入承認証番号等5 */
    private String otherLawRecNo5;
    /** 輸入承認証番号等6 */
    private String otherLawRecNo6;
    /** 輸入承認証番号等7 */
    private String otherLawRecNo7;
    /** 輸入承認証番号等8 */
    private String otherLawRecNo8;
    /** 輸入承認証番号等9 */
    private String otherLawRecNo9;
    /** 輸入承認証番号等10 */
    private String otherLawRecNo10;
    /** 仕入書番号1 */
    private String invoiceNo1;
    /** 仕入書番号2 */
    private String invoiceNo2;
    /** 仕入書(電子） */
    private String eleInvoiceNo;
    /** 仕入書価格1 */
    private String invoiceValue1;
    /** 仕入書価格2 */
    private String invoiceValue2;
    /** 仕入書価格3 */
    private String invoiceValue3;
    /** 仕入書価格4 */
    private String invoiceValue4;
    /** 運賃1 */
    private String fareCurrencyCd1;
    /** 運賃2 */
    private String fareCurrencyCd2;
    /** 運賃3 */
    private String fareCurrencyCd3;
    /** 保険1 */
    private String insurance1;
    /** 保険2 */
    private String insurance2;
    /** 保険3 */
    private String insurance3;
    /** 保険4 */
    private String insurance4;
    /** 通関金額1 */
    private String customsValue1;
    /** 通関金額2 */
    private String customsValue2;
    /** 評価1 */
    private String estimationFlgCd1;
    /** 評価2 */
    private String estimationFlgCd2;
    /** 評価3 */
    private String estimationFlgCd3;
    /** 評価4 */
    private String estimationFlgCd4;
    /** 補正A1 */
    private String incReviseA1;
    /** 補正A2 */
    private String incReviseA2;
    /** 補正A3 */
    private String incReviseA3;
    /** 補正A4 */
    private String incReviseA4;
    /** 補正B1 */
    private String incReviseB1;
    /** 補正B2 */
    private String incReviseB2;
    /** 補正B3 */
    private String incReviseB3;
    /** 補正C1 */
    private String incReviseC1;
    /** 補正C2 */
    private String incReviseC2;
    /** 補正C3 */
    private String incReviseC3;
    /** 補正D1 */
    private String incReviseD1;
    /** 補正D2 */
    private String incReviseD2;
    /** 補正D3 */
    private String incReviseD3;
    /** 事前教示（評価）1 */
    private String advanceTeachingEvaluation1;
    /** 事前教示（評価）2 */
    private String advanceTeachingEvaluation2;
    /** ＢＰＲ合計1 */
    private String basicPriceRate1;
    /** ＢＰＲ合計2 */
    private String basicPriceRate2;
    /** 計算 */
    private String calculate;
    /** 原産地証明 */
    private String originCertifiDisc;
    /** 戻税申告 */
    private String backTaxReportDisc;
    /** 内容点検結果 */
    private String contCheckReOther;
    /** 税科目A1 */
    private String taxItemA1;
    /** 税科目A2 */
    private String taxItemA2;
    /** 税額合計A */
    private String taxAmoA;
    /** 欄数A */
    private String columnCountA;
    /** 税科目B1 */
    private String taxItemB1;
    /** 税科目B2 */
    private String taxItemB2;
    /** 税額合計B */
    private String taxAmoB;
    /** 欄数B */
    private String columnCountB;
    /** 税科目C1 */
    private String taxItemC1;
    /** 税科目C2 */
    private String taxItemC2;
    /** 税額合計C */
    private String taxAmoC;
    /** 欄数C */
    private String columnCountC;
    /** 納税額合計 */
    private String taxAmo;
    /** 担保額 */
    private String collateralAmo;
    /** 通貨レートA1 */
    private String currencyRateA1;
    /** 通貨レートA2 */
    private String currencyRateA2;
    /** 通貨レートB1 */
    private String currencyRateB1;
    /** 通貨レートB2 */
    private String currencyRateB2;
    /** 通貨レートC1 */
    private String currencyRateC1;
    /** 通貨レートC2 */
    private String currencyRateC2;
    /** 口座 */
    private String accountNo;
    /** 都道府県 */
    private String add1;
    /** 納付方法 */
    private String payMethodDis;
    /** 構成1 */
    private String composition1;
    /** 構成2 */
    private String composition2;

    // フッター部
    /** 記事(税関) */
    private String news1;
    /** 記事(税関) */
    private String news2;
    /** 輸 入 者(入力) */
    private String impCustomerNameInput1;
    /** 輸 入 者(入力) */
    private String impCustomerNameInput2;
    /** 記事(通関) */
    private String news3;
    /** 輸入取引者(入力) */
    private String impDealingsNameInput1;
    /** 輸入取引者(入力) */
    private String impDealingsNameInput2;
    /** 記事(荷主) */
    private String newsShipper;
    /** 社内整理番号 */
    private String inHouseRefNumber;
    /** 荷主セクションコード */
    private String merchantSectionCode;
    /** 荷主Ｒｅｆ Ｎｏ． */
    private String shipperReferenceNumber;
    /** 利用者整理番号 */
    private String userRefNumber;
    /** バーコード */
    private String BARCODE;

    // 繰返し部
    /** 欄 */
    private String reNum;
    /** 統合先欄 */
    private String mergeCd;
    /** 品目番号1 */
    private String itemNum1;
    /** 品目番号2 */
    private String itemNum2;
    /** 品目番号3 */
    private String itemNum3;
    /** 価格再確認 */
    private String valueCheckFlg;
    /** 品名 */
    private String item;
    /** 数量（１）1 */
    private String pieceA1;
    /** 数量（１）2 */
    private String pieceA2;
    /** 税表番号 */
    private String examDate;
    /** 数量（２）1 */
    private String pieceB1;
    /** 数量（２）2 */
    private String pieceB2;
    /** 申告価格（ＣＩＦ）A */
    private String reportValueA;
    /** 課税標準数量A1 */
    private String taxBasePieceA1;
    /** 課税標準数量A2 */
    private String taxBasePieceA2;
    /** 申告価格（ＣＩＦ）B */
    private String reportValueB;
    /** 課税標準数量B1 */
    private String taxBasePieceB1;
    /** 課税標準数量B2 */
    private String taxBasePieceB2;
    /** 関税率1 */
    private String customRate1;
    /** 関税率2 */
    private String customRate2;
    /** 輸入令別表 */
    private String impTradeContCd;
    /** 特恵  */
    private String gsp;
    /** 関税額  */
    private String tariffAmount;
    /** ＢＰＲ按分係数  */
    private String basicPriceTotal;
    /** 減免税額A  */
    private String customsExemptAmoA;
    /** ＢＰＲ金額1  */
    private String basicPriceAmo1;
    /** ＢＰＲ金額2  */
    private String basicPriceAmo2;
    /** 減免税額B  */
    private String customsExemptAmoB;
    /** 運賃按分  */
    private String fareDivDisc;
    /** 原産地1  */
    private String countryOfOriginCd1;
    /** 原産地2  */
    private String countryOfOriginCd2;
    /** 原産地3  */
    private String countryOfOriginCd3;
    /** 減免税1  */
    private String inConsTaxExemCd1;
    /** 減免税2  */
    private String inConsTaxExemCd2;
    /** 法  */
    private String otherLaw1;
    /** 令  */
    private String otherLaw2;
    /** 別表  */
    private String otherLaw3;
    /** 事前教示（分類）  */
    private String advanceTeachingClassification;
    /** （原産地）  */
    private String advanceTeachingPlaceOrigin;
    /** 内国消費税等(1)  */
    private String inConsTaxKindCd1;
    /** 種別(1)  */
    private String type1;
    /** 課税標準額(1)C  */
    private String taxBaseAmount1C;
    /** 課税標準数量(1)C1  */
    private String taxBasePiece1C1;
    /** 課税標準数量(1)C2  */
    private String taxBasePiece1C2;
    /** 課税標準額(1)D  */
    private String taxBaseAmount1D;
    /** 課税標準数量(1)D1  */
    private String taxBasePiece1D1;
    /** 課税標準数量(1)D2  */
    private String taxBasePiece1D2;
    /** 税率(1)  */
    private String taxRate1;
    /** 税額(1)  */
    private String taxAmount1;
    /** 減免税(1)  */
    private String taxExemption1;
    /** 減免税額(1)A  */
    private String taxExemptionAmount1A;
    /** 条項(1)A  */
    private String clause1A;
    /** 減免税額(1)B  */
    private String taxExemptionAmount1B;
    /** 条項(1)B  */
    private String clause1B;
    /** 内国消費税等(2)  */
    private String inConsTaxKindCd2;
    /** 種別(2)  */
    private String type2;
    /** 課税標準額(2)C  */
    private String taxBaseAmount2C;
    /** 課税標準数量(2)C1  */
    private String taxBasePiece2C1;
    /** 課税標準数量(2)C2  */
    private String taxBasePiece2C2;
    /** 課税標準額(2)B  */
    private String taxBaseAmount2D;
    /** 課税標準数量(2)D1  */
    private String taxBasePiece2D1;
    /** 課税標準数量(2)D2  */
    private String taxBasePiece2D2;
    /** 税率(2)  */
    private String taxRate2;
    /** 税額(2)  */
    private String taxAmount2;
    /** 減免税(2)  */
    private String taxExemption2;
    /** 減免税額(2)A  */
    private String taxExemptionAmount2A;
    /** 条項(2)A  */
    private String clause2A;
    /** 減免税額(2)B  */
    private String taxExemptionAmount2B;
    /** 条項(2)B  */
    private String clause2B;

}
