package com.kse.wmsv2.oa_et_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_et_002.dto.AEStatusHistoryDto;
import com.kse.wmsv2.oa_et_002.dto.request.AEStatusHistoryRequest;

@Repository
@Mapper
public interface AirExportAEStatusHistoryMapper {
    List<AEStatusHistoryDto> getDataAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest);
    Integer countAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest);
    void deleteAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest);
    void updateAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest);
    void insertAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest);
}
