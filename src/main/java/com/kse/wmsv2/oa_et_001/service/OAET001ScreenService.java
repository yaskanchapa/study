package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.dao.*;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReturnDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001UpdateCheckBoxDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ScreenMapper;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001StatusDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchResultDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import java.util.*;

@Slf4j
@Service
public class OAET001ScreenService extends OAET001CommonConstants{

    @Autowired
    OAET001ScreenMapper screenMapper;

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    StatusService stsService;

    public List getAwbNoList(HttpHeaders headers, OAET001SearchDto params) {
        Map<String,String> paramMap = new HashMap<>();
        List<Object> resultList = new ArrayList<>();
        try{
            paramMap.put("deptCd",commonService.getDeptCd(headers));
            paramMap.put("linkTruckNo",params.getLinkTruckNo().split(":")[0]);
            paramMap.put("truckNoDate",params.getTruckNoDate());
            resultList = screenMapper.getAwbNoList(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }


    public OAET001ReturnDto search(HttpHeaders headers, OAET001SearchDto params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        Map<String, String> paramMap = new HashMap<>();

        try{
            if(!checkParam(headers,params)){
                returnVal.setErrFlg(RESULT_ERROR);
                returnVal.setMessage(MSG_ERR_001);
                return returnVal;
            }
            paramMap = setParam(headers,params);
            List<OAET001SearchResultDao> searchResult = new ArrayList<>();
            searchResult = screenMapper.searchData(paramMap);
            if(searchResult.size() == 0 ){
                returnVal.setErrFlg(RESULT_WARNING);
                returnVal.setMessage(MSG_WAR_003);
                return returnVal;
            }
            returnVal.setErrFlg(RESULT_SUCCESS);
            returnVal.setMessage(MSG_SUC_004);
            returnVal.setSearchResult(searchResult);
        } catch (Exception e){
            log.error(e.getMessage());
            returnVal.setErrFlg(RESULT_ERROR);
            returnVal.setMessage(MSG_ERR_901);
        }
        return returnVal;
    }


    private Map<String, String> setParam(HttpHeaders headers, OAET001SearchDto params) {
        Map<String, String> paramMap = new HashMap<>();
        if(!StringUtil.isStringEmpty(params.getCwbNo())){
            paramMap.put("cwbNo", params.getCwbNo());
        } else {
            paramMap.put("awbNo", params.getAwbNo());
            paramMap.put("truckNoDate", params.getTruckNoDate());
            paramMap.put("linkTruckNo", params.getLinkTruckNo().split(":")[0]);
        }
        paramMap.put("deptCd",commonService.getDeptCd(headers));
        return  paramMap;
    }


    private boolean checkParam(HttpHeaders headers, OAET001SearchDto params) {
        boolean returnVal = true;
        if(StringUtil.isStringEmpty(params.getCwbNo())){
            if(StringUtil.isStringEmpty(params.getAwbNo()) || StringUtil.isStringEmpty(params.getLinkTruckNo())
                    || StringUtil.isStringEmpty(params.getTruckNoDate())){
                returnVal = false;
            }
        }
        return returnVal;
    }


    public List<OAIT001StatusDao> searchAllStatus(String cwbNo, String awbNo) {
        Map<String, String> paramMap = new HashMap<>();
        List<OAIT001StatusDao> resultList = new ArrayList<>();
        try{
            paramMap.put("awbNo",awbNo);
            paramMap.put("cwbNo",cwbNo);
            resultList = screenMapper.searchAllStatus(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }


    public OAET001MECDao selectMECDetail(HttpHeaders headers,OAET001SearchDto param) {
        Map<String,String> paramMap = new HashMap<>();
        OAET001MECDao returnDao = new OAET001MECDao();
        try{
            paramMap.put("awbNo",param.getAwbNo());
            paramMap.put("cwbNo",param.getCwbNo());
            paramMap.put("deptCd",commonService.getDeptCd(headers));
            returnDao = screenMapper.selectMECDetail(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return returnDao;
    }


    public OAET001EDAHeaderDao selectEDAHeader(HttpHeaders headers, OAIT001SearchDto param) {
        Map<String,String> paramMap = new HashMap<>();
        OAET001EDAHeaderDao returnDao = new OAET001EDAHeaderDao();
        try{
            paramMap.put("awbNo",param.getAwbNo());
            paramMap.put("cwbNo",param.getCwbNo());
            paramMap.put("deptCd",commonService.getDeptCd(headers));
            returnDao = screenMapper.selectEDAHeader(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return returnDao;
    }


    public List<OAET001EDADetailDao> selectEDADetail(HttpHeaders headers, OAIT001SearchDto param) {
        Map<String,String> paramMap = new HashMap<>();
        List<OAET001EDADetailDao> resultList = new ArrayList<>();

        try{
            paramMap.put("awbNo",param.getAwbNo());
            paramMap.put("cwbNo",param.getCwbNo());
            paramMap.put("deptCd",commonService.getDeptCd(headers));
            resultList =screenMapper.selectEDADetail(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }


    @Transactional
    public OAET001ReturnDto editExportData(HttpHeaders headers, List<OAET001SearchResultDao> params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        // データ更新用
        Map<String,String> paramMap = new HashMap<>();

        // ステータス更新用
        COMMONStatusDto stsDto = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = DateUtil.getTimeStampNow();
        stsDto.setUserCd(userCd);
        stsDto.setDate(date);
        stsDto.setLinkDataClass("0");
        stsDto.setBusinessClass("02");


        // メッセージ
        String message = "";

        // エラーFLAG
        boolean errorFlg = false;

        // 更新結果データ
        List<OAET001SearchResultDao> resultList = new ArrayList<>();

        // 結果確認用
        int updateCnt = 0;
        int stsCnt = 0;

        try{
            paramMap.put("userCd", commonService.getUserCd(headers));
            paramMap.put("date", DateUtil.getTimeStampNow());

            for(int i = 0; i < params.size(); i++){
                // パラメータ設定
                paramMap.put("expCustomerTelNo", params.get(i).getExpCustomerTelNo());
                paramMap.put("cwbNo",params.get(i).getCwbNo());
                if(params.get(i).getEda().equals("1")){
                    paramMap.put("stsCd", EDA_REGISTER);
                    stsDto.setCustomStatusCd(EDA_REGISTER);
                } else {
                    paramMap.put("stsCd", MEC_REGISTER);
                    stsDto.setCustomStatusCd(MEC_REGISTER);
                }

                // 結果計算
                int tmpCnt = 0;
                tmpCnt = screenMapper.editExportData(paramMap);
                updateCnt = updateCnt + tmpCnt;

                // STATUS更新
                stsDto.setAwbNo(params.get(i).getDataAwbNo());
                stsDto.setCwbNo(params.get(i).getCwbNo());
                stsService.updateStatusMaster(stsDto);
            }

            if(params.size() == updateCnt){
                message = MSG_SUC_001;
            } else {
                throw new Exception(MSG_ERR_101);
            }

        } catch (Exception e){
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            message = e.getMessage();
            errorFlg = true;
        }
        returnVal.setMessage(message);
        returnVal.setErrFlg(String.valueOf(errorFlg));
        returnVal.setSearchResult(resultList);
        return returnVal;
    }

    public Map<String,List> getUploadScreenDefaultValue() {
        List<Map> deptCdList = new ArrayList<>();
        List<Map> agecnyList = new ArrayList<>();
        Map<String,List> screenDefaultValue = new HashMap<>();

        try{
            deptCdList = screenMapper.selectDeptCdList();
            agecnyList = screenMapper.selectAgencyList();
            screenDefaultValue.put("deptCdList", deptCdList);
            screenDefaultValue.put("agencyList", agecnyList);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return screenDefaultValue;
    }

    public List<OAET001DefaultDao> getSearchDefaultList(OAET001SearchDto params) {
        Map<String, String> paramMap = new HashMap<>();
        List<OAET001DefaultDao> resultList = new ArrayList<>();
        try{
            paramMap.put("deptCd",  params.getCurrentDeptCd());
            paramMap.put("nameClass", params.getCurrentTabValue());
            resultList =screenMapper.selectDefault(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return resultList;
    }


    public OAET001ReturnDto searchAwbNo(OAET001SearchDto params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        int awbCnt = 0;

        try{
            awbCnt = screenMapper.searchAwbNo(params);
            if(awbCnt > 0){
                returnVal.setErrFlg(RESULT_ERROR);
                returnVal.setMessage(MSG_WAR_001);
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return returnVal;
    }

    @Transactional
    public OAET001ReturnDto editHeaderData(HttpHeaders headers, OAET001SearchResultDao params) {
        Map<String,String> paramMap = mappingUpdateSqlHeader(headers,params);
        OAET001ReturnDto returnDto = new OAET001ReturnDto();
        int updateCnt = 0;
        try{
            updateCnt = screenMapper.updateHeaderData(paramMap);
            if( updateCnt > 0 ){
                returnDto.setErrFlg(RESULT_SUCCESS);
                returnDto.setMessage(String.valueOf(updateCnt) + MSG_SUC_007);
            } else {
                returnDto.setErrFlg(RESULT_WARNING);
                returnDto.setMessage(MSG_WAR_002);
            }
        }catch (Exception e){
            // Rollback処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();
            // エラー結果保存
            returnDto.setErrFlg("error");
            returnDto.setMessage(MSG_ERR_004);
        }
        return returnDto;
    }

    private Map<String, String> mappingUpdateSqlHeader(HttpHeaders headers, OAET001SearchResultDao params){
        Map<String,String> returnMap = new HashMap<>();
        String user = commonService.getUserCd(headers);
        String date = DateUtil.getTimeStampNow();
        returnMap.put("expReportPlanDate",params.getExpReportPlanDate());
        returnMap.put("bonWareHoCurDate",params.getBonWareHoCurDate());
        returnMap.put("customsPlaceCd",params.getCustomsPlaceCd());
        returnMap.put("fltDestination",params.getFltDestination());
        returnMap.put("bondedWareHouseCd",params.getHeadBondedWarehouseCd());
        returnMap.put("flt1",params.getLoadingPlanFlt1());
        returnMap.put("flt2",params.getLoadingPlanFlt2());
        returnMap.put("depPort",params.getDepPort());
        returnMap.put("reportCondition",params.getHeadReportCondition());
        returnMap.put("inHouseRefNumber",params.getInHouseRefNumber());
        returnMap.put("updateUserCd",user);
        returnMap.put("updateDate",date);
        returnMap.put("awbNo",params.getAwbNo());
        return returnMap;
    }


    public Map<String,List> searchHeaderDefList(HttpHeaders headers) {
        // リターン値
        Map<String,List> returnVal = new HashMap<>();
        // MAPPER用
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd",commonService.getDeptCd(headers));

        try{
            // 結果保存用
            List<Map> customsList = new ArrayList<>() ;
            paramMap.put("class",CUSTOMS_CLASS);
            customsList = screenMapper.getListHeader(paramMap);
            returnVal.put("customsList", customsList);

            List<Map> bonList = new ArrayList<>();
            paramMap.put("class",WAREHOUSE_CLASS);
            bonList = screenMapper.getListHeader(paramMap);
            returnVal.put("bonList", bonList);

            List<Map> depList = new ArrayList<>();
            paramMap.put("class",PORT_CLASS);
            depList = screenMapper.getListHeader(paramMap);
            returnVal.put("depList", depList);

            List<Map> agencyList = new ArrayList<>();
            paramMap.put("class",AGENCY_CLASS);
            paramMap.put("deptCd","");

            agencyList = screenMapper.getListHeader(paramMap);
            returnVal.put("agencyList", agencyList);
        } catch (Exception e){
            log.error(e.getMessage());
        }

        return returnVal;
    }

    @Transactional
    public OAET001ReturnDto deleteExportData(HttpHeaders headers, List<OAET001SearchResultDao> params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        int deleteCnt = 0;
        try{
            for(int i = 0; i < params.size(); i++){
                deleteCnt = deleteCnt + screenMapper.deleteExportData(params.get(i));
            }
            returnVal.setErrFlg(RESULT_SUCCESS);
            returnVal.setMessage(MSG_SUC_008);
        } catch (Exception e){
            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();

            // Rollback処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // 結果保存
            returnVal.setErrFlg(RESULT_ERROR);
            returnVal.setMessage(MSG_ERR_005);
        }
        return returnVal;
    }

    @Transactional
    public OAET001ReturnDto changeColumnValue(HttpHeaders headers, OAET001UpdateCheckBoxDto boxValue) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        try{
            int result = screenMapper.changeColumnValue(boxValue);
            if(result == 1){
                returnVal.setErrFlg("false");
                returnVal.setMessage(MSG_SUC_001);
            } else {
                throw new Exception("Col Update Error 421");
            }
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラー結果保存
            returnVal.setErrFlg("error");
            returnVal.setMessage(MSG_ERR_011);

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }

    public OAET001ReturnDto getOtherSearchInfo(OAET001SearchDto params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        try{
            Map<String, String> resultMap = new HashMap<>();
            resultMap = screenMapper.getOtherSearchInfo(params.getAwbNo());
            params.setTruckNoDate(resultMap.get("TRUCKNODATE"));
            params.setLinkTruckNo(resultMap.get("LINKTRUCKNO"));
            returnVal.setSearchCond(params);
            returnVal.setErrFlg(RESULT_SUCCESS);
        } catch (Exception e){
            e.printStackTrace();
            returnVal.setErrFlg(RESULT_WARNING);
        }
        return returnVal;
    }
}
