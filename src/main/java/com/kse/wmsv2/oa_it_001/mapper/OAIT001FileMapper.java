package com.kse.wmsv2.oa_it_001.mapper;

import com.kse.wmsv2.oa_it_001.dao.*;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface OAIT001FileMapper {

    Integer insertImportDataDetail(OAIT001DetailDataDao detailDao);

    Integer insertImportDataHead(OAIT001HeadDataDao headDao);

    Integer insertImportDataRep(OAIT001RepDataDao repDao);

    OAIT001ImporterDao getImporterInfo(String deptCd);

    Map<String, String> getFareCurrency(Map paramMap);

    String getRate(String currencyCode);

    String getCommonRate(Map paramMap);

    Integer getOptionalService(String cwbNo);

    Integer getConditionCount(String cwbNo);

    Integer getLimitValue();

    Integer getCountIDAItem(Map paramMap);

    Integer getMaximumCargoPiece(Map paramMap);

    Integer getMaximumCargoWeight(Map paramMap);


    List<OAIT001SearchResultDao> searchResultList(String awbNo);

    OAIT001HeaderDao searchResultHeader(String awbNo);

    void deleteOldData(String awbNo);
}
