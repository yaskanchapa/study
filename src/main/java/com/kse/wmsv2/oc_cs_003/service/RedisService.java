package com.kse.wmsv2.oc_cs_003.service;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oc_cs_003.dto.LoginUserInfoDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Redisと通信処理を行うService
 */
@Service
@Transactional
public class RedisService {

    /** Redis通信の為の共通部品 */
    @Autowired
    private RedisUtil redisUtil;

    /**
     * Redisに保存処理を行う
     * @document
     * トークンをkeyとして使用してユーザー情報をHash形式より保存する
     * @param loginUserInfoDto
     * @return 正常:true 異常:false
     */
    public boolean saveRedis(LoginUserInfoDto loginUserInfoDto){
        // トークンをkeyとして使用する
        String key = loginUserInfoDto.getAccessToken();
        //　トークンをkeyとして使用してユーザー情報をHash形式より保存する
        String userCd = loginUserInfoDto.getUserInfoDto().getUserCd();
        String userName = loginUserInfoDto.getUserInfoDto().getUserName();
        String userNameSylabary = loginUserInfoDto.getUserInfoDto().getUserNameSyllabary();
        String userNameEng = loginUserInfoDto.getUserInfoDto().getUserNameEng();
        String userManagementAuthorityCd = loginUserInfoDto.getUserInfoDto().getUserManagementAuthorityCd();
        String userAuthorityCd = loginUserInfoDto.getUserInfoDto().getUserAuthorityCd();
        String userCompanyCd = loginUserInfoDto.getUserInfoDto().getUserCompanyCd();
        String departmentCd = loginUserInfoDto.getUserInfoDto().getDepartmentCd();
        String userAuthorityGroupCd = loginUserInfoDto.getUserInfoDto().getUserAuthorityGroupCd();
        String printUserCompanyNM = loginUserInfoDto.getUserInfoDto().getPrintUserCompanyNm();
        String printUserCompanyCd = loginUserInfoDto.getUserInfoDto().getPrintUserCompanyCd();
        String printUserName = loginUserInfoDto.getUserInfoDto().getPrintUserName();
        String departmentName = loginUserInfoDto.getUserInfoDto().getDepartmentName();

        redisUtil.saveRedis(key, "USERCD", userCd);
        redisUtil.saveRedis(key, "USERNAME", userName);
        redisUtil.saveRedis(key, "USERNAMESYLABARY", userNameSylabary);
        redisUtil.saveRedis(key, "USERNAMEENG", userNameEng);
        redisUtil.saveRedis(key, "USERMANAGEMENTAUTHORITYCD", userManagementAuthorityCd);
        redisUtil.saveRedis(key, "USERAUTHORITYCD", userAuthorityCd);
        redisUtil.saveRedis(key, "USERCOMPANYCD", userCompanyCd);
        redisUtil.saveRedis(key, "DEPARTMENTCD", departmentCd);
        redisUtil.saveRedis(key, "USERAUTHORITYGROUPCD", userAuthorityGroupCd);
        redisUtil.saveRedis(key, "PRINTUSERCOMPANYNM", printUserCompanyNM);
        redisUtil.saveRedis(key, "PRINTUSERCOMPANYCD", printUserCompanyCd);
        redisUtil.saveRedis(key, "PRINTUSERNAME", printUserName);
        redisUtil.saveRedis(key, "DEPARTMENTNAME", departmentName);

       return true;
    }

    public boolean delete(String key){
        redisUtil.delete(key);
        return true;
    }
}
