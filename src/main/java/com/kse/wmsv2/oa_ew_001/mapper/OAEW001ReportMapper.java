package com.kse.wmsv2.oa_ew_001.mapper;

import com.kse.wmsv2.oa_ew_001.dao.OAEW001BILReportDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAEW001ReportMapper {

    List<OAEW001BILReportDao> getBILDataList(Map<String, String> paramMap);
    List<OAEW001BILReportDao> getBILDataListRe(Map<String, String> paramMap);
    int updateMasterStatus(Map paramMap);
    int insertCsSendMessage(Map<String, String> updateParamMap2);
    String getS3Path(String businessName);
}