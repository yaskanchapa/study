package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CHAConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001CHAReportDao;
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
public class OAIT001CHAService extends OAIT001CommonConstants{

    @Autowired
    OAIT001ReportMapper mapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    StatusService stsServ;

    public OAIT001ReportResultDto writeCHA(HttpHeaders headers,Object param){
        OAIT001ReportResultDto returnDto = new OAIT001ReportResultDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();
        Map<String, String> paramMap = new HashMap<>();
        paramMap = getParameterMap(param,headers);
        int folderCnt = 0;
        int fileCnt = 0;
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();
        String idaFolder = "";
        String micFolder = "";

        // ステータス
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(STATUS_CD_CHA_REPORT);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA_CLASS);
        statusDao.setBusinessClass(BUSINESS_CLASS_IMPORT);
        statusDao.setAwbNo(paramMap.get("awbNo"));
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", paramMap.get("awbNo"));
        updateParamMap.put("statusCd", STATUS_CD_CHA_REPORT);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{
            // S3パース取得
            s3Path = reportService.getS3Path("CHA");
            s3FolderList.add(s3Path);

            // 電文作成用データ取得
            List<OAIT001CHAReportDao> dataList = mapper.getCHAData(paramMap);
            if(dataList.size() == 0){
                returnDto.setErrorFlag(RESULT_ERROR);
                returnDto.setMessage(MSG_ERR_351);
                return returnDto;
            }

            // 共通項目作成
            String commonColumn = "";
            commonColumn = reportService.createCommonColumn(headers,"CHA","");
            String format = "%0" + COMMON_LENCABLE + "d";

            // ヘッダ作成
            StringBuilder headerSb = new StringBuilder();
            headerSb.append(writeHeader(dataList.get(0)));

            // 明細作成
            int micSeq = 0;
            int idaSeq = 0;
            StringBuffer detailMICSb = new StringBuffer();
            StringBuffer detailIDASb = new StringBuffer();
            List<StringBuffer> micList = new ArrayList<>();
            List<StringBuffer> idaList = new ArrayList<>();

            for (OAIT001CHAReportDao oait001CHAReportDao : dataList) {
                // IDAの場合
                if (oait001CHAReportDao.getIdaFlg().equals("1")) {
                    // 電文内容作成
                    detailIDASb.append(writeDetail(oait001CHAReportDao));
                    // 20件チェック用
                    idaSeq = idaSeq + 1;
                } else {
                    // MICの場合
                    detailMICSb.append(writeDetail(oait001CHAReportDao));
                    // 20件チェック用
                    micSeq = micSeq + 1;
                }

                // ステータス更新
                statusDao.setCwbNoDeptCd(oait001CHAReportDao.getCwbNoDeptCd());
                statusDao.setBwbNo(oait001CHAReportDao.getBwbNo());
                statusDao.setCwbNo(oait001CHAReportDao.getCwbNo());
                stsServ.updateStatusMaster(statusDao);

                // データ更新
                updateParamMap.put("cwbNo", oait001CHAReportDao.getCwbNo());
                updateParamMap.put("cwbNoDeptCd", oait001CHAReportDao.getCwbNoDeptCd());
                int updateResult = mapper.updateMasterStatus(updateParamMap);
                if (updateResult == 0) {
                    throw new Exception(OAET001CommonConstants.MSG_ERR_004);
                }

                //２０件超えった場合
                if (micSeq > 0 && micSeq % 20 == 0) {
                    micSeq = 0;
                    micList.add(detailMICSb);
                    detailMICSb = new StringBuffer();
                } else if (idaSeq > 0 && idaSeq % 20 == 0) {
                    idaSeq = 0;
                    idaList.add(detailIDASb);
                    detailIDASb = new StringBuffer();
                }
            }

            // ファイル生成用データ
            String destination = dataList.get(0).getCatereringPlace();
            String awbNo = dataList.get(0).getAwbNo();


            // MIC用フォルダ生成とファイル作成
            if(micList.size() > 0 || detailMICSb.length() > 0){
                micFolder = createFolder(param,"CM");
                folderList.add(micFolder);
                folderCnt = folderCnt + 1;
                int seq = 1;
                // 20件データから作成
                if(micList.size() > 0){
                    for (StringBuffer stringBuffer : micList) {
                        // 共通項目用
                        StringBuilder tmpSb = new StringBuilder();
                        int tmpLen = COMMON_LENCOMMONCABLE + OAIT001CHAConstants.CHA_HD_LENGTH + (OAIT001CHAConstants.CHA_REPDATA_LENGTH * 20);
                        tmpSb.append(commonColumn);
                        tmpSb.append(String.format(format, tmpLen));
                        tmpSb.append("\r\n");
                        tmpSb.append(headerSb);
                        tmpSb.append(stringBuffer);

                        String fileName = reportService.getReportName("CM", destination, awbNo, String.format("%04d", seq), "");
                        String filePath = reportService.getReportFilePath(micFolder, fileName);
                        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                        bf.write(tmpSb.toString());
                        bf.flush();
                        bf.close();

                        // S3保存
                        if (StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                            throw new Exception("S3 Bucket Error");
                        }

                        fileNameList.add(fileName);
                        fileCnt = fileCnt + 1;
                        seq = seq + 1;
                    }
                }
                if(detailMICSb.length() > 0 ){
                    StringBuilder tmpSb = new StringBuilder();
                    int tmpLen = COMMON_LENCOMMONCABLE + OAIT001CHAConstants.CHA_HD_LENGTH + (OAIT001CHAConstants.CHA_REPDATA_LENGTH * micSeq);
                    tmpSb.append(commonColumn);
                    tmpSb.append(String.format(format,tmpLen));
                    tmpSb.append("\r\n");
                    tmpSb.append(headerSb);
                    tmpSb.append(detailMICSb);
                    String fileName = reportService.getReportName("CM",destination,awbNo,String.format("%04d", seq),"");
                    String filePath = reportService.getReportFilePath(micFolder,fileName);
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                    bf.write(tmpSb.toString());
                    bf.flush();
                    bf.close();
                    // S3保存
                    if(StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path,fileName,tmpSb.toString())){
                        throw new Exception("S3 Bucket Error");
                    }

                    fileNameList.add(fileName);
                    fileCnt = fileCnt + 1;
                    seq = seq+ 1;
                }
            }


