package com.kse.wmsv2.oc_cm_001.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class UserMasterDto {

     @NotNull(message = "ユーザーコードを入力して下さい。")
     @NotEmpty(message = "ユーザーコードを入力して下さい。")
     @Size(max = 20, message = "入力されたデータが不正。")
     private String userCd;

     @NotNull(message = "ユーザー名(漢字)を入力して下さい。")
     @NotEmpty(message = "ユーザー名(漢字)を入力して下さい。")
     @Size(max = 60, message = "入力されたデータが不正。")
     private String username;

     @Size(max = 60, message = "入力されたデータが不正。")
     private String usernameSyllabary;

     @Size(max = 60, message = "入力されたデータが不正。")
     private String usernameEng;

     @Size(max = 5, message = "入力されたデータが不正。")
     private String usermanagementAuthorityCd;

     private String userManagementAuthorityName;

     @Size(max = 5, message = "入力されたデータが不正。")
     private String userAuthorityCd;

     private String userAuthorityName;

     @Size(max = 20, message = "入力されたデータが不正。")
     private String userAuthorityGroupCd;

     private String userCompanyName;

     private String userCompanyCd;

     @Size(max = 20, message = "入力されたデータが不正。")
     private String departmentCd;

     private String departmentName;

     @Size(max = 60, message = "入力されたデータが不正。")
     private String printUsername;

     @Size(max = 60, message = "入力されたデータが不正。")
     private String printUserCompanyNm;

     @Size(max = 20, message = "入力されたデータが不正。")
     private String printUserCompanyCd;

     @NotNull(message = "パスワードを入力して下さい。")
     @NotEmpty(message = "パスワードを入力して下さい。")
     @Size(max = 20, message = "入力されたデータが不正。")
     private String password;
     
     @DateTimeFormat(pattern = "yyyy/MM/dd")
     @JsonFormat(pattern="yyyy/MM/dd", timezone = "Asia/Tokyo")
     private Date startDate;
     
     @DateTimeFormat(pattern = "yyyy/MM/dd")
     @JsonFormat(pattern="yyyy/MM/dd", timezone = "Asia/Tokyo")
     private Date endDate;

     private String prePassword;

     private String regUserCd;

     private String regUsername;

     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date regDate;

     private String updateUserCd;

     private String updateUsername;

     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date updateDate;

}