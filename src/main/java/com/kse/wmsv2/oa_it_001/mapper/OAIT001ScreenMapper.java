package com.kse.wmsv2.oa_it_001.mapper;

import com.kse.wmsv2.oa_it_001.dao.*;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ImageDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001UpdateCellDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAIT001ScreenMapper {

    OAIT001HeaderDao selectHeader(OAIT001SearchDto params);

    List<OAIT001DefaultDao> selectDefault(Map paramMap);

    List<OAIT001SearchResultDao> selectSearchResult(OAIT001SearchDto params);
//
//
//    List<OAIT001SearchResultDao> insertFile();

    List<OAIT001StatusDao> searchAllStatus(Map paramMap);

    Map<String, List<String>> getUploadScreenDefaultValue(OAIT001SearchDto params);

    List<Map> selectDeptCdList();

    List<Map> selectAgencyList();

    OAIT001MICDao selectMICDetail(Map paramMap);

    OAIT001IDAMainDao selectIDAMain(OAIT001SearchDto params);

    List<OAIT001IDASubDao> selectIDASub(OAIT001SearchDto params);

    int editImportData(Map<String, String> paramMap);

    String getCwbImagePath(OAIT001ImageDto params);

    int resettingDefVal(Map<String, String> paramMap);

    int editHeadData(OAIT001HeaderDao params);

    List<String> selectFltList(Map<String, String> paramMap);

    int searchAwbNo(String value);

    int deleteImportData(OAIT001SearchResultDao oait001SearchResultDao);

    int changeColumnValue(OAIT001UpdateCellDto boxValue);

    int changeCheckBoxValue(OAIT001UpdateCellDto boxValue);
    int checkReportCondition(String value);

    int checkMawbNumber(OAIT001HeaderDao params);

    OAIT001ImageDto getPdfUrl(OAIT001ImageDto param);

    Map<String,String> getCountSearch(OAIT001SearchDto params);
    
    Integer countAiStatusHistory (@Param("awbNo") String awbNo, @Param("cwbNo") String cwbNo, @Param("cwbNoDeptCD") String cwbNoDeptCD);

    Integer  countAiData (@Param("awbNo") String awbNo, @Param("arrFlt1") String arrFlt1, @Param("arrFlt2") String arrFlt2);

    Integer deleteAiDataRow (@Param("awbNo") String awbNo, @Param("cwbNo") String cwbNo, @Param("cwbNoDeptCD") String cwbNoDeptCD);

    Integer deleteAiHead (@Param("awbNo") String awbNo, @Param("arrFlt1") String arrFlt1, @Param("arrFlt2") String arrFlt2);
}