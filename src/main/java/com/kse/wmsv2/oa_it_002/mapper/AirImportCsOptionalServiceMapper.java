package com.kse.wmsv2.oa_it_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_it_002.dto.request.ArrangementDetailRequest;
import com.kse.wmsv2.oa_it_002.dto.CsOptionalServiceDto;
import com.kse.wmsv2.oa_it_002.dto.ArrangementDetailDto;

@Repository
@Mapper
public interface AirImportCsOptionalServiceMapper {

    List<ArrangementDetailDto> getArrangementDetails(ArrangementDetailRequest arrangementDetailRequest);

    void updateCsOptionalService(CsOptionalServiceDto csOptionalServiceDto);

}
