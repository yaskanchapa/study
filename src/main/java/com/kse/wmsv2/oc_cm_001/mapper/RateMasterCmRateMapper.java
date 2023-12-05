package com.kse.wmsv2.oc_cm_001.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.RateMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.RateMasterRequest;

@Repository
@Mapper
public interface RateMasterCmRateMapper {

    void insertCmRateByStartDateAndNameCd(RateMasterDto rateMasterDto);

    void deleteCmRateByStartDate(RateMasterRequest rateMasterRequest);

}
