package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.mapper.COMMONMapper;
import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001IDAConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ReportService {

    @Autowired
    private AwsS3 awsS3;

    @Autowired
    COMMONMapper mapper;

    @Autowired
    private RedisUtil redisUtil;

    //制御情報
    int COMMON_CONTROLINFO  = 3;
    //業務コード
    int COMMON_BUSINESSCD  = 5;
    //予約エリア１
    int COMMON_RESERV01  = 21;
    //利用者コード
    int COMMON_USERCD  = 5;
    //識別番号
    int COMMON_SIKIBETUNO  = 3;
    //利用者パスワード
    int COMMON_USERPASS  = 8;
    //予約エリア２
    int COMMON_RESERV02  = 174;
    //電文引継情報
    int COMMON_CABLESUCCESS  = 26;
    //予約エリア３
    int COMMON_RESERV03  = 8;
    //入力情報特定番号
    int COMMON_INPUTINFONO  = 10;
    //索引引継情報
    int COMMON_INDEXINFO  = 100;
    //予約エリア４
    int COMMON_RESERV04  = 1;
    //システム識別
    int COMMON_SYSTEMSIKIBETUNO  = 1;
    //ＥＤＡ大額小学判断
    int COMMON_LS  = 3;
    //予約エリア５
    int COMMON_RESERV05  = 24;
    //予約エリア５
    int COMMON_RESERV05_N  = 27;
    //電文長
    int COMMON_LENCABLE  = 6;
    //共通部電文長
    int COMMON_LENCOMMONCABLE  = 400;


    public String createCommonColumn(HttpHeaders header,String businessCd,String strWk){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd", getDeptCd(header));

        Map<String, Object> dataMap = mapper.getCommonValueReport(paramMap);
        StringBuffer sb = new StringBuffer();

        // 制御情報
        sb.append(StringUtil.fillSpace("", COMMON_CONTROLINFO,true));

        // 業務コード
        sb.append(StringUtil.fillSpace(businessCd, COMMON_BUSINESSCD,true));

        // 予約エリア１
        sb.append(StringUtil.fillSpace("", COMMON_RESERV01,true));

        // 利用者コード
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM1"), COMMON_USERCD,true));

        // 識別番号
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM2"), COMMON_SIKIBETUNO,true));

        // 利用者パスワード
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM3"), COMMON_USERPASS,true));

        // 予約エリア２
        sb.append(StringUtil.fillSpace("", COMMON_RESERV02,true));

        // 電文引継情報
        sb.append(StringUtil.fillSpace("", COMMON_CABLESUCCESS,true));

        // 予約エリア３
        sb.append(StringUtil.fillSpace("", COMMON_RESERV03,true));

        // 入力情報特定番号
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM4"), COMMON_INPUTINFONO,true));

        // 索引引継情報
        sb.append(StringUtil.fillSpace("", COMMON_INDEXINFO,true));

        // 予約エリア４
        sb.append(StringUtil.fillSpace("", COMMON_RESERV04,true));

        // システム識別
        sb.append(StringUtil.fillSpace("1", COMMON_SYSTEMSIKIBETUNO,true));

        // ＥＤＡ大額小学判断
        sb.append(StringUtil.fillSpace(strWk, COMMON_LS,true));

        // 予約エリア５
        sb.append(StringUtil.fillSpace("", COMMON_RESERV05,true));

        return sb.toString();
    }

    public String createCommonColumnAuto(HttpHeaders header,String businessCd,String fileName,String userCd){
        StringBuffer sb = new StringBuffer();
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("deptCd", getDeptCd(header));
        paramMap.put("userCd", userCd);
        paramMap.put("businessCd", StringUtil.isStringNull(businessCd));

        Map<String, Object> dataMap = mapper.getCommonValueReportAuto(paramMap);

        // 制御情報
        sb.append(StringUtil.fillSpace("", COMMON_CONTROLINFO,true));

        // 業務コード
        sb.append(StringUtil.fillSpace(businessCd, COMMON_BUSINESSCD,true));

        // 予約エリア１
        sb.append(StringUtil.fillSpace("", COMMON_RESERV01,true));

        // 利用者コード
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM1"), COMMON_USERCD,true));

        // 識別番号
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM2"), COMMON_SIKIBETUNO,true));

        // 利用者パスワード
        sb.append(StringUtil.fillSpace((String) dataMap.get("CHARACTERITEM3"), COMMON_USERPASS,true));

        // 予約エリア２
        sb.append(StringUtil.fillSpace("", COMMON_RESERV02,true));

        // 電文引継情報
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(fileName).replaceAll("_"," ").toUpperCase(), COMMON_CABLESUCCESS,true));

        // 予約エリア３
        sb.append(StringUtil.fillSpace("", COMMON_RESERV03,true));

        // 入力情報特定番号
        sb.append(StringUtil.fillSpace("", COMMON_INPUTINFONO,true));

        // 索引引継情報
        sb.append(StringUtil.fillSpace("", COMMON_INDEXINFO,true));

        // 予約エリア４
        sb.append(StringUtil.fillSpace("", COMMON_RESERV04,true));

        // システム識別
        sb.append(StringUtil.fillSpace("1", COMMON_SYSTEMSIKIBETUNO,true));

        // 予約エリア５
        sb.append(StringUtil.fillSpace("", COMMON_RESERV05_N,true));

        return sb.toString();
    }


    public String getDeptCd(HttpHeaders headers){
        String deptCd = "";
        String accessToken = (String)headers.get("authorization").get(0);
        deptCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        return deptCd;
    }


    public String getS3Path(String businessName){
        String path = mapper.getS3Path(businessName);
        path = path + DateUtil.getNow("yyyy/MM/dd/");
        String date = DateUtil.getNow("MMdd_HHmmss/");
        return path + businessName + date;
    }
    public String getS3Path2(String businessName, String simAuto){
        String path = mapper.getS3Path2(simAuto)+"/";
        path = path + DateUtil.getNow("yyyy/MM/dd/");
        String date = DateUtil.getNow("MMdd_HHmmss/");
        return path + businessName + date;
    }

    public String getReportPath(String reportName){
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("reportName" , reportName);
        return mapper.getReportPath(paramMap);
    }

    public boolean createFolder(String folderName){
        boolean result = true;
        File folder = new File(folderName);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    public String getReportFilePath(String path,String fileName){
        return path + File.separator+ fileName;
    }


    public String getReportName(String reportName,String val1,String val2,String val3,String val4){
        StringBuffer sb = new StringBuffer();
        sb.append(reportName);
        sb.append("_");
        switch (reportName){
            case "CDB" :
            case "HM" :
            case "HI" :
            case "CM" :
            case "CI" :
            case "HDM" :
            case "HDI" :
            case "HDF" :
                // 仕向地
                sb.append(val1);
                // AWB番号
                sb.append(val2);
                sb.append("_");
                //　シケンス番号
                sb.append(val3);
                break;
            case "MEC" :
            case "EDC" :
                sb.append(val1);
                sb.append("_");
                sb.append(val3);
                break;
            case "EDA" :
            case "ULM" :
            case "IDC" :
            case "MIC" :
                sb.append(val1);
                sb.append("_");
                sb.append(val2);
                sb.append("_");
                sb.append(val3);
                break;
            case "IDA" :
            case "HPK" :
                sb.append(val1);
                sb.append("_");
                sb.append(val2);
                sb.append("_");
                sb.append(val3);
                sb.append("_");
                sb.append(val4);
                break;
            case "OUT" :
            case "OLT" :
                sb.append(val1);
                break;
            case "BIL" :
                // '_'削除
                sb.deleteCharAt(sb.length()-1);
                // DepartmentCd
                sb.append(val1);
                // 日付
                sb.append(val2);
                //　seq
                sb.append(val3);
                break;
        }
        sb.append(".txt");
        return sb.toString();
    }


    public boolean saveReportS3(String s3Path,String fileName, String content){
        try{
            String key = s3Path + fileName;
            File file = new File(fileName);
            file.createNewFile();
            Files.write(file.toPath(),content.getBytes());
            awsS3.upload(Files.readAllBytes(file.toPath()), key);
            file.delete();
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public void deleteReportS3(String s3Path,List<String> fileName){
        try{
            if(fileName != null && fileName.size() > 0 ){
                for(int i = 0; i < fileName.size(); i++){
                    String key = s3Path + fileName.get(i);
                    awsS3.delete(key);
                }
            } else {
                String[] checkList = s3Path.split("/");
                if(checkList.length > 6){
                    awsS3.delete(s3Path);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public int updateImportHeader(String awbNo,String flt1,String flt2) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("awbNo",awbNo);
        paramMap.put("flt1",flt1);
        paramMap.put("flt2",flt2);
        Map<String,String> resultMap = new HashMap<>();
        resultMap = mapper.getHDInformation(paramMap);
        resultMap.put("awbNo",awbNo);
        resultMap.put("flt1",flt1);
        resultMap.put("flt2",flt2);
        int result = mapper.updateHDInformation(resultMap);

        return result;
    }


}
