package com.kse.wmsv2.oc_cs_006.constant;

public class OCCS006ExConstants {

    /** 検査指定票のイメージクラス */
    public static final String EX_IMAGECLASS = "EX";
    /** 検査指定票(輸出)S3保存先PATH */
    public static final String EX_EXP_S3PATH = "Export/EX/";
    /** 検査指定票(輸入)S3保存先PATH */
    public static final String EX_IMP_S3PATH = "Import/EX/";
    /** 検査指定票の戻り電文S3取得先PATH */
    public static final String GET_EX_S3PATH = "sim/Save/";

    // メッセージ
    public static final String EX_ERROR101 = "検査指定票作成処理中エラーが発生しました。[ERROR CODE: 101]";
    public static final String EX_ERROR102 = "取得データが0件です。[ERROR CODE: 102]";
    public static final String EX_ERROR103 = "CWBNOの取得に失敗しました。[ERROR CODE: 103]";
    public static final String EX_ERROR104 = "PDF作成用データ取得及び項目値設定に失敗しました。[ERROR CODE: 104]";
    public static final String EX_ERROR106 = "S3アップロード処理が失敗しました。[ERROR CODE: 106]";
    public static final String EX_ERROR107 = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。[ERROR CODE: 107]";
    public static final String EX_SUCCESS = "検査指定票作成が完了しました。";

    // Local用
//    /** 検査指定票作成用レポートPATH */
//    public static final String EX_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\ex.rrpt";
//    /** 検査指定票作成用レポート出力PATH */
//    public static final String EX_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\Ex\\";

    // 結合環境
    /** 検査指定票作成用レポートPATH */
    public static final String EX_PATH = "/telegram/Print/Form/ex.rrpt";
    /** 検査指定票作成用レポート出力PATH */
    public static final String EX_OUT_PATH = "/telegram/Print/Report/Ex/";

}