            // IDA用フォルダ生成とファイル作成
            if(idaList.size() > 0 || detailIDASb.length() > 0){
                idaFolder = createFolder(param,"CI");
                folderList.add(idaFolder);
                folderCnt = folderCnt + 1;
                int seq = 1;
                // 20件データから作成
                if(idaList.size() > 0){
                    for (StringBuffer stringBuffer : idaList) {
                        // 共通項目用
                        StringBuilder tmpSb = new StringBuilder();
                        int tmpLen = COMMON_LENCOMMONCABLE + OAIT001CHAConstants.CHA_HD_LENGTH + (OAIT001CHAConstants.CHA_REPDATA_LENGTH * 20);
                        tmpSb.append(commonColumn);
                        tmpSb.append(String.format(format, tmpLen));
                        tmpSb.append("\r\n");
                        tmpSb.append(headerSb);
                        tmpSb.append(stringBuffer);

                        String fileName = reportService.getReportName("CI", destination, awbNo, String.format("%04d", seq), "");
                        String filePath = reportService.getReportFilePath(idaFolder, fileName);
                        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                        bf.write(tmpSb.toString());
                        bf.flush();
                        bf.close();
                        // S3保存
                        if (StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                            throw new Exception("S3 Bucket Error");
                        }

                        fileNameList.add(fileName);
                        fileCnt = fileCnt + 1;
                        seq = seq + 1;
                    }
                }
                if(detailIDASb.length() > 0 ){
                    StringBuilder tmpSb = new StringBuilder();
                    int tmpLen = COMMON_LENCOMMONCABLE + OAIT001CHAConstants.CHA_HD_LENGTH + (OAIT001CHAConstants.CHA_REPDATA_LENGTH * idaSeq);
                    tmpSb.append(commonColumn);
                    tmpSb.append(String.format(format,tmpLen));
                    tmpSb.append("\r\n");
                    tmpSb.append(headerSb);
                    tmpSb.append(detailIDASb);
                    String fileName = reportService.getReportName("CI",destination,awbNo,String.format("%04d", seq),"");
                    String filePath = reportService.getReportFilePath(idaFolder,fileName);
                    BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                    bf.write(tmpSb.toString());
                    bf.flush();
                    bf.close();
                    // S3保存
                    if(StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path,fileName,tmpSb.toString())){
                        throw new Exception("S3 Bucket Error");
                    }

                    fileNameList.add(fileName);
                    fileCnt = fileCnt + 1;
                    seq = seq+ 1;
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
            if(!StringUtil.isStringEmpty(idaFolder)){
                commonService.deleteFolder(idaFolder);
            }
            if(!StringUtil.isStringEmpty(micFolder)){
                commonService.deleteFolder(micFolder);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return returnDto;
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

    private String createFolder(Object param,String type) {
        String folderPath = reportService.getReportPath("CHA");
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


    private StringBuffer writeHeader(OAIT001CHAReportDao data){
        StringBuffer sb = new StringBuffer();

        // 1.共通項目


        // 2.MAWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getAwbNo()), OAIT001CHAConstants.CHA_AWBNO,true));
        sb.append("\r\n");

        // 3.孫混載表示
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_GRANDCHILDMIXED,true));
        sb.append("\r\n");

        // 4.委託元混載業者
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_REQMIXEDFORWARDER,true));
        sb.append("\r\n");

