package com.kse.wmsv2.oc_cs_006.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006PermitConstants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006PermitOutDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006PermitInDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006PermitImpMapper;
import com.kse.wmsv2.oc_cs_006.util.FileUtil;
import com.kse.wmsv2.oc_cs_006.util.ReadUtil;
import com.kse.wmsv2.oc_cs_007.dao.OCCS007InsertCsImageManagementDao;
import com.kse.wmsv2.oc_cs_007.mapper.OCCS007ScreenMapper;
import com.kse.wmsv2.oc_cs_007.service.OCCS007Service;
import jp.co.systembase.report.Report;
import jp.co.systembase.report.ReportPages;
import jp.co.systembase.report.data.ReportDataSource;
import jp.co.systembase.report.renderer.pdf.PdfRenderer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Slf4j
@Service
public class OCCS006PermitService {

    @Autowired
    OCCS006PermitImpMapper occs006PermitMapper;
    @Autowired
    OCCS007ScreenMapper occs007ScreenMapper;

    @Autowired
    OCCS007Service occs007Service;

    @Autowired
    AwsS3 awsS3;
    public OCCS006ResultDto permitPermit(List<?> dataList) throws Exception {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();
        List<String> pdfPath = new ArrayList<String>();

        if (dataList.size() < 1) {
            String msg = "取得データが0件です";
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }

        for (int i = 0; i < dataList.size(); i++) {
            int updateCnt = 0;
            // cwbNo取得

            if (!dataList.isEmpty() && dataList.get(i) instanceof Map<?, ?>) {
                Map<?, ?> data = (Map<?, ?>) dataList.get(i);
                String cwbNo = data.get("cwbno").toString();
                /** test */
                // String cwbNo = "KJL2301010";
                /** test */

                //　cwbNo取得失敗の際にはエラー処理
                if (StringUtils.isEmpty(cwbNo)) {
                    String msg = OCCS006PermitConstants.PERMIT_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("輸入CWB番号取得完了 : " + cwbNo);

                // CS_IMAGE_MANAGEMENTに該当の輸入許可書のPDFが存在しているか確認
                String csImageManagementPath = occs006PermitMapper.selectCsImageManagement(cwbNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPath.add(csImageManagementPath);
                    continue;
                }

                log.info("PDF作成履歴:　無");

                // 該当電文より、PDF作成用Dtoに値をセット
                log.info("戻り電文より必要データ取得開始");
                List<Object> extandData = getExtandData(cwbNo);

                List<OCCS006PermitOutDto> permitOutDtoList = new ArrayList<>();
                List<OCCS006PermitInDto> permitInDtoList = new ArrayList<>();

                for (Object obj : extandData) {
                    if (obj instanceof OCCS006PermitOutDto) {
                        permitOutDtoList.add((OCCS006PermitOutDto) obj);
                    } else if (obj instanceof OCCS006PermitInDto) {
                        permitInDtoList.add((OCCS006PermitInDto) obj);
                    }
                }

                 if (permitOutDtoList.isEmpty() && permitInDtoList.isEmpty()) {
                   String msg = OCCS006PermitConstants.PERMIT_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                 }

                if (!permitOutDtoList.isEmpty()) {
                    String fileName = "";
                    if (permitOutDtoList.get(0).getFilePathList().contains("Rp")) {
                        fileName = permitOutDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006PermitConstants.RP_IMAGECLASS + ".pdf";
                        Report report = new Report(ReadUtil.readJson(OCCS006PermitConstants.RP_EXP_PATH));
                        report.fill(new ReportDataSource(permitOutDtoList));
                        ReportPages pages = report.getPages();
                        String folderPath = OCCS006PermitConstants.RP_EXP_OUT_PATH;
                        FileUtil.folderCheck(folderPath);
                        String filePath = folderPath + fileName;
                        FileOutputStream fos = new FileOutputStream(filePath);

                        try {
                            PdfRenderer renderer = new PdfRenderer(fos);
                            pages.render(renderer);
                            log.info("PDF作成が完了しました。: " + filePath);
                        } finally {
                            fos.close();
                        }

                        log.info("S3 Upload");

                        String keyName = OCCS006PermitConstants.RP_EXP_S3PATH;
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                        keyName += fileName;

                        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                        if (uploadCnt < 1) {
                            String msg = "S3アップロード処理が失敗しました。[filePath:" + fileName + "]";
                            log.error(msg);
                            resultDto.setResult(false);
                            resultDto.setMessage(msg);
                            resultDto.setErrorMessage(msg);
                            return resultDto;
                        }
                        log.info("S3 Upload件数： " + uploadCnt + "件");
                        // CS_IMAGE_MANAGEMENT更新
                        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", permitOutDtoList.get(0).getDateyyyyMMddHHmmss());
                        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                        if (insertCnt < 1) {
                            String msg = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。";
                            log.error(msg);
                            resultDto.setResult(false);
                            resultDto.setMessage(msg);
                            resultDto.setErrorMessage(msg);
                            return resultDto;
                        }

                        log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                        pdfPath.add(keyName);

                        // ETCファイル削除
                        File file = new File(filePath);
                        boolean deleted = file.delete();
                    } else if (permitOutDtoList.get(0).getFilePathList().contains("Pe")) {
                        fileName = permitOutDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006PermitConstants.PE_IMAGECLASS + ".pdf";
                        Report report = new Report(ReadUtil.readJson(OCCS006PermitConstants.PE_EXP_PATH));
                        report.fill(new ReportDataSource(permitOutDtoList));
                        ReportPages pages = report.getPages();
                        String folderPath = OCCS006PermitConstants.PE_EXP_OUT_PATH;
                        FileUtil.folderCheck(folderPath);
                        String filePath = folderPath + fileName;
                        FileOutputStream fos = new FileOutputStream(filePath);

                        try {
                            PdfRenderer renderer = new PdfRenderer(fos);
                            pages.render(renderer);
                            log.info("PDF作成が完了しました。: " + filePath);
                        } finally {
                            fos.close();
                        }

                        log.info("S3 Upload");

                        String keyName = OCCS006PermitConstants.PE_EXP_S3PATH;
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                        keyName += permitOutDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                        keyName += fileName;

                        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                        if (uploadCnt < 1) {
                            String msg = "S3アップロード処理が失敗しました。[filePath:" + fileName + "]";
                            log.error(msg);
                            resultDto.setResult(false);
                            resultDto.setMessage(msg);
                            resultDto.setErrorMessage(msg);
                            return resultDto;
                        }
                        log.info("S3 Upload件数： " + uploadCnt + "件");
                        // CS_IMAGE_MANAGEMENT更新
                        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", permitOutDtoList.get(0).getDateyyyyMMddHHmmss());
                        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                        if (insertCnt < 1) {
                            String msg = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。";
                            log.error(msg);
                            resultDto.setResult(false);
                            resultDto.setMessage(msg);
                            resultDto.setErrorMessage(msg);
                            return resultDto;
                        }

                        log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                        pdfPath.add(keyName);

                        // ETCファイル削除
                        File file = new File(filePath);
                        boolean deleted = file.delete();
                    }
                }else if(!permitInDtoList.isEmpty()) {
                        String fileName = "";
                        if (permitInDtoList.get(0).getFilePathList().contains("Rp")) {
                            fileName = permitInDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006PermitConstants.RP_IMAGECLASS + ".pdf";
                            Report report = new Report(ReadUtil.readJson(OCCS006PermitConstants.RP_IMP_PATH));
                            report.fill(new ReportDataSource(permitInDtoList));
                            ReportPages pages = report.getPages();
                            String folderPath = OCCS006PermitConstants.RP_IMP_OUT_PATH;
                            FileUtil.folderCheck(folderPath);
                            String filePath = folderPath + fileName;
                            FileOutputStream fos = new FileOutputStream(filePath);

                            try {
                                PdfRenderer renderer = new PdfRenderer(fos);
                                pages.render(renderer);
                                log.info("PDF作成が完了しました。: " + filePath);
                            } finally {
                                fos.close();
                            }

                            log.info("S3 Upload");

                            String keyName = OCCS006PermitConstants.RP_IMP_S3PATH;
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                            keyName += fileName;

                            byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                            int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                            if (uploadCnt < 1) {
                                String msg = "S3アップロード処理が失敗しました。[filePath:" + fileName + "]";
                                log.error(msg);
                                resultDto.setResult(false);
                                resultDto.setMessage(msg);
                                resultDto.setErrorMessage(msg);
                                return resultDto;
                            }
                            log.info("S3 Upload件数： " + uploadCnt + "件");
                            // CS_IMAGE_MANAGEMENT更新
                            OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", permitInDtoList.get(0).getDateyyyyMMddHHmmss());
                            int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                            if (insertCnt < 1) {
                                String msg = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。";
                                log.error(msg);
                                resultDto.setResult(false);
                                resultDto.setMessage(msg);
                                resultDto.setErrorMessage(msg);
                                return resultDto;
                            }

                            log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                            pdfPath.add(keyName);

                            // ETCファイル削除
                            File file = new File(filePath);
                            boolean deleted = file.delete();
                        } else if (permitInDtoList.get(0).getFilePathList().contains("Pe")) {
                            fileName = permitInDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006PermitConstants.PE_IMAGECLASS + ".pdf";
                            Report report = new Report(ReadUtil.readJson(OCCS006PermitConstants.PE_IMP_PATH));
                            report.fill(new ReportDataSource(permitInDtoList));
                            ReportPages pages = report.getPages();
                            String folderPath = OCCS006PermitConstants.PE_IMP_OUT_PATH;
                            FileUtil.folderCheck(folderPath);
                            String filePath = folderPath + fileName;
                            FileOutputStream fos = new FileOutputStream(filePath);

                            try {
                                PdfRenderer renderer = new PdfRenderer(fos);
                                pages.render(renderer);
                                log.info("PDF作成が完了しました。: " + filePath);
                            } finally {
                                fos.close();
                            }

                            log.info("S3 Upload");

                            String keyName = OCCS006PermitConstants.PE_IMP_S3PATH;
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                            keyName += permitInDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                            keyName += fileName;

                            byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                            int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                            if (uploadCnt < 1) {
                                String msg = "S3アップロード処理が失敗しました。[filePath:" + fileName + "]";
                                log.error(msg);
                                resultDto.setResult(false);
                                resultDto.setMessage(msg);
                                resultDto.setErrorMessage(msg);
                                return resultDto;
                            }
                            log.info("S3 Upload件数： " + uploadCnt + "件");
                            // CS_IMAGE_MANAGEMENT更新
                            OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", permitInDtoList.get(0).getDateyyyyMMddHHmmss());
                            int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                            if (insertCnt < 1) {
                                String msg = "CS_IMAGE_MANAGEMENTテーブルの更新処理が失敗しました。";
                                log.error(msg);
                                resultDto.setResult(false);
                                resultDto.setMessage(msg);
                                resultDto.setErrorMessage(msg);
                                return resultDto;
                            }

                            log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                            pdfPath.add(keyName);

                            // ETCファイル削除
                            File file = new File(filePath);
                            boolean deleted = file.delete();
                        }
                }
                log.info("データ件数:　" + permitOutDtoList.size() + "件");

            }
        }

        // 正常return値セット
        String msg = OCCS006PermitConstants.PERMIT_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPath);

        return resultDto;
    }

