package com.kse.wmsv2.common.mapper;

import com.kse.wmsv2.common.dto.COMMONImportAddAccDto;
import com.kse.wmsv2.common.dto.COMMONStatusDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

@Repository
@Mapper
public interface COMMONMapper {
    String getBusinessDate();

    int updateStatusMasterTable(COMMONStatusDto param);

    int insertStatusHistoryTable(COMMONStatusDto param);

    int insertStatusMasterTable(COMMONStatusDto params);

    Map getCommonValueReport(Map paramMap);

    String getReportPath(Map paramMap);

    Map getCommonValueReportAuto(Map<String, String> paramMap);

    String getS3Path(String businessName);
    String getS3Path2(String simAuto);

    Map getHDInformation(Map paramMap);

    int updateHDInformation(Map paramMap);

    Integer getOptionalService(String cwbNo);

    Integer getConditionCount(String cwbNo);

    COMMONImportAddAccDto getAddProcessValue(Map paramMap);

    int setAddProcessValue(COMMONImportAddAccDto param);

}
