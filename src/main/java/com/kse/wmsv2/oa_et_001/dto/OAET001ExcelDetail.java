package com.kse.wmsv2.oa_et_001.dto;

import lombok.Data;

@Data
public class OAET001ExcelDetail {
    OAET001ExcelHead head;
    String sn;
    String cwbNo;
    String pieces1;
    String weight1;
    String consigneeName;
    String consigneeAdd;
    String shipperName;
    String shipperAdd;
    String shipperPost;
    String contact;
    String description;
    String currency;
    String invoiceValue;
    String pieces2;
    String weight2;
    String countryOfOrigin;
    String freight;
    String freightAmount;
    String customNo;
    String customType;
    String iie;
    String userMemo;
}
