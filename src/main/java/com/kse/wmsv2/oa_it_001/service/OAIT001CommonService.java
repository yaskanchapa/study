package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001HeaderDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001SearchResultDao;
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
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class OAIT001CommonService {

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

    public String getUserManageAuth(HttpHeaders headers){
        String auth = "";
        String accessToken = (String)headers.get("authorization").get(0);
        auth = redisUtil.loadRedis(accessToken, "USERMANAGEMENTAUTHORITYCD");
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



    public byte[] makeZipFile(String folderPath,HttpHeaders headers,String businessName,String zipName) throws IOException {
        File zipFolder = new File(folderPath);
        File[] fileList = zipFolder.listFiles();
        List<File> zipFileList = new ArrayList<>();

        for(int i = 0 ; i < fileList.length; i++){
            zipFileList.add(fileList[i]);
        }

        String userCd = getUserCd(headers);
        String date = DateUtil.getNow("MMdd_HHmmss");
        String strTempDir = userCd + date;

        Path tempDir = Files.createTempDirectory(strTempDir);
        Path zipFilePath = Paths.get(tempDir.toString(),zipName);
        File zipFile = new File(zipFilePath.toString());
        byte[] buf = new byte[4096];

        try(ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile), Charset.forName("Shift_JIS"))){
            for(File file : zipFileList){
                try(FileInputStream in = new FileInputStream(file);){
                    ZipEntry ze = new ZipEntry(file.getName());
                    out.putNextEntry(ze);
                    int len;
                    while((len = in.read(buf)) > 0 ){
                        out.write(buf,0,len);
                    }
                    out.closeEntry();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }

        byte[] zipFileContent = Files.readAllBytes(zipFilePath);
        FileUtils.deleteDirectory(tempDir.toFile());

        return zipFileContent;
    }

    public boolean checkPkKey(String awb,String cwb, String deptCd){
        boolean result = false;
        if(StringUtil.isStringEmpty(awb) || StringUtil.isStringEmpty(cwb) || StringUtil.isStringEmpty(deptCd)){
            result = true;
        }
        return result;
    }

    public OAIT001HeaderDao getCountMasterNumber(List<OAIT001SearchResultDao> searchList,OAIT001HeaderDao returnVal) throws Exception {
        NumberFormat format = NumberFormat.getInstance();
        int cwbCount = 0;
        double totalWeight = 0;
        int totalPiece = 0;

        int checkedIda = 0;
        int checkedMic = 0;
        int checkedTotal = 0;
        int unCheckedIda = 0;
        int unCheckedMic = 0;
        int unCheckedTotal = 0;
        int totalIda = 0;
        int totalMic = 0;
        int totalTotal = 0;
        int registerIda = 0;
        int registerMic = 0;
        int registerTotal = 0;
        double progressIda = 0;
        double progressMic = 0;
        double progressTotal = 0;

        Set<String> errHawbList = new HashSet<>();


        for(int i = 0; i < searchList.size(); i++){
            String docChk = StringUtil.isStringNull(searchList.get(i).getDocumentCheck());
            String idaFlg = StringUtil.isStringNull(searchList.get(i).getIdaFlg());
            String editFlg = StringUtil.isStringNull(searchList.get(i).getEditFlg());
            cwbCount += 1;

            if(StringUtil.isStringEmpty(searchList.get(i).getCargoWeight())){
                errHawbList.add(searchList.get(i).getCwbNo());
            }else {
                totalWeight = totalWeight + Double.parseDouble(searchList.get(i).getCargoWeight());
            }

            if(StringUtil.isStringEmpty(searchList.get(i).getCargoPiece())){
                errHawbList.add(searchList.get(i).getCwbNo());
            } else {
                totalPiece = totalPiece + Integer.parseInt(searchList.get(i).getCargoPiece());
            }


            if(docChk.equals("1")){
                if(idaFlg.equals("0")){
                    checkedMic += 1;
                    checkedTotal += 1;
                    totalMic += 1;
                    totalTotal += 1;
                } else {
                    checkedIda += 1;
                    checkedTotal += 1;
                    totalIda += 1;
                    totalTotal += 1;
                }
            } else {
                if(idaFlg.equals("0")){
                    unCheckedMic += 1;
                    unCheckedTotal += 1;
                    totalMic += 1;
                    totalTotal += 1;
                } else {
                    unCheckedIda += 1;
                    unCheckedTotal += 1;
                    totalIda += 1;
                    totalTotal += 1;
                }
            }

            if(editFlg.equals("1")){
                if(idaFlg.equals("0")){
                    registerMic += 1;
                    registerTotal += 1;
                } else {
                    registerIda += 1;
                    registerTotal += 1;
                }
            }
        }

        progressIda = Double.isNaN(((double)registerIda / (double)totalIda) * 100) ? 0 : ((double)registerIda / (double)totalIda) * 100;
        progressMic = Double.isNaN(((double)registerMic / (double)totalMic) * 100) ? 0 : ((double)registerMic / (double)totalMic) * 100;
        progressTotal = Double.isNaN(((double)registerTotal / (double)totalTotal) * 100) ? 0 : ((double)registerTotal / (double)totalTotal) * 100;

        returnVal.setCheckedIda(format.format(checkedIda));
        returnVal.setCheckedMic(format.format(checkedMic));
        returnVal.setCheckedTotal(format.format(checkedTotal));

        returnVal.setUnCheckedMic(format.format(unCheckedMic));
        returnVal.setUnCheckedIda(format.format(unCheckedIda));
        returnVal.setUnCheckedTotal(format.format(unCheckedTotal));

        returnVal.setTotalIda(format.format(totalIda));
        returnVal.setTotalMic(format.format(totalMic));
        returnVal.setTotalTotal(format.format(totalTotal));

        returnVal.setRegisterIda(format.format(registerIda));
        returnVal.setRegisterMic(format.format(registerMic));
        returnVal.setRegisterTotal(format.format(registerTotal));

        returnVal.setProgressIda(format.format(Math.floor(progressIda)));
        returnVal.setProgressMic(format.format(Math.floor(progressMic)));
        returnVal.setProgressTotal(format.format(Math.floor(progressTotal)));

        returnVal.setCwbCount(format.format(cwbCount));
        returnVal.setSumCwbPiece(format.format(totalPiece));
        returnVal.setSumCwbWeight(format.format((int)totalWeight));


        if(errHawbList.size() > 0 ){
            String msg = OAIT001CommonConstants.MSG_ERR_998 + errHawbList.toString();
            returnVal.setErrFlg("warning");
            returnVal.setMsg(msg);
        } else {
            returnVal.setErrFlg("false");
        }

        return returnVal;
    }


    public String lbrToKgm(String weight,String weightUnitCD){
        String returnWeight = "";

        if(weightUnitCD.equals(OAIT001CommonConstants.WEIGHT_UNIT_LBR)){
            BigDecimal bdW = new BigDecimal(weight);
            BigDecimal bdV = new BigDecimal(OAIT001CommonConstants.LBR_VALUE);
            weight = String.format("%.2f",bdW.multiply(bdV));
            double doWeight = Double.parseDouble(weight);
            double doWeight2 = Math.round(doWeight*10)/10.0;
            returnWeight = Double.toString(doWeight2 - Math.floor(doWeight2) > 0.5 ? Math.round(doWeight2) : Math.floor(doWeight2) + 0.5);
        } else {
            returnWeight = weight;
        }

        return returnWeight;
    }


    public String createRateErrorMessage(String rate){
        String msg = OAIT001CommonConstants.MSG_ERR_506;
        msg = msg + "「エラーレート : " + rate + "」";
        return msg;
    }


}
