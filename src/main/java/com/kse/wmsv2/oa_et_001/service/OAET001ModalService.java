package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001EDADetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001EDAHeaderDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001MECDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001EDADto;
import com.kse.wmsv2.oa_et_001.dto.OAET001ErrorReportDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReturnDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ModalMapper;
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
public class OAET001ModalService extends OAET001CommonConstants{

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    OAET001ModalMapper modalMapper;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReturnDto insertMecData(HttpHeaders headers, OAET001MECDao param) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();

        try{
            // 重要度
            int priority = customsCheckPriorityMEC(headers,param);
            param.setCustomsCheckPriority(String.valueOf(priority));
            // AE_DATA更新
            modalMapper.updateMecData(param);
            // ステータス更新
            COMMONStatusDto statusDao = new COMMONStatusDto();
            String userCd = commonService.getUserCd(headers);
            String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(MEC_REGISTER);
            statusDao.setLinkDataClass("0");
            statusDao.setBusinessClass("02");
            statusDao.setAwbNo(param.getAwbNo());
            statusDao.setCwbNo(param.getCwbNo());
            statusDao.setCwbNoDeptCd("000");
            statusDao.setBwbNo("00000000000");
            stsServ.updateStatusMaster(statusDao);

            // リターン値設定
            returnVal.setMessage(MSG_SUC_201);
            returnVal.setErrFlg(RESULT_SUCCESS);
        }catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラーログ出力
            e.printStackTrace();
            log.error(e.getMessage());

