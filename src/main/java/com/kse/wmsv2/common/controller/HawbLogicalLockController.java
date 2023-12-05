package com.kse.wmsv2.common.controller;

import com.kse.wmsv2.common.dto.HawbLogicalLockReqDto;
import com.kse.wmsv2.common.dto.HawbLogicalLockResDto;
import com.kse.wmsv2.common.service.HawbLogicalLockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/logicalLock")
public class HawbLogicalLockController {
    @Autowired
    HawbLogicalLockService lockService;
    /**
     * 論理ロック取得
     */
    @PostMapping("/getLock")
    public HawbLogicalLockResDto getLock(@RequestBody HawbLogicalLockReqDto reqDto) {
        HawbLogicalLockResDto resDto = null;
        try {
            resDto = lockService.getLogicalLock(reqDto);
        } catch(Exception e) {
            String errMsg = "ロック取得処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage() + reqDto.toString());

            resDto = new HawbLogicalLockResDto();
            resDto.setResult(false);
            resDto.setErrMessage(errMsg);
        } finally {
            return resDto;
        }
    }

    /**
     * 論理ロック解除
     */
    @PostMapping("/releaseLock")
    public HawbLogicalLockResDto releaseLock(@RequestBody HawbLogicalLockReqDto reqDto) {
        HawbLogicalLockResDto resDto = null;
        try {
            resDto = lockService.releaseLock(reqDto);
        } catch(Exception e) {
            String errMsg = "ロック解除処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage() + reqDto.toString());

            resDto = new HawbLogicalLockResDto();
            resDto.setResult(false);
            resDto.setErrMessage(errMsg);
        } finally {
            return resDto;
        }
    }

    /**
     * ユーザコードと紐づく論理ロック全解除
     */
    @PostMapping("/releaseAllLockByUserCd")
    public void releaseAllLockByUserCd(@RequestBody HawbLogicalLockReqDto reqDto) {
        try {
            lockService.releaseAllLockByUserCd(reqDto);
            log.error("ユーザコード：" + reqDto.getUserCd()+"の全ロックが解除されました。");
        } catch(Exception e) {
            String errMsg = "ロック全解除処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage() + reqDto.toString());
        }
    }
}
