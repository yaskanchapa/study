package com.kse.wmsv2.oa_ew_001.common;

import java.math.BigInteger;

public class OAEW001CommonConstants {
    // Result Status
    public static final String RESULT_ERROR = "error";

    public static final String RESULT_SUCCESS = "false";
    public static final String RESULT_WARNING = "warning";


    // DB用
    public static final String STATUS_CD_BIL_REPORT = "EB00210";
    public static final String IMPORT_EXPORT_CLASS = "2";
    public static final String BUSINESS_CLASS = "02";
    public static final String MESSAGE_TYPE = "BIL01";
    public static final String INIT_CWBNODEPTCD = "000";


    // 電文用(共通用)
    public static final String COMMON_LENCABLE = "6";
    public static final String DEFAULT_LINK_DATA_CLASS = "0";

    public static final int COMMON_LENCOMMONCABLE = 400;
    public static final String SIM_AUTO = "SIMAUTO";
    public static final String SIM_AUTO_BK = "SIMAUTOBK";

    public static final String BIL = "BIL";



    //　message
    public static final String MSG_SUC_001 = "検索しました。";
    public static final String MSG_WAR_001 = "検索結果が「0件」です。検索条件又はログインユーザをご確認ください。";
    public static final String MSG_ERR_001 = "検索処理でエラーが発生しました。[ERROR : 001]";
    public static final String MSG_ERR_351 = "電文作成対象データが見当たりませんでした。[ERROR : 351]";
    public static final String MSG_ERR_012 = "CS_SEND_MESSAGEデータ登録処理でエラーが発生しました。[ERROR:012]";




}
