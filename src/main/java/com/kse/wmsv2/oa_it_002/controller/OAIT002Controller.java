package com.kse.wmsv2.oa_it_002.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_it_002.dto.request.AiStatusHistoryRequest;
import com.kse.wmsv2.oa_it_002.dto.request.AirImportRequest;
import com.kse.wmsv2.oa_it_002.dto.request.AmCustomerNumberRequest;
import com.kse.wmsv2.oa_it_002.dto.request.ArrangementDetailRequest;
import com.kse.wmsv2.oa_it_002.dto.request.CsOptionalServiceFormRequest;
import com.kse.wmsv2.oa_it_002.dto.request.HawbNoCheckRequest;
import com.kse.wmsv2.oa_it_002.dto.response.AiStatusHistoryResponse;
import com.kse.wmsv2.oa_it_002.dto.response.AirImportResponse;
import com.kse.wmsv2.oa_it_002.dto.response.AmCustomerNumberResponse;
import com.kse.wmsv2.oa_it_002.dto.response.ArrangementDetailResponse;
import com.kse.wmsv2.oa_it_002.dto.response.MesssageResponse;
import com.kse.wmsv2.oa_it_002.dto.response.HawbNoCheckResponse;
import com.kse.wmsv2.oa_it_002.service.AirImportService;

@RestController
@RequestMapping("/api/oait002")
public class OAIT002Controller {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AirImportService airImportService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/init")
    public ResponseEntity<AirImportResponse> init(@RequestHeader HttpHeaders header) {

        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String userCompanyCd = redisUtil.loadRedis(accessToken, "USERCOMPANYCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        return ResponseEntity.ok(this.airImportService.init(userCd, authorityCd, userCompanyCd, departmentCd));
    }

    @GetMapping("/get-name-am-customer-number")
    public ResponseEntity<AmCustomerNumberResponse> getNameAmCustomerNumber(@Valid AmCustomerNumberRequest amCustomerNumberRequest) {
        return ResponseEntity.ok(this.airImportService.getNameAmCustomerNumber(amCustomerNumberRequest));
    }

    @GetMapping("/list")
    public ResponseEntity<AirImportResponse> getDataAI(@RequestHeader HttpHeaders header, AirImportRequest airImportRequest) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        return ResponseEntity.ok(this.airImportService.getDataAI(airImportRequest, userCd, authorityCd, departmentCd));
    }

    @GetMapping("/csv/after-permission-download")
    public ResponseEntity<Resource> csvAfterPermission(AirImportRequest airImportRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filename = "許可済CSV_" + LocalDateTime.now().format(formatter) +  ".csv";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (Exception ex) {
            
        }
        InputStreamResource file = new InputStreamResource(this.airImportService.csvAfterPermission(airImportRequest));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(file);
    }

    @GetMapping("/csv/data-ai-download")
    public ResponseEntity<Resource> csvDataAi(AirImportRequest airImportRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filename = "輸入申告データ照会_" + LocalDateTime.now().format(formatter) +  ".csv";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (Exception ex) {
            
        }
        InputStreamResource file = new InputStreamResource(this.airImportService.csvDataAi(airImportRequest));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(file);
    }
    
    @GetMapping("/arrangement-detail/init")
    public ResponseEntity<ArrangementDetailResponse> arrangementDetailInit(ArrangementDetailRequest arrangementDetailRequest) {
        return ResponseEntity.ok(this.airImportService.arrangementDetailInit(arrangementDetailRequest));
    }
    
    @PutMapping("/cs-optional-service/update")
    public ResponseEntity<MesssageResponse> updateCsOptionalService(@RequestHeader HttpHeaders header, @RequestBody @Valid CsOptionalServiceFormRequest csOptionalServiceFormRequest) {

        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.airImportService.updateCsOptionalService(csOptionalServiceFormRequest, userCd));
    }

    @GetMapping("/hawb-no/check")
    public ResponseEntity<HawbNoCheckResponse> checkHawbNo(HawbNoCheckRequest hawbNoCheckRequest) {
        return ResponseEntity.ok(this.airImportService.checkHawbNo(hawbNoCheckRequest));
    }

    @PutMapping("/hawb-no/update")
    public ResponseEntity<MesssageResponse> updateHawbNo(@RequestBody HawbNoCheckRequest hawbNoCheckRequest) {
        return ResponseEntity.ok(this.airImportService.updateHawbNo(hawbNoCheckRequest));
    }

    @GetMapping("/history/list")
    public ResponseEntity<AiStatusHistoryResponse> getDataAiStatusHistory(AiStatusHistoryRequest aiStatusHistoryRequest) {
        return ResponseEntity.ok(this.airImportService.getDataAiStatusHistory(aiStatusHistoryRequest));
    }

    
}
