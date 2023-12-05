package com.kse.wmsv2.oc_cm_001.dto.request;

import lombok.Data;

@Data
public class TraderNoMasterRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    /** 業者No */
    private String customerNo;

    /** N枝番 */
    private String deptCd;

    /** 輸出入フラグ */
    private String impExpFlag;

    /** 使用場所 */
    private String shiyoBashoFlag;

    /** 英文会社名 */
    private String customerNamee;

    /** 郵便番号 */
    private String zipCd;

    /** 英文住所1 */
    private String customerAdde1;

    /** 英文住所2 */
    private String customerAdde2;

    /** 英文住所3 */
    private String customerAdde3;

    /** 英文住所4 */
    private String customerAdde4;

    /** 英文住所(一括) */
    private String customerAddeblAnket;

    /** 和文会社名 */
    private String customerNamej;

    /** 和文部署名 */
    private String division;

    /** 和文住所 */
    private String customerAddej;

    /** 担当者名 */
    private String charger;

    /** TEL No */
    private String telNo;

    /** FAX No */
    private String faxNo;

    /** メールアドレス */
    private String emailAdd;

    /**
     * Set page for get data.
     * 
     * @param traderNoMasterRequest TraderNoMasterRequest
     * @return page number
     */
    public static TraderNoMasterRequest setPaging(TraderNoMasterRequest traderNoMasterRequest) {
        if (traderNoMasterRequest.getPage() != null && traderNoMasterRequest.getItemPerPage() != null) {
            traderNoMasterRequest.setPage((traderNoMasterRequest.getPage() - 1) * traderNoMasterRequest.getItemPerPage());
        }
        return traderNoMasterRequest;
    }
}