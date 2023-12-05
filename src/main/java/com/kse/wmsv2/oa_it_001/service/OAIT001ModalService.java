package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONImportAddAccDto;
import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.mapper.COMMONMapper;
import com.kse.wmsv2.common.service.CommonImportService;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.*;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ErrorReportDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReturnDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001ModalMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAIT001ModalService extends OAIT001CommonConstants {

    @Autowired
    OAIT001ModalMapper modalMapper;

    @Autowired
    COMMONMapper commonMapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    StatusService stsServ;

    @Autowired
    ReportService reportServ;

    @Autowired
    CommonImportService commonImportService;


    @Transactional
    public OAIT001ReturnDto insertMicData(HttpHeaders headers, OAIT001MICDao param) {
        OAIT001ReturnDto resultVal = new OAIT001ReturnDto();
        Map<String,String> keyParam = getKeyParam(param);
        int updateResult = 0;
        int updateHeadResult = 0;
        String resultMsg = "";
        String resultFlg = "false";
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = DateUtil.getTimeStampNow();

        try{
            // 必須チェック
            if(commonService.checkPkKey(param.getAwbNo(),param.getCwbNo(),param.getCwbNoDeptCd())){
                resultMsg = OAIT001CommonConstants.MSG_ERR_999;
                throw new Exception(OAIT001CommonConstants.MSG_ERR_999);
            }

            // 入力値チェック
            String msg = "";
            msg = checkModalValue(param);
            if(!StringUtil.isStringEmpty(msg)){
                resultVal= commonService.setResult(param,msg,"error");
                return resultVal;
            }

            // 重要度
            int priority = getCustomsCheckPriorityMIC(param);
            param.setCustomsCheckPriority(String.valueOf(priority));

            // ADD Proc
            COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
            accDto.setAwbNo(param.getAwbNo());
            accDto.setCwbNo(param.getCwbNo());
            accDto.setReportCondition(param.getReportCondition());
            accDto.setIdaFlg("0");
            accDto.setErrorFlg(true);
            accDto = commonImportService.getAddProcess(accDto);
            param.setInClassifyClassName(accDto.getCargoIn());
            param.setOutClassifyClassName(accDto.getCargoOut());
            param.setDomesticClassifyClassName(accDto.getDomestic());

            // AI_DATA更新
            updateResult = updateMICData(param);
            // AI_HEAD更新
            updateHeadResult = updateMICHeadData(keyParam);
            // 更新結果確認
            if(updateResult != 1 || updateHeadResult != 1){
                resultMsg = "データ更新処理でエラーが発生しました。";
            }

            // ヘッダ更新
            int updateHdCnt = reportServ.updateImportHeader(param.getAwbNo(), param.getCurrentArrFlt1(), param.getCurrentArrFlt2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            // STATUS更新
            statusDao.setBusinessClass("01");
            statusDao.setLinkDataClass("0");
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_EDIT_MIC);
            statusDao.setAwbNo(param.getAwbNo());
            statusDao.setCwbNo(param.getCwbNo());
            statusDao.setBwbNo(param.getBwbNo());
            statusDao.setCwbNoDeptCd(param.getCwbNoDeptCd());
            stsServ.updateStatusMaster(statusDao);

            // 結果メッセージ
            resultMsg = OAIT001CommonConstants.MSG_SUC_201;
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            resultFlg = "error";
            if(StringUtil.isStringEmpty(resultMsg)){
                resultMsg = OAIT001CommonConstants.MSG_ERR_202;
            }

            // ログ
            log.error(e.getMessage());
            resultMsg = "データ更新処理でエラーが発生しました。";
        } finally {
            resultVal= commonService.setResult(param,resultMsg,resultFlg);
        }
        return resultVal;
    }


    @Transactional
    public OAIT001ReturnDto insertIdaData(HttpHeaders headers, OAIT001IDADao param) {
        OAIT001ReturnDto resultVal = new OAIT001ReturnDto();

        String resultMsg = "";
        String resFlg = "false";
        boolean resultFlg = true;
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = DateUtil.getTimeStampNow();

        try{
            // 必須項目チェック
            if(commonService.checkPkKey(param.getMainDao().getAwbNo(),param.getMainDao().getCwbNo(),param.getMainDao().getCwbNoDeptCd())){
                resultMsg = OAIT001CommonConstants.MSG_ERR_999;
                throw new Exception(OAIT001CommonConstants.MSG_ERR_999);
            }

            // IDA作成チェック
            resultFlg = checkReportIda(param);
            if(!resultFlg){
                resultMsg = OAIT001CommonConstants.MSG_ERR_301;
                throw new Exception();
            }

            String msg = "";
            msg = checkModalValue(param);
            if(!StringUtil.isStringEmpty(msg)){
                resultVal= commonService.setResult(param,msg,"error");
                return resultVal;
            }

            // 重量計算
            param = checkIdaWeight(param);

            // ADD Proc
            COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
            accDto.setAwbNo(param.getMainDao().getAwbNo());
            accDto.setCwbNo(param.getMainDao().getCwbNo());
            accDto.setIdaFlg("1");
            accDto.setErrorFlg(true);
            accDto = commonImportService.getAddProcess(accDto);
            param.getMainDao().setInClassifyClassName(accDto.getCargoIn());
            param.getMainDao().setOutClassifyClassName(accDto.getCargoOut());
            param.getMainDao().setDomesticClassifyClassName(accDto.getDomestic());

            // 重要度重み付け
            param = getCustomsCheckPriorityIDA(param);

            // AI_DATAテーブル更新
            resultFlg = updateIDAMainData(param);
            if(!resultFlg){
                resultMsg = OAIT001CommonConstants.MSG_ERR_302;
                throw new Exception();
            }
            // AI_DATA_DETAILテーブル更新
            updateIDASubData(param);


            // CS_OPTIONALSERVICE特別手配更新
            //後で作成


            // HEAD更新
            resultFlg = updateIDAHeadData(param,userCd,date);
            if(!resultFlg){
                resultMsg = OAIT001CommonConstants.MSG_ERR_305;
                throw new Exception();
            }

            // ヘッダ更新
            int updateHdCnt = reportServ.updateImportHeader(param.getMainDao().getAwbNo(), param.getMainDao().getCurrentArrFLT1(), param.getMainDao().getCurrentArrFLT2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            // ステータス更新(IDA作成のみ)
            statusDao.setBusinessClass("01");
            statusDao.setLinkDataClass("0");
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_EDIT_IDA);
            statusDao.setAwbNo(param.getMainDao().getAwbNo());
            statusDao.setCwbNo(param.getMainDao().getCwbNo());
            statusDao.setBwbNo(param.getMainDao().getBwbNo());
            statusDao.setCwbNoDeptCd(param.getMainDao().getCwbNoDeptCd());
            stsServ.updateStatusMaster(statusDao);

            resultMsg = OAIT001CommonConstants.MSG_SUC_301;

        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            resFlg = "error";
            if(StringUtil.isStringEmpty(resultMsg)){
                resultMsg = OAIT001CommonConstants.MSG_ERR_307;
            }

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
            if(StringUtil.isStringEmpty(resultMsg)){
                resultMsg = "データ更新処理でエラーが発生しました。";
            }
        } finally {
            resultVal= commonService.setResult(param,resultMsg,resFlg);
        }
        return resultVal;
    }


    private boolean updateIDAHeadData(OAIT001IDADao param,String userCd,String date) {
        boolean resultVal = false;
        int headCnt = 0;
        int updateCnt = 0;
        headCnt = modalMapper.checkIDAHeaderData(param.getMainDao());
        if(headCnt == 0){
            resultVal = true;
            return resultVal;
        }

        Map<String, Object> paramMap = modalMapper.getIDAHeadData(param.getMainDao());
        paramMap.put("BEFOREFLT1", param.getMainDao().getCurrentArrFLT1());
        paramMap.put("BEFOREFLT2", param.getMainDao().getCurrentArrFLT2());
        paramMap.put("userCd",userCd);
        paramMap.put("date",date);
        updateCnt = modalMapper.updateIDAHeadData(paramMap);
        if(updateCnt > 0 ){
            resultVal = true;
        }
        return resultVal;
    }


    private boolean updateIDAMainData(OAIT001IDADao param) {
        OAIT001IDAMainDao mainDao = param.getMainDao();
        int mapperResult = 0;
        boolean resultVal = false;
        mapperResult = modalMapper.updateIDAMainData(mainDao);
        if(mapperResult == 1){
            resultVal = true;
        }
        return resultVal;
    }


    private void updateIDASubData(OAIT001IDADao param) throws Exception {
        List<OAIT001IDASubDao> subDaoList = param.getSubDao();
        boolean resultVal = false;
        int targetVal = subDaoList.size();
        int updateResult = 0;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("awbNo",param.getMainDao().getAwbNo());
        paramMap.put("cwbNo",param.getMainDao().getCwbNo());
        modalMapper.deleteIDASubData(paramMap);

        for(int i = 0; i < subDaoList.size(); i++){
            int updatetmp = 0;
            updatetmp = modalMapper.updateIDASubData(subDaoList.get(i));
            updateResult = updateResult + updatetmp;
        }

    }


    private OAIT001IDADao getCustomsCheckPriorityIDA(OAIT001IDADao param) {
        OAIT001IDAMainDao mainDao = param.getMainDao();

        int resultVal = 0;
        // INVOICE特定品目
        resultVal = resultVal + getInvoiceSpecialItem(mainDao);

        // 特別手配
        resultVal = resultVal + checkSpecialOrder(mainDao);

        // 業者Noマスタ
        resultVal = resultVal + checkImporter(mainDao);

        // 特定アカウント->未使用テーブルなので未実装

        // 特定業者
        resultVal = resultVal + checkSpecialImporter(mainDao);

        // INVOICE明細行数(重要度)
        resultVal = resultVal + countInvoiceDetailData(mainDao);

        // INVOICE価格(重要度)
        resultVal = resultVal + checkInvoiceAmount(mainDao);

        // 大額少額識別
        resultVal = resultVal + checkBigSmallFlag(mainDao);

        mainDao.setCustomsCheckPriority(String.valueOf(resultVal));
        param.setMainDao(mainDao);
        return param;
    }


    private int checkBigSmallFlag(OAIT001IDAMainDao mainDao) {
        int resultVal = 0;
        String bigSmallFlg = mainDao.getBigSmallFlg();
        if(StringUtil.isStringEmpty(bigSmallFlg)){
            resultVal = 10;
        }
        return resultVal;
    }


    private boolean checkReportIda(OAIT001IDADao param) {
        boolean resultFlg = false;
        int mapperResult = 0;
        Object obj = new OAIT001IDAMainDao();
        obj = param.getMainDao();
        mapperResult = modalMapper.checkReportIda(obj);
        if(mapperResult == 0){
            resultFlg = true;
        }
        return resultFlg;
    }


    private Map<String,String> getKeyParam(Object obj){
        Map<String,String> resultVal = new HashMap<>();
        if(obj instanceof OAIT001MICDao){
            OAIT001MICDao tmpDao = (OAIT001MICDao) obj;
            resultVal.put("awbNo",tmpDao.getAwbNo());
            resultVal.put("cwbNo",tmpDao.getCwbNo());
            resultVal.put("cwbNoDeptCd",tmpDao.getCwbNoDeptCd());
            resultVal.put("afterCurrentArrFlt1",tmpDao.getCurrentArrFlt1());
            resultVal.put("afterCurrentArrFlt2",tmpDao.getCurrentArrFlt2());
        } else {
            OAIT001IDADao tmpDao = (OAIT001IDADao)  obj;
            resultVal.put("awbNo",tmpDao.getMainDao().getAwbNo());
            resultVal.put("cwbNo",tmpDao.getMainDao().getCwbNo());
            resultVal.put("afterCurrentArrFlt1",tmpDao.getMainDao().getCurrentArrFLT1());
            resultVal.put("afterCurrentArrFlt2",tmpDao.getMainDao().getCurrentArrFLT2());
        }

        Map<String,String> beforeFltInfo = getCurrentArrFlt(resultVal);

        resultVal.put("beforeCurrentArrFlt1", beforeFltInfo.get("CURRENTARRFLT_1"));
        resultVal.put("beforeCurrentArrFlt2", beforeFltInfo.get("CURRENTARRFLT_2"));

        return resultVal;
    }


    private int updateMICHeadData(Map<String, String> keyParam) {
        return modalMapper.updateMicHeadData(keyParam);
    }


    private Map<String,String> getCurrentArrFlt(Map param){
        return modalMapper.getCurrentArrFlt(param);
    }


    private int updateMICData(OAIT001MICDao param){
        return modalMapper.updateMicData(param);
    }


    private int getCustomsCheckPriorityMIC(OAIT001MICDao param){
        Object obj = new OAIT001MICDao();
        obj = param;

        int resultVal = 0;
        // INVOICE特定品目
        resultVal = resultVal + getInvoiceSpecialItem(obj);

        // 特別手配
        resultVal = resultVal + checkSpecialOrder(obj);

        // 業者Noマスタ
        resultVal = resultVal + checkImporter(obj);

        // 特定アカウント->未使用テーブルなので未実装

        // 特定業者
        resultVal = resultVal + checkSpecialImporter(obj);

        // INVOICE明細行数(重要度)
        resultVal = resultVal + countInvoiceDetailData(obj);

        // INVOICE価格(重要度)
        resultVal = resultVal + checkInvoiceAmount(obj);

        return resultVal;
    }


    private int checkInvoiceAmount(Object param) {
        Map<String,String> paramMap = new HashMap<>();
        int resultVal = 0;
        String systemDate = commonMapper.getBusinessDate();

        if(param instanceof OAIT001MICDao){
            OAIT001MICDao dao = (OAIT001MICDao) param;
            paramMap.put("currencyCd", dao.getInvoiceCurCd());
            paramMap.put("invoiceValue", dao.getInvoiceValue());
            if(StringUtil.isStringEmpty(dao.getReportPlaningDate())){
                paramMap.put("date", commonMapper.getBusinessDate());
            } else {
                paramMap.put("date", dao.getReportPlaningDate());
            }
        } else {
            OAIT001IDAMainDao dao = (OAIT001IDAMainDao) param;
            paramMap.put("currencyCd", dao.getInvoiceCurCD());
            paramMap.put("invoiceValue", dao.getInvoiceValue());
            if(StringUtil.isStringEmpty(dao.getReportPlaningDate())){
                paramMap.put("date", commonMapper.getBusinessDate());
            } else {
                paramMap.put("date", dao.getReportPlaningDate());
            }
        }
        Map<String,Object> dataList = modalMapper.checkInvoiceAmount(paramMap);
        if(dataList != null){
            resultVal = 5;
        }
        return resultVal;
    }


    private int countInvoiceDetailData(Object param) {
        int resultVal = 0;
        Map<String,Object> dataList = modalMapper.countInvoiceDetailData(param);
        if(dataList != null){
            resultVal = 5;
        }
        return resultVal;
    }


    private int checkSpecialImporter(Object param) {
        int resultVal = 0;
        Map<String,Object> importer = modalMapper.checkSpecialImporter(param);
        if(importer != null){
            List<Object> valueList = new ArrayList<>(importer.values());
            for(Object obj : valueList) {
                resultVal = resultVal + Integer.parseInt(obj.toString());
            }
        }
        return resultVal;
    }


    private int checkImporter(Object param) {
        String importer = "";
        if(param instanceof OAIT001MICDao){
            importer = ((OAIT001MICDao) param).getImpCustomerNo();
        } else {
            importer = ((OAIT001IDAMainDao) param).getImpCustomerNo();
        }

        int resultVal = 0;
        if(!StringUtil.isStringEmpty(importer)){
            resultVal = 5;
        }
        return resultVal;
    }


    private int checkSpecialOrder(Object param) {
        Integer resultVal;
        if(param instanceof OAIT001MICDao){
            resultVal = modalMapper.checkSpecialOrder(((OAIT001MICDao) param).getCwbNo());
        } else {
            resultVal = modalMapper.checkSpecialOrder(((OAIT001IDAMainDao) param).getCwbNo());
        }
        if(resultVal != null){
            return resultVal.intValue()* 5;
        }
        return  0 ;
    }


    private int getInvoiceSpecialItem(Object param) {
        List<String> invoiceItemList = modalMapper.getInvoiceItem(param);
        int resultVal = 0;
        for(int i = 0; i < invoiceItemList.size(); i++){
            int specialItemList = modalMapper.getSpecialItem(invoiceItemList.get(i));
            if(specialItemList > 0){
                resultVal = resultVal + (specialItemList*5);
            }
        }
        return  resultVal;
    }


    public List<OAIT001ErrorReportDto> searchErrorReport(String awbNo, String cwbNo, String type) {
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("awbNo",awbNo);
        paramMap.put("cwbNo", cwbNo);
        paramMap.put("type", type);
        return modalMapper.searchErrorReport(paramMap);
    }


    @Transactional
    public OAIT001ReturnDto saveMicData(HttpHeaders headers, OAIT001MICDao param) {
        OAIT001ReturnDto returnVal = new OAIT001ReturnDto();
        String resultMsg = "";
        try{
            // 必須チェック
            if(commonService.checkPkKey(param.getAwbNo(),param.getCwbNo(),param.getCwbNoDeptCd())){
                resultMsg = OAIT001CommonConstants.MSG_ERR_999;
                throw new Exception(OAIT001CommonConstants.MSG_ERR_999);
            }

            String msg = "";
            msg = checkModalValue(param);
            if(!StringUtil.isStringEmpty(msg)){
                returnVal= commonService.setResult(param,msg,"error");
                return returnVal;
            }

            // ADD Proc
            COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
            accDto.setAwbNo(param.getAwbNo());
            accDto.setCwbNo(param.getCwbNo());
            accDto.setReportCondition(param.getReportCondition());
            accDto.setIdaFlg("0");
            accDto.setErrorFlg(true);
            accDto = commonImportService.getAddProcess(accDto);
            param.setInClassifyClassName(accDto.getCargoIn());
            param.setOutClassifyClassName(accDto.getCargoOut());
            param.setDomesticClassifyClassName(accDto.getDomestic());

            int result = modalMapper.saveMicData(param);
            if(result != 1){
                throw new Exception("Error 461");
            }

            // ヘッダ更新
            int updateHdCnt = reportServ.updateImportHeader(param.getAwbNo(), param.getCurrentArrFlt1(), param.getCurrentArrFlt2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            returnVal.setErrorFlg("false");
            returnVal.setMsg(OAIT001CommonConstants.MSG_SUC_202);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            returnVal.setErrorFlg("error");
            if(StringUtil.isStringEmpty(resultMsg)){
                returnVal.setMsg(OAIT001CommonConstants.MSG_ERR_202);
            } else {
                returnVal.setMsg(resultMsg);
            }

            // ログ
            log.error(e.getMessage());
        }
        return returnVal;
    }


    @Transactional
    public OAIT001ReturnDto saveIdaData(HttpHeaders headers, OAIT001IDADao param) {
        OAIT001ReturnDto returnVal = new OAIT001ReturnDto();
        String resultMsg = "";
        try{
            // 必須項目チェック
            if(commonService.checkPkKey(param.getMainDao().getAwbNo(),param.getMainDao().getCwbNo(),param.getMainDao().getCwbNoDeptCd())){
                resultMsg = OAIT001CommonConstants.MSG_ERR_999;
                throw new Exception(OAIT001CommonConstants.MSG_ERR_999);
            }

            String msg = "";
            msg = checkModalValue(param);
            if(!StringUtil.isStringEmpty(msg)){
                returnVal= commonService.setResult(param,msg,"error");
                return returnVal;
            }

            // 重量計算
            param = checkIdaWeight(param);

            // ADD Proc
            COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
            accDto.setAwbNo(param.getMainDao().getAwbNo());
            accDto.setCwbNo(param.getMainDao().getCwbNo());
            accDto.setIdaFlg("1");
            accDto.setErrorFlg(true);
            accDto = commonImportService.getAddProcess(accDto);
            param.getMainDao().setInClassifyClassName(accDto.getCargoIn());
            param.getMainDao().setOutClassifyClassName(accDto.getCargoOut());
            param.getMainDao().setDomesticClassifyClassName(accDto.getDomestic());

            int mainResult = modalMapper.saveIDAMainData(param.getMainDao());
            if(mainResult != 1){
                throw new Exception("Error 471");
            }

            int subResult = 0;
            List<OAIT001IDASubDao> subDaoList = new ArrayList<>();
            subDaoList = param.getSubDao();
            Map<String, String> paramMap = new HashMap<>();
            paramMap.put("awbNo",param.getMainDao().getAwbNo());
            paramMap.put("cwbNo",param.getMainDao().getCwbNo());
            modalMapper.deleteIDASubData(paramMap);
            for(int i = 0; i < subDaoList.size(); i++){
                subResult = subResult + modalMapper.saveIDASubData(subDaoList.get(i));
            }
//            if(subDaoList.size() != subResult){
//                throw new Exception("Error 472");
//            }
            // ヘッダ更新
            int updateHdCnt = reportServ.updateImportHeader(param.getMainDao().getAwbNo(), param.getMainDao().getCurrentArrFLT1(), param.getMainDao().getCurrentArrFLT2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            returnVal.setObj(param);
            returnVal.setErrorFlg("false");
            returnVal.setMsg(OAIT001CommonConstants.MSG_SUC_302);

        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //　エラー結果保存
            returnVal.setErrorFlg("error");
            if(StringUtil.isStringEmpty(resultMsg)){
                returnVal.setMsg(OAIT001CommonConstants.MSG_ERR_203);
            } else {
                returnVal.setMsg(resultMsg);
            }

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }


    public String checkModalValue(Object obj){
        // 結果値
        String errorMsg = "";

        OAIT001MICDao micDao = new OAIT001MICDao();
        OAIT001IDADao idaDao = new OAIT001IDADao();

        boolean idaMic = obj instanceof OAIT001IDADao ? true : false;
        if(idaMic){
            idaDao = (OAIT001IDADao) obj;
        } else {
            micDao = (OAIT001MICDao) obj;
        }

        // チェック対象
        String invoiceCondition = "";
        String discernmentMark = "";
        String reportPlaningDate = "";
        String fareFlg = "";
        String fareCurrencyCD = "";
        String fare = "";
        String insuranceClassCD = "";
        String insuranceCurCD = "";
        String insuranceAmount = "";
        String incInsuRegNo = "";
        String impCustomerNo = "";
        String impCustomerDeptCd = "";
        String impCustomerOcsDeptCd = "";
        String invoiceCurCD = "";
        String incRevBaseCurCD = "";

        if(idaMic){
            invoiceCondition = idaDao.getMainDao().getInvoiceValConCD();
            discernmentMark = idaDao.getMainDao().getDiscernmentMark();
            reportPlaningDate = idaDao.getMainDao().getReportPlaningDate();
            fareFlg = idaDao.getMainDao().getFareFlg();
            fareCurrencyCD = idaDao.getMainDao().getFareCurrencyCD();
            fare = idaDao.getMainDao().getFare();
            insuranceClassCD = idaDao.getMainDao().getInsuranceClassCD();
            insuranceCurCD = idaDao.getMainDao().getInsuranceCurCD();
            insuranceAmount = idaDao.getMainDao().getInsuranceAmount();
            incInsuRegNo = idaDao.getMainDao().getIncInsuRegNo();
            impCustomerNo = idaDao.getMainDao().getImpCustomerNo();
            impCustomerDeptCd = idaDao.getMainDao().getImpCustomerDeptCd();
            impCustomerOcsDeptCd = idaDao.getMainDao().getImpCustomerOcsDeptCd();
            invoiceCurCD = idaDao.getMainDao().getInvoiceCurCD();
            incRevBaseCurCD = idaDao.getMainDao().getIncRevBaseCurCD();
        } else {
            invoiceCondition = micDao.getInvoiceValConCd();
            discernmentMark = micDao.getDiscernmentMark();
            reportPlaningDate = micDao.getReportPlaningDate();
            fareFlg = micDao.getFareFlg();
            fareCurrencyCD = micDao.getFareCurrencyCd();
            fare = micDao.getFare();
            insuranceClassCD = micDao.getInsuranceClassCd();
            insuranceCurCD = micDao.getInsuranceCurCd();
            insuranceAmount = micDao.getInsuranceAmount();
            impCustomerNo = micDao.getImpCustomerNo();
            impCustomerDeptCd = micDao.getImpCustomerDeptCd();
            impCustomerOcsDeptCd = micDao.getImpCustomerOcsDeptCd();
            invoiceCurCD = micDao.getInvoiceCurCd();
            incRevBaseCurCD = CURRENCY_CODE_JPY;
        }

        if(invoiceCondition.equals(INVOICE_VALUE_CD_CIF)){

            if( !(StringUtil.isStringEmpty(fareFlg) && StringUtil.isStringEmpty(fareCurrencyCD) && StringUtil.isStringEmpty(fare)
                    && StringUtil.isStringEmpty(insuranceClassCD) && StringUtil.isStringEmpty(insuranceCurCD)
                        && StringUtil.isStringEmpty(insuranceAmount) && StringUtil.isStringEmpty(incInsuRegNo))){
                errorMsg = MSG_ERR_501;
                return errorMsg;
            }

        } else if (invoiceCondition.equals(INVOICE_VALUE_CD_CF)){

            if(idaMic){
                if( !(StringUtil.isStringEmpty(fareFlg) && StringUtil.isStringEmpty(fareCurrencyCD) && StringUtil.isStringEmpty(fare))){
                    errorMsg = MSG_ERR_502;
                    return errorMsg;
                }
                if((StringUtil.isStringEmpty(insuranceClassCD) || StringUtil.isStringEmpty(insuranceCurCD)
                        || StringUtil.isStringEmpty(insuranceAmount) || StringUtil.isStringEmpty(incInsuRegNo))){
                    errorMsg = MSG_ERR_502;
                    return errorMsg;
                }
            } else {
                if( !(StringUtil.isStringEmpty(fareFlg) && StringUtil.isStringEmpty(fareCurrencyCD) && StringUtil.isStringEmpty(fare))){
                    errorMsg = MSG_ERR_502;
                    return errorMsg;
                }
                if((StringUtil.isStringEmpty(insuranceClassCD) || StringUtil.isStringEmpty(insuranceCurCD)
                        || StringUtil.isStringEmpty(insuranceAmount))){
                    errorMsg = MSG_ERR_502;
                    return errorMsg;
                }
            }
        } else if (invoiceCondition.equals(INVOICE_VALUE_CD_CI)){

            if(idaMic){
                if( (StringUtil.isStringEmpty(fareFlg) || StringUtil.isStringEmpty(fareCurrencyCD) || StringUtil.isStringEmpty(fare))){
                    errorMsg = MSG_ERR_503;
                    return errorMsg;
                }
                if( !(StringUtil.isStringEmpty(insuranceClassCD) && StringUtil.isStringEmpty(insuranceCurCD)
                        && StringUtil.isStringEmpty(insuranceAmount) && StringUtil.isStringEmpty(incInsuRegNo))){
                    errorMsg = MSG_ERR_503;
                    return errorMsg;
                }
            } else {
                if( (StringUtil.isStringEmpty(fareFlg) || StringUtil.isStringEmpty(fareCurrencyCD) || StringUtil.isStringEmpty(fare))){
                    errorMsg = MSG_ERR_503;
                    return errorMsg;
                }
                if( !(StringUtil.isStringEmpty(insuranceClassCD) && StringUtil.isStringEmpty(insuranceCurCD)
                        && StringUtil.isStringEmpty(insuranceAmount))){
                    errorMsg = MSG_ERR_503;
                    return errorMsg;
                }
            }

        }

        if(discernmentMark.equals("1")){
            if( StringUtil.isStringEmpty(impCustomerNo) || StringUtil.isStringEmpty(impCustomerDeptCd)
                    || StringUtil.isStringEmpty(impCustomerOcsDeptCd) ){
                errorMsg = MSG_ERR_504;
                return errorMsg;
            }
        } else if (discernmentMark.equals("2") || discernmentMark.equals("3")){
            if( !StringUtil.isStringEmpty(impCustomerNo) && !StringUtil.isStringEmpty(impCustomerDeptCd)
                    && !StringUtil.isStringEmpty(impCustomerOcsDeptCd) ){
                errorMsg = MSG_ERR_505;
                return errorMsg;
            }
        }

        Map<String, String> rateMap = new HashMap<>();
        if(StringUtil.isStringEmpty(reportPlaningDate)){
            errorMsg = MSG_ERR_507;
            return errorMsg;
        }
        LocalDate date = LocalDate.parse(reportPlaningDate);
        String rateStartDate = date.minusDays(date.getDayOfWeek().getValue()).toString();
        rateMap.put("date",rateStartDate);

        if( !StringUtil.isStringEmpty(fareCurrencyCD) && !fareCurrencyCD.equals(CURRENCY_CODE_JPY)){
            rateMap.put("rate",fareCurrencyCD);
            String rate = "";
            rate = modalMapper.getRate(rateMap);
            if(StringUtil.isStringEmpty(rate)){
                errorMsg = commonService.createRateErrorMessage(fareCurrencyCD);
                return errorMsg;
            }
        }

        if(!StringUtil.isStringEmpty(insuranceCurCD) && !insuranceCurCD.equals(CURRENCY_CODE_JPY)){
            rateMap.put("rate",insuranceCurCD);
            String rate = "";
            rate = modalMapper.getRate(rateMap);
            if(StringUtil.isStringEmpty(rate)){
                errorMsg = commonService.createRateErrorMessage(insuranceCurCD);
                return errorMsg;
            }
        }

        if(!StringUtil.isStringEmpty(invoiceCurCD) && !invoiceCurCD.equals(CURRENCY_CODE_JPY)){
            rateMap.put("rate",invoiceCurCD);
            String rate = "";
            rate = modalMapper.getRate(rateMap);
            if(StringUtil.isStringEmpty(rate)){
                errorMsg = commonService.createRateErrorMessage(invoiceCurCD);
                return errorMsg;
            }
        }

        if(!StringUtil.isStringEmpty(incRevBaseCurCD) && !incRevBaseCurCD.equals(CURRENCY_CODE_JPY)){
            rateMap.put("rate",incRevBaseCurCD);
            String rate = "";
            rate = modalMapper.getRate(rateMap);
            if(StringUtil.isStringEmpty(rate)){
                errorMsg = commonService.createRateErrorMessage(incRevBaseCurCD);
                return errorMsg;
            }
        }

        return errorMsg;
    }



    private OAIT001IDADao checkIdaWeight(OAIT001IDADao param){
        List<OAIT001IDASubDao> subList = param.getSubDao();
        String unitCd = StringUtil.isStringNull(param.getMainDao().getWeightUnitCD());
        String weight = StringUtil.isStringNull(param.getMainDao().getCargoWeight());


        if(checkLbrWeight(unitCd)){
            param.getMainDao().setWeightUnitCD(WEIGHT_UNIT_KGM);
            param.getMainDao().setCargoWeight(commonService.lbrToKgm(weight,unitCd));
        }

        for(int i = 0; i < subList.size(); i++){
            if(checkLbrWeight(StringUtil.isStringNull(subList.get(i).getAmoUnit1()))){
                String tmpWeight = commonService.lbrToKgm(subList.get(i).getAmo1(),subList.get(i).getAmoUnit1());
                subList.get(i).setAmo1(tmpWeight);
                subList.get(i).setAmoUnit1(WEIGHT_UNIT_KGM);
            }
            if(checkLbrWeight(StringUtil.isStringNull(subList.get(i).getAmoUnit2()))){
                String tmpWeight = commonService.lbrToKgm(subList.get(i).getAmo2(),subList.get(i).getAmoUnit2());
                subList.get(i).setAmo2(tmpWeight);
                subList.get(i).setAmoUnit2(WEIGHT_UNIT_KGM);
            }
        }
        param.setSubDao(subList);

        return param;
    }


    public boolean checkLbrWeight(String weightUnit) {
        return weightUnit.equals(WEIGHT_UNIT_LBR) ? true : false;
    }



}
