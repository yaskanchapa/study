package com.kse.wmsv2.oa_it_001.controller;


import com.kse.wmsv2.common.dto.Auth;
import com.kse.wmsv2.oa_it_001.dao.*;
import com.kse.wmsv2.oa_it_001.dto.*;
import com.kse.wmsv2.oa_it_001.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/oait001")
public class OAIT001Controller {

    @Autowired
    OAIT001ScreenService screenServ;

    @Autowired
    OAIT001HAWBScanService hawbScanServ;

    @Autowired
    OAIT001FileService fileServ;

    @Autowired
    OAIT001ReportService reportServ;

    @Autowired
    OAIT001ModalService modalServ;


    @Auth(role = Auth.Role.USER)
    @GetMapping("/search")
    public OAIT001SearchResultDto gerSearchList(@RequestHeader HttpHeaders headers, OAIT001SearchDto params, HttpServletResponse response){
        OAIT001SearchResultDto returnVal = new OAIT001SearchResultDto();
        returnVal = screenServ.searchIT001(headers, params) ;
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/searchAwbNo")
    public OAIT001ReturnDto searchAwbNo(String awbNo){
        OAIT001ReturnDto returnVal =  new OAIT001ReturnDto();
        returnVal = screenServ.searchAwbNo(awbNo);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/uploadExcel")
    public OAIT001SearchResultDto uploadExcel(@RequestHeader HttpHeaders headers,@ModelAttribute OAIT001UploadDto fileParams){
        OAIT001SearchResultDto returnVal = fileServ.uploadFile(fileParams,headers);
        return returnVal;
    }


    @GetMapping("/getDefaultValueUpload")
    public Map<String,List> setUploadScreenDefaultValue(){
        Map<String,List> returnVal = screenServ.getUploadScreenDefaultValue();
        return returnVal;
    }


    @GetMapping("/getDefaultList")
    public List<OAIT001DefaultDao> getSearchDefault(OAIT001SearchDto params){
        List<OAIT001DefaultDao> returnVal = screenServ.getSearchDefaultList(params);
        return returnVal;
    }


    @GetMapping("/searchAllStatus")
    public List<OAIT001StatusDao> searchAllStatus(String cwbNo,String businessCd,String awbNo){
        return screenServ.searchAllStatus(cwbNo,businessCd,awbNo);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/selectMICDetail")
    public OAIT001MICDao selectMICDetail(OAIT001SearchDto param){
        OAIT001MICDao returnVal = new OAIT001MICDao();
        returnVal = screenServ.selectMICDetail(param);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/selectIDADetail")
    public OAIT001IDADao selectIDADetail(OAIT001SearchDto param){
        OAIT001IDADao returnVal = new OAIT001IDADao();
        returnVal = screenServ.selectIDADetail(param);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/editImportData")
    public OAIT001CommonDto editImportData(@RequestHeader HttpHeaders headers,@RequestBody List<OAIT001SearchResultDao> params){
        return screenServ.editImportData(headers,params);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/deleteImportData")
    public OAIT001CommonDto deleteImportData(@RequestHeader HttpHeaders headers,@RequestBody List<OAIT001SearchResultDao> params){
        return screenServ.deleteImportData(headers,params);
    }


    @GetMapping("/getCwbImagePath")
    public String getCwbImagePath(OAIT001ImageDto params){
        return screenServ.getCwbImagePath(params);
    }


    @PostMapping("/resettingDefVal")
    public OAIT001CommonDto resettingDefVal(@RequestHeader HttpHeaders headers,@RequestBody List<OAIT001DefaultDao> defaultRowData){
        return screenServ.resettingDefVal(headers,defaultRowData);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/editHeaderData")
    public OAIT001CommonDto editHeaderData(@RequestHeader HttpHeaders headers,@RequestBody OAIT001HeaderDao params){
        return screenServ.editHeaderData(headers,params);
    }


    @PostMapping("/selectFltList")
    public List<String> selectFltList(@RequestHeader HttpHeaders headers,@RequestBody Map<String,String> awbMap){
        return screenServ.selectFltList(headers,awbMap);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/writeReport")
    public ResponseEntity<?> writeReport(@RequestHeader HttpHeaders headers,@RequestBody OAIT001SearchDto params){
        return reportServ.writeReport(headers,params);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/insertMicData")
    public OAIT001ReturnDto insertMicData(@RequestHeader HttpHeaders headers, @RequestBody OAIT001MICDao param){
        return modalServ.insertMicData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/insertIdaData")
    public OAIT001ReturnDto insertIdaData(@RequestHeader HttpHeaders headers, @RequestBody OAIT001IDADao param){
        return modalServ.insertIdaData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/writeIDA")
    public ResponseEntity<byte[]> writeIDAReport(@RequestHeader HttpHeaders headers, @RequestBody OAIT001IDADao param){
        return reportServ.writeIDA(headers,param);
    }


    @GetMapping("/searchErrorReport")
    public List<OAIT001ErrorReportDto> searchErrorReport(String awbNo,String cwbNo,String type){
        return modalServ.searchErrorReport(awbNo,cwbNo,type);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/getPdf")
    public ResponseEntity<byte[]> getPdf(@RequestHeader HttpHeaders headers,@RequestBody Map<String,String> pdfUrl){
        return screenServ.getPdf(headers,pdfUrl);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/changeColumnValue")
    public OAIT001SearchResultDto changeColumnValue(@RequestHeader HttpHeaders headers, OAIT001UpdateCellDto boxValue){
        return screenServ.changeColumnValue(headers,boxValue);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/changeCheckBoxValue")
    public OAIT001SearchResultDto changeCheckBoxValue(@RequestHeader HttpHeaders headers, OAIT001UpdateCellDto boxValue){
        return screenServ.changeCheckBoxValue(headers,boxValue);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/saveMicData")
    public OAIT001ReturnDto saveMicData(@RequestHeader HttpHeaders headers, @RequestBody OAIT001MICDao param){
        return modalServ.saveMicData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/checkReport")
    public OAIT001ReturnDto checkReport(@RequestHeader HttpHeaders headers, @RequestBody OAIT001MICDao param){
        return modalServ.saveMicData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/saveIdaData")
    public OAIT001ReturnDto saveIdaData(@RequestHeader HttpHeaders headers, @RequestBody OAIT001IDADao param){
        return modalServ.saveIdaData(headers,param);
    }


    @GetMapping("/getPdfUrl")
    public OAIT001ImageDto getPdfUrl(OAIT001ImageDto param){
        OAIT001ImageDto returnVal = screenServ.getPdfUrl(param);
        return returnVal;
    }

    @GetMapping("/checkDeleteAll")
    public ResponseEntity<OAIT001ReturnDto> checkDeleteAll(@RequestHeader HttpHeaders header, OAIT001SearchDto params) {
        return ResponseEntity.ok(screenServ.checkDeleteAll(header, params));
    }
    
    @GetMapping("/deleteAll")
    public ResponseEntity<OAIT001ReturnDto> deleteAll(@RequestHeader HttpHeaders header, OAIT001SearchDto params) {
        return ResponseEntity.ok(screenServ.deleteAll(header, params));
    }

    @GetMapping("/hawbScan/init")
    public ResponseEntity<OAIT001HawbScanDto> initHawbScan(@RequestHeader HttpHeaders header) {
        return ResponseEntity.ok(hawbScanServ.initHawbScan(header));
    }

    @GetMapping("/hawbScan/dataList")
    public ResponseEntity<OAIT001HawbScanDto> getDataList(@RequestHeader HttpHeaders header, String confirmationItem) {
        return ResponseEntity.ok(hawbScanServ.getDataList(header, confirmationItem));
    }

    @PostMapping("/hawbScan/checkRegist")
    public ResponseEntity<OAIT001HawbScanDto> checkRegistHawbScan(@RequestHeader HttpHeaders header,@RequestBody OAIT001HawbScanDataDto oAET001HawbScanDataDto) {
        return ResponseEntity.ok(hawbScanServ.checkRegistHawbScan(header, oAET001HawbScanDataDto));
    }

    @PostMapping("/hawbScan/applyRegist")
    public ResponseEntity<OAIT001HawbScanDto> registHawbScan(@RequestHeader HttpHeaders header,@RequestBody OAIT001HawbScanDataDto oAET001HawbScanDataDto) {
        return ResponseEntity.ok(hawbScanServ.registHawbScan(header, oAET001HawbScanDataDto));
    }

    @PostMapping("/hawbScan/releaseHawb")
    public ResponseEntity<OAIT001HawbScanDto> releaseHawbScan(@RequestHeader HttpHeaders header,@RequestBody OAIT001HawbScanDataDto oAET001HawbScanDataDto) {
        return ResponseEntity.ok(hawbScanServ.releaseHawbScan(header, oAET001HawbScanDataDto));
    }
}