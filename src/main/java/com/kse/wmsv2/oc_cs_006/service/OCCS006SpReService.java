package com.kse.wmsv2.oc_cs_006.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006SpReConstants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006SpReExpLDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006SpReMapper;
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
import org.springframework.beans.BeanUtils;
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
public class OCCS006SpReService {
    @Autowired
    OCCS006SpReMapper occs006SpReMapper;
    @Autowired
    OCCS007ScreenMapper occs007ScreenMapper;
    @Autowired
    OCCS007Service occs007Service;
    @Autowired
    AwsS3 awsS3;

    private String yyyyMMddHHmm = "";
    private String yyyyMMddHHmmss = "";
    private String yyyyMMddHHmmss2 = "";
    private String yyyyMMdd = "";
    private String impExpClass = "";
    private String imageClassName = "";
    private String spReFileName = "";
    List<String> pdfPath = new ArrayList<>();
    OCCS006ResultDto resultDto = new OCCS006ResultDto();

    public OCCS006ResultDto printSpRe(List<?> dataList) throws Exception {


        // 取得データ有無チェック
        if (dataList.size() < 1) {
            String msg = OCCS006SpReConstants.SP_RE_ERROR102;
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
//                String cwbNo = data.get("cwbNo").toString();
                /** test*/
                String cwbNo = "SF1664474157344";
                /** */

                //　cwbNo取得失敗の際にはエラー処理
                if (StringUtils.isEmpty(cwbNo)) {
                    String msg = OCCS006SpReConstants.SP_RE_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CWBNO取得完了:" + cwbNo);

                // CS_IMAGE_MANAGEMENTに該当の申告控のPDFが存在しているか確認
                String csImageManagementPath = occs006SpReMapper.selectCsImageManagement(cwbNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPath.add(csImageManagementPath);
                    continue;
                }
                log.info("PDF作成履歴:　無");

                // 該当電文より、PDF作成用Dtoに値をセット
                log.info("戻り電文より必要データ取得開始");

                // 対象電文を対象Dtoに設定
                List<?> spReDtoList = getExpandData(cwbNo);
                if (spReDtoList.size() < 1) {
                    String msg = OCCS006SpReConstants.SP_RE_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }

                // 設定したDtoが輸出申告控(大額)の場合
                if (spReDtoList.get(0) instanceof OCCS006SpReExpLDto) {
                    resultDto = makePdfSpReExpL((List<OCCS006SpReExpLDto>) spReDtoList, cwbNo);
                }
            }
        }

        // 正常return値セット
        String msg = OCCS006SpReConstants.SP_RE_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPath);

