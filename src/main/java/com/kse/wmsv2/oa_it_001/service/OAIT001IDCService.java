package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001IDCConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001MICConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDCDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
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
public class OAIT001IDCService {

    @Autowired
    StatusService stsServ;

    @Autowired
    OAIT001ReportMapper mapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;


    public Map<String, String> writeIdc(HttpHeaders headers, OAIT001SearchDto params) {
        Map<String, String> returnMap = new HashMap<>();
        Map<String, String> paramMap = new HashMap<>();
        paramMap = getParameterMap(params,headers);
        int fileCnt = 0;
        int folderCnt = 0;
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();
        String folder = "";
        // ステータス
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(OAIT001CommonConstants.STATUS_CD_IDC_REPORT);
        statusDao.setLinkDataClass("0");
        statusDao.setBusinessClass("01");
        statusDao.setAwbNo(paramMap.get("awbNo"));

        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", paramMap.get("awbNo"));
        updateParamMap.put("statusCd", OAIT001CommonConstants.STATUS_CD_IDC_REPORT);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try {
            s3Path = reportService.getS3Path("IDC");
            List<OAIT001IDCDao> idcList = new ArrayList<>();
            idcList = mapper.getIDCDataList(paramMap);
            if(idcList.size() != 0){
                folder = createFolder(params);
                folderCnt = 1;
            } else {
                throw new Exception("NO DATA");
            }
            String commonColumn = "";
            commonColumn = reportService.createCommonColumn(headers,"IDC","");
            String format = "%0" + OAIT001CommonConstants.COMMON_LENCABLE + "d";
            int totalLen = OAIT001CommonConstants.COMMON_LENCOMMONCABLE + OAIT001IDCConstants.IDC_LENCOMMON;
            commonColumn = commonColumn + String.format(format, totalLen);

            for(int i = 0 ; i < idcList.size(); i++){
                String fileName = reportService.getReportName("IDC",idcList.get(i).getCwbNo()
                        ,idcList.get(i).getCwbNoDeptCd(),String.format("%04d", i+1),"");
                String filePath = reportService.getReportFilePath(folder,fileName);
                StringBuilder sb = new StringBuilder();
                sb = writeIDCReport(commonColumn,idcList.get(i));

                // ファイル作成
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                bf.write(sb.toString());
                bf.flush();
                bf.close();

                // S3保存
                if(!reportService.saveReportS3(s3Path,fileName,sb.toString())){
                    throw new Exception("S3 Bucket Error");
                }

                updateParamMap.put("cwbNo", idcList.get(i).getCwbNo());
                updateParamMap.put("cwbNoDeptCd", idcList.get(i).getCwbNoDeptCd());
                int updateResult = mapper.updateMasterStatus(updateParamMap);
                if (updateResult == 0) {
                    throw new Exception(OAIT001CommonConstants.MSG_ERR_004);
                }

                // ステータス更新
                statusDao.setCwbNoDeptCd(idcList.get(i).getCwbNoDeptCd());
                statusDao.setBwbNo(idcList.get(i).getBwbNo());
                statusDao.setCwbNo(idcList.get(i).getCwbNo());
                stsServ.updateStatusMaster(statusDao);

                fileNameList.add(fileName);
                fileCnt = fileCnt + 1 ;
            }
            // 結果保存
            returnMap.put("errorFlag", "false");
            returnMap.put("cntIDC", String.valueOf(folderCnt));
            returnMap.put("cntFileIDC", String.valueOf(fileCnt));
            returnMap.put("message", "");
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();

            // 結果保存
            returnMap.put("errorFlag", "error");
            returnMap.put("cntMIC", String.valueOf(folderCnt));
            returnMap.put("cntFileMIC", String.valueOf(fileCnt));
            returnMap.put("message", e.getMessage());

            // ファイル削除
            if(!StringUtil.isStringEmpty(folder)){
                commonService.deleteFolder(folder);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return returnMap;
    }

    private String createFolder(Object param) {
        String folderPath = reportService.getReportPath("IDC");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAIT001SearchDto) param).getAwbNo();
        String path = folderPath + File.separator + "IDC"+ date +"_" + awbNo;
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
        OAIT001SearchDto dto = (OAIT001SearchDto) param;
        String[] fltList = dto.getCurrentArrFlt1().split("/");
        parameterMap.put("awbNo", dto.getAwbNo());
        parameterMap.put("currentArrFlt1", fltList[0]);
        parameterMap.put("currentArrFlt2", fltList[1]);
        parameterMap.put("companyCd", commonService.getCompanyCd(headers));
        parameterMap.put("deptCd", commonService.getDeptCd(headers));
        parameterMap.put("reCreate", dto.getReCreate());

        return parameterMap;
    }


    private StringBuilder writeIDCReport(String common,OAIT001IDCDao data){
        StringBuilder sb = new StringBuilder();
        // 共通項目
        sb.append(common);
        sb.append("\r\n");
        //申告等番号
        sb.append(StringUtil.fillSpace(data.getReportNo(), OAIT001IDCConstants.IDC_REPORTNO,true));
        sb.append("\r\n");
        //申告条件コード
        sb.append(StringUtil.fillSpace(data.getReportCondition(), OAIT001IDCConstants.IDC_REPORTCONDITION,true));
        sb.append("\r\n");
        //予備
        sb.append(StringUtil.fillSpace("", OAIT001IDCConstants.IDC_DUMMY_01,true));
        sb.append("\r\n");
        return sb;
    }
}
