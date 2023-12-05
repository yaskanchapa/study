package com.kse.wmsv2.oc_cs_007.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oc_cs_007.dao.OCCS007InsertCsImageManagementDao;
import com.kse.wmsv2.oc_cs_007.dao.OCCS007SelectCsImageManagementDao;
import com.kse.wmsv2.oc_cs_007.mapper.OCCS007ScreenMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Service
public class OCCS007Service {

    @Autowired
    AwsS3 awsS3;

    @Autowired
    OCCS007ScreenMapper occs007ScreenMapper;

    @Autowired
    private RedisUtil redisUtil;

    public boolean pdfUpload(HttpHeaders headers, MultipartHttpServletRequest request) throws IOException {

        boolean rst = false;
        int uploadCnt = 0;
        int insertCnt = 0;

        // ヘッダのtokenより、ユーザーコード取得
        String accessToken = (String)headers.get("authorization").get(0);
        String userCd = redisUtil.loadRedis(accessToken, "USERCD");

        //　輸入・輸出フラグ取得
        String impExpClassName = request.getParameter("impExpClassName");

        // path設定用日付情報取得
        String path = "";
        String date = DateUtil.getNow("yyyy-MM-dd HH:mm:ss");
        String yyyy = date.substring(0, 4);
        String MM = date.substring(5, 7);
        String dd = date.substring(8, 10);
        String HH = date.substring(11,13);
        String mm = date.substring(14,16);
        String ss = date.substring(17,19);
        String HHmmss = HH + mm + ss;

        //　S3Upload処理
        Iterator<String> Its = request.getFileNames();

        while (Its.hasNext()) {

            String key = Its.next();

            // file取得
            List<MultipartFile> fileList = request.getFiles(key);

            //　各fileの繰返し処理開始
            for (MultipartFile file : fileList) {

                //　keyName設定
                String[] keyNameArr = file.getOriginalFilename().split("/");

                // path設定
                if ("輸出".equals(impExpClassName)) {
                    if(keyNameArr[keyNameArr.length - 1].contains("-")){
                        path = "Export/Invoice/" + yyyy + "/" + MM + "/" + dd + "/";
                    } else {
                        path = "Export/Cw/" + yyyy + "/" + MM + "/" + dd + "/";
                    }
                } else {
                    if(keyNameArr[keyNameArr.length - 1].contains("-")){
                        path = "Import/Invoice/" + yyyy + "/" + MM + "/" + dd + "/";
                    } else {
                        path = "Import/Cw/" + yyyy + "/" + MM + "/" + dd + "/";
                    }
                }

                String keyName = keyNameArr[keyNameArr.length - 1];
                if (keyName.contains("-")) {
                    keyName = keyName.substring(0, keyName.indexOf("-")).trim();
                    keyName = path + yyyy + MM + dd + HHmmss + "_" + keyName + "_" + "IN.pdf";
                } else if (keyName.contains("(")) {
                    keyName = keyName.substring(0, keyName.indexOf("(")).trim();
                    keyName = path + yyyy + MM + dd + HHmmss + "_" + keyName + "_" + "CW.pdf";
                } else {
                    keyName = keyName.substring(0, keyName.indexOf(".")).trim();
                    keyName = path + yyyy + MM + dd + HHmmss + "_" + keyName + "_" + "CW.pdf";
                }
                log.info("Uploadファイル名： " + keyName);

                // fileをByte化
                byte[] fileByte = file.getBytes();

                // S3Upload
                uploadCnt += awsS3.uploadPdf(fileByte, keyName);

                // CS_IMAGE_MANAGEMENT更新
                OCCS007InsertCsImageManagementDao parm = setCsImageManagementDao(impExpClassName, keyName, userCd, date);
                insertCnt += occs007ScreenMapper.insertCsImageManagement(parm);
            }

        }

        log.info("UPLOAD件数： " + uploadCnt);
        log.info("CS_IMAGE_MANAGEMENT 更新件数： " + insertCnt);

        if (uploadCnt > 0 && insertCnt > 0) {
            rst = true;
        }
        return rst;
    }

    public OCCS007InsertCsImageManagementDao setCsImageManagementDao(String impExpClassName, String keyName, String userCd, String date){

        OCCS007InsertCsImageManagementDao parm = new OCCS007InsertCsImageManagementDao();
        OCCS007SelectCsImageManagementDao parm2 = new OCCS007SelectCsImageManagementDao();

        String importExportClass = "";
        if("輸出".equals(impExpClassName)){
            importExportClass = "2";
        } else {
            importExportClass = "1";
        }

        // cwbNo取得
        String cwbNo = keyName.split("_")[1];

        // imageClass取得
        String imageClass = keyName.split("_")[2];
        imageClass = imageClass.substring(0, imageClass.indexOf(".pdf"));

        // Seq値取得
        parm2.setImportExportClass(importExportClass);
        parm2.setCwbNo(cwbNo);
        String seq = occs007ScreenMapper.selectCsImageManagement(parm2);
        if(StringUtils.isEmpty(seq)){
            seq = "0";
        }
        int seqInt = Integer.parseInt(seq) + 1;

        parm.setImportExportClass(importExportClass);
        parm.setAwbNo("00");
        parm.setCwbNo(cwbNo);
        parm.setCwbNoDeptCd("000");
        parm.setImageClass(imageClass);
        parm.setSeq(Integer.toString(seqInt));
        parm.setImagePath(keyName);
        parm.setRegUserCd(userCd);
        parm.setRegDate(date);
        parm.setUpdateUserCd(userCd);
        parm.setUpdateDate(date);

        return parm;
    }

}