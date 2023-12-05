package com.kse.wmsv2.oa_et_001.common;

import java.math.BigInteger;

public class OAET001CommonConstants {
    //電文長
    public static final int COMMON_LENCABLE  = 6;
    //共通部電文長
    public static final int COMMON_LENCOMMONCABLE  = 400;

    // リスト取得
    public static final String CUSTOMS_CLASS = "0028";
    public static final String WAREHOUSE_CLASS = "0029";
    public static final String PORT_CLASS = "0030";
    public static final String AGENCY_CLASS = "0404";

    public static final String DEFAULT_CWB_DEPT_CD = "000";

    public static final String DEFAULT_BWB_NO = "00000000000";

    public static final String DEFAULT_LINK_DATA = "0";

    public static final String EXPORT_BUSINESS_CD = "02";

    public static final BigInteger INVOICE_MAX_VALUE_1 = new BigInteger("500000");

    public static final BigInteger INVOICE_MAX_VALUE_2 = new BigInteger("999999999999999");

    //
    public static final String RESULT_ERROR = "error";

    public static final String RESULT_TRUE = "true";

    public static final String RESULT_WARNING = "warning";

    public static final String RESULT_SUCCESS = "false";

    public static final String RESULT_ERROR_FLAG = "errFlg";

    public static final String RESULT_MESSAGE = "message";



    // 業務コード
    public static final String EDA_REGISTER = "EC00400";
    public static final String MEC_REGISTER = "EC00500";
    public static final String LINK_REGISTER = "EC00600";
    public static final String CDB_COMPLETE_WRITE = "EC00700";
    public static final String BII_REPORT = "EC00705";
    public static final String CDB_RECALL = "EC00710";
    public static final String BIL_WRITE = "EC00720";
    public static final String IMPOSTER_CHECK = "EC00800";
    public static final String MEC_WRITE = "EC00900";
    public static final String EDA_WRITE = "EC01000";
    public static final String EDC_WRITE = "EC01100";
    public static final String MEC_MASTER = "EC01210";
    public static final String MEC_SUB = "EC012200";
    public static final String EDC_MASTER = "EC01310";
    public static final String EDC_SUB = "EC01320";
    public static final String INSPECTION = "EC01400";
    public static final String EXPORT_PERMIT = "EC01500";
    public static final String DOC_REPORT = "EC01600";
    public static final String DOC_PERMIT = "EC01700";
    public static final String NO_REPORT = "EC90010";
    public static final String NO_REPORT_UNLOCK = "EC90020";

    public static final String LINK_REGISTER_BON = "EB00100";

    // Error Message

    public static final String MSG_ERR_001 = "MAWB番号で検索する場合は、「トラック便日付」と「トラック便」は必須項目です。[ERROR : 001]";

    public static final String MSG_ERR_002 = "検索処理でエラーが発生しました。[ERROR:002]";

    public static final String MSG_ERR_003 = "検索処理でエラーが発生しました。[ERROR:003]";

    public static final String MSG_ERR_004 = "ヘッダデータ登録処理でエラーが発生しました。[ERROR:004]";

    public static final String MSG_ERR_005 = "削除処理でエラーが発生しました。[ERROR:005]";

    public static final String MSG_ERR_006 = "Excelファイル取込処理でエラーが発生しました。管理者にお問合せ下さい。[ERROR:006]";

    public static final String MSG_ERR_007 = "「MAWB No」は20桁以下で入力して下さい。[ERROR:007]";

    public static final String MSG_ERR_008 = "アップロード可能ファイルは「.xlsx」又は「.xls」のみです。[ERROR:008]";

    public static final String MSG_ERR_009 = "デフォルト値を選択してください[ERROR:009]";

    public static final String MSG_ERR_010 = "マニフェストファイルに重複データが存在します。[ERROR:010]";

    public static final String MSG_ERR_011 = "データ更新に失敗しました。管理者にお問い合わせ下さい。[ERROR:011]";

