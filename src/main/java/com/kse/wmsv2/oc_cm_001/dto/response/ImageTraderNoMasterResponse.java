package com.kse.wmsv2.oc_cm_001.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.ImageTraderNoMasterDto;

@Data
public class ImageTraderNoMasterResponse {

    private List<ImageTraderNoMasterDto> listImageTraderNoMasterDto;

    public ImageTraderNoMasterResponse(List<ImageTraderNoMasterDto> listImageTraderNoMasterDto) {
        this.listImageTraderNoMasterDto = listImageTraderNoMasterDto;
    }
}