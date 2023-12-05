package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.common.OAET001EDCConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001ReportEdcDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReportResultDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
public class OAET001EDCService extends OAET001CommonConstants{

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    OAET001ReportMapper reportMapper;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReportResultDto writeEDC(HttpHeaders header, OAET001SearchDto param) {
        OAET001ReportResultDto resultDto = new OAET001ReportResultDto();
        String folderPath = "";
        int fileCnt = 0;
        int folderCnt = 0;
        // S3処理用
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();
        // ステータス更新
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(header);
        String userAuth = commonService.getAuth(header);
        String deptCd = commonService.getDeptCd(header);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(EDC_WRITE);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA);
        statusDao.setBusinessClass(EXPORT_BUSINESS_CD);
        statusDao.setAwbNo(param.getAwbNo());
        statusDao.setCwbNoDeptCd(DEFAULT_CWB_DEPT_CD);
        statusDao.setBwbNo(DEFAULT_BWB_NO);
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", param.getAwbNo());
        updateParamMap.put("statusCd", EDC_WRITE);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{
            //結果保存
            List<String> folderList = resultDto.getFolderList();
            List<String> s3FolderList = resultDto.getS3FolderList();
            // S3パース取得
            s3Path = reportService.getS3Path("EDC");
            s3FolderList.add(s3Path);
            // 電文作成用データ取得用MAP
            Map<String,String> dataMap = new HashMap<>();
            String depCd = reportMapper.getDeptCd(deptCd);
            dataMap.put("awbNo",param.getAwbNo());
            dataMap.put("userAuth",userAuth);
            dataMap.put("deptCd",deptCd);
            dataMap.put("depCd",depCd);

            // 電文作成用データ取得
            List<OAET001ReportEdcDao> dataList = new ArrayList<>();
            if(param.getRemakeFlg().equals("true")){
                dataList = reportMapper.getEdcDataReMake(dataMap);
            } else {
                dataList = reportMapper.getEdcDataNew(dataMap);
            }

            // データチェック
            if(dataList.size() == 0 ){
                resultDto.setErrorFlag(RESULT_SUCCESS);
                resultDto.setCntEDC(folderCnt);
                resultDto.setCntFileEDC(fileCnt);
                return resultDto;
            }

            // folder作成
            folderPath = createFolder(param);
            folderList.add(folderPath);
            folderCnt = folderCnt + 1;

            // 共通項目作成
            String commonColumn = "";
            commonColumn = reportService.createCommonColumn(header,"EDC","");
            String format = "%0" + COMMON_LENCABLE + "d";
            int totalLen = COMMON_LENCOMMONCABLE + OAET001EDCConstants.LENCOMMON;
            commonColumn = commonColumn + String.format(format, totalLen);

            // 電文作成
            for(int i = 0; i < dataList.size(); i++){
                // 電文ファイル名取得
                String fileName = reportService.getReportName("EDC",dataList.get(i).getCwbNo(),"",String.format("%04d", i+1),"");
                String filePath = reportService.getReportFilePath(folderPath,fileName);

                // 電文内容作成
                StringBuilder sb = new StringBuilder();
                sb.append(writeEDCReport(dataList.get(0),commonColumn));

                // 電文ファイル作成
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                bf.write(sb.toString());
                bf.flush();
                bf.close();

                // S3保存
                if(StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path,fileName,sb.toString())){
                    throw new Exception("S3 Bucket Error");
                }
                fileNameList.add(fileName);

                // 再作成チェック：再作成の場合、更新はしない。
                if(!param.getRemakeFlg().equals("true")){
                    // AE_DATAテーブルアップデート
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("cwbNo",dataList.get(i).getCwbNo());
                    paramMap.put("esaFlg", "2");
                    paramMap.put("statusCd", EDC_WRITE);
                    reportMapper.updateAEDATA(paramMap);
                    // ステータス更新
                    statusDao.setCwbNo(dataList.get(i).getCwbNo());
                    stsServ.updateStatusMaster(statusDao);
                    // データ更新
                    updateParamMap.put("cwbNo",dataList.get(i).getCwbNo());
                    int updateResult = reportMapper.updateMasterStatus(updateParamMap);
                    if(updateResult == 0){
                        throw new Exception(MSG_ERR_907);
                    }
                }
                // ファイル作成CNT
                fileCnt = fileCnt + 1;
            }
            // 処理結果保存
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setCntEDC(folderCnt);
            resultDto.setCntFileEDC(fileCnt);
            resultDto.setFolderList(folderList);
            resultDto.setS3FolderList(s3FolderList);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            //ログ
            log.error(e.getMessage());
            e.printStackTrace();

            // エラー結果保存
            resultDto.setErrorFlag(RESULT_ERROR);

            // フォルダ削除
            if(!StringUtil.isStringEmpty(folderPath)){
                commonService.deleteFolder(folderPath);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return resultDto;
    }

    private StringBuffer writeEDCReport(OAET001ReportEdcDao data, String commonColumn) {
        StringBuffer sb = new StringBuffer();

        // 1.共通項目
        sb.append(commonColumn);
        sb.append("\r\n");

        // 2.輸出申告番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReportNo()), OAET001EDCConstants.EXPREPORTNO,true));
        sb.append("\r\n");

        // 3.申告条件
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportCondition()), OAET001EDCConstants.REPORTCONDITION,true));
        sb.append("\r\n");

        // 4.予備(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDCConstants.DUMMY_01,true));
        sb.append("\r\n");

        return sb;
    }


    private String createFolder(Object param) {
        String folderPath = reportService.getReportPath("EDC");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAET001SearchDto) param).getAwbNo();
        String path = folderPath + File.separator + "EDC"+ date +"_" + awbNo;
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

}
