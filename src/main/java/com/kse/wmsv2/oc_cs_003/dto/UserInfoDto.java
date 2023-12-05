package com.kse.wmsv2.oc_cs_003.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * ユーザー情報を管理するDto
 */
@Getter
@Setter
public class UserInfoDto {

    private String userCd;
    private String password;
    private String startDate;
    private String endDate;
    private String prePassWord;
    private String userName;
    private String userNameSyllabary;
    private String userNameEng;
    private String userManagementAuthorityCd;
    private String userAuthorityCd;
    private String userCompanyCd;
    private String departmentCd;
    private String regUserCd;
    private String regDate;
    private String updateUserCd;
    private String updateDate;
    private String userAuthorityGroupCd;
    private String printUserCompanyNm;
    private String printUserCompanyCd;
    private String printUserName;
    private String departmentName;

}
