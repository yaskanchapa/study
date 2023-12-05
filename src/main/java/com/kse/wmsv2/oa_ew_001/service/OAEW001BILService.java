package com.kse.wmsv2.oa_ew_001.service;


import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_ew_001.common.OAEW001BILConstants;
import com.kse.wmsv2.oa_ew_001.common.OAEW001CommonConstants;
import com.kse.wmsv2.oa_ew_001.dao.OAEW001BILReportDao;
import com.kse.wmsv2.oa_ew_001.dao.OAEW001SelectedDataDao;
import com.kse.wmsv2.oa_ew_001.dto.OAEW001ReturnDto;
import com.kse.wmsv2.oa_ew_001.mapper.OAEW001ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Slf4j
@Service
public class OAEW001BILService extends OAEW001CommonConstants {

    @Autowired
    OAEW001ReportMapper mapper;

    @Autowired
    OAEW001CommonService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAEW001ReturnDto writeBIL(HttpHeaders headers, Object param, String reCreateFlg) {
        OAEW001ReturnDto resultDto = new OAEW001ReturnDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();
        int folderCnt = 0;
        int fileCnt = 0;

        List<Map<String, String>> paramMapList = getParameterList((List<?>) param, headers);
        List<String> fileNameList = new ArrayList<>();
        int seq = 1;
        for (Map<String, String> paramMap : paramMapList) {

            String s3Path = "";
            String bilFolderName = "";

            try {

                // ステータス用データ
                COMMONStatusDto statusDao = new COMMONStatusDto();
                String userCd = commonService.getUserCd(headers);
                String date = getTimeStampNow();
                statusDao.setUserCd(userCd);
                statusDao.setDate(date);
                // statusDao.setCustomStatusCd(STATUS_CD_BIL_REPORT);
                statusDao.setBondStatusCd(STATUS_CD_BIL_REPORT);
                statusDao.setLinkDataClass(DEFAULT_LINK_DATA_CLASS);
                statusDao.setBusinessClass(BUSINESS_CLASS);
                statusDao.setAwbNo(paramMap.get("awbNo"));

                // データ更新用
                Map<String, String> updateParamMap = new HashMap<>();
                updateParamMap.put("awbNo", paramMap.get("awbNo"));
                updateParamMap.put("statusCd", STATUS_CD_BIL_REPORT);
                updateParamMap.put("userCd", userCd);
                updateParamMap.put("date", date);

                // CS_SEND_MESSAGE更新用
                Map<String, String> updateParamMap2 = new HashMap<>();
                updateParamMap2.put("importExportClass", IMPORT_EXPORT_CLASS);
                updateParamMap2.put("messageType", MESSAGE_TYPE);
                updateParamMap2.put("messageCreateDate", date);
                updateParamMap2.put("bondedWareHouseCd", paramMap.get("deptCd"));
                updateParamMap2.put("userCd", userCd);
                updateParamMap2.put("date", date);

                // S3パース取得
                s3Path = reportService.getS3Path2(BIL, SIM_AUTO);
                s3FolderList.add(s3Path);

                // 電文作成対象データ取得
                List<OAEW001BILReportDao> dataList = new ArrayList<OAEW001BILReportDao>();
                if ("0".equals(reCreateFlg)) {
                    dataList = mapper.getBILDataList(paramMap);
                } else {
                    dataList = mapper.getBILDataListRe(paramMap);
                }

                // 対象データ存在チェック
                if (dataList.size() == 0) {
                    resultDto.setErrorFlag(RESULT_ERROR);
                    resultDto.setMessage(MSG_ERR_351);
                    return resultDto;
                }

                // 詳細項目作成
                int bilSeq = 0;
                StringBuffer bilSb = new StringBuffer();
                List<StringBuffer> bilList = new ArrayList<>();

                for (OAEW001BILReportDao oaew001BILReportDao : dataList) {
                    bilSb.append(writeDetail(oaew001BILReportDao));
                    // 16件チェック用
                    bilSeq++;

                    if ("0".equals(reCreateFlg)) {
                        // ステータス更新
                        statusDao.setCwbNoDeptCd(INIT_CWBNODEPTCD);
                        statusDao.setBwbNo(oaew001BILReportDao.getBwbNo());
                        statusDao.setCwbNo(oaew001BILReportDao.getCwbNo());
                        stsServ.updateStatusMaster(statusDao);

                        // データ更新
                        updateParamMap.put("cwbNo", oaew001BILReportDao.getCwbNo());
                        updateParamMap.put("cwbNoDeptCd", INIT_CWBNODEPTCD);
                        int updateResult = mapper.updateMasterStatus(updateParamMap);
                        if (updateResult == 0) {
                            throw new Exception(OAET001CommonConstants.MSG_ERR_004);
                        }
                    }

                    if (bilSeq > 0 && bilSeq % 16 == 0) {
                        bilSeq = 0;
                        bilList.add(bilSb);
                        bilSb = new StringBuffer();
                    }
                }

                // ファイル作成
                String dateyyyyMMddHHmmss = DateUtil.getNow("yyyyMMddHHmmss");

                // フォルダとファイル生成
                if (bilList.size() > 0 || bilSb.length() > 0) {
                    bilFolderName = createFolder(param, BIL, SIM_AUTO_BK, dateyyyyMMddHHmmss);
                    folderList.add(bilFolderName);
                    folderCnt = folderCnt + 1;
//                    int seq = 1;

                    // 16件のデータ
                    if (bilList.size() > 0) {
                        for (StringBuffer stringBuffer : bilList) {
                            String fileName = reportService.getReportName(BIL, paramMap.get("deptCd"), dateyyyyMMddHHmmss, String.format("%04d", seq), "");
                            fileName = fileName.replace(".txt", "");
                            String filePath = reportService.getReportFilePath(bilFolderName, fileName);

                            StringBuilder commonSb = new StringBuilder();
                            String commonColumn = "";
                            commonColumn = reportService.createCommonColumnAuto(headers, MESSAGE_TYPE, fileName, "");
                            String format = "%0" + COMMON_LENCABLE + "d";
                            commonSb.append(writeHeader(dataList.get(0)));

                            // ファイル作成
                            StringBuilder tmpSb = new StringBuilder();

                            int tmpLen = COMMON_LENCOMMONCABLE + OAEW001BILConstants.BIL_HD_LENGTH + (OAEW001BILConstants.BIL_REPDATA_LENGTH * 16);
                            tmpSb.append(commonColumn);
                            tmpSb.append(String.format(format, tmpLen));
                            tmpSb.append("\r\n");
                            tmpSb.append(commonSb);
                            tmpSb.append(stringBuffer);
                            //                        tmpSb.append("\r\n");
                            BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                            bf.write(tmpSb.toString());
                            bf.flush();
                            bf.close();

                            // S3保存
                            if (!reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                                throw new Exception("S3 Bucket Error");
                            }

                            // CS_SEND_MESSAGE更新
                            if ("0".equals(reCreateFlg)) {
                                updateParamMap2.put("messageFileName", fileName);
                                updateParamMap2.put("filePath", s3Path);
                                int updateResult = mapper.insertCsSendMessage(updateParamMap2);
                                if (updateResult == 0) {
                                    throw new Exception(OAEW001CommonConstants.MSG_ERR_012);
                                }
                            }

                            fileNameList.add(fileName);
                            fileCnt = fileCnt + 1;
                            seq = seq + 1;
                        }
                    }

                    // まとめて記載されるデータ(16件のデータ束、次の束が16件未満の場合はまとめて記載)
                    if (bilSb.length() > 0) {
                        StringBuilder tmpSb = new StringBuilder();
                        String fileName = reportService.getReportName(BIL, paramMap.get("deptCd"), dateyyyyMMddHHmmss, String.format("%04d", seq), "");
                        fileName = fileName.replace(".txt", "");
                        String filePath = reportService.getReportFilePath(bilFolderName, fileName);

                        StringBuilder commonSb = new StringBuilder();
                        String commonColumn = "";
                        commonColumn = reportService.createCommonColumnAuto(headers, MESSAGE_TYPE, fileName, "");
                        String format = "%0" + COMMON_LENCABLE + "d";
                        commonSb.append(writeHeader(dataList.get(0)));

                        // ファイル作成
                        int tmpLen = COMMON_LENCOMMONCABLE + OAEW001BILConstants.BIL_HD_LENGTH + (OAEW001BILConstants.BIL_REPDATA_LENGTH * bilSeq);
                        tmpSb.append(commonColumn);
                        tmpSb.append(String.format(format, tmpLen));
                        tmpSb.append("\r\n");
                        tmpSb.append(commonSb);
                        tmpSb.append(bilSb);
                        //                    tmpSb.append("\r\n");
                        BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                        bf.write(tmpSb.toString());
                        bf.flush();
                        bf.close();

                        // S3保存
                        if (!reportService.saveReportS3(s3Path, fileName, tmpSb.toString())) {
                            throw new Exception("S3 Bucket Error");
                        }

                        // CS_SEND_MESSAGE更新
                        if ("0".equals(reCreateFlg)) {
                            updateParamMap2.put("messageFileName", fileName);
                            updateParamMap2.put("filePath", s3Path);
                            int updateResult = mapper.insertCsSendMessage(updateParamMap2);
                            if (updateResult == 0) {
                                throw new Exception(OAEW001CommonConstants.MSG_ERR_012);
                            }
                        }

                        fileNameList.add(fileName);
                        fileCnt = fileCnt + 1;
                        seq = seq + 1;
                    }
                }
                // 作成した電文を自動送信用フォルダにコピーする
                for (String fileName : fileNameList) {
                    // 元ファイル
                    File sourceFile = new File(bilFolderName + File.separator + fileName);
                    //　コピー先
                    String destFilePath = reportService.getReportPath(SIM_AUTO);
                    File folder = new File(destFilePath);
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    File destFile = new File(destFilePath + File.separator + fileName);
                    FileCopyUtils.copy(sourceFile, destFile);
                }

                // 結果保存
                resultDto.setErrorFlag(RESULT_SUCCESS);
                resultDto.setCntFile(fileCnt);
                resultDto.setCntFolder(folderCnt);

            } catch (Exception e) {

                // RollBack処理
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

                // ログ
                log.error(e.getMessage());

                // 結果保存
                resultDto.setErrorFlag(RESULT_ERROR);
                resultDto.setMessage(MSG_ERR_351);

                // ファイル削除
                if (!StringUtil.isStringEmpty(bilFolderName)) {
                    commonService.deleteFolder(bilFolderName);
                }

                if (!StringUtil.isStringEmpty(s3Path)) {
                    reportService.deleteReportS3(s3Path, fileNameList);
                }

            }
        }
        return resultDto;
    }


