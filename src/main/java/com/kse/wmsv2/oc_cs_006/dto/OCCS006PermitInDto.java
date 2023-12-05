package com.kse.wmsv2.oc_cs_006.dto;


import lombok.Data;


//輸入許可書 DTO

// IMAGECLASS = RP
// PATTERN = I2R
// FILENAME = AAD1AG21EKKJ20230201094736040238

@Data
public class OCCS006PermitInDto {
    private String filePathList;
    private String dateyyyyMMdd;
    /** Date Format */
    private String dateyyyyMMddHHmm;
    /** Date Format2 */
    private String dateyyyyMMddHHmmss2;
    /** Date Format3 */
    private String dateyyyyMMddHHmmss;
    /** 代表統番 4 + 5 */
    private String representPermitNo;
    private String representPermitNoCd;
    /**  申告種別 6 + 7 + 8 */
    private String permitClass;
    /**  申告種別2 9*/
    private String permitClassCd2;
    /** 区分 10 */
    private String permitClassCd;
    /** あて先税関 11 */
    private String destCustom;
    /** 部門 12 */
    private String departmentCd;
    /** 申告年月日 13 */
    private String reportDate;
    /** 申告種別 17 */
    private String reportClassCd;
    /** 申告条件 18 + 19 */
    private String reportCondition;
    /** 申告予定年月日 20 */
    private String reportDate2;
    /** 本申告 21 */
    private String isReport;
    /** 輸　　入　　者 22 */
    private String impNo;
    private String impNo2;
    /** 輸　　入　　者 23 */
    private String impCustomerName;
    /** 住　　所 24 */
    private String impCustomerPostCd;
    /** 住　　所 1 25 （都道府県）*/
    private String impCustomerAddr1;
    /** 住　　所 2 26 市区町村（行政区名）*/
    private String impCustomerAddr2;
    /** 住　　所 3 27 （町域名・番地）*/
    private String impCustomerAddr3;

    /** 電　　話 29 */
    private String impCustomerTel;

    /** 税関事務管理人 (税関事務管理人コード) 30 */
    private String customerMasterCd;
    /** 税関事務管理人受理番号 31 */
    private String customerMasterNo;
    /** 税関事務管理人名 32 */
    private String customerMasterName;


    /** 輸 入 取 引 者 (輸入取引者コード) 33 */
    private String importerCd;
    /** 輸入取引者名 34 */
    private String importerName;

    /** 仕　　出　　人  (仕出人コード) 34 */
    private String consigneeCd;
    /** 仕 出 人 名 34 */
    private String consigneeName;
    /** 仕 出 人 住所 36 */
    private String consigneePostCd;
    /** 住所１（Street and number/P.O.BOX）37 */
    private String consigneeAddr1;
    /** 住所２（Street and number/P.O.BOX）38 */
    private String consigneeAddr2;
    /** 住所３（City name）39 */
    private String consigneeAddr3;
    /** 住所４（Country sub-entity,name）40 */
    private String consigneeAddr4;
    /** 国名コード（Country,coded）41 */
    private String conSigneeCountryCd;
    /** 輸出の委託者名 42 */
    private String expConsignorName;
    /** 代理人 (代理人コード) 43 */
    private String agentCd;
    /** 代理人名 44 */
    private String agentDiv;
    /** 通関士コード 45 */
    private String customCd;
    /** 検査立会者 46 */
    private String inspectPerson;
    /** AWB番号 47 */
    private String awbNo;
    /** ＭＡＷＢ番号 48 */
    private String cwbNo;
    /** 取　卸　港  船（取）卸港コード 54 */
    private String arrDest;
    /** 取 卸 港 船（取）卸港コード 55 */
    private String arrFromTo;
    /** 積　出　地 ( 積出地コード ) 56 */
    private String cargoFlt;
    /** 積　出　地 ( 積出地名 ) 57 */
    private String cargoCity;
    /** 積載機名 59 */
    private String loadingName;
    /** 入港年月日 60 */
    private String cargoInDate;
    /** 蔵置税関 61 */
    private String wareHouseName;
    /** 蔵置税関部門 62 */
    private String wareHouseCd;
    /** 貨物個数 63 */
    private String carryingPiece;
    /** 保税地域  (通関蔵置場コード) 65 */
    private String bondedWareHouseCd;
    /** 保税地域 ( 通関蔵置場名) 66 */
    private String bondedLocation;
    /** 貨物重量(貨物重量（グロス)) 67 */
    private String cargoWeight;
    /** 重量単位コード（グロス）68 */
    private String cargoUnitCd;
    /** 搬入予定 68 */
    private String bondedAreaCd;
    /** 蔵入等先保税地域名 69 */
    private String bondedAreaName;
    /** 最初蔵入年月日 71 */
    private String cargoInitialDate;
    /** 貿易形態別符号 73 */
    private String tradeCode;
    /** 調査用符号 74 */
    private String investCd;
    /** 貿易管理令(輸入貿易管理令第３条等識別) 76 */
    private String tradeOrder;
    /** 輸入承認証 77 */
    private String authCertify;
    /** 関税法70条関係許可承認 78 */
    private String impPermission;
    /** 共通管理番号 83 */
    private String cmManageNo;
    /**  食品 84 */
    private String foodCertify;
    /** 号食品等輸入届出受付番 85 */
    private String foodCertifiedNo;

