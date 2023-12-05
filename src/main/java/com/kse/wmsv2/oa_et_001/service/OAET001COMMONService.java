package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReturnDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import java.io.File;

@Service
public class OAET001COMMONService {

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


    public OAIT001ReturnDto setResult(Object obj, String msg, boolean result){
        OAIT001ReturnDto resultVal = new OAIT001ReturnDto();

        if(obj != null){
            resultVal.setObj(obj);
        }
        if( !msg.equals("") || msg !=null){
            resultVal.setMsg(msg);
        }
        resultVal.setResult(result);


        return resultVal;
    }

    public void deleteFolder(String path){
        File deleteFolder = new File(path);
        File[] fileList = deleteFolder.listFiles();
        for(int i = 0; i < fileList.length; i++){
            fileList[i].delete();
        }
        deleteFolder.delete();
    }

    public String createRateErrorMessage(String rate){
        String msg = OAET001CommonConstants.MSG_ERR_012;
        msg = msg + "「エラーレート : " + rate + "」";
        return msg;
    }
}
