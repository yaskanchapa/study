package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003InsertAiStatusHistoryDao {
    private String businessClass;
    private String awbNo;
    private String bwbNo;
    private String cwbNo;
    private String cwbNoDeptCd;
    private String statusCd;
    private String regUserCd;
    private String regDate;
    private String updateUserCd;
    private String updateDate;
}
