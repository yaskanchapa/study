package com.kse.wmsv2.oa_et_001.controller;


import com.kse.wmsv2.common.dto.Auth;
import com.kse.wmsv2.oa_et_001.dao.*;
import com.kse.wmsv2.oa_et_001.dto.*;
import com.kse.wmsv2.oa_et_001.service.OAET001FileService;
import com.kse.wmsv2.oa_et_001.service.OAET001ModalService;
import com.kse.wmsv2.oa_et_001.service.OAET001ReportService;
import com.kse.wmsv2.oa_et_001.service.OAET001ScreenService;
import com.kse.wmsv2.oa_it_001.dao.*;
import com.kse.wmsv2.oa_it_001.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/oaet001")
public class OAET001Controller {


    @Autowired
    OAET001ScreenService screenServ;

    @Autowired
    OAET001FileService fileServ;

    @Autowired
    OAET001ModalService modalServ;

    @Autowired
    OAET001ReportService reportServ;

    /**
     * OAET001Controller
     * gerSearchList
     * parameter : HttpHeaders,OAET001SearchDto
     * returnType : OAET001SearchResultDto
    *
    **/
    @Auth(role = Auth.Role.USER)
    @GetMapping("/search")
    public OAET001ReturnDto getSearchList(@RequestHeader HttpHeaders headers, OAET001SearchDto params){
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        returnVal = screenServ.search(headers,params);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/getAwbNoList")
    public List getAwbNoList(@RequestHeader HttpHeaders headers, OAET001SearchDto params){
        return screenServ.getAwbNoList(headers,params);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/searchAllStatus")
    public List<OAIT001StatusDao> searchAllStatus(String cwbNo,String awbNo){
        List<OAIT001StatusDao> statusList = new ArrayList<>();
        statusList = screenServ.searchAllStatus(cwbNo,awbNo);
        return statusList;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/selectMECDetail")
    public OAET001MECDao selectMECDetail(@RequestHeader HttpHeaders headers, OAET001SearchDto param){
        OAET001MECDao returnVal = new OAET001MECDao();
        returnVal = screenServ.selectMECDetail(headers,param);
        return returnVal;
    }

    @Auth(role = Auth.Role.USER)
    @GetMapping("/selectEDADetail")
    public OAET001EDADto selectEDADetail(@RequestHeader HttpHeaders headers, OAIT001SearchDto param){
        OAET001EDADto returnVal = new OAET001EDADto();
        OAET001EDAHeaderDao headerVal = new OAET001EDAHeaderDao();
        List<OAET001EDADetailDao> detailList = new ArrayList<>();

        // ヘッダ取得
        headerVal = screenServ.selectEDAHeader(headers,param);
        returnVal.setHeaderData(headerVal);
        //明細取得
        detailList = screenServ.selectEDADetail(headers,param);
        returnVal.setDetailDataList(detailList);

        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/editExportData")
    public OAET001ReturnDto editExportData(@RequestHeader HttpHeaders headers,@RequestBody List<OAET001SearchResultDao> params){
        OAET001ReturnDto resultVal = new OAET001ReturnDto();
        resultVal = screenServ.editExportData(headers,params);
        return resultVal;
    }

    @Auth(role = Auth.Role.USER)
    @PostMapping("/deleteExportData")
    public OAET001ReturnDto deleteExportData(@RequestHeader HttpHeaders headers,@RequestBody List<OAET001SearchResultDao> params){
        OAET001ReturnDto resultVal = new OAET001ReturnDto();
        resultVal = screenServ.deleteExportData(headers,params);
        return resultVal;
    }

    @GetMapping("/getDefaultValueUpload")
    public Map<String,List> setUploadScreenDefaultValue(){
        Map<String,List> returnVal = new HashMap<>();
        returnVal = screenServ.getUploadScreenDefaultValue();
        return returnVal;
    }

    @Auth(role = Auth.Role.USER)
    @GetMapping("/getDefaultList")
    public List<OAET001DefaultDao> getSearchDefault(OAET001SearchDto params){
        List<OAET001DefaultDao> returnVal = new ArrayList<>();
        returnVal = screenServ.getSearchDefaultList(params);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/searchAwbNo")
    public OAET001ReturnDto searchAwbNo(OAET001SearchDto params){
        OAET001ReturnDto returnVal =  new OAET001ReturnDto();
        returnVal = screenServ.searchAwbNo(params);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/getOtherSearchInfo")
    public OAET001ReturnDto getOtherSearchInfo(OAET001SearchDto params){
        OAET001ReturnDto returnVal =  new OAET001ReturnDto();
        returnVal = screenServ.getOtherSearchInfo(params);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/uploadExcel")
    public OAET001ReturnDto uploadExcel(@RequestHeader HttpHeaders headers,@ModelAttribute OAET001UploadDto fileParams){
        OAET001ReturnDto returnVal = new OAET001ReturnDto();
        returnVal = fileServ.uploadFile(fileParams,headers);
        return returnVal;
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/insertMecData")
    public OAET001ReturnDto insertMecData(@RequestHeader HttpHeaders headers, @RequestBody OAET001MECDao param){
        return modalServ.insertMecData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/checkMecData")
    public OAET001ReturnDto checkMecData(OAET001MECDao param){
        return modalServ.checkMecData(param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/checkEdaData")
    public OAET001ReturnDto checkEdaData(@RequestHeader HttpHeaders headers, @RequestBody OAET001EDADto param){
        return modalServ.checkEdaData(param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/insertEdaData")
    public OAET001ReturnDto insertEdaData(@RequestHeader HttpHeaders headers, @RequestBody OAET001EDADto param){
        return modalServ.insertEdaData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/writeReport")
    public OAET001ReportResultDto writeReport(@RequestHeader HttpHeaders headers, @RequestBody OAET001SearchDto param){
        return reportServ.checkCondition(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/editHeaderData")
    public OAET001ReturnDto editHeaderData(@RequestHeader HttpHeaders headers,@RequestBody OAET001SearchResultDao params){
        return screenServ.editHeaderData(headers,params);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/searchHeaderDefList")
    public Map<String,List> searchHeaderDefList(@RequestHeader HttpHeaders headers){
        return screenServ.searchHeaderDefList(headers);
    }


    @GetMapping("/searchErrorReport")
    public List<OAET001ErrorReportDto> searchErrorReport(String awbNo,String cwbNo,String type){
        return modalServ.searchErrorReport(awbNo,cwbNo,type);
    }


    @Auth(role = Auth.Role.USER)
    @GetMapping("/changeColumnValue")
    public OAET001ReturnDto changeColumnValue(@RequestHeader HttpHeaders headers,OAET001UpdateCheckBoxDto boxValue){
        return screenServ.changeColumnValue(headers,boxValue);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/saveMecData")
    public OAET001ReturnDto saveMecData(@RequestHeader HttpHeaders headers, @RequestBody OAET001MECDao param){
        return modalServ.saveMecData(headers,param);
    }


    @Auth(role = Auth.Role.USER)
    @PostMapping("/saveEdaData")
    public OAET001ReturnDto saveEdaData(@RequestHeader HttpHeaders headers, @RequestBody OAET001EDADto param){
        return modalServ.saveEdaData(headers,param);
    }

}