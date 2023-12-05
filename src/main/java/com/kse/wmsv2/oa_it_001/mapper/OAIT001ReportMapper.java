package com.kse.wmsv2.oa_it_001.mapper;

import com.kse.wmsv2.oa_it_001.dao.*;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface OAIT001ReportMapper {
    List<OAIT001IDAReportDao> getIDAData(Map paramMap);

    List<OAIT001IDASubDao> getIDARepData(Map paramMap);

    OAIT001MICReportDao getMICData(Map paramMap);

    List<OAIT001HCHReportDao> getHCHData(Map paramMap);

    List<OAIT001HDMReportDao> getHDMData(Map paramMap);

    List<OAIT001CHAReportDao> getCHAData(Map paramMap);

    List<Map<String, String>> getMICDataList(Map<String, String> paramMap);

    List<String> getProhibitionList();

    int updateMasterStatus(Map paramMap);

    List<OAIT001IDCDao> getIDCDataList(Map<String, String> paramMap);
}