    /** 植物防疫証明識別 86 */
    private String plantCertify;
    /** 植物防疫証明識別 87 */
    private String plantCertifiedNo;

    /** 動物検疫証明識別 88 */
    private String animalCertify;
    /** 動物検疫証明識別 89 */
    private String animalCertifiedNo;

    /** 輸入承認証等識別 91~ */
    private String extraCertName1;
    private String extraCert1;
    private String extraCert2Name;
    private String extraCert2;
    private String extraCert3Name;
    private String extraCert3;
    private String extraCert4Name;
    private String extraCert4;
    private String extraCert5Name;
    private String extraCert5;
    private String extraCert6Name;
    private String extraCert6;
    private String extraCert7Name;
    private String extraCert7;
    private String extraCert8Name;
    private String extraCert8;
    private String extraCert9Name;
    private String extraCert9;
    /** 輸入承認証等識別 107 */
    private String extraCert10Name;
    /** 輸入承認証番号等 109*/
    private String extraCert10;
    /** 仕入書番号(インボイス識別) 110 */
    private String invoiceType;
    /** インボイス番号 111 */
    private String invoiceNo;
    /** 電子インボイス受付番号 112 */
    private String elecInvoiceNo;
    /** 仕入書価格(インボイス価格区分コード) 113 */
    private String invoiceSegCd;
    /** インボイス価格条件コード 114 */
    private String invoiceConditionCd;
    /** インボイス通貨コード 115 */
    private String invoiceCurrencyCd;
    /** インボイス価格 116 */
    private String invoiceValue;
    /** 運賃 (運賃区分コード) 117 */
    private String fareClassCd;
    private String fareClassCd2;
    /** 運賃(運賃通貨コード) 118 */
    private String fareCurrencyAmount;

    /** 保険 (保険区分コード) 120 */
    private String insuranceClassCd;
    /** 保険通貨コード 121 */
    private String insuranceCurrencyCd;
    /** 保険金額 122 */
    private String insuranceAmount;
    /** 包括保険番号 123 */
    private String comPreInsuranceNumber;
    /** 通関金額 124 */
    private String clearanceType;
    /** 通関金額 125 */
    private String clearanceValue;
    /** 評価 (評価区分コード) 126 */
    private String evalClassCd;
    /** 包括評価申告受理番号 127 */
    private String reportReceiptNo1;
    /** 包括評価申告受理番号 128 */
    private String reportReceiptNo2;
    /** 包括評価申告受理番号 129 */
    private String reportReceiptNo3;

    /** 補正 (評価補正区分コード) 130 */
    private String evalCorrectClassCd;
    /** 評価補正基礎額通貨コード 131 */
    private String correctBaseCurrencyCd;
    /** 評価補正基礎額 132 */
    private String baseAmountCorrect;

    /** 評価補正補正式 133 */
    private String correctFormula;
    /** 包括評価標準式識別 134 */
    private String evalStandardIdentification1;
    /** 包括評価補正区分コード 135 */
    private String evalCateCd1;

    /** 包括評価補正式 136 */
    private String comPreEvalCorrectFormula1;
    /** 包括評価標準式識別2 137 */
    private String evalStandardIdentification2;

