package com.kse.wmsv2.oc_cm_001.dto.request;

import lombok.Data;

@Data
public class CatererSenderMasterRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    /** 仕出仕向人No */
    private String customerNo;

    /** N枝番 */
    private String deptCd;

    /** 仕出向フラグ */
    private String csrCseFlag;

    /** 使用場所 */
    private String shiyoBashoFlag;

    /** 国連国コード */
    private String countryCd;

    /** 英文会社名 */
    private String customerNamee;
    
    /** 英文部署名 */
    private String division;

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
    private String customerAddeBlanket;

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
     * @param CatererSenderMasterRequest catererSenderMasterRequest
     * @return page number
     */
    public static CatererSenderMasterRequest setPaging(CatererSenderMasterRequest catererSenderMasterRequest) {
        if (catererSenderMasterRequest.getPage() != null && catererSenderMasterRequest.getItemPerPage() != null) {
            catererSenderMasterRequest.setPage((catererSenderMasterRequest.getPage() - 1) * catererSenderMasterRequest.getItemPerPage());
        }
        return catererSenderMasterRequest;
    }
}