package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.dto.COMMONImportAddAccDto;
import com.kse.wmsv2.common.mapper.COMMONMapper;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001DetailDataDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class CommonImportService {

    @Autowired
    COMMONMapper mapper;

   @Transactional
   public boolean setAddProcess(COMMONImportAddAccDto param){
       boolean result = false;

       try{
           COMMONImportAddAccDto tmp = getAddProcess(param);
           param.setInClassifyClassName(tmp.getCargoIn());
           param.setOutClassifyClassName(tmp.getCargoOut());
           param.setDomesticClassifyClassName(tmp.getDomestic());

           int updateResult = mapper.setAddProcessValue(param);
           if(updateResult != 1){
               throw new Exception("ADD PROCESS UPDATE ERROR");
           }
       } catch (Exception e){
           // RollBack処理
           TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

           // エラー結果保存
           result = true;

           // ログ
           log.error(e.getMessage());
           e.printStackTrace();
       }

       return result;
   }



    public COMMONImportAddAccDto getAddProcess(COMMONImportAddAccDto mappingResult) {
        // returnValue
        COMMONImportAddAccDto returnVal = new COMMONImportAddAccDto();
        String cargoIn = "";
        String cargoOut = "";
        String domestic = "";


        try{
            if(mappingResult.isErrorFlg()){
                String reportCondition = mappingResult.getReportCondition();
                Map<String,String> paramMap = new HashMap<>();
                paramMap.put("awbNo" , mappingResult.getAwbNo());
                paramMap.put("cwbNo" , mappingResult.getCwbNo());
                COMMONImportAddAccDto tmp = mapper.getAddProcessValue(paramMap);
                if(!StringUtil.isStringEmpty(reportCondition)){
                    mappingResult.setReportCondition(reportCondition);
                } else {
                    mappingResult.setReportCondition(tmp.getReportCondition());
                }
                mappingResult.setTaxAmo(tmp.getTaxAmo());
                mappingResult.setDomesticClassifyClassName(tmp.getDomesticClassifyClassName());
                mappingResult.setInClassifyClassName(tmp.getInClassifyClassName());
                mappingResult.setOutClassifyClassName(tmp.getOutClassifyClassName());
            }

            String reportCondition = StringUtil.isStringNull(mappingResult.getReportCondition());
            boolean tax = StringUtil.stringParseInt(mappingResult.getTaxAmo()) > 0 ? true : false;
            boolean article = mapper.getOptionalService(mappingResult.getCwbNo()) > 0 ? true : false ;
            boolean sort = mapper.getConditionCount(mappingResult.getCwbNo()) > 0 ? true : false;

            if( !reportCondition.equals("S") && !reportCondition.equals("U")){
                if(article){
                    cargoIn = OAIT001CommonConstants.CARGO_SPECIAL;
                } else {
                    if(tax){
                        cargoIn = OAIT001CommonConstants.CARGO_REQUEST;
                    } else {
                        if(mappingResult.getIdaFlg().equals("1")){
                            cargoIn = OAIT001CommonConstants.CARGO_NO_TAX;
                        } else {
                            cargoIn = OAIT001CommonConstants.CARGO_MANIFEST;
                        }
                    }
                }
            } else {
                cargoIn = OAIT001CommonConstants.CARGO_NOT_ALLOW;
            }

            if(article || sort){
                cargoOut = OAIT001CommonConstants.CARGO_SPECIAL;
                domestic = OAIT001CommonConstants.CARGO_SPECIAL;
            } else {
                if(tax){
                    cargoOut = OAIT001CommonConstants.CARGO_REQUEST;
                    domestic = OAIT001CommonConstants.CARGO_REQUEST;
                } else {
                    cargoOut = OAIT001CommonConstants.CARGO_NO_TAX;
                    domestic = OAIT001CommonConstants.CARGO_NO_TAX;
                }
            }

            // 結果保存
            returnVal.setDomestic(domestic);
            returnVal.setCargoOut(cargoOut);
            returnVal.setCargoIn(cargoIn);
            returnVal.setErrorFlg(false);
        } catch (Exception e){
            returnVal.setDomestic(mappingResult.getDomesticClassifyClassName());
            returnVal.setCargoOut(mappingResult.getOutClassifyClassName());
            returnVal.setCargoIn(mappingResult.getInClassifyClassName());
            returnVal.setErrorFlg(true);
            e.printStackTrace();
        }
        return returnVal;
    }



}
