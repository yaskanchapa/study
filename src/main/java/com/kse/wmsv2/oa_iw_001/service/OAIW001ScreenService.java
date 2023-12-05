package com.kse.wmsv2.oa_iw_001.service;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_iw_001.mapper.OAIW001ScreenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kse.wmsv2.oa_iw_001.dao.*;
import com.kse.wmsv2.oa_iw_001.dto.*;

import org.springframework.transaction.annotation.Transactional;

import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Slf4j
@Service
public class OAIW001ScreenService {
    @Autowired
    OAIW001ScreenMapper oaiw001ScreenMapper;

    @Autowired
    private RedisUtil redisUtil;

    public List<OAIW001SelectBondedInListDto> SelectBondedInList(OAIW001SelectBondedInListDao params) {
        return oaiw001ScreenMapper.SelectBondedInList(params);
    }

    public int selectAiDataCnt(OAIW001SelectAiDataCnt1Dao params) {
        return oaiw001ScreenMapper.selectAiDataCnt(params);
    }
    public int updateAiHead(OAIW001UpdateAiDataDao params) {
        return oaiw001ScreenMapper.updateAiHead(params);
    }
    public int updateAiData(OAIW001UpdateAiDataDao params) {
        return oaiw001ScreenMapper.updateAiData(params);
    }

    public List<OAIW001SelectAiDataDto> selectAiData(OAIW001SelectAiDataDao params) {
        return oaiw001ScreenMapper.selectAiData(params);
    }
    public int insertAiCargoInData(OAIW001InsertAiCargoInDataDao params) {
        return oaiw001ScreenMapper.insertAiCargoInData(params);
    }
    public int selectAiHeadCnt(OAIW001SelectAiHeadCntDao params) {
        return oaiw001ScreenMapper.selectAiHeadCnt(params);
    }
    public int insertAiCargoInHead1(OAIW001InsertAiCargoInHead1Dao params) {
        return oaiw001ScreenMapper.insertAiCargoInHead1(params);
    }
    public int insertAiCargoInHead2(OAIW001InsertAiCargoInHead2Dao params) {
        return oaiw001ScreenMapper.insertAiCargoInHead2(params);
    }
    public int updateAiData2(OAIW001UpdateAiData2Dao params) {
        return oaiw001ScreenMapper.updateAiData2(params);
    }
    public int updateAiHead1(OAIW001UpdateAiHead1Dao params) {
        return oaiw001ScreenMapper.updateAiHead1(params);
    }
    public int insertAiStatusHistory1(List<OAIW001InsertAiStatusHistory1Dao> paramList) {
        return oaiw001ScreenMapper.insertAiStatusHistory1(paramList);
    }
    public List<OAIW001SelectAiCargoInData1Dto> selectAiCargoInData1(OAIW001SelectAiCargoInData1Dao params) {
        return oaiw001ScreenMapper.selectAiCargoInData1(params);
    }
    public List<OAIW001SelectAiCargoInData2Dto> selectAiCargoInData2(OAIW001SelectAiCargoInData2Dao params) {
        return oaiw001ScreenMapper.selectAiCargoInData2(params);
    }
    public int updateAiData3(OAIW001UpdateAiData3Dao params) {
        return oaiw001ScreenMapper.updateAiData3(params);
    }

    public int selectAiDataCnt2(OAIW001SelectAiDataCnt2Dao params) {
        return oaiw001ScreenMapper.selectAiDataCnt2(params);
    }
    public int updateAiData4(OAIW001UpdateAiData4Dao params) {
        return oaiw001ScreenMapper.updateAiData4(params);
    }

    public int updateAiHead2(OAIW001UpdateAiHead2Dao params) {
        return oaiw001ScreenMapper.updateAiHead2(params);
    }
    public List<OAIW001SelectAiCargoInData3Dto> selectAiCargoInData3(OAIW001SelectAiCargoInData3Dao params) {
        return oaiw001ScreenMapper.selectAiCargoInData3(params);
    }
    public List<OAIW001SelectAiCargoInData4Dto> selectAiCargoInData4(OAIW001SelectAiCargoInData4Dao params) {
        return oaiw001ScreenMapper.selectAiCargoInData4(params);
    }
    public int selectAiCargoInHeadCnt(OAIW001SelectAiCargoInHeadCntDao params) {
        return oaiw001ScreenMapper.selectAiCargoInHeadCnt(params);
    }
    public int insertAiStatusHistory3(OAIW001InsertAiStatusHistory3Dao param) {
        return oaiw001ScreenMapper.insertAiStatusHistory3(param);
    }

