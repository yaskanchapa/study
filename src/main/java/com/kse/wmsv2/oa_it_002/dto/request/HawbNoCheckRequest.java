package com.kse.wmsv2.oa_it_002.dto.request;

import java.util.List;

import lombok.Data;

@Data
public class HawbNoCheckRequest {

    private String hawbNo;

    private List<String> listHawb;

}