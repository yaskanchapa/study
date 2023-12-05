package com.kse.wmsv2.oc_cm_001.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.ComBoBoxDto;
import com.kse.wmsv2.oc_cm_001.dto.TraderNoMasterDto;

@Data
public class TraderNoMasterResponse {

    private List<ComBoBoxDto> listImportAndExportFlag;
    private List<ComBoBoxDto> listPlaceOfUse;
    private List<ComBoBoxDto> listConditionImport;
    private List<ComBoBoxDto> listConditionExport;
    private Integer countRecord;
    private List<TraderNoMasterDto> listTraderNoMasterDto;
    private Boolean csvEnable;

    public TraderNoMasterResponse() {
    }

    public TraderNoMasterResponse(List<TraderNoMasterDto> listTraderNoMasterDto) {
        this.listTraderNoMasterDto = listTraderNoMasterDto;
    }
}