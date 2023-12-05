package com.kse.wmsv2.oa_ew_003.dto;

import lombok.Data;

@Data
public class OAEW003examinationUpdateDto {

    String portName;
    String updateUserCd;
    String regUserCd;
    String cwbNo;
    String termNo;
    String customStatusCd;
    String piece;
    String linkPiece;
    String awbNo;
    String currentCargoStatusCd;
    String message;
    String errorMessage;
    boolean result;
}
