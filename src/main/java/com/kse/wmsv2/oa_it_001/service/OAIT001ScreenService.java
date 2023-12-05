package com.kse.wmsv2.oa_it_001.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.kse.wmsv2.common.dto.COMMONImportAddAccDto;
import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import com.kse.wmsv2.common.service.CommonImportService;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001DefaultDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001HeaderDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDADao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDAMainDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDASubDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001SearchResultDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001StatusDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001CommonDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ImageDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReturnDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchResultDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001UpdateCellDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001ScreenMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAIT001ScreenService  {

    @Autowired
    StatusService stsServ;

    @Autowired
    OAIT001ScreenMapper oait001ScreenMapper ;

    @Autowired
    OAIT001FileService fileServ;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    private AwsS3 awsS3;

    @Autowired
    ReportService reportService;

    @Autowired
    CommonImportService commonImportService;

    public OAIT001SearchResultDto searchIT001(HttpHeaders headers, OAIT001SearchDto params){
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        try{
            // 検索結果リスト取得
            List<OAIT001SearchResultDao> detailList = searchResultList(params);

            // 検索ヘッダ取得
            OAIT001HeaderDao headerData = searchHeader(params);

            //結果確認
            if(headerData == null){
                returnVal.setErrFlg("error");
                returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_002);
            } else if(detailList == null){
                returnVal.setErrFlg("warning");
                returnVal.setMessage(OAIT001CommonConstants.MSG_WAR_001);
            } else {
                // 件数
                headerData = commonService.getCountMasterNumber(detailList,headerData);
                returnVal.setHeaderData(headerData);
                returnVal.setDetailDataList(detailList);
                returnVal.setErrFlg("false");
                returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_001);
            }
        } catch (Exception e){
            // エラー処理結果保存
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_001);
            returnVal.setErrFlg("error");

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }

    private OAIT001HeaderDao searchHeader(OAIT001SearchDto params) throws Exception{
        return oait001ScreenMapper.selectHeader(params);
    }


    /**
     * 検索結果を取得する。
     *
     * @param params
     * @return 検索結果リスト
     */
    private  List<OAIT001SearchResultDao> searchResultList(OAIT001SearchDto params) throws Exception{
        String[] tmpList = params.getCurrentArrFlt1().split("/");
        params.setCurrentArrFlt1(tmpList[0]);
        return oait001ScreenMapper.selectSearchResult(params);
    }

    public List<OAIT001StatusDao> searchAllStatus(String cwbNo,String businessCd,String awbNo) {
        Map<String, String> paramMap = new HashMap<>();
        List<OAIT001StatusDao> returnVal = new ArrayList<>();
        try {
            if(commonService.checkPkKey(awbNo,cwbNo,"000")){
                throw new Exception("必須項目なし");
            }

            paramMap.put("cwbNo",cwbNo);
            paramMap.put("businessClass",businessCd);
            paramMap.put("awbNo", awbNo);
            returnVal = oait001ScreenMapper.searchAllStatus(paramMap);
        } catch (Exception e){
            // ログ
            log.error(e.getMessage());
        }
        return  returnVal;
    }

    public List<OAIT001DefaultDao> getSearchDefaultList(OAIT001SearchDto params) {
        Map<String, String> paramMap = new HashMap<>();
        List<OAIT001DefaultDao> returnVal = new ArrayList<>();
        try {
            paramMap.put("deptCd", params.getCurrentDeptCd());
            paramMap.put("nameClass", params.getCurrentTabValue());
            returnVal = oait001ScreenMapper.selectDefault(paramMap);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return  returnVal;
    }

    public Map<String,List> getUploadScreenDefaultValue() {
        List<Map> deptCdList = new ArrayList<>();
        List<Map> agecnyList = new ArrayList<>();
        Map<String,List> screenDefaultValue = new HashMap<>();

        try{
            deptCdList = oait001ScreenMapper.selectDeptCdList();
            agecnyList = oait001ScreenMapper.selectAgencyList();
            screenDefaultValue.put("deptCdList", deptCdList);
            screenDefaultValue.put("agencyList", agecnyList);
        } catch (Exception e){
            log.error(e.getMessage());
        }
        return screenDefaultValue;
    }


    public OAIT001MICDao selectMICDetail(OAIT001SearchDto searchParam) {
        Map<String,String> paramMap = new HashMap<>();
        OAIT001MICDao returnVal = new OAIT001MICDao();
        try {
            paramMap.put("awbNo", searchParam.getAwbNo());
            paramMap.put("cwbNo", searchParam.getCwbNo());
            returnVal = oait001ScreenMapper.selectMICDetail(paramMap);
            if(returnVal == null){
                throw new Exception("データなし");
            } else {
                returnVal.setErrorFlg("false");
            }
        } catch (Exception e){
            e.printStackTrace();
            returnVal.setErrorFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_201);
            log.error(e.getMessage());
        }
        return returnVal;
    }

    public OAIT001IDADao selectIDADetail(OAIT001SearchDto param) {
        OAIT001IDADao returnVal = new OAIT001IDADao();
        OAIT001IDAMainDao mainDao = new OAIT001IDAMainDao();
        List<OAIT001IDASubDao> subDao = new ArrayList<>();
        try {
            mainDao = oait001ScreenMapper.selectIDAMain(param);
            if(mainDao == null) {
                throw new Exception("データなし");
            }
            subDao = oait001ScreenMapper.selectIDASub(param);
            returnVal.setMainDao(mainDao);
            returnVal.setSubDao(subDao);
            returnVal.setErrorFlg("false");
        } catch (Exception e){
            returnVal.setErrorFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_306);
            log.error(e.getMessage());
        }
        return returnVal;
    }

    @Transactional
    public OAIT001CommonDto editImportData(HttpHeaders headers, List<OAIT001SearchResultDao> params) {
        OAIT001CommonDto resultVal = new OAIT001CommonDto();
        boolean resultFlag = false;
        int targetLen = params.size();
        int updateCnt = 0;
        Map<String,String> paramMap = new HashMap<>();
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = DateUtil.getTimeStampNow();

        paramMap.put("userCd",userCd);
        paramMap.put("date", date);
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);

        statusDao.setLinkDataClass("0");
        statusDao.setBusinessClass("01");

        try{
            for(int i= 0; i < params.size(); i++){
                //データ更新
                paramMap.put("awbNo",params.get(i).getAwbNo());
                paramMap.put("cwbNo",params.get(i).getCwbNo());
                //ステータス更新用データ
                statusDao.setAwbNo(params.get(i).getAwbNo());
                statusDao.setCwbNo(params.get(i).getCwbNo());
                statusDao.setCwbNoDeptCd(params.get(i).getCwbNoDeptCd());
                statusDao.setBwbNo(params.get(i).getBwbNo());

                // ADD PROCESS
                COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
                accDto.setAwbNo(params.get(i).getAwbNo());
                accDto.setCwbNo(params.get(i).getCwbNo());
                accDto.setIdaFlg(params.get(i).getIdaFlg());
                accDto.setErrorFlg(true);
                accDto = commonImportService.getAddProcess(accDto);
                paramMap.put("inClassifyClassName",accDto.getCargoIn());
                paramMap.put("outClassifyClassName",accDto.getCargoOut());
                paramMap.put("domesticClassifyClassName",accDto.getDomestic());

                if(params.get(i).getIdaFlg().equals("1")){
                    paramMap.put("customCd",OAIT001CommonConstants.STATUS_CD_EDIT_IDA);
                    statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_EDIT_IDA);
                } else {
                    paramMap.put("customCd",OAIT001CommonConstants.STATUS_CD_EDIT_MIC);
                    statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_EDIT_MIC);
                }

                int tmpCnt = oait001ScreenMapper.editImportData(paramMap);
                updateCnt = updateCnt + tmpCnt;
                stsServ.updateStatusMaster(statusDao);
            }

            // ヘッダ更新
            int updateHdCnt = reportService.updateImportHeader(params.get(0).getAwbNo(),params.get(0).getCurrentArrFlt1(),params.get(0).getCurrentArrFlt2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            if(targetLen != updateCnt){
                throw new Exception("更新データ件数が合わないです。");
            } else {
                resultVal.setErrorFlg("false");
                resultVal.setMessage(OAIT001CommonConstants.MSG_SUC_002);
            }
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // 結果保存
            resultVal.setErrorFlg("error");
            resultVal.setMessage(OAIT001CommonConstants.MSG_ERR_106);

            // ログ
            log.error(e.getMessage());
        }
        return resultVal;
    }

    public String getCwbImagePath(OAIT001ImageDto params) {
        return oait001ScreenMapper.getCwbImagePath(params);
    }

    @Transactional
    public OAIT001CommonDto resettingDefVal(HttpHeaders headers, List<OAIT001DefaultDao> defaultRowData) {
        OAIT001CommonDto returnVal = new OAIT001CommonDto();
        Map<String,String> paramMap = new HashMap<>();

        try{
            paramMap.put("awbNo" , defaultRowData.get(defaultRowData.size()-1).getCharacterItem());
            paramMap.put("sql" , createDefaultUpdateSql(defaultRowData));
            String userCd = commonService.getUserCd(headers);
            String date = DateUtil.getTimeStampNow();
            paramMap.put("userCd",userCd);
            paramMap.put("date", date);
            int resultCnt = 0;
            resultCnt = oait001ScreenMapper.resettingDefVal(paramMap);

            if(resultCnt == 0){
                returnVal.setErrorFlg("warning");
                returnVal.setMessage(OAIT001CommonConstants.MSG_WAR_002);
            } else {
                returnVal.setErrorFlg("false");
                returnVal.setMessage(String.valueOf(resultCnt) + OAIT001CommonConstants.MSG_SUC_003);
            }
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラー結果保存
            returnVal.setErrorFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_107);

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }

    public String createDefaultUpdateSql(List<OAIT001DefaultDao> defaultRowData){
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < defaultRowData.size(); i++){
            OAIT001DefaultDao tmpDao = defaultRowData.get(i);

            if(!StringUtil.isStringEmpty(tmpDao.getCharacterItem()) && !StringUtil.isStringEmpty(tmpDao.getTableName())){
                if(tmpDao.getTableName().equals("HEAD")){
                    sb.append("HD.");
                } else if(tmpDao.getTableName().equals("DETAIL")) {
                    sb.append("DD.");
                } else {
                    sb.append("D.");
                }
                sb.append(defaultRowData.get(i).getColumnName());
                sb.append(" = '");
                sb.append(defaultRowData.get(i).getCharacterItem());
                sb.append("' , ");
                sb.append(System.getProperty("line.separator"));
            }
        }
        return sb.toString();
    }

    @Transactional
    public OAIT001CommonDto editHeaderData(HttpHeaders headers, OAIT001HeaderDao params) {
        OAIT001CommonDto returnVal = new OAIT001CommonDto();
        int reportCondition = 0;
        int mawbCount = 0;

        try{
            String userCd = commonService.getUserCd(headers);
            String date = DateUtil.getTimeStampNow();
            params.setUserCd(userCd);
            params.setDate(date);
            int updateCnt = 0;

            // チェック処理
            reportCondition = oait001ScreenMapper.checkReportCondition(params.getReportCondition());
            mawbCount = oait001ScreenMapper.checkMawbNumber(params);

            if(reportCondition != 1 ){
                returnVal.setErrorFlg("error");
                returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_110);
                return returnVal;
            }

            if(mawbCount > 0 ){
                returnVal.setErrorFlg("error");
                returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_111);
                return returnVal;
            }

            updateCnt = oait001ScreenMapper.editHeadData(params);
            if(updateCnt < 2){
                returnVal.setErrorFlg("error");
                returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_108);
            } else {
                returnVal.setErrorFlg("false");
                returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_004);
            }

            // ヘッダ更新
            int updateHdCnt = reportService.updateImportHeader(params.getAwbNo(),params.getArrFlt1(),params.getArrFlt2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラー結果保存
            returnVal.setErrorFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_108);

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }


    public List<String> selectFltList(HttpHeaders headers, Map<String,String> awbMap){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd", commonService.getDeptCd(headers));
        paramMap.put("companyCd", commonService.getCompanyCd(headers));
        paramMap.put("awbNo", awbMap.get("awbNo"));
        return oait001ScreenMapper.selectFltList(paramMap);
    }


    public OAIT001ReturnDto searchAwbNo(String awbNo) {
        OAIT001ReturnDto returnVal = new OAIT001ReturnDto();
        int awbCnt = 0;
        awbCnt = oait001ScreenMapper.searchAwbNo(awbNo);
        if(awbCnt > 0){
            returnVal.setErrorFlg("warning");
            returnVal.setMsg(OAIT001CommonConstants.MSG_WAR_003);
        }
        return returnVal;
    }

    @Transactional
    public OAIT001CommonDto deleteImportData(HttpHeaders headers, List<OAIT001SearchResultDao> params) {
        OAIT001CommonDto returnVal = new OAIT001CommonDto();
        int deleteCnt = 0;
        try{

            for(int i = 0; i < params.size(); i++){
                deleteCnt = deleteCnt + oait001ScreenMapper.deleteImportData(params.get(i));
            }

            // ヘッダ更新
            int updateHdCnt = reportService.updateImportHeader(params.get(0).getAwbNo(),params.get(0).getCurrentArrFlt1(),params.get(0).getCurrentArrFlt2());
            if(updateHdCnt != 1){
                throw new Exception("ヘッダ更新エラー");
            }

            returnVal.setErrorFlg("false");
            returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_006);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();

            //結果保存
            returnVal.setErrorFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_005);
        }
        return returnVal;
    }


    public ResponseEntity<byte[]> getPdf(HttpHeaders header, Map<String,String> pdfUrl) {
        try{
            Object pdfObj = pdfUrl.get("pdfUrl");
            String url = String.valueOf(pdfObj);
            if("null".equals(url)){
                return null;
            }
            byte[] data = awsS3.get(url);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            ResponseEntity<byte[]> response = new ResponseEntity<>(data, headers, HttpStatus.OK);
            return response;
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    @Transactional
    public OAIT001SearchResultDto changeColumnValue(HttpHeaders headers, OAIT001UpdateCellDto boxValue) {
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        try{

            if(boxValue.getTargetCol().equals("impCustomerAddLump")){
                boxValue.setTargetCol("IMPCUSTOMERADD_LUMP");
            }

            int result = oait001ScreenMapper.changeColumnValue(boxValue);

            if(result == 1){
                returnVal.setErrFlg("false");
                returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_007);
            } else {
                returnVal.setErrFlg("warning");
                returnVal.setMessage(OAIT001CommonConstants.MSG_WAR_004);
            }
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラー結果保存
            returnVal.setErrFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_010);

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }


    @Transactional
    public OAIT001SearchResultDto changeCheckBoxValue(HttpHeaders headers, OAIT001UpdateCellDto boxValue) {
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        try{
            int result = oait001ScreenMapper.changeCheckBoxValue(boxValue);

            // ADDPROC
            if(boxValue.getTargetCol().equals("idaFlg")){
                COMMONImportAddAccDto accDto = new COMMONImportAddAccDto();
                accDto.setAwbNo(boxValue.getAwbNo());
                accDto.setCwbNo(boxValue.getCwbNo());
                accDto.setIdaFlg(boxValue.getSetVal());
                accDto.setErrorFlg(true);
                if(commonImportService.setAddProcess(accDto)){
                    throw new Exception("Col Update Error 420");
                }
            }

            if(result == 1){
                returnVal.setErrFlg("false");
                returnVal.setMessage(OAIT001CommonConstants.MSG_SUC_007);
            } else {
                throw new Exception("Col Update Error 421");
            }
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // エラー結果保存
            returnVal.setErrFlg("error");
            returnVal.setMessage(OAIT001CommonConstants.MSG_ERR_010);

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return returnVal;
    }


    public OAIT001ImageDto getPdfUrl(OAIT001ImageDto param) {
        OAIT001ImageDto returnVal = new OAIT001ImageDto();
        try {
            OAIT001ImageDto pdfUrl = oait001ScreenMapper.getPdfUrl(param);

            if(StringUtil.isStringEmpty(pdfUrl.getPdfUrl1()) && StringUtil.isStringEmpty(pdfUrl.getPdfUrl2())){
                returnVal.setErrorFlag("error");
                returnVal.setMessage("エラーが発生しました。");
            } else {
                if(!StringUtil.isStringEmpty(pdfUrl.getPdfUrl1())){
                    returnVal.setPdfUrl1(pdfUrl.getPdfUrl1());
                }
                if(!StringUtil.isStringEmpty(pdfUrl.getPdfUrl2())){
                    returnVal.setPdfUrl2(pdfUrl.getPdfUrl2());
                }
                returnVal.setErrorFlag("false");
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            returnVal.setErrorFlag("error");
            returnVal.setMessage("エラーが発生しました。");
        }
        return returnVal;
    }

    public Map<String,String> getCountSearch(OAIT001SearchDto params){
        Map<String,String> resultMap = new HashMap<>();

        try{
            resultMap = oait001ScreenMapper.getCountSearch(params);
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return resultMap;
    }

    public OAIT001ReturnDto checkDeleteAll(HttpHeaders headers, OAIT001SearchDto params) { 
        OAIT001ReturnDto result = new OAIT001ReturnDto();
        try {
            OAIT001SearchResultDto searchResult = searchIT001(headers, params) ;
            List<OAIT001SearchResultDao> gridList = searchResult.getDetailDataList();
            // 1  パラメータを定義する。
            String userAuth = commonService.getUserManageAuth(headers);
            Integer count = 0;
            // 2	削除前のチェック処理を実施。
            // 2.1	検索結果のリストをループして、各レコードを利用して、下記のクエリを実施する。
            for (OAIT001SearchResultDao gridRow : gridList) {
                int registeredNum = oait001ScreenMapper.countAiStatusHistory(gridRow.getAwbNo(), gridRow.getCwbNo(), gridRow.getCwbNoDeptCd());
                if (registeredNum > 0) {
                    if (userAuth.equals("01")) {
                        result.setMsg("搬入スキャンされているため、削除できません。");
                        result.setResult(false);
                        return result;
                    }
                    count++;
                }
            }
            // 2.2	パラメータ.Count　＞　０　の場合、下記のチェックを行う。	
            if (count > 0) {
                if (userAuth.equals("02")) {
                    result.setMsg("搬入済みです。全件削除しますか？");
                    result.setResult(true);
                }
            } else {
                // 2.3	2.2以外の場合、確認ダイアログを表示して、ユーザーの選択が「キャンセル」の場合、処理終了する。　	
                result.setMsg("全件削除しますか？");
                result.setResult(true);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(new Error("500", e.getMessage()));
        }
        return result;
    }

    @Transactional
    public OAIT001ReturnDto deleteAll(HttpHeaders headers, OAIT001SearchDto params) {
        OAIT001ReturnDto result = new OAIT001ReturnDto();
        try {
            OAIT001SearchResultDto searchResult = searchIT001(headers, params) ;
            List<OAIT001SearchResultDao> gridList = searchResult.getDetailDataList();
            if (gridList.size() == 0) {
                result.setMsg("該当の検索条件で削除データが存在しません");
                result.setResult(false);
                return result;
            }
            // 1  パラメータを定義する。
            String awbNo = params.getAwbNo();
            String arrFlt1 = "";
            String arrFlt2 = "";
            // 3	削除処理を実施。
            // 3.1	検索結果のリストをループして、下記のデータ更新モデルにより、「AI_DATA」テーブルのデータを更新する。
            for (OAIT001SearchResultDao gridRow : gridList) {
                arrFlt1 = gridRow.getCurrentArrFlt1();
                arrFlt2 = gridRow.getCurrentArrFlt2();
                oait001ScreenMapper.deleteAiDataRow(awbNo, gridRow.getCwbNo(), gridRow.getCwbNoDeptCd());
            }

            // 全件データを削除する後に、ヘッダのデータも削除する
            if (!arrFlt1.isEmpty() && !arrFlt2.isEmpty() && oait001ScreenMapper.countAiData(awbNo, arrFlt1, arrFlt2) <= 0) {
                oait001ScreenMapper.deleteAiHead(awbNo, arrFlt1, arrFlt2);
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
            log.error(e.getMessage());
            throw new InternalServerErrorException(new Error("500", e.getMessage()));
        }
        result.setMsg("削除が完了しました。");
        result.setResult(true);
        return result;
    }
}