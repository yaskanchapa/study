package com.kse.wmsv2.common.dto;

import lombok.Data;

@Data
public class COMMONImportAddAccDto {
    // return
    String cargoIn;
    String cargoOut;
    String domestic;
    String reportCondition;
    boolean errorFlg;

    // input
    String cwbNo;
    String awbNo;
    String taxAmo;
    String idaFlg;
    String inClassifyClassName;
    String outClassifyClassName;
    String domesticClassifyClassName;
}
