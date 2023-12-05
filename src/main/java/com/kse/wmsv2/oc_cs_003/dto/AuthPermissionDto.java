package com.kse.wmsv2.oc_cs_003.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 権限情報を管理するDto
 */
@Getter
@Setter
public class AuthPermissionDto {

//    int seq;
    private String authId;
    private String userManagementAuthorityCdInfo;
    private String userAuthorityCdInfo;
    private String userAuthorityGroupCd;
//    String regUserCd;
//    String regDate;
//    String updateUserCd;
//    String updateDate;

}
