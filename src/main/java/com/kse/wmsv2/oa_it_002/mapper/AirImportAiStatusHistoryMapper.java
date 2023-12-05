package com.kse.wmsv2.oa_it_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_it_002.dto.AiStatusHistoryDto;
import com.kse.wmsv2.oa_it_002.dto.request.AiStatusHistoryRequest;

@Repository
@Mapper
public interface AirImportAiStatusHistoryMapper {
    List<AiStatusHistoryDto> getDataAiStatusHistory(AiStatusHistoryRequest aiStatusHistoryRequest);
}