    private List<Object> getExtandData(String cwbNo) throws Exception {
        List<Object> resultList = new ArrayList<>();
        List<OCCS006PermitOutDto> occs006PermitOutDtoList = new ArrayList<OCCS006PermitOutDto>();
        List<OCCS006PermitInDto> occs006PermitInDtoList = new ArrayList<OCCS006PermitInDto>();

        // cwbNoに該当する戻り電文リスト　
        List<String> fileNameList = occs006PermitMapper.selectFileName(cwbNo);

        if (fileNameList.size() < 1) {
            return resultList;
        }

        // 輸入許可書該当する電文種類名リスト
        List<String> expRpTypeList = occs006PermitMapper.selectRpFileName(cwbNo + "%");
        List<String> expPeTypeList = occs006PermitMapper.selectPeFileName(cwbNo + "%");

        List<String> impRpTypeList = occs006PermitMapper.selectRpFileName(cwbNo + "%");
        List<String> impPeTypeList = occs006PermitMapper.selectPeFileName(cwbNo + "%");

        // 戻り電文リストに輸入許可書の該当する電文があるのかチェック
        List<String> expRpFileNameList = new ArrayList<>();
        List<String> expPeFileNameList = new ArrayList<>();

        List<String> impRpFileNameList = new ArrayList<>();
        List<String> impPeFileNameList = new ArrayList<>();

        for (String fileName : fileNameList) {
            if (fileName.substring(0,3).equals("AAD")) {
                for (String permitFileType : impRpTypeList) {
                    if (permitFileType.equals(fileName.substring(0, 6))) {
                        impRpFileNameList.add(fileName);
                    }
                }
                for (String permitFileType : impPeTypeList) {
                    if (permitFileType.equals(fileName.substring(0, 6))) {
                        impPeFileNameList.add(fileName);
                    }
                }
            } else if (fileName.substring(0,3).equals("AAE")) {
                for (String permitFileType : expRpTypeList) {
                    if (permitFileType.equals(fileName.substring(0, 6))) {
                        expRpFileNameList.add(fileName);
                    }
                }
                for (String permitFileType : expPeTypeList) {
                    if (permitFileType.equals(fileName.substring(0, 6))) {
                        expPeFileNameList.add(fileName);
                    }
                }
            }

        }


        if (expRpFileNameList.size() >= 1) {
            for (String rpFileName : fileNameList) {
                String addPath = rpFileName.substring(12, 16) + "/" + rpFileName.substring(16, 18) + "/" + rpFileName.substring(18, 20) + "/";
                String key = OCCS006PermitConstants.GET_RP_EXP_S3PATH + addPath + rpFileName;
                byte[] data = awsS3.get(key);
                String strValue = new String(data, Charset.forName("Shift_JIS"));
                String[] strArr = strValue.split("\n");

                List<String> strList = new ArrayList<String>();
                for (String str : strArr) {
                    strList.add(str);
                }

                for (int i = 138; i < strList.size(); i++) {
                    OCCS006PermitOutDto permitOutDto = new OCCS006PermitOutDto();

                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = format.parse(rpFileName.substring(12, 24));
                    String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
                    String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
                    String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
                    String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");
                    permitOutDto.setDateyyyyMMdd(yyyyMMdd);
                    permitOutDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
                    permitOutDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
                    permitOutDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
                    permitOutDto.setFilePathList("Rp");

                    // 代表税番
                    permitOutDto.setExtand2(strList.get(1));
                    //申告種別
                    permitOutDto.setExtand3(strList.get(2));
                    permitOutDto.setExtand4(strList.get(3));
                    permitOutDto.setExtand5(strList.get(4));
                    permitOutDto.setExtand7(strList.get(6));
                    // 区分
                    permitOutDto.setExtand9(strList.get(8));
                    // あて先税関
                    permitOutDto.setExtand10(strList.get(9));
                    // 提出先
                    permitOutDto.setExtand11(strList.get(10));
                    // 申告年月日
                    String value = strList.get(11).trim();
                    if (value == "") {
                        permitOutDto.setExtand12("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand12(formattedValue);
                    }
                    // 申告番号
                    value = strList.get(12).trim();
                    if (value == "") {
                        permitOutDto.setExtand13("");
                    } else {
                        String formattedValue = value.substring(0, 3) + " " + value.substring(3, 7) + " " + value.substring(7);
                        permitOutDto.setExtand13(formattedValue);
                    }
                    // 申告条件
                    permitOutDto.setExtand14(strList.get(13));
                    // 搬入
                    permitOutDto.setExtand17(strList.get(16));
                    // 搬入
                    value = strList.get(17).trim();
                    if (value == "") {
                        permitOutDto.setExtand18("");
                    } else {
                        String formattedValue = value.substring(0, 13) + "-" + value.substring(13);
                        permitOutDto.setExtand18(formattedValue);
                    }
                    permitOutDto.setExtand19(strList.get(18));
                    // 住　  所
                    permitOutDto.setExtand20(strList.get(19));
                    permitOutDto.setExtand21(strList.get(20));
                    permitOutDto.setExtand22(strList.get(21));
                    permitOutDto.setExtand23(strList.get(22));
                    permitOutDto.setExtand24(strList.get(23));
                    // 電  　話
                    permitOutDto.setExtand25(strList.get(24));
                    // 税関事務管理人
                    permitOutDto.setExtand26(strList.get(25));
                    permitOutDto.setExtand27(strList.get(26));
                    permitOutDto.setExtand28(strList.get(27));
                    // 仕  　向　  人
                    permitOutDto.setExtand29(strList.get(28));
                    permitOutDto.setExtand30(strList.get(29));
                    // 住　  所
                    permitOutDto.setExtand31(strList.get(30));
                    permitOutDto.setExtand32(strList.get(31));
                    permitOutDto.setExtand33(strList.get(32));
                    permitOutDto.setExtand34(strList.get(33));
                    permitOutDto.setExtand35(strList.get(34));
                    // 国コード
                    permitOutDto.setExtand36(strList.get(35));
                    // 仕  　理　  人
                    permitOutDto.setExtand37(strList.get(36));
                    permitOutDto.setExtand38(strList.get(37));
                    // 通関士コード
                    permitOutDto.setExtand39(strList.get(38));
                    //検査立会者
                    permitOutDto.setExtand40(strList.get(39));
                    // A W B 番 号　
                    value = strList.get(43).trim();
                    if (value == "") {
                        permitOutDto.setExtand44("");
                    } else {
                        String formattedValue = value.substring(0, 3) + "-" + value.substring(3);
                        permitOutDto.setExtand44(formattedValue);
                    }
                    // 貨物個数
                    permitOutDto.setExtand42(strList.get(41));
                    // 保税地域
                    permitOutDto.setExtand47(strList.get(46));
                    permitOutDto.setExtand48(strList.get(47));
                    // 蔵置税関
                    permitOutDto.setExtand49(strList.get(48));
                    permitOutDto.setExtand50(strList.get(49));
                    // 最終仕向地
                    permitOutDto.setExtand51(strList.get(50));
                    permitOutDto.setExtand52(strList.get(51));
                    //事前検査済貨物等識別
                    permitOutDto.setExtand53(strList.get(52));
                    // 積込港
                    permitOutDto.setExtand54(strList.get(53));
                    permitOutDto.setExtand55(strList.get(54));
                    // 貿易形態別符号
                    permitOutDto.setExtand56(strList.get(55));
                    // 調査用符号
                    permitOutDto.setExtand57(strList.get(56));
                    // 出港予定年月日
                    permitOutDto.setExtand60(strList.get(59));
                    //  船積(搭載)確認(関税)
                    permitOutDto.setExtand61(strList.get(60));
                    //  船積(搭載)確認 (内国消費税)
                    permitOutDto.setExtand62(strList.get(61));
                    //  船積(搭載)確認 (その他)
                    permitOutDto.setExtand63(strList.get(62));
                    // 輸出承認証等区分
                    permitOutDto.setExtand65(strList.get(64));
                    // 輸出承認証番号等
                    permitOutDto.setExtand66(strList.get(65));
                    permitOutDto.setExtand67(strList.get(66));
                    permitOutDto.setExtand68(strList.get(67));
                    permitOutDto.setExtand69(strList.get(68));
                    permitOutDto.setExtand70(strList.get(69));
                    permitOutDto.setExtand71(strList.get(70));
                    permitOutDto.setExtand72(strList.get(71));
                    permitOutDto.setExtand73(strList.get(72));
                    permitOutDto.setExtand74(strList.get(73));
                    permitOutDto.setExtand75(strList.get(74));
                    permitOutDto.setExtand76(strList.get(75));
                    permitOutDto.setExtand77(strList.get(76));
                    permitOutDto.setExtand78(strList.get(77));
                    permitOutDto.setExtand79(strList.get(78));
                    permitOutDto.setExtand80(strList.get(79));
                    permitOutDto.setExtand81(strList.get(80));
                    permitOutDto.setExtand82(strList.get(81));
                    permitOutDto.setExtand83(strList.get(82));
                    permitOutDto.setExtand84(strList.get(83));
                    permitOutDto.setExtand85(strList.get(84));
                    permitOutDto.setExtand86(strList.get(85));
                    permitOutDto.setExtand87(strList.get(86));
                    permitOutDto.setExtand88(strList.get(87));
                    permitOutDto.setExtand89(strList.get(88));
                    permitOutDto.setExtand90(strList.get(89));
                    permitOutDto.setExtand91(strList.get(90));
                    permitOutDto.setExtand92(strList.get(91));
                    permitOutDto.setExtand93(strList.get(92));
                    permitOutDto.setExtand94(strList.get(93));
                    permitOutDto.setExtand95(strList.get(94));
                    // 仕入書番号
                    permitOutDto.setExtand96(strList.get(95));
                    permitOutDto.setExtand97(strList.get(96));
                    // 仕入書番号(電子)
                    permitOutDto.setExtand98(strList.get(97));
                    // 仕入書価格
                    permitOutDto.setExtand99(strList.get(98));
                    permitOutDto.setExtand100(strList.get(99));
                    value = strList.get(100).trim();
                    if (value == "") {
                        permitOutDto.setExtand101("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
                        String formattedNumber = decimalFormat.format(number);
                        permitOutDto.setExtand101(formattedNumber);
                    }
                    permitOutDto.setExtand102(strList.get(101));
                    // ＦＯＢ価格
                    permitOutDto.setExtand103(strList.get(102));
                    permitOutDto.setExtand104(strList.get(103));
                    // 通貨レート
                    permitOutDto.setExtand105(strList.get(104));
                    permitOutDto.setExtand106(strList.get(105));
                    permitOutDto.setExtand107(strList.get(106));
                    permitOutDto.setExtand108(strList.get(107));
                    // ＢＰＲ合計
                    value = strList.get(108).trim();
                    if (value == "") {
                        permitOutDto.setExtand109("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                        String formattedNumber = decimalFormat.format(number);
                        permitOutDto.setExtand109(formattedNumber);
                    }
                    permitOutDto.setExtand110(strList.get(109));
                    // 構成
                    permitOutDto.setExtand111(strList.get(110));
                    // 欄
                    permitOutDto.setExtand112(strList.get(111));
                    // 記事(税関)
                    permitOutDto.setExtand118(strList.get(117));
                    permitOutDto.setExtand122(strList.get(121));
                    // 記事(通関)
                    permitOutDto.setExtand126(strList.get(125));
                    // 記事(荷主)
                    permitOutDto.setExtand127(strList.get(126));
                    // 荷主セクションコード
                    permitOutDto.setExtand128(strList.get(127));
                    // 荷主Ｒｅｆ Ｎｏ．
                    permitOutDto.setExtand129(strList.get(128));
                    // 社内整理番号
                    permitOutDto.setExtand130(strList.get(129));
                    // 利用者整理番号
                    permitOutDto.setExtand131(strList.get(130));
                    // 輸出者（入力）
                    value = strList.get(131);
                    if (value == "") {
                        permitOutDto.setExtand132("");
                    } else {
                        String formattedValue = value.substring(0, 8) + "-" + value.substring(8);
                        permitOutDto.setExtand132(formattedValue);
                    }
                    // 税関通知欄
                    permitOutDto.setExtand134(strList.get(133));
                    // 許可年月日
                    value = strList.get(134).trim();
                    if (value == "") {
                        permitOutDto.setExtand135("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand135(formattedValue);
                    }
                    // 許可年月日
                    permitOutDto.setExtand136(strList.get(135).trim());
                    // 保税運送承認期間
                    value = strList.get(136).trim();
                    if (value == "") {
                        permitOutDto.setExtand137("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand137(formattedValue);
                    }
                    // 保税運送承認期間
                    value = strList.get(137).trim();
                    if (value == "") {
                        permitOutDto.setExtand138("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand138(formattedValue);
                    }

                    //欄
                    permitOutDto.setExtand139(strList.get(i++));
                    // 統合先欄
                    permitOutDto.setExtand140(strList.get(i++));
                    // 価格再確認
                    permitOutDto.setExtand141(strList.get(i++));
                    // 品名
                    permitOutDto.setExtand142(strList.get(i++));
                    // 統計品目番号
                    value = strList.get(i++).trim();
                    if (value == "") {
                        permitOutDto.setExtand143("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "." + value.substring(4, 6) + "-" + value.substring(6);
                        permitOutDto.setExtand143(formattedValue);
                    }
                    permitOutDto.setExtand144(strList.get(i++));
                    // 申告価格（ＦＯＢ）
                    value = strList.get(i++).trim();
                    if (value.equals("")) {
                        permitOutDto.setExtand145("");
                    } else {
                        double number2 = Double.parseDouble(value);
                        DecimalFormat decimalFormat2 = new DecimalFormat("¥#,###");
                        String formattedNumber2 = decimalFormat2.format(number2);
                        permitOutDto.setExtand145(formattedNumber2);
                    }

                    // 数量(1)
                    permitOutDto.setExtand146(strList.get(i++));
                    permitOutDto.setExtand147(strList.get(i++));
                    //申告価格（ＦＯＢ）
                    permitOutDto.setExtand148(strList.get(i++));
                    // 数量(2)
                    permitOutDto.setExtand149(strList.get(i++));
                    permitOutDto.setExtand150(strList.get(i++));
                    // BPR按分係数
                    value = strList.get(i++);
                    if (value == "") {
                        permitOutDto.setExtand151("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                        String formattedValue = decimalFormat.format(number);
                        permitOutDto.setExtand151(formattedValue);
                    }
                    // BPR金額
                    permitOutDto.setExtand152(strList.get(i++));
                    permitOutDto.setExtand153(strList.get(i++));
                    // 関税法70条関係
                    permitOutDto.setExtand154(strList.get(i++));
                    permitOutDto.setExtand155(strList.get(i++));
                    permitOutDto.setExtand156(strList.get(i++));
                    permitOutDto.setExtand157(strList.get(i++));
                    permitOutDto.setExtand158(strList.get(i++));
                    // 輸出令別表
                    permitOutDto.setExtand159(strList.get(i++));
                    // 外為法第48条
                    permitOutDto.setExtand160(strList.get(i++));
                    // 減免戻税条項符号
                    permitOutDto.setExtand161(strList.get(i++));
                    permitOutDto.setExtand162(strList.get(i++));
                    permitOutDto.setExtand163(strList.get(i++));
                    permitOutDto.setExtand164(strList.get(i++));
                    // 内消税免税符号
                    permitOutDto.setExtand165(strList.get(i++));
                    permitOutDto.setExtand166(strList.get(i++));
                    permitOutDto.setExtand167(strList.get(i));

                    occs006PermitOutDtoList.add(permitOutDto);
                }
            }
            resultList.addAll(occs006PermitOutDtoList);
        } else if (expPeFileNameList.size() >= 1) {
            for (String peFileName : fileNameList) {
                String addPath = peFileName.substring(12, 16) + "/" + peFileName.substring(16, 18) + "/" + peFileName.substring(18, 20) + "/";
                String key = OCCS006PermitConstants.GET_PE_EXP_S3PATH + addPath + peFileName;
                byte[] data = awsS3.get(key);
                String strValue = new String(data, Charset.forName("Shift_JIS"));
                String[] strArr = strValue.split("\n");

                List<String> strList = new ArrayList<String>();
                for (String str : strArr) {
                    strList.add(str);
                }
                for (int i = 138; i < strList.size(); i++) {
                    OCCS006PermitOutDto permitOutDto = new OCCS006PermitOutDto();

                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = format.parse(peFileName.substring(12, 24));
                    String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
                    String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
                    String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
                    String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");
                    permitOutDto.setDateyyyyMMdd(yyyyMMdd);
                    permitOutDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
                    permitOutDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
                    permitOutDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
                    permitOutDto.setFilePathList("Pe");

                    // 代表税番
                    permitOutDto.setExtand2(strList.get(1));
                    //申告種別
                    permitOutDto.setExtand3(strList.get(2));
                    permitOutDto.setExtand4(strList.get(3));
                    permitOutDto.setExtand5(strList.get(4));
                    permitOutDto.setExtand7(strList.get(6));
                    // 区分
                    permitOutDto.setExtand9(strList.get(8));
                    // あて先税関
                    permitOutDto.setExtand10(strList.get(9));
                    // 提出先
                    permitOutDto.setExtand11(strList.get(10));
                    // 申告年月日
                    String value = strList.get(11).trim();
                    if (value == "") {
                        permitOutDto.setExtand12("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand12(formattedValue);
                    }
                    // 申告番号
                    value = strList.get(12).trim();
                    if (value == "") {
                        permitOutDto.setExtand13("");
                    } else {
                        String formattedValue = value.substring(0, 3) + " " + value.substring(3, 7) + " " + value.substring(7);
                        permitOutDto.setExtand13(formattedValue);
                    }
                    // 申告条件
                    permitOutDto.setExtand14(strList.get(13));
                    // 搬入
                    permitOutDto.setExtand17(strList.get(16));
                    // 搬入
                    value = strList.get(17).trim();
                    if (value == "") {
                        permitOutDto.setExtand18("");
                    } else {
                        String formattedValue = value.substring(0, 13) + "-" + value.substring(13);
                        permitOutDto.setExtand18(formattedValue);
                    }
                    permitOutDto.setExtand19(strList.get(18));
                    // 住　  所
                    permitOutDto.setExtand20(strList.get(19));
                    permitOutDto.setExtand21(strList.get(20));
                    permitOutDto.setExtand22(strList.get(21));
                    permitOutDto.setExtand23(strList.get(22));
                    permitOutDto.setExtand24(strList.get(23));
                    // 電  　話
                    permitOutDto.setExtand25(strList.get(24));
                    // 税関事務管理人
                    permitOutDto.setExtand26(strList.get(25));
                    permitOutDto.setExtand27(strList.get(26));
                    permitOutDto.setExtand28(strList.get(27));
                    // 仕  　向　  人
                    permitOutDto.setExtand29(strList.get(28));
                    permitOutDto.setExtand30(strList.get(29));
                    // 住　  所
                    permitOutDto.setExtand31(strList.get(30));
                    permitOutDto.setExtand32(strList.get(31));
                    permitOutDto.setExtand33(strList.get(32));
                    permitOutDto.setExtand34(strList.get(33));
                    permitOutDto.setExtand35(strList.get(34));
                    // 国コード
                    permitOutDto.setExtand36(strList.get(35));
                    // 仕  　理　  人
                    permitOutDto.setExtand37(strList.get(36));
                    permitOutDto.setExtand38(strList.get(37));
                    // 通関士コード
                    permitOutDto.setExtand39(strList.get(38));
                    //検査立会者
                    permitOutDto.setExtand40(strList.get(39));
                    // A W B 番 号　
                    value = strList.get(43).trim();
                    if (value == "") {
                        permitOutDto.setExtand44("");
                    } else {
                        String formattedValue = value.substring(0, 3) + "-" + value.substring(3);
                        permitOutDto.setExtand44(formattedValue);
                    }
                    // 貨物個数
                    permitOutDto.setExtand42(strList.get(41));
                    // 保税地域
                    permitOutDto.setExtand47(strList.get(46));
                    permitOutDto.setExtand48(strList.get(47));
                    // 蔵置税関
                    permitOutDto.setExtand49(strList.get(48));
                    permitOutDto.setExtand50(strList.get(49));
                    // 最終仕向地
                    permitOutDto.setExtand51(strList.get(50));
                    permitOutDto.setExtand52(strList.get(51));
                    //事前検査済貨物等識別
                    permitOutDto.setExtand53(strList.get(52));
                    // 積込港
                    permitOutDto.setExtand54(strList.get(53));
                    permitOutDto.setExtand55(strList.get(54));
                    // 貿易形態別符号
                    permitOutDto.setExtand56(strList.get(55));
                    // 調査用符号
                    permitOutDto.setExtand57(strList.get(56));
                    // 出港予定年月日
                    permitOutDto.setExtand60(strList.get(59));
                    //  船積(搭載)確認(関税)
                    permitOutDto.setExtand61(strList.get(60));
                    //  船積(搭載)確認 (内国消費税)
                    permitOutDto.setExtand62(strList.get(61));
                    //  船積(搭載)確認 (その他)
                    permitOutDto.setExtand63(strList.get(62));
                    // 輸出承認証等区分
                    permitOutDto.setExtand65(strList.get(64));
                    // 輸出承認証番号等
                    permitOutDto.setExtand66(strList.get(65));
                    permitOutDto.setExtand67(strList.get(66));
                    permitOutDto.setExtand68(strList.get(67));
                    permitOutDto.setExtand69(strList.get(68));
                    permitOutDto.setExtand70(strList.get(69));
                    permitOutDto.setExtand71(strList.get(70));
                    permitOutDto.setExtand72(strList.get(71));
                    permitOutDto.setExtand73(strList.get(72));
                    permitOutDto.setExtand74(strList.get(73));
                    permitOutDto.setExtand75(strList.get(74));
                    permitOutDto.setExtand76(strList.get(75));
                    permitOutDto.setExtand77(strList.get(76));
                    permitOutDto.setExtand78(strList.get(77));
                    permitOutDto.setExtand79(strList.get(78));
                    permitOutDto.setExtand80(strList.get(79));
                    permitOutDto.setExtand81(strList.get(80));
                    permitOutDto.setExtand82(strList.get(81));
                    permitOutDto.setExtand83(strList.get(82));
                    permitOutDto.setExtand84(strList.get(83));
                    permitOutDto.setExtand85(strList.get(84));
                    permitOutDto.setExtand86(strList.get(85));
                    permitOutDto.setExtand87(strList.get(86));
                    permitOutDto.setExtand88(strList.get(87));
                    permitOutDto.setExtand89(strList.get(88));
                    permitOutDto.setExtand90(strList.get(89));
                    permitOutDto.setExtand91(strList.get(90));
                    permitOutDto.setExtand92(strList.get(91));
                    permitOutDto.setExtand93(strList.get(92));
                    permitOutDto.setExtand94(strList.get(93));
                    permitOutDto.setExtand95(strList.get(94));
                    // 仕入書番号
                    permitOutDto.setExtand96(strList.get(95));
                    permitOutDto.setExtand97(strList.get(96));
                    // 仕入書番号(電子)
                    permitOutDto.setExtand98(strList.get(97));
                    // 仕入書価格
                    permitOutDto.setExtand99(strList.get(98));
                    permitOutDto.setExtand100(strList.get(99));
                    value = strList.get(100).trim();
                    if (value == "") {
                        permitOutDto.setExtand101("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,###");
                        String formattedNumber = decimalFormat.format(number);
                        permitOutDto.setExtand101(formattedNumber);
                    }
                    permitOutDto.setExtand102(strList.get(101));
                    // ＦＯＢ価格
                    permitOutDto.setExtand103(strList.get(102));
                    permitOutDto.setExtand104(strList.get(103));
                    // 通貨レート
                    permitOutDto.setExtand105(strList.get(104));
                    permitOutDto.setExtand106(strList.get(105));
                    permitOutDto.setExtand107(strList.get(106));
                    permitOutDto.setExtand108(strList.get(107));
                    // ＢＰＲ合計
                    value = strList.get(108).trim();
                    if (value == "") {
                        permitOutDto.setExtand109("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                        String formattedNumber = decimalFormat.format(number);
                        permitOutDto.setExtand109(formattedNumber);
                    }
                    permitOutDto.setExtand110(strList.get(109));
                    // 構成
                    permitOutDto.setExtand111(strList.get(110));
                    // 欄
                    permitOutDto.setExtand112(strList.get(111));
                    // 記事(税関)
                    permitOutDto.setExtand118(strList.get(117));
                    permitOutDto.setExtand122(strList.get(121));
                    // 記事(通関)
                    permitOutDto.setExtand126(strList.get(125));
                    // 記事(荷主)
                    permitOutDto.setExtand127(strList.get(126));
                    // 荷主セクションコード
                    permitOutDto.setExtand128(strList.get(127));
                    // 荷主Ｒｅｆ Ｎｏ．
                    permitOutDto.setExtand129(strList.get(128));
                    // 社内整理番号
                    permitOutDto.setExtand130(strList.get(129));
                    // 利用者整理番号
                    permitOutDto.setExtand131(strList.get(130));
                    // 輸出者（入力）
                    value = strList.get(131);
                    if (value == "") {
                        permitOutDto.setExtand132("");
                    } else {
                        String formattedValue = value.substring(0, 8) + "-" + value.substring(8);
                        permitOutDto.setExtand132(formattedValue);
                    }
                    // 税関通知欄
                    permitOutDto.setExtand134(strList.get(133));
                    // 許可年月日
                    value = strList.get(134).trim();
                    if (value == "") {
                        permitOutDto.setExtand135("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand135(formattedValue);
                    }
                    // 許可年月日
                    permitOutDto.setExtand136(strList.get(135).trim());
                    // 保税運送承認期間
                    value = strList.get(136).trim();
                    if (value == "") {
                        permitOutDto.setExtand137("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand137(formattedValue);
                    }
                    // 保税運送承認期間
                    value = strList.get(137).trim();
                    if (value == "") {
                        permitOutDto.setExtand138("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "/" + value.substring(4, 6) + "/" + value.substring(6);
                        permitOutDto.setExtand138(formattedValue);
                    }

                    //欄
                    permitOutDto.setExtand139(strList.get(i++));
                    // 統合先欄
                    permitOutDto.setExtand140(strList.get(i++));
                    // 価格再確認
                    permitOutDto.setExtand141(strList.get(i++));
                    // 品名
                    permitOutDto.setExtand142(strList.get(i++));
                    // 統計品目番号
                    value = strList.get(i++).trim();
                    if (value == "") {
                        permitOutDto.setExtand143("");
                    } else {
                        String formattedValue = value.substring(0, 4) + "." + value.substring(4, 6) + "-" + value.substring(6);
                        permitOutDto.setExtand143(formattedValue);
                    }
                    permitOutDto.setExtand144(strList.get(i++));
                    // 申告価格（ＦＯＢ）
                    value = strList.get(i++).trim();
                    if (value.equals("")) {
                        permitOutDto.setExtand145("");
                    } else {
                        double number2 = Double.parseDouble(value);
                        DecimalFormat decimalFormat2 = new DecimalFormat("¥#,###");
                        String formattedNumber2 = decimalFormat2.format(number2);
                        permitOutDto.setExtand145(formattedNumber2);
                    }

                    // 数量(1)
                    permitOutDto.setExtand146(strList.get(i++));
                    permitOutDto.setExtand147(strList.get(i++));
                    //申告価格（ＦＯＢ）
                    permitOutDto.setExtand148(strList.get(i++));
                    // 数量(2)
                    permitOutDto.setExtand149(strList.get(i++));
                    permitOutDto.setExtand150(strList.get(i++));
                    // BPR按分係数
                    value = strList.get(i++);
                    if (value == "") {
                        permitOutDto.setExtand151("");
                    } else {
                        double number = Double.parseDouble(value);
                        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                        String formattedValue = decimalFormat.format(number);
                        permitOutDto.setExtand151(formattedValue);
                    }
                    // BPR金額
                    permitOutDto.setExtand152(strList.get(i++));
                    permitOutDto.setExtand153(strList.get(i++));
                    // 関税法70条関係
                    permitOutDto.setExtand154(strList.get(i++));
                    permitOutDto.setExtand155(strList.get(i++));
                    permitOutDto.setExtand156(strList.get(i++));
                    permitOutDto.setExtand157(strList.get(i++));
                    permitOutDto.setExtand158(strList.get(i++));
                    // 輸出令別表
                    permitOutDto.setExtand159(strList.get(i++));
                    // 外為法第48条
                    permitOutDto.setExtand160(strList.get(i++));
                    // 減免戻税条項符号
                    permitOutDto.setExtand161(strList.get(i++));
                    permitOutDto.setExtand162(strList.get(i++));
                    permitOutDto.setExtand163(strList.get(i++));
                    permitOutDto.setExtand164(strList.get(i++));
                    // 内消税免税符号
                    permitOutDto.setExtand165(strList.get(i++));
                    permitOutDto.setExtand166(strList.get(i++));
                    permitOutDto.setExtand167(strList.get(i));

                    occs006PermitOutDtoList.add(permitOutDto);
                }
            }
            resultList.addAll(occs006PermitOutDtoList);
        } else if (impRpFileNameList.size() >= 1) {
            for (String rpFileName : fileNameList) {
                String addPath = rpFileName.substring(12, 16) + "/" + rpFileName.substring(16, 18) + "/" + rpFileName.substring(18, 20) + "/";
                String key = OCCS006PermitConstants.GET_RP_IMP_S3PATH + addPath + rpFileName;
                byte[] data = awsS3.get(key);
                String strValue = new String(data, Charset.forName("Shift_JIS"));
                String[] strArr = strValue.split("\n");

                List<String> strList = new ArrayList<String>();
                for (String str : strArr) {
                    strList.add(str);
                }

                for (int i = 258; i < strList.size(); i++) {
                    OCCS006PermitInDto permitInDto = new OCCS006PermitInDto();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = format.parse(rpFileName.substring(12, 24));

                    String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
                    String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
                    String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
                    String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");
                    permitInDto.setDateyyyyMMdd(yyyyMMdd);
                    permitInDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
                    permitInDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
                    permitInDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
                    permitInDto.setFilePathList("Rp");

                    // 代表統番
                    permitInDto.setRepresentPermitNo(strList.get(3).trim());
                    permitInDto.setRepresentPermitNoCd(strList.get(4).trim());
                    // 申告種別
                    permitInDto.setPermitClass(strList.get(5).trim() + strList.get(6).trim() + strList.get(7).trim());
                    permitInDto.setPermitClassCd2(strList.get(8).trim());
                    // 区分
                    permitInDto.setPermitClassCd(strList.get(9).trim());
                    // あて先税関
                    permitInDto.setDestCustom(strList.get(10).trim());
                    // 部門 11
                    permitInDto.setDepartmentCd(strList.get(11).trim());
                    // 申告年月日
                    String reportDate = strList.get(12).trim();
                    String formattedDate = reportDate.substring(0, 4).trim() + "/" + reportDate.substring(4, 6).trim() + "/" + reportDate.substring(6).trim();
                    permitInDto.setReportDate(formattedDate);
                    // 申告種別
                    String reportClassCd = strList.get(16);
                    String forMattedReportClassCd = reportClassCd.substring(0, 3).trim() + " " + reportClassCd.substring(3, 7).trim() + " " + reportClassCd.substring(7).trim();
                    permitInDto.setReportClassCd(forMattedReportClassCd);
                    // 申告条件
                    permitInDto.setReportCondition(strList.get(18));
                    //申告予定年月日
                    String reportDate2 = strList.get(19).trim();
                    String formattedDate2 = reportDate2.substring(0, 4).trim() + "/" + reportDate2.substring(4, 6).trim() + "/" + reportDate2.substring(6).trim();
                    permitInDto.setReportDate2(formattedDate2);
                    // 本申告 21
                    permitInDto.setIsReport(strList.get(20).trim());
                    // 輸　　入　　者 22
                    String impNo = strList.get(21);
                    permitInDto.setImpNo(impNo.substring(0, impNo.length() - 4).trim());
                    permitInDto.setImpNo2(impNo.substring(impNo.length() - 5).trim());
                    // 住 所
                    permitInDto.setImpCustomerName(strList.get(22).trim());
                    permitInDto.setImpCustomerPostCd(strList.get(23).trim());
                    permitInDto.setImpCustomerAddr1(strList.get(24).trim());
                    permitInDto.setImpCustomerAddr2(strList.get(25).trim());
                    permitInDto.setImpCustomerAddr3(strList.get(26).trim());
                    // impCustomerTel
                    permitInDto.setImpCustomerTel(strList.get(28).trim());
                    // 税関事務管理人
                    permitInDto.setCustomerMasterCd(strList.get(29));
                    permitInDto.setCustomerMasterNo(strList.get(30));
                    permitInDto.setCustomerMasterName(strList.get(31));
                    // 税関事務管理人
                    permitInDto.setImporterCd(strList.get(32));
                    permitInDto.setImporterName(strList.get(33).trim());
                    // 仕 出 人
                    permitInDto.setConsigneeCd(strList.get(34).trim());
                    permitInDto.setConsigneeName(strList.get(35).trim());
                    permitInDto.setConsigneePostCd(strList.get(36).trim());
                    // 住 所
                    permitInDto.setConsigneeAddr1(strList.get(37).trim());
                    permitInDto.setConsigneeAddr2(strList.get(38).trim());
                    permitInDto.setConsigneeAddr3(strList.get(39).trim());
                    permitInDto.setConsigneeAddr4(strList.get(40).trim());
                    permitInDto.setConSigneeCountryCd(strList.get(41).trim());
                    // 輸出 の 委託者
                    permitInDto.setExpConsignorName(strList.get(42));
                    // 代理人
                    permitInDto.setAgentCd(strList.get(43));
                    permitInDto.setAgentDiv(strList.get(44));
                    permitInDto.setCustomCd(strList.get(45));
                    permitInDto.setInspectPerson(strList.get(46));
                    // AWB番号
                    String awbNo = strList.get(47);
                    String modifiedAwbNo = awbNo.substring(0, 3) + "-" + awbNo.substring(3);
                    permitInDto.setAwbNo(modifiedAwbNo);
                    // ＭＡＷＢ番号
                    String newCwbNo = strList.get(48);
                    String modifiedCwbNo = newCwbNo.substring(0, 3) + "-" + newCwbNo.substring(3);
                    permitInDto.setCwbNo(modifiedCwbNo);
                    // 取卸港
                    permitInDto.setArrDest(strList.get(53));
                    permitInDto.setArrFromTo(strList.get(54));
                    //　積出地
                    permitInDto.setCargoFlt(strList.get(55));
                    permitInDto.setCargoCity(strList.get(56));
                    // 積載機名
                    permitInDto.setLoadingName(strList.get(58));
                    // 入港年月日
                    String cargoInDate = strList.get(59);
                    String formattedCargoInDate = cargoInDate.substring(0, 4).trim() + "/" + cargoInDate.substring(4, 6).trim() + "/" + cargoInDate.substring(6).trim();
                    permitInDto.setCargoInDate(formattedCargoInDate);
                    // 蔵置税関
                    permitInDto.setWareHouseName(strList.get(60).trim());
                    permitInDto.setWareHouseCd(strList.get(61).trim());
                    // 貨物個数
                    permitInDto.setCarryingPiece(strList.get(62).trim());
                    // 保税地域
                    permitInDto.setBondedWareHouseCd(strList.get(64));
                    permitInDto.setBondedLocation(strList.get(65));
                    // 貨物重量
                    permitInDto.setCargoWeight(strList.get(66));
                    permitInDto.setCargoUnitCd(strList.get(67));
                    // 搬入予定
                    permitInDto.setBondedAreaCd(strList.get(68));
                    permitInDto.setBondedAreaName(strList.get(69));
                    // 最初蔵入年月日
                    String cargoInitialDate = strList.get(71).trim();
                    if (cargoInitialDate == null || cargoInitialDate.isEmpty()) {
                        permitInDto.setCargoInitialDate(strList.get(71));
                    } else {
                        String formattedInitialDate = cargoInitialDate.substring(0, 4).trim() + "/" + cargoInitialDate.substring(4, 6).trim() + "/" + cargoInitialDate.substring(6).trim();
                        permitInDto.setCargoInitialDate(formattedInitialDate);
                    }
                    // 貿易形態別符号
                    permitInDto.setTradeCode(strList.get(73));
                    // 調査用符号
                    permitInDto.setInvestCd(strList.get(74));
                    // 貿易管理令
                    permitInDto.setTradeOrder(strList.get(76));
                    // 輸入承認証
                    permitInDto.setAuthCertify(strList.get(77));
                    // 関税法70条関係許可承認
                    permitInDto.setImpPermission(strList.get(78));
                    // 共通管理番号
                    permitInDto.setCmManageNo(strList.get(83));
                    // 食品
                    permitInDto.setFoodCertify(strList.get(84));
                    permitInDto.setFoodCertifiedNo(strList.get(85));
                    // 植防
                    permitInDto.setPlantCertify(strList.get(86));
                    permitInDto.setPlantCertifiedNo(strList.get(87));
                    // 動検
                    permitInDto.setAnimalCertify(strList.get(88));
                    permitInDto.setAnimalCertifiedNo(strList.get(89));
                    // 輸入承認証番号等
                    permitInDto.setExtraCertName1(strList.get(91));
                    permitInDto.setExtraCert1(strList.get(92));
                    permitInDto.setExtraCert2Name(strList.get(93));
                    permitInDto.setExtraCert2(strList.get(94));
                    permitInDto.setExtraCert3Name(strList.get(95));
                    permitInDto.setExtraCert3(strList.get(96));
                    permitInDto.setExtraCert4Name(strList.get(97));
                    permitInDto.setExtraCert4(strList.get(98));
                    permitInDto.setExtraCert5Name(strList.get(99));
                    permitInDto.setExtraCert5(strList.get(100));
                    permitInDto.setExtraCert6Name(strList.get(101));
                    permitInDto.setExtraCert6(strList.get(102));
                    permitInDto.setExtraCert7Name(strList.get(103));
                    permitInDto.setExtraCert7(strList.get(104));
                    permitInDto.setExtraCert8Name(strList.get(105));
                    permitInDto.setExtraCert8(strList.get(106));
                    permitInDto.setExtraCert9Name(strList.get(107));
                    permitInDto.setExtraCert9(strList.get(108));
                    permitInDto.setExtraCert10(strList.get(109));
                    // 仕入書番号
                    permitInDto.setInvoiceType(strList.get(110));
                    permitInDto.setInvoiceNo(strList.get(111));
                    // 仕入書(電子）
                    permitInDto.setElecInvoiceNo(strList.get(112));
                    // 仕入書価格
                    permitInDto.setInvoiceSegCd(strList.get(113));
                    permitInDto.setInvoiceConditionCd(strList.get(114));
                    permitInDto.setInvoiceCurrencyCd(strList.get(115));
                    int invoiceValue = Integer.parseInt(strList.get(116).trim());
                    String formattedNumber = String.format("%,d", invoiceValue);
                    permitInDto.setInvoiceValue(formattedNumber);
                    // 運賃
                    permitInDto.setFareClassCd(strList.get(117));
                    permitInDto.setFareClassCd2(strList.get(118));
                    int fareCurrencyAmount = Integer.parseInt(strList.get(119).trim());
                    formattedNumber = String.format("%,d", fareCurrencyAmount);
                    permitInDto.setFareCurrencyAmount(formattedNumber);
                    // 保険
                    permitInDto.setInsuranceClassCd(strList.get(120));
                    permitInDto.setInsuranceCurrencyCd(strList.get(121));
                    permitInDto.setInsuranceAmount(strList.get(122));
                    permitInDto.setComPreInsuranceNumber(strList.get(123));
                    // 通関金額
                    permitInDto.setClearanceType(strList.get(124));
                    double clearanceValue = Double.parseDouble(strList.get(125).trim());
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                    formattedNumber = decimalFormat.format(clearanceValue);
                    permitInDto.setClearanceValue(formattedNumber);
                    // 評価
                    permitInDto.setEvalClassCd(strList.get(126));
                    permitInDto.setReportReceiptNo1(strList.get(127));
                    permitInDto.setReportReceiptNo2(strList.get(128));
                    permitInDto.setReportReceiptNo3(strList.get(129));
                    // 補正
                    permitInDto.setEvalCorrectClassCd(strList.get(130));
                    permitInDto.setCorrectBaseCurrencyCd(strList.get(131));
                    permitInDto.setBaseAmountCorrect(strList.get(132));
                    permitInDto.setCorrectFormula(strList.get(133));
                    permitInDto.setEvalStandardIdentification1(strList.get(134));
                    permitInDto.setEvalCateCd1(strList.get(135));
                    permitInDto.setComPreEvalCorrectFormula1(strList.get(136));
                    permitInDto.setEvalStandardIdentification2(strList.get(137));
                    permitInDto.setEvalCateCd2(strList.get(138));
                    permitInDto.setComPreEvalCorrectFormula2(strList.get(139));
                    permitInDto.setEvalStandardIdentification3(strList.get(140));
                    permitInDto.setEvalCateCd3(strList.get(141));
                    permitInDto.setComPreEvalCorrectFormula3(strList.get(142));
                    // 事前教示（評価）
                    permitInDto.setPreLevel1(strList.get(143));
                    permitInDto.setPreLevel2(strList.get(144));

                    // ＢＰＲ合計
                    double totalTaxPrice2 = Double.parseDouble(strList.get(145).trim());
                    formattedNumber = decimalFormat.format(totalTaxPrice2);
                    permitInDto.setTotalTaxPrice2(formattedNumber);
                    permitInDto.setTotalInputIdentify(strList.get(146));
                    permitInDto.setComputedCd(strList.get(147));
                    // 原産地証明
                    permitInDto.setComputedCd(strList.get(147));
                    // 戻税申告
                    permitInDto.setCertOrigin(strList.get(148));
                    // 内容点検結果
                    permitInDto.setInspectResult(strList.get(150));
                    // 税科目
                    permitInDto.setTaxSubjectCd1(strList.get(151));
                    permitInDto.setTaxSubjectCd2(strList.get(155));
                    permitInDto.setTaxSubjectCd3(strList.get(159));
                    permitInDto.setTaxSubject1(strList.get(152));
                    permitInDto.setTaxSubject2(strList.get(156));
                    permitInDto.setTaxSubject3(strList.get(160));
                    // 税額合計
                    int totalTaxAmount1 = Integer.parseInt(strList.get(153).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount1);
                    permitInDto.setTotalTaxAmount1("¥" + formattedNumber);

                    int totalTaxAmount2 = Integer.parseInt(strList.get(157).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount2);
                    permitInDto.setTotalTaxAmount2("¥" + formattedNumber);

                    int totalTaxAmount3 = Integer.parseInt(strList.get(161).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount3);
                    permitInDto.setTotalTaxAmount3("¥" + formattedNumber);

                    // 欄数
                    permitInDto.setTotalTaxCnt1(strList.get(154));
                    permitInDto.setTotalTaxCnt2(strList.get(158));
                    permitInDto.setTotalTaxCnt3(strList.get(162));
                    // 納税額合計
                    int totalTaxPaid = Integer.parseInt(strList.get(179).trim());
                    formattedNumber = String.format("%,d", totalTaxPaid);
                    permitInDto.setTotalTaxPaid("¥" + formattedNumber);
                    // 担保額
                    permitInDto.setCollateralAmount(strList.get(180));
                    // 通貨レート
                    permitInDto.setConversionCurrencyCd1(strList.get(181));
                    permitInDto.setConversionCurrency1(strList.get(182));
                    permitInDto.setConversionCurrencyCd2(strList.get(183));
                    permitInDto.setConversionCurrency2(strList.get(184));
                    permitInDto.setConversionCurrencyCd3(strList.get(185));
                    permitInDto.setConversionCurrency3(strList.get(186));
                    // 都道府県
                    permitInDto.setProvinceCd(strList.get(188));
                    // 口座
                    permitInDto.setAccountIdentify(strList.get(191));
                    // 都道府県
                    permitInDto.setPaymentMethod(strList.get(194));
                    // 構成
                    permitInDto.setConfigure(strList.get(195));
                    permitInDto.setDeclareCnt(strList.get(196));
                    // 記事(税関)
                    permitInDto.setForCustom(strList.get(197));
                    // 輸 入 者(入力)
                    String importDealer = strList.get(198);
                    permitInDto.setImportDealer(importDealer.substring(0, importDealer.length() - 5).trim());
                    permitInDto.setImportDealer2(importDealer.substring(importDealer.length() - 5).trim());
                    // 記事(通関)
                    permitInDto.setForCustomsAgents(strList.get(199));
                    // 輸入取引者(入力)
                    permitInDto.setImporterInput(strList.get(200));
                    // 記事(荷主)
                    permitInDto.setShipper(strList.get(201));
                    // 社内整理番号
                    permitInDto.setCompanyReferenceNo(strList.get(202));
                    // 荷主セクションコード
                    permitInDto.setShipperSectionCd(strList.get(203));
                    //荷主Ｒｅｆ Ｎｏ.
                    permitInDto.setShipperRefNo(strList.get(204));
                    // 利用者整理番号
                    permitInDto.setCustomerUsageNo(strList.get(205));
                    // ［税関通知欄］
                    permitInDto.setCustomNoticeSection(strList.get(206));
                    permitInDto.setDirectorName(strList.get(207));
                    // 輸入許可日
                    String permitDate = strList.get(208).trim();
                    if (permitDate == null || permitDate.isEmpty()) {
                        permitInDto.setPermitDate(strList.get(208));
                    } else {
                        String formattedPermitDate = permitDate.substring(0, 4) + "/" + permitDate.substring(4, 6) + "/" + permitDate.substring(6);
                        permitInDto.setPermitDate(formattedPermitDate);
                    }
                    // 審査終了年月日
                    String examDate = strList.get(209).trim();
                    if (examDate == null || examDate.isEmpty()) {
                        permitInDto.setExamDate(strList.get(209));
                    } else {
                        String formattedExamDate = examDate.substring(0, 4) + "/" + examDate.substring(4, 6) + "/" + examDate.substring(6);
                        permitInDto.setExamDate(formattedExamDate);
                    }

                    permitInDto.setItemColumns(strList.get(i++).trim());
                    permitInDto.setItemDestColumn(strList.get(i++).trim());

                    String itemNo = strList.get(i++).trim();
                    if (itemNo.isEmpty()) {
                        permitInDto.setItemNo(" ");
                    } else {
                        String formattedItemNo = String.format("%s.%s-%s", itemNo.substring(0, 4), itemNo.substring(4, 6), itemNo.substring(6));
                        permitInDto.setItemNo(formattedItemNo);
                    }
                    permitInDto.setItemNoUnit(strList.get(i++).trim());
                    permitInDto.setPriceReConfirm(strList.get(i++).trim());
                    permitInDto.setItemName(strList.get(i++).trim());
                    permitInDto.setQuantity1(strList.get(i++).trim());
                    permitInDto.setQuantityUnit(strList.get(i++).trim());
                    permitInDto.setTaxListNo(strList.get(i++).trim());
                    permitInDto.setQuantity2(strList.get(i++).trim());
                    permitInDto.setQuantityUnit2(strList.get(i++).trim());

                    String declearedValue = strList.get(i++).trim();
                    if (declearedValue.isEmpty()) {
                        permitInDto.setDeclaredValue("");
                    } else {
                        int declearedValueInt = Integer.parseInt(declearedValue);
                        formattedNumber = String.format("%,d", declearedValueInt);
                        permitInDto.setDeclaredValue("¥" + formattedNumber);
                    }

                    permitInDto.setQuantity3(strList.get(i++).trim());
                    permitInDto.setQuantityUnit3(strList.get(i++).trim());
                    permitInDto.setQuantity4(strList.get(i++).trim());
                    permitInDto.setDeclaredValue2(strList.get(i++).trim());
                    permitInDto.setQuantityUnit4(strList.get(i++).trim());
                    permitInDto.setTariffCd(strList.get(i++).trim());
                    permitInDto.setTariffRate(strList.get(i++).trim());
                    permitInDto.setImportOrder(strList.get(i++).trim());
                    permitInDto.setImportOrder2(strList.get(i++).trim());
                    permitInDto.setPreferTreat(strList.get(i++).trim());

                    String customDuties = strList.get(i++).trim();
                    if(customDuties == ""){
                        permitInDto.setCustomsDuties("");
                    }else{
                        int value = Integer.parseInt(customDuties);
                        formattedNumber = String.format("%,d", value);
                        permitInDto.setCustomsDuties("¥" + formattedNumber);
                    }


                    double bprDivision = Double.parseDouble(strList.get(i++).trim());
                    formattedNumber = decimalFormat.format(bprDivision);
                    permitInDto.setBprDivision(formattedNumber);


                    permitInDto.setTaxReduceAmount(strList.get(i++).trim());
                    permitInDto.setBprAmountCd(strList.get(i++).trim());
                    permitInDto.setBprAmount(strList.get(i++).trim());
                    permitInDto.setTaxReduceAmount2(strList.get(i++).trim());
                    permitInDto.setFreightProPort(strList.get(i++).trim());
                    permitInDto.setFreightProPrt2(strList.get(i++).trim());
                    permitInDto.setCountryOri1(strList.get(i++).trim());
                    permitInDto.setCountryOri2(strList.get(i++).trim());
                    permitInDto.setCountryOri3(strList.get(i++).trim());
                    // 292
                    permitInDto.setExemptionTax(strList.get(i++).trim());
                    permitInDto.setExemptionTax2(strList.get(i++).trim());
                    permitInDto.setExemptionTax3(strList.get(i++).trim());
                    permitInDto.setExemptionTax4(strList.get(i++).trim());
                    permitInDto.setExemptionTax5(strList.get(i++).trim());

                    permitInDto.setDeCree(strList.get(i++).trim());
                    permitInDto.setAppendedTable(strList.get(i++).trim());

                    permitInDto.setAppendedTable2(strList.get(i++).trim());
                    permitInDto.setAppendedTable3(strList.get(i++).trim());

                    permitInDto.setPreTeaching(strList.get(i++).trim());
                    permitInDto.setPreCountryOri(strList.get(i++).trim());

                    // item　repetition
                    permitInDto.setDomesticTax(strList.get(i++).trim());
                    permitInDto.setItemClass(strList.get(i++).trim());

                    int itemBaseTax = Integer.parseInt(strList.get(i++).trim());
                    formattedNumber = String.format("%,d", itemBaseTax);
                    permitInDto.setItemBaseTax("¥" + formattedNumber);


                    permitInDto.setItemTaxBaseQty1(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd1(strList.get(i++).trim());
                    permitInDto.setItemBaseTax2(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQty2(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd2(strList.get(i++).trim());
                    permitInDto.setItemReDuctedTax(strList.get(i++).trim());
                    // 7.8
                    int itemAmountTax = Integer.parseInt(strList.get(i++).trim());
                    formattedNumber = String.format("%,d", itemAmountTax);
                    permitInDto.setItemAmountTax("¥" + formattedNumber);

                    permitInDto.setItemReDuctedTaxCd(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount1(strList.get(i++).trim());
                    permitInDto.setItemArticle1(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount2(strList.get(i++).trim());
                    permitInDto.setItemArticle2(strList.get(i++).trim());

                    permitInDto.setDomesticTax2(strList.get(i++).trim());
                    permitInDto.setItemClass2(strList.get(i++).trim());
                    // null check
                    String itemBaseTax3 = strList.get(i++).trim();
                    if (itemBaseTax3.isEmpty()) {
                        permitInDto.setItemBaseTax3("");
                    } else {
                        int baseTax3 = Integer.parseInt(itemBaseTax3);
                        formattedNumber = String.format("%,d", baseTax3);
                        permitInDto.setItemBaseTax3("¥" + formattedNumber);
                    }

                    permitInDto.setItemTaxBaseQty3(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd3(strList.get(i++).trim());
                    permitInDto.setItemBaseTax4(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQty4(strList.get(i++).trim());
                    permitInDto.setItemBaseQtyCd4(strList.get(i++).trim());
                    permitInDto.setItemReDuctedTax2(strList.get(i++).trim());

                    String itemAmountTax2 = strList.get(i++).trim();
                    if (itemAmountTax2.isEmpty()) {
                        permitInDto.setItemAmountTax2("");
                    } else {
                        int amountTax2 = Integer.parseInt(itemAmountTax2);
                        formattedNumber = String.format("%,d", amountTax2);
                        permitInDto.setItemAmountTax2(formattedNumber);
                    }

                    permitInDto.setItemReDuctedTaxCd2(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount3(strList.get(i++).trim());
                    permitInDto.setItemArticle3(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount4(strList.get(i++).trim());
                    permitInDto.setItemArticle4(strList.get(i).trim());
                    i += 60;

                    occs006PermitInDtoList.add(permitInDto);
                }
                resultList.addAll(occs006PermitInDtoList);
            }
        } else if (impPeFileNameList.size() >= 1) {
            for (String peFileName : fileNameList) {
                String addPath = peFileName.substring(12, 16) + "/" + peFileName.substring(16, 18) + "/" + peFileName.substring(18, 20) + "/";
                String key = OCCS006PermitConstants.GET_PE_IMP_S3PATH + addPath + peFileName;
                byte[] data = awsS3.get(key);
                String strValue = new String(data, Charset.forName("Shift_JIS"));
                String[] strArr = strValue.split("\n");

                List<String> strList = new ArrayList<String>();
                for (String str : strArr) {
                    strList.add(str);
                }
                for (int i = 258; i < strList.size(); i++) {
                    OCCS006PermitInDto permitInDto = new OCCS006PermitInDto();
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
                    Date date = format.parse(peFileName.substring(12, 24));

                    String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
                    String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
                    String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
                    String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");
                    permitInDto.setDateyyyyMMdd(yyyyMMdd);
                    permitInDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
                    permitInDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
                    permitInDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
                    permitInDto.setFilePathList("Pe");

                    // 代表統番
                    permitInDto.setRepresentPermitNo(strList.get(3).trim());
                    permitInDto.setRepresentPermitNoCd(strList.get(4).trim());
                    // 申告種別
                    permitInDto.setPermitClass(strList.get(5).trim() + strList.get(6).trim() + strList.get(7).trim());
                    permitInDto.setPermitClassCd2(strList.get(8).trim());
                    // 区分
                    permitInDto.setPermitClassCd(strList.get(9).trim());
                    // あて先税関
                    permitInDto.setDestCustom(strList.get(10).trim());
                    // 部門 11
                    permitInDto.setDepartmentCd(strList.get(11).trim());
                    // 申告年月日
                    String reportDate = strList.get(12).trim();
                    String formattedDate = reportDate.substring(0, 4).trim() + "/" + reportDate.substring(4, 6).trim() + "/" + reportDate.substring(6).trim();
                    permitInDto.setReportDate(formattedDate);
                    // 申告種別
                    String reportClassCd = strList.get(16);
                    String forMattedReportClassCd = reportClassCd.substring(0, 3).trim() + " " + reportClassCd.substring(3, 7).trim() + " " + reportClassCd.substring(7).trim();
                    permitInDto.setReportClassCd(forMattedReportClassCd);
                    // 申告条件
                    permitInDto.setReportCondition(strList.get(18));
                    //申告予定年月日
                    String reportDate2 = strList.get(19).trim();
                    String formattedDate2 = reportDate2.substring(0, 4).trim() + "/" + reportDate2.substring(4, 6).trim() + "/" + reportDate2.substring(6).trim();
                    permitInDto.setReportDate2(formattedDate2);
                    // 本申告 21
                    permitInDto.setIsReport(strList.get(20).trim());
                    // 輸　　入　　者 22
                    String impNo = strList.get(21);
                    permitInDto.setImpNo(impNo.substring(0, impNo.length() - 4).trim());
                    permitInDto.setImpNo2(impNo.substring(impNo.length() - 5).trim());
                    // 住 所
                    permitInDto.setImpCustomerName(strList.get(22).trim());
                    permitInDto.setImpCustomerPostCd(strList.get(23).trim());
                    permitInDto.setImpCustomerAddr1(strList.get(24).trim());
                    permitInDto.setImpCustomerAddr2(strList.get(25).trim());
                    permitInDto.setImpCustomerAddr3(strList.get(26).trim());
                    // impCustomerTel
                    permitInDto.setImpCustomerTel(strList.get(28).trim());
                    // 税関事務管理人
                    permitInDto.setCustomerMasterCd(strList.get(29));
                    permitInDto.setCustomerMasterNo(strList.get(30));
                    permitInDto.setCustomerMasterName(strList.get(31));
                    // 税関事務管理人
                    permitInDto.setImporterCd(strList.get(32));
                    permitInDto.setImporterName(strList.get(33).trim());
                    // 仕 出 人
                    permitInDto.setConsigneeCd(strList.get(34).trim());
                    permitInDto.setConsigneeName(strList.get(35).trim());
                    permitInDto.setConsigneePostCd(strList.get(36).trim());
                    // 住 所
                    permitInDto.setConsigneeAddr1(strList.get(37).trim());
                    permitInDto.setConsigneeAddr2(strList.get(38).trim());
                    permitInDto.setConsigneeAddr3(strList.get(39).trim());
                    permitInDto.setConsigneeAddr4(strList.get(40).trim());
                    permitInDto.setConSigneeCountryCd(strList.get(41).trim());
                    // 輸出 の 委託者
                    permitInDto.setExpConsignorName(strList.get(42));
                    // 代理人
                    permitInDto.setAgentCd(strList.get(43));
                    permitInDto.setAgentDiv(strList.get(44));
                    permitInDto.setCustomCd(strList.get(45));
                    permitInDto.setInspectPerson(strList.get(46));
                    // AWB番号
                    String awbNo = strList.get(47);
                    String modifiedAwbNo = awbNo.substring(0, 3) + "-" + awbNo.substring(3);
                    permitInDto.setAwbNo(modifiedAwbNo);
                    // ＭＡＷＢ番号
                    String newCwbNo = strList.get(48);
                    String modifiedCwbNo = newCwbNo.substring(0, 3) + "-" + newCwbNo.substring(3);
                    permitInDto.setCwbNo(modifiedCwbNo);
                    // 取卸港
                    permitInDto.setArrDest(strList.get(53));
                    permitInDto.setArrFromTo(strList.get(54));
                    //　積出地
                    permitInDto.setCargoFlt(strList.get(55));
                    permitInDto.setCargoCity(strList.get(56));
                    // 積載機名
                    permitInDto.setLoadingName(strList.get(58));
                    // 入港年月日
                    String cargoInDate = strList.get(59);
                    String formattedCargoInDate = cargoInDate.substring(0, 4).trim() + "/" + cargoInDate.substring(4, 6).trim() + "/" + cargoInDate.substring(6).trim();
                    permitInDto.setCargoInDate(formattedCargoInDate);
                    // 蔵置税関
                    permitInDto.setWareHouseName(strList.get(60).trim());
                    permitInDto.setWareHouseCd(strList.get(61).trim());
                    // 貨物個数
                    permitInDto.setCarryingPiece(strList.get(62).trim());
                    // 保税地域
                    permitInDto.setBondedWareHouseCd(strList.get(64));
                    permitInDto.setBondedLocation(strList.get(65));
                    // 貨物重量
                    permitInDto.setCargoWeight(strList.get(66));
                    permitInDto.setCargoUnitCd(strList.get(67));
                    // 搬入予定
                    permitInDto.setBondedAreaCd(strList.get(68));
                    permitInDto.setBondedAreaName(strList.get(69));
                    // 最初蔵入年月日
                    String cargoInitialDate = strList.get(71).trim();
                    if (cargoInitialDate == null || cargoInitialDate.isEmpty()) {
                        permitInDto.setCargoInitialDate(strList.get(71));
                    } else {
                        String formattedInitialDate = cargoInitialDate.substring(0, 4).trim() + "/" + cargoInitialDate.substring(4, 6).trim() + "/" + cargoInitialDate.substring(6).trim();
                        permitInDto.setCargoInitialDate(formattedInitialDate);
                    }
                    // 貿易形態別符号
                    permitInDto.setTradeCode(strList.get(73));
                    // 調査用符号
                    permitInDto.setInvestCd(strList.get(74));
                    // 貿易管理令
                    permitInDto.setTradeOrder(strList.get(76));
                    // 輸入承認証
                    permitInDto.setAuthCertify(strList.get(77));
                    // 関税法70条関係許可承認
                    permitInDto.setImpPermission(strList.get(78));
                    // 共通管理番号
                    permitInDto.setCmManageNo(strList.get(83));
                    // 食品
                    permitInDto.setFoodCertify(strList.get(84));
                    permitInDto.setFoodCertifiedNo(strList.get(85));
                    // 植防
                    permitInDto.setPlantCertify(strList.get(86));
                    permitInDto.setPlantCertifiedNo(strList.get(87));
                    // 動検
                    permitInDto.setAnimalCertify(strList.get(88));
                    permitInDto.setAnimalCertifiedNo(strList.get(89));
                    // 輸入承認証番号等
                    permitInDto.setExtraCertName1(strList.get(91));
                    permitInDto.setExtraCert1(strList.get(92));
                    permitInDto.setExtraCert2Name(strList.get(93));
                    permitInDto.setExtraCert2(strList.get(94));
                    permitInDto.setExtraCert3Name(strList.get(95));
                    permitInDto.setExtraCert3(strList.get(96));
                    permitInDto.setExtraCert4Name(strList.get(97));
                    permitInDto.setExtraCert4(strList.get(98));
                    permitInDto.setExtraCert5Name(strList.get(99));
                    permitInDto.setExtraCert5(strList.get(100));
                    permitInDto.setExtraCert6Name(strList.get(101));
                    permitInDto.setExtraCert6(strList.get(102));
                    permitInDto.setExtraCert7Name(strList.get(103));
                    permitInDto.setExtraCert7(strList.get(104));
                    permitInDto.setExtraCert8Name(strList.get(105));
                    permitInDto.setExtraCert8(strList.get(106));
                    permitInDto.setExtraCert9Name(strList.get(107));
                    permitInDto.setExtraCert9(strList.get(108));
                    permitInDto.setExtraCert10(strList.get(109));
                    // 仕入書番号
                    permitInDto.setInvoiceType(strList.get(110));
                    permitInDto.setInvoiceNo(strList.get(111));
                    // 仕入書(電子）
                    permitInDto.setElecInvoiceNo(strList.get(112));
                    // 仕入書価格
                    permitInDto.setInvoiceSegCd(strList.get(113));
                    permitInDto.setInvoiceConditionCd(strList.get(114));
                    permitInDto.setInvoiceCurrencyCd(strList.get(115));
                    int invoiceValue = Integer.parseInt(strList.get(116).trim());
                    String formattedNumber = String.format("%,d", invoiceValue);
                    permitInDto.setInvoiceValue(formattedNumber);
                    // 運賃
                    permitInDto.setFareClassCd(strList.get(117));
                    permitInDto.setFareClassCd2(strList.get(118));
                    int fareCurrencyAmount = Integer.parseInt(strList.get(119).trim());
                    formattedNumber = String.format("%,d", fareCurrencyAmount);
                    permitInDto.setFareCurrencyAmount(formattedNumber);
                    // 保険
                    permitInDto.setInsuranceClassCd(strList.get(120));
                    permitInDto.setInsuranceCurrencyCd(strList.get(121));
                    permitInDto.setInsuranceAmount(strList.get(122));
                    permitInDto.setComPreInsuranceNumber(strList.get(123));
                    // 通関金額
                    permitInDto.setClearanceType(strList.get(124));
                    double clearanceValue = Double.parseDouble(strList.get(125).trim());
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.00");
                    formattedNumber = decimalFormat.format(clearanceValue);
                    permitInDto.setClearanceValue(formattedNumber);
                    // 評価
                    permitInDto.setEvalClassCd(strList.get(126));
                    permitInDto.setReportReceiptNo1(strList.get(127));
                    permitInDto.setReportReceiptNo2(strList.get(128));
                    permitInDto.setReportReceiptNo3(strList.get(129));
                    // 補正
                    permitInDto.setEvalCorrectClassCd(strList.get(130));
                    permitInDto.setCorrectBaseCurrencyCd(strList.get(131));
                    permitInDto.setBaseAmountCorrect(strList.get(132));
                    permitInDto.setCorrectFormula(strList.get(133));
                    permitInDto.setEvalStandardIdentification1(strList.get(134));
                    permitInDto.setEvalCateCd1(strList.get(135));
                    permitInDto.setComPreEvalCorrectFormula1(strList.get(136));
                    permitInDto.setEvalStandardIdentification2(strList.get(137));
                    permitInDto.setEvalCateCd2(strList.get(138));
                    permitInDto.setComPreEvalCorrectFormula2(strList.get(139));
                    permitInDto.setEvalStandardIdentification3(strList.get(140));
                    permitInDto.setEvalCateCd3(strList.get(141));
                    permitInDto.setComPreEvalCorrectFormula3(strList.get(142));
                    // 事前教示（評価）
                    permitInDto.setPreLevel1(strList.get(143));
                    permitInDto.setPreLevel2(strList.get(144));

                    // ＢＰＲ合計
                    double totalTaxPrice2 = Double.parseDouble(strList.get(145).trim());
                    formattedNumber = decimalFormat.format(totalTaxPrice2);
                    permitInDto.setTotalTaxPrice2(formattedNumber);
                    permitInDto.setTotalInputIdentify(strList.get(146));
                    permitInDto.setComputedCd(strList.get(147));
                    // 原産地証明
                    permitInDto.setComputedCd(strList.get(147));
                    // 戻税申告
                    permitInDto.setCertOrigin(strList.get(148));
                    // 内容点検結果
                    permitInDto.setInspectResult(strList.get(150));
                    // 税科目
                    permitInDto.setTaxSubjectCd1(strList.get(151));
                    permitInDto.setTaxSubjectCd2(strList.get(155));
                    permitInDto.setTaxSubjectCd3(strList.get(159));
                    permitInDto.setTaxSubject1(strList.get(152));
                    permitInDto.setTaxSubject2(strList.get(156));
                    permitInDto.setTaxSubject3(strList.get(160));
                    // 税額合計
                    int totalTaxAmount1 = Integer.parseInt(strList.get(153).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount1);
                    permitInDto.setTotalTaxAmount1("¥" + formattedNumber);

                    int totalTaxAmount2 = Integer.parseInt(strList.get(157).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount2);
                    permitInDto.setTotalTaxAmount2("¥" + formattedNumber);

                    int totalTaxAmount3 = Integer.parseInt(strList.get(161).trim());
                    formattedNumber = String.format("%,d", totalTaxAmount3);
                    permitInDto.setTotalTaxAmount3("¥" + formattedNumber);

                    // 欄数
                    permitInDto.setTotalTaxCnt1(strList.get(154));
                    permitInDto.setTotalTaxCnt2(strList.get(158));
                    permitInDto.setTotalTaxCnt3(strList.get(162));
                    // 納税額合計
                    int totalTaxPaid = Integer.parseInt(strList.get(179).trim());
                    formattedNumber = String.format("%,d", totalTaxPaid);
                    permitInDto.setTotalTaxPaid("¥" + formattedNumber);
                    // 担保額
                    permitInDto.setCollateralAmount(strList.get(180));
                    // 通貨レート
                    permitInDto.setConversionCurrencyCd1(strList.get(181));
                    permitInDto.setConversionCurrency1(strList.get(182));
                    permitInDto.setConversionCurrencyCd2(strList.get(183));
                    permitInDto.setConversionCurrency2(strList.get(184));
                    permitInDto.setConversionCurrencyCd3(strList.get(185));
                    permitInDto.setConversionCurrency3(strList.get(186));
                    // 都道府県
                    permitInDto.setProvinceCd(strList.get(188));
                    // 口座
                    permitInDto.setAccountIdentify(strList.get(191));
                    // 都道府県
                    permitInDto.setPaymentMethod(strList.get(194));
                    // 構成
                    permitInDto.setConfigure(strList.get(195));
                    permitInDto.setDeclareCnt(strList.get(196));
                    // 記事(税関)
                    permitInDto.setForCustom(strList.get(197));
                    // 輸 入 者(入力)
                    String importDealer = strList.get(198);
                    permitInDto.setImportDealer(importDealer.substring(0, importDealer.length() - 5).trim());
                    permitInDto.setImportDealer2(importDealer.substring(importDealer.length() - 5).trim());
                    // 記事(通関)
                    permitInDto.setForCustomsAgents(strList.get(199));
                    // 輸入取引者(入力)
                    permitInDto.setImporterInput(strList.get(200));
                    // 記事(荷主)
                    permitInDto.setShipper(strList.get(201));
                    // 社内整理番号
                    permitInDto.setCompanyReferenceNo(strList.get(202));
                    // 荷主セクションコード
                    permitInDto.setShipperSectionCd(strList.get(203));
                    //荷主Ｒｅｆ Ｎｏ.
                    permitInDto.setShipperRefNo(strList.get(204));
                    // 利用者整理番号
                    permitInDto.setCustomerUsageNo(strList.get(205));
                    // ［税関通知欄］
                    permitInDto.setCustomNoticeSection(strList.get(206));
                    permitInDto.setDirectorName(strList.get(207));
                    // 輸入許可日
                    String permitDate = strList.get(208).trim();
                    if (permitDate == null || permitDate.isEmpty()) {
                        permitInDto.setPermitDate(strList.get(208));
                    } else {
                        String formattedPermitDate = permitDate.substring(0, 4) + "/" + permitDate.substring(4, 6) + "/" + permitDate.substring(6);
                        permitInDto.setPermitDate(formattedPermitDate);
                    }
                    // 審査終了年月日
                    String examDate = strList.get(209).trim();
                    if (examDate == null || examDate.isEmpty()) {
                        permitInDto.setExamDate(strList.get(209));
                    } else {
                        String formattedExamDate = examDate.substring(0, 4) + "/" + examDate.substring(4, 6) + "/" + examDate.substring(6);
                        permitInDto.setExamDate(formattedExamDate);
                    }

                    permitInDto.setItemColumns(strList.get(i++).trim());
                    permitInDto.setItemDestColumn(strList.get(i++).trim());

                    String itemNo = strList.get(i++).trim();
                    if (itemNo.isEmpty()) {
                        permitInDto.setItemNo(" ");
                    } else {
                        String formattedItemNo = String.format("%s.%s-%s", itemNo.substring(0, 4), itemNo.substring(4, 6), itemNo.substring(6));
                        permitInDto.setItemNo(formattedItemNo);
                    }
                    permitInDto.setItemNoUnit(strList.get(i++).trim());
                    permitInDto.setPriceReConfirm(strList.get(i++).trim());
                    permitInDto.setItemName(strList.get(i++).trim());
                    permitInDto.setQuantity1(strList.get(i++).trim());
                    permitInDto.setQuantityUnit(strList.get(i++).trim());
                    permitInDto.setTaxListNo(strList.get(i++).trim());
                    permitInDto.setQuantity2(strList.get(i++).trim());
                    permitInDto.setQuantityUnit2(strList.get(i++).trim());

                    String declearedValue = strList.get(i++).trim();
                    if (declearedValue.isEmpty()) {
                        permitInDto.setDeclaredValue("");
                    } else {
                        int declearedValueInt = Integer.parseInt(declearedValue);
                        formattedNumber = String.format("%,d", declearedValueInt);
                        permitInDto.setDeclaredValue("¥" + formattedNumber);
                    }

                    permitInDto.setQuantity3(strList.get(i++).trim());
                    permitInDto.setQuantityUnit3(strList.get(i++).trim());
                    permitInDto.setQuantity4(strList.get(i++).trim());
                    permitInDto.setDeclaredValue2(strList.get(i++).trim());
                    permitInDto.setQuantityUnit4(strList.get(i++).trim());
                    permitInDto.setTariffCd(strList.get(i++).trim());
                    permitInDto.setTariffRate(strList.get(i++).trim());
                    permitInDto.setImportOrder(strList.get(i++).trim());
                    permitInDto.setImportOrder2(strList.get(i++).trim());
                    permitInDto.setPreferTreat(strList.get(i++).trim());

                    int customsDuties = Integer.parseInt(strList.get(i++).trim());
                    formattedNumber = String.format("%,d", customsDuties);
                    permitInDto.setCustomsDuties("¥" + formattedNumber);


                    double bprDivision = Double.parseDouble(strList.get(i++).trim());
                    formattedNumber = decimalFormat.format(bprDivision);
                    permitInDto.setBprDivision(formattedNumber);


                    permitInDto.setTaxReduceAmount(strList.get(i++).trim());
                    permitInDto.setBprAmountCd(strList.get(i++).trim());
                    permitInDto.setBprAmount(strList.get(i++).trim());
                    permitInDto.setTaxReduceAmount2(strList.get(i++).trim());
                    permitInDto.setFreightProPort(strList.get(i++).trim());
                    /** have to check */
                    permitInDto.setFreightProPrt2(strList.get(i++).trim());
                    permitInDto.setCountryOri1(strList.get(i++).trim());
                    permitInDto.setCountryOri2(strList.get(i++).trim());
                    permitInDto.setCountryOri3(strList.get(i++).trim());
                    // 292
                    permitInDto.setExemptionTax(strList.get(i++).trim());
                    permitInDto.setExemptionTax2(strList.get(i++).trim());
                    permitInDto.setExemptionTax3(strList.get(i++).trim());
                    permitInDto.setExemptionTax4(strList.get(i++).trim());
                    permitInDto.setExemptionTax5(strList.get(i++).trim());

                    permitInDto.setDeCree(strList.get(i++).trim());
                    permitInDto.setAppendedTable(strList.get(i++).trim());

                    permitInDto.setAppendedTable2(strList.get(i++).trim());
                    permitInDto.setAppendedTable3(strList.get(i++).trim());

                    permitInDto.setPreTeaching(strList.get(i++).trim());
                    permitInDto.setPreCountryOri(strList.get(i++).trim());

                    // item　repetition
                    permitInDto.setDomesticTax(strList.get(i++).trim());
                    permitInDto.setItemClass(strList.get(i++).trim());

                    int itemBaseTax = Integer.parseInt(strList.get(i++).trim());
                    formattedNumber = String.format("%,d", itemBaseTax);
                    permitInDto.setItemBaseTax("¥" + formattedNumber);


                    permitInDto.setItemTaxBaseQty1(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd1(strList.get(i++).trim());
                    permitInDto.setItemBaseTax2(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQty2(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd2(strList.get(i++).trim());
                    permitInDto.setItemReDuctedTax(strList.get(i++).trim());
                    // 7.8
                    int itemAmountTax = Integer.parseInt(strList.get(i++).trim());
                    formattedNumber = String.format("%,d", itemAmountTax);
                    permitInDto.setItemAmountTax("¥" + formattedNumber);

                    // 이게 문제임
                    permitInDto.setItemReDuctedTaxCd(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount1(strList.get(i++).trim());
                    permitInDto.setItemArticle1(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount2(strList.get(i++).trim());
                    permitInDto.setItemArticle2(strList.get(i++).trim());

                    permitInDto.setDomesticTax2(strList.get(i++).trim());
                    permitInDto.setItemClass2(strList.get(i++).trim());
                    // null check
                    String itemBaseTax3 = strList.get(i++).trim();
                    if (itemBaseTax3.isEmpty()) {
                        permitInDto.setItemBaseTax3("");
                    } else {
                        int baseTax3 = Integer.parseInt(itemBaseTax3);
                        formattedNumber = String.format("%,d", baseTax3);
                        permitInDto.setItemBaseTax3("¥" + formattedNumber);
                    }

                    permitInDto.setItemTaxBaseQty3(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQtyCd3(strList.get(i++).trim());
                    permitInDto.setItemBaseTax4(strList.get(i++).trim());
                    permitInDto.setItemTaxBaseQty4(strList.get(i++).trim());
                    permitInDto.setItemBaseQtyCd4(strList.get(i++).trim());
                    permitInDto.setItemReDuctedTax2(strList.get(i++).trim());

                    String itemAmountTax2 = strList.get(i++).trim();
                    if (itemAmountTax2.isEmpty()) {
                        permitInDto.setItemAmountTax2("");
                    } else {
                        int amountTax2 = Integer.parseInt(itemAmountTax2);
                        formattedNumber = String.format("%,d", amountTax2);
                        permitInDto.setItemAmountTax2(formattedNumber);
                    }

                    permitInDto.setItemReDuctedTaxCd2(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount3(strList.get(i++).trim());
                    permitInDto.setItemArticle3(strList.get(i++).trim());
                    permitInDto.setItemReducedTaxAmount4(strList.get(i++).trim());
                    permitInDto.setItemArticle4(strList.get(i).trim());

                    i += 60;

                    occs006PermitInDtoList.add(permitInDto);
                }
                resultList.addAll(occs006PermitInDtoList);
            }
        }
        return resultList;
    }
}