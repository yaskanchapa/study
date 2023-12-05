package com.kse.wmsv2.oa_it_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_it_002.dto.request.AirImportRequest;
import com.kse.wmsv2.oa_it_002.dto.AirImportDto;
import com.kse.wmsv2.oa_it_002.dto.AirImportCSVDto;
import com.kse.wmsv2.oa_it_002.dto.HawbDto;

@Repository
@Mapper
public interface AirImportAIDataMapper {
    List<AirImportDto> getDataAI(AirImportRequest airImportRequest);
    List<AirImportCSVDto> getDataCSVAfterPermission(@Param("countHawb") String countHawb, @Param("hawb1") String hawb1, @Param("hawb2") String hawb2);
    List<HawbDto> checkHawbNo(HawbDto hawbDto);
    void updateHawbNo(HawbDto hawbDto);
}
