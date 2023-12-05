package com.kse.wmsv2.oa_it_004.service;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReturnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class OAIT004COMMONService {

    @Autowired
    private RedisUtil redisUtil;

    public String getDeptCd(HttpHeaders headers){
        String deptCd = "";
        String accessToken = (String)headers.get("authorization").get(0);
        deptCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        return deptCd;
    }

    public String getAuth(HttpHeaders headers){
        String auth = "";
        String accessToken = (String)headers.get("authorization").get(0);
        auth = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        return auth;
    }


    public String getUserCd(HttpHeaders headers) {
        String userCd = "";
        String accessToken = (String)headers.get("authorization").get(0);
        userCd = redisUtil.loadRedis(accessToken, "USERCD");
        return userCd;
    }

    public String getCompanyCd(HttpHeaders headers) {
        String userCd = "";
        String accessToken = (String)headers.get("authorization").get(0);
        userCd = redisUtil.loadRedis(accessToken, "USERCOMPANYCD");
        return userCd;
    }

}