    /** 包括評価補正区分コード 138 */
    private String evalCateCd2;
    /** 包括評価補正式 139 */
    private String comPreEvalCorrectFormula2;
    /** 包括評価標準式識別3 140 */
    private String evalStandardIdentification3;
    /** 包括評価補正区分コード 141 */
    private String evalCateCd3;
    /** 包括評価補正式 142 */
    private String comPreEvalCorrectFormula3;
    /** 事前教示（評価）143 */
    private String preLevel1;
    /** 事前教示（評価）144 */
    private String preLevel2;
    /** ＢＰＲ金額 145 */
    private String totalTaxPrice;
    private String totalTaxPrice2;
    /** 課税価格按分係数合計入力識別 146 */
    private String totalInputIdentify;
    /** 計算方式コード 147 */
    private String computedCd;
    /** 原産地証明 148 */
    private String certOrigin;
    /** 戻税申告 149 */
    private String taxReturn;
    /** 内容点検結果 150 */
    private String inspectResult;
    /** 税科目 Cd 151 , 155 , 159 */
    private String taxSubjectCd1; // 151
    private String taxSubjectCd2; // 155
    private String taxSubjectCd3; // 159
    /** 税科目 152, 156, 160 */
    private String taxSubject1; // 152
    private String taxSubject2; // 156
    private String taxSubject3; // 160
    /** 税額合計 153, 157, 161 */
    private String totalTaxAmount1; // 153
    private String totalTaxAmount2; // 157
    private String totalTaxAmount3; // 161
    /**  税額合計欄数 154 , 158, 162 */
    private String totalTaxCnt1; // 154
    private String totalTaxCnt2; // 158
    private String totalTaxCnt3; // 162
    /** 納税額合計 179 */
    private String totalTaxPaid;
    /** 担保額 180 */
    private String collateralAmount;
    /** 通貨換算レート通貨コード 181 - 186 */
    private String conversionCurrencyCd1; // 181
    private String conversionCurrency1; // 182
    private String conversionCurrencyCd2; // 183
    private String conversionCurrency2; // 184
    private String conversionCurrencyCd3; // 185
    private String conversionCurrency3; // 186
    /** 口座 191 */
    private String accountIdentify;
    /** 都道府県 (都道府県コード) 188 */
    private String provinceCd;
    /** 納付方法 194 */
    private String paymentMethod;
    /** 構成枚数 195 */
    private String configure;
    /** 申告欄数 196 */
    private String declareCnt;

    // Footer
    /** 記事（税関）chk 197 */
    private String forCustom;

    /** 輸　入　者（入力）198 */
    private String importDealer;
    private String importDealer2;

    /** 記事（通関業者用） 199 */
    private String forCustomsAgents;

    /** 輸入取引者(入力) */
    private String importerInput;

    /** 記事（荷主） -  記事（荷主用）*/
    private String shipper;

    /** 社内整理番号 202 */
    private String companyReferenceNo;

    /** 荷主セクションコード 203 */
    private String shipperSectionCd;

    /** 荷主Ｒｅｆ Ｎｏ 204 */
    private String shipperRefNo;

    /** 利用者整理番号 205 */
    private String customerUsageNo;
    /** ［税関通知欄］税関通知欄 206 */
    private String customNoticeSection;
    /** 税関官署長名 207 */
    private String directorName;
    /** 許可・承認年月日 208 */
    private String permitDate;
    /** 審査終了年月日 209 */
    private String examDate;
    /**  事後審査識別 210 */
    private String postExam;

    /** 欄 258 */
    private String itemColumns;
    private String itemDestColumn;
    private String itemNo;
    private String itemNoUnit;
    private String priceReConfirm;
    private String itemName;
    private String taxListNo;
    private String quantity1;
    private String quantityUnit;
    private String quantity2;
    private String quantityUnit2;
    private String quantity3;
    private String quantityUnit3;
    private String quantity4;
    private String quantityUnit4;
    private String declaredValue;
    private String declaredValue2;
    private String tariffCd;
    private String tariffRate;
    private String importOrder;
    private String importOrder2;
    private String preferTreat;
    private String customsDuties;
    private String bprDivision;
    private String taxReduceAmount;
    private String taxReduceAmount2;
    private String bprAmountCd;
    private String bprAmount;
    private String exemptionTax;
    private String exemptionTax2;
    private String exemptionTax3;
    private String exemptionTax4;
    private String exemptionTax5;
    private String deCree;
    private String appendedTable;
    private String appendedTable2;
    private String appendedTable3;
    private String freightProPort;
    private String freightProPrt2;
    private String countryOri1;
    private String countryOri2;
    private String countryOri3;
    private String preTeaching;
    private String preCountryOri;

    private String domesticTax;
    private String itemClass;
    private String itemBaseTax;
    private String itemBaseTax2;
    private String itemTaxBaseQty1;
    private String itemTaxBaseQtyCd1;
    private String itemTaxBaseQty2;
    private String itemTaxBaseQtyCd2;
    private String itemTaxRate;
    private String itemAmountTax;
    private String itemReDuctedTax;
    private String itemReDuctedTaxCd;
    private String itemReducedTaxAmount1;
    private String itemArticle1;
    private String itemReducedTaxAmount2;
    private String itemArticle2;

    private String domesticTax2;
    private String itemClass2;
    private String itemBaseTax3;
    private String itemBaseTax4;
    private String itemTaxBaseQty3;
    private String itemTaxBaseQtyCd3;
    private String itemTaxBaseQty4;
    private String itemBaseQtyCd4;
    private String itemReDuctedTax2;
    private String itemAmountTax2;
    private String itemReDuctedTaxCd2;
    private String itemReducedTaxAmount3;
    private String itemArticle3;
    private String itemReducedTaxAmount4;
    private String itemArticle4;

}
