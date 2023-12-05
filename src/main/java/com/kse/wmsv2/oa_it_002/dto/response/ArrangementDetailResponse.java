package com.kse.wmsv2.oa_it_002.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oa_it_002.dto.ArrangementDetailDto;

@Data
public class ArrangementDetailResponse {

    private List<ArrangementDetailDto> arrangementDetailDto;

    public ArrangementDetailResponse() {
    }

    public ArrangementDetailResponse(List<ArrangementDetailDto> arrangementDetailDto) {
        this.arrangementDetailDto = arrangementDetailDto;
    }
}