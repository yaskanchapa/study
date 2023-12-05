package com.kse.wmsv2.oa_it_001.common;

import java.math.BigDecimal;
import java.math.BigInteger;

public class OAIT001CommonConstants {

    public static final String RESULT_ERROR = "error";

    public static final String RESULT_TRUE = "true";

    public static final String RESULT_WARNING = "warning";

    public static final String RESULT_SUCCESS = "false";

    public static final String RESULT_ERROR_FLAG = "errFlg";

    public static final String RESULT_MESSAGE = "message";

    public static final String BUSINESS_CLASS_IMPORT = "01";

    public static final String DEFAULT_LINK_DATA_CLASS = "0";

    //電文長
    public static final int COMMON_LENCABLE  = 6;
    //共通部電文長
    public static final int COMMON_LENCOMMONCABLE  = 400;

    public static final String EXCEL_CONTENT_TYPE_1 = "xls";

    public static final String EXCEL_CONTENT_TYPE_2 = "xlsx";

    public static final int IMPORT_EXCEL_LIMIT_CELL_LENGTH = 19;

    public static final int EXPORT_EXCEL_LIMIT_CELL_LENGTH = 21;

    public static final String SPS_DOC_FLAG = "1";

    public static final String IMP_TYPE = "2";

    public static final String OCS = "OCS";
    public static final String INVOICE_VALUE_CD_CIF = "CIF";

    public static final String INVOICE_VALUE_CD_CF = "C&F";

    public static final String INVOICE_VALUE_CD_CI = "C&I";

    public static final String INVOICE_VALUE_CD_FOB = "FOB";

    public static final String CURRENCY_CODE_JPY = "JPY";

    public static final String IDA_TRUE = "1";

    public static final String IDA_FALSE = "0";

    public static final String REPORT_CONDITION_U = "U";

    public static final int MAX_TAXABLE_AMOUNT = 999999;

    public static final String STATUS_CD_LINK = "IC00100";

    public static final String STATUS_CD_EDIT_MIC = "IC00500";

    public static final String STATUS_CD_EDIT_IDA = "IC00600";

    public static final String STATUS_CD_IDA_REPORT = "IC00800";

    public static final String STATUS_CD_CHA_REPORT = "IC00310";

    public static final String STATUS_CD_HCH_REPORT = "IC00300";

    public static final String STATUS_CD_HDM_REPORT = "IC00301";

    public static final String STATUS_CD_MIC_REPORT = "IC00700";

    public static final String STATUS_CD_IDC_REPORT = "IC00900";

    public static final String CARGO_SPECIAL = "緊急";

    public static final String CARGO_REQUEST = "請求";

    public static final String CARGO_NO_TAX = "無税";

    public static final String CARGO_MANIFEST = "マニ申告";

    public static final String CARGO_NOT_ALLOW = "未許可";

    public static final String CARGO_NORMAL_INSPECTION = "検査";

    public static final String CARGO_SU_INSPECTION = "S/U検査";

    public static final String CARGO_SPECIAL_ALLOW = "許可緊急";

    public static final String CARGO_REQUEST_ALLOW = "許可請求";

    public static final String CARGO_NO_TAX_ALLOW = "許可無税";

    public static final String CARGO_S_IN = "Ｓ搬入";

    public static final String CARGO_S_NO_TAX = "Ｓ無税";

    public static final String CARGO_S_MANIFEST = "Sマニ申告";

    public static final BigInteger INVOICE_MAX_VALUE_1 = new BigInteger("201000");

    public static final BigInteger INVOICE_MAX_VALUE_2 = new BigInteger("999999999999999");

    public static final String WEIGHT_UNIT_LBR = "LBR";

    public static final String WEIGHT_UNIT_KGM = "KGM";

    public static final double LBR_VALUE = 0.45359;

    // MESSAGE区分　
    // ERR:エラー　SUC:正常
    // 1 : SCREEN
    // 2 : MIC
    // 3 : IDA

    public static final String MSG_SUC_001 = "検索しました。";

    public static final String MSG_SUC_002 = "選択したデータを更新しました。";

    public static final String MSG_SUC_003 = "件のデータを更新しました。";

    public static final String MSG_SUC_004 = "ヘッダデータを更新しました。再検索して結果を確認して下さい。";

    public static final String MSG_SUC_005 = "電文ファイル作成を完了しました。";

    public static final String MSG_SUC_006 = "データを削除しました。再検索して結果を確認して下さい。";

    public static final String MSG_SUC_007 = "データを更新しました。";

    public static final String MSG_SUC_201 = "MICデータを登録しました。";

    public static final String MSG_SUC_202 = "MICデータを保存しました。";

    public static final String MSG_SUC_301 = "IDAデータを登録しました。";

    public static final String MSG_SUC_302 = "IDAデータを保存しました。";



    public static final String MSG_WAR_001 = "検索結果が0件です。";

    public static final String MSG_WAR_002 = "更新結果が0件です。";

    public static final String MSG_WAR_003 = "既に同じMAWB番号でデータが存在します。既存データをすべて削除して新しく登録しますか？";

    public static final String MSG_WAR_004 = "他のユーザによって変更されたデータです。もう一度検索して確認ください。";

    public static final String MSG_ERR_001 = "検索処理でエラーが発生しました。[ERROR : 001]";

    public static final String MSG_ERR_002 = "当MAWBのヘッダ情報がないです。[ERROR : 002]";

    public static final String MSG_ERR_003 = "電文ファイル作成処理でエラーが発生しました。[ERROR : 003]";

