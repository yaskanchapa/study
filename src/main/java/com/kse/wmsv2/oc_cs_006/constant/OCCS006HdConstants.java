package com.kse.wmsv2.oc_cs_006.constant;

public class OCCS006HdConstants {

    /** 申告入力控のイメージクラス */
    public static final String HD_IMAGECLASS = "HD";
    /** 申告入力控(輸出)S3保存先PATH */
    public static final String HD_EXP_S3PATH = "Export/HD/";
    /** 申告入力控(輸入)S3保存先PATH */
    public static final String HD_IMP_S3PATH = "Import/HD/";
    /** 申告入力控の戻り電文S3取得先PATH */
    public static final String GET_HD_S3PATH = "sim/Save/";

    // メッセージ
    public static final String HD_ERROR101 = "申告入力控作成処理中エラーが発生しました。[ERROR CODE: 101]";
    public static final String HD_ERROR102 = "取得データが0件です。[ERROR CODE: 102]";
    public static final String HD_ERROR103 = "CWBNOの取得に失敗しました。[ERROR CODE: 103]";
    public static final String HD_ERROR104 = "PDF作成用データ取得及び項目値設定に失敗しました。[ERROR CODE: 104]";
    public static final String HD_ERROR106 = "S3アップロード処理が失敗しました。[ERROR CODE: 106]";
    public static final String HD_ERROR107 = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。[ERROR CODE: 107]";
    public static final String HD_SUCCESS = "申告入力控作成が完了しました。";

    // Local用
    /** 申告入力控作成用レポートPATH */
    public static final String HD_IMP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\hdImp.rrpt";
    public static final String HD_EXP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\hdExp.rrpt";
    /** 申告入力控作成用レポート出力PATH */
    public static final String HD_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\Hd\\";

    // 結合環境
//    /** 申告入力控作成用レポートPATH */
//    public static final String HD_PATH = "/telegram/Print/Form/hdImp.rrpt";
//    /** 申告入力控作成用レポート出力PATH */
//    public static final String HD_OUT_PATH = "/telegram/Print/Report/Hd/";

}
