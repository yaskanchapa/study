package com.kse.wmsv2.oa_iw_003.mapper;

import com.kse.wmsv2.oa_iw_003.dao.*;
import com.kse.wmsv2.oa_iw_003.dto.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OAIW003ScreenMapper {
    int selectAiInventoryCnt(OAIW003SearchDataDao params);
    List<OAIW003SearchResultDto> selectAiInventory(OAIW003SearchDataDao params);
    List<OAIW003SearchResult2Dto> searchMawb(OAIW003SearchData2Dao params);
    OAIW003SelectAmNameDto selectAmName(OAIW003SelectAmNameDao params);
    OAIW003SelectAiDataDto selectTargetData(OAIW003KeyDataDao params);
    OAIW003SelectAiData2Dto selectTargetData2(OAIW003KeyDataDao params);
    int updateAiData(OAIW003UpdateAiDataDao params);
    int insertAiInventory(@Param("insertAiInventoryDaoList") List<OAIW003InsertAiInventoryDao> insertAiInventoryDaoList);
    int insertAiInventoryHead(@Param("insertAiInventoryHeadDaoList") List<OAIW003InsertAiInventoryHeadDao> insertAiInventoryHeadDaoList);
    int insertAiStatusHistory(@Param("insertAiStatusHistoryDaoList") List<OAIW003InsertAiStatusHistoryDao> insertAiStatusHistoryDaoList);
    int insertAiStatus(@Param("insertAiStatusDaoList") List<OAIW003InsertAiStatusHistoryDao> insertAiStatusDaoList);
    List<OAIW003SelectAiInventoryDto> selectAiInventory2(OAIW003endKeyDataDao params);
    int updateAiInventory(OAIW003UpdateAiInventoryDao params);
    int updateAiInventoryHead(OAIW003UpdateAiInventoryHeadDao params);
    int searchAiInventory(OAIW003SearchData2Dao params);
    int searchStatusCnt(OAIW003SearchStatusDao params);
    List<OAIW003SearchStatusDto> searchStatus(OAIW003SearchStatusDao params);
}
