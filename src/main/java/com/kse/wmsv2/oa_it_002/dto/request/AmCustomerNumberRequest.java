package com.kse.wmsv2.oa_it_002.dto.request;

import javax.validation.constraints.NotEmpty;

import lombok.Data;

@Data
public class AmCustomerNumberRequest {

    @NotEmpty(message = "輸入者コードを入力してください。")
    private String customerNo;

    @NotEmpty(message = "輸入者コード枝番を入力してください。")
    private String deptCd;

    @NotEmpty(message = "輸入者コードOCSを入力してください。")
    private String ocsDeptCd;
    
}