        // 5.到着便名1
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getArrFlt1()), OAIT001CHAConstants.CHA_ARRFLT_1,true));
        sb.append("\r\n");

        // 6.到着便名2
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getArrFlt2()), OAIT001CHAConstants.CHA_ARRFLT_2,true));
        sb.append("\r\n");

        // 7.到着空港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getArrAirportCd()), OAIT001CHAConstants.CHA_ARRAIRPORTCD,true));
        sb.append("\r\n");

        // 8.仕出地
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCatereringPlace()), OAIT001CHAConstants.CHA_CATERERINGPLACE,true));

        return sb;
    }

    private StringBuffer writeDetail(OAIT001CHAReportDao data){
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n");

        // 9.HAWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAIT001CHAConstants.CHA_CWBNO,true));
        sb.append("\r\n");

        // 10.総個数
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_CARGOPIECE,false));
        sb.append("\r\n");

        // 11.総重量
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_CARGOWEIGHT,false));
        sb.append("\r\n");

        // 12.重量単位コード
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_WEIGHTUNITCD,true));
        sb.append("\r\n");

        // 13.品名
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_ITEM,true));
        sb.append("\r\n");

        // 14.特殊貨物記号
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_SPECIALCARGOSIGN,true));
        sb.append("\r\n");

        // 15.仕向地
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_DESTINATION,true));
        sb.append("\r\n");

        // 16.搬入保税蔵置場
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_CARBONDEDWAREHOUSE,true));
        sb.append("\r\n");

        // 17.管理資料計上表示
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_DUMMY_01,true));
        sb.append("\r\n");

        // 18.混載仕立業
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_MIXEDFORWARDER,true));
        sb.append("\r\n");

        // 19.荷送人名
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 20.荷送人住所
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_EXPCUSTOMERADD_LUMP,true));
        sb.append("\r\n");

        // 21.荷送人電話番号
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_EXPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 22.荷受人コード
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_IMPCUSTOMERNO,true));
        sb.append("\r\n");

        // 23.荷受人名
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_IMPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 24.荷受人住所
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_IMPCUSTOMERADD_LUMP,true));
        sb.append("\r\n");

        // 25.荷受人電話番号
        sb.append(StringUtil.fillSpace("", OAIT001CHAConstants.CHA_IMPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 26.訂正理由コード
        sb.append(StringUtil.fillSpace(OAIT001CHAConstants.CHA_IMPCORRECTION_REASON_CD, OAIT001CHAConstants.CHA_IMPCORRECTION_REASON,true));
        return sb;
    }
}
