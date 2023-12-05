package com.kse.wmsv2.oc_cs_006.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006BondInBillConstants;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006ExConstants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ExDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006ExMapper;
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
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class OCCS006ExService {
    @Autowired
    OCCS006ExMapper occs006ExMapper;
    @Autowired
    OCCS007ScreenMapper occs007ScreenMapper;
    @Autowired
    OCCS007Service occs007Service;
    @Autowired
    AwsS3 awsS3;

    public OCCS006ResultDto printExam(List<?> dataList) throws Exception {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();
        List<String> pdfPath = new ArrayList<String>();
        // 取得データ有無チェック
        if (dataList.size() < 1) {
            String msg = OCCS006ExConstants.EX_ERROR102;
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }

        // 選択したデータに対して繰返し処理
        for (int i = 0; i < dataList.size(); i++) {
            // cwbNo取得
            if (dataList.get(i) instanceof Map<?, ?>) {
                Map<?, ?> data = (Map<?, ?>) dataList.get(i);
                String cwbNo = data.get("cwbNo").toString();

                //　cwbNo取得失敗の際にはエラー処理
                if (StringUtils.isEmpty(cwbNo)) {
                    String msg = OCCS006ExConstants.EX_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CWBNO取得完了:" + cwbNo);

                // CS_IMAGE_MANAGEMENTに該当の検査指定票のPDFが存在しているか確認
                String csImageManagementPath = occs006ExMapper.selectCsImageManagement(cwbNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPath.add(csImageManagementPath);
                    continue;
                }
                log.info("PDF作成履歴:　無");

                // 該当電文より、PDF作成用Dtoに値をセット
                log.info("戻り電文より必要データ取得開始");
                List<OCCS006ExDto> exDtoList = new ArrayList<OCCS006ExDto>();
                OCCS006ExDto exDto = getExpandData(cwbNo);
                exDtoList.add(exDto);
                if (exDtoList.size() < 1) {
                    String msg = OCCS006ExConstants.EX_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }

                // 検査指定票作成
                Report report = new Report(ReadUtil.readJson(OCCS006ExConstants.EX_PATH));
                report.fill(new ReportDataSource(exDtoList));
                ReportPages pages = report.getPages();
                String fileName = exDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006ExConstants.EX_IMAGECLASS + ".pdf";
                String folderPath = OCCS006ExConstants.EX_OUT_PATH;
                FileUtil.folderCheck(folderPath);
                String filePath = folderPath + fileName;
                FileOutputStream fos = new FileOutputStream(filePath);

                try {
                    PdfRenderer renderer = new PdfRenderer(fos);
                    pages.render(renderer);

                } finally {
                    fos.close();
                }

                // S3 Upload
                String keyName = "";
                if("<AIR/IMP>".equals(exDtoList.get(0).getImpExpClass())){
                    keyName = OCCS006ExConstants.EX_IMP_S3PATH;
                } else {
                    keyName = OCCS006ExConstants.EX_EXP_S3PATH;
                }
                keyName += exDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                keyName += exDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                keyName += exDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                keyName += fileName;

                byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                if (uploadCnt < 1) {
                    String msg = OCCS006ExConstants.EX_ERROR106 + "[filePath:" + fileName + "]";
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("S3 Upload件数： " + uploadCnt + "件");

                // CS_IMAGE_MANAGEMENT更新
                OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸出", keyName, "SYSTEM", exDtoList.get(0).getDateyyyyMMddHHmmss());
                int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                if (insertCnt < 1) {
                    String msg = OCCS006ExConstants.EX_ERROR107;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                // S3保存先をpdfPathリストへ
                pdfPath.add(keyName);

                // ETCファイル削除
                File file = new File(filePath);
                boolean deleted = file.delete();
            }
        }

        // 正常return値セット
        String msg = OCCS006ExConstants.EX_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPath);

        return resultDto;
    }

    private OCCS006ExDto getExpandData(String cwbNo) throws Exception {
        OCCS006ExDto occs006ExDto = new OCCS006ExDto();
        String exFileName = "";

        // cwbNoに該当する戻り電文リスト
        List<String> fileNameList = occs006ExMapper.selectFileName(cwbNo);

        // 検査指定票に該当する電文種類名リスト
        List<String> exFileTypeList = occs006ExMapper.selectExFileName(cwbNo + "%");

        // 戻り電文リストに検査指定票に該当する電文があるのかチェック
        for (String fileName : fileNameList) {
            for (String exFileType : exFileTypeList) {
                if (exFileType.equals(fileName.substring(0, 6))) {
                    exFileName = fileName;
                }
            }
        }

        // 輸入・輸出判定処理
        if("AAD".equals(exFileName.substring(0,3))){
            occs006ExDto.setImpExpClass("<AIR/IMP>");
        } else {
            occs006ExDto.setImpExpClass("<AIR/EXP>");
        }

        // S3より該当の戻り電文を取得
        String addPath = exFileName.substring(12, 16) + "/" + exFileName.substring(16, 18) + "/" + exFileName.substring(18, 20) + "/";
        String key = OCCS006ExConstants.GET_EX_S3PATH + addPath + exFileName;
        byte[] data = awsS3.get(key);
        // Shift_JSへEncoding
        String strValue = new String(data, Charset.forName("Shift_JIS"));
//        String strValue = new String(data);
        String[] strArr = strValue.split("\n");

        //　取得した戻り電文の内容を文字列リストで編集
        List<String> strList = new ArrayList<String>();
        for (String str : strArr) {
            strList.add(str);
        }

        //  検査指定票Dto設定用値編集
        // 日付編集
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = format.parse(exFileName.substring(12, 24));
        String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
        String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
        String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
        String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");

        // 年月日編集
        String bonWareHoCurDate = strList.get(46).substring(0, 4) + "/" + strList.get(46).substring(4, 6) + "/" + strList.get(46).substring(6, 8);

        // 検査指定票Dtoへ電文内容をセット
        //　日付
        occs006ExDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
        occs006ExDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
        occs006ExDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
        occs006ExDto.setDateyyyyMMdd(yyyyMMdd);
        // 検査等区分内容
        occs006ExDto.setExamTypeName(strList.get(1));
        // 申告等番号
        occs006ExDto.setExpReportNo(strList.get(2));
        // 申告区分
        occs006ExDto.setEsaFlg(strList.get(3));
        // 申告種別
        occs006ExDto.setReportKind(strList.get(4));
        // 申告条件
        occs006ExDto.setReportCondition(strList.get(5));
        // 申告者
        occs006ExDto.setReportPlanPersonCd(strList.get(6));
        // 書類提出先1(あて先官署コード)
        occs006ExDto.setReportDepCd(strList.get(7));
        // 書類提出先2(あて先部門コード)
        occs006ExDto.setReportDivisionCd(strList.get(8));
        // 貨物番号
        occs006ExDto.setCwbNo(strList.get(9));
        // MAWB番号
        occs006ExDto.setAwbNo(strList.get(10));
        // MAWB番号先頭3桁
        occs006ExDto.setAwbNo1(strList.get(10).substring(0, 4));
        // MAWB番号先頭3桁以外
        occs006ExDto.setAwbNo2(strList.get(10).substring(3, 15));
        // 蔵置場所1(通関蔵置場)
        occs006ExDto.setClearPlanPlaceCd(strList.get(11));
        // 蔵置場所2(通関蔵置場名)
        occs006ExDto.setClearPlanPlaceName(strList.get(12));
        // 蔵置税関1(蔵置税関)
        occs006ExDto.setCustomsPlaceName(strList.get(13));
        // 蔵置税関2(蔵置税関部門)
        occs006ExDto.setCustomsPlaceDeptCd(strList.get(14));
        // 検査立会者
        occs006ExDto.setCustomsCheckUserCd(strList.get(15));
        // 貨物個数
        occs006ExDto.setCarryingPiece(strList.get(16));
        // 単位
        occs006ExDto.setUnitOfPiece(strList.get(17));
        // 貨物重量
        occs006ExDto.setCarryingWeight(strList.get(18));
        // 重量単位コード（グロス）
        occs006ExDto.setUnitOfWeight(strList.get(19));
        // 事故コード
        occs006ExDto.setTroubleCd(strList.get(22));
        // 積載船（機）名１
        occs006ExDto.setLoadingPlanFLT1(strList.get(24));
        // 積載船（機）名２
        occs006ExDto.setLoadingPlanFLT2(strList.get(25));
        // スプリット３便以上表示
        occs006ExDto.setLoadingPlanFLT3(strList.get(26));
        // 品名
        occs006ExDto.setItem(strList.get(27));
        // 輸出入者1(輸出入者コード)
        occs006ExDto.setExpCustomerNo(strList.get(28).substring(0, 14));
        // 輸出入者2(部門コード)
        occs006ExDto.setExpCustomerDeptCd(strList.get(28).substring(13, 17));
        // 輸出入者3(輸出入者名)
        occs006ExDto.setExpCustomerName(strList.get(29));
        // 税関事務管理人1
        occs006ExDto.setCustomsOfficeJanitorCd1(strList.get(30));
        // 税関事務管理人2
        occs006ExDto.setCustomsOfficeJanitorCd2(strList.get(31));
        // 税関事務管理人3
        occs006ExDto.setCustomsOfficeJanitorCd3(strList.get(32));
        // 記事(税関用)
        occs006ExDto.setNews1(strList.get(33));
        // 記事(通関業者用)
        occs006ExDto.setNews2(strList.get(34));
        // ロケーション
        occs006ExDto.setLocation(strList.get(43));
        // 代理店
        occs006ExDto.setAgent(strList.get(45));
        // 搬入年月日
        occs006ExDto.setBonWareHoCurDate(bonWareHoCurDate);
        // 特残貨物
        occs006ExDto.setSpecialCargoSign(strList.get(47));
        // 仕向(出)地
        occs006ExDto.setLastDestinationCd(strList.get(48));
        // 社内整理番号
        occs006ExDto.setInHouseRefNumber(strList.get(49));
        // 審査区分1
        occs006ExDto.setExamFlg(strList.get(50));
        // 審査区分2
        occs006ExDto.setExamFlgName(strList.get(51));
        // 検査種別1
        occs006ExDto.setExamType(strList.get(53));
        // 検査種別2
        occs006ExDto.setExamTypeName(strList.get(54));
        // 検査実施場所1
        occs006ExDto.setExamPlace(strList.get(59));
        // 検査実施場所2
        occs006ExDto.setExamPlaceName(strList.get(60));
        // 内容
        occs006ExDto.setComment(strList.get(63));
        // 検査方法
        occs006ExDto.setExamMethod(strList.get(64));
        // 検査方法名
        occs006ExDto.setExamMethodName(strList.get(65));
        // 全量検査表示
        occs006ExDto.setAllExamFlg(strList.get(66));
        // 再指定識別
        occs006ExDto.setReSelectFlg(strList.get(67));
        // 検査指定日時
        occs006ExDto.setExamDate(yyyyMMddHHmmss);

        return occs006ExDto;
    }

}

