package com.kse.wmsv2.oc_cs_006.constant;

public class OCCS006SpRe2Constants {

    /** マニフェスト通関申告控(輸出)S3保存先PATH(SP) */
    public static final String SP_EXP_S3PATH = "Export/SP/";
    /** マニフェスト通関申告控(輸入)S3保存先PATH(RE) */
    public static final String RE_EXP_S3PATH = "Export/RE/";
    /** マニフェスト通関申告控(輸入)S3保存先PATH(SP) */
    public static final String SP_IMP_S3PATH = "Import/SP/";
    /** マニフェスト通関申告控(輸入)S3保存先PATH(RE) */
    public static final String RE_IMP_S3PATH = "Import/RE/";
    /** マニフェスト通関申告控の戻り電文S3取得先PATH */
    public static final String GET_SP_RE_S3PATH = "sim/Save/";

    // メッセージ
    public static final String SP_RE_ERROR101 = "マニフェスト通関申告控作成処理中エラーが発生しました。[ERROR CODE: 101]";
    public static final String SP_RE_ERROR102 = "取得データが0件です。[ERROR CODE: 102]";
    public static final String SP_RE_ERROR103 = "CWBNOの取得に失敗しました。[ERROR CODE: 103]";
    public static final String SP_RE_ERROR104 = "PDF作成用データ取得及び項目値設定に失敗しました。[ERROR CODE: 104]";
    public static final String SP_RE_ERROR106 = "S3アップロード処理が失敗しました。[ERROR CODE: 106]";
    public static final String SP_RE_ERROR107 = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。[ERROR CODE: 107]";
    public static final String SP_RE_ERROR108 = "一時作成PDFファイル削除に失敗しました。[ERROR CODE: 108]";
    public static final String SP_RE_SUCCESS = "マニフェスト通関申告控作成が完了しました。";

    // Local用
    /** 輸出マニフェスト通関申告控作成用レポートPATH */
    public static final String SP_RE_EXP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\spRe2Exp.rrpt";
    /** 輸入マニフェスト通関申告控作成用レポートPATH */
    public static final String SP_RE_IMP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\spRe2Imp.rrpt";
    /** マニフェスト通関申告控作成用レポート出力PATH */
    public static final String SP_RE_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\SpRe\\";

    // 結合環境
//    /** マニフェスト通関申告控作成用レポートPATH */
//    public static final String HD_PATH = "/telegram/Print/Form/hd.rrpt";
//    /** マニフェスト通関申告控作成用レポート出力PATH */
//    public static final String HD_OUT_PATH = "/telegram/Print/Report/Hd/";

}
