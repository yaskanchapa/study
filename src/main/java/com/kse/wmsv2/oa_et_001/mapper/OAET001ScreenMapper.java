package com.kse.wmsv2.oa_et_001.mapper;


import com.kse.wmsv2.oa_et_001.dao.*;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001UpdateCheckBoxDto;
import com.kse.wmsv2.oa_it_001.dao.OAIT001StatusDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAET001ScreenMapper {

    List<OAET001SearchResultDao> searchData(Map<String, String> param);

    List<Object> getAwbNoList(Map<String, String> param);

    List<OAIT001StatusDao> searchAllStatus(Map<String, String> paramMap);

    OAET001MECDao selectMECDetail(Map<String, String> paramMap);

    OAET001EDAHeaderDao selectEDAHeader(Map<String, String> paranMap);

    List<OAET001EDADetailDao> selectEDADetail(Map<String, String> paramMap);

    int editExportData(Map<String,String> paramMap);

    List<Map> selectDeptCdList();

    List<Map> selectAgencyList();

    List<OAET001DefaultDao> selectDefault(Map<String, String> paramMap);

    int searchAwbNo(OAET001SearchDto params);

    int updateHeaderData(Map<String, String> paramMap);

    List<Map> getListHeader(Map<String, String> paramMap);

    int deleteExportData(OAET001SearchResultDao oaet001SearchResultDao);

    int changeColumnValue(OAET001UpdateCheckBoxDto boxValue);

    Map<String,String> getOtherSearchInfo(String value);
}
