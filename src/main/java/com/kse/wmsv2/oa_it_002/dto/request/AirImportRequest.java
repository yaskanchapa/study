package com.kse.wmsv2.oa_it_002.dto.request;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

import com.kse.wmsv2.oa_it_002.dto.HawbDto;

@Data
public class AirImportRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    /** HAWB No */
    private String hawbNo;

    /** MAWB No */
    private String mawbNo;

    /** Origin */
    private String origin;

    /** 到着便名 */
    private String currentArrflt;

    /** 通関STS */
    private String currentCustomsStatusCd;

    /** 書類STS */
    private String currentDocStatusCd;

    /** 賃物STS */
    private String currentCargoStatusCd;

    /** 品名 */
    private String itemName;

    /** 通関業者 */
    private String customsTraderCd;

    /** 通関場所 */
    private String customSplaceCd;

    /** 許可区分 */
    private String permitClassCd;

    /** 提出有無 */
    private String permitClass;

    /** 対象日・到着 */
    private Integer objectDate;
    // /** 対象日・到着 */
    // private Integer objectDateArrival;

    // /** 対象日・搬入 */
    // private Integer objectDateCarrying;

    // /** 対象日・申告 */
    // private Integer objectDateReport;

    // /** 対象日・許可 */
    // private String objectDatePermission;

    // /** 対象日・搬出 */
    // private String objectDateCarry;

    /** 対象日・開始 */
    private String objectDateFrom;

    /** 対象日・終了 */
    private String objectDateTo;

    /** 申告番号 */
    private String reportNo;

    /** HPK・未 */
    private Integer hpkFlg01;

    /** HPK・済 */
    private Integer hpkFlg02;

    /** 申告・未 */
    private Integer reportFlg01;

    /** 申告・済 */
    private Integer reportFlg02;

    /** 許可・未 */
    private Integer permitFlg01;

    /** 許可・済 */
    private Integer permitFlg02;

    /** C4登録・未 */
    private Integer c4regFlg01;

    /** C4登録・済 */
    private Integer c4regFlg02;

    /** 3点SET・無 */
    private Integer set;

    /** 申告書発行・未 */
    private Integer declarationOutput01;

    /** 申告書発行・済 */
    private Integer declarationOutput02;

    /** イメージ・有り */
    private Integer image01;

    /** イメージ・無し */
    private Integer image02;

    /** 申告種別・MIC */
    private Integer reportMic;

    /** 申告種別・IDA */
    private Integer reportIda;

    /** 内点/確認書・有 */
    private Integer certificate01;

    /** 内点/確認書・無 */
    private Integer certificate02;

    /** 少額/大額・少額 */
    private Integer bigSmallFlg01;

    /** 少額/大額・大額 */
    private Integer bigSmallFlg02;

    /** オプション・有り */
    private Integer optional01;

    /** オプション・無し */
    private Integer optional02;

    /** 輸入者・No */
    private String customerNo;

    /** 輸入者・DeptCD */
    private String customerDeptCd;

    /** 輸入者・OCSDeptCD */
    private String customerOcsDeptCd;

    /** 代理店選択 */
    private String agencyCmb;

    /** 代理店入力 */
    private String agencyInput;

    /** 比較オペレータ */
    private String operator;
    
    /** HAWBNo選択 */
    private String hawbNoCmb;

    /** HAWBNoリスト */
    private String arrHawb;
    private List<HawbDto> listHawb;

    /**
     * Set page for get data.
     * 
     * @param adminSearch AdminSearch
     * @return page number
     */
    public static AirImportRequest setPaging(AirImportRequest airImportRequest) {
        if (airImportRequest.getPage() != null && airImportRequest.getItemPerPage() != null) {
            airImportRequest.setPage((airImportRequest.getPage() - 1) * airImportRequest.getItemPerPage());
        }
        return airImportRequest;
    }
}