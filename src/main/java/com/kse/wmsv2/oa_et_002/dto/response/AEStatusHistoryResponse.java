package com.kse.wmsv2.oa_et_002.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oa_et_002.dto.AEStatusHistoryDto;
import com.kse.wmsv2.oa_et_002.dto.ComBoBoxDto;

@Data
public class AEStatusHistoryResponse {

    private List<AEStatusHistoryDto> listAEStatusHistoryDtoDto;

    private List<ComBoBoxDto> listBusinessClassName;
    private List<ComBoBoxDto> list01;
    private List<ComBoBoxDto> list02;
    private List<ComBoBoxDto> list03;

    public AEStatusHistoryResponse() {
    }

    public AEStatusHistoryResponse(List<AEStatusHistoryDto> listAEStatusHistoryDtoDto) {
        this.listAEStatusHistoryDtoDto = listAEStatusHistoryDtoDto;
    }
}