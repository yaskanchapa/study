package com.kse.wmsv2.oa_ew_001.service;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_ew_001.common.OAEW001CommonConstants;
import com.kse.wmsv2.oa_ew_001.dao.*;
import com.kse.wmsv2.oa_ew_001.dto.*;
import com.kse.wmsv2.oa_ew_001.mapper.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class OAEW001ScreenService extends OAEW001CommonConstants {

    @Autowired
    OAEW001ScreenMapper oaew001ScreenMapper;

    @Autowired
    private RedisUtil redisUtil;


    public OAEW001ReturnDto searchResultList(HttpHeaders header, OAEW001SearchDto params){
        OAEW001ReturnDto returnValue = new OAEW001ReturnDto();
        List<OAEW001SearchResultDao> resultList = new ArrayList<>();

        try{
            params.setUserAuthorityCd(getAuth(header));
            params.setUserDepartmentCd(getDeptCd(header));
            resultList = oaew001ScreenMapper.selectSearchResult(params);
            if(resultList.size() == 0){
                returnValue.setErrorFlag(RESULT_WARNING);
                returnValue.setMessage(MSG_WAR_001);
            } else {
                returnValue.setSearchResultList(resultList);
                returnValue.setErrorFlag(RESULT_SUCCESS);
                returnValue.setMessage(MSG_SUC_001);
            }
        } catch (Exception e){
            e.printStackTrace();
            log.error(e.getMessage());
            returnValue.setErrorFlag(RESULT_ERROR);
            returnValue.setMessage(MSG_ERR_001);
        }

        return returnValue;
    }


    private String getAuth(HttpHeaders headers){
        String auth = "";
        String accessToken = (String)headers.get("authorization").get(0);
        auth = redisUtil.loadRedis(accessToken, "USERAUTHORITYCD");
        return auth;
    }

    private String getDeptCd(HttpHeaders headers){
        String deptCd = "";
        String accessToken = (String)headers.get("authorization").get(0);
        deptCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        return deptCd;
    }

}
