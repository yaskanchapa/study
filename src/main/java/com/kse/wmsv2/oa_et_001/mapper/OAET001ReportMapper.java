package com.kse.wmsv2.oa_et_001.mapper;


import com.kse.wmsv2.oa_et_001.dao.*;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAET001ReportMapper {

    List<OAET001ReportCdbDao> getCdbData(Map dataMap);

    List<OAET001ReportMecDao> getMecDataReMake(Map param);

    List<OAET001ReportMecDao> getMecDataNew(Map param);

    void updateAEDATA(Map param);

    String getDeptCd(String value);

    List<OAET001ReportEdcDao> getEdcDataNew(Map param);

    List<OAET001ReportEdcDao> getEdcDataReMake(Map param);

    List<String> getEdaDataReMake(Map<String, String> dataMap);

    List<String> getEdaDataNew(Map<String, String> dataMap);

    OAET001ReportEdaHeadDao getEdaHeader(String value);

    List<OAET001ReportEdaDetailDao> getEdaDetail(String value);

    List<String> getReportAwbList(OAET001SearchDto param);

    int updateMasterStatus(Map paramMap);


}
