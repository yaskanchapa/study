package com.kse.wmsv2.oa_iw_002.mapper;

import org.apache.ibatis.annotations.Mapper;
import com.kse.wmsv2.oa_iw_002.dao.*;
import com.kse.wmsv2.oa_iw_002.dto.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OAIW002ScreenMapper {
    List<OAIW002OutFilePathDao> selectOutFilePathList(OAIW002OutFilePathDto params);
    List<OAIW002SelectBondedOutListDto> selectBondedOutList(OAIW002SelectBondedOutListDao params);
    List<OAIW002SelectAiData1Dto> selectAiData1(OAIW002SelectAiData1Dao params);
    List<OAIW002SelectAiCargoOutHead1Dto> selectAiCargoOutHead1(OAIW002SelectAiCargoOutHead1Dao params);
    int insertAiCargoInData1(OAIW002InsertAiCargoInData1Dao params);
    int insertAiCargoOutData1(@Param("insertAiCargoOutData1DaoList") List<OAIW002InsertAiCargoOutData1Dao> insertAiCargoOutData1DaoList);
    List<OAIW002SelectAiCargoOutDataCnt1Dto> OAIW002SelectAiCargoOutDataCnt1Dao(OAIW002SelectAiCargoOutDataCnt1Dao params );
    OAIW002SelectAiCargoOutDataCnt1Dto selectAiCargoOutDataCnt1(OAIW002SelectAiCargoOutDataCnt1Dao params);
    int updateAiCargoOutHead1(OAIW002UpdateAiCargoOutHead1Dao params);
    int updateAiData1(OAIW002UpdateAiData1Dao params);
    int insertAiStatusHistory1(@Param("insertAiStatusHistory1DaoList") List<OAIW002InsertAiStatusHistory1Dao> insertAiStatusHistory1DaoList);
    int updateAiCargoOutData1(OAIW002UpdateAiCargoOutData1Dao params);
    List<OAIW002SelectAiCargoOutData1Dto> selectAiCargoOutData1(OAIW002SelectAiCargoOutData1Dao params);
    int deleteAiStatusHistory1(OAIW002DeleteAiStatusHistory1Dao params);
    int deleteAiCargoOutData1(OAIW002DeleteAiCargoOutData1Dao params);
    int updateAiCargoOutHead2(OAIW002UpdateAiCargoOutHead2Dao params);
    OAIW002SelectAiCargoOutHead2Dto selectAiCargoOutHead2(OAIW002SelectAiCargoOutHead2Dao params);

    int deleteAiCargoOutHead1(OAIW002DeleteAiCargoOutHead1Dao params);
    int updateAiData2(OAIW002UpdateAiData2Dao params);
//　 KNACCSにで該当するデータ(OT_IMPORTHD, OT_IMPORTHDACCにてBBM=='1'のデータ)が無いため、処理不要
//    int insertAiCargoInData2(OAIW002InsertAiCargoInData2Dao params);
//    int insertAiCargoOutData2(@Param("insertAiCargoOutData2DaoList") List<OAIW002InsertAiCargoOutData2Dao> insertAiCargoOutData2DaoList);
//    int updateAiCargoOutHead3(OAIW002UpdateAiCargoOutHead3Dao params);
//    int insertAiStatusHistory2(@Param("insertAiStatusHistory2DaoList") List<OAIW002InsertAiStatusHistory2Dao> insertAiStatusHistory2DaoList);
    List<OAIW002SelectAiCargoOutData2Dto> selectAiCargoOutData2(OAIW002SelectAiCargoOutData2Dao params);
    int deleteAiCargoOutData2(OAIW002DeleteAiCargoOutData2Dao params);
    List<OAIW002SelectAiData3Dto> selectAiData3(OAIW002SelectAiData3Dao params);
    OAIW002SelectAiCargoOutHead3Dto selectAiCargoOutHead3(OAIW002SelectAiCargoOutHead3Dao params);
    int updateAiCargoOutHead4(OAIW002UpdateAiCargoOutHead4Dao params);
    int deleteAiCargoOutHead2(OAIW002DeleteAiCargoOutHead2Dao params);
    int updateAiData3(OAIW002UpdateAiData3Dao params);
    int insertAiStatusHistory3(@Param("insertAiStatusHistory3DaoList") List<OAIW002InsertAiStatusHistory3Dao> insertAiStatusHistory3DaoList);
    int insertCsSendMessage(OAIW002InsertCsSendMessageDao insertCsSendMessageDaoList);
}
