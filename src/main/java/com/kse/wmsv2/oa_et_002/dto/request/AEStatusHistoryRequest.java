package com.kse.wmsv2.oa_et_002.dto.request;

import java.util.Date;

import lombok.Data;

@Data
public class AEStatusHistoryRequest {

  private String regDate;

  private String stsName;

  private String businessClassName;

  private String userName;

  private String businessClass;

  private String awbNo;

  private String bwbNo;

  private String cwbNo;

  private String cwbNoDeptCd;

  private String statusCd;

  private String regUserCd;

  private String userCd;

  private Date systemDate;

  private Boolean updateFlag;

  private Boolean deleteFlag;

  private String headerBusinessClass;

  private String headerStatusCd;

  private String headerRegDate;

  private Boolean useHeader;

}