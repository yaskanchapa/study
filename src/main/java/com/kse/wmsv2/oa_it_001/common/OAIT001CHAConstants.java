package com.kse.wmsv2.oa_it_001.common;

/**
 * CHA電文作成に必要な情報
 */
public class OAIT001CHAConstants {
//----------------------------電文作成用--------------------
    //----------------------ヘッダ領域----------------------
    /**
     * ヘッダ領域桁数
     */
    public static final int CHA_HD_LENGTH = 57;

    /**
     * 税関官署
     */
    public static final int CHA_CUSTOMSDIV = 2;
    /**
     * MAWB番号
     */
    public static final int CHA_AWBNO = 20;
    /**
     * 孫混載表示
     */
    public static final int CHA_GRANDCHILDMIXED = 1;
    /**
     * 委託元混載業者
     */
    public static final int CHA_REQMIXEDFORWARDER = 5;
    /**
     * 到着便名1
     */
    public static final int CHA_ARRFLT_1 = 6;
    /**
     * 到着便名2
     */
    public static final int CHA_ARRFLT_2 = 5;
    /**
     * 到着空港
     */
    public static final int CHA_ARRAIRPORTCD = 3;
    /**
     * 仕出地
     */
    public static final int CHA_CATERERINGPLACE = 3;

//-----------------------明細領域-----------------------
    /**
     * 明細領域桁数
     */
    public static final int CHA_REPDATA_LENGTH = 507;
    /**
     * HAWB番号
     */
    public static final int CHA_CWBNO = 20;
    /**
     * 総個数
     */
    public static final int CHA_CARGOPIECE = 6;
    /**
     * 総重量
     */
    public static final int CHA_CARGOWEIGHT = 8;
    /**
     * 重量単位コード
     */
    public static final int CHA_WEIGHTUNITCD = 3;
    /**
     * 品名
     */
    public static final int CHA_ITEM = 21;
    /**
     * 特殊貨物記号
     */
    public static final int CHA_SPECIALCARGOSIGN = 3;
    /**
     * 仕向地
     */
    public static final int CHA_DESTINATION = 3;
    /**
     * 搬入保税蔵置場
     */
    public static final int CHA_CARBONDEDWAREHOUSE = 5;
    /**
     * 管理資料計上表示
     */
    public static final int CHA_MANAGEDDATAFLG = 1;
    /**
     * 混載仕立業
     */
    public static final int CHA_MIXEDFORWARDER = 3;
    /**
     * 荷送人名
     */
    public static final int CHA_EXPCUSTOMERNAME = 70;
    /**
     * 荷送人住所
     */
    public static final int CHA_EXPCUSTOMERADD_LUMP = 105;
    /**
     * 荷送人電話番号
     */
    public static final int CHA_EXPCUSTOMERTELNO = 14;
    /**
     * 荷受人コード
     */
    public static final int CHA_IMPCUSTOMERNO = 17;
    /**
     * 荷受人名
     */
    public static final int CHA_IMPCUSTOMERNAME = 70;
    /**
     * 荷受人住所
     */
    public static final int CHA_IMPCUSTOMERADD_LUMP = 105;
    /**
     * 荷受人電話番号
     */
    public static final int CHA_IMPCUSTOMERTELNO = 14;
    /**
     * 訂正理由コード
     */
    public static final int CHA_IMPCORRECTION_REASON = 3;
    /**
     * 訂正理由：削除
     */
    public static final String CHA_IMPCORRECTION_REASON_CD = "DEL";
    /**
     * 予備
     */
    public static final int CHA_DUMMY_01 = 1;
//------------------------------------------------------

}
