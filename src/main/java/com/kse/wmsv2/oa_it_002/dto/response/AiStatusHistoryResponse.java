package com.kse.wmsv2.oa_it_002.dto.response;

import lombok.Data;

import java.util.List;

import com.kse.wmsv2.oa_it_002.dto.AiStatusHistoryDto;

@Data
public class AiStatusHistoryResponse {

    private List<AiStatusHistoryDto> listAiStatusHistoryDto;

    public AiStatusHistoryResponse(List<AiStatusHistoryDto> listAiStatusHistoryDto) {
        this.listAiStatusHistoryDto = listAiStatusHistoryDto;
    }
}