package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CDBConstants;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001ReportCdbDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReportResultDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAET001CDBService extends OAET001CommonConstants{
    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    OAET001ReportMapper reportMapper;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReportResultDto writeCDB(HttpHeaders header, OAET001SearchDto param) {
        OAET001ReportResultDto resultDto = new OAET001ReportResultDto();
        String folderPath = "";
        int fileCnt = 0;
        int folderCnt = 0;

        // S3処理用
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();

        // ステータス更新
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(header);
        String userAuth = commonService.getAuth(header);
        String deptCd = commonService.getDeptCd(header);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(CDB_COMPLETE_WRITE);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA);
        statusDao.setBusinessClass(EXPORT_BUSINESS_CD);
        statusDao.setAwbNo(param.getAwbNo());
        statusDao.setCwbNoDeptCd(DEFAULT_CWB_DEPT_CD);
        statusDao.setBwbNo(DEFAULT_BWB_NO);
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", param.getAwbNo());
        updateParamMap.put("statusCd", CDB_COMPLETE_WRITE);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{
            //結果保存
            List<String> folderList = resultDto.getFolderList();
            List<String> s3FolderList = resultDto.getS3FolderList();


            // 電文作成用データ取得用MAP
            Map<String,String> dataMap = new HashMap<>();
            dataMap.put("awbNo",param.getAwbNo());
            dataMap.put("userAuth",userAuth);
            dataMap.put("deptCd",deptCd);
            dataMap.put("remakeFlg",param.getRemakeFlg());
            // 電文作成用データ取得
            List<OAET001ReportCdbDao> dataList = new ArrayList<>();
            dataList = reportMapper.getCdbData(dataMap);
            // データチェック
            if(dataList.size() == 0 ){
                resultDto.setErrorFlag(RESULT_SUCCESS);
                resultDto.setCntCdb(folderCnt);
                resultDto.setCntFileCDB(fileCnt);
                return resultDto;
            }
            // folder作成
            folderPath = createFolder(param);
            folderList.add(folderPath);
            folderCnt = folderCnt + 1;

            // S3パース取得
            s3Path = reportService.getS3Path("CDB");
            s3FolderList.add(s3Path);

            int totalSize = 0;
            int maxLen = 50;
            if(dataList.size() % 50 != 0){
                totalSize = dataList.size()/50 + 1;
            } else {
                totalSize = dataList.size() / 50;
            }

            for(int i = 0; i < totalSize; i++){
                // 共通項目作成
                maxLen = i == totalSize -1 ? dataList.size() : (i*50)+50 ;
                int reportLen = i== totalSize -1 ? dataList.size()% 50 : 50;
                String commonColumn = "";
                commonColumn = reportService.createCommonColumn(header,"CDB01","");
                int reportSize  = COMMON_LENCOMMONCABLE + OAET001CDBConstants.CDB_LENCOMMON
                        + (OAET001CDBConstants.CDB_LENEXDATAREP * reportLen);
                String format = "%0" + COMMON_LENCABLE + "d";
                commonColumn = commonColumn + String.format(format, reportSize);
                //データ作成
                StringBuilder sbReport = new StringBuilder();
                sbReport.append(commonColumn);
                sbReport.append("\r\n");
                sbReport.append(writeCDBHeader(dataList.get(0)));
                sbReport.append("\r\n");
                for(int k = i*50 ; k < maxLen ; k ++){
                    sbReport.append(writeCDBDetail(dataList.get(k)));
                    sbReport.append("\r\n");
                    // 再作成チェック：再作成の場合、更新はしない。
                    if(!param.getRemakeFlg().equals("true")){
                        // AE_DATAテーブルアップデート
                        Map<String,String> paramMap = new HashMap<>();
                        paramMap.put("cwbNo",dataList.get(k).getCwbNo());
                        paramMap.put("esaFlg", "");
                        paramMap.put("statusCd", CDB_COMPLETE_WRITE);
                        reportMapper.updateAEDATA(paramMap);
                        // ステータス更新
                        statusDao.setCwbNo(dataList.get(k).getCwbNo());
                        stsServ.updateStatusMaster(statusDao);
                        // データ更新
                        updateParamMap.put("cwbNo",dataList.get(k).getCwbNo());
                        int updateResult = reportMapper.updateMasterStatus(updateParamMap);
                        if(updateResult == 0){
                            throw new Exception(MSG_ERR_910);
                        }
                    }


                }
                // 電文ファイル名取得
                String fileName = reportService.getReportName("CDB",dataList.get(0).getFltDestination(),dataList.get(i).getAwbNo(),String.format("%04d", i+1),"");
                String filePath = reportService.getReportFilePath(folderPath,fileName);

                // 電文ファイル作成
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                bf.write(sbReport.toString());
                bf.flush();
                bf.close();

                // S3保存
                if(!reportService.saveReportS3(s3Path,fileName,sbReport.toString())){
                    throw new Exception("S3 Bucket Error");
                }
                fileNameList.add(fileName);

                // ファイル作成CNT
                fileCnt = fileCnt + 1;
            }
            // 処理結果保存
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setCntCdb(folderCnt);
            resultDto.setCntFileCDB(fileCnt);
            resultDto.setFolderList(folderList);
            resultDto.setS3FolderList(s3FolderList);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            //ログ
            log.error(e.getMessage());
            e.printStackTrace();

            // エラー結果保存
            resultDto.setErrorFlag(RESULT_ERROR);

            // フォルダ削除
            if(!StringUtil.isStringEmpty(folderPath)){
                commonService.deleteFolder(folderPath);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return resultDto;
    }

    private StringBuffer writeCDBHeader(OAET001ReportCdbDao data) {
        StringBuffer sb = new StringBuffer();

        // 2.搬入予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBonWareHoCurDate().replaceAll("-","")),OAET001CDBConstants.CDB_BONWAREHOCURDATE ,true));
        sb.append("\r\n");

        // 3.搬入予定保税蔵置場
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBondedWarehouseCd()),OAET001CDBConstants.CDB_BONDEDWAREHOUSECD ,true));
        sb.append("\r\n");

        // 4.搬入伝票作成要否
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarSlipMakeFlg()),OAET001CDBConstants.CDB_CARSLIPMAKEFLG ,true));
        sb.append("\r\n");

        // 5.航空貨物代理店
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getAgent()),OAET001CDBConstants.CDB_AGENT ,true));
        sb.append("\r\n");

        // 6.搬入伝票番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingSlipNo()),OAET001CDBConstants.CDB_CARRYINGSLIPNO ,true));

        return sb;
    }

    private StringBuffer writeCDBDetail(OAET001ReportCdbDao data) {
        StringBuffer sb = new StringBuffer();

        // 7.AWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()),OAET001CDBConstants.CDB_AWBNO ,true));
        sb.append("\r\n");

        // 8.貨物識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCargoDisc()),OAET001CDBConstants.CDB_CARGODISC ,true));
        sb.append("\r\n");

        // 9.搬入個数
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingPiece()),OAET001CDBConstants.CDB_CARRYINGPIECE ,false));
        sb.append("\r\n");

        // 10.搬入重量
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingWeight()),OAET001CDBConstants.CDB_CARRYINGWEIGHT ,false));
        sb.append("\r\n");

        // 11.積込港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDepPort()),OAET001CDBConstants.CDB_DEPPORTCD ,true));
        sb.append("\r\n");

        // 12.仕向地
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDestination()),OAET001CDBConstants.CDB_LASTDESTINATIONCD ,true));
        sb.append("\r\n");

        // 13.航空会社
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_AIRLINE ,true));
        sb.append("\r\n");

        // 14.混載業
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getMixedForwarder()),OAET001CDBConstants.CDB_MIXEDFORWARDER ,true));
        sb.append("\r\n");

        // 15.代理店営業所
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getAgentDiv()),OAET001CDBConstants.CDB_AGENTDIVE ,true));
        sb.append("\r\n");

        // 16.通関依頼先
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsRequest()),OAET001CDBConstants.CDB_CUSTOMSREQUEST ,true));
        sb.append("\r\n");

        // 17.輸出申告予定年月日
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_EXPREPORTPLANDATE ,false));
        sb.append("\r\n");

        // 18.MAWB番号
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_AWBNO02 ,true));
        sb.append("\r\n");

        // 19.貨物種別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCargoType()),OAET001CDBConstants.CDB_CARGOTYPE ,true));
        sb.append("\r\n");

        // 20.総個数(ダミー)
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_DUMMY_01 ,false));
        sb.append("\r\n");

        // 21.総重量(ダミー)
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_DUMMY_02 ,false));
        sb.append("\r\n");

        // 22.許可・承認等番号(ダミー)
        sb.append(StringUtil.fillSpace("",OAET001CDBConstants.CDB_DUMMY_03 ,true));
        sb.append("\r\n");

        // 23.特殊貨物記号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getSpecialCargoSign()),OAET001CDBConstants.CDB_SPECIALCARGOSIGN ,true));
        sb.append("\r\n");

        // 24.品名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getItem()),OAET001CDBConstants.CDB_ITEM ,true));

        return sb;
    }

    private String createFolder(Object param) throws Exception {
        String folderPath = reportService.getReportPath("CDB");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String path = folderPath + File.separator + "CDB"+ date;
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                log.error(e.getMessage());
                throw new FileSystemException("FAIL FOLDER MAKE");
            }
        }
        return path;
    }

    private String createS3FolderPath(Object param) throws Exception {
        String folderPath = reportService.getReportPath("CDB");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String path = folderPath + File.separator + "CDB"+ date;
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                log.error(e.getMessage());
                throw new FileSystemException("FAIL FOLDER MAKE");
            }
        }
        return path;
    }


}
