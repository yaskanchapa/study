package com.kse.wmsv2.oa_it_002.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class AiStatusHistoryDto {

     private String stsName;

     private String userName;

     @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
     private Date updateDate;

     private String sts;

}