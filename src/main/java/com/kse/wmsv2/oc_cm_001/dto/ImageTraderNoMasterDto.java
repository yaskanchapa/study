package com.kse.wmsv2.oc_cm_001.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ImageTraderNoMasterDto {

     private String customerNo;
     private Integer seq;
     private String imageName;
     private String imagePath;
     private String regUserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date regDate;
     private String updateUserCd;
     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date updateDate;

}