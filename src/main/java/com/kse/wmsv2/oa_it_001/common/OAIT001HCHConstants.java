package com.kse.wmsv2.oa_it_001.common;

/**
 * HCH電文作成に必要な情報
 */
public class OAIT001HCHConstants {
//-------------------------------------ヘッダ-------------------------------------------
    /**
     * ヘッダ領域桁数
     */
    public static final int HCH_HD_LENGTH = 64;
    /**
     * 委託元混載業者
     */
    public static final int HCH_REQMIXEDFORWARDER = 5;
    /**
     * 税関官署
     */
    public static final int HCH_CUSTOMSDIV = 2;
    /**
     * MAWB番号
     */
    public static final int HCH_AWBNO = 20;
    /**
     * 孫混載表示
     */
    public static final int HCH_GRANDCHILDMIXED = 1;
    /**
     * 到着便名1
     */
    public static final int HCH_ARRFLT_1 = 6;
    /**
     * 到着便名2
     */
    public static final int HCH_ARRFLT_2 = 5;
    /**
     * 到着空港
     */
    public static final int HCH_ARRAIRPORTCD = 3;
    /**
     * 仕出地
     */
    public static final int HCH_CATERERINGPLACE = 3;
    /**
     * ジョイント混載
     */
    public static final int HCH_JOINT = 1;


//-------------------------------------明細-----------------------------------------------
    /**
     * 明細桁数
     */
    public static final int HCH_REPDATA_LENGTH = 502;
    /**
     * HAWB番号
     */
    public static final int HCH_CWBNO = 20;
    /**
     * 総個数
     */
    public static final int HCH_CARGOPIECE = 6;
    /**
     * 総重量
     */
    public static final int HCH_CARGOWEIGHT = 8;
    /**
     * 重量単位コード
     */
    public static final int HCH_WEIGHTUNITCD = 3;
    /**
     * 品名
     */
    public static final int HCH_ITEM = 21;
    /**
     * 特殊貨物記号
     */
    public static final int HCH_SPECIALCARGOSIGN = 3;
    /**
     * 仕向地
     */
    public static final int HCH_DESTINATION = 3;
    /**
     * 搬入保税蔵置場
     */
    public static final int HCH_CARBONDEDWAREHOUSE = 5;
    /**
     * 管理資料計上表示
     */
    public static final int HCH_MANAGEDDATAFLG = 1;
    /**
     * 混載仕立業
     */
    public static final int HCH_MIXEDFORWARDER = 3;
    /**
     * 荷送人名
     */
    public static final int HCH_EXPCUSTOMERNAME = 70;
    /**
     * 荷送人住所
     */
    public static final int HCH_EXPCUSTOMERADD_LUMP = 105;
    /**
     * 荷送人電話番号
     */
    public static final int HCH_EXPCUSTOMERTELNO = 14;
    /**
     * 荷受人コード
     */
    public static final int HCH_IMPCUSTOMERNO = 17;
    /**
     * 荷受人名
     */
    public static final int HCH_IMPCUSTOMERNAME = 70;
    /**
     * 荷受人住所
     */
    public static final int HCH_IMPCUSTOMERADD_LUMP = 105;
    /**
     * 荷受人電話番号
     */
    public static final int HCH_IMPCUSTOMERTELNO = 14;





}
