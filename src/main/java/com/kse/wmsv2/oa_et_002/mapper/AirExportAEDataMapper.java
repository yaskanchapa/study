package com.kse.wmsv2.oa_et_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_et_002.dto.AirExportDto;
import com.kse.wmsv2.oa_et_002.dto.request.AEStatusHistoryRequest;
import com.kse.wmsv2.oa_et_002.dto.request.AirExportRequest;

@Repository
@Mapper
public interface AirExportAEDataMapper {
    List<AirExportDto> getDataAE(AirExportRequest airExportRequest);
    void updateAEData(AEStatusHistoryRequest aEStatusHistoryRequest);
}