    public static final String MSG_ERR_012 = "入力した通貨コードのレート情報がないです。管理者にお問い合わせください。[ERROR : 012]";


    public static final String MSG_ERR_901 = "処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 901]";

    public static final String MSG_ERR_902 = "MEC電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 902]";

    public static final String MSG_ERR_903 = "EDC電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 903]";

    public static final String MSG_ERR_904 = "EDA電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 904]";
    public static final String MSG_ERR_905 = "EDA電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 905]";

    public static final String MSG_ERR_906 = "EDA電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 906]";
    public static final String MSG_ERR_907 = "EDC電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 907]";
    public static final String MSG_ERR_908 = "MEC電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 908]";

    public static final String MSG_ERR_909 = "電文作成処理中エラーが発生しました。。管理者にお問合せ下さい。[ERROR : 909]";

    public static final String MSG_ERR_910 = "CDB電文作成処理中エラーが発生しました。管理者にお問合せ下さい。[ERROR : 910]";

    public static final String MSG_ERR_101 = "データ更新中エラーが発生しました。管理者にお問合せ下さい。[ERROR：101]";

    public static final String MSG_ERR_201 = "MECデータ登録処理でエラーが発生しました。[ERROR：201]";

    public static final String MSG_ERR_202 = "HAWB番号は必須です。[ERROR：202]";

    public static final String MSG_ERR_203 = "輸出者電話番号は11桁以下です。[ERROR：203]";

    public static final String MSG_ERR_204 = "選択した業者はマニ不可です。[ERROR：204]";

    public static final String MSG_ERR_205 = "MECデータ保存処理でエラーが発生しました。[ERROR：205]";
    public static final String MSG_ERR_301 = "EDAデータ登録処理でエラーが発生しました。[ERROR：301]";
    public static final String MSG_ERR_302 = "HAWB番号は必須です。[ERROR：302]";

    public static final String MSG_ERR_303 = "輸出者電話番号は11桁以下です。[ERROR：303]";

    public static final String MSG_ERR_304 = "選択した業者はマニ不可です。[ERROR：304]";

    public static final String MSG_ERR_305 = "EDAデータ保存処理でエラーが発生しました。[ERROR：305]";




    // Success Message
    public static final String MSG_SUC_001 = "データを更新しました。";

    public static final String MSG_SUC_002 = "データを登録しました。";

    public static final String MSG_SUC_003 = "データを削除しました。";

    public static final String MSG_SUC_004 = "データを検索しました。";

    public static final String MSG_SUC_005 = "EXCELファイルを正常に登録しました。";

    public static final String MSG_SUC_006 = "電文ファイル作成を正常に完了しました。";

    public static final String MSG_SUC_007 = "件のデータを更新しました。再検索して結果確認お願いします。";

    public static final String MSG_SUC_008 = "選択したデータを削除しました。再検索して結果を確認して下さい。";

    public static final String MSG_SUC_009 = "データを更新しました。";

    public static final String MSG_SUC_201 = "MECデータを登録しました。";

    public static final String MSG_SUC_202 = "MECデータを保存しました。";

    public static final String MSG_SUC_301 = "EDAデータを登録しました。";

    public static final String MSG_SUC_302 = "EDAデータを保存しました。";

    // Warning Message
    public static final String MSG_WAR_001 = "既に同じMAWB番号でデータが存在します。既存データをすべて削除して新しく登録しますか？";

    public static final String MSG_WAR_002 = "対象データがありません。入力値を確認して下さい";

    public static final String MSG_WAR_003 = "検索結果が「0件」です。";

    public static final String MSG_WAR_201 = "既に編集済みのデータです。登録処理を実行します。";

    public static final String MSG_WAR_202 = "既に通関チェック済みのデータです。訂正処理を実行します。";

    public static final String MSG_WAR_301 = "既に編集済みのデータです。登録処理を実行します。";

    public static final String MSG_WAR_302 = "既に通関チェック済みのデータです。訂正処理を実行します。";

    public static final String MSG_WAR_303 = "指定した日付で対象データが0件です。[WARNING：303]";

}
