package com.kse.wmsv2.oa_ew_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW003stowageUpdateDto {
   String fltDestination;
   String containerName;
   String updateUserCd;
   String cwbNo;
   String termNo;
   String bondStatusCd;
   String awbNo;
   String currentCargoStatusCd;
   String containerClassCd;
   String regUserCd;
   String message;
   String errorMessage;
   boolean result;
}
