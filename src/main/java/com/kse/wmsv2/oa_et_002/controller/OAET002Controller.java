package com.kse.wmsv2.oa_et_002.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_et_002.dto.request.AEStatusHistoryRequest;
import com.kse.wmsv2.oa_et_002.dto.request.AirExportRequest;
import com.kse.wmsv2.oa_et_002.dto.response.AEStatusHistoryResponse;
import com.kse.wmsv2.oa_et_002.dto.response.AirExportResponse;
import com.kse.wmsv2.oa_et_002.dto.response.MesssageResponse;
import com.kse.wmsv2.oa_et_002.service.AirExportService;

@RestController
@RequestMapping("/api/oaet002")
public class OAET002Controller {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AirExportService airExportService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/init")
    public ResponseEntity<AirExportResponse> init(@RequestHeader HttpHeaders header) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");


        return ResponseEntity.ok(this.airExportService.init(userCd, authorityCd, departmentCd));
    }

    @GetMapping("/list")
    public ResponseEntity<AirExportResponse> getDataAE(@RequestHeader HttpHeaders header, AirExportRequest airExportRequest) {

        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        return ResponseEntity.ok(this.airExportService.getDataAE(airExportRequest, userCd, authorityCd, departmentCd));
    }

    @GetMapping("/csv/data-ae-download")
    public ResponseEntity<Resource> csvDataAi(@RequestHeader HttpHeaders header, AirExportRequest airExportRequest) {

        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String authorityCd = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filename = "輸出申告データ照会_" + LocalDateTime.now().format(formatter) +  ".csv";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (Exception ex) {
            logger.error("UTF-8のエンコードが失敗しました。");
            throw new BadRequestException( new Error("エラー", "UTF-8のエンコードが失敗しました。"));
        }
        InputStreamResource file = new InputStreamResource(this.airExportService.csvDataAe(airExportRequest, userCd, authorityCd, departmentCd));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(file);
    }
    
    @GetMapping("/status-history/list")
    public ResponseEntity<AEStatusHistoryResponse> getDataAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest) {
        return ResponseEntity.ok(this.airExportService.getDataAEStatusHistory(aEStatusHistoryRequest));
    }

    @DeleteMapping("/status-history/delete")
    public ResponseEntity<MesssageResponse> deleteAeStatusHistory(@RequestHeader HttpHeaders header, @RequestBody AEStatusHistoryRequest aEStatusHistoryRequest) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.airExportService.deleteAeStatusHistory(aEStatusHistoryRequest, userCd));
    }

    @PutMapping("/status-history/update")
    public ResponseEntity<MesssageResponse> updateAeStatusHistory(@RequestHeader HttpHeaders header, @RequestBody AEStatusHistoryRequest aEStatusHistoryRequest) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.airExportService.updateAeStatusHistory(aEStatusHistoryRequest, userCd));
    }

    @PostMapping("/status-history/regist")
    public ResponseEntity<MesssageResponse> registAeStatusHistory(@RequestHeader HttpHeaders header, @RequestBody AEStatusHistoryRequest aEStatusHistoryRequest) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.airExportService.registAeStatusHistory(aEStatusHistoryRequest, userCd));
    }
}
