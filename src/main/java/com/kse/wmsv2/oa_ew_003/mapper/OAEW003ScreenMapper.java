package com.kse.wmsv2.oa_ew_003.mapper;

import com.kse.wmsv2.oa_ew_003.dao.OAEW003portSearchResultDao;
import com.kse.wmsv2.oa_ew_003.dto.*;
import com.kse.wmsv2.oa_ew_003.dao.OAEW003flightSearchResultDao;
import com.kse.wmsv2.oa_ew_003.dao.OAEW003containerSearchResultDao;
import com.kse.wmsv2.oa_ew_003.dao.OAEW003examinationSearchResultDao;
import com.kse.wmsv2.oa_ew_003.dao.OAEW003stowageSearchResultDao;
import com.kse.wmsv2.oa_ew_003.dao.OAEW003SearchWarehouseDao;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAEW003ScreenMapper {

    List <OAEW003portSearchResultDao> searchPort(OAEW003portSearchDto params);
    List <OAEW003flightSearchResultDao> searchFlight(OAEW003flightSearchDto params);
    List <OAEW003containerSearchResultDao> searchContainer(OAEW003containerSearchDto params);
    List<OAEW003SearchWarehouseDao> searchWarehouse(OAEW003SearchWarehouseDto params);
    OAEW003examinationSearchResultDao searchExamination(OAEW003examinationSearchDto params);
    int updateExamination(OAEW003examinationUpdateDto params);
    OAEW003stowageSearchResultDao searchStowage(OAEW003stowageSearchDto params);
    int updateStowage(OAEW003stowageUpdateDto params);
}
