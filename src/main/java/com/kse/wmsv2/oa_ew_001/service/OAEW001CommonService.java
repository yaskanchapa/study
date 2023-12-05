package com.kse.wmsv2.oa_ew_001.service;

import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReturnDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class OAEW001CommonService {

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

    public OAIT001ReturnDto setResult(Object obj,String msg,String result){
        OAIT001ReturnDto resultVal = new OAIT001ReturnDto();
        if(obj != null){
            resultVal.setObj(obj);
        }
        if( !msg.equals("") || msg !=null){
            resultVal.setMsg(msg);
        }
        resultVal.setErrorFlg(result);
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

}
