package com.kse.wmsv2.oc_cs_006.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006HdConstants;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006SpReConstants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006HdImpDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006HdMapper;
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
public class OCCS006HdService {
    @Autowired
    OCCS006HdMapper occs006HdMapper;
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
    private String hdFileName = "";
    List<String> pdfPath = new ArrayList<>();
    OCCS006ResultDto resultDto = new OCCS006ResultDto();

    public OCCS006ResultDto printHd(List<?> dataList) throws Exception {

        // 取得データ有無チェック
        if (dataList.size() < 1) {
            String msg = OCCS006HdConstants.HD_ERROR102;
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
                    String msg = OCCS006HdConstants.HD_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CWBNO取得完了:" + cwbNo);

                // CS_IMAGE_MANAGEMENTに該当の申告入力控のPDFが存在しているか確認
                String csImageManagementPath = occs006HdMapper.selectCsImageManagement(cwbNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPath.add(csImageManagementPath);
                    continue;
                }
                log.info("PDF作成履歴:　無");

                // 対象電文を対象Dtoに設定
                log.info("戻り電文より必要データ取得開始");
                List<?> hdDtoList = getExpandData(cwbNo);
                if (hdDtoList.size() < 1) {
                    String msg = OCCS006HdConstants.HD_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                //　設定したDtoが輸入申告入力控の場合
                if (hdDtoList.get(0) instanceof OCCS006HdImpDto) {
                    resultDto = makePdfHdImp((List<OCCS006HdImpDto>) hdDtoList, cwbNo);
                }
            }
        }

        // 正常return値セット
        String msg = OCCS006HdConstants.HD_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPath);

