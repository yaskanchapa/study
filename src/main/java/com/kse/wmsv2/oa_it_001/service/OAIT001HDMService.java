package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001HDMConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001HDMReportDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReportResultDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAIT001HDMService extends OAIT001CommonConstants {

    @Autowired
    OAIT001ReportMapper mapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    StatusService stsServ;


    public OAIT001ReportResultDto writeHDM( HttpHeaders headers,Object param){
        OAIT001ReportResultDto returnDto = new OAIT001ReportResultDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();

        Map<String, String> paramMap = new HashMap<>();
        paramMap = getParameterMap(param,headers);
        int folderCnt = 0;
        int fileCnt = 0;
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();
        String idaFolderName = "";
        String micFolderName = "";
        // ステータス
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(STATUS_CD_HDM_REPORT);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA_CLASS);
        statusDao.setBusinessClass(BUSINESS_CLASS_IMPORT);
        statusDao.setAwbNo(paramMap.get("awbNo"));
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", paramMap.get("awbNo"));
        updateParamMap.put("statusCd", STATUS_CD_HDM_REPORT);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{
            // S3パース取得
            s3Path = reportService.getS3Path("HDM");
            s3FolderList.add(s3Path);

            // 電文作成対象データ取得
            List<OAIT001HDMReportDao> dataList = mapper.getHDMData(paramMap);

            // 対象データ存在チェック
            if(dataList.size() == 0){
                returnDto.setErrorFlag(RESULT_ERROR);
                returnDto.setMessage(MSG_ERR_351);
                return returnDto;
            }

            // 詳細項目作成
            int micSeq = 0;
            int idaSeq = 0;
            StringBuffer micSb = new StringBuffer();
            StringBuffer idaSb = new StringBuffer();
            List<StringBuffer> micList = new ArrayList<>();
            List<StringBuffer> idaList = new ArrayList<>();


            for (OAIT001HDMReportDao oait001HDMReportDao : dataList) {
                if (oait001HDMReportDao.getIdaFlg().equals("1")) {
                    idaSb.append(writeDetail(oait001HDMReportDao));
                    // 20件チェック用
                    idaSeq = idaSeq + 1;
                } else {
                    micSb.append(writeDetail(oait001HDMReportDao));
                    // 20件チェック用
                    micSeq = micSeq + 1;
                }

                // ステータス更新
                statusDao.setCwbNoDeptCd(oait001HDMReportDao.getCwbNoDeptCd());
                statusDao.setBwbNo(oait001HDMReportDao.getBwbNo());
                statusDao.setCwbNo(oait001HDMReportDao.getCwbNo());
                stsServ.updateStatusMaster(statusDao);

                // データ更新
                updateParamMap.put("cwbNo", oait001HDMReportDao.getCwbNo());
                updateParamMap.put("cwbNoDeptCd", oait001HDMReportDao.getCwbNoDeptCd());
                int updateResult = mapper.updateMasterStatus(updateParamMap);
                if (updateResult == 0) {
                    throw new Exception(OAET001CommonConstants.MSG_ERR_004);
                }

                if (micSeq > 0 && micSeq % 20 == 0) {
                    micSeq = 0;
                    micList.add(micSb);
                    micSb = new StringBuffer();
                } else if (idaSeq > 0 && idaSeq % 20 == 0) {
                    idaSeq = 0;
                    idaList.add(idaSb);
                    idaSb = new StringBuffer();
                }
            }

            // ファイル作成
            String destination = dataList.get(0).getCatereringPlace();
            String awbNo = dataList.get(0).getAwbNo();


            // IDAフォルダとファイル生成
            if(idaList.size() > 0 || idaSb.length() > 0){
                idaFolderName = createFolder(param,"HDI");
                folderList.add(idaFolderName);
                folderCnt = folderCnt + 1;
                int seq = 1;

                // 20件のデータ
                if(idaList.size() > 0 ){
                    for (StringBuffer stringBuffer : idaList) {
                        String fileName = reportService.getReportName("HDI", destination, awbNo, String.format("%04d", seq), "");
                        String filePath = reportService.getReportFilePath(idaFolderName, fileName);


                        StringBuilder commonSb = new StringBuilder();
                        String commonColumn = "";
                        commonColumn = reportService.createCommonColumnAuto(headers, "HDM01", fileName, "");
                        String format = "%0" + COMMON_LENCABLE + "d";
                        commonSb.append(writeHeader(dataList.get(0)));


                        // ファイル作成
                        StringBuilder tmpSb = new StringBuilder();
                        int tmpLen = COMMON_LENCOMMONCABLE + OAIT001HDMConstants.HDM_HD_LENGTH + (OAIT001HDMConstants.HDM_REPDATA_LENGTH * 20);
                        tmpSb.append(commonColumn);
                        tmpSb.append(String.format(format, tmpLen));
                        tmpSb.append("\r\n");
                        tmpSb.append(commonSb);
                        tmpSb.append(stringBuffer);
                        tmpSb.append("\r\n");
                        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                        bf.write(tmpSb.toString());
                        bf.flush();
                        bf.close();

                        // S3保存
                        if (!reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                            throw new Exception("S3 Bucket Error");
                        }

                        fileNameList.add(fileName);
                        fileCnt = fileCnt + 1;
                        seq = seq + 1;
                    }
                }

                // 20件以下のデータ
                if(idaSb.length() >0){
                    StringBuilder tmpSb = new StringBuilder();
                    String fileName = reportService.getReportName("HDI",destination,awbNo,String.format("%04d", seq),"");
                    String filePath = reportService.getReportFilePath(idaFolderName,fileName);


                    StringBuilder commonSb = new StringBuilder();
                    String commonColumn = "";
                    commonColumn = reportService.createCommonColumnAuto(headers,"HDM01",fileName,"");
                    String format = "%0" + COMMON_LENCABLE + "d";
                    commonSb.append(writeHeader(dataList.get(0)));

                    // ファイル作成
                    int tmpLen = COMMON_LENCOMMONCABLE + OAIT001HDMConstants.HDM_HD_LENGTH + (OAIT001HDMConstants.HDM_REPDATA_LENGTH * idaSeq);
                    tmpSb.append(commonColumn);
                    tmpSb.append(String.format(format, tmpLen));
                    tmpSb.append("\r\n");
                    tmpSb.append(commonSb);
                    tmpSb.append(idaSb);
                    tmpSb.append("\r\n");
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                    bf.write(tmpSb.toString());
                    bf.flush();
                    bf.close();

                    // S3保存
                    if(!reportService.saveReportS3(s3Path,fileName,tmpSb.toString())){
                        throw new Exception("S3 Bucket Error");
                    }

                    fileNameList.add(fileName);
                    fileCnt = fileCnt+ 1;
                    seq = seq+ 1;
                }
            }


            // MICフォルダとファイル生成
            if(micList.size() > 0 || micSb.length() > 0 ){
                micFolderName = createFolder(param,"HDM");
                folderList.add(micFolderName);
                folderCnt = folderCnt+ 1;
                int seq = 1;
                if(micList.size() > 0 ){
                    for (StringBuffer stringBuffer : micList) {
                        StringBuilder tmpSb = new StringBuilder();
                        String fileName = reportService.getReportName("HDM", destination, awbNo, String.format("%04d", seq), "");
                        String filePath = reportService.getReportFilePath(micFolderName, fileName);

                        StringBuilder commonSb = new StringBuilder();
                        String commonColumn = "";
                        commonColumn = reportService.createCommonColumnAuto(headers, "HDM01", fileName, "");
                        String format = "%0" + COMMON_LENCABLE + "d";
                        commonSb.append(writeHeader(dataList.get(0)));

                        int tmpLen = COMMON_LENCOMMONCABLE + OAIT001HDMConstants.HDM_HD_LENGTH + (OAIT001HDMConstants.HDM_REPDATA_LENGTH * 20);
                        tmpSb.append(commonColumn);
                        tmpSb.append(String.format(format, tmpLen));
                        tmpSb.append("\r\n");
                        tmpSb.append(commonSb);
                        tmpSb.append(stringBuffer);
                        tmpSb.append("\r\n");
                        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                        bf.write(tmpSb.toString());
                        bf.flush();
                        bf.close();

                        // S3保存
                        if (!reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                            throw new Exception("S3 Bucket Error");
                        }

                        fileNameList.add(fileName);
                        fileCnt = fileCnt + 1;
                        seq = seq + 1;
                    }
                }
                if(micSb.length() >0){
                    StringBuilder tmpSb = new StringBuilder();
                    String fileName = reportService.getReportName("HDM",destination,awbNo,String.format("%04d", seq),"");
                    String filePath = reportService.getReportFilePath(micFolderName,fileName);

                    StringBuilder commonSb = new StringBuilder();
                    String commonColumn = "";
                    commonColumn = reportService.createCommonColumnAuto(headers,"HDM01",fileName,"");
                    String format = "%0" + COMMON_LENCABLE + "d";
                    commonSb.append(writeHeader(dataList.get(0)));

                    int tmpLen = COMMON_LENCOMMONCABLE + OAIT001HDMConstants.HDM_HD_LENGTH + (OAIT001HDMConstants.HDM_REPDATA_LENGTH * micSeq);
                    tmpSb.append(commonColumn);
                    tmpSb.append(String.format(format, tmpLen));
                    tmpSb.append("\r\n");
                    tmpSb.append(commonSb);
                    tmpSb.append(micSb);
                    tmpSb.append("\r\n");
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                    bf.write(tmpSb.toString());
                    bf.flush();
                    bf.close();

                    // S3保存
                    if(!reportService.saveReportS3(s3Path,fileName,tmpSb.toString())){
                        throw new Exception("S3 Bucket Error");
                    }

                    fileNameList.add(fileName);
                    fileCnt = fileCnt + 1;
                    seq = seq + 1;
                }
            }

            // 結果保存
            returnDto.setErrorFlag(RESULT_SUCCESS);
            returnDto.setCntFile(fileCnt);
            returnDto.setCntFolder(folderCnt);
            returnDto.setS3FolderList(s3FolderList);
            returnDto.setFolderList(folderList);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ
            log.error(e.getMessage());
            e.printStackTrace();

            // 結果保存
            returnDto.setErrorFlag(RESULT_ERROR);
            returnDto.setMessage(e.toString());

            // ファイル削除
            if(!StringUtil.isStringEmpty(idaFolderName)){
                commonService.deleteFolder(idaFolderName);
            }
            if(!StringUtil.isStringEmpty(micFolderName)){
                commonService.deleteFolder(micFolderName);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return returnDto;
    }


    private String createFolder(Object param,String type) {
        String folderPath = reportService.getReportPath("HDM");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String path = folderPath + File.separator + type + date;
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return path;
    }

    private Map getParameterMap(Object param,HttpHeaders headers){
        Map<String, String> parameterMap = new HashMap<>();
        if(param instanceof OAIT001MICDao){
            parameterMap.put("awbNo", ((OAIT001MICDao) param).getAwbNo());
        } else {
            parameterMap.put("awbNo", ((OAIT001SearchDto) param).getAwbNo());
            parameterMap.put("reCreate", ((OAIT001SearchDto)param).getReCreate());
            String[] tmpList = ((OAIT001SearchDto) param).getCurrentArrFlt1().split("/");
            parameterMap.put("arrFlt1", tmpList[0]);
            parameterMap.put("arrFlt2", tmpList[1]);
        }
        String deptCd = commonService.getDeptCd(headers);
        String companyCd = commonService.getCompanyCd(headers);
        parameterMap.put("deptCd",deptCd);
        parameterMap.put("companyCd",companyCd);
        return parameterMap;
    }


    private StringBuffer writeHeader(OAIT001HDMReportDao data){
        StringBuffer sb = new StringBuffer();
        // 1.共通項目


        // 2.委託元混載業者
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_REQMIXEDFORWARDER,true));
        sb.append("\r\n");

        // 3.税関官署
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_CUSTOMSDIV,true));
        sb.append("\r\n");

        // 4.MAWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getAwbNo()), OAIT001HDMConstants.HDM_AWBNO,true));
        sb.append("\r\n");

        // 5.孫混載表示
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_GRANDCHILDMIXED,true));
        sb.append("\r\n");

        // 6.到着便名1
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getArrFlt1()), OAIT001HDMConstants.HDM_ARRFLT_1,true));
        sb.append("\r\n");

        // 7.到着便名2
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getArrFlt2()), OAIT001HDMConstants.HDM_ARRFLT_2,true));
        sb.append("\r\n");

        // 8.到着空港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getAirportCd()), OAIT001HDMConstants.HDM_ARRAIRPORTCD,true));
        sb.append("\r\n");

        // 9.仕出地
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCatereringPlace()), OAIT001HDMConstants.HDM_CATERERINGPLACE,true));
        sb.append("\r\n");

        // 10.ジョイント混載
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_JOINT,true));

        return sb;
    }


    private StringBuffer writeDetail(OAIT001HDMReportDao data) {
        StringBuffer sb = new StringBuffer();

        sb.append("\r\n");

        // 11.HAWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAIT001HDMConstants.HDM_CWBNO,true));
        sb.append("\r\n");

        // 12.総個数
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCargoPiece()), OAIT001HDMConstants.HDM_CARGOPIECE,false));
        sb.append("\r\n");

        // 13.総重量
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCargoWeight()), OAIT001HDMConstants.HDM_CARGOWEIGHT,false));
        sb.append("\r\n");

        // 14.重量単位コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getWeightUnitCd()), OAIT001HDMConstants.HDM_WEIGHTUNITCD,true));
        sb.append("\r\n");

        // 15.品名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getItem()).toUpperCase(), OAIT001HDMConstants.HDM_ITEM,true));
        sb.append("\r\n");

        // 16.特殊貨物記号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getSpecialCargoSign()), OAIT001HDMConstants.HDM_SPECIALCARGOSIGN,true));
        sb.append("\r\n");

        // 17.仕向地
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDestination()), OAIT001HDMConstants.HDM_DESTINATION,true));
        sb.append("\r\n");

        // 18.搬入保税蔵置場
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_CARBONDEDWAREHOUSE,true));
        sb.append("\r\n");

        // 19.管理資料計上表示
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_MANAGEDDATAFLG,true));
        sb.append("\r\n");

        // 20.混載仕立業
        sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_MIXEDFORWARDER,true));
        sb.append("\r\n");

        // 21.荷送人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerName()), OAIT001HDMConstants.HDM_EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 22.荷送人住所
        if(StringUtil.isStringEmpty(data.getExpCustomerAdd1())){
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAddLump()), OAIT001HDMConstants.HDM_EXPCUSTOMERADD_LUMP,true));
        } else {
            String expAdd = data.getExpCustomerAdd1() + " " + data.getExpCustomerAdd2() + " " + data.getExpCustomerAdd3() + " " + data.getExpCustomerAdd1();
            sb.append(StringUtil.fillSpace(expAdd, OAIT001HDMConstants.HDM_EXPCUSTOMERADD_LUMP,true));
        }
        sb.append("\r\n");

        // 23.荷送人電話番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerTelNo()), OAIT001HDMConstants.HDM_EXPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 24.荷受人コード
        if(data.getImpCustomerNo().length() > 3 && data.getImpCustomerNo().substring(0,3).equals("KSE")){
            sb.append(StringUtil.fillSpace("", OAIT001HDMConstants.HDM_IMPCUSTOMERNO,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getImpCustomerNo()), OAIT001HDMConstants.HDM_IMPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 25.荷受人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getImpCustomerName()), OAIT001HDMConstants.HDM_IMPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 26.荷受人住所
        if(StringUtil.isStringEmpty(data.getImpCustomerAdd1())){
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getImpCustomerAddLUMP()), OAIT001HDMConstants.HDM_IMPCUSTOMERADD_LUMP,true));
        } else {
            String tmp = data.getImpCustomerAdd1() + " " + data.getImpCustomerAdd2() + " " + data.getImpCustomerAdd3() + " " + data.getImpCustomerAdd4();
            sb.append(StringUtil.fillSpace(tmp, OAIT001HDMConstants.HDM_IMPCUSTOMERADD_LUMP,true));
        }
        sb.append("\r\n");

        // 27.荷受人電話番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getImpCustomerTelNo()), OAIT001HDMConstants.HDM_IMPCUSTOMERTELNO,true));

        return sb;
    }

}
