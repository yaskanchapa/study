package com.kse.wmsv2.oa_iw_001.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kse.wmsv2.oa_iw_001.dao.*;
import com.kse.wmsv2.oa_iw_001.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OAIW001ScreenMapper {
    List<OAIW001SelectBondedInListDto> SelectBondedInList(OAIW001SelectBondedInListDao params);
    int selectAiDataCnt(OAIW001SelectAiDataCnt1Dao params);
    int updateAiHead(OAIW001UpdateAiDataDao params);
    int updateAiData(OAIW001UpdateAiDataDao params);
    List<OAIW001SelectAiDataDto> selectAiData(OAIW001SelectAiDataDao params);
    int insertAiCargoInData(OAIW001InsertAiCargoInDataDao params);
    int selectAiHeadCnt(OAIW001SelectAiHeadCntDao params);
    int insertAiCargoInHead1(OAIW001InsertAiCargoInHead1Dao params);
    int insertAiCargoInHead2(OAIW001InsertAiCargoInHead2Dao params);
    int updateAiData2(OAIW001UpdateAiData2Dao params);
    int updateAiHead1(OAIW001UpdateAiHead1Dao params);
    int insertAiStatusHistory1(@Param("aiStatusHistory1DaoList") List<OAIW001InsertAiStatusHistory1Dao> paramList);
    List<OAIW001SelectAiCargoInData1Dto> selectAiCargoInData1(OAIW001SelectAiCargoInData1Dao params);
    List<OAIW001SelectAiCargoInData2Dto> selectAiCargoInData2(OAIW001SelectAiCargoInData2Dao params);
    int updateAiData3(OAIW001UpdateAiData3Dao params);
    int selectAiDataCnt2(OAIW001SelectAiDataCnt2Dao params);
    int updateAiData4(OAIW001UpdateAiData4Dao params);
    int updateAiHead2(OAIW001UpdateAiHead2Dao params);
    List<OAIW001SelectAiCargoInData3Dto> selectAiCargoInData3(OAIW001SelectAiCargoInData3Dao params);
    List<OAIW001SelectAiCargoInData4Dto> selectAiCargoInData4(OAIW001SelectAiCargoInData4Dao params);
    int selectAiCargoInHeadCnt(OAIW001SelectAiCargoInHeadCntDao params);
    int insertAiStatusHistory3(OAIW001InsertAiStatusHistory3Dao params);
    int deleteAiCargoInHead(OAIW001SelectAiCargoInData1Dao params);
    int deleteAiCargoInData(OAIW001SelectAiCargoInData1Dao params);
}
