package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.common.OAET001MECConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001ReportMecDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ReportResultDto;
import com.kse.wmsv2.oa_et_001.dto.OAET001SearchDto;
import com.kse.wmsv2.oa_et_001.mapper.OAET001ReportMapper;
import com.kse.wmsv2.oa_it_001.common.OAIT001MICConstants;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class OAET001MECService extends OAET001CommonConstants{

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    OAET001ReportMapper reportMapper;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReportResultDto writeMEC(HttpHeaders header, OAET001SearchDto param) {
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
        statusDao.setCustomStatusCd(MEC_WRITE);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA);
        statusDao.setBusinessClass(EXPORT_BUSINESS_CD);
        statusDao.setAwbNo(param.getAwbNo());
        statusDao.setCwbNoDeptCd(DEFAULT_CWB_DEPT_CD);
        statusDao.setBwbNo(DEFAULT_BWB_NO);
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", param.getAwbNo());
        updateParamMap.put("statusCd", MEC_WRITE);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{
            //結果保存
            List<String> folderList = resultDto.getFolderList();
            List<String> s3FolderList = resultDto.getS3FolderList();
            // S3パース取得
            s3Path = reportService.getS3Path("MEC");
            s3FolderList.add(s3Path);
            // 電文作成用データ取得用MAP
            Map<String,String> dataMap = new HashMap<>();
            String depCd = reportMapper.getDeptCd(deptCd);
            dataMap.put("awbNo",param.getAwbNo());
            dataMap.put("userAuth",userAuth);
            dataMap.put("deptCd",deptCd);
            dataMap.put("depCd",depCd);

            // 電文作成用データ取得
            List<OAET001ReportMecDao> dataList;

            if(param.getRemakeFlg().equals("true")){
                dataList = reportMapper.getMecDataReMake(dataMap);
            } else {
                dataList = reportMapper.getMecDataNew(dataMap);
            }

            // データチェック
            if(dataList.size() == 0 ){
                resultDto.setErrorFlag(RESULT_SUCCESS);
                resultDto.setCntMec(folderCnt);
                resultDto.setCntFileMEC(fileCnt);
                return resultDto;
            }

            // folder作成
            folderPath = createFolder(param);
            folderList.add(folderPath);
            folderCnt = folderCnt + 1;

            // 共通項目作成
            String commonColumn;
            commonColumn = reportService.createCommonColumn(header,"MEC","");
            String format = "%0" + COMMON_LENCABLE + "d";
            int totalLen = COMMON_LENCOMMONCABLE + OAET001MECConstants.LENCOMMON;
            commonColumn = commonColumn + String.format(format, totalLen);

            // MEC電文作成
            for(int i = 0 ; i < dataList.size() ; i++){
                // 電文ファイル名取得
                String fileName = reportService.getReportName("MEC",dataList.get(i).getCwbNo(),"",String.format("%04d", i+1),"");
                String filePath = reportService.getReportFilePath(folderPath,fileName);

                // MEC電文内容作成
                StringBuffer sb = new StringBuffer();
                System.out.println("TESTMA1");
                System.out.println(dataList.get(i).toString());
                sb.append(writeMECReport(dataList.get(i),commonColumn));

                // 電文ファイル作成
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                bf.write(sb.toString());
                bf.flush();
                bf.close();

                // S3保存
                if(StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path,fileName,sb.toString())){
                    throw new Exception("S3 Bucket Error");
                }
                fileNameList.add(fileName);

                // 再作成チェック：再作成の場合、更新はしない。
                if(!param.getRemakeFlg().equals("true")){
                    // AE_DATAテーブルアップデート
                    Map<String,String> paramMap = new HashMap<>();
                    paramMap.put("cwbNo",dataList.get(i).getCwbNo());
                    paramMap.put("esaFlg", "1");
                    paramMap.put("statusCd", MEC_WRITE);
                    reportMapper.updateAEDATA(paramMap);
                    // ステータス更新
                    statusDao.setCwbNo(dataList.get(i).getCwbNo());
                    stsServ.updateStatusMaster(statusDao);
                    // データ更新
                    updateParamMap.put("cwbNo",dataList.get(i).getCwbNo());
                    int updateResult = reportMapper.updateMasterStatus(updateParamMap);
                    if(updateResult == 0){
                        throw new Exception(MSG_ERR_908);
                    }
                }
                // ファイル作成CNT
                fileCnt = fileCnt + 1;
            }

            // 処理結果保存
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setCntMec(folderCnt);
            resultDto.setCntFileMEC(fileCnt);
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

    private StringBuffer writeMECReport(OAET001ReportMecDao data, String commonColumn) {
        StringBuffer sb = new StringBuffer();

        // 1.共通項目
        sb.append(commonColumn);
        sb.append("\r\n");

        // 2.申告番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReportNo()), OAET001MECConstants.EXPREPORTNO,true));
        sb.append("\r\n");

        // 3.申告条件
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportCondition()), OAET001MECConstants.REPORTCONDITION,true));
        sb.append("\r\n");

        // 4.申告先種別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsKindCd1()), OAET001MECConstants.CUSTOMSKINDCD_1,true));
        sb.append("\r\n");

        // 5.識別符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDiscernmentMark()), OAET001MECConstants.DISCERNMENTMARK,true));
        sb.append("\r\n");

        // 6.あて先部門コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDepCd()), OAET001MECConstants.REPORTDEPCD,true));
        sb.append("\r\n");

        // 7.あて先官署コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDivisionCd()), OAET001MECConstants.REPORTDIVISIONCD,true));
        sb.append("\r\n");

        // 8.申告予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(StringUtil.isStringNull(data.getExpReportPlanDate())), OAIT001MICConstants.MIC_REPORTPLANINGDATE,false));
        sb.append("\r\n");

        // 9.輸出者コード
        String exporterNo = StringUtil.isStringNull(data.getExpCustomerNo());
        if(exporterNo.length() > 2 && exporterNo.substring(0,3).equals("OCS")){
            sb.append(StringUtil.fillSpace("", OAET001MECConstants.EXPCUSTOMERNO,true));
        } else {
            String deptCd = StringUtil.isStringNull(data.getExpCustomerDeptCd());
            sb.append(StringUtil.fillSpace(exporterNo + deptCd, OAET001MECConstants.EXPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 10.輸出者名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerName()), OAET001MECConstants.EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 11.郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerPostcode()), OAET001MECConstants.EXPCUSTOMERPOSTCODE,true));
        sb.append("\r\n");

        // 12.住所1(都道府県)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd1()), OAET001MECConstants.EXPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 13.住所2(市区町村)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd2()), OAET001MECConstants.EXPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 14.住所3(町域名・番地)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd3()), OAET001MECConstants.EXPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 15.住所4(ビル名他)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd4()), OAET001MECConstants.EXPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 16.輸出者電話番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerTelNo()).replaceAll("-",""), OAET001MECConstants.EXPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 17.輸出者住所
        if(StringUtil.isStringEmpty(data.getExpCustomerAdd1()) && StringUtil.isStringEmpty(data.getExpCustomerAdd2())
                && StringUtil.isStringEmpty(data.getExpCustomerAdd3()) && StringUtil.isStringEmpty(data.getExpCustomerAdd4())){
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAddLump()), OAET001MECConstants.EXPCUSTOMERADD_LUMP,true));
        } else {
            sb.append(StringUtil.fillSpace("", OAET001MECConstants.EXPCUSTOMERADD_LUMP,true));
        }
        sb.append("\r\n");

        // 18.税関事務管理人コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorCd()), OAET001MECConstants.CUSTOMSOFFICEJANITORCD,true));
        sb.append("\r\n");

        // 19.税関事務管理人受理番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorReNo()), OAET001MECConstants.CUSTOMSOFFICEJANITORRENO,true));
        sb.append("\r\n");

        // 20.税関事務管理人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorName()), OAET001MECConstants.CUSTOMSOFFICEJANITORNAME,true));
        sb.append("\r\n");

        // 21.通関予定蔵置場コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBondedWareHouseCd()), OAET001MECConstants.BONDEDWAREHOUSECD,true));
        sb.append("\r\n");

        // 22.検査立会者
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInspectionWitness()), OAET001MECConstants.INSPECTIONWITNESS,true));
        sb.append("\r\n");

        // 23.仕向人コード
        sb.append(StringUtil.fillSpace("", OAET001MECConstants.CONSIGNEENO,true));
        sb.append("\r\n");

        // 24.仕向人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeName()), OAET001MECConstants.CONSIGNEENAME,true));
        sb.append("\r\n");

        // 25.住所1
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd1()), OAET001MECConstants.CONSIGNEEADD_1,true));
        sb.append("\r\n");

        // 26.住所2
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd2()), OAET001MECConstants.CONSIGNEEADD_2,true));
        sb.append("\r\n");

        // 27.住所3
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd3()), OAET001MECConstants.CONSIGNEEADD_3,true));
        sb.append("\r\n");

        // 28.住所4
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd4()), OAET001MECConstants.CONSIGNEEADD_4,true));
        sb.append("\r\n");

        // 29.郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneePostcode()), OAET001MECConstants.CONSIGNEEPOSTCODE,true));
        sb.append("\r\n");

        // 30.国名コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeCountry()), OAET001MECConstants.CONSIGNEECOUNTRY,true));
        sb.append("\r\n");

        // 31.仕向人住所
        if(StringUtil.isStringEmpty(data.getConsigneeAdd1()) && StringUtil.isStringEmpty(data.getConsigneeAdd2())
                && StringUtil.isStringEmpty(data.getConsigneeAdd3()) && StringUtil.isStringEmpty(data.getConsigneeAdd4())){
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAddLump()), OAET001MECConstants.CONSIGNEEADD_LUMP,true));
        } else {
            sb.append(StringUtil.fillSpace("", OAET001MECConstants.CONSIGNEEADD_LUMP,true));
        }
        sb.append("\r\n");

        // 32.HAWB番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAET001MECConstants.CWBNO,true));
        sb.append("\r\n");

        // 33.貨物個数
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingPiece()), OAET001MECConstants.CARRYINGPIECE,false));
        sb.append("\r\n");

        // 34.貨物重量
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingWeight()), OAET001MECConstants.CARRYINGWEIGHT,false));
        sb.append("\r\n");

        // 35.最終仕向地コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLastDestinationCd()), OAET001MECConstants.LASTDESTINATIONCD,true));
        sb.append("\r\n");

        // 36.積込港コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDepPort()), OAET001MECConstants.DEPPORT,true));
        sb.append("\r\n");

        // 37.FOB通貨コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobCurrencyCd()), OAET001MECConstants.FOBCURRENCYCD,true));
        sb.append("\r\n");

        // 38.FOB価格
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobAmo()), OAET001MECConstants.FOBAMO,false));
        sb.append("\r\n");

        // 39.申告価格
        sb.append(StringUtil.fillSpace("", OAET001MECConstants.REPORTVALUE,true));
        sb.append("\r\n");

        // 40.品名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getItem()), OAET001MECConstants.ITEM,true));
        sb.append("\r\n");

        // 41.記事
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNews2()), OAET001MECConstants.NEWS_2,true));
        sb.append("\r\n");

        // 42.荷主セクションコード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersSecCd()), OAET001MECConstants.SHIPPERSSECCD,true));
        sb.append("\r\n");

        // 43.荷主リファレンスナンバー
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersRefNo()), OAET001MECConstants.SHIPPERSREFNO,true));
        sb.append("\r\n");

        // 44.社内整理用番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInHouseRefNumber()), OAET001MECConstants.INHOUSEREFNUMBER,true));
        sb.append("\r\n");


        return sb;
    }


    private String createFolder(Object param) {
        String folderPath = reportService.getReportPath("MEC");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAET001SearchDto) param).getAwbNo();
        String path = folderPath + File.separator+ "MEC"+ date +"_" + awbNo;
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                log.error(e.getMessage());
            }
        }
        return path;
    }




}
