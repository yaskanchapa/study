package com.kse.wmsv2.oc_cm_001.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.validation.Valid;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oc_cm_001.dto.CatererSenderMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.ImageTraderNoMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.TraderNoMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.UserMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.CatererSenderMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.RateMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.TraderNoMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.UserMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.response.CatererSenderMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.ImageTraderNoMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.MesssageResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.RateMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.TraderNoMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.UserMasterResponse;
import com.kse.wmsv2.oc_cm_001.service.OCCM001Service;

@RestController
@RequestMapping("/api/occm001")
public class OCCM001Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private OCCM001Service oCCM001Service;

    @GetMapping("/user/info/{userCd}")
    public ResponseEntity<UserMasterDto> getCmUserInfo(@PathVariable String userCd) {
        return ResponseEntity.ok(this.oCCM001Service.getCmUserInfo(userCd));
    }

    @GetMapping("/user/init")
    public ResponseEntity<UserMasterResponse> initUserMaster(@RequestHeader HttpHeaders header) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        return ResponseEntity.ok(this.oCCM001Service.initUserMaster(departmentCd));
    }

    @GetMapping("/user/list")
    public ResponseEntity<UserMasterResponse> getDataCmUser(UserMasterRequest userMasterRequest) {
        return ResponseEntity.ok(this.oCCM001Service.getDataCmUser(userMasterRequest));
    }

    @GetMapping("/user/form/init")
    public ResponseEntity<UserMasterResponse> initFormUserMaster(@RequestHeader HttpHeaders header) {
        String accessToken = (String)header.get("authorization").get(0);

        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        return ResponseEntity.ok(this.oCCM001Service.initFormUserMaster(departmentCd));
    }

    @PostMapping("/user/regist")
    public ResponseEntity<MesssageResponse> registUserMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid UserMasterDto userMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.registUserMaster(userMasterDto, userCd));
    }

    @PutMapping("/user/update")
    public ResponseEntity<MesssageResponse> updateUserMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid UserMasterDto userMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.updateUserMaster(userMasterDto, userCd));
    }

    @DeleteMapping("/user/delete/{userCd}")
    public ResponseEntity<Integer> deleteCmUserByUserCd(@PathVariable String userCd) {
        return ResponseEntity.ok(this.oCCM001Service.deleteCmUserByUserCd(userCd));
    }

    @GetMapping("/user/csv")
    public ResponseEntity<Resource> csvCmUser(UserMasterRequest userMasterRequest) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filename = "ユーザー検索結果_" + LocalDateTime.now().format(formatter) +  ".csv";
        try {
            filename = URLEncoder.encode(filename,"UTF-8");
        } catch (Exception ex) {
            
        }
        InputStreamResource file = new InputStreamResource(this.oCCM001Service.csvCmUser(userMasterRequest));

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(file);
    }

    @GetMapping("/trader/init")
    public ResponseEntity<TraderNoMasterResponse> initTraderNoMaster() {
        return ResponseEntity.ok(this.oCCM001Service.initTraderNoMaster());
    }

    @GetMapping("/trader/list")
    public ResponseEntity<TraderNoMasterResponse> getDataAmCustomerNumber(TraderNoMasterRequest traderNoMasterRequest) {
        return ResponseEntity.ok(this.oCCM001Service.getDataAmCustomerNumber(traderNoMasterRequest));
    }

    @GetMapping("/trader/form/init")
    public ResponseEntity<TraderNoMasterResponse> initFormTraderNoMaster() {
        return ResponseEntity.ok(this.oCCM001Service.initFormTraderNoMaster());
    }

    @PostMapping("/trader/regist")
    public ResponseEntity<MesssageResponse> registTraderNoMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid TraderNoMasterDto traderNoMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.registTraderNoMaster(traderNoMasterDto, userCd));
    }

    @GetMapping("/trader/detail")
    public ResponseEntity<TraderNoMasterDto> detailTraderNoMaster(TraderNoMasterDto traderNoMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.detailTraderNoMaster(traderNoMasterDto));
    }

    @PutMapping("/trader/update")
    public ResponseEntity<MesssageResponse> updateTraderNoMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid TraderNoMasterDto traderNoMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.updateTraderNoMaster(traderNoMasterDto, userCd));
    }

    @GetMapping("/trader/image/list")
    public ResponseEntity<ImageTraderNoMasterResponse> getListImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.getListImageTraderNoMaster(imageTraderNoMasterDto));
    }

    @PostMapping("/trader/image/upload")
    public ResponseEntity<MesssageResponse> uploadImageTraderNoMaster(@RequestHeader HttpHeaders header,@RequestParam("customerNo") String customerNo,
                                            @RequestParam("fileName") String fileName,
                                            @RequestParam("file") MultipartFile file) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }
        if(fileName == null || file == null) 
        {
            logger.error("イメージファイルを選択してください。");
            throw new BadRequestException( new Error("KSEMSG000001", "イメージファイルを選択してください。"));
        }
        if(file.getSize() > 10000000 ) {
            logger.error("10Mb以下のファイルを選択して下さい。");
            throw new BadRequestException( new Error("エラー", "10Mb以下のファイルを選択して下さい。"));
        }
        return ResponseEntity.ok(this.oCCM001Service.uploadImageTraderNoMaster(customerNo, fileName, file, userCd));
    }

    @GetMapping("/trader/image/view")
    public ResponseEntity<byte[]> viewImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto) throws IOException {
        byte[] pdf = this.oCCM001Service.viewImageTraderNoMaster(imageTraderNoMasterDto);
        HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/pdf"));
            headers.add("content-disposition", "inline;filename=" + imageTraderNoMasterDto.getImageName()); 
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdf, headers, HttpStatus.OK);
            return response;

    }

    @DeleteMapping("/trader/image/delete")
    public ResponseEntity<MesssageResponse> deleteImageTraderNoMaster(@RequestBody ImageTraderNoMasterDto imageTraderNoMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.deleteImageTraderNoMaster(imageTraderNoMasterDto));
    }

    @DeleteMapping("/trader/delete")
    public ResponseEntity<MesssageResponse> deleteTraderNoMaster(@RequestBody TraderNoMasterDto traderNoMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.deleteTraderNoMaster(traderNoMasterDto));
    }

    @GetMapping("/rate/list")
    public ResponseEntity<RateMasterResponse> getDataRate(RateMasterRequest rateMasterRequest) {
        return ResponseEntity.ok(this.oCCM001Service.getDataRate(rateMasterRequest));
    }

    @PutMapping("/rate/update")
    public ResponseEntity<MesssageResponse> updateRateMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid RateMasterRequest rateMasterRequest) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.updateRateMaster(rateMasterRequest, userCd));
    }

    @DeleteMapping("/rate/delete")
    public ResponseEntity<MesssageResponse> deleteRateMaster(@RequestBody RateMasterRequest rateMasterRequest) {
        return ResponseEntity.ok(this.oCCM001Service.deleteRateMaster(rateMasterRequest));
    }
    
    @GetMapping("/caterer/init")
    public ResponseEntity<CatererSenderMasterResponse> initCatererSenderMaster() {
        return ResponseEntity.ok(this.oCCM001Service.initCatererSenderMaster());
    }

    @GetMapping("/caterer/list")
    public ResponseEntity<CatererSenderMasterResponse> getDataAmConsignerCosigneeNumber(CatererSenderMasterRequest catererSenderMasterRequest) {
        return ResponseEntity.ok(this.oCCM001Service.getDataAmConsignerCosigneeNumber(catererSenderMasterRequest));
    }

    @PostMapping("/caterer/regist")
    public ResponseEntity<MesssageResponse> registCatererSenderMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid CatererSenderMasterDto catererSenderMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.registCatererSenderMaster(catererSenderMasterDto, userCd));
    }

    @GetMapping("/caterer/detail")
    public ResponseEntity<CatererSenderMasterDto> detailCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.detailCatererSenderMaster(catererSenderMasterDto));
    }

    @DeleteMapping("/caterer/delete")
    public ResponseEntity<MesssageResponse> deleteCatererSenderMaster(@RequestBody CatererSenderMasterDto catererSenderMasterDto) {
        return ResponseEntity.ok(this.oCCM001Service.deleteCatererSenderMaster(catererSenderMasterDto));
    }

    @PutMapping("/caterer/update")
    public ResponseEntity<MesssageResponse> updateCatererSenderMaster(@RequestHeader HttpHeaders header, @RequestBody @Valid CatererSenderMasterDto catererSenderMasterDto) {
        String accessToken = (String)header.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        if(userCd == null) 
        {
            logger.error("ユーザーIDは必須です。");
            throw new BadRequestException( new Error("KSEMSG000001", "ユーザーIDは必須です。"));
        }

        return ResponseEntity.ok(this.oCCM001Service.updateCatererSenderMaster(catererSenderMasterDto, userCd));
    }
}