            // リターン値設定
            returnVal.setMessage(MSG_ERR_201);
            returnVal.setErrFlg(RESULT_ERROR);
        }
        return returnVal;
    }

    public OAET001ReturnDto checkMecData(OAET001MECDao param) {
        OAET001ReturnDto result = new OAET001ReturnDto();
        String message = "";

        try{

            // 必須項目チェック
            if(StringUtil.isStringEmpty(param.getCwbNo())){
                message = MSG_ERR_202;
                result.setErrFlg(RESULT_ERROR);

            }

            // 輸出者電話番号チェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getExpCustomerTelNo()).length() > 11){
                message = MSG_ERR_203;
                result.setErrFlg(RESULT_ERROR);
            }

            // 編集済みチェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getEditFlg()).equals("1")){
                message = MSG_WAR_201;
                result.setErrFlg(RESULT_WARNING);
            }

            // 通関済みチェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getCustomsCheckClass()).equals("1")){
                message = MSG_WAR_202;
                result.setErrFlg(RESULT_WARNING);
            }

            // 輸出者チェック
            if(StringUtil.isStringEmpty(message)){
                String[] conditionList = modalMapper.selectExpCustomer(param);
                for(String val : conditionList){
                    if(StringUtil.isStringNull(val).equals("1")){
                        message = MSG_ERR_204;
                        result.setErrFlg(RESULT_ERROR);
                        break;
                    }
                }
            }

            // 結果保存
            if(StringUtil.isStringEmpty(message)){
                result.setErrFlg(RESULT_SUCCESS);
            } else {
                result.setMessage(message);
            }

        } catch (Exception e){
            e.printStackTrace();
            result.setErrFlg(RESULT_ERROR);
            result.setMessage(e.getMessage());
            log.error(e.getMessage());
        }
        return result;
    }

    @Transactional
    public OAET001ReturnDto insertEdaData(HttpHeaders headers, OAET001EDADto param) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();

        try{
            String userCd = commonService.getUserCd(headers);
            String date = DateUtil.getTimeStampNow();
            // Priority値セット
            int priority = customsCheckPriorityEDA(headers,param);
            param.getHeaderData().setCustomsCheckPriority(String.valueOf(priority));

            // AE_DATA更新
            modalMapper.updateEdaHeaderData(param.getHeaderData());

            // AE_DATA_DETAIL更新
            List<OAET001EDADetailDao> detailList;
            int detailCnt = 0;
            detailList = param.getDetailDataList();
            for (OAET001EDADetailDao oaet001EDADetailDao : detailList) {
                oaet001EDADetailDao.setDate(date);
                oaet001EDADetailDao.setUser(userCd);
                detailCnt = detailCnt + modalMapper.updateEdaDetailData(oaet001EDADetailDao);
            }

            // ステータス更新
            COMMONStatusDto statusDao = new COMMONStatusDto();
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(EDA_REGISTER);
            statusDao.setLinkDataClass("0");
            statusDao.setBusinessClass("02");
            statusDao.setAwbNo(param.getHeaderData().getAwbNo());
            statusDao.setCwbNo(param.getHeaderData().getCwbNo());
            statusDao.setCwbNoDeptCd("000");
            statusDao.setBwbNo("00000000000");
            stsServ.updateStatusMaster(statusDao);

            // リターン値設定
            returnVal.setMessage(MSG_SUC_301);
            returnVal.setErrFlg(RESULT_SUCCESS);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラーログ出力
            e.printStackTrace();
            log.error(e.getMessage());

            // リターン値設定
            returnVal.setMessage(MSG_ERR_301);
            returnVal.setErrFlg(RESULT_ERROR);
        }
        return  returnVal;
    }


    public OAET001ReturnDto checkEdaData(OAET001EDADto params) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        String message = "";
        OAET001EDAHeaderDao param = params.getHeaderData();
        try{
            // 必須項目チェック
            if(StringUtil.isStringEmpty(param.getCwbNo())){
                message = MSG_ERR_302;
                returnVal.setErrFlg(RESULT_ERROR);

            }
            // 輸出者電話番号チェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getExpCustomerTelNo()).length() > 11){
                message = MSG_ERR_303;
                returnVal.setErrFlg(RESULT_ERROR);
            }
            // 編集済みチェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getEditFlg()).equals("1")){
                message = MSG_WAR_301;
                returnVal.setErrFlg(RESULT_WARNING);
            }

            // 通関済みチェック
            if(StringUtil.isStringEmpty(message)  && StringUtil.isStringNull(param.getCustomsCheckClass()).equals("1")){
                message = MSG_WAR_302;
                returnVal.setErrFlg(RESULT_WARNING);
            }
            if(StringUtil.isStringEmpty(message)){
                returnVal.setErrFlg(RESULT_SUCCESS);
            } else {
                returnVal.setMessage(message);
            }
        } catch (Exception e){
            e.printStackTrace();
            returnVal.setErrFlg(RESULT_ERROR);
            returnVal.setMessage(e.getMessage());
            log.error(e.getMessage());
        }
        return  returnVal;
    }


    private int customsCheckPriorityMEC(HttpHeaders headers,OAET001MECDao param) throws Exception{
        int resultVal = 0;

        // INVOICE特定品目
        resultVal = resultVal + checkInvoiceItem(param);

        // 特別手配
        resultVal = resultVal + checkSpecialOrder(param);


        // 業者No
        resultVal = resultVal + checkExporter(param);

        // 特定アカウント->未使用テーブルなので未実装

        // 特別手配
        resultVal = resultVal + checkSpecialExporter(param);

        // INVOICE明細行数
        resultVal = resultVal + countInvoiceDetailData(param);

        // INVOICE価格
        resultVal = resultVal + checkInvoiceAmount(param);

        return resultVal;
    }

    private int customsCheckPriorityEDA(HttpHeaders headers, OAET001EDADto param) throws Exception{
        int resultVal = 0;

        // INVOICE特定品目
        resultVal = resultVal + checkInvoiceItem(param);


        // 特別手配
        resultVal = resultVal + checkSpecialOrder(param);


        // 業者No
        resultVal = resultVal + checkExporter(param);

        // 特定アカウント->未使用テーブルなので未実装

        // 特別手配
        resultVal = resultVal + checkSpecialExporter(param);

        // INVOICE明細行数
        resultVal = resultVal + countInvoiceDetailData(param);

        // INVOICE価格
        resultVal = resultVal + checkInvoiceAmount(param);

        // 大額少額識別
        resultVal = resultVal + checkLargeSmallFlag(param);

        return resultVal;
    }




    private int checkInvoiceAmount(Object param) throws  Exception {
        int resultVal;
        Map<String,String> paramMap = new HashMap<>();
        if(param instanceof OAET001MECDao){
            paramMap.put("date",((OAET001MECDao) param).getExpReportPlanDate());
            paramMap.put("currencyCd",((OAET001MECDao) param).getCwbNo());
            paramMap.put("invoiceValue",((OAET001MECDao) param).getCwbNo());
        } else {
            paramMap.put("date",((OAET001EDADto) param).getHeaderData().getExpReportPlanDate());
            paramMap.put("currencyCd",((OAET001EDADto) param).getHeaderData().getFobCurrencyCd());
            paramMap.put("invoiceValue",((OAET001EDADto) param).getHeaderData().getFobAmo());
        }
        resultVal = modalMapper.checkInvoiceAmount(paramMap);
        if(resultVal > 0 ){
            resultVal = 5;
        }
        return resultVal;
    }

    private int countInvoiceDetailData(Object param) throws  Exception {
        int resultVal;
        Map<String,String> paramMap = new HashMap<>();
        if(param instanceof OAET001MECDao){
            paramMap.put("cwbNo",((OAET001MECDao) param).getCwbNo());
            paramMap.put("cwbNoDeptCd","000");
        } else {
            paramMap.put("cwbNo",((OAET001EDADto) param).getHeaderData().getCwbNo());
            paramMap.put("cwbNoDeptCd","000");
        }
        resultVal = modalMapper.countInvoiceDetailData(paramMap);
        if(resultVal > 0 ){
            resultVal = 5;
        }
        return resultVal;
    }

    private int checkSpecialExporter(Object param) throws Exception {
        int resultVal = 0;
        Map<String,String> paramMap = new HashMap<>();
        if(param instanceof OAET001MECDao){
            paramMap.put("expCustomerNo",((OAET001MECDao) param).getExpCustomerNo());
            paramMap.put("expCustomerDeptCd",((OAET001MECDao) param).getExpCustomerDeptCd());
            paramMap.put("expCustomerOcsDeptCd",((OAET001MECDao) param).getExpCustomerOcsDeptCd());

        } else {
            paramMap.put("expCustomerNo",((OAET001EDADto) param).getHeaderData().getExpCustomerNo());
            paramMap.put("expCustomerDeptCd",((OAET001EDADto) param).getHeaderData().getExpCustomerDeptCd());
            paramMap.put("expCustomerOcsDeptCd",((OAET001EDADto) param).getHeaderData().getExpCustomerOcsDeptCd());
        }
        Map<String,Object> exporter = modalMapper.checkSpecialExporter(paramMap);
        if(exporter != null){
            List<Object> valueList = new ArrayList<>(exporter.values());
            for(Object obj : valueList) {
                resultVal = resultVal + Integer.parseInt(obj.toString());
            }
        }
        return resultVal;
    }

    private int checkExporter(Object param) throws Exception {
        String exporterNo;
        int resultVal = 0;
        if(param instanceof OAET001MECDao){
            exporterNo = ((OAET001MECDao) param).getExpCustomerNo();
        } else {
            exporterNo = ((OAET001EDADto) param).getHeaderData().getExpCustomerNo();
        }
        if(StringUtil.isStringEmpty(exporterNo)){
            resultVal = 5;
        }
        return resultVal;
    }

    private int checkSpecialOrder(Object param) throws Exception {
        String[] searchList ;
        int resultVal = 0;
        if(param instanceof OAET001MECDao){
            searchList = modalMapper.checkSpecialOrder(((OAET001MECDao) param).getAwbNo());
        } else {
            searchList = modalMapper.checkSpecialOrder(((OAET001EDADto) param).getHeaderData().getAwbNo());
        }
        if(searchList != null){
            Map<String,String> paramMap = new HashMap<>();
            paramMap.put("deptCd","ALL");
            paramMap.put("classCd","0232");
            paramMap.put("column","NUMERICITEM4");
            for (String s : searchList) {
                paramMap.put("nameCd", s);
                resultVal = resultVal + modalMapper.getPriority(paramMap);
            }
        }
        return resultVal;
    }

    private int checkInvoiceItem(Object obj)throws  Exception{
        int resultVal = 0;
        String cwbNo;

        if(obj instanceof OAET001MECDao){
            cwbNo = ((OAET001MECDao)obj).getCwbNo();
        } else {
            cwbNo = ((OAET001EDADto) obj).getHeaderData().getAwbNo();
        }

        List<String> invoiceItemList = modalMapper.getInvoiceItem(cwbNo);
        for (String s : invoiceItemList) {
            int specialItemList = modalMapper.getSpecialItem(s);
            if (specialItemList > 0) {
                resultVal = resultVal + (specialItemList * 5);
            }
        }
        return resultVal;
    }


    private int checkLargeSmallFlag(OAET001EDADto param) throws Exception {
        int resultVal = 0;
        String largeSmallFlag = param.getHeaderData().getLargeSmallFlg();
        if(StringUtil.isStringNull(largeSmallFlag).equals("L")){
            resultVal = 10;
        }
        return resultVal;
    }


    public List<OAET001ErrorReportDto> searchErrorReport(String awbNo, String cwbNo, String type) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("awbNo",awbNo);
        paramMap.put("cwbNo", cwbNo);
        paramMap.put("type", type);
        return modalMapper.searchErrorReport(paramMap);
    }

    @Transactional
    public OAET001ReturnDto saveMecData(HttpHeaders headers, OAET001MECDao param) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        try {
            int result = modalMapper.saveMecData(param);
            if(result != 1){
                throw new Exception("Error 438");
            }

            returnVal.setMessage(MSG_SUC_202);
            returnVal.setErrFlg(RESULT_SUCCESS);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラーログ出力
            e.printStackTrace();
            log.error(e.getMessage());

            // リターン値設定
            returnVal.setMessage(MSG_ERR_205);
            returnVal.setErrFlg(RESULT_ERROR);

        }
        return returnVal;
    }


    @Transactional
    public OAET001ReturnDto saveEdaData(HttpHeaders headers, OAET001EDADto param) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        try {
            int mainResult = 0;
            mainResult = mainResult +modalMapper.saveEdaHeaderData(param.getHeaderData());
            if(mainResult != 1){
                throw new Exception("Error 467");
            }
            int detailResult = 0;
            List<OAET001EDADetailDao> detailDataList;
            detailDataList = param.getDetailDataList();
            for (OAET001EDADetailDao oaet001EDADetailDao : detailDataList) {
                int tmp = modalMapper.saveEdaDetailData(oaet001EDADetailDao) >= 1 ? 1 : 0;
                detailResult = detailResult + tmp;
            }

            returnVal.setMessage(MSG_SUC_302);
            returnVal.setErrFlg(RESULT_SUCCESS);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラーログ出力
            e.printStackTrace();
            log.error(e.getMessage());

            // リターン値設定
            returnVal.setMessage(MSG_ERR_305);
            returnVal.setErrFlg(RESULT_ERROR);

        }
        return returnVal;
    }
}
