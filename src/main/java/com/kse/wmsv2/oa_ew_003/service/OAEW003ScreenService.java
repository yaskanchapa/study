package com.kse.wmsv2.oa_ew_003.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.oa_ew_003.dao.*;
import com.kse.wmsv2.oa_ew_003.dto.*;
import com.kse.wmsv2.oa_ew_003.mapper.OAEW003ScreenMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.List;

@Service
public class OAEW003ScreenService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OAEW003ScreenMapper oaew003ScreenMapper;

    @Autowired
    StatusService stsServ;

    public List<OAEW003portSearchResultDao> searchPort(OAEW003portSearchDto params) {

        return oaew003ScreenMapper.searchPort(params);
    }

    public List<OAEW003flightSearchResultDao> searchFlight(OAEW003flightSearchDto params) {

        return oaew003ScreenMapper.searchFlight(params);
    }

    public List<OAEW003containerSearchResultDao> searchContainer(OAEW003containerSearchDto params) {

        return oaew003ScreenMapper.searchContainer(params);
    }

    public List<OAEW003SearchWarehouseDao> searchWarehouse(OAEW003SearchWarehouseDto params) {
        return oaew003ScreenMapper.searchWarehouse(params);
    }

    public OAEW003examinationSearchResultDao searchExamination(OAEW003examinationSearchDto params) {

        return oaew003ScreenMapper.searchExamination(params);
    }

    @Transactional
    public void updateExamination(OAEW003examinationUpdateDto params) throws Exception{
        try {
            int returnVal = oaew003ScreenMapper.updateExamination(params);
            if(returnVal !=1){
                throw new Exception("正常な更新が行えませんでした。もう一度スキャンしてください");
            }
            COMMONStatusDto statusDao = new COMMONStatusDto();
            String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
            statusDao.setBusinessClass("02");
            statusDao.setCwbNo(params.getCwbNo());
            statusDao.setBondStatusCd(params.getCurrentCargoStatusCd());
            statusDao.setHandyTerminalId(params.getTermNo());
            statusDao.setAwbNo(params.getAwbNo());
            statusDao.setUserCd(params.getRegUserCd());
            statusDao.setDate(date);
            // 共通部品へステータス送信
            stsServ.updateStatusMaster(statusDao);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
            logger.error(e.toString());
            throw new Exception(e.getMessage());
        }
    }

    public OAEW003stowageSearchResultDao searchStowage(OAEW003stowageSearchDto params) {

        return oaew003ScreenMapper.searchStowage(params);
    }

    @Transactional
    public void updateStowage(OAEW003stowageUpdateDto params,OAEW003stowageSearchDto param) throws Exception {
        try {
            int returnVal =  oaew003ScreenMapper.updateStowage(params);
            if(returnVal !=1){
                throw new Exception("正常な更新が行えませんでした。もう一度スキャンしてください");
            }
            COMMONStatusDto statusDao = new COMMONStatusDto();
            String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
            statusDao.setBusinessClass("02");
            statusDao.setCwbNo(param.getCwbNo());
            statusDao.setBondStatusCd(params.getCurrentCargoStatusCd());
            statusDao.setHandyTerminalId(params.getTermNo());
            statusDao.setAwbNo(params.getAwbNo());
            statusDao.setUserCd(params.getRegUserCd());
            statusDao.setDate(date);
            // 共通部品へステータス送信
            stsServ.updateStatusMaster(statusDao);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
            logger.error(e.toString());
            throw new Exception(e.getMessage());
        }
    }
}