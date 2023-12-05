package com.kse.wmsv2.oc_cm_001.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class TraderNoMasterDto {

     @NotNull(message = "業者№を入力して下さい。")
     @NotEmpty(message = "業者№を入力して下さい。")
     @Size(max = 13, message = "入力されたデータが不正。")
     private String customerNo;
     @NotNull(message = "N枝番を入力して下さい。")
     @NotEmpty(message = "N枝番を入力して下さい。")
     @Size(max = 4, message = "入力されたデータが不正。")
     private String deptCd;
     @Size(max = 5, message = "入力されたデータが不正。")
     private String ocsdeptCd;
     private String impExpFlag;
     private String shiyoBashoFlag;
     private String seq;
     private Integer optionSelectedFlag;
     private String jastProCode;
     @NotNull(message = "英文会社名を入力して下さい。")
     @NotEmpty(message = "英文会社名を入力して下さい。")
     @Size(max = 70, message = "入力されたデータが不正。")
     private String customerNamee;
     @Size(max = 15, message = "入力されたデータが不正。")
     private String customerAdde1;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde2;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String customerAdde3;
     @Size(max = 70, message = "入力されたデータが不正。")
     private String customerAdde4;
     @Size(max = 105, message = "入力されたデータが不正。")
     private String customerAddeblAnket;
     @Size(max = 80, message = "入力されたデータが不正。")
     private String customerNamej;
     @Size(max = 7, message = "入力されたデータが不正。")
     private String zipCd;
     @Size(max = 100, message = "入力されたデータが不正。")
     private String customerAddej;
     @Size(max = 60, message = "入力されたデータが不正。")
     private String division;
     @Size(max = 40, message = "入力されたデータが不正。")
     private String charger;
     @NotNull(message = "TELNoを入力して下さい。")
     @NotEmpty(message = "TELNoを入力して下さい。")
     @Size(max = 14, message = "入力されたデータが不正。")
     private String telNo;
     @Size(max = 14, message = "入力されたデータが不正。")
     private String faxNo;
     @Size(max = 100, message = "入力されたデータが不正。")
     private String emailAdd;
     @Size(max = 1000, message = "入力されたデータが不正。")
     private String remarks;
     @Size(max = 100, message = "入力されたデータが不正。")
     private String conditionComment;
     @Size(max = 14, message = "入力されたデータが不正。")
     private String conditionBankAccountNo;
     @Size(max = 9, message = "入力されたデータが不正。")
     private String conditionCollateralNo1;
     @Size(max = 9, message = "入力されたデータが不正。")
     private String conditionCollateralNo2;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg01;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg02;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg03;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg04;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg05;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg06;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg07;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg08;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg09;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg10;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg11;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg12;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg13;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg14;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg15;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg16;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg17;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg18;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg19;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg20;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg21;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg22;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg23;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg24;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String conditionFlg25;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String deliveryDateExtCd;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String estimationFlgCd;
     @Size(max = 12, message = "入力されたデータが不正。")
     private String incestrepreNo;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String insuranceClassCd;
     @Size(max = 8, message = "入力されたデータが不正。")
     private String incinsuregNo;
     @Size(max = 1, message = "入力されたデータが不正。")
     private String paymethodDisc;
     private String conditionCollateralNo;
     private String reguserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date regDate;
     private String updateUserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date updateDate;
     @Size(max = 12, message = "入力されたデータが不正。")
     private String incestrepreNo2;
     @Size(max = 12, message = "入力されたデータが不正。")
     private String incestrepreNo3;
     @Size(max = 17, message = "入力されたデータが不正。")
     private String customsOfficeJanitorCd;
     @Size(max = 10, message = "入力されたデータが不正。")
     private String customsOfficeJanitorreNo;
     @Size(max = 70, message = "入力されたデータが不正。")
     private String customsOfficeJanitorName;
     @Size(max = 20, message = "入力されたデータが不正。")
     private String shipperssecCd;
     @Size(max = 35, message = "入力されたデータが不正。")
     private String shippersrefNo;

}