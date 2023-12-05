package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReportResultDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ReportMapper;
import com.kse.wmsv2.oa_it_001.service.OAIT001CommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAET001ReportService extends OAET001CommonConstants {

    @Autowired
    OAET001ReportMapper mapper;

    @Autowired
    OAET001CDBService cdbServ;

    @Autowired
    OAET001MECService mecServ;

    @Autowired
    OAET001EDCService edcServ;

    @Autowired
    OAET001EDAService edaServ;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;

    public OAET001ReportResultDto checkCondition(HttpHeaders header, OAET001SearchDto param) {
        if(!StringUtil.isStringEmpty(param.getAwbNo())){
            return writeReport(header,param);
        } else {
            return writeReports(header,param);
        }
    }

    public OAET001ReportResultDto writeReports(HttpHeaders header, OAET001SearchDto param) {
        OAET001ReportResultDto resultDto = new OAET001ReportResultDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();
        List<String> awbList ;
        String message = "";
        String deptCd = commonService.getDeptCd(header);
        param.setDeptCd(deptCd);
        int cntCdb = 0;
        int cntMec = 0;
        int cntEDC = 0;
        int cntEDA = 0;
        int cntFileCDB = 0;
        int cntFileMEC = 0;
        int cntFileEDC = 0;
        int cntFileEDA = 0;

        try{
            awbList = mapper.getReportAwbList(param);
            if(awbList.size()==0){
                resultDto.setErrorFlag(RESULT_WARNING);
                resultDto.setMessage(MSG_WAR_303);
                return resultDto;
            }
            for (String s : awbList) {
                param.setAwbNo(s);
                OAET001ReportResultDto tmpDto ;
                tmpDto = writeReport(header, param);
                if (StringUtil.isStringNull(tmpDto.getErrorFlag()).equals(RESULT_ERROR)) {
                    message = tmpDto.getMessage();
                    throw new Exception(message);
                } else {
                    cntCdb = cntCdb + tmpDto.getCntCdb();
                    cntMec = cntMec + tmpDto.getCntMec();
                    cntEDC = cntEDC + tmpDto.getCntEDC();
                    cntEDA = cntEDA + tmpDto.getCntEDA();
                    cntFileCDB = cntFileCDB + tmpDto.getCntFileCDB();
                    cntFileMEC = cntFileMEC + tmpDto.getCntFileMEC();
                    cntFileEDC = cntFileEDC + tmpDto.getCntFileEDC();
                    cntFileEDA = cntFileEDA + tmpDto.getCntFileEDA();
                    folderList.addAll(tmpDto.getFolderList());
                    s3FolderList.addAll(tmpDto.getS3FolderList());
                }
            }
            resultDto.setCntCdb(cntCdb);
            resultDto.setCntMec(cntMec);
            resultDto.setCntEDA(cntEDA);
            resultDto.setCntEDC(cntEDC);
            resultDto.setCntFileCDB(cntFileCDB);
            resultDto.setCntFileMEC(cntFileMEC);
            resultDto.setCntFileEDA(cntFileEDA);
            resultDto.setCntFileEDC(cntFileEDC);
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setMessage(MSG_SUC_006);
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();

            // エラー結果保存
            resultDto.setErrorFlag(RESULT_ERROR);
            if(StringUtil.isStringEmpty(message)){
                resultDto.setMessage(MSG_ERR_909);
            } else {
                resultDto.setMessage(message);
            }
            deleteAllFolder(resultDto.getFolderList());
            deleteAllS3Folder(resultDto.getS3FolderList());
        }
        return resultDto;
    }

    public OAET001ReportResultDto writeReport(HttpHeaders header, OAET001SearchDto param) {
        OAET001ReportResultDto returnValue = new OAET001ReportResultDto();
        Map<String,String> cntMap = new HashMap<>();
        String msg = "";
        List<String> folderList = new ArrayList<>();
        try{
            // CDB作成
            if(param.getCdbFlg().equals(RESULT_TRUE)){
                returnValue = cdbServ.writeCDB(header,param);
                if(returnValue.getErrorFlag().equals(RESULT_ERROR)){
                    msg = MSG_ERR_910;
                    throw new Exception("CDB電文作成エラー");
                } 
            }

            // MEC作成
            if(param.getMecFlg().equals(RESULT_TRUE)){
                returnValue = mecServ.writeMEC(header,param);
                if(returnValue.getErrorFlag().equals(RESULT_ERROR)){
                    msg = MSG_ERR_902;
                    throw new Exception(msg);
                }
            }

            // EDC電文作成
            if(param.getEdcFlg().equals(RESULT_TRUE)){
                returnValue = edcServ.writeEDC(header,param);
                if(returnValue.getErrorFlag().equals(RESULT_ERROR)){
                    msg = OAET001CommonConstants.MSG_ERR_903;
                    throw new Exception(msg);
                }
            }
            // EDA電文作成
            if(param.getEdaFlg().equals(RESULT_TRUE)){
                returnValue = edaServ.writeEDA(header,param);
                if(returnValue.getErrorFlag().equals(RESULT_ERROR)){
                    msg = OAET001CommonConstants.MSG_ERR_904;
                    throw new Exception(msg);
                }
            }
            returnValue.setErrorFlag(RESULT_SUCCESS);
            returnValue.setMessage(MSG_SUC_006);
        } catch (Exception e){
            // エラーログ出力
            e.printStackTrace();
            log.error(e.getMessage());

            // リターン値設定
            returnValue.setErrorFlag(RESULT_ERROR);
            if(StringUtil.isStringEmpty(msg)){
                returnValue.setMessage(OAET001CommonConstants.MSG_ERR_905);
            } else {
                returnValue.setMessage(msg);
            }
            deleteAllFolder(returnValue.getFolderList());
            deleteAllS3Folder(returnValue.getS3FolderList());
        }
        return returnValue;
    }


    private void deleteAllFolder(List<String> folderList){
        try{
            for(String path : folderList){
                commonService.deleteFolder(path);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void deleteAllS3Folder(List<String> folderList){
        try{
            for(String path : folderList){
                reportService.deleteReportS3(path,null);
            }
        } catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

}
