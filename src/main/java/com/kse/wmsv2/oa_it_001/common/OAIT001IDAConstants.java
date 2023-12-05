package com.kse.wmsv2.oa_it_001.common;

/**
 * The type Oait 001 ida constants.
 */
public class OAIT001IDAConstants {
//----------------------------電文作成用--------------------
    /**
     * IDA共通部レコード長
     */
    public static final int IDA_LENCOMMON = 2301;
    /**
     * IDA電文長さ
     */
    public static final int IDA_LENEXDATAREP = 383;
    /**
     * 申告番号
     */
    public static final int IDA_REPORTNO = 11;
    /**
     * 大額・少額区分
     */
    public static final int IDA_BIGSMALLFLG = 1;
    /**
     * 申告種別コード１
     */
    public static final int IDA_REPORTKINDCD_1 = 1;
    /**
     * 申告種別コード２
     */
    public static final int IDA_REPORTKINDCD_2 = 1;
    /**
     * 申告貨物識別
     */
    public static final int IDA_CARGODISC = 1;
    /**
     * 識別符号
     */
    public static final int IDA_DISCERNMENTMARK = 1;
    /**
     * 申告官署コード
     */
    public static final int IDA_REPORTDIVCD = 2;
    /**
     * 申告部門コード
     */
    public static final int IDA_REPORTDEPCD = 2;
    /**
     * 特例宛先官署コード
     */
    public static final int IDA_EXCEPTIONALDIVCD = 2;
    /**
     * 特例宛先部門コード
     */
    public static final int IDA_EXCEPTIONALDEPCD = 2;
    /**
     * 申告予定年月日
     */
    public static final int IDA_REPORTPLANINGDATE = 8;
    /**
     * 輸入者コード
     */
    public static final int IDA_IMPCUSTOMERNO = 17;
    /**
     * 輸入者名
     */
    public static final int IDA_IMPCUSTOMERNAME = 70;
    /**
     * 輸入者郵便番号
     */
    public static final int IDA_IMPPOSTCODE = 7;
    /**
     * 輸入者住所1(都道府県)
     */
    public static final int IDA_IMPCUSTOMERADD_1 = 15;
    /**
     * 輸入者住所2(市区町村)
     */
    public static final int IDA_IMPCUSTOMERADD_2 = 35;
    /**
     * 輸入者住所3(町域名・番地)
     */
    public static final int IDA_IMPCUSTOMERADD_3 = 35;
    /**
     * 輸入者住所4(ビル名他)
     */
    public static final int IDA_IMPCUSTOMERADD_4 = 70;
    /**
     * 輸入者電話番号
     */
    public static final int IDA_IMPCUSTOMERTELNO = 11;
    /**
     * 税関事務管理人コード
     */
    public static final int IDA_CUSTOMSOFFICEJANITORCD = 17;
    /**
     * 税関事務管理人受理番号
     */
    public static final int IDA_CUSTOMSOFFICEJANITORRENO = 10;
    /**
     * 税関事務管理人名
     */
    public static final int IDA_CUSTOMSOFFICEJANITORNAME = 70;
    /**
     * 蔵置場所コード
     */
    public static final int IDA_BONDEDWAREHOUSECD = 5;
    /**
     * 一括申告等種別
     */
    public static final int IDA_PACKAGEREPORT = 1;
    /**
     * 申告予定者 (コード)
     */
    public static final int IDA_REPORTPERSONCD = 5;
    /**
     * 輸入取引者コード
     */
    public static final int IDA_IMPDEALINGSCD = 17;
    /**
     * 輸入取引者名称
     */
    public static final int IDA_IMPDEALINGSNAME = 70;
    /**
     * 輸出者コード
     */
    public static final int IDA_EXPCUSTOMERNO = 12;
    /**
     * 輸出者名
     */
    public static final int IDA_EXPCUSTOMERNAME = 70;
    /**
     * 輸出者住所1
     */
    public static final int IDA_EXPCUSTOMERADD_1 = 35;
    /**
     * 輸出者住所2
     */
    public static final int IDA_EXPCUSTOMERADD_2 = 35;
    /**
     * 輸出者住所3
     */
    public static final int IDA_EXPCUSTOMERADD_3 = 35;
    /**
     * 輸出者住所4
     */
    public static final int IDA_EXPCUSTOMERADD_4 = 35;
    /**
     * 輸出者郵便番号
     */
    public static final int IDA_EXPCUSTOMERPOSTCODE = 9;
    /**
     * 輸出者国コード
     */
    public static final int IDA_EXPCUSTOMERCOUNTRY = 2;
    /**
     * 検査立会者
     */
    public static final int IDA_INSPECTIONWITNESS = 5;
    /**
     * AWBNo1
     */
    public static final int IDA_AWBNO_1 = 35;
    /**
     * AWBNo2
     */
    public static final int IDA_AWBNO_2 = 35;
    /**
     * AWBNo3
     */
    public static final int IDA_AWBNO_3 = 35;
    /**
     * AWBNo4
     */
    public static final int IDA_AWBNO_4 = 35;
    /**
     * AWBNo5
     */
    public static final int IDA_AWBNO_5 = 35;
    /**
     * 貨物個数
     */
    public static final int IDA_CARGOPIECE = 8;
    /**
     * 個数単位コード
     */
    public static final int IDA_CARGOPIECEUNITCD = 3;
    /**
     * 貨物重量
     */
    public static final int IDA_CARGOWEIGHT = 10;
    /**
     * 重量単位コード
     */
    public static final int IDA_WEIGHTUNITCD = 3;
    /**
     * 記号番号
     */
    public static final int IDA_SIGNNO = 140;
    /**
     * 積載船舶コード
     */
    public static final int IDA_CARRYSHIPCD = 9;
    /**
     * 積載船（機）名
     */
    public static final int IDA_CURRENTARRFLT = 35;
    /**
     * 入港年月日
     */
    public static final int IDA_ARRPORTDATE = 8;
    /**
     * 取卸港
     */
    public static final int IDA_GETPORT = 3;
    /**
     * 積出地コード
     */
    public static final int IDA_SHIPMENTCD = 5;
    /**
     * 積出地名
     */
    public static final int IDA_SHIPMENTPLACENAME = 20;
    /**
     * 貿易形態別符号
     */
    public static final int IDA_TRADETYPEMARK = 3;
    /**
     * コンテナ扱い本数
     */
    public static final int IDA_CONTAINERPIECE = 3;
    /**
     * 戻税申告識別
     */
    public static final int IDA_BACKTAXREPORTDISC = 1;
    /**
     * 輸入貿易管理令第３条等識別
     */
    public static final int IDA_IMPTRADECONT3DISC = 1;
    /**
     * 輸入承認証添付識別
     */
    public static final int IDA_IMPRECOATTACHDISC = 1;
    /**
     * 内容点検結果その他
     */
    public static final int IDA_CONTCHECKREOTHER = 1;
    /**
     * 税関調査用符号
     */
    public static final int IDA_CUSTOMSEXAMMARK = 5;
    /**
     * 他方令１
     */
    public static final int IDA_OTHERLAW_1 = 2;
    /**
     * 他方令2
     */
    public static final int IDA_OTHERLAW_2 = 2;
    /**
     * 他方令3
     */
    public static final int IDA_OTHERLAW_3 = 2;
    /**
     * 他方令4
     */
    public static final int IDA_OTHERLAW_4 = 2;
    /**
     * 他方令5
     */
    public static final int IDA_OTHERLAW_5 = 2;
    /**
     * 他省庁共通管理番号
     */
    public static final int IDA_OTHERMINICONTNO = 10;
    /**
     * 食品衛生証明識別
     */
    public static final int IDA_FHPROOFDISC = 1;
    /**
     * 植物防疫証明識別
     */
    public static final int IDA_PLANTPEDISC = 1;
    /**
     * 動物検疫証明識別
     */
    public static final int IDA_ANIMALQUARADISC = 1;
    /**
     * 他方令承認等識別１
     */
    public static final int IDA_OTHERLAWRECDISC_1 = 4;
    /**
     * 他方令承認等番号１
     */
    public static final int IDA_OTHERLAWRECNO_1 = 20;
    /**
     * 他方令承認等識別２
     */
    public static final int IDA_OTHERLAWRECDISC_2 = 4;
    /**
     * 他方令承認等番号２
     */
    public static final int IDA_OTHERLAWRECNO_2 = 20;
    /**
     * 他方令承認等識別３
     */
    public static final int IDA_OTHERLAWRECDISC_3 = 4;
    /**
     * 他方令承認等番号３
     */
    public static final int IDA_OTHERLAWRECNO_3 = 20;
    /**
     * 他方令承認等識別４
     */
    public static final int IDA_OTHERLAWRECDISC_4 = 4;
    /**
     * 他方令承認等番号４
     */
    public static final int IDA_OTHERLAWRECNO_4 = 20;
    /**
     * 他方令承認等識別５
     */
    public static final int IDA_OTHERLAWRECDISC_5 = 4;
    /**
     * 他方令承認等番号５
     */
    public static final int IDA_OTHERLAWRECNO_5 = 20;
    /**
     * 他方令承認等識別６
     */
    public static final int IDA_OTHERLAWRECDISC_6 = 4;
    /**
     * 他方令承認等番号６
     */
    public static final int IDA_OTHERLAWRECNO_6 = 20;
    /**
     * 他方令承認等識別７
     */
    public static final int IDA_OTHERLAWRECDISC_7 = 4;
    /**
     * 他方令承認等番号７
     */
    public static final int IDA_OTHERLAWRECNO_7 = 20;
    /**
     * 他方令承認等識別８
     */
    public static final int IDA_OTHERLAWRECDISC_8 = 4;
    /**
     * 他方令承認等番号８
     */
    public static final int IDA_OTHERLAWRECNO_8 = 20;
    /**
     * 他方令承認等識別９
     */
    public static final int IDA_OTHERLAWRECDISC_9 = 4;
    /**
     * 他方令承認等番号９
     */
    public static final int IDA_OTHERLAWRECNO_9 = 20;
    /**
     * 他方令承認等識別１０
     */
    public static final int IDA_OTHERLAWRECDISC_10 = 4;
    /**
     * 他方令承認等番号１０
     */
    public static final int IDA_OTHERLAWRECNO_10 = 20;
    /**
     * インボイス識別
     */
    public static final int IDA_INVOICEDISCERNMENT = 1;
    /**
     * 電子インボイス受付番号
     */
    public static final int IDA_INVOICERECEIPTNO = 10;
    /**
     * インボイス番号
     */
    public static final int IDA_INVOICENO = 35;
    /**
     * インボイス価格区分コード
     */
    public static final int IDA_INVOICEVALCLASSCD = 1;
    /**
     * インボイス価格条件コード
     */
    public static final int IDA_INVOICEVALCONCD = 3;
    /**
     * インボイス通貨コード
     */
    public static final int IDA_INVOICECURCD = 3;
    /**
     * インボイス価格
     */
    public static final int IDA_INVOICEVALUE = 18;
    /**
     * 運賃区分
     */
    public static final int IDA_FAREFLG = 1;
    /**
     * 運賃通貨コード
     */
    public static final int IDA_FARECURRENCYCD = 3;
    /**
     * 運賃
     */
    public static final int IDA_FARE = 16;
    /**
     * 保険区分コード
     */
    public static final int IDA_INSURANCECLASSCD = 1;
    /**
     * 保険通貨コード
     */
    public static final int IDA_INSURANCECURCD = 3;
    /**
     * 保険金額
     */
    public static final int IDA_INSURANCEAMOUNT = 14;
    /**
     * 包括保険登録番号
     */
    public static final int IDA_INCINSUREGNO = 8;
    /**
     * 評価区分コード
     */
    public static final int IDA_ESTIMATIONFLGCD = 1;
    /**
     * 包括評価申告受理番号
     */
    public static final int IDA_INCESTREPRENO = 12;
    /**
     * 包括評価申告受理番号２
     */
    public static final int IDA_INCESTREPRENO_2 = 12;
    /**
     * 包括評価申告受理番号３
     */
    public static final int IDA_INCESTREPRENO_3 = 12;
    /**
     * 評価補正区分コード
     */
    public static final int IDA_INCREVISEFLGCD = 3;
    /**
     * 評価補正基礎額通貨コード
     */
    public static final int IDA_INCREVBASECURCD = 3;
    /**
     * 事前教示（評価）１
     */
    public static final int IDA_ADVANCERULINGVALUATION_1 = 7;
    /**
     * 事前教示（評価）２
     */
    public static final int IDA_ADVANCERULINGVALUATION_2 = 7;
    /**
     * 評価補正基礎額
     */
    public static final int IDA_INCREVBASEAMO = 18;
    /**
     * 評価補正補正式
     */
    public static final int IDA_INCREVBASE = 11;
    /**
     * 課税価格按分係数合計
     */
    public static final int IDA_TAXABLESAMOPDIVTO = 18;
    /**
     * 最初蔵入等承認年月日
     */
    public static final int IDA_FIRSTCARBONDEDDATE = 8;
    /**
     * 蔵入等先保税地域コード
     */
    public static final int IDA_LONGKEEPBONDEDWAREHOUSE = 5;
    /**
     * 納期限延長コード
     */
    public static final int IDA_DELIVERYDATEEXTCD = 1;
    /**
     * BP申請事由コード
     */
    public static final int IDA_BPAPPLICATIONCD = 2;
    /**
     * 納付方法識別
     */
    public static final int IDA_PAYMETHODDISC = 1;
    /**
     * 口座番号
     */
    public static final int IDA_ACCOUNTNO = 14;
    /**
     * 担保登録番号１
     */
    public static final int IDA_SECURITYREGNO_1 = 9;
    /**
     * 担保登録番号２
     */
    public static final int IDA_SECURITYREGNO_2 = 9;
    /**
     * 記事１
     */
    public static final int IDA_NEWS_1 = 140;
    /**
     * 記事２
     */
    public static final int IDA_NEWS_2 = 70;
    /**
     * 記事（荷主用）
     */
    public static final int IDA_NEWSSHIPPER = 70;
    /**
     * 荷主セクションコード
     */
    public static final int IDA_SHIPPERSSECCD = 20;
    /**
     * 荷主リファレンスナンバー
     */
    public static final int IDA_SHIPPERSREFNO = 35;
    /**
     * 社内整理番号
     */
    public static final int IDA_INHOUSEREFNUMBER = 20;
    /**
     * 品目コード
     */
    public static final int IDA_ITEMCD = 9;
    /**
     * NACCS用コード
     */
    public static final int IDA_NACCSCD = 1;
    /**
     * 品名
     */
    public static final int IDA_ITEM = 40;
    /**
     * 原産地コード
     */
    public static final int IDA_COUNTRYOFORIGINCD = 2;
    /**
     * 原産地証明書識別
     */
    public static final int IDA_ORIGINCERTIFIDISC = 4;
    /**
     * 数量１
     */
    public static final int IDA_AMO_1 = 12;
    /**
     * 数量単位コード１
     */
    public static final int IDA_AMOUNIT_1 = 4;
    /**
     * 数量２
     */
    public static final int IDA_AMO_2 = 12;
    /**
     * 数量単位コード２
     */
    public static final int IDA_AMOUNIT_2 = 4;
    /**
     * 輸入貿易管理令別表コード
     */
    public static final int IDA_IMPTRADECONTCD = 4;
    /**
     * 蔵置種別コード
     */
    public static final int IDA_WAREHOUSECD = 1;
    /**
     * 課税価格按分係数
     */
    public static final int IDA_TAXABLESAMOPDIVTO_M = 18;
    /**
     * 運賃按分識別
     */
    public static final int IDA_FAREDIVDISC = 1;
    /**
     * FOB通貨コード
     */
    public static final int IDA_FOBCURRENCYCD = 3;
    /**
     * 課税価格
     */
    public static final int IDA_TAXABLESAMO = 18;
    /**
     * 事前教示（分類）
     */
    public static final int IDA_ADVANCERULINGCLASSIFICATION =9 ;
    /**
     * 事前教示（原産地）
     */
    public static final int IDA_ADVANCERULINGORIGIN = 7;
    /**
     * 関税減免税コード
     */
    public static final int IDA_CUSTOMSEXEMPTCD = 5;
    /**
     * 関税減免税額
     */
    public static final int IDA_CUSTOMSEXEMPTAMO = 11;
    /**
     * 内国消費税等種別コード１
     */
    public static final int IDA_INCONSTAXKINDCD_1 = 10;
    /**
     * 内国消費税等減免税コード１
     */
    public static final int IDA_INCONSTAXEXEMCD_1 = 3;
    /**
     * 内国消費税等減免額１
     */
    public static final int IDA_INCONSTAXEXEMAMO_1 = 11;
    /**
     * 内国消費税等種別コード２
     */
    public static final int IDA_INCONSTAXKINDCD_2 = 10;
    /**
     * 内国消費税等減免税コード２
     */
    public static final int IDA_INCONSTAXEXEMCD_2 = 3;
    /**
     * 内国消費税等減免額２
     */
    public static final int IDA_INCONSTAXEXEMAMO_2 = 11;
    /**
     * 内国消費税等種別コード３
     */
    public static final int IDA_INCONSTAXKINDCD_3 = 10;
    /**
     * 内国消費税等減免税コード３
     */
    public static final int IDA_INCONSTAXEXEMCD_3 = 3;
    /**
     * 内国消費税等減免額３
     */
    public static final int IDA_INCONSTAXEXEMAMO_3 = 11;
    /**
     * 内国消費税等種別コード４
     */
    public static final int IDA_INCONSTAXKINDCD_4 = 10;
    /**
     * 内国消費税等減免税コード４
     */
    public static final int IDA_INCONSTAXEXEMCD_4 = 3;
    /**
     * 内国消費税等減免額４
     */
    public static final int IDA_INCONSTAXEXEMAMO_4 = 11;
    /**
     * 内国消費税等種別コード５
     */
    public static final int IDA_INCONSTAXKINDCD_5 = 10;
    /**
     * 内国消費税等減免税コード５
     */
    public static final int IDA_INCONSTAXEXEMCD_5 = 3;
    /**
     * 内国消費税等減免額５
     */
    public static final int IDA_INCONSTAXEXEMAMO_5 = 11;
    /**
     * 内国消費税等種別コード６
     */
    public static final int IDA_INCONSTAXKINDCD_6 = 10;
    /**
     * 内国消費税等減免税コード６
     */
    public static final int IDA_INCONSTAXEXEMCD_6 = 3;
    /**
     * 内国消費税等減免額６
     */
    public static final int IDA_INCONSTAXEXEMAMO_6 = 11;





//--------------------------------------------------------
}
