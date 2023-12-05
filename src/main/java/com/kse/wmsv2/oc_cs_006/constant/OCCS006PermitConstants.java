package com.kse.wmsv2.oc_cs_006.constant;


//輸入許可書 DTO

// IMAGECLASS = RP
// PATTERN = I2R
// FILENAME = AAD1AG21EKKJ20230201094736040238

public class OCCS006PermitConstants {

    public static final String RP_IMAGECLASS = "RP";
    public static final String PE_IMAGECLASS = "PE";
    /** 輸入許可書S3保存先PATH */
    public static final String RP_IMP_S3PATH = "Import/RP/";
    public static final String PE_IMP_S3PATH = "Import/PE/";
    public static final String RP_EXP_S3PATH = "Export/RP/";
    public static final String PE_EXP_S3PATH = "Export/PE/";

    /** 輸入許可書定表の戻り電文S3取得先PATH */
    public static final String GET_RP_IMP_S3PATH = "sim/Save/";
    public static final String GET_PE_IMP_S3PATH = "sim/Save/";

    public static final String GET_RP_EXP_S3PATH = "sim/Save/";
    public static final String GET_PE_EXP_S3PATH = "sim/Save/";



    // Local用
    /** 輸入許可書作成用レポートPATH */

    public static final String RP_IMP_PATH = "/Volumes/Telegram/Print/Form/permitImpRp.rrpt";
    public static final String PE_IMP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\permitImpPe.rrpt";
    /** 輸入許可書作成用レポート出力PATH */
    public static final String RP_IMP_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\RpImp\\";
    public static final String PE_IMP_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\PeImp\\";

    public static final String RP_EXP_PATH = "/Volumes/Telegram/Print/Form/permitExpRp.rrpt";
    public static final String PE_EXP_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Form\\permitExpPe.rrpt";
    /** 輸入許可書作成用レポート出力PATH */
    public static final String RP_EXP_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\RpExp\\";
    public static final String PE_EXP_OUT_PATH = "\\\\172.30.251.140\\Telegram\\Print\\Report\\PeExp\\";


    // 結合環境
//    /** 輸入許可書作成用レポートPATH */
//    public static final String RE_PATH = "/telegram/Print/Form/rpImp.rrpt";
//    public static final String PE_PATH = "/telegram/Print/Form/rpImp.rrpt";
//    /** 輸入許可書作成用レポート出力PATH */
//    public static final String EX_OUT_PATH = "/telegram/Print/Report/RpImp/";


    // メッセージ
    public static final String PERMIT_ERROR101 = "輸入許可書作成処理中エラーが発生しました。[ERROR CODE: 101]";
    public static final String PERMIT_ERROR102 = "取得データが0件です。[ERROR CODE: 102]";
    public static final String PERMIT_ERROR103 = "輸入CWB番号の取得に失敗しました。[ERROR CODE: 103]";
    public static final String PERMIT_ERROR104 = "PDF作成用データ取得及び項目値設定に失敗しました。[ERROR CODE: 104]";
    public static final String PERMIT_ERROR106 = "S3アップロード処理が失敗しました。[ERROR CODE: 106]";
    public static final String PERMIT_ERROR107 = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。[ERROR CODE: 107]";
    public static final String PERMIT_SUCCESS = "輸入許可書作成が完了しました。";


}


