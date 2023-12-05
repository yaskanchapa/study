package com.kse.wmsv2.oa_it_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.common.OAIT001CommonConstants;
import com.kse.wmsv2.oa_it_001.common.OAIT001MICConstants;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICReportDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ReportResultDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001SearchDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001ReportMapper;
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
public class OAIT001MICService extends OAIT001CommonConstants{

    @Autowired
    OAIT001ReportMapper mapper;

    @Autowired
    OAIT001CommonService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAIT001ReportResultDto writeMIC(HttpHeaders headers,Object param){
        OAIT001ReportResultDto returnDto = new OAIT001ReportResultDto();
        List<String> folderList = new ArrayList<>();
        List<String> s3FolderList = new ArrayList<>();

        Map<String, String> paramMap = new HashMap<>();
        paramMap = getParameterMap(param,headers);
        int fileCnt = 0;
        int folderCnt = 0;
        String s3Path = "";
        List<String> fileNameList = new ArrayList<>();
        String folder = "";
        // ステータス
        COMMONStatusDto statusDao = new COMMONStatusDto();
        String userCd = commonService.getUserCd(headers);
        String date = com.kse.wmsv2.common.util.DateUtil.getTimeStampNow();
        statusDao.setUserCd(userCd);
        statusDao.setDate(date);
        statusDao.setCustomStatusCd(STATUS_CD_MIC_REPORT);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA_CLASS);
        statusDao.setBusinessClass(BUSINESS_CLASS_IMPORT);
        statusDao.setAwbNo(paramMap.get("awbNo"));

        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", paramMap.get("awbNo"));
        updateParamMap.put("statusCd", STATUS_CD_MIC_REPORT);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);


        try{
            // S3パース
            s3Path = reportService.getS3Path("MIC");
            s3FolderList.add(s3Path);

            List<Map<String,String>> micList = new ArrayList<>();
            micList = mapper.getMICDataList(paramMap);
            Map<String,String> keyMap = new HashMap<>();
            keyMap.put("cwbNo","");
            int seq = 1;
            if(micList.size() != 0){
                folder = createFolder(param);
                folderList.add(folder);
                folderCnt = 1;
            } else {
                throw new Exception("NO DATA");
            }
            String commonColumn = "";
            commonColumn = reportService.createCommonColumn(headers,"MIC","");
            String format = "%0" + COMMON_LENCABLE + "d";
            int totalLen = COMMON_LENCOMMONCABLE + OAIT001MICConstants.MIC_LENCOMMON;
            commonColumn = commonColumn + String.format(format, totalLen);

            for (Map<String, String> stringStringMap : micList) {
                if (!keyMap.get("cwbNo").equals(stringStringMap.get("CWBNO"))) {
                    seq = 1;
                }
                keyMap.put("awbNo", stringStringMap.get("AWBNO"));
                keyMap.put("bwbNo", stringStringMap.get("BWBNO"));
                keyMap.put("cwbNo", stringStringMap.get("CWBNO"));
                keyMap.put("cwbNoDeptCd", stringStringMap.get("CWBNODEPTCD"));
                OAIT001MICReportDao micData = mapper.getMICData(keyMap);
                StringBuilder sb = new StringBuilder();
                sb.append(writeMICReport(micData, commonColumn));
                String fileName = reportService.getReportName("MIC", keyMap.get("cwbNo"), keyMap.get("cwbNoDeptCd"), String.format("%04d", seq), "");
                String filePath = reportService.getReportFilePath(folder, fileName);

                // S3保存
                if (!reportService.saveReportS3(s3Path, fileName, sb.toString())) {
                    throw new Exception("S3 Bucket Error");
                }

                // ファイル作成
                BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "Shift-JIS"));
                bf.write(sb.toString());
                bf.flush();
                bf.close();

                updateParamMap.put("cwbNo", micData.getCwbNo());
                updateParamMap.put("cwbNoDeptCd", micData.getCwbNoDeptCd());
                int updateResult = mapper.updateMasterStatus(updateParamMap);
                if (updateResult == 0) {
                    throw new Exception(OAIT001CommonConstants.MSG_ERR_004);
                }

                // ステータス更新
                statusDao.setCwbNoDeptCd(micData.getCwbNoDeptCd());
                statusDao.setBwbNo(micData.getBwbNo());
                statusDao.setCwbNo(micData.getCwbNo());
                stsServ.updateStatusMaster(statusDao);

                fileNameList.add(fileName);
                seq = seq + 1;
                fileCnt = fileCnt + 1;
            }

            // 結果保存
            returnDto.setErrorFlag(RESULT_SUCCESS);
            returnDto.setCntFile(fileCnt);
            returnDto.setCntFolder(folderCnt);
            returnDto.setS3FolderList(s3FolderList);
            returnDto.setFolderList(folderList);
        } catch (Exception e){
            // RollBack処理
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            // ログ出力
            log.error(e.getMessage());
            e.printStackTrace();

            // 結果保存
            returnDto.setErrorFlag(RESULT_ERROR);
            returnDto.setMessage(e.toString());

            // ファイル削除
            if(!StringUtil.isStringEmpty(folder)){
                commonService.deleteFolder(folder);
            }
            if(!StringUtil.isStringEmpty(s3Path)){
                reportService.deleteReportS3(s3Path,fileNameList);
            }
        }
        return returnDto;
    }

    private String createFolder(Object param) {
        String folderPath = reportService.getReportPath("MIC");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAIT001SearchDto) param).getAwbNo();
        String path = folderPath + File.separator + "MIC"+ date +"_" + awbNo;
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


    private Map getParameterMap(Object param,HttpHeaders headers){
        Map<String, String> parameterMap = new HashMap<>();
        if(param instanceof OAIT001MICDao){
            parameterMap.put("awbNo", ((OAIT001MICDao) param).getAwbNo());
            parameterMap.put("cwbNo", ((OAIT001MICDao) param).getCwbNo());
            parameterMap.put("cwbNoDeptCd", ((OAIT001MICDao) param).getCwbNoDeptCd());
        } else {
            OAIT001SearchDto dto = (OAIT001SearchDto) param;
            String[] fltList = dto.getCurrentArrFlt1().split("/");
            parameterMap.put("awbNo", dto.getAwbNo());
            parameterMap.put("currentArrFlt1", fltList[0]);
            parameterMap.put("currentArrFlt2", fltList[1]);
            parameterMap.put("companyCd", commonService.getCompanyCd(headers));
            parameterMap.put("deptCd", commonService.getDeptCd(headers));
            parameterMap.put("reCreate", dto.getReCreate());
        }
        return parameterMap;
    }

    private StringBuffer writeMICReport(OAIT001MICReportDao data,String commonColumn) {
        StringBuffer sb = new StringBuffer();


        // 1.共通項目
        sb.append(commonColumn);
        sb.append("\r\n");
        // 2.申告番号
        sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_REPORTNO,true));
        sb.append("\r\n");

        // 3.申告条件
        sb.append(StringUtil.fillSpace(data.getReportCondition(), OAIT001MICConstants.MIC_REPORTCONDITION,true));
        sb.append("\r\n");

        // 4.申告先種別コード
        sb.append(StringUtil.fillSpace(data.getReportKindCd2(), OAIT001MICConstants.MIC_REPORTKINDCD_2,true));
        sb.append("\r\n");

        // 5.識別符号
        sb.append(StringUtil.fillSpace(data.getDiscernmentMark(), OAIT001MICConstants.MIC_DISCERNMENTMARK,true));
        sb.append("\r\n");

        // 6.あて先官署コード
        sb.append(StringUtil.fillSpace(data.getReportDivCd(), OAIT001MICConstants.MIC_REPORTDIVCD,true));
        sb.append("\r\n");

        // 7.あて先部門コード
        sb.append(StringUtil.fillSpace(data.getReportDepCd(), OAIT001MICConstants.MIC_REPORTDEPCD,true));
        sb.append("\r\n");

        // 8.申告予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(data.getReportPlaningDate()), OAIT001MICConstants.MIC_REPORTPLANINGDATE,false));
        sb.append("\r\n");

        // 9.輸入者コード
        if( !StringUtil.isStringEmpty(data.getImpCustomerNo())
                && data.getImpCustomerNo().length() > 2
                && data.getImpCustomerNo().substring(0,3).equals(OCS) ){
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_IMPCUSTOMERNO,true));
        } else {
            sb.append(StringUtil.fillSpace(data.getImpCustomerNo(), OAIT001MICConstants.MIC_IMPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 10.輸入者名
        sb.append(StringUtil.fillSpace(data.getImpCustomerName(), OAIT001MICConstants.MIC_IMPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 11.郵便番号
        sb.append(StringUtil.fillSpace(data.getImpPostCode(), OAIT001MICConstants.MIC_IMPPOSTCODE,true));
        sb.append("\r\n");

        // 12.住所１（都道府県）
        sb.append(StringUtil.fillSpace(data.getImpCustomerAdd1(), OAIT001MICConstants.MIC_IMPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 13.住所２（市区町村（行政区名））
        sb.append(StringUtil.fillSpace(data.getImpCustomerAdd2(), OAIT001MICConstants.MIC_IMPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 14.住所３（町域名・番地）
        sb.append(StringUtil.fillSpace(data.getImpCustomerAdd3(), OAIT001MICConstants.MIC_IMPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 15.住所４（ビル名ほか）
        sb.append(StringUtil.fillSpace(data.getImpCustomerAdd4(), OAIT001MICConstants.MIC_IMPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 16.輸入者電話番号
        sb.append(StringUtil.fillSpace(data.getImpCustomerTelNo(), OAIT001MICConstants.MIC_IMPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 17.輸入者住所
        if(StringUtil.isStringEmpty(data.getImpCustomerAdd1()) && StringUtil.isStringEmpty(data.getImpCustomerAdd2())
                && StringUtil.isStringEmpty(data.getImpCustomerAdd3()) && StringUtil.isStringEmpty(data.getImpCustomerAdd4())){
            String tmpStr = StringUtil.isStringNull(data.getImpCustomerAddLump()).replaceAll("\r\n","");
            sb.append(StringUtil.fillSpace(tmpStr, OAIT001MICConstants.MIC_IMPCUSTOMERADD_LUMP,true));
        }else{
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_IMPCUSTOMERADD_LUMP,true));
        }
        sb.append("\r\n");

        // 18.税関事務管理人コード
        sb.append(StringUtil.fillSpace(data.getCustomsOfficeJanitorCd(), OAIT001MICConstants.MIC_CUSTOMSOFFICEJANITORCD,true));
        sb.append("\r\n");

        // 19.税関事務管理人受理番号
        sb.append(StringUtil.fillSpace(data.getCustomsOfficeJanitorReNo(), OAIT001MICConstants.MIC_CUSTOMSOFFICEJANITORRENO,true));
        sb.append("\r\n");

        // 20.税関事務管理人名
        sb.append(StringUtil.fillSpace(data.getCustomsOfficeJanitorName(), OAIT001MICConstants.MIC_CUSTOMSOFFICEJANITORNAME,true));
        sb.append("\r\n");

        // 21.通関予定蔵置場コード
        sb.append(StringUtil.fillSpace(data.getCarBondedWareHouse(), OAIT001MICConstants.MIC_BONDEDWAREHOUSECD,true));
        sb.append("\r\n");

        // 22.検査立会者
        sb.append(StringUtil.fillSpace(data.getInspectionWitness(), OAIT001MICConstants.MIC_INSPECTIONWITNESS,true));
        sb.append("\r\n");

        // 23.仕出人コード
        sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_EXPCUSTOMERNO,true));
        sb.append("\r\n");

        // 24.仕出人名
        sb.append(StringUtil.fillSpace(data.getExpCustomerName(), OAIT001MICConstants.MIC_EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 25.住所１(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(data.getExpCustomerAdd1(), OAIT001MICConstants.MIC_EXPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 26.住所２(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(data.getExpCustomerAdd2(), OAIT001MICConstants.MIC_EXPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 27.住所３(CityName)
        sb.append(StringUtil.fillSpace(data.getExpCustomerAdd3(), OAIT001MICConstants.MIC_EXPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 28.住所４(Country sub-Entity,Name)
        sb.append(StringUtil.fillSpace(data.getExpCustomerAdd4(), OAIT001MICConstants.MIC_EXPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 29.郵便番号（Postcodeidentification）
        sb.append(StringUtil.fillSpace(data.getExpCustomerPostCode(), OAIT001MICConstants.MIC_EXPCUSTOMERPOSTCODE,true));
        sb.append("\r\n");

        // 30.国名コード（Country,coded）
        sb.append(StringUtil.fillSpace(data.getExpCustomerCountry(), OAIT001MICConstants.MIC_EXPCUSTOMERCOUNTRY,true));
        sb.append("\r\n");

        // 31.仕出人住所
        if( StringUtil.isStringEmpty(data.getExpCustomerAdd1()) && StringUtil.isStringEmpty(data.getExpCustomerAdd2())
                && StringUtil.isStringEmpty(data.getExpCustomerAdd3()) && StringUtil.isStringEmpty(data.getExpCustomerAdd4())){
            String tmpStr = StringUtil.isStringNull(data.getExpCustomerAddLump()).replaceAll("\r\n","");
            sb.append(StringUtil.fillSpace(tmpStr, OAIT001MICConstants.MIC_EXPCUSTOMERADD_LUMP,true));
        } else {
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_EXPCUSTOMERADD_LUMP,true));
        }
        sb.append("\r\n");

        // 32.HAWB番号
        sb.append(StringUtil.fillSpace(data.getCwbNo(), OAIT001MICConstants.MIC_CWBNO,true));
        sb.append("\r\n");

        // 33.MAWB番号
        sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_AWBNO,true));
        sb.append("\r\n");

        // 34.貨物個数
        sb.append(StringUtil.fillSpace(data.getCargoPiece(), OAIT001MICConstants.MIC_CARGOPIECE,false));
        sb.append("\r\n");

        // 35.貨物重量
        sb.append(StringUtil.fillSpace(data.getCargoWeight(), OAIT001MICConstants.MIC_CARGOWEIGHT,false));
        sb.append("\r\n");

        //特別仕様 (36-38)
        if(data.getCustomsPlaceCd().equals("W9ANG")){
            // 36.積載機名
            String tmpStr = data.getCurrentArrFlt1() + "/" + data.getCurrentArrFlt2();
            sb.append(StringUtil.fillSpace(tmpStr, OAIT001MICConstants.MIC_CURRENTARRFLT,true));
            sb.append("\r\n");

            // 37.入港年月日
            sb.append(StringUtil.fillSpace(StringUtil.changeReportData(data.getArrPortDate()), OAIT001MICConstants.MIC_ARRPORTDATE,false));
            sb.append("\r\n");

            // 38.取卸港コード
            sb.append(StringUtil.fillSpace(data.getGetPort(), OAIT001MICConstants.MIC_GETPORT,true));
            sb.append("\r\n");

        } else {
            // 36.積載機名
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_CURRENTARRFLT,true));
            sb.append("\r\n");

            // 37.入港年月日
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_ARRPORTDATE,false));
            sb.append("\r\n");

            // 38.取卸港コード
            sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_GETPORT,true));
            sb.append("\r\n");
        }

        // 39.積出地コード
        sb.append(StringUtil.fillSpace(data.getShipmentCd(), OAIT001MICConstants.MIC_SHIPMENTCD,true));
        sb.append("\r\n");

        // 40.インボイス価格区分コード
        sb.append(StringUtil.fillSpace(data.getInvoiceValClassCd(), OAIT001MICConstants.MIC_INVOICEVALCLASSCD,true));
        sb.append("\r\n");

        // 41.インボイス価格条件コード
        sb.append(StringUtil.fillSpace(data.getInvoiceValConCd(), OAIT001MICConstants.MIC_INVOICEVALCONCD,true));
        sb.append("\r\n");

        // 42.インボイス通貨コード
        sb.append(StringUtil.fillSpace(data.getInvoiceCurCd(), OAIT001MICConstants.MIC_INVOICECURCD,true));
        sb.append("\r\n");

        // 43.インボイス価格
        if(StringUtil.isStringNull(data.getInvoiceCurCd()).equals("JPY")
                && data.getInvoiceValue().contains(".")){
            String tmpStr = data.getInvoiceValue();
            sb.append(StringUtil.fillSpace(tmpStr.substring(0,tmpStr.indexOf(".")), OAIT001MICConstants.MIC_INVOICEVALUE,false));
        } else {
            sb.append(StringUtil.fillSpace(data.getInvoiceValue(), OAIT001MICConstants.MIC_INVOICEVALUE,false));
        }
        sb.append("\r\n");

        // 44.運賃区分コード
        sb.append(StringUtil.fillSpace(data.getFareFlg(), OAIT001MICConstants.MIC_FAREFLG,true));
        sb.append("\r\n");

        // 45.運賃通貨コード
        sb.append(StringUtil.fillSpace(data.getFareCurrencyCd(), OAIT001MICConstants.MIC_FARECURRENCYCD,true));
        sb.append("\r\n");

        // 46.運賃
        if(StringUtil.isStringNull(data.getFareCurrencyCd()).equals("JPY")
                && data.getFare().contains(".")){
            String tmpStr = data.getFare();
            sb.append(StringUtil.fillSpace(tmpStr.substring(0,tmpStr.indexOf(".")), OAIT001MICConstants.MIC_FARE,false));
        } else {
            sb.append(StringUtil.fillSpace(data.getFare(), OAIT001MICConstants.MIC_FARE,false));
        }
        sb.append("\r\n");

        // 47.保険区分コード
        sb.append(StringUtil.fillSpace(data.getInsuranceClassCd(), OAIT001MICConstants.MIC_INSURANCECLASSCD,true));
        sb.append("\r\n");

        // 48.保険通貨コード
        sb.append(StringUtil.fillSpace(data.getInsuranceCurCd(), OAIT001MICConstants.MIC_INSURANCECURCD,true));
        sb.append("\r\n");

        // 49.保険金額
        if(StringUtil.isStringNull(data.getInsuranceCurCd()).equals("JPY")
                && data.getInsuranceAmount().contains(".")){
            String tmpStr = data.getInsuranceAmount();
            sb.append(StringUtil.fillSpace(tmpStr.substring(0,tmpStr.indexOf(".")), OAIT001MICConstants.MIC_INSURANCEAMOUNT,false));
        } else {
            sb.append(StringUtil.fillSpace(data.getInsuranceAmount(), OAIT001MICConstants.MIC_INSURANCEAMOUNT,false));
        }
        sb.append("\r\n");

        // 50.品名
        sb.append(StringUtil.fillSpace(data.getItem(), OAIT001MICConstants.MIC_ITEM,true));
        sb.append("\r\n");

        // 51.原産地コード
        sb.append(StringUtil.fillSpace(data.getCountryOfOriginCD(), OAIT001MICConstants.MIC_COUNTRYOFORIGINCD,true));
        sb.append("\r\n");

        // 52.課税価格
        sb.append(StringUtil.fillSpace("", OAIT001MICConstants.MIC_TAXABLESAMO,false));
        sb.append("\r\n");

        // 53.記事
        sb.append(StringUtil.fillSpace(data.getNews2(), OAIT001MICConstants.MIC_NEWS_2,true));
        sb.append("\r\n");

        // 54.荷主セクションコード
        sb.append(StringUtil.fillSpace(data.getShippersSecCd(), OAIT001MICConstants.MIC_SHIPPERSSECCD,true));
        sb.append("\r\n");

        // 55.荷主リファレンスナンバー
        sb.append(StringUtil.fillSpace(data.getShippersRefNo(), OAIT001MICConstants.MIC_SHIPPERSREFNO,true));
        sb.append("\r\n");

        // 56.社内整理番号
        sb.append(StringUtil.fillSpace(data.getInhouseRefNumber(), OAIT001MICConstants.MIC_INHOUSEREFNUMBER,true));
        sb.append("\r\n");
        return sb;
    }

}
