package com.kse.wmsv2.oa_it_001.dto;

import lombok.Data;

@Data
public class OAIT001ImageDto {
    String importExportClass;
    String awbNo;
    String cwbNo;
    String cwbNoDeptCd;
    String imageClass;
    String seq;
    String pdfUrl1;
    String pdfUrl2;
    String errorFlag;
    String message;
}
