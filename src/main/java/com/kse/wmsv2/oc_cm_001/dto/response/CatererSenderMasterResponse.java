package com.kse.wmsv2.oc_cm_001.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.ComBoBoxDto;
import com.kse.wmsv2.oc_cm_001.dto.CatererSenderMasterDto;

@Data
public class CatererSenderMasterResponse {

    private List<ComBoBoxDto> listSenderFlag;
    private List<ComBoBoxDto> listPlaceOfUse;
    private Integer countRecord;
    private List<CatererSenderMasterDto> listCatererSenderMasterDto;
    private Boolean csvEnable;

    public CatererSenderMasterResponse() {
    }

    public CatererSenderMasterResponse(List<CatererSenderMasterDto> listCatererSenderMasterDto) {
        this.listCatererSenderMasterDto = listCatererSenderMasterDto;
    }
}