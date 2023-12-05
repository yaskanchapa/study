package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.exception.exceptions.CustomsException;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001FileDetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001FileHeadDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001SearchResultDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ExcelDetail;
import com.kse.wmsv2.oa_et_001.dto.OAET001ExcelHead;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReturnDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001UploadDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001FileMapper;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class OAET001FileService extends OAET001CommonConstants {

    @Autowired
    OAET001FileMapper fileMapper;

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReturnDto uploadFile(OAET001UploadDto fileParams,HttpHeaders headers) {
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        int detailCnt = 0;
        int repCnt = 0;

        List<OAET001SearchResultDao> resultList;

        try{
            // パラメータチェック
            returnVal = checkParam(fileParams);
            if(returnVal.getErrFlg().equals(RESULT_ERROR)){
                return returnVal;
            }

            // ステータス登録用
            COMMONStatusDto statusDao = new COMMONStatusDto();
            String userCd = commonService.getUserCd(headers);
            String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(LINK_REGISTER);
            statusDao.setBondStatusCd(LINK_REGISTER_BON);
            statusDao.setLinkDataClass(DEFAULT_LINK_DATA);
            statusDao.setBusinessClass(EXPORT_BUSINESS_CD);
            statusDao.setAwbNo(fileParams.getAwbNo());

            // Excelファイル取得
            MultipartFile uploadFile = fileParams.getUploadFile();

            // 古いデータ削除
            deleteOldData(fileParams);

            //EXCELファイル情報取得
            List<OAET001ExcelDetail> excelList = getExcelData(uploadFile);

            //デフォルトリスト取得
            Map<String, String> defaultList;
            defaultList = setDefaultList(fileParams);
            fileParams.setDefaultList(defaultList);

            //ヘッダ情報マッピング
            OAET001FileHeadDao headerData;
            headerData = mappingHead(fileParams,excelList.get(0).getHead());
            headerData.setUserCd(userCd);
            headerData.setDate(date);

            // AE_HEADテーブル登録
            fileMapper.insertHeadData(headerData);

            for (OAET001ExcelDetail oaet001ExcelDetail : excelList) {
                OAET001FileDetailDao detail = mappingDetail(fileParams, oaet001ExcelDetail);
                detail.setUserCd(userCd);
                detail.setDate(date);
                // ステータス登録
                detail.setCurrentCustomsStatusCd(LINK_REGISTER);

                // AE_DATAテーブル登録
                detailCnt = detailCnt + fileMapper.insertDetailData(detail);

                // AE_DATA_DETAILテーブル登録
                if (detail.getAutoEsaFlg().equals("2")) {
                    repCnt = repCnt + fileMapper.insertRepData(detail);
                }

                //ステータス登録
                statusDao.setCwbNo(detail.getCwbNo());
                statusDao.setCwbNoDeptCd(DEFAULT_CWB_DEPT_CD);
                statusDao.setBwbNo(detail.getBwbNo());
                stsServ.insertStatusMaster(statusDao);
            }

            // 登録したデータ検索
            Map<String,String> searchMap = new HashMap<>();
            searchMap.put("awbNo",fileParams.getAwbNo());
            searchMap.put("deptCd", fileParams.getCurrentDeptCd().split(":")[0]);
            resultList = fileMapper.searchData(searchMap);

            // 成功メッセージ設定
            returnVal.setMessage(MSG_SUC_005);
            returnVal.setErrFlg(RESULT_SUCCESS);
            returnVal.setSearchResult(resultList);
        } catch (CustomsException e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // 処理結果保存
            returnVal.setErrFlg(RESULT_ERROR);
            returnVal.setMessage(e.getMessage());

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (DuplicateKeyException e){
            // RollBack
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // リターン値設定
            returnVal.setMessage(MSG_ERR_010);
            returnVal.setErrFlg(RESULT_ERROR);

            // 開発用ログ
            e.printStackTrace();
            log.error(e.getMessage());
        } catch (Exception e){
            // RollBack
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // リターン値設定
            returnVal.setMessage(MSG_ERR_006);
            returnVal.setErrFlg(RESULT_ERROR);

            // 開発用ログ
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return returnVal;
    }

    private OAET001ReturnDto checkParam(OAET001UploadDto fileParams){
        OAET001ReturnDto returnDto = new OAET001ReturnDto();
        boolean errflg = false;
        String msg = "";
        // AWBチェック
        String awbNo = fileParams.getAwbNo();
        if(StringUtil.isStringEmpty(awbNo) || (awbNo.length() > 20)){
            errflg = true;
            msg = MSG_ERR_007;
        }

        // ファイルチェック
        try{
            MultipartFile uploadFile = fileParams.getUploadFile();
            if(!errflg &&  !checkFile(uploadFile)){
                errflg = true;
                msg = MSG_ERR_008;
            }
        } catch (Exception e){
            e.printStackTrace();
            errflg = true;
            msg = MSG_ERR_008;
        }

        // デフォルト設チェック
        if(!errflg &&  StringUtil.isStringNull(fileParams.getCurrentDefaultList()).length() < 3){
            errflg = true;
            msg = MSG_ERR_009;
        }
        if(errflg){
            returnDto.setErrFlg(RESULT_ERROR);
            returnDto.setMessage(msg);
        } else {
            returnDto.setErrFlg(RESULT_SUCCESS);
        }
        return returnDto;
    }

    private void deleteOldData(OAET001UploadDto fileParams) throws Exception {
        boolean deleteFlg = fileParams.getDeleteOld().equals("1");
        if(deleteFlg){
            fileMapper.deleteOldData(fileParams);
        }
    }


    private boolean checkFile(MultipartFile uploadFile) throws Exception{
        return checkFileIsEmpty(uploadFile) && checkContentType(uploadFile);
    }


    private boolean checkContentType(MultipartFile file) throws Exception{
        String contentType = file.getOriginalFilename();
        boolean checkResult = contentType.endsWith(OAIT001CommonConstants.EXCEL_CONTENT_TYPE_1) || contentType.endsWith(OAIT001CommonConstants.EXCEL_CONTENT_TYPE_2);
        return checkResult;
    }

    private boolean checkFileIsEmpty(MultipartFile file) throws Exception{
        return !file.isEmpty();
    }



    private List<OAET001ExcelDetail> getExcelData(MultipartFile uploadFile) throws Exception{
        List<OAET001ExcelDetail> returnList = new ArrayList<>();

        String fileName = uploadFile.getOriginalFilename();
        try {
            Workbook workbook = null;
            if(fileName.endsWith(".xls")){
                workbook = new HSSFWorkbook(uploadFile.getInputStream());
            } else if(fileName.endsWith(".xlsx")){
                workbook = new XSSFWorkbook(uploadFile.getInputStream());
            }
            Sheet sheet = workbook.getSheetAt(0);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            // header取得
            Map<String, Object> headerMap = new LinkedHashMap<>();
            for(int i = 1; i < 6; i++){
                Row row = sheet.getRow(i);
                if(row.getCell(3) != null && !row.getCell(3).toString().isBlank()){
                    Cell cell = row.getCell(3);
                    headerMap.put(String.valueOf(i),getCellValue(cell,evaluator));
                }
            }
            OAET001ExcelHead head;
            head = mappingExcelHead(headerMap);

            for(int i = 10; i < sheet.getLastRowNum()+1; i++){
                Row row = sheet.getRow(i);
                if(row != null && (row.getCell(1) != null && !row.getCell(1).toString().equals("")) ){
                    Map<String, Object> map = new LinkedHashMap<>();
                    int cells = OAIT001CommonConstants.EXPORT_EXCEL_LIMIT_CELL_LENGTH;
                    for(int j=0; j < cells+1; j++){
                        Cell cell = row.getCell(j);
                        map.put(String.valueOf(j),getCellValue(cell,evaluator));
                    }
                    OAET001ExcelDetail tmp;
                    tmp = mappingExcelDetail(map);
                    tmp.setHead(head);
                    returnList.add(tmp);
                }
            }

        } catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(e);
        }
        return  returnList;
    }

    private OAET001ExcelDetail mappingExcelDetail(Map<String, Object> map) throws Exception{
        OAET001ExcelDetail detail = new OAET001ExcelDetail();
        detail.setSn(StringUtil.isStringNull((String) map.get("0")));
        detail.setCwbNo(StringUtil.isStringNull((String) map.get("1")));
        detail.setPieces1(StringUtil.isStringNull((String) map.get("2")));
        detail.setWeight1(StringUtil.isStringNull((String) map.get("3")));

        // 70桁
        String consignName = StringUtil.isStringNull((String) map.get("4"));
        consignName = consignName.substring(0, Math.min(consignName.length(), 70));
        detail.setConsigneeName(consignName);

        // 105桁
        String consignAdd = StringUtil.isStringNull((String) map.get("5"));
        consignAdd = consignAdd.substring(0,consignAdd.length() > 105 ? 104 : consignAdd.length());
        detail.setConsigneeAdd(consignAdd);

        // 70桁
        String shipperName = StringUtil.isStringNull((String) map.get("6"));
        shipperName = shipperName.substring(0, Math.min(shipperName.length(), 70));
        detail.setShipperName(shipperName);

        // 105桁
        String shipperAdd = StringUtil.isStringNull((String) map.get("7"));
        shipperAdd = shipperAdd.substring(0,shipperAdd.length() > 105 ? 104 : shipperAdd.length());
        detail.setShipperAdd(shipperAdd);

        detail.setShipperPost(StringUtil.isStringNull((String) map.get("8")));

        // [-]削除
        String contact = StringUtil.isStringNull((String) map.get("9"));
        contact = contact.replaceAll("-","");
        detail.setContact(contact);

        // 40桁
        String description = StringUtil.isStringNull((String) map.get("10"));
        description = description.substring(0,description.length() > 40 ? 40 : description.length());
        detail.setDescription(description);

        detail.setCurrency(StringUtil.isStringNull((String) map.get("11")));
        detail.setInvoiceValue(StringUtil.isStringNull((String) map.get("12")));
        detail.setPieces2(StringUtil.isStringNull((String) map.get("13")));
        detail.setWeight2(StringUtil.isStringNull((String) map.get("14")));
        detail.setCountryOfOrigin(StringUtil.isStringNull((String) map.get("15")));
        detail.setFreight(StringUtil.isStringNull((String) map.get("16")));
        detail.setFreightAmount(StringUtil.isStringNull((String) map.get("17")));
        detail.setCustomNo(StringUtil.isStringNull((String) map.get("18")));
        detail.setCustomType(StringUtil.isStringNull((String) map.get("19")));
        detail.setIie(StringUtil.isStringNull((String) map.get("20")));
        // 35桁
        String memo = StringUtil.isStringNull((String) map.get("21"));
        memo = memo.substring(0,memo.length() > 35 ? 34 : memo.length());
        detail.setUserMemo(memo);

        return detail;
    }

    private OAET001ExcelHead mappingExcelHead(Map<String, Object> headerMap) throws Exception{
        OAET001ExcelHead head = new OAET001ExcelHead();
        head.setStepsDate(StringUtil.isStringNull((String) headerMap.get("1")));
        head.setFlightNo(StringUtil.isStringNull((String) headerMap.get("2")));
        head.setAwbNo(StringUtil.isStringNull((String) headerMap.get("3")));
        head.setOrigin(StringUtil.isStringNull((String) headerMap.get("4")));
        head.setDestination(StringUtil.isStringNull((String) headerMap.get("5")));

        String[] fltList = StringUtil.isStringNull((String) headerMap.get("2")).split("/");
        String air = fltList[0].substring(0,2);
        head.setFlt1(fltList[0]);
        head.setFlt2(fltList[1]);
        head.setAirline(air);
        return head;
    }


    private String getCellValue(Cell cell,FormulaEvaluator evaluator) throws Exception{
        String value = "";
        if(cell == null){
            return value;
        }
        switch (cell.getCellType()) {
            case STRING:
                value = cell.getStringCellValue().strip();
                break;
            case NUMERIC:
                if(DateUtil.isCellDateFormatted(cell)){
                    Date date = cell.getDateCellValue();
                    value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(date).toUpperCase();
                } else{
                    DataFormatter formatter = new DataFormatter();
                    value = formatter.formatCellValue(cell);
                }
                break;
            case FORMULA:
                evaluator.evaluateFormulaCell(cell);
                switch (cell.getCachedFormulaResultType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            Date date = cell.getDateCellValue();
                            value = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.ENGLISH).format(date).toUpperCase();
                        } else {
                            DataFormatter dataFormatter = new DataFormatter();
                            CellValue cellValue = evaluator.evaluate(cell);
                            value = dataFormatter.formatCellValue(cell, evaluator);
                        }
                        break;
                    case STRING:
                        value = cell.getStringCellValue().strip();
                        break;
                }
                break;
            default:
                break;
        }
        return value;
    }


    private Map<String, String> setDefaultList(OAET001UploadDto params) throws Exception{
        String defaultStr = params.getCurrentDefaultList();
        String[] tmpStr = defaultStr.replaceAll(",\\[","").replaceAll("\\[","").replaceAll("\"","").split("]");
        Map<String,String> valueMap = new HashMap<>();
        for (String s : tmpStr) {
            String[] tmp = s.split(",");
            if (tmp.length > 1) {
                valueMap.put(tmp[0], tmp[1]);
            } else {
                valueMap.put(tmp[0], "");
            }
        }

        String deptCd = params.getCurrentDeptCd();

        params.setCurrentDeptCd(deptCd);

        return valueMap;
    }


    private OAET001FileHeadDao mappingHead(OAET001UploadDto params,OAET001ExcelHead head) throws Exception{
        OAET001FileHeadDao returnVal = new OAET001FileHeadDao();
        Map<String,String> defaultMap = params.getDefaultList();

        // 固定値マッピング
        returnVal.setStepsDate(com.kse.wmsv2.common.util.DateUtil.getTimeStampNow());
        returnVal.setTrackService("01");

        // ファイル値マッピング
        returnVal.setAwbNo(params.getAwbNo());
        returnVal.setBonWareHoCurDate(head.getStepsDate());
        returnVal.setLoadingPlanFlt1(head.getFlt1());
        returnVal.setLoadingPlanFlt2(head.getFlt2());
        returnVal.setAirline(head.getAirline());
        returnVal.setFltDestination(head.getDestination());

        // デフォルト値マッピング
        returnVal.setCarSlipMakeFlg(StringUtil.isStringNull(defaultMap.get("026")));
        returnVal.setAgent(StringUtil.isStringNull(defaultMap.get("027")));
        returnVal.setReportCondition(StringUtil.isStringNull(defaultMap.get("023")));
        returnVal.setDepPortCd(StringUtil.isStringNull(defaultMap.get("001")));
        returnVal.setBondedWarehouseCd(StringUtil.isStringNull(defaultMap.get("025")));
        returnVal.setJoint(StringUtil.isStringNull(defaultMap.get("032")));
        returnVal.setLoadingWorker(StringUtil.isStringNull(defaultMap.get("034")));

        return returnVal;
    }



    private OAET001FileDetailDao mappingDetail(OAET001UploadDto params,OAET001ExcelDetail detail) throws Exception{
        OAET001FileDetailDao detailData = new OAET001FileDetailDao();

        // デフォルト値設定
        detailData = mappingDetailDefault(detailData,params);

        // Excel値設定
        detailData = mappingDetailExcel(detailData,detail);

        // 固定値設定
        detailData = mappingDetailFixValue(detailData,params);

        // 輸出者情報設定
        detailData = mappingCustomer(detailData);

        // ESA FLAG設定
        detailData = mappingESAFlag(detailData,params);

        // pre対応
        // 緊急度・重要度・難易度
        detailData = mappingPriority(detailData,params);

        return detailData;
    }

    private OAET001FileDetailDao mappingPriority(OAET001FileDetailDao detailData, OAET001UploadDto params) throws Exception {
        int priority = 0;
        int difficulty = 0;
        int severity = 0;

        // 業者Noチェック
        if(StringUtil.isStringEmpty(detailData.getExpCustomerNo())){
            difficulty = difficulty + 5;
            priority = priority + 5;
        }

        // INVOICE価格
        Map<String,String> rateMap = new HashMap<>();
        String date = StringUtil.isStringEmpty(detailData.getExpReportPlanDate())  ? com.kse.wmsv2.common.util.DateUtil.getTimeStampNow() : detailData.getExpReportPlanDate();
        rateMap.put("date",date);
        rateMap.put("currencyCd",detailData.getInvoiceCurCd());
        String strRate = fileMapper.getCommonRate(rateMap);
        if(StringUtil.isStringEmpty(strRate)){
            throw new CustomsException(commonService.createRateErrorMessage(detailData.getInvoiceCurCd()));
        }
        BigDecimal rate = new BigDecimal(strRate);
        BigDecimal invoiceVal = new BigDecimal(detailData.getInvoiceValue());
        int compare = INVOICE_MAX_VALUE_2.compareTo(rate.multiply(invoiceVal).toBigInteger());
        if(compare < 0){
            priority = priority + 5;
        }

        // 大額少額識別
        if(StringUtil.isStringNull(detailData.getLargeSmallFlg()).equals("L")){
            difficulty = difficulty + 10;
            priority = priority + 10;
        }

        detailData.setCustomsCheckPriority(String.valueOf(priority));
        detailData.setEditDifficulty(String.valueOf(difficulty));
        detailData.setEditSeverity(String.valueOf(severity));

        return detailData;
    }

    private OAET001FileDetailDao mappingESAFlag(OAET001FileDetailDao detailData,OAET001UploadDto params) throws Exception{
        // 初期値設定
        detailData.setReportNayClassCd("00");
        detailData.setAutoEsaFlg("1");
        detailData.setEsaFlg("1");

        // 金額判断
        if(valueCheck(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 禁止アイテム
        } else if(checkProhibitionItem(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 個数チェック
        } else if(checkMaximumPiece(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 重量チェック
        } else if(checkMaximumWeight(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 輸出禁止国
        } else if(checkProhibitionCountry(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 輸出者条件チェック
        } else if(checkExporterCondition(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 輸出者名チェック
        } else if(checkExporterName(detailData)){
            detailData.setAutoEsaFlg("2");
            detailData.setEsaFlg("2");

            // 再shippingチェック
        } else if(checkReShipping(detailData)){
            detailData.setReportNayClassCd("01");
            detailData.setAutoEsaFlg("3");
            detailData.setEsaFlg("3");

        }
        // 結果セット
        if(detailData.getEsaFlg().equals("1")){
            detailData.setReportDivisionCd(params.getDefaultList().get("024"));
        } else if(detailData.getEsaFlg().equals("2")){
            detailData.setReportDivisionCd(params.getDefaultList().get("013"));
        } else {
            detailData.setReportDivisionCd(null);
        }
        return detailData;
    }


    private boolean valueCheck(OAET001FileDetailDao detailData) throws Exception{
        String maximumVal = fileMapper.getMaximumExportValue();
        String rate = fileMapper.getRate(detailData);
        if(StringUtil.isStringEmpty(rate)){
            throw new CustomsException(commonService.createRateErrorMessage(detailData.getInvoiceCurCd()));
        }
        BigDecimal bdVal = new BigDecimal(maximumVal);
        BigDecimal bdRate = new BigDecimal(rate);
        BigDecimal bdInvoice = new BigDecimal(detailData.getLinkFobAmo());
        return bdInvoice.multiply(bdRate).compareTo(bdVal) >= 1 ? true : false;
    }


    private boolean checkProhibitionItem(OAET001FileDetailDao detailData) throws Exception{
        return fileMapper.checkProhibitionItem(detailData) > 0;
    }


    private boolean checkMaximumPiece(OAET001FileDetailDao detailData) throws Exception {
        int carryingPiece = Integer.parseInt(detailData.getCarryingPiece());
        int maximumPiece = fileMapper.getMaximumPiece(detailData);
        return carryingPiece > maximumPiece;
    }


    private boolean checkMaximumWeight(OAET001FileDetailDao detailData) throws Exception{
        BigDecimal carryingWeight = new BigDecimal(detailData.getCarryingWeight());
        BigDecimal maximumWeight = new BigDecimal(fileMapper.getMaximumWeight(detailData));
        return carryingWeight.compareTo(maximumWeight) >= 1;
    }


    private boolean checkProhibitionCountry(OAET001FileDetailDao detailData) throws Exception{
        String result;
        result = fileMapper.checkProhibitionCountry(detailData);
        return StringUtil.isStringNull(result).equals("1");
    }


    private boolean checkExporterCondition(OAET001FileDetailDao detailData) throws Exception{
        boolean resultFlg = false;
        if(!StringUtil.isStringEmpty(detailData.getExpCustomerNo())){
            String cond;
            cond = fileMapper.checkExporterCondition(detailData);
            if(StringUtil.isStringNull(cond).equals("1")){
                resultFlg = true;
            }
        }
        return  resultFlg;
    }


    private boolean checkExporterName(OAET001FileDetailDao detailData) throws Exception{
        boolean resultFlg = false;
        if(StringUtil.isStringEmpty(detailData.getConsigneeName())){
            String name = detailData.getConsigneeName().substring(1,1);
            if(name.equals("*")){
                resultFlg = true;
            }
        }
        return  resultFlg;
    }


    private boolean checkReShipping(OAET001FileDetailDao detailData) throws Exception{
        return fileMapper.checkReShipping(detailData) > 0;
    }


    private OAET001FileDetailDao mappingCustomer(OAET001FileDetailDao detailData) throws Exception{
        Map<String, String> resultMap;
        resultMap = fileMapper.customerMapping(detailData);
        if(resultMap != null && !StringUtil.isStringEmpty(resultMap.get("CUSTOMERNO"))){
            detailData.setExpCustomerOcsDeptCd(StringUtil.isStringNull(resultMap.get("OCSDEPTCD")));
            detailData.setExpCustomerName(StringUtil.isStringNull(resultMap.get("CUSTOMERNAMEE")));
            detailData.setExpCustomerPostcode(StringUtil.isStringNull(resultMap.get("ZIPCD")));
            detailData.setExpCustomerAdd1(StringUtil.isStringNull(resultMap.get("CUSTOMERADDE1")));
            detailData.setExpCustomerAdd2(StringUtil.isStringNull(resultMap.get("CUSTOMERADDE2")));
            detailData.setExpCustomerAdd3(StringUtil.isStringNull(resultMap.get("CUSTOMERADDE3")));
            detailData.setExpCustomerAdd4(StringUtil.isStringNull(resultMap.get("CUSTOMERADDE4")));
            detailData.setExpCustomerAddLump(StringUtil.isStringNull(resultMap.get("CUSTOMERADDEBLANKET")));
            detailData.setNewsShipper(StringUtil.isStringNull(resultMap.get("CONDITIONCOMMENT")));
            detailData.setConditionFlg(StringUtil.isStringNull(resultMap.get("CONDITIONFLG")));
            if(StringUtil.isStringEmpty(detailData.getExpCustomerNo())){
                detailData.setExpCustomerNo(StringUtil.isStringNull(resultMap.get("CUSTOMERNO")));
                detailData.setExpCustomerDeptCd(StringUtil.isStringNull(resultMap.get("DEPTCD")));
            }
        }
        return detailData;
    }


    private OAET001FileDetailDao mappingDetailDefault(OAET001FileDetailDao detailData, OAET001UploadDto params) throws Exception{
        Map<String,String> defaultMap = params.getDefaultList();
        detailData.setDepPort(StringUtil.isStringNull(defaultMap.get("001")));
        detailData.setCustomsKindCd1(StringUtil.isStringNull(defaultMap.get("003")));
        detailData.setReportDepCd(StringUtil.isStringNull(defaultMap.get("004")));
        detailData.setBondedWarehouseCd(StringUtil.isStringNull(defaultMap.get("005")));
        detailData.setCargoType(StringUtil.isStringNull(defaultMap.get("007")));
        detailData.setMixedForwarder(StringUtil.isStringNull(defaultMap.get("009")));
        detailData.setLargeSmallFlg(StringUtil.isStringNull(defaultMap.get("010")));
        detailData.setReportKindCd(StringUtil.isStringNull(defaultMap.get("011")));
        detailData.setCustomsKindCd2(StringUtil.isStringNull(defaultMap.get("012")));
        detailData.setReportPlanPersonCd(StringUtil.isStringNull(defaultMap.get("014")));
        detailData.setTradeTypeMark(StringUtil.isStringNull(defaultMap.get("015")));
        detailData.setCustomsExamMark(StringUtil.isStringNull(defaultMap.get("016")));
        detailData.setExpRecoFlg(StringUtil.isStringNull(defaultMap.get("017")));
        detailData.setPreExamCargoDisc(StringUtil.isStringNull(defaultMap.get("018")));
        detailData.setInvoiceDiscernment(StringUtil.isStringNull(defaultMap.get("019")));
        detailData.setInvoiceValConCd(StringUtil.isStringNull(defaultMap.get("020")));
        detailData.setInvoiceValClassCd(StringUtil.isStringNull(defaultMap.get("021")));
        detailData.setNeedLoadingRecDisc(StringUtil.isStringNull(defaultMap.get("022")));
        detailData.setReportCondition(StringUtil.isStringNull(defaultMap.get("023")));
        detailData.setCarSlipmakeFlg(StringUtil.isStringNull(defaultMap.get("026")));
        detailData.setAgent(StringUtil.isStringNull(defaultMap.get("027")));
        detailData.setCargoDisc(StringUtil.isStringNull(defaultMap.get("028")));
        detailData.setAgentDiv(StringUtil.isStringNull(defaultMap.get("029")));
        detailData.setCustomsRequest(StringUtil.isStringNull(defaultMap.get("030")));
        detailData.setDesClassDisc(StringUtil.isStringNull(defaultMap.get("031")));
        detailData.setJoint(StringUtil.isStringNull(defaultMap.get("032")));
        detailData.setLoadingWorker(StringUtil.isStringNull(defaultMap.get("034")));
        detailData.setCustomsOfficeJanitorCd(StringUtil.isStringNull(defaultMap.get("041")));
        detailData.setCustomsOfficeJanitorReNo(StringUtil.isStringNull(defaultMap.get("042")));
        detailData.setCustomsOfficeJanitorName(StringUtil.isStringNull(defaultMap.get("043")));
        detailData.setInspectionWitness(StringUtil.isStringNull(defaultMap.get("044")));
        detailData.setShippersSecCd(StringUtil.isStringNull(defaultMap.get("045")));
        detailData.setShippersRefNo(StringUtil.isStringNull(defaultMap.get("046")));
        return  detailData;
    }

    private OAET001FileDetailDao mappingDetailFixValue(OAET001FileDetailDao detailData, OAET001UploadDto params) throws Exception{
        String dateNow = com.kse.wmsv2.common.util.DateUtil.getNow("yyyy-MM-dd 00:00:00.000");
        detailData.setAwbNo(params.getAwbNo());
        detailData.setTruckNoDate(dateNow);
        detailData.setCustomsPlaceCd(params.getCurrentDeptCd());
        detailData.setExpReportPlanDate(dateNow);
        // pre対応
        String agency = params.getCurrentAgency().split(":")[0];
        detailData.setInHouseRefNumber(agency);
        return detailData;
    }

    private OAET001FileDetailDao mappingDetailExcel(OAET001FileDetailDao detailData, OAET001ExcelDetail excel) throws Exception{
        detailData.setCwbNo(excel.getCwbNo());
        // MAWB番号はユーザ入力を優先
        detailData.setAwbNo(excel.getHead().getAwbNo());
        detailData.setOrigin(excel.getHead().getOrigin());

        // 1次対応
        //detailData.setExpCustomerAdd(excel.getShipperAdd());
        detailData.setExpCustomerAdd(StringUtil.subString(excel.getShipperAdd(),74));
        detailData.setExpCustomerTelNo(excel.getContact());
        detailData.setConsigneeName(excel.getConsigneeName());
        detailData.setConsigneeAddLump(excel.getConsigneeAdd());

        // 1次対応
        // 80桁
        String consigneeAdd = excel.getConsigneeAdd();
        consigneeAdd = consigneeAdd.substring(0, consigneeAdd.length() > 80 ? 79 : consigneeAdd.length());
        detailData.setConsigneeAdd(consigneeAdd);
        detailData.setCarryingPiece(excel.getPieces2());
        detailData.setCarryingWeight(excel.getWeight2());
        detailData.setLinkPiece(excel.getPieces2());
        detailData.setLinkWeight(excel.getWeight2());
        detailData.setInvoiceCurCd(excel.getCurrency());
        detailData.setInvoiceValue(excel.getInvoiceValue());
        detailData.setFobCurrencyCd(excel.getCurrency());
        detailData.setFobAmo(excel.getInvoiceValue());
        detailData.setLinkFobAmo(excel.getInvoiceValue());
        detailData.setNews2(excel.getUserMemo());

        // エンター除外
        detailData.setItem(StringUtil.subString(excel.getDescription().replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," ").stripTrailing(),40));

        detailData.setAirline(excel.getHead().getAirline());
        detailData.setOrigin5(excel.getHead().getOrigin());
        detailData.setFlt1(excel.getHead().getFlt1());
        detailData.setFlt2(excel.getHead().getFlt2());
        detailData.setBonWarehoCurDate(excel.getHead().getStepsDate());
        detailData.setFltDestination(excel.getHead().getDestination());

        if(!StringUtil.isStringEmpty(excel.getIie())){
            String iie = excel.getIie();
            if(iie.length()>=9){
                detailData.setExpCustomerNo(iie.substring(0,7));
                detailData.setExpCustomerDeptCd(iie.substring(8,12));
            }
        }

        // 輸出者情報がなしの場合
        detailData.setExpCustomerName(excel.getShipperName());
        detailData.setExpCustomerAddLump(excel.getShipperAdd());

        // 名称マスタセット
        String country = "";
        country = fileMapper.getConsigneeCountry(excel.getHead());
        detailData.setConsigneeCountry(country);

        country = country+excel.getHead().getDestination();
        detailData.setLastDestinationCd(country);
        detailData.setLinkLastDestinationCd(country);
        detailData.setDestination5(country);

        // pre対応
        detailData.setDestination(excel.getHead().getDestination());

        return detailData;
    }
}