    public int deleteAiCargoInHead(OAIW001SelectAiCargoInData1Dao params) {
        return oaiw001ScreenMapper.deleteAiCargoInHead(params);
    }
    public int deleteAiCargoInData(OAIW001SelectAiCargoInData1Dao params) {
        return oaiw001ScreenMapper.deleteAiCargoInData(params);
    }

    @Transactional(rollbackFor = Exception.class)
    public int registBusiness(HttpHeaders headers, List<OAIW001SelectAiDataCnt1Dao> params) throws Exception {
        String accessToken = (String)headers.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        int totalAiDataCnt = 0;
        for (OAIW001SelectAiDataCnt1Dao param : params) {
            OAIW001UpdateAiDataDao updateAiDataDao = new OAIW001UpdateAiDataDao();
            updateAiDataDao.setAwbNo(param.getAwbNo());
            updateAiDataDao.setArrFtl1(param.getArrFtl1());
            updateAiDataDao.setArrFtl2(param.getArrFtl2());
            updateAiDataDao.setUpdateDate(getTimeStampNow());
            updateAiDataDao.setUserCd(userCd);

            // 1.2 輸入通関ヘッダを更新する
            int aiHeadCnt = this.updateAiHead(updateAiDataDao);
            if(aiHeadCnt == 0) {
                String errMsg = "更新対象輸入通関ヘッダがありません。";
                log.error(errMsg + updateAiDataDao.toString());
                throw new Exception(errMsg + updateAiDataDao.toString());
            }

            // 1.3.1 輸入通関データのチェックを行う。
            param.setBondedWarehouseCd(departmentCd); // ログインユーザのDEPARTMENTCDセット
            int aiDataCnt = this.selectAiDataCnt(param);
            totalAiDataCnt += aiDataCnt;
            if (aiDataCnt > 0) {
                // 1.3.2 上記SQL文の結果が1以上の場合、輸入通関データを更新する
                int updateResult = this.updateAiData(updateAiDataDao);
                log.info("screenServ.updateAiData update cnt:" + updateResult);
            }
        }
        return totalAiDataCnt;
    }
    @Transactional(rollbackFor = Exception.class)
    public OAIW0001ResultDto bondedInSetTargetBusiness(HttpHeaders headers,
                                                       OAIW001SelectAiDataDao param) throws Exception {
        String accessToken = (String)headers.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        OAIW0001ResultDto resultDto = new OAIW0001ResultDto();

        //1.2 輸入通関データ(AI_DATA)から取込処理対象データを取得する。
        List<OAIW001SelectAiDataDto> selectAiDataDtoList = this.selectAiData(param);
        String now = getTimeStampNow();

        // 1.3 輸入搬入データ(AI_CARGO_IN_DATA)に登録するする。
        OAIW001InsertAiCargoInDataDao aiCargoInDataDao = new OAIW001InsertAiCargoInDataDao();
        aiCargoInDataDao.setUserCd(userCd);
        aiCargoInDataDao.setUpdateDate(now);
        aiCargoInDataDao.setAwbNo(param.getAwbNo());
        aiCargoInDataDao.setBondedWarehouseCd(param.getBondedWarehouseCd());
        aiCargoInDataDao.setArrFtl1(param.getArrFtl1());
        aiCargoInDataDao.setArrFtl2(param.getArrFtl2());
        int insertAiCargoInDataCnt = this.insertAiCargoInData(aiCargoInDataDao);
        log.info("screenServ.insertAiCargoInData insertAiCargoInDataCnt:" + insertAiCargoInDataCnt);

        // 1.4 輸入搬入データヘッダ(AI_AI_CARGO_IN_HEAD)を登録する。
        OAIW001SelectAiHeadCntDao aiHeadCntDao = new OAIW001SelectAiHeadCntDao();
        aiHeadCntDao.setAwbNo(param.getAwbNo());
        aiHeadCntDao.setBondedWarehouseCd(param.getBondedWarehouseCd());
        aiHeadCntDao.setArrFtl1(param.getArrFtl1());
        aiHeadCntDao.setArrFtl2(param.getArrFtl2());

        //1.4.1 登録対象データの存在チェックを行う。
        int aiHeadCnt = this.selectAiHeadCnt(aiHeadCntDao);
        log.info("screenServ.insertAiCargoInData aiHeadCnt:" + aiHeadCnt);

        // 1.4.2 「1.4.1の存在チェック」の結果が0の場合、下記のINSERT文で輸入搬入データヘッダ(AI_CARGO_IN_HEAD)を登録する。
        if (aiHeadCnt == 0) {
            OAIW001InsertAiCargoInHead1Dao aiCargoInHead1Dao = new OAIW001InsertAiCargoInHead1Dao();
            aiCargoInHead1Dao.setAwbNo(param.getAwbNo());
            aiCargoInHead1Dao.setBondedWarehouseCd(param.getBondedWarehouseCd());
            aiCargoInHead1Dao.setArrFtl1(param.getArrFtl1());
            aiCargoInHead1Dao.setArrFtl2(param.getArrFtl2());
            aiCargoInHead1Dao.setUserCd(userCd);
            aiCargoInHead1Dao.setUpdateDate(now);
            // Insert
            int insertAiCargoInHead1Cnt = this.insertAiCargoInHead1(aiCargoInHead1Dao);
            log.info("screenServ.insertAiCargoInHead1 insertAiCargoInHead1Cnt:" + insertAiCargoInHead1Cnt);

        } else { // 1.4.3 「1.4.1の存在チェック」の結果が0以外の場合、下記のINSERT文で輸入搬入データヘッダ(AI_CARGO_IN_HEAD)を登録する。
            OAIW001InsertAiCargoInHead2Dao aiCargoInHead2Dao = new OAIW001InsertAiCargoInHead2Dao();
            aiCargoInHead2Dao.setAwbNo(param.getAwbNo());
            aiCargoInHead2Dao.setBondedWarehouseCd(param.getBondedWarehouseCd());
            aiCargoInHead2Dao.setArrFtl1(param.getArrFtl1());
            aiCargoInHead2Dao.setArrFtl2(param.getArrFtl2());
            aiCargoInHead2Dao.setUserCd(userCd);
            aiCargoInHead2Dao.setUpdateDate(now);
            // Insert
            int insertAiCargoInHead2Cnt = this.insertAiCargoInHead2(aiCargoInHead2Dao);
            log.info("screenServ.insertAiCargoInHead2 insertAiCargoInHead2Cnt:" + insertAiCargoInHead2Cnt);
        }

        // 1.5 取込まれた「1.2の取込処理対象データ」と紐づく輸入通関データ(AI_DATA)を更新する。
        int updateAiData2Cnt = 0;
        for(OAIW001SelectAiDataDto selectAiDataDto: selectAiDataDtoList) {
            OAIW001UpdateAiData2Dao updateAiData2Dao = new OAIW001UpdateAiData2Dao();
            updateAiData2Dao.setAwbNo(selectAiDataDto.getAwbNo());
            updateAiData2Dao.setCwbNo(selectAiDataDto.getCwbNo());
            updateAiData2Dao.setCwbNoDeptCd(selectAiDataDto.getCwbNoDeptCd());
            updateAiData2Dao.setUserCd(userCd);
            updateAiData2Dao.setUpdateDate(now);
            // Update
            updateAiData2Cnt += this.updateAiData2(updateAiData2Dao);
        };
        log.info("screenServ.updateAiData2 updateAiData2Cnt:" + updateAiData2Cnt);

        if(selectAiDataDtoList.size() > 0) {
            //1.6 輸入通関データヘッダ(AI_HEAD)を更新する。
            OAIW001UpdateAiHead1Dao updateAiHead1Dao = new OAIW001UpdateAiHead1Dao();
            updateAiHead1Dao.setAwbNo(param.getAwbNo());
            updateAiHead1Dao.setTreatmentStartDate(now);
            updateAiHead1Dao.setArrFtl1(param.getArrFtl1());
            updateAiHead1Dao.setArrFtl2(param.getArrFtl2());
            updateAiHead1Dao.setUserCd(userCd);
            updateAiHead1Dao.setUpdateDate(now);

            // Update
            int updateAiHead1Cnt = this.updateAiHead1(updateAiHead1Dao);
            log.info("screenServ.updateAiHead1 updateAiHead1Cnt:" + updateAiHead1Cnt);
        }

        //1.7 取込まれた「1.2の取込処理対象データ」と紐づくデータを輸入申告状況履歴(AI_STATUS_HISTORY)に登録する。
        ArrayList<OAIW001InsertAiStatusHistory1Dao> aiStatusHistory1DaoList = new ArrayList<OAIW001InsertAiStatusHistory1Dao>();
        for (OAIW001SelectAiDataDto selectAiDataDto : selectAiDataDtoList) {
            OAIW001InsertAiStatusHistory1Dao aiStatusHistory1Dao = new OAIW001InsertAiStatusHistory1Dao();
            aiStatusHistory1Dao.setAwbNo(selectAiDataDto.getAwbNo());
            aiStatusHistory1Dao.setBwbNo(selectAiDataDto.getBwbNo());
            aiStatusHistory1Dao.setCwbNo(selectAiDataDto.getCwbNo());
            aiStatusHistory1Dao.setCwbNoDeptCd(selectAiDataDto.getCwbNoDeptCd());
            aiStatusHistory1Dao.setUserCd(userCd);
            aiStatusHistory1Dao.setUpdateDate(now);
            aiStatusHistory1DaoList.add(aiStatusHistory1Dao);
        }
        if(aiStatusHistory1DaoList.size() > 0) {
            // Insert
            int insertAiStatusHistory1Cnt = this.insertAiStatusHistory1(aiStatusHistory1DaoList);
            log.info("screenServ.insertAiStatusHistory1 insertAiStatusHistory1Cnt:" + insertAiStatusHistory1Cnt);
        }
        resultDto.setResult(true);
        resultDto.setMessage("バッチ対象処理が正常終了しました。");
        resultDto.setErrorMessage("");
        return resultDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public OAIW0001ResultDto bondedInUnSetTargetBusiness(HttpHeaders headers,
                                                         OAIW001SelectAiCargoInData1Dao param) throws Exception {
        String accessToken = (String)headers.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        OAIW0001ResultDto resultDto = new OAIW0001ResultDto();
        String now = getTimeStampNow();
        param.setBondedWarehouseCd(departmentCd); // BondedWarehouseCdにユーザーのdepartmentCdをセット

        // 1.2.1 処理対象データ１(輸入搬入データ)取得
        // 処理対象データ１(輸入搬入データ)
        List<OAIW001SelectAiCargoInData1Dto> aiCargoInData1List = this.selectAiCargoInData1(param); // Select11

        // 1.2.2 処理対象データ2(輸入搬入データ)取得
        OAIW001SelectAiCargoInData2Dao SelectAiCargoInData2Dao = new OAIW001SelectAiCargoInData2Dao();
        SelectAiCargoInData2Dao.setAwbNo(param.getAwbNo());
        SelectAiCargoInData2Dao.setBondedWarehouseCd(param.getBondedWarehouseCd());
        SelectAiCargoInData2Dao.setArrFtl1(param.getArrFtl1());
        SelectAiCargoInData2Dao.setArrFtl2(param.getArrFtl2());
        // 処理対象データ2(輸入搬入データ)
        List<OAIW001SelectAiCargoInData2Dto> aiCargoInData2List = this.selectAiCargoInData2(SelectAiCargoInData2Dao); // Select10

        // 1.3 処理対象データ１(輸入搬入データ)からデータ更新パラメータ１(輸入搬入データ)を作成する。
        // 1.4 処理対象データ１(輸入搬入データ)から更新SQL1を作成する。
        List<OAIW001UpdateAiData3Dao>  updateAiData3DaoList = new ArrayList<OAIW001UpdateAiData3Dao>();
        for(OAIW001SelectAiCargoInData1Dto aiCargoInData1 : aiCargoInData1List) {
            OAIW001UpdateAiData3Dao updateAiData3Dao = new OAIW001UpdateAiData3Dao();
            updateAiData3Dao.setAwbNo(aiCargoInData1.getAwbNo());
            updateAiData3Dao.setCwbNo(aiCargoInData1.getCwbNo());
            updateAiData3Dao.setCwbNoDeptCd(aiCargoInData1.getCwbNoDeptCd());
            updateAiData3Dao.setRemodelingFlg("0");
            updateAiData3Dao.setOverFlg(aiCargoInData1.getOverFlg());
            updateAiData3Dao.setCurrentCargoStatusCd(aiCargoInData1.getCurrentCargoStatusCd());
            updateAiData3Dao.setEntryType(aiCargoInData1.getEntryType());
            updateAiData3Dao.setUserCd(userCd);
            updateAiData3Dao.setUpdateDate(now);

            if (aiCargoInData1.getCurrentCargoStatusCd() != null && !aiCargoInData1.getCurrentCargoStatusCd().equals("")) {
                // checkCurrentCargoStatusCd >= "IB00200"
                if(aiCargoInData1.getCurrentCargoStatusCd().compareTo("IB00200") < 0){
                    updateAiData3Dao.setLagerOrSameCurrentCargoStatusCdThanIB00200("0");
                } else {
                    updateAiData3Dao.setLagerOrSameCurrentCargoStatusCdThanIB00200("1");
                }
                // currentCargoStatusCdの頭文字取得
                updateAiData3Dao.setCharAt1currentCargoStatusCd(aiCargoInData1.getCurrentCargoStatusCd().substring(0, 1));
            } else {
                updateAiData3Dao.setLagerOrSameCurrentCargoStatusCdThanIB00200("0");
            }

            // Set EntryType
            String overFlg = aiCargoInData1.getOverFlg();
            if(overFlg != null && overFlg.equals("2")) {
                updateAiData3Dao.setEntryType("2");
            } else {
                updateAiData3Dao.setEntryType(aiCargoInData1.getEntryType());
            }

            // Set CargoInScanPiece, CargoInPiece, ReportCondition
            String scanPiece = aiCargoInData1.getScanPicec();
            String cargoPiece = aiCargoInData1.getCargoPiece();
            String cargoInScanPiece = aiCargoInData1.getCargoInScanPiece();
            if (scanPiece != null &&scanPiece.equals("1") &&
                    cargoPiece != null && cargoPiece.equals("1")) {
                updateAiData3Dao.setCargoInScanPiece(aiCargoInData1.getCargoInScanPiece());
                updateAiData3Dao.setCargoInPiece(scanPiece);
                updateAiData3Dao.setReportCondition(aiCargoInData1.getReportCondition());
            } else {
                int iScanPiece = 0;
                int iCargoInScanPiece = 0;
                try { iScanPiece = Integer.parseInt(scanPiece); } catch(Exception e1) { /*do nothing*/ };
                try { iCargoInScanPiece = Integer.parseInt(cargoInScanPiece); } catch(Exception e2) { /*do nothing*/ };

                if(iScanPiece + iCargoInScanPiece > 0) {
                    updateAiData3Dao.setCargoInScanPiece(String.valueOf(iScanPiece + iCargoInScanPiece));
                } else {
                    updateAiData3Dao.setCargoInScanPiece("0");
                }
                updateAiData3Dao.setCargoInPiece(aiCargoInData1.getCargoInPiece());
                String classifyClassName = aiCargoInData1.getClassifyClassname();
                if(classifyClassName != null
                        && (classifyClassName.equals("未許可") || classifyClassName.equals("S/U検査"))) {
                    updateAiData3Dao.setReportCondition("Z");
                } else {
                    updateAiData3Dao.setReportCondition(aiCargoInData1.getReportCondition());
                }
            }
            updateAiData3DaoList.add(updateAiData3Dao);
        };


        // 1.6 業者イメージ(AM_NAME)から更新パラメータ3(reportPersonCd, destination)を取得する。
        OAIW001SelectAmNameDao selectAmNameDao = new OAIW001SelectAmNameDao();
        selectAmNameDao.setDepartmentCd(departmentCd);

        // 更新パラメータ5(輸入申告状況履歴データ)
        List<OAIW001InsertAiStatusHistory3Dao> insertAiStatusHistory3DaoList = new ArrayList<OAIW001InsertAiStatusHistory3Dao>();
        // 更新SQL2(輸入通関データクリア)
        List<OAIW001UpdateAiData4Dao> UpdateAiData4DaoList = new ArrayList<OAIW001UpdateAiData4Dao>();

        // 1.7.1 処理対象データ2(輸入搬入データ)と紐づく輸入通関データ(AI_DATA)の存在チェックを行う。
        for(OAIW001SelectAiCargoInData2Dto aiCargoInData2 : aiCargoInData2List) {
            OAIW001SelectAiDataCnt2Dao selectAiDataCnt2Dao = new OAIW001SelectAiDataCnt2Dao();
            selectAiDataCnt2Dao.setAwbNo(aiCargoInData2.getAwbNo());
            selectAiDataCnt2Dao.setCwbNo(aiCargoInData2.getCwbNo());
            selectAiDataCnt2Dao.setCwbNo(aiCargoInData2.getCwbNoDeptCd());

            int aiDataCnt2 = this.selectAiDataCnt2(selectAiDataCnt2Dao);

            if(aiDataCnt2 != 0) {
                // 1.7.3 「1.7.1存在チェック」結果が0以外の場合、更新SQL2(輸入通関データクリア)を作成する。
                // UpdateAiData4DaoList
                OAIW001UpdateAiData4Dao updateAiData4Dao = new OAIW001UpdateAiData4Dao();
                updateAiData4Dao.setAwbNo(aiCargoInData2.getAwbNo());
                updateAiData4Dao.setCwbNo(aiCargoInData2.getCwbNo());
                updateAiData4Dao.setCwbNoDeptCd(aiCargoInData2.getCwbNoDeptCd());
                updateAiData4Dao.setUserCd(userCd);
                updateAiData4Dao.setUpdateDate(now);
                UpdateAiData4DaoList.add(updateAiData4Dao);
            }
        };

        // 1.9 輸入通関データヘッダをクリアする更新SQL4を作成する。
        OAIW001UpdateAiHead2Dao updateAiHead2Dao = new OAIW001UpdateAiHead2Dao();
        updateAiHead2Dao.setAwbNo(param.getAwbNo());
        updateAiHead2Dao.setArrFtl1(param.getArrFtl1());
        updateAiHead2Dao.setArrFtl2(param.getArrFtl2());
        updateAiHead2Dao.setTreatmentEndDate(now);
        updateAiHead2Dao.setUserCd(userCd);
        updateAiHead2Dao.setUpdateDate(now);

        // 1.10 輸入通関データヘッダ(AI_HEAD)から更新パラメータ6(輸入搬入データ)を取得する。
        // 更新パラメータ6(輸入搬入データあり、かつ輸入貨物飛行機データあり)
        List<OAIW001SelectAiCargoInData3Dto> selectAiCargoInData3DtoList = new ArrayList<OAIW001SelectAiCargoInData3Dto>();
        OAIW001SelectAiCargoInData3Dao selectAiCargoInData3Dao = new OAIW001SelectAiCargoInData3Dao();
        selectAiCargoInData3Dao.setAwbNo(param.getAwbNo());
        selectAiCargoInData3Dao.setArrFtl1(param.getArrFtl1());
        selectAiCargoInData3Dao.setArrFtl2(param.getArrFtl2());
        selectAiCargoInData3DtoList = this.selectAiCargoInData3(selectAiCargoInData3Dao); //Select12

        // 1.11 輸入通関データヘッダ(AI_HEAD)から更新パラメータ7(輸入搬入データ)を取得する。
        List<OAIW001SelectAiCargoInData4Dto> selectAiCargoInData4DtoList = new ArrayList<OAIW001SelectAiCargoInData4Dto>();
        OAIW001SelectAiCargoInData4Dao selectAiCargoInData4Dao = new OAIW001SelectAiCargoInData4Dao();
        selectAiCargoInData4Dao.setAwbNo(param.getAwbNo());
        selectAiCargoInData4Dao.setArrFtl1(param.getArrFtl1());
        selectAiCargoInData4Dao.setArrFtl2(param.getArrFtl2());
        selectAiCargoInData4DtoList = this.selectAiCargoInData4(selectAiCargoInData4Dao); // Select13

        // 1.12 更新パラメータ6(輸入搬入データあり、かつ輸入貨物飛行機データあり)のデータを「1.7.2」にて作成した更新パラメータ5(輸入申告状況履歴データ)に追加する。
        // 更新パラメータ5(輸入申告状況履歴データ) : insertAiStatusHistory3DaoList
        for(OAIW001SelectAiCargoInData3Dto selectAiCargoInData3Dto : selectAiCargoInData3DtoList) {
            OAIW001InsertAiStatusHistory3Dao insertAiStatusHistory3Dao = new OAIW001InsertAiStatusHistory3Dao();
            insertAiStatusHistory3Dao.setImportExportClass("I");
            insertAiStatusHistory3Dao.setBusinessClass("02");
            insertAiStatusHistory3Dao.setAwbNo(selectAiCargoInData3Dto.getAwbNo());
            insertAiStatusHistory3Dao.setCwbNo(selectAiCargoInData3Dto.getCwbNo());
            insertAiStatusHistory3Dao.setBwbNo(selectAiCargoInData3Dto.getBwbNo());
            insertAiStatusHistory3Dao.setCwbNoDeptCd(selectAiCargoInData3Dto.getCwbNoDeptCd());
            insertAiStatusHistory3Dao.setStatusCd("IB00130");
            insertAiStatusHistory3Dao.setUpdateDate(now);
            insertAiStatusHistory3Dao.setUserCd(userCd);
            insertAiStatusHistory3DaoList.add(insertAiStatusHistory3Dao);
        };

        // 1.13 更新パラメータ7(輸入搬入データあり、かつ輸入貨物飛行機データなし)のデータを「1.7.2」にて作成した更新パラメータ5(輸入申告状況履歴データ)に追加する。
        for(OAIW001SelectAiCargoInData4Dto selectAiCargoInData4Dto : selectAiCargoInData4DtoList) {
            OAIW001InsertAiStatusHistory3Dao insertAiStatusHistory3Dao = new OAIW001InsertAiStatusHistory3Dao();
            insertAiStatusHistory3Dao.setImportExportClass("I");
            insertAiStatusHistory3Dao.setBusinessClass("02");
            insertAiStatusHistory3Dao.setAwbNo(selectAiCargoInData4Dto.getAwbNo());
            insertAiStatusHistory3Dao.setCwbNo(selectAiCargoInData4Dto.getCwbNo());
            insertAiStatusHistory3Dao.setBwbNo(selectAiCargoInData4Dto.getBwbNo());
            insertAiStatusHistory3Dao.setCwbNoDeptCd(selectAiCargoInData4Dto.getCwbNoDeptCd());
            insertAiStatusHistory3Dao.setStatusCd("IB00130");
            insertAiStatusHistory3Dao.setUpdateDate(now);
            insertAiStatusHistory3Dao.setUserCd(userCd);
            insertAiStatusHistory3DaoList.add(insertAiStatusHistory3Dao);
        };

        // 1.14 輸入通関データヘッドに一覧画面のデータが存在するかチェックを行う。
        OAIW001SelectAiCargoInHeadCntDao selectAiCargoInHeadCntDao = new OAIW001SelectAiCargoInHeadCntDao();
        selectAiCargoInHeadCntDao.setAwbNo(param.getAwbNo());
        selectAiCargoInHeadCntDao.setBondedWarehouseCd(param.getBondedWarehouseCd());
        selectAiCargoInHeadCntDao.setArrFtl1(param.getArrFtl1());
        selectAiCargoInHeadCntDao.setArrFtl2(param.getArrFtl2());
        int aiCargoInHeadCnt = this.selectAiCargoInHeadCnt(selectAiCargoInHeadCntDao);

        //1.15 輸入通関データヘッドデータチェックの結果が0件の場合、「E006：搬入対象外」処理を中断する。
        if (aiCargoInHeadCnt == 0 ) {
            // ・エラーメッセージ出力(M008)
            // ・全DB関連処理をロールバック(RollBack)する。
            String errMsg = "AI_CARGO_IN_HEADのデータが0件です。";
            log.error(errMsg);
            throw new Exception(errMsg);
        } else {
            // 1.16 更新SQL1(輸入通関データクリア)のデータ件数が1件以上の場合、更新SQL1を実行する。
            int updateAiData3cnt = 0;
            for (OAIW001UpdateAiData3Dao updateAiData3Dao : updateAiData3DaoList) {
                updateAiData3cnt += this.updateAiData3(updateAiData3Dao);
            }
            log.info("screenServ.updateAiData3 updateAiData3cnt:" + updateAiData3cnt);

            // 1.18 更新SQL2(輸入通関データクリア)のデータ件数が1件以上の場合、更新SQL2を実行する。
            int updateAiData4Cnt = 0;
            for (OAIW001UpdateAiData4Dao UpdateAiData4Dao : UpdateAiData4DaoList) {
                updateAiData4Cnt += this.updateAiData4(UpdateAiData4Dao);
            }
            log.info("screenServ.updateAiData4 updateAiData4:" + updateAiData4Cnt);

            // 1.19 更新SQL4(輸入通関データヘッダクリア)のデータ件数が1件以上の場合、更新SQL4を実行する。
            int updateAiHead2Cnt = this.updateAiHead2(updateAiHead2Dao);
            log.info("screenServ.updateAiHead2 updateAiHead2:" + updateAiHead2Cnt);

            // 1.20 更新パラメータ5(輸入申告状況履歴データ)のデータ件数が1件以上の場合、輸入申告状況履歴テーブル(AI_STATUS_HISTORY)に登録する。
            if (insertAiStatusHistory3DaoList.size() > 0) {
                int insertAiStatusHistory3Cnt = 0;
                for(OAIW001InsertAiStatusHistory3Dao dao : this.getListWithoutDuplicates(insertAiStatusHistory3DaoList)) {// 重複行削除
                    insertAiStatusHistory3Cnt += this.insertAiStatusHistory3(dao);
                }
                log.info("screenServ.insertAiStatusHistory3 insertAiStatusHistory3:" + insertAiStatusHistory3Cnt);
            }

            // 1.21 輸入搬入データヘッダ（AI_CARGO_IN_HEAD）を削除する。
            int deleteAiCargoInHeadcnt = this.deleteAiCargoInHead(param);
            log.info("screenServ.deleteAiCargoInHead deleteAiCargoInHeadcnt:" + deleteAiCargoInHeadcnt);

            // 1.22 輸入搬入データ（AI_CARGO_IN_DATA）を削除する。
            int deleteAiCargoInDatacnt = this.deleteAiCargoInData(param);
            log.info("screenServ.deleteAiCargoInData deleteAiCargoInDatacnt:" + deleteAiCargoInDatacnt);

            resultDto.setResult(true);
            resultDto.setMessage("バッチ対象外処理が正常終了しました。");
            resultDto.setErrorMessage("");
            return resultDto;
        }
    }

    private List<OAIW001InsertAiStatusHistory3Dao> getListWithoutDuplicates(List<OAIW001InsertAiStatusHistory3Dao> queryItems) {
        Map<String, OAIW001InsertAiStatusHistory3Dao> filteringMap = new HashMap<String, OAIW001InsertAiStatusHistory3Dao>();
        for (OAIW001InsertAiStatusHistory3Dao item: queryItems) {
            String key = item.getBusinessClass() + item.getAwbNo() + item.getBwbNo()
                            + item.getCwbNo() + item.getCwbNoDeptCd() + item.getStatusCd()
                            + item.getUserCd() + item.getUpdateDate();
            if (filteringMap.containsKey(key)) {
                continue;
            } else {
                filteringMap.put(key, item);
            }
        }
        return new ArrayList<OAIW001InsertAiStatusHistory3Dao>(filteringMap.values());
    }
}
