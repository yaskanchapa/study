package com.kse.wmsv2.oc_cm_001.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.RateMasterDto;

@Data
public class RateMasterResponse {

    private Integer countRecord;
    private List<RateMasterDto> listRateMasterDto;
    private Boolean csvEnable;
    private Boolean deleteEnable;

    public RateMasterResponse() {
    }

    public RateMasterResponse(List<RateMasterDto> listRateMasterDto) {
        this.listRateMasterDto = listRateMasterDto;
    }
}