        return resultDto;
    }

    /**
     * 輸入申告入力控のpdfファイル作成メソッド
     *
     * @return 処理結果としては、OCCS006ResultDtoをreturnする
     * @parm spReExpLDtoList 輸入申告入力控に該当するDto
     * @parm cwbNo ハウス番号
     */
    private OCCS006ResultDto makePdfHdImp(List<OCCS006HdImpDto> hdImpDtoList, String cwbNo) throws Exception {
        // 輸入申告入力控のrrpt取得及びpdf作成
        Report report = new Report(ReadUtil.readJson(OCCS006HdConstants.HD_IMP_PATH));
        report.fill(new ReportDataSource(hdImpDtoList));
        ReportPages pages = report.getPages();
        String fileName = hdImpDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + OCCS006HdConstants.HD_IMAGECLASS + ".pdf";
        String folderPath = OCCS006HdConstants.HD_OUT_PATH;
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
        String keyName = OCCS006HdConstants.HD_IMP_S3PATH;
        keyName += hdImpDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
        keyName += hdImpDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
        keyName += hdImpDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
        keyName += fileName;

        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
        if (uploadCnt < 1) {
            String msg = OCCS006HdConstants.HD_ERROR106 + "[filePath:" + fileName + "]";
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }
        log.info("S3 Upload件数： " + uploadCnt + "件");

        // CS_IMAGE_MANAGEMENTテーブル更新
        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", hdImpDtoList.get(0).getDateyyyyMMddHHmmss());

        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
        if (insertCnt < 1) {
            String msg = OCCS006HdConstants.HD_ERROR107;
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
        List<?> hdDtoList = new ArrayList<>();
        // 該当戻り電文データ取得
        List<String> strList = getTelegramStrList(cwbNo);
        setDate();
        // 戻り電文データをDtoへセット
        switch (hdFileName.substring(0, 3)) {
            case "AAD":
                impExpClass = "<AIR/IMP>";
                hdDtoList = setHdImpDto(strList);
                return hdDtoList;
            default:
                break;
        }
        return hdDtoList;
    }

    /**
     * 輸入申告入力控に該当するDtoに、取得した電文内容を設定する。
     *
     * @param strList 文字列リスト化した電文
     * @return 輸入申告入力控に該当するDto
     */
    private List<OCCS006HdImpDto> setHdImpDto(List<String> strList) {
        // 申告入力控Dtoへ電文内容をセット
        List<OCCS006HdImpDto> occs006HdImpDtoList = new ArrayList<OCCS006HdImpDto>();
        OCCS006HdImpDto occs006HdImpDto = new OCCS006HdImpDto();
        // 輸入・輸出・イメージクラス設定
        occs006HdImpDto.setImpExpClass(impExpClass);
        //　日付
        // 日付1 */
        occs006HdImpDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
        // 日付2 */
        occs006HdImpDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
        // 日付3 */
        occs006HdImpDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
        // 日付4 */
        occs006HdImpDto.setDateyyyyMMdd(yyyyMMdd);

        // 代表税番 */
        occs006HdImpDto.setTaxNo(strList.get(3) + " " + strList.get(4));
        // 申告種別1 */
        occs006HdImpDto.setReportKindCd1(strList.get(5));
        // 申告種別2 */
        occs006HdImpDto.setReportKindCd2(strList.get(8));
        // 区分 */
        occs006HdImpDto.setReportClass(strList.get(9));
        // あて先税関 */
        occs006HdImpDto.setReportDepCd(strList.get(10));
        // 部門 */
        occs006HdImpDto.setReportDivisionCd(strList.get(11));
        // 申告年月日 */
        occs006HdImpDto.setReportDate(dateFormyyyyMMdd(strList.get(12)));
        // 申告番号 */
        occs006HdImpDto.setReportNo(strList.get(16));

        // 申告条件 */
        occs006HdImpDto.setReportCondition(strList.get(17));
        // 申告予定年月日 */
        occs006HdImpDto.setReportPlanDate(dateFormyyyyMMdd(strList.get(19)));
        // 本申告 */
        occs006HdImpDto.setDeclaration(strList.get(20));
        // 輸入者1 */
        occs006HdImpDto.setImpCustomerName1(strList.get(21).substring(0,13));
        // 輸入者2 */
        occs006HdImpDto.setImpCustomerName2(strList.get(21).substring(13,17));
        // 輸入者3 */
        occs006HdImpDto.setImpCustomerName3(strList.get(22));
        // 住所1 */
        occs006HdImpDto.setImpCustomerAdd1(strList.get(23));
        // 住所2 */
        occs006HdImpDto.setImpCustomerAdd2(strList.get(24));
        // 住所3 */
        occs006HdImpDto.setImpCustomerAdd3(strList.get(25));
        // 住所4 */
        occs006HdImpDto.setImpCustomerAdd4(strList.get(26));
        // 住所5 */
        occs006HdImpDto.setImpCustomerAdd5(strList.get(27));
        // 電話 */
        occs006HdImpDto.setImpCustomerTel(strList.get(28));
        // 税関事務管理人1 */
        occs006HdImpDto.setCustomsOfficeJanitorName1(strList.get(29).substring(0, 13));
        // 税関事務管理人2 */
        occs006HdImpDto.setCustomsOfficeJanitorName2(strList.get(29).substring(13, 17));
        // 税関事務管理人3 */
        occs006HdImpDto.setCustomsOfficeJanitorName3(strList.get(30).substring(0, 4));
        // 税関事務管理人4 */
        occs006HdImpDto.setCustomsOfficeJanitorName4(strList.get(30).substring(4, 6));
        // 税関事務管理人5 */
        occs006HdImpDto.setCustomsOfficeJanitorName5(strList.get(30).substring(6, 10));
        // 税関事務管理人6 */
        occs006HdImpDto.setCustomsOfficeJanitorName6(strList.get(31));
        // 輸入取引者1 */
        occs006HdImpDto.setImpDealingsName1(strList.get(32).substring(0, 13));
        // 輸入取引者2 */
        occs006HdImpDto.setImpDealingsName2(strList.get(32).substring(13, 17));
        // 輸入取引者3 */
        occs006HdImpDto.setImpDealingsName3(strList.get(33));
        // 仕出人1 */
        occs006HdImpDto.setExpCustomerName1(strList.get(34).substring(0, 8));
        // 仕出人2 */
        occs006HdImpDto.setExpCustomerName2(strList.get(34).substring(8, 12));
        // 仕出人3 */
        occs006HdImpDto.setExpCustomerName3(strList.get(35));
        // 住所1 */
        occs006HdImpDto.setExpCustomerAdd1(strList.get(36));
        // 住所2 */
        occs006HdImpDto.setExpCustomerAdd2(strList.get(37));
        // 住所3 */
        occs006HdImpDto.setExpCustomerAdd3(strList.get(38));
        // 住所4 */
        occs006HdImpDto.setExpCustomerAdd4(strList.get(39));
        // 住所5 */
        occs006HdImpDto.setExpCustomerAdd5(strList.get(40));
        // 住所6 */
        occs006HdImpDto.setExpCustomerAdd6(strList.get(41));
        // 輸出の委託者 */
        occs006HdImpDto.setAuthor(strList.get(42));
        // 代理人1 */
        occs006HdImpDto.setRepresentativeName1(strList.get(43));
        // 代理人2 */
        occs006HdImpDto.setRepresentativeName2(strList.get(44));
        // 通関士コード */
        occs006HdImpDto.setReportCustomsSpecialistUserCd(strList.get(45));
        // 検査立会者 */
        occs006HdImpDto.setInspectionWitness(strList.get(46));
        // ＡＷＢ番号先頭3桁 */
        occs006HdImpDto.setCwbNo1(strList.get(47).substring(0, 3));
        // ＡＷＢ番号 */
        occs006HdImpDto.setCwbNo2(strList.get(47).substring(3, 35));
        // ＭＡＷＢ番号先頭3桁 */
        occs006HdImpDto.setAwbNo1(strList.get(48).substring(0, 3));
        // ＭＡＷＢ番号 */
        occs006HdImpDto.setAwbNo2(strList.get(48).substring(3, 35));
        // 取卸港1 */
        occs006HdImpDto.setGetPort1(strList.get(53));
        // 取卸港2 */
        occs006HdImpDto.setGetPort2(strList.get(54));
        // 積出地1 */
        occs006HdImpDto.setShipmentCd1(strList.get(55));
        // 積出地2 */
        occs006HdImpDto.setShipmentCd2(strList.get(56));
        // 積載機名 */
        occs006HdImpDto.setLoadingPlanFLT(strList.get(58));
        // 入港年月日 */
        occs006HdImpDto.setArrPortDate(strList.get(59));
        // 蔵置税関1 */
        occs006HdImpDto.setCustomsCd1(strList.get(60));
        // 蔵置税関2 */
        occs006HdImpDto.setCustomsCd2(strList.get(61));
        // 貨物個数 */
        occs006HdImpDto.setCarryingPiece(commaStr(strList.get(62)));
        // 保税地域1 */
        occs006HdImpDto.setCustomsPlaceCd1(strList.get(64));
        // 保税地域2 */
        occs006HdImpDto.setCustomsPlaceCd2(strList.get(65));
        // 貨物重量1 */
        occs006HdImpDto.setCarryingWeight1(commaStr(strList.get(66)));
        // 貨物重量2 */
        occs006HdImpDto.setCarryingWeight2(strList.get(67));
        // 搬入予定1 */
        occs006HdImpDto.setBondedWareHouseCurDate1(strList.get(68));
        // 搬入予定2 */
        occs006HdImpDto.setBondedWareHouseCurDate2(strList.get(69));
        // 最初蔵入年月日 */
        occs006HdImpDto.setFirstCarBondedDate(dateFormyyyyMMdd(strList.get(71)));
        // 貿易形態別符号 */
        occs006HdImpDto.setTradeTypeMark(strList.get(73));
        // 調査用符号 */
        occs006HdImpDto.setCustomsExamMark(strList.get(74));
        // 貿易管理 */
        occs006HdImpDto.setTradeCont(strList.get(76));
        // 輸入承認証 */
        occs006HdImpDto.setImpRecoAttachDisc(strList.get(77));
        // 関税法70条関係許可承認1 */
        occs006HdImpDto.setCustomsLaw1(strList.get(78));
        // 関税法70条関係許可承認2 */
        occs006HdImpDto.setCustomsLaw2(strList.get(79));
        // 関税法70条関係許可承認3 */
        occs006HdImpDto.setCustomsLaw3(strList.get(80));
        // 関税法70条関係許可承認4 */
        occs006HdImpDto.setCustomsLaw4(strList.get(81));
        // 関税法70条関係許可承認5 */
        occs006HdImpDto.setCustomsLaw5(strList.get(82));
        // 共通管理番号 */
        occs006HdImpDto.setOtherMiniContNo(strList.get(83));
        // 食品1 */
        occs006HdImpDto.setFhProofDisc1(strList.get(84));
        // 食品2 */
        occs006HdImpDto.setFhProofDisc2(strList.get(85));
        // 植防1 */
        occs006HdImpDto.setPlantPEDisc1(strList.get(86));
        // 植防2 */
        occs006HdImpDto.setPlantPEDisc2(strList.get(87));
        // 動検1 */
        occs006HdImpDto.setAnimalQuaraDisc1(strList.get(88));
        // 動検2 */
        occs006HdImpDto.setAnimalQuaraDisc2(strList.get(89));
        // 輸入承認証番号等1 */
        occs006HdImpDto.setOtherLawRecNo1(strList.get(90) + " " + strList.get(91));
        // 輸入承認証番号等2 */
        occs006HdImpDto.setOtherLawRecNo2(strList.get(92) + " " + strList.get(93));
        // 輸入承認証番号等3 */
        occs006HdImpDto.setOtherLawRecNo3(strList.get(94) + " " + strList.get(95));
        // 輸入承認証番号等4 */
        occs006HdImpDto.setOtherLawRecNo4(strList.get(96) + " " + strList.get(97));
        // 輸入承認証番号等5 */
        occs006HdImpDto.setOtherLawRecNo5(strList.get(98) + " " + strList.get(99));
        // 輸入承認証番号等6 */
        occs006HdImpDto.setOtherLawRecNo6(strList.get(100) + " " + strList.get(101));
        // 輸入承認証番号等7 */
        occs006HdImpDto.setOtherLawRecNo7(strList.get(102) + " " + strList.get(103));
        // 輸入承認証番号等8 */
        occs006HdImpDto.setOtherLawRecNo8(strList.get(104) + " " + strList.get(105));
        // 輸入承認証番号等9 */
        occs006HdImpDto.setOtherLawRecNo9(strList.get(106) + " " + strList.get(107));
        // 輸入承認証番号等10 */
        occs006HdImpDto.setOtherLawRecNo10(strList.get(108) + " " + strList.get(109));
        // 仕入書番号1 */
        occs006HdImpDto.setInvoiceNo1(strList.get(110));
        // 仕入書番号2 */
        occs006HdImpDto.setInvoiceNo2(strList.get(111));
        // 仕入書(電子） */
        occs006HdImpDto.setEleInvoiceNo(strList.get(112));
        // 仕入書価格1 */
        occs006HdImpDto.setInvoiceValue1(strList.get(113));
        // 仕入書価格2 */
        occs006HdImpDto.setInvoiceValue2(strList.get(114));
        // 仕入書価格3 */
        occs006HdImpDto.setInvoiceValue3(strList.get(115));
        // 仕入書価格4 */
        occs006HdImpDto.setInvoiceValue4(changeDouble(commaStr(strList.get(116))));
        // 運賃1 */
        occs006HdImpDto.setFareCurrencyCd1(strList.get(117));
        // 運賃2 */
        occs006HdImpDto.setFareCurrencyCd2(strList.get(118));
        // 運賃3 */
        occs006HdImpDto.setFareCurrencyCd3(changeDouble(commaStr(strList.get(119))));
        // 保険1 */
        occs006HdImpDto.setInsurance1(strList.get(120));
        // 保険2 */
        occs006HdImpDto.setInsurance2(strList.get(121));
        // 保険3 */
        occs006HdImpDto.setInsurance3(changeDouble(commaStr(strList.get(122))));
        // 保険4 */
        occs006HdImpDto.setInsurance4(strList.get(123));
        // 通関金額1 */
        occs006HdImpDto.setCustomsValue1(strList.get(124));
        // 通関金額2 */
        occs006HdImpDto.setCustomsValue2(changeDouble(commaStr(strList.get(125))));
        // 評価1 */
        occs006HdImpDto.setEstimationFlgCd1(strList.get(126));
        // 評価2 */
        occs006HdImpDto.setEstimationFlgCd2(strList.get(127));
        // 評価3 */
        occs006HdImpDto.setEstimationFlgCd3(strList.get(128));
        // 評価4 */
        occs006HdImpDto.setEstimationFlgCd4(strList.get(129));
        // 補正A1 */
        occs006HdImpDto.setIncReviseA1(strList.get(130));
        // 補正A2 */
        occs006HdImpDto.setIncReviseA2(strList.get(131));
        // 補正A3 */
        occs006HdImpDto.setIncReviseA3(commaStr(strList.get(132)));
        // 補正A4 */
        occs006HdImpDto.setIncReviseA4(strList.get(133));
        // 補正B1 */
        occs006HdImpDto.setIncReviseB1(strList.get(134));
        // 補正B2 */
        occs006HdImpDto.setIncReviseB2(strList.get(135));
        // 補正B3 */
        occs006HdImpDto.setIncReviseB3(strList.get(136));
        // 補正C1 */
        occs006HdImpDto.setIncReviseC1(strList.get(137));
        // 補正C2 */
        occs006HdImpDto.setIncReviseC2(strList.get(138));
        // 補正C3 */
        occs006HdImpDto.setIncReviseC3(strList.get(139));
        // 補正D1 */
        occs006HdImpDto.setIncReviseD1(strList.get(140));
        // 補正D2 */
        occs006HdImpDto.setIncReviseD2(strList.get(141));
        // 補正D3 */
        occs006HdImpDto.setIncReviseD3(strList.get(142));
        // 事前教示（評価）1 */
        occs006HdImpDto.setAdvanceTeachingEvaluation1(strList.get(143));
        // 事前教示（評価）2 */
        occs006HdImpDto.setAdvanceTeachingEvaluation2(strList.get(144));
        // ＢＰＲ合計1 */
        occs006HdImpDto.setBasicPriceRate1(commaStr(strList.get(145)));
        // ＢＰＲ合計2 */
        occs006HdImpDto.setBasicPriceRate2(strList.get(146));
        // 計算 */
        occs006HdImpDto.setCalculate(strList.get(147));
        // 原産地証明 */
        occs006HdImpDto.setOriginCertifiDisc(strList.get(148));
        // 戻税申告 */
        occs006HdImpDto.setBackTaxReportDisc(strList.get(149));
        // 内容点検結果 */
        occs006HdImpDto.setContCheckReOther(strList.get(150));
        // 税科目A1 */
        occs006HdImpDto.setTaxItemA1(strList.get(151));
        // 税科目A2 */
        occs006HdImpDto.setTaxItemA2(strList.get(152));
        // 税額合計A */
        occs006HdImpDto.setTaxAmoA(plusYen(strList.get(153)));
        // 欄数A */
        occs006HdImpDto.setColumnCountA(strList.get(154));
        // 税科目B1 */
        occs006HdImpDto.setTaxItemB1(strList.get(155));
        // 税科目B2 */
        occs006HdImpDto.setTaxItemB2(strList.get(156));
        // 税額合計B */
        occs006HdImpDto.setTaxAmoB(plusYen(strList.get(157)));
        // 欄数B */
        occs006HdImpDto.setColumnCountB(strList.get(158));
        // 税科目C1 */
        occs006HdImpDto.setTaxItemC1(strList.get(159));
        // 税科目C2 */
        occs006HdImpDto.setTaxItemC2(strList.get(160));
        // 税額合計C */
        occs006HdImpDto.setTaxAmoC(plusYen(strList.get(161)));
        // 欄数C */
        occs006HdImpDto.setColumnCountC(strList.get(162));
        // 納税額合計 */
        occs006HdImpDto.setTaxAmo(plusYen(strList.get(179)));
        // 担保額 */
        occs006HdImpDto.setCollateralAmo(plusYen(strList.get(180)));
        // 通貨レートA1 */
        occs006HdImpDto.setCurrencyRateA1(strList.get(181));
        // 通貨レートA2 */
        occs006HdImpDto.setCurrencyRateA2(strList.get(182));
        // 通貨レートB1 */
        occs006HdImpDto.setCurrencyRateB1(strList.get(183));
        // 通貨レートB2 */
        occs006HdImpDto.setCurrencyRateB2(strList.get(184));
        // 通貨レートC1 */
        occs006HdImpDto.setCurrencyRateC1(strList.get(185));
        // 通貨レートC2 */
        occs006HdImpDto.setCurrencyRateC2(strList.get(186));
        // 口座 */
        occs006HdImpDto.setAccountNo(strList.get(191));
        // 都道府県 */
        occs006HdImpDto.setAdd1(strList.get(188));
        // 納付方法 */
        occs006HdImpDto.setPayMethodDis(strList.get(194));
        // 構成1 */
        occs006HdImpDto.setComposition1(strList.get(195));
        // 構成2 */
        occs006HdImpDto.setComposition2(strList.get(196));

        // フッター部
        String newsAll = strList.get(197).trim();
        if (newsAll.length() < 100) {
            // 記事(税関) */
            occs006HdImpDto.setNews1(newsAll);
        } else {
            // 記事(税関) */
            occs006HdImpDto.setNews1(newsAll.substring(0, 100));
            // 記事(税関) */
            occs006HdImpDto.setNews2(newsAll.substring(100, newsAll.length()));
        }
        // 輸 入 者(入力) */
        occs006HdImpDto.setImpCustomerNameInput1(strList.get(198).substring(0, 8));
        // 輸 入 者(入力) */
        occs006HdImpDto.setImpCustomerNameInput2(strList.get(198).substring(8, 12));
        // 記事(通関) */
        occs006HdImpDto.setNews3(strList.get(199));
        // 輸入取引者(入力) */
        occs006HdImpDto.setImpDealingsNameInput1(strList.get(200).substring(0, 8));
        // 輸入取引者(入力) */
        occs006HdImpDto.setImpDealingsNameInput2(strList.get(200).substring(8, 12));
        // 記事(荷主) */
        occs006HdImpDto.setNewsShipper(strList.get(201));
        // 社内整理番号 */
        occs006HdImpDto.setInHouseRefNumber(strList.get(202));
        // 荷主セクションコード */
        occs006HdImpDto.setMerchantSectionCode(strList.get(203));
        // 荷主Ｒｅｆ Ｎｏ． */
        occs006HdImpDto.setShipperReferenceNumber(strList.get(204));
        // 利用者整理番号 */
        occs006HdImpDto.setUserRefNumber(strList.get(205));
        // バーコード */
        occs006HdImpDto.setBARCODE(strList.get(16).trim());

        // 繰返し部
        for (int i = 242; i < strList.size(); i++) {

            OCCS006HdImpDto copyDto = new OCCS006HdImpDto();
            BeanUtils.copyProperties(occs006HdImpDto, copyDto);

            // 欄 */
            copyDto.setReNum(strList.get(i++));
            // 統合先欄 */
            copyDto.setMergeCd(strList.get(i++));
            // 品目番号1 */
            copyDto.setItemNum1(decimal2(strList.get(i).substring(0,6)));
            // 品目番号2 */
            copyDto.setItemNum2(strList.get(i++).substring(6,9));
            // 品目番号3 */
            copyDto.setItemNum3(strList.get(i++));
            // 価格再確認 */
            copyDto.setValueCheckFlg(strList.get(i++));
            // 品名 */
            copyDto.setItem(strList.get(i++));
            // 数量（１）1 */
            copyDto.setPieceA1(commaStr(strList.get(i++)));
            // 数量（１）2 */
            copyDto.setPieceA2(strList.get(i++));
            // 税表番号 */
            copyDto.setExamDate(strList.get(i++));
            // 数量（２）1 */
            copyDto.setPieceB1(commaStr(strList.get(i++)));
            // 数量（２）2 */
            copyDto.setPieceB2(strList.get(i++));
            // 申告価格（ＣＩＦ）A */
            copyDto.setReportValueA(plusYen(strList.get(i++)));
            // 課税標準数量A1 */
            copyDto.setTaxBasePieceA1(commaStr(strList.get(i++)));
            // 課税標準数量A2 */
            copyDto.setTaxBasePieceA2(strList.get(i++));
            // 申告価格（ＣＩＦ）B */
            copyDto.setReportValueB(plusYen(strList.get(i++)));
            // 課税標準数量B1 */
            copyDto.setTaxBasePieceB1(commaStr(strList.get(i++)));
            // 課税標準数量B2 */
            copyDto.setTaxBasePieceB2(strList.get(i++));
            // 関税率1 */
            copyDto.setCustomRate1(strList.get(i++));
            // 関税率2 */
            copyDto.setCustomRate2(strList.get(i++));
            i++;
            // 輸入令別表 */
            copyDto.setImpTradeContCd(strList.get(i++));
            // 特恵
            copyDto.setGsp(strList.get(i++));
            // 関税額
            copyDto.setTariffAmount(plusYen(strList.get(i++)));
            // ＢＰＲ按分係数
            copyDto.setBasicPriceTotal(commaStr(strList.get(i++)));
            // 減免税額A
            copyDto.setCustomsExemptAmoA(plusYen(strList.get(i++)));
            // ＢＰＲ金額1
            copyDto.setBasicPriceAmo1(strList.get(i++));
            // ＢＰＲ金額2
            copyDto.setBasicPriceAmo2(commaStr(strList.get(i++)));
            // 減免税額B
            copyDto.setCustomsExemptAmoB(plusYen(strList.get(i++)));
            // 運賃按分
            copyDto.setFareDivDisc(strList.get(i++));
            i++;
            // 原産地1
            copyDto.setCountryOfOriginCd1(strList.get(i++));
            // 原産地2
            copyDto.setCountryOfOriginCd2(strList.get(i++));
            // 原産地3
            copyDto.setCountryOfOriginCd3(strList.get(i++));
            // 減免税1
            copyDto.setInConsTaxExemCd1(strList.get(i++));
            // 減免税2
            copyDto.setInConsTaxExemCd2(strList.get(i++));
            // 法
            copyDto.setOtherLaw1(strList.get(i++));
            i += 2;
            // 令
            copyDto.setOtherLaw2(strList.get(i++));
            i += 2;
            // 別表
            copyDto.setOtherLaw3(strList.get(i++));
            // 事前教示（分類）
            copyDto.setAdvanceTeachingClassification(strList.get(i++));
            // （原産地）
            copyDto.setAdvanceTeachingPlaceOrigin(strList.get(i++));
            // 内国消費税等(1)
            copyDto.setInConsTaxKindCd1(strList.get(i++));
            // 種別(1)
            copyDto.setType1(strList.get(i++));
            // 課税標準額(1)C
            copyDto.setTaxBaseAmount1C(plusYen(strList.get(i++)));
            // 課税標準数量(1)C1
            copyDto.setTaxBasePiece1C1(commaStr(strList.get(i++)));
            // 課税標準数量(1)C2
            copyDto.setTaxBasePiece1C2(strList.get(i++));
            // 課税標準額(1)D
            copyDto.setTaxBaseAmount1D(plusYen(strList.get(i++)));
            // 課税標準数量(1)D1
            copyDto.setTaxBasePiece1D1(commaStr(strList.get(i++)));
            // 課税標準数量(1)D2
            copyDto.setTaxBasePiece1D2(strList.get(i++));
            // 税率(1)
            copyDto.setTaxRate1(strList.get(i++));
            // 税額(1)
            copyDto.setTaxAmount1(plusYen(strList.get(i++)));
            // 減免税(1)
            copyDto.setTaxExemption1(strList.get(i++));
            // 減免税額(1)A
            copyDto.setTaxExemptionAmount1A(plusYen(strList.get(i++)));
            // 条項(1)A
            copyDto.setClause1A(strList.get(i++));
            // 減免税額(1)B
            copyDto.setTaxExemptionAmount1B(plusYen(strList.get(i++)));
            // 条項(1)B
            copyDto.setClause1B(strList.get(i++));
            // 内国消費税等(2)
            copyDto.setInConsTaxKindCd2(strList.get(i++));
            // 種別(2)
            copyDto.setType2(strList.get(i++));
            // 課税標準額(2)C
            copyDto.setTaxBaseAmount2C(plusYen(strList.get(i++)));
            // 課税標準数量(2)C1
            copyDto.setTaxBasePiece2C1(commaStr(strList.get(i++)));
            // 課税標準数量(2)C2
            copyDto.setTaxBasePiece2C2(strList.get(i++));
            // 課税標準額(2)B
            copyDto.setTaxBaseAmount2D(plusYen(strList.get(i++)));
            // 課税標準数量(2)D1
            copyDto.setTaxBasePiece2D1(commaStr(strList.get(i++)));
            // 課税標準数量(2)D2
            copyDto.setTaxBasePiece2D2(strList.get(i++));
            // 税率(2)
            copyDto.setTaxRate2(strList.get(i++));
            // 税額(2)
            copyDto.setTaxAmount2(plusYen(strList.get(i++)));
            // 減免税(2)
            copyDto.setTaxExemption2(strList.get(i++));
            // 減免税額(2)A
            copyDto.setTaxExemptionAmount2A(plusYen(strList.get(i++)));
            // 条項(2)A
            copyDto.setClause2A(strList.get(i++));
            // 減免税額(2)B
            copyDto.setTaxExemptionAmount2B(plusYen(strList.get(i++)));
            // 条項(2)B
            copyDto.setClause2B(strList.get(i));
            i += 62;

            occs006HdImpDtoList.add(copyDto);

        }

        return occs006HdImpDtoList;

    }

    /**
     * 日付の値を各種フォーマットに合わせて設定するメソッド
     */
    private void setDate() throws Exception {
        // 日付編集
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        Date date = format.parse(hdFileName.substring(12, 24));
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
        List<String> fileNameList = occs006HdMapper.selectFileName(cwbNo);

        // 申告入力控の電文種類名リスト
        List<String> hdFileTypeList = occs006HdMapper.selectHdFileName();

        // 申告入力控に該当する電文名取得
        for (String fileName : fileNameList) {
            for (String hdFileType : hdFileTypeList) {
                if (hdFileType.equals(fileName.substring(0, 6))) {
                    // 対象データが複数あった場合は、最新版を作成対象とする
                    hdFileName = fileName;
                }
            }
        }

        // S3より該当の戻り電文を取得
        String addPath = hdFileName.substring(12, 16) + "/" + hdFileName.substring(16, 18) + "/" + hdFileName.substring(18, 20) + "/";
        String key = OCCS006SpReConstants.GET_SP_RE_S3PATH + addPath + hdFileName;
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

    private String dateFormyyyyMMdd(String str) {
        String dateStr = str.trim();
        if (!StringUtils.isEmpty(dateStr)) {
            dateStr = str.substring(0, 4) + "/" + str.substring(4, 6) + "/" + str.substring(6, 8);
        }
            return dateStr;
    }

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

    private String decimal2(String str) {
        String formattedStr = str.substring(0, str.length() - 2) + "." + str.substring(str.length() - 2);
        return formattedStr;
    }

    private String changeDouble(String str) {
        if(!StringUtils.isEmpty(str)){
            str += ".00";
        }
        return str;
    }
}

