package com.kse.wmsv2.oa_iw_003.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAIW003SearchStatusDto {
    private String cwbNo;
    private String handyTerminalId;
    private String scanDateTime;
    private String scanPiece;
    private String overFlg;
    private String sts;
}
