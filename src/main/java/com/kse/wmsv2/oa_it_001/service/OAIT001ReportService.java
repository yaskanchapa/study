package com.kse.wmsv2.oa_it_001.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDADao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReportResultDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReportReturnDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAIT001ReportService extends OAIT001CommonConstants{

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    OAIT001IDAService idaService;

    @Autowired
    OAIT001MICService micService;

    @Autowired
    OAIT001CHAService chaService;

    @Autowired
    OAIT001HCHService hchService;

    @Autowired
    OAIT001HDMService hdmService;

    @Autowired
    OAIT001IDCService idcService;


    public ResponseEntity<?> writeReport(HttpHeaders headers, OAIT001SearchDto params) {
        Map<String,String> returnVal = new HashMap<>();
        HttpHeaders returnHeaders = new HttpHeaders();
        OAIT001ReportReturnDto resultVal = new OAIT001ReportReturnDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();
        byte[] zipFile = new byte[0];
        String msg = null;

        try{
            //パラメータチェック
            if(StringUtil.isStringEmpty(params.getAwbNo())){
                msg = MSG_ERR_204;
                throw new Exception(MSG_ERR_204);
            }else if(StringUtil.isStringEmpty(params.getCurrentArrFlt1())){
                msg = MSG_ERR_205;
                throw new Exception(MSG_ERR_205);
            }

            // 1.HCH作成
            if(params.getHchFlg().equals("true")){
                OAIT001ReportResultDto tmp = new OAIT001ReportResultDto();
                tmp = hchService.writeHCH(headers,params);
                if(tmp.getErrorFlag().equals(RESULT_ERROR)){
                    throw new Exception(tmp.getMessage());
                }
                folderList.addAll(tmp.getFolderList());
                s3FolderList.addAll(tmp.getS3FolderList());
                resultVal.setCntHCH(String.valueOf(tmp.getCntFolder()));
                resultVal.setCntFileHCH(String.valueOf(tmp.getCntFile()));
            } else {
                resultVal.setCntHCH("0");
                resultVal.setCntFileHCH("0");
            }

            // 2.HDM作成
            if(params.getHdmFlg().equals("true")){
                OAIT001ReportResultDto tmp = new OAIT001ReportResultDto();
                tmp  = hdmService.writeHDM(headers,params);
                if(tmp.getErrorFlag().equals(RESULT_ERROR)){
                    throw new Exception(tmp.getMessage());
                }
                folderList.addAll(tmp.getFolderList());
                s3FolderList.addAll(tmp.getS3FolderList());
                resultVal.setCntHDM(String.valueOf(tmp.getCntFolder()));
                resultVal.setCntFileHDM(String.valueOf(tmp.getCntFile()));
            } else {
                resultVal.setCntHDM("0");
                resultVal.setCntFileHDM("0");
            }

            // 3.CHA作成
            if(params.getChaFlg().equals("true")){
                OAIT001ReportResultDto tmp = new OAIT001ReportResultDto();
                tmp = chaService.writeCHA(headers,params);
                if(tmp.getErrorFlag().equals(RESULT_ERROR)){
                    throw new Exception(tmp.getMessage());
                }
                folderList.addAll(tmp.getFolderList());
                s3FolderList.addAll(tmp.getS3FolderList());
                resultVal.setCntCHA(String.valueOf(tmp.getCntFolder()));
                resultVal.setCntFileCHA(String.valueOf(tmp.getCntFile()));
            } else {
                resultVal.setCntCHA("0");
                resultVal.setCntFileCHA("0");
            }

            // 4.MIC作成
            if(params.getMicFlg().equals("true")){
                OAIT001ReportResultDto tmp = new OAIT001ReportResultDto();
                tmp = micService.writeMIC(headers,params);
                if(tmp.getErrorFlag().equals(RESULT_ERROR)){
                    throw new Exception(tmp.getMessage());
                }
                folderList.addAll(tmp.getFolderList());
                s3FolderList.addAll(tmp.getS3FolderList());
                resultVal.setCntMIC(String.valueOf(tmp.getCntFolder()));
                resultVal.setCntFileMIC(String.valueOf(tmp.getCntFile()));
            } else {
                resultVal.setCntMIC("0");
                resultVal.setCntFileMIC("0");
            }

            // 5.IDC作成
//            if(params.getIdcFlg().equals("true")){
//                Map<String,String> tmpMap = new HashMap<>();
//                tmpMap = idcService.writeIdc(headers,params);
//                if(!tmpMap.get("errorFlag").equals("false")){
//                    throw new Exception(tmpMap.get("message"));
//                }
//                resultVal.setCntIDC(tmpMap.get("cntIDC"));
//                resultVal.setCntFileIDC(tmpMap.get("cntFileIDC"));
//            } else {
//                resultVal.setCntIDC("0");
//                resultVal.setCntFileIDC("0");
//            }

            // 今は未使用
//            // 5.IDA作成
//            if(params.getIdaFlg().equals("true")){
//                Map<String, Object> idaResult = new HashMap<>();
//                idaResult = idaService.createIDAReport(headers,params);
//                zipFile = (byte[]) idaResult.get("zipFile");
//                returnHeaders = (HttpHeaders) idaResult.get("headers");
//            } else {
//                returnVal.put("cntIDA","0");
//                returnVal.put("cntFileIDA","0");
//            }

            resultVal.setErrorFlg("false");
            resultVal.setMsg(OAIT001CommonConstants.MSG_SUC_005);
        } catch(Exception e){
            // エラー結果保存
            resultVal.setErrorFlg("error");
            if(StringUtil.isStringEmpty(msg)){
                resultVal.setMsg(OAIT001CommonConstants.MSG_ERR_003);
            } else {
                resultVal.setMsg(msg);
            }


            // ログ
            log.error(e.getMessage());
            e.printStackTrace();

            // すべてのファイル削除
            deleteAllFile(folderList);
            deleteAllS3Folder(s3FolderList);
        } finally {
            try{
                returnHeaders.set("custom-header", new ObjectMapper().writeValueAsString(resultVal));
            } catch (Exception e){
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(zipFile, returnHeaders, HttpStatus.OK);
    }


    public ResponseEntity<byte[]> writeIDA(HttpHeaders headers, OAIT001IDADao param){
        Map<String,Object> returnMap = new HashMap<>();
        try{
            returnMap = idaService.createIDAReport(headers,param);
            byte[] zipFile = (byte[]) returnMap.get("zipFile");
            HttpHeaders returnHeaders = (HttpHeaders) returnMap.get("headers");
            return new ResponseEntity<>(zipFile, returnHeaders, HttpStatus.OK);
        } catch (Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    public void deleteAllFile(List<String> folderList){
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
