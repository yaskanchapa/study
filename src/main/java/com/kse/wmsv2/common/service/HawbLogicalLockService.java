package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.dto.HawbLogicalLockReqDto;
import com.kse.wmsv2.common.dto.HawbLogicalLockResDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Slf4j
@Service
@Transactional
public class HawbLogicalLockService {
    private final String PREFIX = "HawbLogicalLock";
    private final String USER_CD = "USER_CD";
    private final String LOCK_DATE_TIME = "LOCK_DATE_TIME";

    /** Redis通信に使用するRedisTemplate */
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 論理ロック取得
     */
    public HawbLogicalLockResDto getLogicalLock(HawbLogicalLockReqDto reqDto) {
        HawbLogicalLockResDto resDto = new HawbLogicalLockResDto();
        String key = this.buildKey(reqDto.getAwbNo(), reqDto.getCwbNo());

        boolean isExist = redisTemplate.hasKey(key);
        boolean isOverride = reqDto.isOverride();
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        if (!isExist || isOverride) { // ロックが存在しない、又はオーバーライドの場合は論理ロックを作成する。
            final String lockDateTime = getTimeStampNow();
            ops.put(key, USER_CD, reqDto.getUserCd());
            ops.put(key, LOCK_DATE_TIME, lockDateTime);
            resDto.setResult(true);
            resDto.setAwbNo(reqDto.getAwbNo());
            resDto.setCwbNo(reqDto.getCwbNo());
            resDto.setUserCd(reqDto.getUserCd());
            resDto.setLockDateTime(lockDateTime);
        } else {
            String userCd = ops.get(key, USER_CD);
            String lockDateTime = ops.get(key, LOCK_DATE_TIME);
            resDto.setResult(false);
            resDto.setErrMessage("既にロックされています。");
            resDto.setAwbNo(reqDto.getAwbNo());
            resDto.setCwbNo(reqDto.getCwbNo());
            resDto.setUserCd(userCd);
            resDto.setLockDateTime(lockDateTime);
        }
        return resDto;
    }

    /**
     * 論理ロック解除
     */
    public HawbLogicalLockResDto releaseLock(HawbLogicalLockReqDto reqDto) {
        HawbLogicalLockResDto resDto = new HawbLogicalLockResDto();
        String key = this.buildKey(reqDto.getAwbNo(), reqDto.getCwbNo());

        boolean isExist = redisTemplate.hasKey(key);
        boolean result = false;
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        if(isExist) {
            String _userCd = ops.get(key, USER_CD);
            //ロックしたユーザと解除するユーザが同一の場合のみ削除する。
            if(_userCd.equals(reqDto.getUserCd())) {
                result = redisTemplate.delete(key);

                // ロック解除失敗字
                if(!result) {
                    resDto.setErrMessage("ロック解除に失敗しました。");
                }
            } else {
                result = false;
                resDto.setErrMessage("ロックを取得したユーザのみ解除が可能です。");
            }
        } else {
            result = false;
            resDto.setErrMessage("ロックデータが存在しません。");
        }

        //戻り値セット
        resDto.setResult(result);
        resDto.setAwbNo(reqDto.getAwbNo());
        resDto.setCwbNo(reqDto.getCwbNo());
        resDto.setUserCd(reqDto.getUserCd());
        return resDto;
    }

    /**
     * ユーザの論理ロック全解除
     */
    public void releaseAllLockByUserCd(HawbLogicalLockReqDto reqDto) {
        String targetUserCd = reqDto.getUserCd();
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Set<String> hKeySet = redisTemplate.keys(PREFIX+"*");
        int delCnt = 0;
        for(String hKey : hKeySet) {
            String userCd = ops.get(hKey, USER_CD);
            // 指定ユーザコードのKeyを削除する。
            if(userCd != null && targetUserCd.equals(userCd)) {
                boolean ret = redisTemplate.delete(hKey);
                if(ret) {
                    delCnt++;
                }
            }
        }
        log.info("releaseAllLockByUserCd delCnt:" + delCnt + " userCd:" + reqDto.getUserCd());
    }

    /**
     * Redisキー作成(HawbLogicalLock:MAWBNo:HAWBNo)
     */
    private String buildKey(String awbNo, String cwbNo) {
        return PREFIX + ":" + awbNo + ":" + cwbNo;
    }
}
