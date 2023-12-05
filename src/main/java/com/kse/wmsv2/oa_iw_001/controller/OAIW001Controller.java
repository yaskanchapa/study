package com.kse.wmsv2.oa_iw_001.controller;
import com.kse.wmsv2.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.kse.wmsv2.oa_iw_001.dao.*;
import com.kse.wmsv2.oa_iw_001.dto.*;
import com.kse.wmsv2.oa_iw_001.service.OAIW001ScreenService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/oaiw001")
public class OAIW001Controller {
    @Autowired
    OAIW001ScreenService screenServ;

    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/search")
    public List<OAIW001SelectBondedInListDto> SelectBondedInList(@RequestBody OAIW001SelectBondedInListDao param) {
        List<OAIW001SelectBondedInListDto> result = screenServ.SelectBondedInList(param);
        return result;
    }

    @PostMapping("/regist")
    public OAIW0001ResultDto regist(@RequestHeader HttpHeaders headers,
                                    @RequestBody List<OAIW001SelectAiDataCnt1Dao> params) {
        OAIW0001ResultDto resultDto = new OAIW0001ResultDto();
        int totalAiDataCnt = 0;
        try {
            totalAiDataCnt = screenServ.registBusiness(headers, params);
            if (totalAiDataCnt > 0) {
                resultDto.setResult(true);
                resultDto.setMessage("搬入データ登録処理が正常終了しました。");
                resultDto.setErrorMessage("");
            } else {
                resultDto.setResult(true);
                resultDto.setMessage("登録対象の搬入データがありません。");
                resultDto.setErrorMessage("");
            }
        } catch(Exception e) {
            String errMsg = "搬入データ登録処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
        } finally {
            return resultDto;
        }
    }

    @PostMapping("/bondedInSetTarget")
    public OAIW0001ResultDto bondedInSetTarget(@RequestHeader HttpHeaders headers,
                                               @RequestBody OAIW001SelectAiDataDao param) {
        OAIW0001ResultDto resultDto = null;
        try {
            resultDto = screenServ.bondedInSetTargetBusiness(headers, param);
        } catch(Exception e) {
            resultDto = new OAIW0001ResultDto();
            String errMsg = "バッチ対象処理にエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
        } finally {
            return resultDto;
        }
    }

    @PostMapping("/bondedInUnsetTarget")
    public OAIW0001ResultDto bondedInUnSetTarget(@RequestHeader HttpHeaders headers,
                                                 @RequestBody OAIW001SelectAiCargoInData1Dao param) {
        OAIW0001ResultDto resultDto = null;
        try {
            resultDto = screenServ.bondedInUnSetTargetBusiness(headers, param);
        } catch(Exception e) {
            String errMsg = "バッチ対象外処理にエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto = new OAIW0001ResultDto();
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
        } finally {
            return resultDto;
        }
    }
}
