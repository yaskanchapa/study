package com.kse.wmsv2.oa_it_001.dto;

import lombok.Data;

@Data
public class OAIT001AIStatusHistoryDto
 {
    String awbNo;
    String bwbNo;
    String cwbNo;
    String cwbNoDeptCD;
    String statusCd;
    String importExportClass;
    String bussinessClass;
    String documentCheck;
    Integer seq;
    String departMentCd;
    Integer insFlg;
    String idaFlg;
    String confirmationItem;
    String detail;
    String customsBroker;
    String customsClearancePlace;
    String cmbCustomsClearanceSituation;
    String updateUserCD;
    String updateDate;
}
