package com.kse.wmsv2.oc_cm_001.dto.request;

import lombok.Data;

@Data
public class UserMasterRequest {

    /** ページ. */
    private Integer page;

    /** ページあたりのアイテム. */
    private Integer itemPerPage;

    /** ユーザーコード */
    private String userCd;

    /** ユーザー名(漢字) */
    private String username;

    /** ユーザー名(カナ) */
    private String usernameSyllabary;

    /** ユーザー名(英) */
    private String usernameEng;

    /** 管理権限 */
    private String usermanagementAuthorityCd;

    /** 業務権限 */
    private String userAuthorityCd;

    /** 所属会社 */
    private String userCompanyCd;

    /** 所属部署 */
    private String departmentCd;

    /**
     * Set page for get data.
     * 
     * @param adminSearch AdminSearch
     * @return page number
     */
    public static UserMasterRequest setPaging(UserMasterRequest userMasterRequest) {
        if (userMasterRequest.getPage() != null && userMasterRequest.getItemPerPage() != null) {
            userMasterRequest.setPage((userMasterRequest.getPage() - 1) * userMasterRequest.getItemPerPage());
        }
        return userMasterRequest;
    }
}