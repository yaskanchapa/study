package com.kse.wmsv2.oa_it_004.service;


import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_004.common.OAIT004CommonConstants;
import com.kse.wmsv2.oa_it_004.dto.OAIT004OneShotDto;
import com.kse.wmsv2.oa_it_004.mapper.OAIT004ScreenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAIT004ScreenService extends OAIT004CommonConstants {

    @Autowired
    OAIT004COMMONService commonService;

    @Autowired
    OAIT004ScreenMapper mapper;

    @Autowired
    StatusService stsServ;

    public Map<String, List> searchOneShotDefList(HttpHeaders headers) {
        // リターン値
        Map<String,List> returnVal = new HashMap<>();
        List<Map> resultList = new ArrayList<>();
        Map<String, String> resultMap = new HashMap<>();

        try{
            // MAPPER用
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("deptCd",commonService.getDeptCd(headers));

            // 結果保存用
            // 通関業者リスト
            List<Map> traderList = new ArrayList<>() ;
            paramMap.put("class", CLASS_CUSTOMS_TRADER);
            traderList = mapper.getDefaultValueList(paramMap);
            returnVal.put("traderList", traderList);

            // 登録内容選択
            List<Map> registerList = new ArrayList<>() ;
            paramMap.put("class", CLASS_REGISTER);
            registerList = mapper.getDefaultValueList(paramMap);
            returnVal.put("registerList", registerList);

            // 通関状態
            List<Map> conditionList = new ArrayList<>() ;
            paramMap.put("class", CLASS_CUSTOMS_CONDITION);
            conditionList = mapper.getDefaultValueList(paramMap);
            returnVal.put("conditionList", conditionList);

            // 結果
            resultMap.put(RESULT_ERROR_FLAG,RESULT_SUCCESS);
        } catch (Exception e){
            //ログ
            log.error(e.getMessage());
            e.printStackTrace();

            // エラー結果保存
            resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
            resultMap.put(RESULT_MESSAGE,MSG_ERR_001);
        }
        resultList.add(resultMap);
        returnVal.put("result",resultList);
        return returnVal;
    }

    @Transactional
    public Map<String, String> insertOneShot(HttpHeaders headers, OAIT004OneShotDto oneShotDto) {
        // リターン値
        Map<String, String> resultMap = new HashMap<>();
        String date = DateUtil.getTimeStampNow();
        String user = commonService.getUserCd(headers);
        int totalReuslt = 0;

        try{
            // 入力値チェック
            if(StringUtil.isStringEmpty(oneShotDto.getTrader())){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_004);
                return resultMap;
            } else if(StringUtil.isStringEmpty(oneShotDto.getRegister())){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_006);
                return resultMap;
            } else if(StringUtil.isStringEmpty(oneShotDto.getCustomsCd())){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_005);
                return resultMap;
            }
            // 未製造
            if(oneShotDto.getRegister().equals("2")){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_WARNING);
                resultMap.put(RESULT_MESSAGE,MSG_WAR_001);
                return resultMap;
            } else {
                if(StringUtil.isStringEmpty(oneShotDto.getCondition())){
                    resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                    resultMap.put(RESULT_MESSAGE,MSG_ERR_007);
                    return resultMap;
                }
            }

            // SQL用
            Map<String, String> paramMap = new HashMap<>();
            // 基本値設定
            paramMap.put("condition",oneShotDto.getCondition());
            paramMap.put("customsCd",oneShotDto.getCustomsCd());
            paramMap.put("comment", oneShotDto.getComment());
            paramMap.put("trader", oneShotDto.getTrader());
            paramMap.put("date",date);
            paramMap.put("user",user);

            //ステータス更新用
            Map<String, String> statusMap = new HashMap<>();
            statusMap.put("statusCd",oneShotDto.getCondition());
            statusMap.put("user",user);
            statusMap.put("date",date);

            List<Map<String,String>> cwbList = new ArrayList();
            cwbList = oneShotDto.getHawbList();
            if(cwbList.size() == 0){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_002);
                return resultMap;
            }

            for(int i = 0 ; i < cwbList.size(); i++){
                if(!StringUtil.isStringEmpty(cwbList.get(i).get("cwbNo"))){
                    paramMap.put("cwbNo",cwbList.get(i).get("cwbNo"));
                    if(StringUtil.isStringEmpty(cwbList.get(i).get("deptCd"))){
                        paramMap.put("cwbNoDeptCd",DEFAULT_CWB_DEPT_CD);
                    } else {
                        paramMap.put("cwbNoDeptCd",cwbList.get(i).get("deptCd"));
                    }
                    int insertResult = 0;
                    int updateResult = 0;
                    insertResult = mapper.insertOneShot(paramMap);
                    if(insertResult != 1){
                        throw new Exception();
                    }
                    updateResult = mapper.updateOneShot(paramMap);
                    if(updateResult != 1){
                        throw new Exception();
                    }

                    // ステータス更新
                    statusMap.put("cwbNo",paramMap.get("cwbNo"));
                    statusMap.put("cwbNoDeptCd",paramMap.get("cwbNoDeptCd"));
                    updateStatus(statusMap);

                    totalReuslt = totalReuslt + insertResult;
                }
            }
            resultMap.put(RESULT_ERROR_FLAG,RESULT_SUCCESS);
            resultMap.put(RESULT_MESSAGE,totalReuslt+MSG_SUC_001);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
            resultMap.put(RESULT_MESSAGE,MSG_ERR_003);

            // ログ
            log.error(e.getMessage());
        }

        return resultMap;
    }

    private void updateStatus(Map<String, String> statusMap) throws Exception{
        mapper.updateDocStatusMaster(statusMap);
        mapper.updateDocStatusHistory(statusMap);
    }

    public Map<String, String> deleteOneShot(HttpHeaders headers, OAIT004OneShotDto dto) {
        // リターン値
        Map<String, String> resultMap = new HashMap<>();
        int totalResult = 0;
        try{
            // 値チェック
            if(dto.getHawbList().size() <=1){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_002);
                return resultMap;
            } else if(StringUtil.isStringEmpty(dto.getCondition())){
                resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
                resultMap.put(RESULT_MESSAGE,MSG_ERR_008);
                return resultMap;
            }

            // SQL用
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("condition",dto.getCondition());

            // 処理
            List<Map<String,String>> cwbList = new ArrayList();
            cwbList = dto.getHawbList();
            for(int i = 0 ; i < cwbList.size(); i++){
                if(!StringUtil.isStringEmpty(cwbList.get(i).get("cwbNo"))){
                    paramMap.put("cwbNo",cwbList.get(i).get("cwbNo"));
                    if(StringUtil.isStringEmpty(cwbList.get(i).get("deptCd"))){
                        paramMap.put("cwbNoDeptCd",DEFAULT_CWB_DEPT_CD);
                    } else {
                        paramMap.put("cwbNoDeptCd",cwbList.get(i).get("deptCd"));
                    }
                    totalResult = totalResult + mapper.deleteOneShot(paramMap);
                }
            }
            resultMap.put(RESULT_ERROR_FLAG,RESULT_SUCCESS);
            resultMap.put(RESULT_MESSAGE,totalResult+MSG_SUC_002);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            resultMap.put(RESULT_ERROR_FLAG,RESULT_ERROR);
            resultMap.put(RESULT_MESSAGE,MSG_ERR_009);

            // ログ
            log.error(e.getMessage());
        }

        return resultMap;
    }
}
