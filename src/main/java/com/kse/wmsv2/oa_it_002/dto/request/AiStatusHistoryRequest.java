package com.kse.wmsv2.oa_it_002.dto.request;

import lombok.Data;

@Data
public class AiStatusHistoryRequest {

    private String businessClass;

    private String cwbNo;

    private String cwbNoDeptCd;

    private String statusCd;

}