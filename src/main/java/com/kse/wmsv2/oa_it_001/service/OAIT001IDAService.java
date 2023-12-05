package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001IDAConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDADao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDAReportDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDASubDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReportReturnDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001ReportMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
@Service
public class OAIT001IDAService extends OAIT001CommonConstants{

    @Autowired
    OAIT001ReportMapper reportMapper;

    @Autowired
    ReportService reportServ;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    StatusService stsServ;

    @Autowired
    ReportService reportService;


    @Transactional
    public Map<String,Object> createIDAReport(HttpHeaders headers, Object param) throws Exception{
        // SELECT用
        Map<String, String> parameterMap = new HashMap<>();
        parameterMap = getParameterMap(param);

        // リターン値
        Map<String,Object> returnMap = new HashMap<>();
        HttpHeaders returnHeaders = new HttpHeaders();
        byte[] zipFile = new byte[0];

        // S3処理用
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();

        // 共通処理用
        String userCd = commonService.getUserCd(headers);
        String date = DateUtil.getNow("MMdd_HHmmss");

        // 保存用
        String folderPath = createFolder(param);

        // ステータス
        String dateSts = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        COMMONStatusDto statusDao = new COMMONStatusDto();
        statusDao.setUserCd(userCd);
        statusDao.setDate(dateSts);
        statusDao.setCustomStatusCd(STATUS_CD_IDA_REPORT);
        statusDao.setLinkDataClass("0");
        statusDao.setBusinessClass("01");
        statusDao.setAwbNo(parameterMap.get("awbNo"));

        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", parameterMap.get("awbNo"));
        updateParamMap.put("statusCd", STATUS_CD_IDA_REPORT);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);

        try{



            // S3PATH
            s3Path = reportService.getS3Path("IDA");
            List<OAIT001IDAReportDao> idaMainList = new ArrayList<>();
            idaMainList = getIDAData(parameterMap);
            if(idaMainList.size() == 0){
                returnMap.put("msg",MSG_ERR_351);
            }

            for(int i = 0; i < idaMainList.size(); i++){
                // DetailList
                List<OAIT001IDASubDao> refList = reportMapper.getIDARepData(parameterMap);
                int refSize = refList.size() == 0 ? 1 : refList.size();
                // 共通項目作成
                String commonColumn = "";
                commonColumn = reportServ.createCommonColumn(headers,"IDA","AID");
                String format = "%0" + COMMON_LENCABLE + "d";
                int totalLen = COMMON_LENCOMMONCABLE + OAIT001IDAConstants.IDA_LENCOMMON + (OAIT001IDAConstants.IDA_LENEXDATAREP * refSize);
                commonColumn = commonColumn + String.format(format, totalLen);

                StringBuffer sbMain = new StringBuffer();
                StringBuffer sbRef = new StringBuffer();
                sbMain = writeReportMain(headers,idaMainList.get(i),commonColumn);

                int refLen = refList.size() > 98 ? 98 : refList.size()  ;
                if(refLen == 0 ){
                    sbRef.append(writeReportRef(null));
                } else {
                    for(int j = 0; j < refLen; j++){
                        sbRef.append(writeReportRef(refList.get(j)));
                    }
                }

                // ヘッダ+REF内容
                sbMain.append(sbRef);

                // ファイル作成
                String fileName = reportServ.getReportName("IDA",idaMainList.get(i).getCwbNo(),idaMainList.get(i).getCwbNoDeptCd(),String.format("%04d", i+1),"");
                String filePath = reportServ.getReportFilePath(folderPath,fileName);
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath),"Shift-JIS"));
                bf.write(sbMain.toString());
                bf.flush();
                bf.close();

                // S3保存
                if(StringUtil.isStringEmpty(s3Path) && !reportService.saveReportS3(s3Path,fileName,sbMain.toString())){
                    throw new Exception("S3 Bucket Error");
                }

                updateParamMap.put("cwbNo", idaMainList.get(i).getCwbNo());
                updateParamMap.put("cwbNoDeptCd", idaMainList.get(i).getCwbNoDeptCd());
                int updateResult = reportMapper.updateMasterStatus(updateParamMap);
                if (updateResult == 0) {
                    throw new Exception(MSG_ERR_004);
                }

                // ステータス更新
                statusDao.setCwbNoDeptCd(idaMainList.get(i).getCwbNoDeptCd());
                statusDao.setBwbNo(idaMainList.get(i).getBwbNo());
                statusDao.setCwbNo(idaMainList.get(i).getCwbNo());
                stsServ.updateStatusMaster(statusDao);
            }

            // 作成したファイルをZipに保存
            String zipName = "IDA_" + userCd + "_" + date + ".zip";
            zipFile = commonService.makeZipFile(folderPath,headers,"IDA",zipName);
            returnHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            returnHeaders.setContentLength(zipFile.length);
            returnHeaders.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(zipName)
                    .build());

            // 結果保存
            returnMap.put("zipFile",zipFile);
            returnMap.put("headers",returnHeaders);
            returnMap.put("result","false");
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();

            // エラー結果保存
