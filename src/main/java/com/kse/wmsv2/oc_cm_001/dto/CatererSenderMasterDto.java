package com.kse.wmsv2.oc_cm_001.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CatererSenderMasterDto {

     @NotNull(message = "業者№を入力して下さい。")
     @NotEmpty(message = "業者№を入力して下さい。")
     @Size(max = 13, message = "入力されたデータが不正。")
     private String customerNo;
     @NotNull(message = "N枝番を入力して下さい。")
     @NotEmpty(message = "N枝番を入力して下さい。")
     @Size(max = 4, message = "入力されたデータが不正。")
     private String deptCd;
     private String ocsdeptCd;
     @NotNull(message = "仕出向フラグを入力して下さい。")
     @NotEmpty(message = "仕出向フラグを入力して下さい。")
     private String csrCseFlag;
     @NotNull(message = "使用場所を入力して下さい。")
     @NotEmpty(message = "使用場所を入力して下さい。")
     private String shiyoBashoFlag;
     private String seq;
     @Size(max = 70, message = "入力されたデータが不正。")
     private String customerNamee;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde1;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde2;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde3;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde4;
     @Size(max = 105, message = "入力されたデータが不正。")
     private String customerAddeblAnket;
     @Size(max = 2, message = "入力されたデータが不正。")
     private String countryCd;
     @Size(max = 9, message = "入力されたデータが不正。")
     private String zipCd;
     @Size(max = 60, message = "入力されたデータが不正。")
     private String division;
     @Size(max = 40, message = "入力されたデータが不正。")
     private String charger;
     @Size(max = 20, message = "入力されたデータが不正。")
     private String telNo;
     @Size(max = 20, message = "入力されたデータが不正。")
     private String faxNo;
     @Size(max = 100, message = "入力されたデータが不正。")
     private String emailAdd;
     @Size(max = 1000, message = "入力されたデータが不正。")
     private String remarks;
     private String regUserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date regDate;
     private String updateUserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date updateDate;
}