    public static final String MSG_ERR_004 = "電文ファイル作成処理でエラーが発生しました。[ERROR : 004]";

    public static final String MSG_ERR_005 = "データ削除処理でエラーが発生しました。[ERROR : 005]";

    public static final String MSG_ERR_006 = "マニフェストファイルに重複データが存在します。[ERROR:006]";

    public static final String MSG_ERR_007 = "「MAWB No」は20桁以下で入力して下さい。[ERROR:007]";

    public static final String MSG_ERR_008 = "アップロード可能ファイルは「.xlsx」又は「.xls」のみです。[ERROR:008]";

    public static final String MSG_ERR_009 = "デフォルト値を選択してください[ERROR:009]";

    public static final String MSG_ERR_010 = "データ更新に失敗しました。管理者にお問い合わせ下さい。[ERROR:010]";


    public static final String MSG_SUC_101 = "EXCELファイルデータを正常に登録しました。";

    public static final String MSG_ERR_102 = "EXCELファイル登録エラーが発生しました。[ERROR : 102]";

    public static final String MSG_ERR_103 = "EXCELファイル登録エラーが発生しました。[ERROR : 103]";

    public static final String MSG_ERR_104 = "EXCELファイル登録エラーが発生しました。[ERROR : 104]";

    public static final String MSG_ERR_105 = "EXCELファイル登録エラーが発生しました。[ERROR : 105]";

    public static final String MSG_ERR_106 = "データ更新処理でエラーが発生しました。[ERROR : 106]";

    public static final String MSG_ERR_107 = "データ更新処理でエラーが発生しました。[ERROR : 107]";

    public static final String MSG_ERR_108 = "ヘッダデータ更新処理でエラーが発生しました。[ERROR : 108]";

    public static final String MSG_ERR_109 = "EXCELファイル登録エラーが発生しました。[ERROR : 109]";

    public static final String MSG_ERR_110 = "入力した申告条件の値を確認して下さい。[ERROR : 110]";

    public static final String MSG_ERR_111 = "搬入処理が終わった貨物が存在するため登録できないです。[ERROR : 111]";

    public static final String MSG_ERR_112 = "セールに禁止文字が含まれています。[ERROR : 112]";

    public static final String MSG_ERR_201 = "MICデータ検索でエーらが発生しました。[ERROR : 201]";

    public static final String MSG_ERR_202 = "MICデータ登録でエーらが発生しました。[ERROR : 202]";

    public static final String MSG_ERR_203 = "IDAデータ保存でエーらが発生しました。[ERROR : 203]";

    public static final String MSG_ERR_204 = "「MAWB NO.」を入力してください。[ERROR : 204]";

    public static final String MSG_ERR_205 = "「FLT」を選択してください。[ERROR : 205]";

    public static final String MSG_ERR_301 = "IDA電文を作成済みのデータです。[ERROR : 301]";
    public static final String MSG_ERR_302 = "AI_DATA更新処理でエラーが発生しました。[ERROR : 302]";
    public static final String MSG_ERR_303 = "AI_DATA_DETAIL更新処理でエラーが発生しました。[ERROR : 303]";

    public static final String MSG_ERR_304 = "特別手配更新処理でエラーが発生しました。[ERROR : 304]";

    public static final String MSG_ERR_305 = "ヘッダデータ更新処理でエラーが発生しました。[ERROR : 305]";

    public static final String MSG_ERR_306 = "IDAデータ検索処理でエーらが発生しました。[ERROR : 306]";

    public static final String MSG_ERR_307 = "IDAデータ登録処理でエーらが発生しました。[ERROR : 307]";

    public static final String MSG_ERR_308 = "IDA電文作成処理でエーらが発生しました。[ERROR : 308]";

    public static final String MSG_ERR_309 = "IDAデータ保存処理でエーらが発生しました。[ERROR : 309]";

    public static final String MSG_ERR_351 = "電文作成対象データがないです。[ERROR : 351]";

    public static final String MSG_ERR_401 = "HAWB登録作業でエラーが発生しました。管理者にお問い合わせください。[ERROR : 401]";

    public static final String MSG_ERR_501 = "「インボイス価格条件」が「CIF」の場合は「運賃欄：入力不可」、「保険区分コード：入力不可」です。[ERROR : 501]";

    public static final String MSG_ERR_502 = "「インボイス価格条件」が「C&F」の場合は「保険区分コード：必須入力」、「運賃欄：入力不可」です。[ERROR : 502]";

    public static final String MSG_ERR_503 = "「インボイス価格条件」が「C&I」の場合は「保険区分コード：入力不可」、「運賃欄：必須入力」です。[ERROR : 503]";

    public static final String MSG_ERR_504 = "「識別符号」が「1」の場合は「輸入者コード欄：必須入力」です。[ERROR : 504]";

    public static final String MSG_ERR_505 = "「識別符号」が「2」又は「3」の場合は「輸入者コード欄：入力不可」です。[ERROR : 505]";

    public static final String MSG_ERR_506 = "入力した通貨コードのレート情報がないです。管理者にお問い合わせください。[ERROR : 506]";

    public static final String MSG_ERR_507 = "申告予定日を入力してください。[ERROR : 507]";

    public static final String MSG_ERR_998 = "貨物重量又は貨物個数に値がないHAWBが存在します。HAWB番号：";
    public static final String MSG_ERR_999 = "「MAWB」、「HAWB」、「HAWB部門コード」は必須項目です。";


}
