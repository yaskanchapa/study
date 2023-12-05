package com.kse.wmsv2.oa_et_003.dto;


import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;


import java.util.List;

@Data
public class OAET003SearchDto {
    String declarationDate1;
    String declarationDate2;
    String permissionDate1;
    String permissionDate2;
    String bondedCd;
    String shedCd;
    String awbNo;
    String cwbNo;
    String flt1;
    String flt2;
    String referenceNo;
    String item;
    String exporterName;
    String news1;
    String news2;
    String newsShipper;
    String exporterAdd;
    String inputExpCustomerNo;
    String depPort;
    String consigneeName;
    String consigneeAddLump;

}
