package com.kse.wmsv2.oa_et_001.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.ReportService;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_et_001.common.OAET001CommonConstants;
import com.kse.wmsv2.oa_et_001.common.OAET001EDAConstants;
import com.kse.wmsv2.oa_et_001.dao.OAET001ReportEdaDetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001ReportEdaHeadDao;
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
public class OAET001EDAService extends OAET001CommonConstants{

    @Autowired
    OAET001COMMONService commonService;

    @Autowired
    ReportService reportService;

    @Autowired
    OAET001ReportMapper reportMapper;

    @Autowired
    StatusService stsServ;

    @Transactional
    public OAET001ReportResultDto writeEDA(HttpHeaders header, OAET001SearchDto param) {
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
        statusDao.setCustomStatusCd(EDA_WRITE);
        statusDao.setLinkDataClass(DEFAULT_LINK_DATA);
        statusDao.setBusinessClass(EXPORT_BUSINESS_CD);
        statusDao.setAwbNo(param.getAwbNo());
        statusDao.setCwbNoDeptCd(DEFAULT_CWB_DEPT_CD);
        statusDao.setBwbNo(DEFAULT_BWB_NO);
        // データ更新用
        Map<String,String> updateParamMap = new HashMap<>();
        updateParamMap.put("awbNo", param.getAwbNo());
        updateParamMap.put("statusCd", EDA_WRITE);
        updateParamMap.put("userCd",userCd);
        updateParamMap.put("date",date);


        try{
            //結果保存
            List<String> folderList = resultDto.getFolderList();
            List<String> s3FolderList = resultDto.getS3FolderList();

            // S3パース取得
            s3Path = reportService.getS3Path("EDA");
            s3FolderList.add(s3Path);

            // 電文作成用データ取得用MAP
            Map<String,String> dataMap = new HashMap<>();
            String depCd = reportMapper.getDeptCd(deptCd);
            dataMap.put("awbNo",param.getAwbNo());
            dataMap.put("userAuth",userAuth);
            dataMap.put("deptCd",deptCd);
            dataMap.put("depCd",depCd);

            // 電文作成用データ取得
            List<String> dataList = new ArrayList<>();
            if(param.getRemakeFlg().equals("true")){
                dataList = reportMapper.getEdaDataReMake(dataMap);
            } else {
                dataList = reportMapper.getEdaDataNew(dataMap);
            }

            // データチェック
            if(dataList.size() == 0 ){
                resultDto.setErrorFlag(RESULT_SUCCESS);
                resultDto.setCntEDA(folderCnt);
                resultDto.setCntFileEDA(fileCnt);
                return resultDto;
            }

            // folder作成
            folderPath = createFolder(param);
            folderList.add(folderPath);
            folderCnt = folderCnt + 1;

            // 共通項目作成
            String commonColumn = "";
            commonColumn = reportService.createCommonColumn(header,"EDA","");
            String format = "%0" + COMMON_LENCABLE + "d";


            // 電文作成
            for(int i = 0; i < dataList.size(); i++){
                // 電文ファイル名取得
                String fileName = reportService.getReportName("EDA",dataList.get(i),"",String.format("%04d", i+1),"");
                String filePath = reportService.getReportFilePath(folderPath,fileName);

                // 内容
                StringBuilder sb = new StringBuilder();

                // データ取得
                OAET001ReportEdaHeadDao headerDao = new OAET001ReportEdaHeadDao();
                List<OAET001ReportEdaDetailDao> detailList = new ArrayList<>();
                headerDao = reportMapper.getEdaHeader(dataList.get(i));
                detailList = reportMapper.getEdaDetail(dataList.get(i));

                // 共通項目作成
                String tmpCommon = "";
                int totalLen = 0;
                if(detailList.size()>0){
                    totalLen = COMMON_LENCOMMONCABLE + OAET001EDAConstants.LENCOMMON
                            + (OAET001EDAConstants.LENEXDATAREP * detailList.size());
                } else{
                    totalLen = COMMON_LENCOMMONCABLE + OAET001EDAConstants.LENCOMMON
                            + OAET001EDAConstants.LENEXDATAREP;
                }
                tmpCommon = commonColumn + String.format(format, totalLen);

                // Large,Small判断して内容作成
                if(headerDao.getLargeSmallFlg().equals("L")){
                    sb.append(writeHeadLarge(headerDao,tmpCommon));
                    sb.append(writeDetailLarge(detailList));
                } else {
                    sb.append(writeHeadSmall(headerDao,tmpCommon));
                    sb.append(writeDetailSmall(detailList));
                }
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
                    paramMap.put("cwbNo",dataList.get(i));
                    paramMap.put("esaFlg", "2");
                    paramMap.put("statusCd", EDA_WRITE);
                    reportMapper.updateAEDATA(paramMap);
                    // ステータス更新
                    statusDao.setCwbNo(dataList.get(i));
                    stsServ.updateStatusMaster(statusDao);
                    // データ更新
                    updateParamMap.put("cwbNo",headerDao.getCwbNo());
                    int updateResult = reportMapper.updateMasterStatus(updateParamMap);
                    if(updateResult == 0){
                        throw new Exception(MSG_ERR_906);
                    }
                }
                // ファイル作成CNT
                fileCnt = fileCnt + 1;
            }

            // 結果保存
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setErrorFlag(RESULT_SUCCESS);
            resultDto.setCntEDA(folderCnt);
            resultDto.setCntFileEDA(fileCnt);
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

    private StringBuffer writeHeadLarge(OAET001ReportEdaHeadDao data, String common) {
        StringBuffer sb = new StringBuffer();

        // 1.共通項目
        sb.append(common);
        sb.append("\r\n");

        // 2.輸出申告番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReportNo()), OAET001EDAConstants.EXPREPORTNO,true));
        sb.append("\r\n");

        // 3.大額・少額識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLargeSmallFlg()), OAET001EDAConstants.LARGESMALLFLG,true));
        sb.append("\r\n");

        // 4.申告種別コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportKindCd()), OAET001EDAConstants.REPORTKINDCD,true));
        sb.append("\r\n");

        // 5.通関種別コード１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsKindCd1()), OAET001EDAConstants.CUSTOMSKINDCD_1,true));
        sb.append("\r\n");

        // 6.通関種別コード２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsKindCd2()), OAET001EDAConstants.CUSTOMSKINDCD_2,true));
        sb.append("\r\n");

        // 7.識別符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDiscernmentMark()), OAET001EDAConstants.DISCERNMENTMARK,true));
        sb.append("\r\n");

        // 8.申告官署コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDepCd()), OAET001EDAConstants.REPORTDEPCD,true));
        sb.append("\r\n");

        // 9.申告先部門コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDivisionCd()), OAET001EDAConstants.REPORTDIVISIONCD,true));
        sb.append("\r\n");

        // 10.輸出申告予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(StringUtil.isStringNull(data.getExpReportPlanDate())), OAET001EDAConstants.EXPREPORTPLANDATE,false));
        sb.append("\r\n");

        // 11.輸出者コード
        String expCustomerNo = StringUtil.isStringNull(data.getExpCustomerNo());
        if(expCustomerNo.length() > 2 && expCustomerNo.substring(0,3).equals("OCS")){
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPCUSTOMERNO,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerNo()), OAET001EDAConstants.EXPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 12.輸出者名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerName()), OAET001EDAConstants.EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 13.輸出者郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerPostcode()), OAET001EDAConstants.EXPCUSTOMERPOSTCODE,true));
        sb.append("\r\n");

        // 14.輸出者住所１(都道府県)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd1()), OAET001EDAConstants.EXPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 15.輸出者住所２(市区町村)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd2()), OAET001EDAConstants.EXPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 16.輸出者住所３(町域名・番地)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd3()), OAET001EDAConstants.EXPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 17.輸出者住所４(ビル名他)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd4()), OAET001EDAConstants.EXPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 18.輸出者電話番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerTelNo()).replaceAll("-",""), OAET001EDAConstants.EXPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 19.税関事務管理人コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorCd()), OAET001EDAConstants.CUSTOMSOFFICEJANITORCD,true));
        sb.append("\r\n");

        // 20.税関事務管理人受理番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorReNo()), OAET001EDAConstants.CUSTOMSOFFICEJANITORRENO,true));
        sb.append("\r\n");

        // 21.税関事務管理人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorName()), OAET001EDAConstants.CUSTOMSOFFICEJANITORNAME,true));
        sb.append("\r\n");

        // 22.申告予定者コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportPlanPersonCd()), OAET001EDAConstants.REPORTPLANPERSONCD,true));
        sb.append("\r\n");

        // 23.蔵置場コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBondedWareHouseCd()), OAET001EDAConstants.BONDEDWAREHOUSECD,true));
        sb.append("\r\n");

        // 24.仕向人コード
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.CONSIGNEENO,true));
        sb.append("\r\n");

        // 25.仕向人氏名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeName()), OAET001EDAConstants.CONSIGNEENAME,true));
        sb.append("\r\n");

        // 26.仕向人住所１(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd1()), OAET001EDAConstants.CONSIGNEEADD_1,true));
        sb.append("\r\n");

        // 27.仕向人住所２(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd2()), OAET001EDAConstants.CONSIGNEEADD_2,true));
        sb.append("\r\n");

        // 28.仕向人住所３(CityName)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd3()), OAET001EDAConstants.CONSIGNEEADD_3,true));
        sb.append("\r\n");

        // 29.仕向人住所４(CountrySubEntry.Name)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd4()), OAET001EDAConstants.CONSIGNEEADD_4,true));
        sb.append("\r\n");

        // 30.仕向人郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneePostcode()), OAET001EDAConstants.CONSIGNEEPOSTCODE,true));
        sb.append("\r\n");

        // 31.仕向人国コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeCountry()), OAET001EDAConstants.CONSIGNEECOUNTRY,true));
        sb.append("\r\n");

        // 32.検査立会者
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInspectionWitness()), OAET001EDAConstants.INSPECTIONWITNESS,true));
        sb.append("\r\n");

        // 33.輸出管理番号(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_01,true));
        sb.append("\r\n");

        // 34.AWB番号(CWB)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAET001EDAConstants.CWBNO,true));
        sb.append("\r\n");

        // 35.搬入個数
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingPiece()), OAET001EDAConstants.CARRYINGPIECE,false));
        sb.append("\r\n");

        // 36.個数単位コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_02,true));
        sb.append("\r\n");

        // 37.貨物重量(グロス)(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_03,true));
        sb.append("\r\n");

        // 38.重量単位コード(グロス)(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_04,true));
        sb.append("\r\n");

        // 39.記号番号(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_05,true));
        sb.append("\r\n");

        // 40.最終仕向地コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLastDestinationCd()), OAET001EDAConstants.LASTDESTINATIONCD,true));
        sb.append("\r\n");

        // 41.最終仕向地名
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.LASTDESTNAME,true));
        sb.append("\r\n");

        // 42.積込港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDepPort()), OAET001EDAConstants.DEPPORT,true));
        sb.append("\r\n");

        // 43.貿易形態符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getTradeTypeMark()), OAET001EDAConstants.TRADETYPEMARK,true));
        sb.append("\r\n");

        // 44.積載予定船舶コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_06,true));
        sb.append("\r\n");

        // 45.積載予定(機)名(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_07,true));
        sb.append("\r\n");

        // 46.出港予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(StringUtil.isStringNull(data.getDepPlaningDate())), OAET001EDAConstants.DEPPLANINGDATE,false));
        sb.append("\r\n");

        // 47.コンテナ扱い本数(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_08,true));
        sb.append("\r\n");

        // 48.税関調査用符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsExamMark()), OAET001EDAConstants.CUSTOMSEXAMMARK,true));
        sb.append("\r\n");

        // 49.輸出承認証等区分
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReCoFlg()), OAET001EDAConstants.EXPRECOFLG,true));
        sb.append("\r\n");

        // 50.事前審査済貨物等識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getPreExamCargoDisc()), OAET001EDAConstants.PREEXAMCARGODISC,true));
        sb.append("\r\n");

        // 51.輸出承認証等識別１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc1()), OAET001EDAConstants.EXPRECDISC_1,true));
        sb.append("\r\n");

        // 52.輸出承認証番号等１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo1()), OAET001EDAConstants.EXPRECNO_1,true));
        sb.append("\r\n");

        // 53.輸出承認証等識別２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc2()), OAET001EDAConstants.EXPRECDISC_2,true));
        sb.append("\r\n");

        // 54.輸出承認証番号等２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo2()), OAET001EDAConstants.EXPRECNO_2,true));
        sb.append("\r\n");

        // 55.輸出承認証等識別３
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc3()), OAET001EDAConstants.EXPRECDISC_3,true));
        sb.append("\r\n");

        // 56.輸出承認証番号等３
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo3()), OAET001EDAConstants.EXPRECNO_3,true));
        sb.append("\r\n");

        // 57.輸出承認証等識別４
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc4()), OAET001EDAConstants.EXPRECDISC_4,true));
        sb.append("\r\n");

        // 58.輸出承認証番号等４
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo4()), OAET001EDAConstants.EXPRECNO_4,true));
        sb.append("\r\n");

        // 59.輸出承認証等識別５
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc5()), OAET001EDAConstants.EXPRECDISC_5,true));
        sb.append("\r\n");

        // 60.輸出承認証番号等５
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo5()), OAET001EDAConstants.EXPRECNO_5,true));
        sb.append("\r\n");

        // 61.輸出承認証等識別６
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc6()), OAET001EDAConstants.EXPRECDISC_6,true));
        sb.append("\r\n");

        // 62.輸出承認証番号等６
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo6()), OAET001EDAConstants.EXPRECNO_6,true));
        sb.append("\r\n");

        // 63.輸出承認証等識別７
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc7()), OAET001EDAConstants.EXPRECDISC_7,true));
        sb.append("\r\n");

        // 64.輸出承認証番号等７
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo7()), OAET001EDAConstants.EXPRECNO_7,true));
        sb.append("\r\n");

        // 65.輸出承認証等識別８
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc8()), OAET001EDAConstants.EXPRECDISC_8,true));
        sb.append("\r\n");

        // 66.輸出承認証番号等８
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo8()), OAET001EDAConstants.EXPRECNO_8,true));
        sb.append("\r\n");

        // 67.輸出承認証等識別９
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc9()), OAET001EDAConstants.EXPRECDISC_9,true));
        sb.append("\r\n");

        // 68.輸出承認証番号等９
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo9()), OAET001EDAConstants.EXPRECNO_9,true));
        sb.append("\r\n");

        // 69.輸出承認証等識別10
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc10()), OAET001EDAConstants.EXPRECDISC_10,true));
        sb.append("\r\n");

        // 70.輸出承認証番号等10
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo10()), OAET001EDAConstants.EXPRECNO_10,true));
        sb.append("\r\n");

        // 71.輸出承認証等識別11
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc11()), OAET001EDAConstants.EXPRECDISC_11,true));
        sb.append("\r\n");

        // 72.輸出承認証番号等11
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo11()), OAET001EDAConstants.EXPRECNO_11,true));
        sb.append("\r\n");

        // 73.輸出承認証等識別12
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc12()), OAET001EDAConstants.EXPRECDISC_12,true));
        sb.append("\r\n");

        // 74.輸出承認証番号等12
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo12()), OAET001EDAConstants.EXPRECNO_12,true));
        sb.append("\r\n");

        // 75.輸出承認証等識別13
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc13()), OAET001EDAConstants.EXPRECDISC_13,true));
        sb.append("\r\n");

        // 76.輸出承認証番号等13
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo13()), OAET001EDAConstants.EXPRECNO_13,true));
        sb.append("\r\n");

        // 77.輸出承認証等識別14
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc14()), OAET001EDAConstants.EXPRECDISC_14,true));
        sb.append("\r\n");

        // 78.輸出承認証番号等14
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo14()), OAET001EDAConstants.EXPRECNO_14,true));
        sb.append("\r\n");

        // 79.輸出承認証等識別15
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc15()), OAET001EDAConstants.EXPRECDISC_15,true));
        sb.append("\r\n");

        // 80.輸出承認証番号等15
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo15()), OAET001EDAConstants.EXPRECNO_15,true));
        sb.append("\r\n");

        // 81.インボイス識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceDiscernment()), OAET001EDAConstants.INVOICEDISCERNMENT,true));
        sb.append("\r\n");

        // 82.電子インボイス受付番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getEleInvoiceReNo()), OAET001EDAConstants.ELEINVOICERENO,true));
        sb.append("\r\n");

        // 83.インボイス番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceNo()), OAET001EDAConstants.INVOICENO,true));
        sb.append("\r\n");

        // 84.インボイス価格条件コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValConCd()), OAET001EDAConstants.INVOICEVALCONCD,true));
        sb.append("\r\n");

        // 85.インボイス通貨コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceCurCd()), OAET001EDAConstants.INVOICECURCD,true));
        sb.append("\r\n");

        // 86.インボイス価格
        if(data.getInvoiceCurCd().equals("JPY")){
            double inValue = Double.parseDouble(data.getInvoiceValue());
            sb.append(StringUtil.fillSpace(String.valueOf(Math.floor(inValue)), OAET001EDAConstants.INVOICEVALUE,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValue()), OAET001EDAConstants.INVOICEVALUE,true));
        }
        sb.append("\r\n");

        // 87.インボイス価格区分コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValClassCd()), OAET001EDAConstants.INVOICEVALCLASSCD,true));
        sb.append("\r\n");

        // 88.FOB通貨コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobCurrencyCd()), OAET001EDAConstants.FOBCURRENCYCD,true));
        sb.append("\r\n");

        // 89.FOB価格
        if(data.getFobCurrencyCd().equals("JPY")){
            double inValue = Double.parseDouble(data.getFobAmo());
            sb.append(StringUtil.fillSpace(String.valueOf(Math.floor(inValue)), OAET001EDAConstants.FOBAMO,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobAmo()), OAET001EDAConstants.FOBAMO,true));
        }
        sb.append("\r\n");

        // 90.ベーシックプライス合計
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBasicPriceTotal()), OAET001EDAConstants.BASICPRICETOTAL,true));
        sb.append("\r\n");

        // 91.要搭載確認識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNeedLoadingRecDisc()), OAET001EDAConstants.NEEDLOADINGRECDISC,true));
        sb.append("\r\n");

        // 92.バンニング場所コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");

        // 93.バンニング場所名(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_10,true));
        sb.append("\r\n");

        // 94.バンニング場所住所１(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_11,true));
        sb.append("\r\n");

        // 95.バンニング場所住所２(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_12,true));
        sb.append("\r\n");

        // 96.バンニング場所住所３(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_13,true));
        sb.append("\r\n");

        // 97.バンニング場所住所４(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_14,true));
        sb.append("\r\n");

        // 98.記事(税関用)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNews1()), OAET001EDAConstants.NEWS_1,true));
        sb.append("\r\n");

        // 99.記事(通関業者用)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNews2()), OAET001EDAConstants.NEWS_2,true));
        sb.append("\r\n");

        // 100.記事（荷主用）
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNewsShipper()), OAET001EDAConstants.NEWSSHIPPER,true));
        sb.append("\r\n");

        // 101.荷主セクションコード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersSecCd()), OAET001EDAConstants.SHIPPERSSECCD,true));
        sb.append("\r\n");

        // 102.荷主リファレンスナンバー
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersRefNo()), OAET001EDAConstants.SHIPPERSREFNO,true));
        sb.append("\r\n");

        // 103.社内整理用番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInHouseRefNumber()), OAET001EDAConstants.INHOUSEREFNUMBER,true));
        sb.append("\r\n");

        return sb;
    }

    private StringBuffer writeHeadSmall(OAET001ReportEdaHeadDao data,String common) {
        StringBuffer sb = new StringBuffer();

        // 1.共通項目
        sb.append(common);
        sb.append("\r\n");

        // 2.輸出申告番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReportNo()), OAET001EDAConstants.EXPREPORTNO,true));
        sb.append("\r\n");

        // 3.大額・少額識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLargeSmallFlg()), OAET001EDAConstants.LARGESMALLFLG,true));
        sb.append("\r\n");

        // 4.申告種別コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportKindCd()), OAET001EDAConstants.REPORTKINDCD,true));
        sb.append("\r\n");

        // 5.通関種別コード１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsKindCd1()), OAET001EDAConstants.CUSTOMSKINDCD_1,true));
        sb.append("\r\n");

        // 6.通関種別コード２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsKindCd2()), OAET001EDAConstants.CUSTOMSKINDCD_2,true));
        sb.append("\r\n");

        // 7.識別符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDiscernmentMark()), OAET001EDAConstants.DISCERNMENTMARK,true));
        sb.append("\r\n");

        // 8.申告官署コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDepCd()), OAET001EDAConstants.REPORTDEPCD,true));
        sb.append("\r\n");

        // 9.申告先部門コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportDivisionCd()), OAET001EDAConstants.REPORTDIVISIONCD,true));
        sb.append("\r\n");

        // 10.輸出申告予定年月日
        sb.append(StringUtil.fillSpace(StringUtil.changeReportData(data.getExpReportPlanDate()), OAET001EDAConstants.EXPREPORTPLANDATE,false));
        sb.append("\r\n");

        // 11.輸出者コード
        String expCustomerNo = StringUtil.isStringNull(data.getExpCustomerNo());
        if(expCustomerNo.length() > 2 && expCustomerNo.substring(0,3).equals("OCS")){
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPCUSTOMERNO,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerNo()), OAET001EDAConstants.EXPCUSTOMERNO,true));
        }
        sb.append("\r\n");

        // 12.輸出者名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerName()), OAET001EDAConstants.EXPCUSTOMERNAME,true));
        sb.append("\r\n");

        // 13.輸出者郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerPostcode()), OAET001EDAConstants.EXPCUSTOMERPOSTCODE,true));
        sb.append("\r\n");

        // 14.輸出者住所１(都道府県)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd1()), OAET001EDAConstants.EXPCUSTOMERADD_1,true));
        sb.append("\r\n");

        // 15.輸出者住所２(市区町村)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd2()), OAET001EDAConstants.EXPCUSTOMERADD_2,true));
        sb.append("\r\n");

        // 16.輸出者住所３(町域名・番地)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd3()), OAET001EDAConstants.EXPCUSTOMERADD_3,true));
        sb.append("\r\n");

        // 17.輸出者住所４(ビル名他)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerAdd4()), OAET001EDAConstants.EXPCUSTOMERADD_4,true));
        sb.append("\r\n");

        // 18.輸出者電話番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpCustomerTelNo()).replaceAll("-",""), OAET001EDAConstants.EXPCUSTOMERTELNO,true));
        sb.append("\r\n");

        // 19.税関事務管理人コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorCd()), OAET001EDAConstants.CUSTOMSOFFICEJANITORCD,true));
        sb.append("\r\n");

        // 20.税関事務管理人受理番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorReNo()), OAET001EDAConstants.CUSTOMSOFFICEJANITORRENO,true));
        sb.append("\r\n");

        // 21.税関事務管理人名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsOfficeJanitorName()), OAET001EDAConstants.CUSTOMSOFFICEJANITORNAME,true));
        sb.append("\r\n");

        // 22.申告予定者コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getReportPlanPersonCd()), OAET001EDAConstants.REPORTPLANPERSONCD,true));
        sb.append("\r\n");

        // 23.蔵置場コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getBondedWareHouseCd()), OAET001EDAConstants.BONDEDWAREHOUSECD,true));
        sb.append("\r\n");

        // 24.仕向人コード
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.CONSIGNEENO,true));
        sb.append("\r\n");

        // 25.仕向人氏名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeName()), OAET001EDAConstants.CONSIGNEENAME,true));
        sb.append("\r\n");

        // 26.仕向人住所１(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd1()), OAET001EDAConstants.CONSIGNEEADD_1,true));
        sb.append("\r\n");

        // 27.仕向人住所２(Street&Number/P.O.BOX)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd2()), OAET001EDAConstants.CONSIGNEEADD_2,true));
        sb.append("\r\n");

        // 28.仕向人住所３(CityName)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd3()), OAET001EDAConstants.CONSIGNEEADD_3,true));
        sb.append("\r\n");

        // 29.仕向人住所４(CountrySubEntry.Name)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeAdd4()), OAET001EDAConstants.CONSIGNEEADD_4,true));
        sb.append("\r\n");

        // 30.仕向人郵便番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneePostcode()), OAET001EDAConstants.CONSIGNEEPOSTCODE,true));
        sb.append("\r\n");

        // 31.仕向人国コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getConsigneeCountry()), OAET001EDAConstants.CONSIGNEECOUNTRY,true));
        sb.append("\r\n");

        // 32.検査立会者
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInspectionWitness()), OAET001EDAConstants.INSPECTIONWITNESS,true));
        sb.append("\r\n");

        // 33.輸出管理番号(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_01,true));
        sb.append("\r\n");

        // 34.AWB番号(CWB)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCwbNo()), OAET001EDAConstants.CWBNO,true));
        sb.append("\r\n");

        // 35.搬入個数
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCarryingPiece()), OAET001EDAConstants.CARRYINGPIECE,false));
        sb.append("\r\n");

        // 36.個数単位コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_02,true));
        sb.append("\r\n");

        // 37.貨物重量(グロス)(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_03,true));
        sb.append("\r\n");

        // 38.重量単位コード(グロス)(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_04,true));
        sb.append("\r\n");

        // 39.記号番号(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_05,true));
        sb.append("\r\n");

        // 40.最終仕向地コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLastDestinationCd()), OAET001EDAConstants.LASTDESTINATIONCD,true));
        sb.append("\r\n");

        // 41.最終仕向地名
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getLastDestName()), OAET001EDAConstants.LASTDESTNAME,true));
        sb.append("\r\n");

        // 42.積込港
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getDepPort()), OAET001EDAConstants.DEPPORT,true));
        sb.append("\r\n");

        // 43.貿易形態符号
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.TRADETYPEMARK,true));
        sb.append("\r\n");

        // 44.積載予定船舶コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_06,true));
        sb.append("\r\n");

        // 45.積載予定(機)名(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_07,true));
        sb.append("\r\n");

        // 46.出港予定年月日
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DEPPLANINGDATE,false));
        sb.append("\r\n");

        // 47.コンテナ扱い本数(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_08,true));
        sb.append("\r\n");

        // 48.税関調査用符号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getCustomsExamMark()), OAET001EDAConstants.CUSTOMSEXAMMARK,true));
        sb.append("\r\n");

        // 49.輸出承認証等区分
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpReCoFlg()), OAET001EDAConstants.EXPRECOFLG,true));
        sb.append("\r\n");

        // 50.事前審査済貨物等識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getPreExamCargoDisc()), OAET001EDAConstants.PREEXAMCARGODISC,true));
        sb.append("\r\n");

        // 51.輸出承認証等識別１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc1()), OAET001EDAConstants.EXPRECDISC_1,true));
        sb.append("\r\n");

        // 52.輸出承認証番号等１
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo1()), OAET001EDAConstants.EXPRECNO_1,true));
        sb.append("\r\n");

        // 53.輸出承認証等識別２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc2()), OAET001EDAConstants.EXPRECDISC_2,true));
        sb.append("\r\n");

        // 54.輸出承認証番号等２
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo2()), OAET001EDAConstants.EXPRECNO_2,true));
        sb.append("\r\n");

        // 55.輸出承認証等識別３
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc3()), OAET001EDAConstants.EXPRECDISC_3,true));
        sb.append("\r\n");

        // 56.輸出承認証番号等３
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo3()), OAET001EDAConstants.EXPRECNO_3,true));
        sb.append("\r\n");

        // 57.輸出承認証等識別４
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc4()), OAET001EDAConstants.EXPRECDISC_4,true));
        sb.append("\r\n");

        // 58.輸出承認証番号等４
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo4()), OAET001EDAConstants.EXPRECNO_4,true));
        sb.append("\r\n");

        // 59.輸出承認証等識別５
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc5()), OAET001EDAConstants.EXPRECDISC_5,true));
        sb.append("\r\n");

        // 60.輸出承認証番号等５
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo5()), OAET001EDAConstants.EXPRECNO_5,true));
        sb.append("\r\n");

        // 61.輸出承認証等識別６
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc6()), OAET001EDAConstants.EXPRECDISC_6,true));
        sb.append("\r\n");

        // 62.輸出承認証番号等６
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo6()), OAET001EDAConstants.EXPRECNO_6,true));
        sb.append("\r\n");

        // 63.輸出承認証等識別７
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc7()), OAET001EDAConstants.EXPRECDISC_7,true));
        sb.append("\r\n");

        // 64.輸出承認証番号等７
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo7()), OAET001EDAConstants.EXPRECNO_7,true));
        sb.append("\r\n");

        // 65.輸出承認証等識別８
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc8()), OAET001EDAConstants.EXPRECDISC_8,true));
        sb.append("\r\n");

        // 66.輸出承認証番号等８
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo8()), OAET001EDAConstants.EXPRECNO_8,true));
        sb.append("\r\n");

        // 67.輸出承認証等識別９
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc9()), OAET001EDAConstants.EXPRECDISC_9,true));
        sb.append("\r\n");

        // 68.輸出承認証番号等９
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo9()), OAET001EDAConstants.EXPRECNO_9,true));
        sb.append("\r\n");

        // 69.輸出承認証等識別10
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc10()), OAET001EDAConstants.EXPRECDISC_10,true));
        sb.append("\r\n");

        // 70.輸出承認証番号等10
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo10()), OAET001EDAConstants.EXPRECNO_10,true));
        sb.append("\r\n");

        // 71.輸出承認証等識別11
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc11()), OAET001EDAConstants.EXPRECDISC_11,true));
        sb.append("\r\n");

        // 72.輸出承認証番号等11
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo11()), OAET001EDAConstants.EXPRECNO_11,true));
        sb.append("\r\n");

        // 73.輸出承認証等識別12
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc12()), OAET001EDAConstants.EXPRECDISC_12,true));
        sb.append("\r\n");

        // 74.輸出承認証番号等12
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo12()), OAET001EDAConstants.EXPRECNO_12,true));
        sb.append("\r\n");

        // 75.輸出承認証等識別13
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc13()), OAET001EDAConstants.EXPRECDISC_13,true));
        sb.append("\r\n");

        // 76.輸出承認証番号等13
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo13()), OAET001EDAConstants.EXPRECNO_13,true));
        sb.append("\r\n");

        // 77.輸出承認証等識別14
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc14()), OAET001EDAConstants.EXPRECDISC_14,true));
        sb.append("\r\n");

        // 78.輸出承認証番号等14
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo14()), OAET001EDAConstants.EXPRECNO_14,true));
        sb.append("\r\n");

        // 79.輸出承認証等識別15
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecDisc15()), OAET001EDAConstants.EXPRECDISC_15,true));
        sb.append("\r\n");

        // 80.輸出承認証番号等15
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getExpRecNo15()), OAET001EDAConstants.EXPRECNO_15,true));
        sb.append("\r\n");

        // 81.インボイス識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceDiscernment()), OAET001EDAConstants.INVOICEDISCERNMENT,true));
        sb.append("\r\n");

        // 82.電子インボイス受付番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getEleInvoiceReNo()), OAET001EDAConstants.ELEINVOICERENO,true));
        sb.append("\r\n");

        // 83.インボイス番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceNo()), OAET001EDAConstants.INVOICENO,true));
        sb.append("\r\n");

        // 84.インボイス価格条件コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValConCd()), OAET001EDAConstants.INVOICEVALCONCD,true));
        sb.append("\r\n");

        // 85.インボイス通貨コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceCurCd()), OAET001EDAConstants.INVOICECURCD,true));
        sb.append("\r\n");

        // 86.インボイス価格
        if(data.getInvoiceCurCd().equals("JPY")){
            double inValue = Double.parseDouble(data.getInvoiceValue());
            sb.append(StringUtil.fillSpace(String.valueOf(Math.floor(inValue)), OAET001EDAConstants.INVOICEVALUE,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValue()), OAET001EDAConstants.INVOICEVALUE,true));
        }
        sb.append("\r\n");

        // 87.インボイス価格区分コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInvoiceValClassCd()), OAET001EDAConstants.INVOICEVALCLASSCD,true));
        sb.append("\r\n");

        // 88.FOB通貨コード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobCurrencyCd()), OAET001EDAConstants.FOBCURRENCYCD,true));
        sb.append("\r\n");

        // 89.FOB価格
        if(data.getFobCurrencyCd().equals("JPY")){
            double inValue = Double.parseDouble(data.getFobAmo());
            sb.append(StringUtil.fillSpace(String.valueOf(Math.floor(inValue)), OAET001EDAConstants.FOBAMO,true));
        } else {
            sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getFobAmo()), OAET001EDAConstants.FOBAMO,true));
        }
        sb.append("\r\n");

        // 90.ベーシックプライス合計
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICETOTAL,true));
        sb.append("\r\n");

        // 91.要搭載確認識別
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNeedLoadingRecDisc()), OAET001EDAConstants.NEEDLOADINGRECDISC,true));
        sb.append("\r\n");

        // 92.バンニング場所コード(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_09,true));
        sb.append("\r\n");

        // 93.バンニング場所名(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_10,true));
        sb.append("\r\n");

        // 94.バンニング場所住所１(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_11,true));
        sb.append("\r\n");

        // 95.バンニング場所住所２(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_12,true));
        sb.append("\r\n");

        // 96.バンニング場所住所３(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_13,true));
        sb.append("\r\n");

        // 97.バンニング場所住所４(ダミー)
        sb.append(StringUtil.fillSpace("", OAET001EDAConstants.DUMMY_14,true));
        sb.append("\r\n");

        // 98.記事(税関用)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNews1()), OAET001EDAConstants.NEWS_1,true));
        sb.append("\r\n");

        // 99.記事(通関業者用)
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNews2()), OAET001EDAConstants.NEWS_2,true));
        sb.append("\r\n");

        // 100.記事（荷主用）
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getNewsShipper()), OAET001EDAConstants.NEWSSHIPPER,true));
        sb.append("\r\n");

        // 101.荷主セクションコード
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersSecCd()), OAET001EDAConstants.SHIPPERSSECCD,true));
        sb.append("\r\n");

        // 102.荷主リファレンスナンバー
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getShippersRefNo()), OAET001EDAConstants.SHIPPERSREFNO,true));
        sb.append("\r\n");

        // 103.社内整理用番号
        sb.append(StringUtil.fillSpace(StringUtil.isStringNull(data.getInHouseRefNumber()), OAET001EDAConstants.INHOUSEREFNUMBER,true));
        sb.append("\r\n");

        return sb;
    }


    private StringBuffer writeDetailLarge(List<OAET001ReportEdaDetailDao> detailList) {
        StringBuffer sb = new StringBuffer();


        if(detailList.size() == 0){
            // 1.輸出統計品目番号
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPSTATITEMNO,true));
            sb.append("\r\n");

            // 2.NACCS用コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.NACCSCD,true));
            sb.append("\r\n");

            // 3.品名
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.ITEM,true));
            sb.append("\r\n");

            // 4.数量１
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_1,false));
            sb.append("\r\n");

            // 5.数量単位コード１
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_1,true));
            sb.append("\r\n");

            // 6.数量２
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_2,false));
            sb.append("\r\n");

            // 7.数量単位コード２
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_2,true));
            sb.append("\r\n");

            // 8.ベーシックプライス按分係数
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEDIVCOEF,false));
            sb.append("\r\n");

            // 9.ベーシックプライス通貨コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICECURCD,true));
            sb.append("\r\n");

            // 10.ベーシックプライス金額
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEAMO,false));
            sb.append("\r\n");

            // 11.他法令コード1
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_1,true));
            sb.append("\r\n");

            // 12.他法令コード2
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_2,true));
            sb.append("\r\n");

            // 13.他法令コード3
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_3,true));
            sb.append("\r\n");

            // 14.他法令コード4
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_4,true));
            sb.append("\r\n");

            // 15.他法令コード5
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_5,true));
            sb.append("\r\n");

            // 16.輸出貿易管理令別表コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPTRADECONTCD,true));
            sb.append("\r\n");

            // 17.外為法第48条コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.FOREXCHLOW48,true));
            sb.append("\r\n");

            // 18.関税減免戻税コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.CUSTOMSEXEMPTCD,true));
            sb.append("\r\n");

            // 19.内国消費税免税コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.INCONSTAXEXEMPCD,true));
            sb.append("\r\n");

            // 20.内国消費税免税識別
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.INCONSTAXEXEMPDISC,true));
            sb.append("\r\n");

        } else {
            int stopFlag = Math.min(detailList.size(), 98);
            for(int i = 0; i < stopFlag; i++){
                // 1.輸出統計品目番号
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getExpStatItemNo()), OAET001EDAConstants.EXPSTATITEMNO,true));
                sb.append("\r\n");

                // 2.NACCS用コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getNaccsCd()), OAET001EDAConstants.NACCSCD,true));
                sb.append("\r\n");

                // 3.品名
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getItem()), OAET001EDAConstants.ITEM,true));
                sb.append("\r\n");

                // 4.数量１
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getAmo1()), OAET001EDAConstants.AMO_1,true));
                sb.append("\r\n");

                // 5.数量単位コード１
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getAmoUnit1()), OAET001EDAConstants.AMOUNIT_1,true));
                sb.append("\r\n");

                // 6.数量２
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getAmo2()), OAET001EDAConstants.AMO_2,true));
                sb.append("\r\n");

                // 7.数量単位コード２
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getAmoUnit2()), OAET001EDAConstants.AMOUNIT_2,true));
                sb.append("\r\n");

                // 8.ベーシックプライス按分係数
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getBasicPriceDivCoEf()), OAET001EDAConstants.BASICPRICEDIVCOEF,true));
                sb.append("\r\n");

                // 9.ベーシックプライス通貨コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getBasicPriceCurCd()), OAET001EDAConstants.BASICPRICECURCD,true));
                sb.append("\r\n");

                // 10.ベーシックプライス金額
                if(detailList.get(i).getBasicPriceCurCd().equals("JPY")){
                    double value = Double.parseDouble(detailList.get(i).getBasicPriceAmo());
                    sb.append(StringUtil.fillSpace(String.valueOf(Math.floor(value)), OAET001EDAConstants.BASICPRICEAMO,true));
                } else {
                    sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getBasicPriceAmo()), OAET001EDAConstants.BASICPRICEAMO,true));
                }
                sb.append("\r\n");

                // 11.他法令コード1
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd1()), OAET001EDAConstants.OTHERLAWCD_1,true));
                sb.append("\r\n");

                // 12.他法令コード2
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd2()), OAET001EDAConstants.OTHERLAWCD_2,true));
                sb.append("\r\n");

                // 13.他法令コード3
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd3()), OAET001EDAConstants.OTHERLAWCD_3,true));
                sb.append("\r\n");

                // 14.他法令コード4
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd4()), OAET001EDAConstants.OTHERLAWCD_4,true));
                sb.append("\r\n");

                // 15.他法令コード5
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd5()), OAET001EDAConstants.OTHERLAWCD_5,true));
                sb.append("\r\n");

                // 16.輸出貿易管理令別表コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getExpTradeContCd()), OAET001EDAConstants.EXPTRADECONTCD,true));
                sb.append("\r\n");

                // 17.外為法第48条コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getForExchLow48()), OAET001EDAConstants.FOREXCHLOW48,true));
                sb.append("\r\n");

                // 18.関税減免戻税コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getCustomsExemptCd()), OAET001EDAConstants.CUSTOMSEXEMPTCD,true));
                sb.append("\r\n");

                // 19.内国消費税免税コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getInconsTaxExempCd()), OAET001EDAConstants.INCONSTAXEXEMPCD,true));
                sb.append("\r\n");

                // 20.内国消費税免税識別
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getInconsTaxExempDisc()), OAET001EDAConstants.INCONSTAXEXEMPDISC,true));
                sb.append("\r\n");
            }
        }
        return sb;
    }


    private StringBuffer writeDetailSmall(List<OAET001ReportEdaDetailDao> detailList) {
        StringBuffer sb = new StringBuffer();
        if(detailList.size() == 0 ){
            // 1.輸出統計品目番号
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPSTATITEMNO,true));
            sb.append("\r\n");

            // 2.NACCS用コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.NACCSCD,true));
            sb.append("\r\n");

            // 3.品名
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.ITEM,true));
            sb.append("\r\n");

            // 4.数量１
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_1,false));
            sb.append("\r\n");

            // 5.数量単位コード１
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_1,true));
            sb.append("\r\n");

            // 6.数量２
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_2,false));
            sb.append("\r\n");

            // 7.数量単位コード２
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_2,true));
            sb.append("\r\n");

            // 8.ベーシックプライス按分係数
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEDIVCOEF,false));
            sb.append("\r\n");

            // 9.ベーシックプライス通貨コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICECURCD,true));
            sb.append("\r\n");

            // 10.ベーシックプライス金額
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEAMO,false));
            sb.append("\r\n");

            // 11.他法令コード1
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_1,true));
            sb.append("\r\n");

            // 12.他法令コード2
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_2,true));
            sb.append("\r\n");

            // 13.他法令コード3
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_3,true));
            sb.append("\r\n");

            // 14.他法令コード4
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_4,true));
            sb.append("\r\n");

            // 15.他法令コード5
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.OTHERLAWCD_5,true));
            sb.append("\r\n");

            // 16.輸出貿易管理令別表コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.EXPTRADECONTCD,true));
            sb.append("\r\n");

            // 17.外為法第48条コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.FOREXCHLOW48,true));
            sb.append("\r\n");

            // 18.関税減免戻税コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.CUSTOMSEXEMPTCD,true));
            sb.append("\r\n");

            // 19.内国消費税免税コード
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.INCONSTAXEXEMPCD,true));
            sb.append("\r\n");

            // 20.内国消費税免税識別
            sb.append(StringUtil.fillSpace("", OAET001EDAConstants.INCONSTAXEXEMPDISC,true));
            sb.append("\r\n");

        } else {
            int stopFlag = Math.min(detailList.size(), 98);
            for(int i = 0; i < stopFlag; i++){
                // 1.輸出統計品目番号
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getExpStatItemNo()), OAET001EDAConstants.EXPSTATITEMNO,true));
                sb.append("\r\n");

                // 2.NACCS用コード
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.NACCSCD,true));
                sb.append("\r\n");

                // 3.品名
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.ITEM,true));
                sb.append("\r\n");

                // 4.数量１
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_1,true));
                sb.append("\r\n");

                // 5.数量単位コード１
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_1,true));
                sb.append("\r\n");

                // 6.数量２
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMO_2,true));
                sb.append("\r\n");

                // 7.数量単位コード２
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.AMOUNIT_2,true));
                sb.append("\r\n");

                // 8.ベーシックプライス按分係数
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEDIVCOEF,true));
                sb.append("\r\n");

                // 9.ベーシックプライス通貨コード
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICECURCD,true));
                sb.append("\r\n");

                // 10.ベーシックプライス金額
                sb.append(StringUtil.fillSpace("", OAET001EDAConstants.BASICPRICEAMO,true));
                sb.append("\r\n");

                // 11.他法令コード1
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd1()), OAET001EDAConstants.OTHERLAWCD_1,true));
                sb.append("\r\n");

                // 12.他法令コード2
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd2()), OAET001EDAConstants.OTHERLAWCD_2,true));
                sb.append("\r\n");

                // 13.他法令コード3
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd3()), OAET001EDAConstants.OTHERLAWCD_3,true));
                sb.append("\r\n");

                // 14.他法令コード4
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd4()), OAET001EDAConstants.OTHERLAWCD_4,true));
                sb.append("\r\n");

                // 15.他法令コード5
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getOtherLawCd5()), OAET001EDAConstants.OTHERLAWCD_5,true));
                sb.append("\r\n");

                // 16.輸出貿易管理令別表コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getExpTradeContCd()), OAET001EDAConstants.EXPTRADECONTCD,true));
                sb.append("\r\n");

                // 17.外為法第48条コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getForExchLow48()), OAET001EDAConstants.FOREXCHLOW48,true));
                sb.append("\r\n");

                // 18.関税減免戻税コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getCustomsExemptCd()), OAET001EDAConstants.CUSTOMSEXEMPTCD,true));
                sb.append("\r\n");

                // 19.内国消費税免税コード
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getInconsTaxExempCd()), OAET001EDAConstants.INCONSTAXEXEMPCD,true));
                sb.append("\r\n");

                // 20.内国消費税免税識別
                sb.append(StringUtil.fillSpace(StringUtil.isStringNull(detailList.get(i).getInconsTaxExempDisc()), OAET001EDAConstants.INCONSTAXEXEMPDISC,true));
                sb.append("\r\n");
            }
        }

        return sb;
    }

    private String createFolder(Object param) throws Exception{
        String folderPath = reportService.getReportPath("EDA");
        String date = DateUtil.getNow("MMdd_HHmmss");
        String awbNo = ((OAET001SearchDto) param).getAwbNo();
        String path = folderPath + File.separator + "EDA"+ date +"_" + awbNo;
        File folder = new File(path);
        if(!folder.exists()){
            try {
                folder.mkdir();
            } catch (Exception e){
                e.printStackTrace();
                throw new FileSystemException("FAIL FOLDER MAKE");
            }
        }
        return path;
    }
}
