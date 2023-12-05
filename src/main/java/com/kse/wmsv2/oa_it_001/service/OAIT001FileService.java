package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONImportAddAccDto;
import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.exception.exceptions.CustomsException;
import com.kse.wmsv2.common.service.CommonImportService;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.*;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.*;
import com.kse.wmsv2.oa_it_001.dto.*;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001FileMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * The type Oait 001 file service.
 */
@Slf4j
@Service
public class OAIT001FileService {

    @Autowired
    OAIT001FileMapper mapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    StatusService stsServ;

    @Autowired
    ReportService reportServ;

    @Autowired
    CommonImportService commonImportService;


    /**
     * Upload file list.
     *
     * @param params the params
     * @return the list
     */
    @Transactional
    public OAIT001SearchResultDto uploadFile(OAIT001UploadDto params, HttpHeaders headers){
        // Excelファイル取得
        MultipartFile uploadFile = params.getUploadFile();

        // リターン値
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        String errorFlg = "";
        String message = "";
        String deptCd = commonService.getDeptCd(headers);
        params.setCurrentDeptCd(deptCd);

        try{
            returnVal = checkParam(params);
            if(returnVal.getErrFlg().equals("error")){
                return returnVal;
            }

            // ファイルチェック
            if(StringUtil.isStringEmpty(errorFlg) && !checkFile(uploadFile)){
                errorFlg = "error";
                message = OAIT001CommonConstants.MSG_ERR_103;
            }

            // 古いデータ削除
            deleteOldData(params);

            //EXCELファイル情報取得
            List<Map<String, Object>> listMap = getListData(uploadFile);

            //REPデータリスト
            List<OAIT001RepDataDao> repList = new ArrayList<>();
            OAIT001RepDataDao repTmp = new OAIT001RepDataDao();

            Map<String,String> settingMap = new HashMap<>();
            settingMap = mappingSettingMap(settingMap);

            //デフォルトリスト取得
            Map<String, String> defaultList = new HashMap<>();
            defaultList = setDefaultList(params);
            params.setDefaultList(defaultList);

            int targetLen = listMap.size();
            int insertDetailCnt = 0;

            OAIT001DetailDataDao targetVal = new OAIT001DetailDataDao();

            //STATUS更新用
            COMMONStatusDto statusDao = new COMMONStatusDto();
            String userCd = commonService.getUserCd(headers);
            String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
            statusDao.setUserCd(userCd);
            statusDao.setDate(date);
            statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_LINK);
            statusDao.setLinkDataClass("0");
            statusDao.setBusinessClass("01");
            statusDao.setAwbNo(params.getAwbNo());


            // ヘッダ更新用
            String arrFlt1 = "";
            String arrFlt2 = "";

            for(int i = 0; i < listMap.size(); i++){
                //明細データマッピング
                targetVal = mappingDetailData(listMap.get(i),params);
                // 基本データ
                targetVal.setCustomsPlaceCd(deptCd);
                targetVal.setUserCd(userCd);
                targetVal.setDate(date);

                // AI_DATA
                int tmpCnt = insertImportDataDetail(targetVal);
                insertDetailCnt = insertDetailCnt + tmpCnt;

                arrFlt1 = targetVal.getCurrentArrFlt1();
                arrFlt2 = targetVal.getCurrentArrFlt2();


                //REPデータマイニング
                repTmp = new OAIT001RepDataDao();
                repTmp = mappingRepData(targetVal,params,repTmp);
                repList.add(repTmp);

                //STAUTS登録
                statusDao.setCwbNo(targetVal.getCwbNo());
                statusDao.setCwbNoDeptCd(targetVal.getCwbNoDeptCd());
                statusDao.setBwbNo(targetVal.getBwbNo());
                stsServ.insertStatusMaster(statusDao);
            }


            if(StringUtil.isStringEmpty(errorFlg) && targetLen != insertDetailCnt){
                //エラー処理
                errorFlg = "error";
                message = OAIT001CommonConstants.MSG_ERR_103;
            }

            int insertHeadCnt = insertImportDataHead(params,userCd,date);
            if(StringUtil.isStringEmpty(errorFlg) && !(insertHeadCnt >= 1)){
                //エラー処理
                errorFlg = "error";
                message = OAIT001CommonConstants.MSG_ERR_104;
            }

            int insertRepCnt = insertImportDataRep(repList);
            if(StringUtil.isStringEmpty(errorFlg) && insertRepCnt!=insertDetailCnt){
                //エラー処理
                errorFlg = "error";
                message = OAIT001CommonConstants.MSG_ERR_105;
            }

            // ヘッダデータ更新
            int updateHeadCnt = reportServ.updateImportHeader(params.getAwbNo(),arrFlt1,arrFlt2);

            // 処理結果保存
            if(StringUtil.isStringEmpty(errorFlg)){
                returnVal.setErrFlg("false");
                returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_101);
                List<OAIT001SearchResultDao> detailDataList = new ArrayList<>();
                OAIT001HeaderDao headerData = new OAIT001HeaderDao();
                detailDataList = mapper.searchResultList(params.getAwbNo());
                headerData = mapper.searchResultHeader(params.getAwbNo());
                // 件数
                headerData = commonService.getCountMasterNumber(detailDataList,headerData);

                returnVal.setHeaderData(headerData);
                returnVal.setDetailDataList(detailDataList);
            } else {
                // RollBack処理
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                returnVal.setErrFlg(errorFlg);
                returnVal.setMessage(message);
            }
        } catch (CustomsException e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // 処理結果保存
            returnVal.setErrFlg("error");
            returnVal.setMessage(e.getMessage());

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // 処理結果保存
            returnVal.setErrFlg("error");
            if(StringUtil.isStringEmpty(message)){
                returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_102);
            } else {
                returnVal.setMessage(message);
            }

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }


    private OAIT001SearchResultDto checkParam(OAIT001UploadDto params) {
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        boolean errflg = false;
        String msg = "";
        // AWBチェック
        String awbNo = params.getAwbNo();
        if(StringUtil.isStringEmpty(awbNo) || (awbNo.length() > 20)){
            errflg = true;
            msg = OAIT001CommonConstants.MSG_ERR_007;
        }

        // ファイルチェック
        try{
            MultipartFile uploadFile = params.getUploadFile();
            if(!errflg &&  !checkFile(uploadFile)){
                errflg = true;
                msg = OAIT001CommonConstants.MSG_ERR_008;
            }
        } catch (Exception e){
            e.printStackTrace();
            errflg = true;
            msg = OAIT001CommonConstants.MSG_ERR_008;
        }

        // デフォルト設チェック
        if(!errflg &&  StringUtil.isStringNull(params.getCurrentDefaultList()).length() < 3){
            errflg = true;
            msg = OAIT001CommonConstants.MSG_ERR_009;
        }
        if(errflg){
            returnVal.setErrFlg("error");
            returnVal.setMessage(msg);
        } else {
            returnVal.setErrFlg("false");
        }

        return  returnVal;
    }


    private void deleteOldData(OAIT001UploadDto params) {
        boolean deleteFlg = params.getDeleteOld().equals("1") ? true : false;
        if(deleteFlg){
            mapper.deleteOldData(params.getAwbNo());
        }
    }


    private Map<String, String> setDefaultList(OAIT001UploadDto params){
        String defaultStr = params.getCurrentDefaultList();
        String[] tmpStr = defaultStr.replaceAll(",\\[","").replaceAll("\\[","").replaceAll("\"","").split("]");
        Map<String,String> valueMap = new HashMap<>();

        for(int i = 0; i < tmpStr.length; i++){
            valueMap.put(tmpStr[i].substring(0,3),tmpStr[i].substring(4));
        }

        String[] strList = params.getCurrentDeptCd().split(":");
        String deptCd = strList[0];

//        String deptCd = params.getCurrentDeptCd();
        params.setCurrentDeptCd(deptCd);
        return valueMap;
    }


    private OAIT001RepDataDao mappingRepData(OAIT001DetailDataDao targetVal, OAIT001UploadDto params,OAIT001RepDataDao repDao) {
        repDao.setAwbNo(targetVal.getAwbNo());
        repDao.setCwbNo(targetVal.getCwbNo());
        repDao.setItem(targetVal.getItem());
        repDao.setExpCustomerCountry(targetVal.getExpCustomerCountry());
        repDao.setOriginCertifiDisc(params.getDefaultList().get("031"));
        repDao.setFareDivDisc(params.getDefaultList().get("032"));
        repDao.setInConsTaxKindCd1(params.getDefaultList().get("033"));
        repDao.setUserCd(targetVal.getUserCd());
        repDao.setDate(targetVal.getDate());
        return repDao;
    }


    private int insertImportDataRep(List<OAIT001RepDataDao> repList ) {
        int insertCnt = 0;
        for(int i =0; i < repList.size(); i++){
            int tmpCnt = 0 ;
            tmpCnt = mapper.insertImportDataRep(repList.get(i));
            insertCnt = insertCnt + tmpCnt;
        }
        return insertCnt;
    }


    private int insertImportDataHead(OAIT001UploadDto params,String userCd,String date) {
        return mapper.insertImportDataHead(mappingHeadData(params,userCd,date));
    }


    private OAIT001HeadDataDao mappingHeadData(OAIT001UploadDto params,String userCd,String date){
        OAIT001HeadDataDao headDao = new OAIT001HeadDataDao();
        String dateArr = com.kse.wmsv2.common.util.DateUtil.getNow("yyyy/MM/dd 0:00:00");
        Map<String, String> defaultList = params.getDefaultList();
        headDao.setAwbNo(params.getAwbNo());
        headDao.setImportSource("1");
        headDao.setDeptCd(params.getCurrentDeptCd());
        headDao.setCustomDiv(defaultList.get("009"));
        headDao.setReportCondition(defaultList.get("006"));
        headDao.setGetPort(defaultList.get("019"));
        headDao.setClearPlanPlaceCd(defaultList.get("018"));
        headDao.setShipmentCd(defaultList.get("043"));
        headDao.setBondedWareHouse(params.getCurrentTabValue());
        headDao.setUserCd(userCd);
        headDao.setDate(date);
        // STEP2
        if(StringUtil.isStringEmpty(defaultList.get("016"))){
            headDao.setPresent(StringUtil.isStringNull(defaultList.get("013")));
        } else {
            headDao.setPresent(defaultList.get("016"));
        }
        headDao.setAirportCd(defaultList.get("043"));

        // STEP2 No4.
        headDao.setArrDate(dateArr);
        headDao.setArrPortDate(dateArr);


        return headDao;
    }


    private Map<String, String> mappingSettingMap(Map<String, String> settingMap) {
        int limitValue = mapper.getLimitValue();
        return settingMap;
    }


    /**
     * Get importer info oait 001 importer dao.
     *
     * @param telNo the tel no
     * @return the oait 001 importer dao
     */
    private OAIT001ImporterDao getImporterInfo(String telNo){
        OAIT001ImporterDao importerInfo = mapper.getImporterInfo(telNo);
        return importerInfo;
    }


    /**
     * Check file boolean.
     *
     * @param uploadFile the upload file
     * @return the boolean
     */
    private boolean checkFile(MultipartFile uploadFile){
        boolean checkResult = false;
        if(checkFileIsEmpty(uploadFile) && checkContentType(uploadFile)){
            checkResult = true;
        }
        return checkResult;
    }

    /**
     * Check file is empty boolean.
     *
     * @param file the file
     * @return the boolean
     */
    private boolean checkFileIsEmpty(MultipartFile file){
        return !file.isEmpty();
    }

    /**
     * Check content type boolean.
     *
     * @param file the file
     * @return the boolean
     */
    private boolean checkContentType(MultipartFile file){
        String contentType = file.getOriginalFilename();
        boolean checkResult = false;
        if(contentType.endsWith(OAIT001CommonConstants.EXCEL_CONTENT_TYPE_1) || contentType.endsWith(OAIT001CommonConstants.EXCEL_CONTENT_TYPE_2)){
            checkResult = true;
        }
        return checkResult;
    }

    /**
     * Get list data list.
     *
     * @param uploadFile the upload file
     * @return the list
     */
    private List<Map<String, Object>> getListData(MultipartFile uploadFile) throws Exception{
        List<Map<String, Object>> fileDataList = new ArrayList<Map<String,Object>>();
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

            List<String> errorCellList = new ArrayList<>();

            for(int i = 1; i < sheet.getLastRowNum()+1; i++){
                Row row = sheet.getRow(i);
                if(row != null && (row.getCell(0) != null && !row.getCell(0).toString().equals(""))){
                    Map<String, Object> map = new LinkedHashMap<>();
                    int cells = OAIT001CommonConstants.IMPORT_EXCEL_LIMIT_CELL_LENGTH;
                    for(int j=0; j < cells+1; j++){
                        Cell cell = row.getCell(j);
                        try{
                            map.put(String.valueOf(j),getCellValue(cell,evaluator));
                        } catch (CustomsException e){
                            errorCellList.add(e.getMessage());
                        }
                    }
                    fileDataList.add(map);
                }
            }
//            if(errorCellList.size() > 0 ){
//                throw new CustomsException(createCellErrorMessage(errorCellList));
//            }
//        } catch (CustomsException e){
//            log.error(e.getMessage());
//            throw new CustomsException(e.getMessage()+OAIT001CommonConstants.MSG_ERR_112);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new Exception(OAIT001CommonConstants.MSG_ERR_109);
        }
        return  fileDataList;
    }

    /**
     * Gets cell value.
     *
     * @param cell the cell
     * @return the cell value
     */
    private String getCellValue(Cell cell,FormulaEvaluator evaluator) throws Exception {
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
                    value = new SimpleDateFormat("ddMMM",Locale.ENGLISH).format(date).toUpperCase();
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
                            value = new SimpleDateFormat("ddMMM",Locale.ENGLISH).format(date).toUpperCase();
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

//        if(StringUtil.isProhibitionText(value)){
//            throw new CustomsException(String.valueOf(cell.getAddress()));
//        }
        return value;
    }




    private OAIT001DetailDataDao mappingDetailData(Map<String, Object> excelVal, OAIT001UploadDto params) throws Exception{
        OAIT001DetailDataDao mappingResult = new OAIT001DetailDataDao();
        //Default設定
        mappingResult = mappingDefault(params);
        //AWB設定
        mappingResult.setAwbNo(params.getAwbNo());

        //Excelデータ設定
        mappingResult = mappingExcelData(excelVal,mappingResult);
        //イメージチェック
        if(checkImgCount(mappingResult.getCwbNo())){
            mappingResult.setDocumentCheck("1");
        } else {
            mappingResult.setDocumentCheck("0");
        }
        //Fare関連値取得
        mappingResult = calcFare(mappingResult,params);
        //IDA関連値取得
        mappingResult = checkIDAFlag(mappingResult,params);
        //IMPORTER関連値取得
        mappingResult = getImporterInfo(mappingResult,params);
        //TAXABLEAMO計算
        mappingResult.setTaxablesAmo(getTaxbleAmo(mappingResult));
        //緊急度・重要度・難易度
        mappingResult = setPriority(mappingResult);
        //AddProcess
        mappingResult = setAddProcess(mappingResult);

        return mappingResult;
    }



    private OAIT001DetailDataDao mappingExcelData(Map<String, Object> excelVal,OAIT001DetailDataDao mappingResult){
        mappingResult.setCwbNo((String) excelVal.get("4"));

        mappingResult.setItem(StringUtil.subString((String) excelVal.get("8"),40).stripTrailing().replaceAll("(\\r\\n|\\r|\\n|\\n\\r)"," "));

        mappingResult.setCargoPiece((String) excelVal.get("5"));
        mappingResult.setCargoWeight((String) excelVal.get("6"));
        mappingResult.setInvoiceCurCd((String) excelVal.get("14"));
        mappingResult.setInvoiceValue(((String)excelVal.get("15")).replaceAll(",",""));

        mappingResult.setExpCustomerAdd(StringUtil.subString((String) excelVal.get("13"),80).strip());
        mappingResult.setExpCustomerAddLump(StringUtil.subString((String) excelVal.get("13"),105).strip());
        mappingResult.setExpCustomerName(StringUtil.subString((String) excelVal.get("12"),70).strip());

        mappingResult.setImpCustomerName(StringUtil.subString((String) excelVal.get("9"),70).strip());
        mappingResult.setImpCustomerAdd(StringUtil.subString((String) excelVal.get("10"),74).strip());
        mappingResult.setImpCustomerAddLump(StringUtil.subString((String) excelVal.get("10"),105).strip());
        mappingResult.setImpPostCode((String)excelVal.get("19"));
        String telNo = (String)excelVal.get("11");
        telNo = telNo.contains("#") ? telNo.split("#")[0] : telNo;
        mappingResult.setImpCustomerTelNo(StringUtil.subString(telNo,14).strip());

        mappingResult.setCurrentArrFlt1((String)excelVal.get("2"));
        mappingResult.setCurrentArrFlt2((String)excelVal.get("3"));
        mappingResult.setNews2(StringUtil.subString((String)excelVal.get("22"),35));

        mappingResult.setCountryOfOriginCd((String)excelVal.get("16"));

        // 0323追加
        mappingResult.setExpCustomerCountry((String)excelVal.get("16"));
        if(!checkReportCondition(mappingResult.getReportCondtion())){
            mappingResult.setLongKeepBondedWarehouse(null);
        }

        return mappingResult;
    }

    private String getTaxbleAmo(OAIT001DetailDataDao mappingResult) throws Exception {
        String taxableAmo = "";
        double invoiceVal = Double.parseDouble(mappingResult.getInvoiceValue());
        String strRate = getRate(mappingResult.getInvoiceCurCd());
        if(StringUtil.isStringEmpty(strRate)){
            throw new CustomsException(commonService.createRateErrorMessage(mappingResult.getInvoiceValue()));
        }
        double rate = Double.parseDouble(strRate);
        int result = (int) Math.floor(invoiceVal*rate);
        if(!StringUtil.isStringEmpty(mappingResult.getJpyFare())){
            if(result > OAIT001CommonConstants.MAX_TAXABLE_AMOUNT){
                taxableAmo = Integer.toString(OAIT001CommonConstants.MAX_TAXABLE_AMOUNT);
            } else {
                int jpyFare = Integer.parseInt(mappingResult.getJpyFare());
                taxableAmo = String.valueOf(result + jpyFare);
            }
        }else {
            taxableAmo = "0";
        }
        return taxableAmo;
    }

    private OAIT001DetailDataDao setAddProcess(OAIT001DetailDataDao mappingResult) {
        // Mapping
        COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
        accDto.setIdaFlg(mappingResult.getIdaFlg());
        accDto.setAwbNo(mappingResult.getAwbNo());
        accDto.setReportCondition(mappingResult.getReportCondtion());
        accDto.setTaxAmo(mappingResult.getTaxAmo());
        accDto.setCwbNo(mappingResult.getCwbNo());

        accDto = commonImportService.getAddProcess(accDto);
        if(!accDto.isErrorFlg()){
            mappingResult.setInClassifyClassName(accDto.getCargoIn());
            mappingResult.setOutClassifyClassName(accDto.getCargoOut());
            mappingResult.setDomesticClassifyClassName(accDto.getDomestic());
        }

        return mappingResult;
    }

    private OAIT001DetailDataDao setPriority(OAIT001DetailDataDao mappingResult) {
        int priority = 0;
        int difficulty = 0;
        int severity = 0;

        // 業者チェック
        if(StringUtil.isStringEmpty(mappingResult.getImpCustomerNo())){
            priority = priority + 5;
            difficulty = difficulty + 5;
        }

        // INVOICE価格(重要度)
        Map<String,String> rateMap = new HashMap<>();
        String date = StringUtil.isStringEmpty(mappingResult.getReportPlaningDate())  ? com.kse.wmsv2.common.util.DateUtil.getTimeStampNow() : mappingResult.getReportPlaningDate();
        rateMap.put("date",date);
        rateMap.put("currencyCd",mappingResult.getInvoiceCurCd());
        BigDecimal rate = new BigDecimal(mapper.getCommonRate(rateMap));
        BigDecimal invoiceVal = new BigDecimal(mappingResult.getInvoiceValue());
        int compare = OAIT001CommonConstants.INVOICE_MAX_VALUE_2.compareTo(rate.multiply(invoiceVal).toBigInteger());
        if(compare == -1){
            priority = priority + 5;
        }

        // 大額少額識別
        if(StringUtil.isStringNull(mappingResult.getBigSmallFlg()).equals("L")){
            priority = priority + 10;
            difficulty = difficulty + 10;
        }

        // 結果保存
        mappingResult.setEditDifficulty(String.valueOf(difficulty));
        mappingResult.setEditSeverity(String.valueOf(severity));
        mappingResult.setCustomsCheckPriority(String.valueOf(priority));

        return mappingResult;
    }

    private OAIT001DetailDataDao mappingDefault(OAIT001UploadDto params){
        OAIT001DetailDataDao mappingResult = new OAIT001DetailDataDao();
        Map<String, String> defaultList = params.getDefaultList();

        if(StringUtil.isStringEmpty(defaultList.get("001"))){
            mappingResult.setReportPlaningDate(com.kse.wmsv2.common.util.DateUtil.getNow("yyyy/MM/dd 0:00:00")); // 001
        } else {
            mappingResult.setReportPlaningDate(StringUtil.isStringNull(defaultList.get("001"))); // 001
        }
        mappingResult.setBpApplicationCd(StringUtil.isStringNull(defaultList.get("002"))); //002
        mappingResult.setReportCondtion(StringUtil.isStringNull(defaultList.get("006"))); //006
        mappingResult.setReportKindCd2(StringUtil.isStringNull(defaultList.get("007"))); //007
        mappingResult.setDiscernmentMark(StringUtil.isStringNull(defaultList.get("008"))); //008
        mappingResult.setReportDivCd(StringUtil.isStringNull(defaultList.get("009"))); //009
        mappingResult.setBigSmallFlg(StringUtil.isStringNull(defaultList.get("010"))); //010
        mappingResult.setReportKindCd1(StringUtil.isStringNull(defaultList.get("011"))); //011
        mappingResult.setCargoDisc(StringUtil.isStringNull(defaultList.get("012"))); //012
        mappingResult.setInvoiceDiscernment(StringUtil.isStringNull(defaultList.get("014"))); //014
        mappingResult.setInvoiceValConCd(StringUtil.isStringNull(defaultList.get("015"))); //015
        mappingResult.setBondedWareHouseCd(StringUtil.isStringNull(defaultList.get("018"))); //018
        mappingResult.setGetPort(StringUtil.isStringNull(defaultList.get("019"))); //019
        mappingResult.setInvoiceValClassCd(StringUtil.isStringNull(defaultList.get("020"))); //020
        mappingResult.setFareFlg(StringUtil.isStringNull(defaultList.get("021"))); //021
        // 結果反映のため修正
        if(StringUtil.isStringEmpty(defaultList.get("023"))){
            mappingResult.setInHouseRefNumber(StringUtil.isStringNull(params.getCurrentAgency().split(":")[0]));
        } else {
            mappingResult.setInHouseRefNumber(StringUtil.isStringNull(defaultList.get("023"))); //023
        }
        mappingResult.setReportPersonCd(StringUtil.isStringNull(defaultList.get("024"))); //024
        mappingResult.setWeightUnitCd(StringUtil.isStringNull(defaultList.get("025"))); //025
        mappingResult.setTradeTypeMark(StringUtil.isStringNull(defaultList.get("026"))); //026
        mappingResult.setIncreviseFlgCd(StringUtil.isStringNull(defaultList.get("028"))); //028
        mappingResult.setManagedDataFlg(StringUtil.isStringNull(defaultList.get("035"))); //035
        mappingResult.setMixedForwarder(StringUtil.isStringNull(defaultList.get("036"))); //036
        mappingResult.setLongKeepBondedWarehouse(StringUtil.isStringNull(defaultList.get("037"))); //037
        mappingResult.setDestination(StringUtil.isStringNull(defaultList.get("039"))); //039
        mappingResult.setCustomsTraderCd(StringUtil.isStringNull(defaultList.get("040"))); //040
        mappingResult.setArrAirportCd(StringUtil.isStringNull(defaultList.get("043"))); //043
        mappingResult.setCarBondedWarehouse(StringUtil.isStringNull(defaultList.get("044"))); //044
        mappingResult.setLocationCd(StringUtil.isStringNull(defaultList.get("045"))); //045
        mappingResult.setCustomsOfficeJanitorCd(StringUtil.isStringNull(defaultList.get("046"))); //046
        mappingResult.setCustomsOfficeJanitorReno(StringUtil.isStringNull(defaultList.get("047"))); //047
        mappingResult.setCustomsOfficeJanitorName(StringUtil.isStringNull(defaultList.get("048"))); //048
        mappingResult.setInspectionWitness(StringUtil.isStringNull(defaultList.get("049"))); //049
        mappingResult.setShippersSecCd(StringUtil.isStringNull(defaultList.get("050"))); // 050
        mappingResult.setShippersRefNo(StringUtil.isStringNull(defaultList.get("051"))); // 051
        mappingResult.setAdvanceRulingValuation1(StringUtil.isStringNull(defaultList.get("052"))); //0052
        mappingResult.setAdvanceRulingValuation2(StringUtil.isStringNull(defaultList.get("053"))); //0053


        return  mappingResult;
    }


    private boolean checkReportCondition(String reportCondition){
        boolean result = false;

        if(reportCondition.equals(OAIT001CommonConstants.REPORT_CONDITION_U)){
            result=true;
        }
        return result;
    }

    private boolean checkImgCount(String cwbNo){
        boolean checkResult = false;
        return checkResult;
    }


    private OAIT001DetailDataDao calcFare(OAIT001DetailDataDao param,OAIT001UploadDto params) throws Exception{
        String valCondCd = param.getInvoiceValConCd();
        String jpyFare = "0";
        if( !valCondCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_CIF) && !valCondCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_CF)){

            //dbからデータ取得
            Map<String,String> resultMap = new HashMap();
            Map<String,String> paramMap = new HashMap<>();

            paramMap.put("deptCd",params.getCurrentAgency());
            paramMap.put("cargoWeight",param.getCargoWeight());

            resultMap = mapper.getFareCurrency(paramMap);

            if(resultMap != null){
                String currencyCd = resultMap.get("FARECURRENCYCD");
                param.setFareCurrencyCd(currencyCd);

                //JPY以外はRATEテーブルからデータ取得し、計算する。
                if(!currencyCd.equals(OAIT001CommonConstants.CURRENCY_CODE_JPY)){
                    String strRate = mapper.getRate(currencyCd);
                    if(StringUtil.isStringEmpty(strRate)){
                        throw new CustomsException(commonService.createRateErrorMessage(currencyCd));
                    }
                    double rate = Double.parseDouble(strRate);
                    double fare = Double.parseDouble(resultMap.get("FARE"));

                    int tempval = 0;
                    tempval = (int) Math.floor(fare*rate);
                    jpyFare = Integer.toString(tempval);
                }
            }
        }
        param.setJpyFare(jpyFare);
        return param;
    }

    private OAIT001DetailDataDao checkIDAFlag(OAIT001DetailDataDao param,OAIT001UploadDto uploadDto) throws Exception{
        boolean checkResult = false;
        String strRate = mapper.getRate(param.getInvoiceCurCd());
        if(StringUtil.isStringEmpty(strRate)){
            throw new CustomsException(commonService.createRateErrorMessage(param.getInvoiceCurCd()));
        }
        Double rate = Double.parseDouble(strRate);
        int limitValue = mapper.getLimitValue();
        double invoiceValue = Double.parseDouble(param.getInvoiceValue());
        double jpyFare = Double.parseDouble(param.getJpyFare());
        double cargoPiece = Double.parseDouble(param.getCargoPiece());
        double cargoWeight = Double.parseDouble(param.getCargoWeight());

        double tmpVal = (invoiceValue * rate) + jpyFare;

        if(tmpVal > limitValue){
            checkResult = true;
        } else if (getCountIDAItem(uploadDto,param.getItem())){
            checkResult = true;
        } else if (cargoPiece > getMaximumCargoPiece(uploadDto.getCurrentDeptCd())){
            checkResult = true;
        } else if (cargoWeight > getMaximumCargoWeight(uploadDto.getCurrentDeptCd())){
            checkResult = true;
        }

        if(!checkResult){
            param.setReportDepCd(uploadDto.getDefaultList().get("016")); // 016
        } else {
            param.setReportDepCd(uploadDto.getDefaultList().get("013"));  // 013
        }

        if( !checkResult && uploadDto.getCurrentMailOrder().equals("1")){
            if(param.getInvoiceCurCd().equals("JPY")){
                param.setInvoiceValue("0");
            } else{
                BigDecimal tmpInVal = new BigDecimal(param.getInvoiceValue());
                tmpInVal = tmpInVal.multiply(BigDecimal.valueOf(0.60)).setScale(2, RoundingMode.FLOOR);
                param.setInvoiceValue(tmpInVal.toString());
            }
            param.setInvoiceValClassCd("D");
            param.setInvoiceValConCd(OAIT001CommonConstants.INVOICE_VALUE_CD_CIF);
        }

        String invoiceValConCd = param.getInvoiceValConCd();
        if(invoiceValConCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_CIF) || invoiceValConCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_CF)){
            param.setFare(null);
            param.setFareFlg(null);
            param.setFareCurrencyCd(null);
            param.setJpyFare(null);
        }

        if(checkResult){
            param.setIdaFlg(OAIT001CommonConstants.IDA_TRUE);
        } else {
            param.setIdaFlg(OAIT001CommonConstants.IDA_FALSE);
        }

        return param;
    }


    private OAIT001DetailDataDao getImporterInfo(OAIT001DetailDataDao param,OAIT001UploadDto uploadDto){
        OAIT001ImporterDao importerInfo = new OAIT001ImporterDao();
        importerInfo = mapper.getImporterInfo(param.getImpCustomerTelNo());
        String customerNo = "";
        if(importerInfo != null){
            customerNo = importerInfo.getCustomerNo();
        }
        Map<String, String> defaultMap = uploadDto.getDefaultList();
        String invoiceValConCd = param.getInvoiceValConCd();

        if(StringUtil.isStringEmpty(customerNo)){
            param.setImpCustomerNo(null);
            param.setImpCustomerDeptCd(null);
            param.setImpCustomerAdd1(null);
            param.setImpCustomerAdd2(null);
            param.setImpCustomerAdd3(null);
            param.setImpCustomerAdd4(null);
            if(invoiceValConCd.equals("FOB") || invoiceValConCd.equals("C&F")){
                param.setInsuranceClassCd(defaultMap.get("022"));//default 022
            } else {
                param.setInsuranceClassCd(null);
            }
            param.setIncInsureRegNo(null);
            param.setEstimationFlgCd(defaultMap.get("027"));
            param.setIncEstRePreNo(null);
            param.setDeliveryDateExtCd(defaultMap.get("029"));
            param.setPayMethodDisc(defaultMap.get("030"));
            param.setAccountNo(defaultMap.get("003"));
            param.setSecurityRegNo1(defaultMap.get("004"));
            param.setSecurityRegNo2(defaultMap.get("005"));
            param.setNewsShipper(null);
        } else {
            param.setImpCustomerNo(StringUtil.isStringNull(importerInfo.getCustomerNo()));
            param.setImpCustomerDeptCd(StringUtil.isStringNull(importerInfo.getDeptCd()));
            param.setImpCustomerOcsDeptCd(StringUtil.isStringNull(importerInfo.getOcsDeptCd()));
            param.setImpCustomerName(StringUtil.isStringNull(importerInfo.getCustomerNameE()));
            param.setImpCustomerAdd1(StringUtil.isStringNull(importerInfo.getCustomerAddE1()));
            param.setImpCustomerAdd2(StringUtil.isStringNull(importerInfo.getCustomerAddE2()));
            param.setImpCustomerAdd3(StringUtil.isStringNull(importerInfo.getCustomerAddE3()));
            param.setImpCustomerAdd4(StringUtil.isStringNull(importerInfo.getCustomerAddE4()));
            param.setImpCustomerAddLump(StringUtil.isStringNull(importerInfo.getCustomerAddeBlanket()));
//            param.setImpCustomerAdd(StringUtil.isStringNull(importerInfo.getCustomerAddeBlanket()));
            param.setImpCustomerTelNo(StringUtil.isStringNull(importerInfo.getTelNo()));
            param.setIncInsureRegNo(StringUtil.isStringNull(importerInfo.getIncInsuRegNo()));
            param.setIncEstRePreNo(StringUtil.isStringNull(importerInfo.getIncEstrePreNo()));
            param.setNewsShipper(StringUtil.isStringNull(importerInfo.getConditionComment()));

            if(!StringUtil.isStringEmpty(importerInfo.getZipCd())){
                param.setImpPostCode(importerInfo.getZipCd());
            }

            if(!StringUtil.isStringEmpty(importerInfo.getEstimationFlgCd())){
                param.setEstimationFlgCd(importerInfo.getEstimationFlgCd());
            } else {
                param.setEstimationFlgCd(defaultMap.get("027"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getDeliveryDateExtCd())){
                param.setDeliveryDateExtCd(importerInfo.getDeliveryDateExtCd());
            } else {
                param.setDeliveryDateExtCd(defaultMap.get("029"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getPayMethodDisc())){
                param.setPayMethodDisc(importerInfo.getPayMethodDisc());
            } else {
                param.setPayMethodDisc(defaultMap.get("030"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getConditionBankAccountNo())){
                param.setAccountNo(importerInfo.getConditionBankAccountNo());
            } else {
                param.setAccountNo(defaultMap.get("003"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getConditionCollateralNo1())){
                param.setSecurityRegNo1(importerInfo.getConditionCollateralNo1());
            } else {
                param.setSecurityRegNo1(defaultMap.get("004"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getConditionCollateralNo2())){
                param.setSecurityRegNo2(importerInfo.getConditionCollateralNo2());
            } else {
                param.setSecurityRegNo2(defaultMap.get("005"));
            }

            if(!StringUtil.isStringEmpty(importerInfo.getInsuranceClassCd())){
                param.setInsuranceClassCd(importerInfo.getInsuranceClassCd());
            } else {
                if(invoiceValConCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_FOB) || invoiceValConCd.equals(OAIT001CommonConstants.INVOICE_VALUE_CD_CF)){
                    param.setInsuranceClassCd(defaultMap.get("022"));
                } else {
                    param.setInsuranceClassCd(null);
                }
            }
        }
        return param;
    }


    private int insertImportDataDetail(OAIT001DetailDataDao param){
        return mapper.insertImportDataDetail(param);
    }

    private String getRate(String currencyCode){
        return mapper.getRate(currencyCode);
    }

    private boolean getCountIDAItem(OAIT001UploadDto param,String item){
        boolean result = false;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("deptCd",param.getCurrentDeptCd());
        paramMap.put("item", item);
        int cnt = mapper.getCountIDAItem(paramMap);
        if(cnt > 0){
            result = true;
        }
        return result;
    }

    private int getMaximumCargoPiece(String deptCd){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd",deptCd);
        return mapper.getMaximumCargoPiece(paramMap);
    }

    private int getMaximumCargoWeight(String deptCd){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd",deptCd);
        return mapper.getMaximumCargoWeight(paramMap);
    }


    private String createCellErrorMessage(List<String> errorCells){
        String returnStr = "[ ";
        for(int i = 0; i < errorCells.size(); i++){
            if(i == errorCells.size()-1){
                returnStr = returnStr + errorCells.get(i)+ " ]";
            } else {
                returnStr = returnStr + errorCells.get(i) + " , ";
            }
        }
        return returnStr;
    }


}