//            returnMap.put("zipFile",null);
//            returnMap.put("headers",null);
//            returnMap.put("msg",MSG_ERR_308);
//            returnMap.put("result","error");

            // S3ファイル削除
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
            // 臨時フォルダ・ファイル削除
            if(!StringUtil.isStringEmpty(folderPath)){
                commonService.deleteFolder(folderPath);
            }
            throw new Exception(e.getMessage());
        }
        return returnMap;
    }



    private Map getParameterMap(Object param){
        Map<String, String> parameterMap = new HashMap<>();
        if(param instanceof OAIT001IDADao){
            parameterMap.put("awbNo", ((OAIT001IDADao) param).getMainDao().getAwbNo());
            parameterMap.put("cwbNo", ((OAIT001IDADao) param).getMainDao().getCwbNo());
            parameterMap.put("cwbNoDeptCd", ((OAIT001IDADao) param).getMainDao().getCwbNoDeptCd());
        } else if(param instanceof  OAIT001IDAReportDao) {
            parameterMap.put("awbNo", ((OAIT001IDAReportDao) param).getAwbNo());
            parameterMap.put("cwbNo", ((OAIT001IDAReportDao) param).getCwbNo());
            parameterMap.put("cwbNoDeptCd", ((OAIT001IDAReportDao) param).getCwbNoDeptCd());
        } else {
            parameterMap.put("awbNo", ((OAIT001SearchDto) param).getAwbNo());
            parameterMap.put("cwbNo", "");
            parameterMap.put("cwbNoDeptCd", "");
        }
        return parameterMap;
    }


    private List<OAIT001IDAReportDao> getIDAData(Map paramMap){
        return reportMapper.getIDAData(paramMap);
    }

    private StringBuffer writeReportMain(HttpHeaders headers,OAIT001IDAReportDao oait001IDAReportDao,String commonColumn) {
        StringBuffer sb = new StringBuffer();

        // 1.共通項目
        sb.append(commonColumn);
        sb.append("\r\n");

        // 2.申告番号
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_REPORTNO,true));
        sb.append("\r\n");

        // 3.大額・少額区分
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getBigSmallFlg(), OAIT001IDAConstants.IDA_BIGSMALLFLG,true));
        sb.append("\r\n");

        // 4.申告種別コード１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getReportKindCD1(), OAIT001IDAConstants.IDA_REPORTKINDCD_1,true));
        sb.append("\r\n");

        // 5.申告種別コード２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getReportKindCD2(), OAIT001IDAConstants.IDA_REPORTKINDCD_2,true));
        sb.append("\r\n");

        // 6.申告貨物識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCargoDisc(), OAIT001IDAConstants.IDA_CARGODISC,true));
        sb.append("\r\n");

        // 7.識別符号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getDiscernmentMark(), OAIT001IDAConstants.IDA_DISCERNMENTMARK,true));
        sb.append("\r\n");

        // 8.申告官署コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getReportDivCD(), OAIT001IDAConstants.IDA_REPORTDIVCD,true));
        sb.append("\r\n");

        // 9.申告部門コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getReportDepCD(), OAIT001IDAConstants.IDA_REPORTDEPCD,true));
        sb.append("\r\n");

        // 10.特例宛先官署コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExceptionalDivCd(), OAIT001IDAConstants.IDA_EXCEPTIONALDIVCD,true));
        sb.append("\r\n");

        // 11.特例宛先部門コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExceptionalDepCd(), OAIT001IDAConstants.IDA_EXCEPTIONALDEPCD,true));
        sb.append("\r\n");

        // 12.申告予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(oait001IDAReportDao.getReportPlaningDate()), OAIT001IDAConstants.IDA_REPORTPLANINGDATE,false));
        sb.append("\r\n");

        // 13.輸入者コード
        if(StringUtil.isStringNull(oait001IDAReportDao.getImpCustomerNo()).length() >= 3
                && oait001IDAReportDao.getImpCustomerNo().substring(0,3).equals(OCS)){
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_IMPCUSTOMERNO,true));
        } else {
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerNo(), OAIT001IDAConstants.IDA_IMPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 14.輸入者名
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerName(), OAIT001IDAConstants.IDA_IMPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 15.輸入者郵便番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpPostcode(), OAIT001IDAConstants.IDA_IMPPOSTCODE,true));
        sb.append("\r\n");

        // 16.輸入者住所1(都道府県)
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerAdd1(), OAIT001IDAConstants.IDA_IMPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 17.輸入者住所2(市区町村)
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerAdd2(), OAIT001IDAConstants.IDA_IMPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 18.輸入者住所3(町域名・番地)
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerAdd3(), OAIT001IDAConstants.IDA_IMPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 19.輸入者住所4(ビル名他)
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerAdd4(), OAIT001IDAConstants.IDA_IMPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 20.輸入者電話番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpCustomerTelNo(), OAIT001IDAConstants.IDA_IMPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 21.税関事務管理人コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCustomsOfficeJanitorCd(), OAIT001IDAConstants.IDA_CUSTOMSOFFICEJANITORCD,true));
        sb.append("\r\n");

        // 22.税関事務管理人受理番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCustomsOfficeJanitorReNo(), OAIT001IDAConstants.IDA_CUSTOMSOFFICEJANITORRENO,true));
        sb.append("\r\n");

        // 23.税関事務管理人名
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCustomsOfficeJanitorName(), OAIT001IDAConstants.IDA_CUSTOMSOFFICEJANITORNAME,true));
        sb.append("\r\n");

        // 24.蔵置場所コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCarBondedWareHouse(), OAIT001IDAConstants.IDA_BONDEDWAREHOUSECD,true));
        sb.append("\r\n");

        // 25.一括申告等種別
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_PACKAGEREPORT,true));
        sb.append("\r\n");

        // 26.申告予定者 (コード)
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getReportPersonCD(), OAIT001IDAConstants.IDA_REPORTPERSONCD,true));
        sb.append("\r\n");

        // 27.輸入取引者コード
        if(StringUtil.isStringNull(oait001IDAReportDao.getImpDealingsCD()).length() >= 3
                && oait001IDAReportDao.getImpDealingsCD().substring(0,3).equals(OCS)){
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_IMPDEALINGSCD,true));
        } else {
            String tmp = StringUtil.isStringNull(oait001IDAReportDao.getImpDealingsCD())
                    + StringUtil.isStringNull(oait001IDAReportDao.getImpDealingsDeptCD());
            sb.append(StringUtil.fillSpace(tmp, OAIT001IDAConstants.IDA_IMPDEALINGSCD,true));
        }
        sb.append("\r\n");

        // 28.輸入取引者名称
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpDealingsName(), OAIT001IDAConstants.IDA_IMPDEALINGSNAME,true));
        sb.append("\r\n");

        // 29.輸出者コード
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_EXPCUSTOMERNO,true));
        sb.append("\r\n");

        // 30.輸出者名
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerName(), OAIT001IDAConstants.IDA_EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 31.輸出者住所1
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerAdd1(), OAIT001IDAConstants.IDA_EXPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 32.輸出者住所2
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerAdd2(), OAIT001IDAConstants.IDA_EXPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 33.輸出者住所3
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerAdd3(), OAIT001IDAConstants.IDA_EXPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 34.輸出者住所4
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerAdd4(), OAIT001IDAConstants.IDA_EXPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 35.輸出者郵便番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerPostCode(), OAIT001IDAConstants.IDA_EXPCUSTOMERPOSTCODE,true));
        sb.append("\r\n");

        // 36.輸出者国コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getExpCustomerCountry(), OAIT001IDAConstants.IDA_EXPCUSTOMERCOUNTRY,true));
        sb.append("\r\n");

        // 37.検査立会者
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInspectionWitness(), OAIT001IDAConstants.IDA_INSPECTIONWITNESS,true));
        sb.append("\r\n");

        // 38.AWBNo1
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCwbNo(), OAIT001IDAConstants.IDA_AWBNO_1,true));
        sb.append("\r\n");

        // 39.AWBNo2
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AWBNO_2,true));
        sb.append("\r\n");

        // 40.AWBNo3
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AWBNO_3,true));
        sb.append("\r\n");

        // 41.AWBNo4
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AWBNO_4,true));
        sb.append("\r\n");

        // 42.AWBNo5
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AWBNO_5,true));
        sb.append("\r\n");

        // 43.貨物個数
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCargoPiece(), OAIT001IDAConstants.IDA_CARGOPIECE,false));
        sb.append("\r\n");

        // 44.個数単位コード
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CARGOPIECEUNITCD,true));
        sb.append("\r\n");

        // 45.貨物重量
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCargoWeight(), OAIT001IDAConstants.IDA_CARGOWEIGHT,false));
        sb.append("\r\n");

        // 46.重量単位コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getWeightUnitCD(), OAIT001IDAConstants.IDA_WEIGHTUNITCD,true));
        sb.append("\r\n");

        // 47.記号番号
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_SIGNNO,true));
        sb.append("\r\n");

        // 48.積載船舶コード
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CARRYSHIPCD,true));
        sb.append("\r\n");

        if(StringUtil.isStringNull(oait001IDAReportDao.getCustomsPlaceCd()).equals("W9ANG")){
            // 49.積載船（機）名
            String tmp = oait001IDAReportDao.getCurrentArrFLT1() + "/" + oait001IDAReportDao.getCurrentArrFLT2();
            sb.append(StringUtil.fillSpace(tmp, OAIT001IDAConstants.IDA_CURRENTARRFLT,true));
            sb.append("\r\n");

            // 50.入港年月日
            sb.append(StringUtil.fillSpace(StringUtil.changeReportData(oait001IDAReportDao.getArrPortDate()), OAIT001IDAConstants.IDA_ARRPORTDATE,true));
            sb.append("\r\n");

            // 51.取卸港
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getGetPort(), OAIT001IDAConstants.IDA_GETPORT,true));
            sb.append("\r\n");

        } else {
            // 49.積載船（機）名
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CURRENTARRFLT,true));
            sb.append("\r\n");
            // 50.入港年月日
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ARRPORTDATE,false));
            sb.append("\r\n");
            // 51.取卸港
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_GETPORT,true));
            sb.append("\r\n");
        }

        // 52.積出地コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getShipmentCD(), OAIT001IDAConstants.IDA_SHIPMENTCD,true));
        sb.append("\r\n");

        // 53.積出地名
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getShipmentPlaceName(), OAIT001IDAConstants.IDA_SHIPMENTPLACENAME,true));
        sb.append("\r\n");

        // 54.貿易形態別符号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getTradeTypeMark(), OAIT001IDAConstants.IDA_TRADETYPEMARK,true));
        sb.append("\r\n");

        // 55.コンテナ扱い本数
        sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CONTAINERPIECE,false));
        sb.append("\r\n");

        // 56.戻税申告識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getBackTaxReportDisc(), OAIT001IDAConstants.IDA_BACKTAXREPORTDISC,true));
        sb.append("\r\n");

        // 57.輸入貿易管理令第３条等識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpTradeCont3Disc(), OAIT001IDAConstants.IDA_IMPTRADECONT3DISC,true));
        sb.append("\r\n");

        // 58.輸入承認証添付識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getImpRecoAttachDisc(), OAIT001IDAConstants.IDA_IMPRECOATTACHDISC,true));
        sb.append("\r\n");

        // 59.内容点検結果その他
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getContCheckReOther(), OAIT001IDAConstants.IDA_CONTCHECKREOTHER,true));
        sb.append("\r\n");

        // 60.税関調査用符号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getCustomsExamMark(), OAIT001IDAConstants.IDA_CUSTOMSEXAMMARK,true));
        sb.append("\r\n");

        // 61.他方令１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLaw1(), OAIT001IDAConstants.IDA_OTHERLAW_1,true));
        sb.append("\r\n");

        // 62.他方令２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLaw2(), OAIT001IDAConstants.IDA_OTHERLAW_2,true));
        sb.append("\r\n");

        // 63.他方令3
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLaw3(), OAIT001IDAConstants.IDA_OTHERLAW_3,true));
        sb.append("\r\n");

        // 64.他方令4
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLaw4(), OAIT001IDAConstants.IDA_OTHERLAW_4,true));
        sb.append("\r\n");

        // 65.他方令5
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLaw5(), OAIT001IDAConstants.IDA_OTHERLAW_5,true));
        sb.append("\r\n");

        // 66.他省庁共通管理番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherMiniContNo(), OAIT001IDAConstants.IDA_OTHERMINICONTNO,true));
        sb.append("\r\n");

        // 67.食品衛生証明識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getFhProofDisc(), OAIT001IDAConstants.IDA_FHPROOFDISC,true));
        sb.append("\r\n");

        // 68.植物防疫証明識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getPlantPEDisc(), OAIT001IDAConstants.IDA_PLANTPEDISC,true));
        sb.append("\r\n");

        // 69.動物検疫証明識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getAnimalQuaraDisc(), OAIT001IDAConstants.IDA_ANIMALQUARADISC,true));
        sb.append("\r\n");

        // 70.他方令承認等識別１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc1(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_1,true));
        sb.append("\r\n");

        // 71.他方令承認等番号１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo1(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_1,true));
        sb.append("\r\n");

        // 72.他方令承認等識別2
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc2(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_2,true));
        sb.append("\r\n");

        // 73.他方令承認等番号2
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo2(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_2,true));
        sb.append("\r\n");

        // 74.他方令承認等識別3
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc3(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_3,true));
        sb.append("\r\n");

        // 75.他方令承認等番号3
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo3(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_3,true));
        sb.append("\r\n");

        // 76.他方令承認等識別4
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc4(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_4,true));
        sb.append("\r\n");

        // 77.他方令承認等番号4
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo4(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_4,true));
        sb.append("\r\n");

        // 78.他方令承認等識別5
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc5(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_5,true));
        sb.append("\r\n");

        // 79.他方令承認等番号5
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo5(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_5,true));
        sb.append("\r\n");

        // 80.他方令承認等識別6
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc6(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_6,true));
        sb.append("\r\n");

        // 81.他方令承認等番号6
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo6(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_6,true));
        sb.append("\r\n");

        // 82.他方令承認等識別7
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc7(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_7,true));
        sb.append("\r\n");

        // 83.他方令承認等番号7
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo7(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_7,true));
        sb.append("\r\n");

        // 84.他方令承認等識別8
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc8(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_8,true));
        sb.append("\r\n");

        // 85.他方令承認等番号8
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo8(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_8,true));
        sb.append("\r\n");

        // 86.他方令承認等識別9
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc9(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_9,true));
        sb.append("\r\n");

        // 87.他方令承認等番号9
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo9(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_9,true));
        sb.append("\r\n");

        // 88.他方令承認等識別10
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecDisc10(), OAIT001IDAConstants.IDA_OTHERLAWRECDISC_10,true));
        sb.append("\r\n");

        // 89.他方令承認等番号10
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getOtherLawRecNo10(), OAIT001IDAConstants.IDA_OTHERLAWRECNO_10,true));
        sb.append("\r\n");

        // 90.インボイス識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceDiscernment(), OAIT001IDAConstants.IDA_INVOICEDISCERNMENT,true));
        sb.append("\r\n");

        // 91.電子インボイス受付番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceReceiptNo(), OAIT001IDAConstants.IDA_INVOICERECEIPTNO,true));
        sb.append("\r\n");

        // 92.インボイス番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceNo(), OAIT001IDAConstants.IDA_INVOICENO,true));
        sb.append("\r\n");

        // 93.インボイス価格区分コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceValClassCD(), OAIT001IDAConstants.IDA_INVOICEVALCLASSCD,true));
        sb.append("\r\n");

        // 94.インボイス価格条件コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceValConCD(), OAIT001IDAConstants.IDA_INVOICEVALCONCD,true));
        sb.append("\r\n");

        // 95.インボイス通貨コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceCurCD(), OAIT001IDAConstants.IDA_INVOICECURCD,true));
        sb.append("\r\n");

        // 96.インボイス価格
        boolean curFlg = oait001IDAReportDao.getInvoiceCurCD().equals("JPY") ? true : false;
        if(curFlg && oait001IDAReportDao.getInvoiceValue().contains(".")){
            String tempValue =oait001IDAReportDao.getInvoiceValue();
            sb.append(StringUtil.fillSpace(tempValue.substring(0,tempValue.indexOf(".")), OAIT001IDAConstants.IDA_INVOICEVALUE,false));
        } else {
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInvoiceValue(), OAIT001IDAConstants.IDA_INVOICEVALUE,false));
        }
        sb.append("\r\n");


        // 97.運賃区分
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getFareFlg(), OAIT001IDAConstants.IDA_FAREFLG,true));
        sb.append("\r\n");

        // 98.運賃通貨コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getFareCurrencyCD(), OAIT001IDAConstants.IDA_FARECURRENCYCD,true));
        sb.append("\r\n");

        // 99.運賃
        if(StringUtil.isStringNull(oait001IDAReportDao.getFareCurrencyCD()).equals("JPY")
                && oait001IDAReportDao.getFare().contains(".")){

            String tmp = oait001IDAReportDao.getFare();
            sb.append(StringUtil.fillSpace(tmp.substring(0,tmp.indexOf(".")), OAIT001IDAConstants.IDA_FARE,false));
        } else {
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getFare(), OAIT001IDAConstants.IDA_FARE,false));
        }
        sb.append("\r\n");

        // 100.保険区分コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInsuranceClassCD(), OAIT001IDAConstants.IDA_INSURANCECLASSCD,true));
        sb.append("\r\n");

        // 101.保険通貨コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInsuranceCurCD(), OAIT001IDAConstants.IDA_INSURANCECURCD,true));
        sb.append("\r\n");

        // 102.保険金額
        if(StringUtil.isStringNull(oait001IDAReportDao.getInsuranceCurCD()).equals("JPY")
                && oait001IDAReportDao.getInsuranceAmount().contains(".")){
            String tmp = oait001IDAReportDao.getInsuranceAmount();
            sb.append(StringUtil.fillSpace(tmp.substring(0,tmp.indexOf(".")), OAIT001IDAConstants.IDA_INSURANCEAMOUNT,false));
        } else {
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInsuranceAmount(), OAIT001IDAConstants.IDA_INSURANCEAMOUNT,false));
        }
        sb.append("\r\n");

        // 103.包括保険登録番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncInsuRegNo(), OAIT001IDAConstants.IDA_INCINSUREGNO,true));
        sb.append("\r\n");

        // 104.評価区分コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getEstimationFlgCD(), OAIT001IDAConstants.IDA_ESTIMATIONFLGCD,true));
        sb.append("\r\n");

        // 105.包括評価申告受理番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncEstRepReNo(), OAIT001IDAConstants.IDA_INCESTREPRENO,true));
        sb.append("\r\n");

        // 106.包括評価申告受理番号２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncEstRepReNo2(), OAIT001IDAConstants.IDA_INCESTREPRENO_2,true));
        sb.append("\r\n");

        // 107.包括評価申告受理番号３
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncEstRepReNo3(), OAIT001IDAConstants.IDA_INCESTREPRENO_3,true));
        sb.append("\r\n");

        // 108.評価補正区分コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncReviseFlgCD(), OAIT001IDAConstants.IDA_INCREVISEFLGCD,true));
        sb.append("\r\n");

        // 109.評価補正基礎額通貨コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncRevBaseCurCD(), OAIT001IDAConstants.IDA_INCREVBASECURCD,true));
        sb.append("\r\n");

        // 110.評価補正基礎額
        if(StringUtil.isStringNull(oait001IDAReportDao.getIncRevBaseCurCD()).equals("JPY")
                && oait001IDAReportDao.getIncRevBaseAmo().contains(".")){
            String tmpStr = oait001IDAReportDao.getIncRevBaseAmo();
            sb.append(StringUtil.fillSpace(tmpStr.substring(0,tmpStr.indexOf(".")), OAIT001IDAConstants.IDA_INCREVBASEAMO,false));
        } else {
            sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncRevBaseAmo(), OAIT001IDAConstants.IDA_INCREVBASEAMO,false));
        }
        sb.append("\r\n");

        // 111.評価補正補正式
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getIncRevBase(), OAIT001IDAConstants.IDA_INCREVBASE,true));
        sb.append("\r\n");

        // 112.事前教示（評価）１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getAdvanceRulingValuation1(), OAIT001IDAConstants.IDA_ADVANCERULINGVALUATION_1,true));
        sb.append("\r\n");

        // 113.事前教示（評価）２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getAdvanceRulingValuation2(), OAIT001IDAConstants.IDA_ADVANCERULINGVALUATION_2,true));
        sb.append("\r\n");

        // 114.課税価格按分係数合計
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getTaxablesAmoPdivTo(), OAIT001IDAConstants.IDA_TAXABLESAMOPDIVTO,false));
        sb.append("\r\n");

        // 115.最初蔵入等承認年月日
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getFirstCarBondedDate(), OAIT001IDAConstants.IDA_FIRSTCARBONDEDDATE,false));
        sb.append("\r\n");

        // 116.蔵入等先保税地域コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getLongKeepBondedWarehouse(), OAIT001IDAConstants.IDA_LONGKEEPBONDEDWAREHOUSE,true));
        sb.append("\r\n");

        // 117.納期限延長コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getDeliveryDateExtCD(), OAIT001IDAConstants.IDA_DELIVERYDATEEXTCD,true));
        sb.append("\r\n");

        // 118.BP申請事由コード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getBpApplicationCD(), OAIT001IDAConstants.IDA_BPAPPLICATIONCD,true));
        sb.append("\r\n");

        // 119.納付方法識別
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getPayMethodDisc(), OAIT001IDAConstants.IDA_PAYMETHODDISC,true));
        sb.append("\r\n");

        // 120.口座番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getAccountNo(), OAIT001IDAConstants.IDA_ACCOUNTNO,true));
        sb.append("\r\n");

        // 121.担保登録番号１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getSecurityRegNo1(), OAIT001IDAConstants.IDA_SECURITYREGNO_1,true));
        sb.append("\r\n");

        // 122.担保登録番号２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getSecurityRegNo2(), OAIT001IDAConstants.IDA_SECURITYREGNO_2,true));
        sb.append("\r\n");

        // 123.記事１
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getNews1(), OAIT001IDAConstants.IDA_NEWS_1,true));
        sb.append("\r\n");

        // 124.記事２
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getNews2(), OAIT001IDAConstants.IDA_NEWS_2,true));
        sb.append("\r\n");

        // 125.記事（荷主用）
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getNewsShipper(), OAIT001IDAConstants.IDA_NEWSSHIPPER,true));
        sb.append("\r\n");

        // 126.荷主セクションコード
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getShippersSecCd(), OAIT001IDAConstants.IDA_SHIPPERSSECCD,true));
        sb.append("\r\n");

        // 127.荷主リファレンスナンバー
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getShippersRefNo(), OAIT001IDAConstants.IDA_SHIPPERSREFNO,true));
        sb.append("\r\n");

        // 128.社内整理番号
        sb.append(StringUtil.fillSpace(oait001IDAReportDao.getInHouseRefNumber(), OAIT001IDAConstants.IDA_INHOUSEREFNUMBER,true));
        sb.append("\r\n");

        return sb;
    }

    private StringBuffer writeReportRef(OAIT001IDASubDao data) {
        StringBuffer sb = new StringBuffer();
        if(data == null){
            // 1.品目コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ITEMCD,true));
            sb.append("\r\n");

            // 2.NACCS用コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_NACCSCD,true));
            sb.append("\r\n");

            // 3.品名
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ITEM,true));
            sb.append("\r\n");

            // 4.原産地コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_COUNTRYOFORIGINCD,true));
            sb.append("\r\n");

            // 5.原産地証明書識別
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ORIGINCERTIFIDISC,true));
            sb.append("\r\n");

            // 6.数量１
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AMO_1,false));
            sb.append("\r\n");

            // 7.数量単位コード１
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AMOUNIT_1,true));
            sb.append("\r\n");

            // 8.数量２
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AMO_2,false));
            sb.append("\r\n");

            // 9.数量単位コード２
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_AMOUNIT_2,true));
            sb.append("\r\n");

            // 10.輸入貿易管理令別表コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_IMPTRADECONTCD,true));
            sb.append("\r\n");

            // 11.蔵置種別コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_WAREHOUSECD,true));
            sb.append("\r\n");

            // 12.課税価格按分係数
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_TAXABLESAMOPDIVTO,false));
            sb.append("\r\n");

            // 13.運賃按分識別
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_FAREDIVDISC,true));
            sb.append("\r\n");

            // 14.FOB通貨コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_FOBCURRENCYCD,true));
            sb.append("\r\n");

            // 15.課税価格
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_TAXABLESAMO,false));
            sb.append("\r\n");

            // 16.事前教示（分類）
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ADVANCERULINGCLASSIFICATION,true));
            sb.append("\r\n");

            // 17.事前教示（原産地）
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_ADVANCERULINGORIGIN,true));
            sb.append("\r\n");

            // 18.関税減免税コード
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CUSTOMSEXEMPTCD,true));
            sb.append("\r\n");

            // 19.関税減免税額
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_CUSTOMSEXEMPTAMO,false));
            sb.append("\r\n");

            // 20.内国消費税等種別コード１
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_1,true));
            sb.append("\r\n");

            // 21.内国消費税等減免税コード１
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_1,true));
            sb.append("\r\n");

            // 22.内国消費税等減免額１
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_1,false));
            sb.append("\r\n");

            // 23.内国消費税等種別コード2
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_2,true));
            sb.append("\r\n");

            // 24.内国消費税等減免税コード2
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_2,true));
            sb.append("\r\n");

            // 25.内国消費税等減免額2
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_2,false));
            sb.append("\r\n");

            // 26.内国消費税等種別コード3
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_3,true));
            sb.append("\r\n");

            // 27.内国消費税等減免税コード3
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_3,true));
            sb.append("\r\n");

            // 28.内国消費税等減免額3
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_3,false));
            sb.append("\r\n");

            // 29.内国消費税等種別コード4
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_4,true));
            sb.append("\r\n");

            // 30.内国消費税等減免税コード4
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_4,true));
            sb.append("\r\n");

            // 31.内国消費税等減免額4
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_4,false));
            sb.append("\r\n");

            // 32.内国消費税等種別コード5
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_5,true));
            sb.append("\r\n");

            // 33.内国消費税等減免税コード5
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_5,true));
            sb.append("\r\n");

            // 34.内国消費税等減免額5
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_5,false));
            sb.append("\r\n");

            // 35.内国消費税等種別コード6
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXKINDCD_6,true));
            sb.append("\r\n");

            // 36.内国消費税等減免税コード6
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_6,true));
            sb.append("\r\n");

            // 37.内国消費税等減免額6
            sb.append(StringUtil.fillSpace("", OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_6,false));
            sb.append("\r\n");

        } else {
            // 1.品目コード
            sb.append(StringUtil.fillSpace(data.getItemCD(), OAIT001IDAConstants.IDA_ITEMCD,true));
            sb.append("\r\n");

            // 2.NACCS用コード
            sb.append(StringUtil.fillSpace(data.getNaccsCD(), OAIT001IDAConstants.IDA_NACCSCD,true));
            sb.append("\r\n");

            // 3.品名
            sb.append(StringUtil.fillSpace(data.getItem(), OAIT001IDAConstants.IDA_ITEM,true));
            sb.append("\r\n");

            // 4.原産地コード
            sb.append(StringUtil.fillSpace(data.getCountryOfOriginCD(), OAIT001IDAConstants.IDA_COUNTRYOFORIGINCD,true));
            sb.append("\r\n");

            // 5.原産地証明書識別
            sb.append(StringUtil.fillSpace(data.getOriginCertifiDisc(), OAIT001IDAConstants.IDA_ORIGINCERTIFIDISC,true));
            sb.append("\r\n");

            // 6.数量１
            sb.append(StringUtil.fillSpace(data.getAmo1(), OAIT001IDAConstants.IDA_AMO_1,false));
            sb.append("\r\n");

            // 7.数量単位コード１
            sb.append(StringUtil.fillSpace(data.getAmoUnit1(), OAIT001IDAConstants.IDA_AMOUNIT_1,true));
            sb.append("\r\n");

            // 8.数量２
            sb.append(StringUtil.fillSpace(data.getAmo2(), OAIT001IDAConstants.IDA_AMO_2,false));
            sb.append("\r\n");

            // 9.数量単位コード２
            sb.append(StringUtil.fillSpace(data.getAmoUnit2(), OAIT001IDAConstants.IDA_AMOUNIT_2,true));
            sb.append("\r\n");

            // 10.輸入貿易管理令別表コード
            sb.append(StringUtil.fillSpace(data.getImpTradeContCD(), OAIT001IDAConstants.IDA_IMPTRADECONTCD,true));
            sb.append("\r\n");

            // 11.蔵置種別コード
            sb.append(StringUtil.fillSpace(data.getWareHouseCd(), OAIT001IDAConstants.IDA_WAREHOUSECD,true));
            sb.append("\r\n");

            // 12.課税価格按分係数
            sb.append(StringUtil.fillSpace(data.getTaxablesAmoPdivTo(), OAIT001IDAConstants.IDA_TAXABLESAMOPDIVTO_M,false));
            sb.append("\r\n");

            // 13.運賃按分識別
            sb.append(StringUtil.fillSpace(data.getFareDivDisc(), OAIT001IDAConstants.IDA_FAREDIVDISC,true));
            sb.append("\r\n");

            // 14.FOB通貨コード
            sb.append(StringUtil.fillSpace(data.getFobCurrencyCD(), OAIT001IDAConstants.IDA_FOBCURRENCYCD,true));
            sb.append("\r\n");

            // 15.課税価格
            if(data.getFobCurrencyCD() != null
                    && data.getFobCurrencyCD().equals("JPY")
                    && data.getTaxablesAmo().contains(".")){
                String tmpStr = data.getTaxablesAmo();
                sb.append(StringUtil.fillSpace(tmpStr.substring(0,tmpStr.indexOf(".")), OAIT001IDAConstants.IDA_TAXABLESAMO,false));
            } else {
                sb.append(StringUtil.fillSpace(data.getTaxablesAmo(), OAIT001IDAConstants.IDA_TAXABLESAMO,false));
            }
            sb.append("\r\n");

            // 16.事前教示（分類）
            sb.append(StringUtil.fillSpace(data.getAdvanceRulingClassification(), OAIT001IDAConstants.IDA_ADVANCERULINGCLASSIFICATION,true));
            sb.append("\r\n");

            // 17.事前教示（原産地）
            sb.append(StringUtil.fillSpace(data.getAdvanceRulingOrigin(), OAIT001IDAConstants.IDA_ADVANCERULINGORIGIN,true));
            sb.append("\r\n");

            // 18.関税減免税コード
            sb.append(StringUtil.fillSpace(data.getCustomsExemptCD(), OAIT001IDAConstants.IDA_CUSTOMSEXEMPTCD,true));
            sb.append("\r\n");

            // 19.関税減免税額
            sb.append(StringUtil.fillSpace(data.getCustomsExemptAmo(), OAIT001IDAConstants.IDA_CUSTOMSEXEMPTAMO,false));
            sb.append("\r\n");

            // 20.内国消費税等種別コード１
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD1(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_1,true));
            sb.append("\r\n");

            // 21.内国消費税等減免税コード１
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD1(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_1,true));
            sb.append("\r\n");

            // 22.内国消費税等減免額１
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo1(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_1,false));
            sb.append("\r\n");

            // 23.内国消費税等種別コード2
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD2(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_2,true));
            sb.append("\r\n");

            // 24.内国消費税等減免税コード2
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD2(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_2,true));
            sb.append("\r\n");

            // 25.内国消費税等減免額2
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo2(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_2,false));
            sb.append("\r\n");

            // 26.内国消費税等種別コード3
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD3(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_3,true));
            sb.append("\r\n");

            // 27.内国消費税等減免税コード3
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD3(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_3,true));
            sb.append("\r\n");

            // 28.内国消費税等減免額3
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo3(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_3,false));
            sb.append("\r\n");

            // 29.内国消費税等種別コード4
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD4(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_4,true));
            sb.append("\r\n");

            // 30.内国消費税等減免税コード4
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD4(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_4,true));
            sb.append("\r\n");

            // 31.内国消費税等減免額4
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo4(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_4,false));
            sb.append("\r\n");

            // 32.内国消費税等種別コード5
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD5(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_5,true));
            sb.append("\r\n");

            // 33.内国消費税等減免税コード5
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD5(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_5,true));
            sb.append("\r\n");

            // 34.内国消費税等減免額5
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo5(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_5,false));
            sb.append("\r\n");

            // 35.内国消費税等種別コード6
            sb.append(StringUtil.fillSpace(data.getInConsTaxKindCD6(), OAIT001IDAConstants.IDA_INCONSTAXKINDCD_6,true));
            sb.append("\r\n");

            // 36.内国消費税等減免税コード6
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemCD6(), OAIT001IDAConstants.IDA_INCONSTAXEXEMCD_6,true));
            sb.append("\r\n");

            // 37.内国消費税等減免額6
            sb.append(StringUtil.fillSpace(data.getInConsTaxExemAmo6(), OAIT001IDAConstants.IDA_INCONSTAXEXEMAMO_6,false));
            sb.append("\r\n");
        }
        return sb;
    }



    private String createFolder(Object param) {
        String folderPath = reportServ.getReportPath("IDA");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAIT001IDADao) param).getMainDao().getAwbNo();
        String path = folderPath + File.separator + "IDA"+ date +"_" + awbNo;
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