    private String createFolder(Object param, String type, String type2, String dateyyyyMMddHHmmss) {
        String folderPath = reportService.getReportPath(type2);
        File folder = new File(folderPath);
        if (!folder.exists()) {
            try {
                folder.mkdir();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        return folderPath;
    }

    private List getParameterList(List<?> dataList, HttpHeaders headers) {
        List<Map<String, String>> parameterMapList = new ArrayList<Map<String, String>>();
        for (int i = 0; i < dataList.size(); i++) {
            if (!dataList.isEmpty() && dataList.get(i) instanceof OAEW001SelectedDataDao) {
                Map<String, String> parameterMap = new HashMap<>();
                OAEW001SelectedDataDao data = (OAEW001SelectedDataDao) dataList.get(i);
                parameterMap.put("awbNo", data.getAwbNo());
                parameterMap.put("bondInBillNo", data.getBondInBillNo());
                parameterMap.put("deptCd", commonService.getDeptCd(headers));
                parameterMap.put("companyCd", commonService.getCompanyCd(headers));
                parameterMap.put("userAuthorityCd", commonService.getAuth(headers));
                parameterMapList.add(parameterMap);
            }
        }
        return parameterMapList;
    }


    private StringBuffer writeHeader(OAEW001BILReportDao data) {
        StringBuffer sb = new StringBuffer();
        // 1.共通項目

        // 2.搬入伝票番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBondInBillNo()), OAEW001BILConstants.BIL01_BONDINBILLNO, true));
        sb.append("\r\n");

        // 3.棟記号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_01, true));
        sb.append("\r\n");

        // 4.車上通関表示
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_02, true));
        sb.append("\r\n");

        // 5.時間外搬入表示
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_03, true));
        sb.append("\r\n");

        return sb;
    }


    private StringBuffer writeDetail(OAEW001BILReportDao data) {
        StringBuffer sb = new StringBuffer();
        // 6.処理識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_04, true));
        sb.append("\r\n");

        // 7.CWBNO
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAEW001BILConstants.BIL01_CWBNO, true));
        sb.append("\r\n");

        // 8.積込港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DEPPORT, true));
        sb.append("\r\n");

        // 9.許可・承諾等番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_05, true));
        sb.append("\r\n");

        // 10.品名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getItem()).toUpperCase(), OAEW001BILConstants.BIL01_ITEM, true));
        sb.append("\r\n");

        // 11.事故貨物
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_DUMMY_06, true));
        sb.append("\r\n");

        // 12.備考
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(""), OAEW001BILConstants.BIL01_REMARKS, true));
        sb.append("\r\n");

        return sb;
    }

}