        return resultDto;
    }

    /**
     * 輸出申告控(大額)のpdfファイル作成メソッド
     *
     * @return 処理結果としては、OCCS006ResultDtoをreturnする
     * @parm spReExpLDtoList 輸出申告控(大額)に該当するDto
     * @parm cwbNo ハウス番号
     */
    private OCCS006ResultDto makePdfSpReExpL(List<OCCS006SpReExpLDto> spReExpLDtoList, String cwbNo) throws Exception {
        // 輸出申告控(大額)のrrpt取得及びpdf作成
        Report report = new Report(ReadUtil.readJson(OCCS006SpReConstants
                .SP_RE_EXP_PATH));
        report.fill(new ReportDataSource(spReExpLDtoList));
        ReportPages pages = report.getPages();
        String fileName = spReExpLDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + spReExpLDtoList.get(0).getImageClassName() + ".pdf";
        String folderPath = OCCS006SpReConstants.SP_RE_OUT_PATH;
        FileUtil.folderCheck(folderPath);
        String filePath = folderPath + fileName;
        FileOutputStream fos = new FileOutputStream(filePath);
        try {
            PdfRenderer renderer = new PdfRenderer(fos);
            pages.render(renderer);
        } finally {
            fos.close();
        }
        // S3へアップロード
        String keyName = "";
        if ("SP".equals(spReExpLDtoList.get(0).getImageClassName())) {
            keyName = OCCS006SpReConstants.SP_EXP_S3PATH;
        } else {
            keyName = OCCS006SpReConstants.RE_EXP_S3PATH;
        }
        keyName += spReExpLDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
        keyName += spReExpLDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
        keyName += spReExpLDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
        keyName += fileName;

        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
        if (uploadCnt < 1) {
            String msg = OCCS006SpReConstants.SP_RE_ERROR106 + "[filePath:" + fileName + "]";
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }
        log.info("S3 Upload件数： " + uploadCnt + "件");

        // CS_IMAGE_MANAGEMENTテーブル更新
        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸出", keyName, "SYSTEM", spReExpLDtoList.get(0).getDateyyyyMMddHHmmss());

        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
        if (insertCnt < 1) {
            String msg = OCCS006SpReConstants.SP_RE_ERROR107;
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
        file.delete();

        return resultDto;
    }

    /**
     * 文字列リスト化した対象電文を該当のDtoに設定する。
     *
     * @return 文字列リスト化した対象電文を設定したDto
     * @parm cwbNo　ハウス番号
     */
    private List<?> getExpandData(String cwbNo) throws Exception {
        List<?> spReDtoList = new ArrayList<>();
        // 該当戻り電文データ取得
        List<String> strList = getTelegramStrList(cwbNo);
        setDate();
        // 戻り電文データをDtoへセット
        switch (spReFileName.substring(0, 6)) {
            case "AAE1LD":
                impExpClass = "<AIR/EXP>";
                imageClassName = "SP";
                spReDtoList = setExpLDto(strList);
                return spReDtoList;
            case "AAE2LD":
            case "AAE3LD":
                impExpClass = "<AIR/EXP>";
                imageClassName = "RE";
                spReDtoList = setExpLDto(strList);
                return spReDtoList;
            default:
                break;
        }
        return spReDtoList;
    }

    /**
     * 日付の値を各種フォーマットに合わせて設定するメソッド
     */
    private void setDate() throws Exception {
        // 日付編集
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = format.parse(spReFileName.substring(12, 24));
        yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
        yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
        yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
        yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");
    }

    /**
     * S3より、該当電文を取得して、文字列リスト化するメソッド
     *
     * @return 取得した電文を文字列リスト化して、returnする。
     * @Parm cwbNo ハウス番号をパラメータとして取得する
     */
    private List<String> getTelegramStrList(String cwbNo) throws Exception {
        // 現在DBに登録されている対象の戻り電文リスト
        List<String> fileNameList = occs006SpReMapper.selectFileName(cwbNo);

        // 申告控の電文種類名リスト
        List<String> spReFileTypeList = occs006SpReMapper.selectSpReFileName();

        // 申告控に該当する電文名取得
        for (String fileName : fileNameList) {
            for (String spReFileType : spReFileTypeList) {
                if (spReFileType.equals(fileName.substring(0, 6))) {
                    // 対象データが複数あった場合は、最新版を作成対象とする
                    spReFileName = fileName;
                }
            }
        }

        // S3より該当の戻り電文を取得
        String addPath = spReFileName.substring(12, 16) + "/" + spReFileName.substring(16, 18) + "/" + spReFileName.substring(18, 20) + "/";
        String key = OCCS006SpReConstants.GET_SP_RE_S3PATH + addPath + spReFileName;
        byte[] data = awsS3.get(key);

//        String strValue = new String(data, Charset.forName("Shift_JIS"));  // JIS Encoding
        String strValue = new String(data, Charset.forName("UTF-8")); // UTF-8 Encoding
        String[] strArr = strValue.split("\n");

        //　取得した戻り電文の内容を文字列リストで編集
        List<String> strList = new ArrayList<String>();
        for (String str : strArr) {
            strList.add(str);
        }
        return strList;

    }

    /**
     * 輸出申告控(大額)に該当するDtoに、取得した電文内容を設定する。
     *
     * @param strList 文字列リスト化した電文
     * @return 輸出申告控(大額)に該当するDto
     */
    private List<OCCS006SpReExpLDto> setExpLDto(List<String> strList) {
        // 申告控Dtoへ電文内容をセット
        List<OCCS006SpReExpLDto> spReExpDtoList = new ArrayList<OCCS006SpReExpLDto>();
        OCCS006SpReExpLDto spReExpDto = new OCCS006SpReExpLDto();
        // awbNo設定
        String awbNo1 = "";
        String awbNo2 = "";
        if (!StringUtils.isEmpty(strList.get(40).trim())) {
            awbNo1 = strList.get(40).substring(0, 4);
            awbNo2 = strList.get(40).substring(4, strList.get(40).length()).trim();
        } else {
            awbNo1 = strList.get(43).substring(0, 4);
            awbNo2 = strList.get(43).substring(4, 35);
        }
        // 輸入・輸出・イメージクラス設定
        spReExpDto.setImpExpClass(impExpClass);
        spReExpDto.setImageClassName(imageClassName);
        //　日付
        // 日付1 */
        spReExpDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
        // 日付2 */
        spReExpDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
        // 日付3 */
        spReExpDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
        // 日付4 */
        spReExpDto.setDateyyyyMMdd(yyyyMMdd);

        //　ヘッダー部
        // 代表統番
        spReExpDto.setTaxNo(strList.get(1));
        // 申告種別1
        spReExpDto.setReportKindCd1(strList.get(2));
        // 申告種別2
        spReExpDto.setReportKindCd2(strList.get(3));
        // 申告種別3
        spReExpDto.setReportKindCd3(strList.get(4));
        // 申告種別4
        spReExpDto.setReportKindCd4(strList.get(5));
        // 申告種別5
        spReExpDto.setReportKindCd5(strList.get(6));
        // 区分
        spReExpDto.setReportClass(strList.get(8));
        // あて先税関
        spReExpDto.setReportDepCd(strList.get(9));
        // 提出先
        spReExpDto.setReportDivisionCd(strList.get(10));
        // 申告年月日
        spReExpDto.setReportDate(dateFormyyyyMMdd(strList.get(11)));
        // 申告番号
        spReExpDto.setReportNo(strList.get(12));

        // ボディー部
        // 申告条件
        spReExpDto.setReportCondition(strList.get(13));
        // 搬入
        spReExpDto.setCarryingIn(strList.get(14));
        // 輸出者1
        spReExpDto.setExpCustomerName1(strList.get(17).substring(0, 13));
        // 輸出者2
        spReExpDto.setExpCustomerName2(strList.get(17).substring(13, 17));
        // 輸出者3
        spReExpDto.setExpCustomerName3(strList.get(18));
        // 住所1
        spReExpDto.setExpCustomerAdd1(strList.get(19));
        // 住所2
        spReExpDto.setExpCustomerAdd2(strList.get(20));
        // 住所3
        spReExpDto.setExpCustomerAdd3(strList.get(21));
        // 住所4
        spReExpDto.setExpCustomerAdd4(strList.get(22));
        // 住所5
        spReExpDto.setExpCustomerAdd5(strList.get(23));
        // 電話
        spReExpDto.setExpCustomerTel(strList.get(24));
        // 税関事務管理人1
        spReExpDto.setCustomsOfficeJanitorName1(strList.get(25).substring(0, 13));
        // 税関事務管理人2
        spReExpDto.setCustomsOfficeJanitorName2(strList.get(25).substring(13, 17));
        // 税関事務管理人3
        spReExpDto.setCustomsOfficeJanitorName3(strList.get(26).substring(0, 4));
        // 税関事務管理人4
        spReExpDto.setCustomsOfficeJanitorName4(strList.get(26).substring(4, 6));
        // 税関事務管理人5
        spReExpDto.setCustomsOfficeJanitorName5(strList.get(26).substring(6, 10));
        // 税関事務管理人6
        spReExpDto.setCustomsOfficeJanitorName6(strList.get(27));
        // 仕向人1
        spReExpDto.setConsigneeNo1(strList.get(28).substring(0, 8));
        // 仕向人2
        spReExpDto.setConsigneeNo2(strList.get(28).substring(8, 12));
        // 仕向人3
        spReExpDto.setConsigneeName(strList.get(29));
        // 住所1
        spReExpDto.setConsigneeAdd1(strList.get(30));
        // 住所2
        spReExpDto.setConsigneeAdd2(strList.get(31));
        // 住所3
        spReExpDto.setConsigneeAdd3(strList.get(32));
        // 住所4
        spReExpDto.setConsigneeAdd4(strList.get(33));
        // 住所5
        spReExpDto.setConsigneeAdd5(strList.get(34));
        // 国コード
        spReExpDto.setConsigneeCountry(strList.get(35));
        // 代理人1
        spReExpDto.setAgent1(strList.get(36));
        // 代理人2
        spReExpDto.setAgent2(strList.get(37));
        // 通関士コード
        spReExpDto.setReportCustomsSpecialistUserCd(strList.get(38));
        // 検査立会者
        spReExpDto.setInspectionWitness(strList.get(39));
        // ＡＷＢ番号先頭3桁
        spReExpDto.setAwbNo1(awbNo1);
        // ＡＷＢ番号
        spReExpDto.setAwbNo2(awbNo2);
        // 貨物個数
        spReExpDto.setCarryingPiece(commaStr(strList.get(41)));
        // 保税地域1
        spReExpDto.setCustomsPlaceCd1(strList.get(46));
        // 保税地域2
        spReExpDto.setCustomsPlaceCd2(strList.get(47));
        // 蔵置税関1
        spReExpDto.setBondedWareHouseCustomsCd1(strList.get(48));
        // 蔵置税関2
        spReExpDto.setBondedWareHouseCustomsCd2(strList.get(49));
        // 最終仕向地1
        spReExpDto.setLastDestinationCd1(strList.get(50));
        // 最終仕向地2
        spReExpDto.setLastDestinationCd2(strList.get(51));
        // 事前検査済貨物等識別
        spReExpDto.setPreExamCargoDisc(strList.get(52));
        // 積込港1
        spReExpDto.setDepPortCd1(strList.get(53));
        // 積込港2
        spReExpDto.setDepPortCd2(strList.get(54));
        // 貿易形態別符号
        spReExpDto.setTradeTypeMark(strList.get(55));
        // 調査用符号
        spReExpDto.setCustomsExamMark(strList.get(56));
        // 出港予定年月日
        spReExpDto.setDepPlaningDate(dateFormyyyyMMdd(strList.get(59)));
        // 関税
        spReExpDto.setCustoms(strList.get(60));
        // 内国消費税
        spReExpDto.setInConsTax(strList.get(61));
        // その他
        spReExpDto.setOther(strList.get(62));
        // 輸出承認証等区分
        spReExpDto.setExpRecoFlg(strList.get(64));
        // 輸出承認証番号等(1)1
        spReExpDto.setExpRecA1(strList.get(65));
        // 輸出承認証番号等(1)2
        spReExpDto.setExpRecA2(strList.get(66));
        // 輸出承認証番号等(2)1
        spReExpDto.setExpRecB1(strList.get(67));
        // 輸出承認証番号等(2)2
        spReExpDto.setExpRecB2(strList.get(68));
        // 輸出承認証番号等(3)1
        spReExpDto.setExpRecC1(strList.get(69));
        // 輸出承認証番号等(3)2
        spReExpDto.setExpRecC2(strList.get(70));
        // 輸出承認証番号等(4)1
        spReExpDto.setExpRecD1(strList.get(71));
        // 輸出承認証番号等(4)2
        spReExpDto.setExpRecD2(strList.get(72));
        // 輸出承認証番号等(5)1
        spReExpDto.setExpRecE1(strList.get(73));
        // 輸出承認証番号等(5)2
        spReExpDto.setExpRecE2(strList.get(74));
        // 輸出承認証番号等(6)1
        spReExpDto.setExpRecF1(strList.get(75));
        // 輸出承認証番号等(6)2
        spReExpDto.setExpRecF2(strList.get(76));
        // 輸出承認証番号等(7)1
        spReExpDto.setExpRecG1(strList.get(77));
        // 輸出承認証番号等(7)2
        spReExpDto.setExpRecG2(strList.get(78));
        // 輸出承認証番号等(8)1
        spReExpDto.setExpRecH1(strList.get(79));
        // 輸出承認証番号等(8)2
        spReExpDto.setExpRecH2(strList.get(80));
        // 輸出承認証番号等(9)1
        spReExpDto.setExpRecI1(strList.get(81));
        // 輸出承認証番号等(9)2
        spReExpDto.setExpRecI2(strList.get(82));
        // 輸出承認証番号等(10)1
        spReExpDto.setExpRecJ1(strList.get(83));
        // 輸出承認証番号等(10)2
        spReExpDto.setExpRecJ2(strList.get(84));
        // 輸出承認証番号等(11)1
        spReExpDto.setExpRecK1(strList.get(85));
        // 輸出承認証番号等(11)2
        spReExpDto.setExpRecK2(strList.get(86));
        // 輸出承認証番号等(12)1
        spReExpDto.setExpRecL1(strList.get(87));
        // 輸出承認証番号等(12)2
        spReExpDto.setExpRecL2(strList.get(88));
        // 輸出承認証番号等(13)1
        spReExpDto.setExpRecM1(strList.get(89));
        // 輸出承認証番号等(13)2
        spReExpDto.setExpRecM2(strList.get(90));
        // 輸出承認証番号等(14)1
        spReExpDto.setExpRecN1(strList.get(91));
        // 輸出承認証番号等(14)2
        spReExpDto.setExpRecN2(strList.get(92));
        // 輸出承認証番号等(15)1
        spReExpDto.setExpRecO1(strList.get(93));
        // 輸出承認証番号等(15)2
        spReExpDto.setExpRecO2(strList.get(94));
        // 仕入書番号1
        spReExpDto.setInvoiceNo1(strList.get(95));
        // 仕入書番号2
        spReExpDto.setInvoiceNo2(strList.get(96));
        // 仕入書(電子）
        spReExpDto.setEleInvoiceNo(strList.get(97));
        // 仕入書価格1
        spReExpDto.setInvoiceValue1(strList.get(98));
        // 仕入書価格2
        spReExpDto.setInvoiceValue2(strList.get(99));
        // 仕入書価格3
        spReExpDto.setInvoiceValue3(commaStr(strList.get(100)));
        // 仕入書価格4
        spReExpDto.setInvoiceValue4(strList.get(101));
        // ＦＯＢ価格1
        spReExpDto.setFobAmo1(strList.get(102));
        // ＦＯＢ価格2
        spReExpDto.setFobAmo2(commaStr(strList.get(103)));
        // 通貨レート1
        spReExpDto.setCurrencyRate1(strList.get(104));
        // 通貨レート2
        spReExpDto.setCurrencyRate2(commaStr(strList.get(105)));
        // 通貨レート3
        spReExpDto.setCurrencyRate3(strList.get(106));
        // 通貨レート4
        spReExpDto.setCurrencyRate4(commaStr(strList.get(107)));
        // ＢＰＲ合計1
        spReExpDto.setBasicPriceTotal1(commaStr(strList.get(108)));
        // ＢＰＲ合計2
        spReExpDto.setBasicPriceTotal2(strList.get(109));
        // 枚
        spReExpDto.setConfigure(strList.get(110));
        // 欄
        spReExpDto.setDeclareCnt(strList.get(111));

        // フッター部
        // 記事(税関)
        spReExpDto.setNews1(strList.get(124).substring(0, 70));
        // 記事(税関)
        spReExpDto.setNews2(strList.get(124).substring(70, strList.get(125).length()));
        // 記事(通関)
        spReExpDto.setNews3(strList.get(125));
        // 記事(荷主)
        spReExpDto.setNewsShipper(strList.get(126));
        // 荷主セクションコード
        spReExpDto.setMerchantSectionCode(strList.get(127));
        // 荷主Ｒｅｆ Ｎｏ．
        spReExpDto.setShipperReferenceNumber(strList.get(128));
        // 社内整理番号
        spReExpDto.setInHouseRefNumber(strList.get(129));
        // 利用者整理番号
        spReExpDto.setUserRefNumber(strList.get(130));
        // 輸 出 者(入力)1
        spReExpDto.setExpCustomerNameInput1(strList.get(131).substring(0, 8));
        // 輸 出 者(入力)2
        spReExpDto.setExpCustomerNameInput2(strList.get(131).substring(8, 12));

        // 繰返し部
        for (int i = 132; i < strList.size(); i++) {
            OCCS006SpReExpLDto copyDto = new OCCS006SpReExpLDto();
            BeanUtils.copyProperties(spReExpDto, copyDto);
            // 欄
            copyDto.setReNum(strList.get(i++));
            // 統合先欄
            copyDto.setMergeCd(strList.get(i++));
            // 価格再確認
            copyDto.setValueCheckFlg(strList.get(i++));
            // 品名
            copyDto.setItem(strList.get(i++));
            // 統計品目番号1
            copyDto.setItemNo1(decimal2(strList.get(i).substring(0, 6)));
            // 統計品目番号2
            copyDto.setItemNo2(strList.get(i++).substring(6, 9));
            // 統計品目番号3
            copyDto.setItemNo3(strList.get(i++));
            // 申告価格(FOB)1
            copyDto.setReportValueA(plusYen(strList.get(i++)));
            // 数量（１）1
            copyDto.setPieceA1(commaStr(strList.get(i++)));
            // 数量（１）2
            copyDto.setPieceA2(strList.get(i++));
            // 申告価格(FOB)2
            copyDto.setReportValueB(plusYen(strList.get(i++)));
            // 数量（２）1
            copyDto.setPieceB1(commaStr(strList.get(i++)));
            // 数量（２）2
            copyDto.setPieceB2(strList.get(i++));
            // ＢＰＲ按分係数
            copyDto.setBasicPriceRate(commaStr(strList.get(i++)));
            // ＢＰＲ金額1
            copyDto.setBasicPriceAmo1(strList.get(i++));
            // ＢＰＲ金額2
            copyDto.setBasicPriceAmo2(commaStr(strList.get(i++)));
            // 関税法70条関係(1)
            copyDto.setTaxLaw1(strList.get(i++));
            // 関税法70条関係(2)
            copyDto.setTaxLaw2(strList.get(i++));
            // 関税法70条関係(3)
            copyDto.setTaxLaw3(strList.get(i++));
            // 関税法70条関係(4)
            copyDto.setTaxLaw4(strList.get(i++));
            // 関税法70条関係(5)
            copyDto.setTaxLaw5(strList.get(i++));
            // 輸出令別表
            copyDto.setOtherLaw1(strList.get(i++));
            // 外為法第48条
            copyDto.setOtherLaw2(strList.get(i++));
            // 減免戻税条項符号1
            copyDto.setOtherLaw3(strList.get(i++));
            // 減免戻税条項符号2
            copyDto.setOtherLaw4(strList.get(i++));
            // 減免戻税条項符号(法)
            copyDto.setOtherLaw5(strList.get(i++));
            // 減免戻税条項符号(令)
            copyDto.setOtherLaw6(strList.get(i++));
            // 内消税免税符号1
            copyDto.setInConsTaxKind1(strList.get(i++));
            // 内消税免税符号2
            copyDto.setInConsTaxKind2(strList.get(i++));
            // 内消税免税符号3
            copyDto.setInConsTaxKind3(strList.get(i++));
            spReExpDtoList.add(copyDto);
        }

        return spReExpDtoList;

    }

    /**
     * 取得した文字列(日付)を yyyy/MM/dd形式に変更するメソッド
     *
     * @return yyyy/MM/dd形式に変更した日付の値
     * @parm str 日付
     */
    private String dateFormyyyyMMdd(String str) {
        String dateStr = str.trim();
        if (!StringUtils.isEmpty(dateStr)) {
            dateStr = str.substring(0, 4) + "/" + str.substring(4, 6) + "/" + str.substring(6, 8);
        }
        return dateStr;
    }

    /**
     * 取得した文字列(金額)に ¥マークをつけるメソッド
     *
     * @return ¥マーク付けの文字列(金額)
     * @parm str 文字列(金額)
     */
    private String plusYen(String str) {
        String valueStr = str.trim();
        if (!StringUtils.isEmpty(valueStr)) {
            int valueInt = Integer.parseInt(valueStr);
            Formatter formatter = new Formatter();
            formatter.format("%,d", valueInt);
            valueStr = "¥" + formatter.toString();
            formatter.close();
        }
        return valueStr;
    }

    /**
     * 取得した文字列の下2桁を小数点として修正するメソッド
     *
     * @return XXXXXX->XXXX.XXに修正した文字列
     * @parm str 文字列
     */
    private String decimal2(String str) {
        String formattedStr = str.substring(0, str.length() - 2) + "." + str.substring(str.length() - 2);
        return formattedStr;
    }

    /**
     * 取得した文字列の3桁区切り処理を行うメソッド
     *
     * @return 3桁区切りされた文字列
     * @parm str 文字列(金額)
     */
    private String commaStr(String str) {
        String valueStr = str.trim();
        if (!StringUtils.isEmpty(valueStr)) {
            if (!valueStr.contains(".")) {
                int valueInt = Integer.parseInt(valueStr);
                Formatter formatter = new Formatter();
                formatter.format("%,d", valueInt);
                valueStr = formatter.toString();
                formatter.close();
            } else {
                double valueDouble = Double.parseDouble(valueStr);
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##");
                valueStr = decimalFormat.format(valueDouble);
            }
        }
        return valueStr;
    }
}

