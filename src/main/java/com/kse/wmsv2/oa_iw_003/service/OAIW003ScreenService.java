package com.kse.wmsv2.oa_iw_003.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_iw_003.dao.*;
import com.kse.wmsv2.oa_iw_003.dto.*;
import com.kse.wmsv2.oa_iw_003.mapper.OAIW003ScreenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class OAIW003ScreenService {
    @Autowired
    OAIW003ScreenMapper oaiw003ScreenMapper;

    @Autowired
    private RedisUtil redisUtil;

    public int selectAiInventoryCnt(OAIW003SearchDataDao params) {
        return oaiw003ScreenMapper.selectAiInventoryCnt(params);
    }

    public List<OAIW003SearchResultDto> selectAiInventory(OAIW003SearchDataDao params) {
        return oaiw003ScreenMapper.selectAiInventory(params);
    }

    public List<OAIW003SearchResult2Dto> searchMawb(OAIW003SearchData2Dao params) {
        return oaiw003ScreenMapper.searchMawb(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public List<OAIW003SearchResultDto> insertMawb(List<OAIW003SearchData2Dao> params, String accessToken) {

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        /* AM_NAMEテーブル照会*/
        OAIW003SelectAmNameDao oaiw003SelectAmNameDao = new OAIW003SelectAmNameDao();
        oaiw003SelectAmNameDao.setDepartmentCd("ALL");
        oaiw003SelectAmNameDao.setNameClass("0008");
        oaiw003SelectAmNameDao.setNameCd(params.get(0).getBondedWareHouseCd());
        OAIW003SelectAmNameDto customsInfo = oaiw003ScreenMapper.selectAmName(oaiw003SelectAmNameDao);
        log.info("通関関連情報: ", customsInfo);

        /* 通関業者コード,通関場所コード取得*/
        String customsPlaceCd = customsInfo.getCharacterItem2();
        String customsTraderCd = customsInfo.getCharacterItem3();

        /* サーバーの日付取得*/
        String dateCurrent = DateUtil.getTimeStampNow();

        /* 条件設定 keyDataList*/
        List<OAIW003KeyDataDao> keyDataList = new ArrayList<OAIW003KeyDataDao>();
        for(OAIW003SearchData2Dao param : params){
            OAIW003KeyDataDao keyData = new OAIW003KeyDataDao();
            keyData.setInstAwbNo(param.getAwbNo());
            keyData.setInstArrFlt1(param.getArrFlt1());
            keyData.setInstArrFlt2(param.getArrFlt2());
            keyData.setBondedWareHouseCd(param.getBondedWareHouseCd());
            if(param.isObjectAll() == true){
                keyData.setInstObjectAll(true);
            } else {
                keyData.setInstObjectAWB(true);
            }
            keyDataList.add(keyData);
        }

        /* 対象データ抽出*/
        List<OAIW003SelectAiDataDto> targetDataList = new ArrayList<OAIW003SelectAiDataDto>();
        for(OAIW003KeyDataDao keyData : keyDataList){
            OAIW003SelectAiDataDto targetData= oaiw003ScreenMapper.selectTargetData(keyData);
            if(targetData != null){
                targetDataList.add(targetData);
            }
        }
        log.info("AI_DATA 対象データ: " + targetDataList);
        /* 対象データ件数が0の場合は処理終了*/
        if(targetDataList.size() < 1){
            return new ArrayList<OAIW003SearchResultDto>();
        }

        /* 条件設定 UPDATE用 AI_DATA*/
        List<OAIW003UpdateAiDataDao> updateDataList = new ArrayList<OAIW003UpdateAiDataDao>();
        for(OAIW003SelectAiDataDto targetData : targetDataList){
            OAIW003UpdateAiDataDao updateData = new OAIW003UpdateAiDataDao();

//            updateData.setAwbNo(targetData.getAwbNo());
//            updateData.setCwbNo(targetData.getCwbNo());
//            updateData.setCwbNoDeptCd(targetData.getCwbNodeptCd());
//            updateData.setInClassifyClassName(targetData.getInClassifyClassName());
//            updateData.setSpecialPrepareName01(targetData.getSpecialPrepareName01());
//            updateData.setSpecialPrepareName02(targetData.getSpecialPrepareName02());
//            updateData.setSpecialPrepareName03(targetData.getSpecialPrepareName03());
//            updateData.setSpecialPrepareName04(targetData.getSpecialPrepareName04());
//            updateData.setSpecialPrepareName05(targetData.getSpecialPrepareName05());
//            updateData.setSpecialPrepareName06(targetData.getSpecialPrepareName06());
//            updateData.setSpecialPrepareName07(targetData.getSpecialPrepareName07());
//            updateData.setSpecialPrepareName08(targetData.getSpecialPrepareName08());
//            updateData.setSpecialPrepareName09(targetData.getSpecialPrepareName09());
//            updateData.setSpecialPrepareName10(targetData.getSpecialPrepareName10());
            updateData.setUpdateUserCd(userCd);
            updateData.setUpdateDate(dateCurrent);

            updateDataList.add(updateData);
        }
        log.info("AI_DATA UPDATE用データ: " + updateDataList);

        /* 条件設定 INSERT用 AI_INVENTORY*/
        List<OAIW003InsertAiInventoryDao> insertDataList = new ArrayList<OAIW003InsertAiInventoryDao>();
        for(OAIW003SelectAiDataDto targetData : targetDataList){
            OAIW003InsertAiInventoryDao insertData = new OAIW003InsertAiInventoryDao();

            insertData.setBondedWareHouseCd(targetData.getBondedWarehouseCd());
            insertData.setCwbNo(targetData.getCwbNo());
            insertData.setCwbNoDeptCd(targetData.getCwbNodeptCd());
            insertData.setRemodelingFlg(targetData.getRemodelingFlg());
            insertData.setOrigin("");
            insertData.setAwbNo(targetData.getAwbNo());
            insertData.setBwbNo(targetData.getBwbNo());
            insertData.setScanPicec("0");
            insertData.setClassifyClassName(targetData.getInClassifyClassName());
            insertData.setSpecialPrepareName01(targetData.getSpecialPrepareName01());
            insertData.setSpecialPrepareName02(targetData.getSpecialPrepareName02());
            insertData.setSpecialPrepareName03(targetData.getSpecialPrepareName03());
            insertData.setSpecialPrepareName04(targetData.getSpecialPrepareName04());
            insertData.setSpecialPrepareName05(targetData.getSpecialPrepareName05());
            insertData.setSpecialPrepareName06(targetData.getSpecialPrepareName06());
            insertData.setSpecialPrepareName07(targetData.getSpecialPrepareName07());
            insertData.setSpecialPrepareName08(targetData.getSpecialPrepareName08());
            insertData.setSpecialPrepareName09(targetData.getSpecialPrepareName09());
            insertData.setSpecialPrepareName10(targetData.getSpecialPrepareName10());
            insertData.setOverFlg("0");
            insertData.setHandyTerminalId("");
            insertData.setRegDate(dateCurrent);
            insertData.setRegUserCd(userCd);

            if(params.get(0).isObjectAll() == true){
                insertData.setObjectFlg("1");
                insertData.setFlt1("000000");
                insertData.setFlt2("00000");
            } else {
                insertData.setObjectFlg("2");
                insertData.setFlt1(targetData.getCurrentArrFlt1());
                insertData.setFlt2(targetData.getCurrentArrFlt2());
            }

            if(StringUtils.isEmpty(targetData.getCargoInPiece().trim())){
                insertData.setDataPicec("-1");
            } else {
                insertData.setDataPicec(targetData.getCargoInPiece());
            }

            if(StringUtils.isEmpty(targetData.getCargoWeight().trim())){
                insertData.setDataWeight("-1");
            } else {
                insertData.setDataWeight(targetData.getCargoWeight());
            }

            insertDataList.add(insertData);
        }
        log.info("AI_INVENTORY INSERT用データ: " + insertDataList);


        /* 条件設定 INSERT用 AI_INVENTORY_HEAD*/

        /* 対象データ取得*/
        List<OAIW003SelectAiData2Dto> targetDataList2 = new ArrayList<OAIW003SelectAiData2Dto>();
        for(OAIW003KeyDataDao keyData : keyDataList){
            OAIW003SelectAiData2Dto targetData2 = oaiw003ScreenMapper.selectTargetData2(keyData);
            if(targetData2 != null){
                targetDataList2.add(targetData2);
            }
        }
        log.info("AI_DATA 対象データ2: " + targetDataList2);

        /* 対象データ件数が0の場合は処理終了*/
        if(targetDataList2.size() < 1){
            return new ArrayList<OAIW003SearchResultDto>();
        }


        /* 条件設定 INSERT用 AI_INVENTORY_HEAD*/
        List<OAIW003InsertAiInventoryHeadDao> insertDataList2 = new ArrayList<OAIW003InsertAiInventoryHeadDao>();
        if(params.get(0).isObjectAll() == true){
            // 全権
            int tmpCwbCount = 0;
            int tmpDataPiece = 0;
            Double tmpDataWeight = 0.0;

            for(OAIW003SelectAiData2Dto targetData : targetDataList2){
                tmpCwbCount += Integer.parseInt(targetData.getCwbCount());
                tmpDataPiece += Integer.parseInt(targetData.getDataPiece());
                tmpDataWeight += Double.parseDouble(targetData.getDataWeight());
            }

            OAIW003InsertAiInventoryHeadDao insertData2 = new OAIW003InsertAiInventoryHeadDao();
            insertData2.setBondedWareHouseCd(params.get(0).getBondedWareHouseCd());
            insertData2.setFlt1("000000");
            insertData2.setFlt2("00000");
            insertData2.setOrigin("000");
            insertData2.setAwbNo("00000000000");
            insertData2.setWorkStartTime(dateCurrent);
            insertData2.setObjectFlg("1");
            insertData2.setCwbCount(Integer.toString(tmpCwbCount));
            insertData2.setScanCwbCount("0");
            insertData2.setOverCwbCount("0");
            insertData2.setScanPiece("0");
            insertData2.setDataPiece(Integer.toString(tmpDataPiece));
            insertData2.setDataWeight(Double.toString(tmpDataWeight));
            insertData2.setRegDate(dateCurrent);
            insertData2.setRegUserCd(userCd);

            insertDataList2.add(insertData2);
        } else {
            //　AWB指定

            for(OAIW003SelectAiData2Dto targetData : targetDataList2){
                OAIW003InsertAiInventoryHeadDao insertData2 = new OAIW003InsertAiInventoryHeadDao();

                insertData2.setBondedWareHouseCd(targetData.getBondedWarehouseCd());
                insertData2.setFlt1(targetData.getFlt1());
                insertData2.setFlt2(targetData.getFlt2());
                insertData2.setWorkStartTime(dateCurrent);
                insertData2.setWorkEndTime(dateCurrent);
                insertData2.setOrigin(targetData.getOrigin());
                insertData2.setAwbNo(targetData.getAwbNo());
                insertData2.setObjectFlg("2");
                insertData2.setCwbCount(targetData.getCwbCount());
                insertData2.setScanCwbCount("0");
                insertData2.setOverCwbCount("0");
                insertData2.setScanPiece("0");
                insertData2.setDataPiece(targetData.getDataPiece());
                insertData2.setDataWeight(targetData.getDataWeight());
                insertData2.setRegDate(dateCurrent);
                insertData2.setDelFlg("0");

                insertDataList2.add(insertData2);
            }
        }
        log.info("AI_INVENTORY_HEAD INSERT用データ: " + insertDataList2);

        /* 条件設定 INSERT用 AI_STATUS_HISTORY AI_STATUS*/
        List<OAIW003InsertAiStatusHistoryDao> insertDataList3 = new ArrayList<OAIW003InsertAiStatusHistoryDao>();

        for(OAIW003SelectAiDataDto targetData : targetDataList){
            OAIW003InsertAiStatusHistoryDao insertData3 = new OAIW003InsertAiStatusHistoryDao();

            insertData3.setBusinessClass("02");
            if(StringUtils.isEmpty(targetData.getAwbNo().trim())){
                insertData3.setAwbNo("00000000000");
            } else {
                insertData3.setAwbNo(targetData.getAwbNo());
            }
            if(StringUtils.isEmpty(targetData.getBwbNo())){
                insertData3.setBwbNo("00000000000");
            } else {
                insertData3.setBwbNo(targetData.getBwbNo());
            }
            insertData3.setCwbNo(targetData.getCwbNo());
            insertData3.setCwbNoDeptCd(targetData.getCwbNodeptCd());
            insertData3.setStatusCd("IB00410");
            insertData3.setRegUserCd(userCd);
            insertData3.setRegDate(dateCurrent);
            insertData3.setUpdateUserCd(userCd);
            insertData3.setUpdateDate(dateCurrent);

            insertDataList3.add(insertData3);

        }
        log.info("AI_STATUS_HISTORY, AI_STATUS INSERT用データ: " + insertDataList3);

        /* データUPDATE, INSERT*/
        int updateCnt = 0;
        for(OAIW003UpdateAiDataDao updateData : updateDataList){
            int updateResultCnt = oaiw003ScreenMapper.updateAiData(updateData);
            updateCnt += updateResultCnt;
        }
        log.info("AI_DATA UPDATE件数: " + updateCnt + "件");

        int insertResultCnt1 = oaiw003ScreenMapper.insertAiInventory(insertDataList);
        log.info("AI_INVENTORY INSERT件数: " + insertResultCnt1 + "件");

        int insertResultCnt2 = oaiw003ScreenMapper.insertAiInventoryHead(insertDataList2);
        log.info("AI_INVENTORY_HEAD INSERT件数: " + insertResultCnt2 + "件");

        int insertResultCnt3 = oaiw003ScreenMapper.insertAiStatusHistory(insertDataList3);
        log.info("AI_STATUS_HISTORY INSERT件数: " + insertResultCnt3 + "件");

        int insertResultCnt4 = oaiw003ScreenMapper.insertAiStatus(insertDataList3);
        log.info("AI_STATUS INSERT件数: " + insertResultCnt4 + "件");


        /* Gridに追加データ出力*/
        List<OAIW003SearchResultDto> insertDataSearchList = new ArrayList<OAIW003SearchResultDto>();
        for(OAIW003InsertAiInventoryDao insertData : insertDataList){
            OAIW003SearchDataDao param = new OAIW003SearchDataDao();
            param.setWorkingDays(insertData.getRegDate());
            param.setAwbNo(insertData.getAwbNo());
            param.setArrFlt1(insertData.getFlt1());
            param.setArrFlt2(insertData.getFlt2());
            param.setBondedWareHouseCd(insertData.getBondedWareHouseCd());
            if("1".equals(insertData.getObjectFlg())){
                param.setObjectAll(true);
                param.setObjectAWB(false);
            } else {
                param.setObjectAll(false);
                param.setObjectAWB(true);
            }

            List<OAIW003SearchResultDto> searchResultDtoList = oaiw003ScreenMapper.selectAiInventory(param);
            for(OAIW003SearchResultDto dto : searchResultDtoList){
                insertDataSearchList.add(dto);
            }
        }
        log.info("検索結果に追加されるデータ: " + insertDataSearchList);

        return insertDataSearchList;

    }

    @Transactional(rollbackFor = Exception.class)
    public void end(OAIW003SearchResultDao param, String accessToken) throws Exception {
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        String bondedWareHouseCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        /* サーバーの日付取得*/
//        String dateCurrent = DateUtil.getTimeStampNow();

        /* AI_INVENTORY更新用データ設定*/
        OAIW003endKeyDataDao keyData = new OAIW003endKeyDataDao();
        if (!StringUtils.isEmpty(param.getWorkStartTime().trim())) {
            keyData.setWorkStartTime(param.getWorkStartTime());
        } else {
            keyData.setWorkStartTime("");
        }
//        keyData.setWorkEndTime(dateCurrent);
        if (!StringUtils.isEmpty(param.getAwbNo().trim())) {
            keyData.setAwbNo(param.getAwbNo());
        } else {
            keyData.setAwbNo("");
        }
        if (!StringUtils.isEmpty(param.getObjectFlg().trim())) {
            keyData.setObjectFlg(param.getObjectFlg());
        } else {
            keyData.setObjectFlg("");
        }

        if (!StringUtils.isEmpty(param.getFlt1().trim())) {
            keyData.setArrFlt1(param.getFlt1());
        } else {
            keyData.setArrFlt1("");
        }
        if (!StringUtils.isEmpty(param.getFlt2().trim())) {
            keyData.setArrFlt2(param.getFlt2());
        } else {
            keyData.setArrFlt2("");
        }
        if (!StringUtils.isEmpty(param.getObjectFlg().trim())) {
            keyData.setObjectFlg(param.getObjectFlg());
        } else {
            keyData.setObjectFlg("");
        }
        keyData.setBondedWareHouseCd(bondedWareHouseCd);
        log.info("AI_INVENTORY　更新用データ: " + keyData);

        /* 対象データ取得*/
        List<OAIW003SelectAiInventoryDto> searchResultDtoList = oaiw003ScreenMapper.selectAiInventory2(keyData);
        log.info("AI_INVENTORY　対象データ: " + searchResultDtoList);

        /* AI_STATUS, AI_STATUS_HISTORY更新用データ設定*/
        List<OAIW003InsertAiStatusHistoryDao> insertDataList = new ArrayList<OAIW003InsertAiStatusHistoryDao>();

        for(OAIW003SelectAiInventoryDto targetData : searchResultDtoList){
            OAIW003InsertAiStatusHistoryDao insertData = new OAIW003InsertAiStatusHistoryDao();

            insertData.setBusinessClass("02");
            if(StringUtils.isEmpty(targetData.getAwbNo().trim())){
                insertData.setAwbNo("00000000000");
            } else {
                insertData.setAwbNo(targetData.getAwbNo());
            }
            if(StringUtils.isEmpty(targetData.getBwbNo())){
                insertData.setBwbNo("00000000000");
            } else {
                insertData.setBwbNo(targetData.getBwbNo());
            }
            insertData.setCwbNo(targetData.getCwbNo());
            insertData.setCwbNoDeptCd(targetData.getCwbNoDeptCd());
            insertData.setStatusCd("IB00430");
            insertData.setRegUserCd(userCd);
//            insertData.setRegDate(dateCurrent);
            insertData.setUpdateUserCd(userCd);
//            insertData.setUpdateDate(dateCurrent);

            insertDataList.add(insertData);
        }
        log.info("AI_STATUS_HISTORY, AI_STATUS　INSERTデータ: " + insertDataList);

        /* AI_INVENTORY　対象データDELFLG更新*/
        int updateCnt = 0;
        for(OAIW003SelectAiInventoryDto dto : searchResultDtoList){
            OAIW003UpdateAiInventoryDao updateParam = new OAIW003UpdateAiInventoryDao();
            updateParam.setBondedWareHouseCd(dto.getBondedWareHouseCd());
            updateParam.setCwbNo(dto.getCwbNo());
            updateParam.setCwbNoDeptCd(dto.getCwbNoDeptCd());
//            updateParam.setWorkEndTime(dateCurrent);
            int updateResultCnt = oaiw003ScreenMapper.updateAiInventory(updateParam);
            updateCnt += updateResultCnt;
        }
        log.info("AI_INVENTORY　UPDATE件数: " + updateCnt + "件");


        /* AI_INVENTORY_HEAD　対象データDELFLG更新*/
        int updateCnt2 = 0;
        for(OAIW003SelectAiInventoryDto dto : searchResultDtoList){
            OAIW003UpdateAiInventoryHeadDao updateParam = new OAIW003UpdateAiInventoryHeadDao();
            updateParam.setBondedWareHouseCd(dto.getBondedWareHouseCd());
            updateParam.setFlt1(dto.getFlt1());
            updateParam.setFlt2(dto.getFlt2());
            updateParam.setAwbNo(dto.getAwbNo());
//            updateParam.setWorkEndTime(dateCurrent);
            int updateResultCnt2 = oaiw003ScreenMapper.updateAiInventoryHead(updateParam);
            updateCnt2 += updateResultCnt2;
        }
        log.info("AI_INVENTORY_HEAD　UPDATE件数: " + updateCnt2 + "件");

        int insertResultCnt3 = oaiw003ScreenMapper.insertAiStatusHistory(insertDataList);
        log.info("AI_STATUS_HISTORY　INSERT件数: " + insertResultCnt3 + "件");

        int insertResultCnt4 = oaiw003ScreenMapper.insertAiStatus(insertDataList);
        log.info("AI_STATUS　INSERT件数: " + insertResultCnt4 + "件");

    }

    public int searchAiInventory(OAIW003SearchData2Dao params) {
        return oaiw003ScreenMapper.searchAiInventory(params);
    }

    public int searchStatusCnt(OAIW003SearchStatusDao params) {
        return oaiw003ScreenMapper.searchStatusCnt(params);
    }

    public List<OAIW003SearchStatusDto> searchStatus(OAIW003SearchStatusDao params) {
        return oaiw003ScreenMapper.searchStatus(params);
    }
}
