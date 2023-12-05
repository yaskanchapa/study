package com.kse.wmsv2.oa_it_001.common;

/**
 * MIC電文作成に必要な情報
 */
public class OAIT001MICConstants {
//----------------------------電文作成用--------------------
    /**
     * MIC電文長さ
     */
    public static final int MIC_LENCOMMON = 1264;
    /**
     * 申告番号
     */
    public static final int MIC_REPORTNO = 11;
    /**
     * 申告条件
     */
    public static final int MIC_REPORTCONDITION = 1;
    /**
     * 申告先種別コード
     */
    public static final int MIC_REPORTKINDCD_2 = 1;
    /**
     * 識別符号
     */
    public static final int MIC_DISCERNMENTMARK = 1;
    /**
     * あて先官署コード
     */
    public static final int MIC_REPORTDIVCD = 2;
    /**
     * あて先部門コード
     */
    public static final int MIC_REPORTDEPCD = 2;
    /**
     * 申告予定年月日
     */
    public static final int MIC_REPORTPLANINGDATE = 8;
    /**
     * 輸入者コード
     */
    public static final int MIC_IMPCUSTOMERNO = 17;
    /**
     * 輸入者名
     */
    public static final int MIC_IMPCUSTOMERNAME = 70;
    /**
     * 郵便番号
     */
    public static final int MIC_IMPPOSTCODE = 7;
    /**
     * 住所１（都道府県）
     */
    public static final int MIC_IMPCUSTOMERADD_1 = 15;
    /**
     * 住所２（市区町村（行政区名））
     */
    public static final int MIC_IMPCUSTOMERADD_2 = 35;
    /**
     * 住所３（町域名・番地）
     */
    public static final int MIC_IMPCUSTOMERADD_3 = 35;
    /**
     * 住所４（ビル名ほか）
     */
    public static final int MIC_IMPCUSTOMERADD_4 = 70;
    /**
     * 輸入者電話番号
     */
    public static final int MIC_IMPCUSTOMERTELNO = 11;
    /**
     * 輸入者住所
     */
    public static final int MIC_IMPCUSTOMERADD_LUMP = 105;
    /**
     * 税関事務管理人コード
     */
    public static final int MIC_CUSTOMSOFFICEJANITORCD = 17;
    /**
     * 税関事務管理人受理番号
     */
    public static final int MIC_CUSTOMSOFFICEJANITORRENO = 10;
    /**
     * 税関事務管理人名
     */
    public static final int MIC_CUSTOMSOFFICEJANITORNAME = 70;
    /**
     * 通関予定蔵置場コード
     */
    public static final int MIC_BONDEDWAREHOUSECD = 5;
    /**
     * 検査立会者
     */
    public static final int MIC_INSPECTIONWITNESS = 5;
    /**
     * 仕出人コード
     */
    public static final int MIC_EXPCUSTOMERNO = 12;
    /**
     * 仕出人名
     */
    public static final int MIC_EXPCUSTOMERNAME = 70;
    /**
     * 住所１(Street&Number/P.O.BOX)
     */
    public static final int MIC_EXPCUSTOMERADD_1 = 35;
    /**
     * 住所２(Street&Number/P.O.BOX)
     */
    public static final int MIC_EXPCUSTOMERADD_2 = 35;
    /**
     * 住所３(CityName)
     */
    public static final int MIC_EXPCUSTOMERADD_3 = 35;
    /**
     * 住所４(Country sub-Entity,Name)
     */
    public static final int MIC_EXPCUSTOMERADD_4 = 35;
    /**
     * 郵便番号（Postcodeidentification）
     */
    public static final int MIC_EXPCUSTOMERPOSTCODE = 9;
    /**
     * 国名コード（Country,coded）
     */
    public static final int MIC_EXPCUSTOMERCOUNTRY = 2;
    /**
     * HAWB番号
     */
    public static final int MIC_EXPCUSTOMERADD_LUMP = 105;
    /**
     * MAWB番号
     */
    public static final int MIC_CWBNO = 35;
    /**
     * 貨物個数
     */
    public static final int MIC_AWBNO = 35;
    /**
     * 貨物重量
     */
    public static final int MIC_CARGOPIECE = 6;
    /**
     * 積載機名
     */
    public static final int MIC_CARGOWEIGHT = 8;
    /**
     * 積載機名
     */
    public static final int MIC_CURRENTARRFLT = 12;
    /**
     * 入港年月日
     */
    public static final int MIC_ARRPORTDATE = 8;
    /**
     * 取卸港コード
     */
    public static final int MIC_GETPORT = 3;
    /**
     * 積出地コード
     */
    public static final int MIC_SHIPMENTCD = 5;
    /**
     * インボイス価格区分コード
     */
    public static final int MIC_INVOICEVALCLASSCD = 1;
    /**
     * インボイス価格条件コード
     */
    public static final int MIC_INVOICEVALCONCD = 3;
    /**
     * インボイス通貨コード
     */
    public static final int MIC_INVOICECURCD = 3;
    /**
     * インボイス価格
     */
    public static final int MIC_INVOICEVALUE = 13;
    /**
     * 運賃区分コード
     */
    public static final int MIC_FAREFLG = 1;
    /**
     * 運賃通貨コード
     */
    public static final int MIC_FARECURRENCYCD = 3;
    /**
     * 運賃
     */
    public static final int MIC_FARE = 11;
    /**
     * 保険区分コード
     */
    public static final int MIC_INSURANCECLASSCD = 1;
    /**
     * 保険通貨コード
     */
    public static final int MIC_INSURANCECURCD = 3;
    /**
     * 保険金額
     */
    public static final int MIC_INSURANCEAMOUNT = 9;
    /**
     * 品名
     */
    public static final int MIC_ITEM = 40;
    /**
     * 原産地コード
     */
    public static final int MIC_COUNTRYOFORIGINCD = 2;
    /**
     * 課税価格
     */
    public static final int MIC_TAXABLESAMO = 6;
    /**
     * 記事
     */
    public static final int MIC_NEWS_2 = 35;
    /**
     * 荷主セクションコード
     */
    public static final int MIC_SHIPPERSSECCD = 20;
    /**
     * 荷主リファレンスナンバー
     */
    public static final int MIC_SHIPPERSREFNO = 35;
    /**
     * 社内整理番号
     */
    public static final int MIC_INHOUSEREFNUMBER = 20;
//--------------------------------------------------------
}
