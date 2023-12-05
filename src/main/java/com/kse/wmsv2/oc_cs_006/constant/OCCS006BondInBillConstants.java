package com.kse.wmsv2.oc_cs_006.constant;

public class OCCS006BondInBillConstants {
    // 搬入伝票
    /** 搬入伝票のイメージクラス */
    public static final String BONDINBILL_IMAGECLASS = "BI";
    /** 搬入伝票S3PATH */
    public static final String BONDINBILL_S3_PATH = "Export/BI/";
    /** 搬入伝票の戻り電文S3取得先PATH */
    public static final String GET_BONDINBILL_S3PATH = "sim/Save/";

    // メッセージ
    public static final String BONDINBILL_ERROR101 = "搬入伝票作成処理中エラーが発生しました。[ERROR CODE: 101]";
    public static final String BONDINBILL_ERROR102 = "取得データが0件です。[ERROR CODE: 102]";
    public static final String BONDINBILL_ERROR103 = "搬入伝票番号の取得に失敗しました。[ERROR CODE: 103]";
    public static final String BONDINBILL_ERROR104 = "PDF作成用データ取得及び項目値設定に失敗しました。[ERROR CODE: 104]";
    public static final String BONDINBILL_ERROR105 = "AE_DATAテーブルの更新処理が失敗しました。[ERROR CODE: 105]";
    public static final String BONDINBILL_ERROR106 = "S3アップロード処理が失敗しました。[ERROR CODE: 106]";
    public static final String BONDINBILL_ERROR107 = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。[ERROR CODE: 107]";
    public static final String BONDINBILL_SUCCESS = "搬入伝票作成が完了しました。";


    // Local用 Path
//    /** 搬入伝票作成用レポートPATH */
//    public static final String BONDINBILL_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\bondInBill.rrpt";
//    /** 搬入伝票作成用レポート出力PATH */
//    public static final String BONDINBILL_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\BondInBill\\";

    // 結合環境 Path
    /** 搬入伝票作成用レポートPATH */
    public static final String BONDINBILL_PATH = "/telegram/Print/Form/bondInBill.rrpt";
    /** 搬入伝票作成用レポート出力PATH */
    public static final String BONDINBILL_OUT_PATH = "/telegram/Print/Report/BondInBill/";

}
