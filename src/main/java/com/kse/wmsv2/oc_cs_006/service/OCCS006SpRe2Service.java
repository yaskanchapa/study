package com.kse.wmsv2.oc_cs_006.service;


import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006SpRe2Constants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006SpRe2ExpDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006SpRe2ImpDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006SpRe2Mapper;
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
public class OCCS006SpRe2Service {
    @Autowired
    OCCS006SpRe2Mapper occs006SpRe2Mapper;
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

    public OCCS006ResultDto printSpRe2(List<?> dataList) throws Exception {


        // 取得データ有無チェック
        if (dataList.size() < 1) {
            String msg = OCCS006SpRe2Constants.SP_RE_ERROR102;
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
                /** exp test*/
                String cwbNo = "SF6310904975773";
                /** */
                /** imp test*/
//                String cwbNo = "359780006316";
                /** */

                //　cwbNo取得失敗の際にはエラー処理
                if (StringUtils.isEmpty(cwbNo)) {
                    String msg = OCCS006SpRe2Constants.SP_RE_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CWBNO取得完了:" + cwbNo);

                // CS_IMAGE_MANAGEMENTに該当のマニフェスト通関申告控のPDFが存在しているか確認
                String csImageManagementPath = occs006SpRe2Mapper.selectCsImageManagement(cwbNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPath.add(csImageManagementPath);
                    continue;
                }
                log.info("PDF作成履歴:　無");

                // 該当電文より、PDF作成用Dtoに値をセット
                log.info("戻り電文より必要データ取得開始");

                // 対象電文を対象Dtoに設定
                List<?> spRe2DtoList = getExpandData(cwbNo);
                if (spRe2DtoList.size() < 1) {
                    String msg = OCCS006SpRe2Constants.SP_RE_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }

                // 設定したDtoが輸出マニフェスト通関申告控の場合
                if (spRe2DtoList.get(0) instanceof OCCS006SpRe2ExpDto) {
                    resultDto = makePdfSpRe2Exp((List<OCCS006SpRe2ExpDto>) spRe2DtoList, cwbNo);
                } else if (spRe2DtoList.get(0) instanceof OCCS006SpRe2ImpDto) {
                    resultDto = makePdfSpRe2Imp((List<OCCS006SpRe2ImpDto>) spRe2DtoList, cwbNo);
                }
            }
        }

        // 正常return値セット
        String msg = OCCS006SpRe2Constants.SP_RE_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPath);

        return resultDto;
    }


    /**
     * 文字列リスト化した対象電文を該当のDtoに設定する。
     *
     * @return 文字列リスト化した対象電文を設定したDto
     * @parm cwbNo　ハウス番号
     */
    private List<?> getExpandData(String cwbNo) throws Exception {
        List<?> spRe2DtoList = new ArrayList<>();
        // 該当戻り電文データ取得
        List<String> strList = getTelegramStrList(cwbNo);
        setDate();
        // 戻り電文データをDtoへセット
        switch (spReFileName.substring(0, 6)) {
            case "AAE1JD":
                impExpClass = "<AIR/EXP>";
                imageClassName = "SP";
                spRe2DtoList = setExpDto(strList);
                return spRe2DtoList;
            case "AAE2JD":
            case "AAE3JD":
                impExpClass = "<AIR/EXP>";
                imageClassName = "RE";
                spRe2DtoList = setExpDto(strList);
                return spRe2DtoList;
            case "AAD1FD":
            case "AAD2FD":
            case "AAD3FD":
                impExpClass = "<AIR/IMP>";
                imageClassName = "SP";
                spRe2DtoList = setImpDto(strList);
                return spRe2DtoList;
            case "AAD1FC":
            case "AAD2FC":
            case "AAD3FC":
                impExpClass = "<AIR/IMP>";
                imageClassName = "RE";
                spRe2DtoList = setImpDto(strList);
                return spRe2DtoList;
            default:
                break;
        }
        return spRe2DtoList;
    }

    /**
     * 輸出マニフェスト通関申告控に該当するDtoに、取得した電文内容を設定する。
     *
     * @param strList 文字列リスト化した電文
     * @return 輸出マニフェスト通関申告控に該当するDto
     */
    private List<OCCS006SpRe2ExpDto> setExpDto(List<String> strList) {
        // マニフェスト通関申告控Dtoへ電文内容をセット
        List<OCCS006SpRe2ExpDto> spRe2ExpDtoList = new ArrayList<OCCS006SpRe2ExpDto>();
        OCCS006SpRe2ExpDto spRe2ExpDto = new OCCS006SpRe2ExpDto();
        // 輸入・輸出・イメージクラス設定
        spRe2ExpDto.setImpExpClass(impExpClass);
        spRe2ExpDto.setImageClassName(imageClassName);
        //　日付
        // 日付1 */
        spRe2ExpDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
        // 日付2 */
        spRe2ExpDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
        // 日付3 */
        spRe2ExpDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
        // 日付4 */
        spRe2ExpDto.setDateyyyyMMdd(yyyyMMdd);

        //　ヘッダー部
        // 申告先種別
        spRe2ExpDto.setCustomsKindCd(strList.get(1));
        // 識別符号
        spRe2ExpDto.setDiscernmentMark(strList.get(2));
        // 区分
        spRe2ExpDto.setReportClass(strList.get(3));
        // あて先税関
        spRe2ExpDto.setReportDepCd(strList.get(4));
        // 提出先
        spRe2ExpDto.setReportDivisionCd(strList.get(5));
        // 申告年月日
        spRe2ExpDto.setReportDate(dateFormyyyyMMdd(strList.get(6)));
        // 申告番号
        spRe2ExpDto.setReportNo(strList.get(7));

        // ボディー部
        // 申告条件
        spRe2ExpDto.setReportCondition(strList.get(8));
        // 搬入
        spRe2ExpDto.setCarryingIn(strList.get(10));
        // 輸出者1
        spRe2ExpDto.setExpCustomerName1(strList.get(11).substring(0, 13));
        // 輸出者2
        spRe2ExpDto.setExpCustomerName2(strList.get(11).substring(13, 17));
        // 輸出者3
        spRe2ExpDto.setExpCustomerName3(strList.get(12));
        // 住所1
        spRe2ExpDto.setExpCustomerAdd1(strList.get(13));
        // 住所2
        spRe2ExpDto.setExpCustomerAdd2(strList.get(14));
        // 住所3
        spRe2ExpDto.setExpCustomerAdd3(strList.get(15));
        // 住所4
        spRe2ExpDto.setExpCustomerAdd4(strList.get(16));
        // 住所5
        spRe2ExpDto.setExpCustomerAdd5(strList.get(17));
        // 住所6
        spRe2ExpDto.setExpCustomerAdd6(strList.get(18));
        // 電話
        spRe2ExpDto.setExpCustomerTel(strList.get(19));
        // 税関事務管理人1
        spRe2ExpDto.setCustomsOfficeJanitorName1(strList.get(20).substring(0, 13));
        // 税関事務管理人2
        spRe2ExpDto.setCustomsOfficeJanitorName2(strList.get(20).substring(13, 17));
        // 税関事務管理人3
        spRe2ExpDto.setCustomsOfficeJanitorName3(strList.get(21).substring(0, 4));
        // 税関事務管理人4
        spRe2ExpDto.setCustomsOfficeJanitorName4(strList.get(21).substring(4, 6));
        // 税関事務管理人5
        spRe2ExpDto.setCustomsOfficeJanitorName5(strList.get(21).substring(6, 10));
        // 税関事務管理人6
        spRe2ExpDto.setCustomsOfficeJanitorName6(strList.get(22));
        // 仕向人1
        spRe2ExpDto.setConsigneeNo1(strList.get(23).substring(0, 8));
        // 仕向人2
        spRe2ExpDto.setConsigneeNo2(strList.get(23).substring(8, 12));
        // 仕向人3
        spRe2ExpDto.setConsigneeName(strList.get(24));
        // 住所1
        spRe2ExpDto.setConsigneeAdd1(strList.get(25));
        // 住所2
        spRe2ExpDto.setConsigneeAdd2(strList.get(26));
        // 住所3
        spRe2ExpDto.setConsigneeAdd3(strList.get(27));
        // 住所4
        spRe2ExpDto.setConsigneeAdd4(strList.get(28));
        // 住所5
        spRe2ExpDto.setConsigneeAdd5(strList.get(29));
        // 国コード
        spRe2ExpDto.setConsigneeCountry(strList.get(30));
        // 住所6
        spRe2ExpDto.setConsigneeAdd6(strList.get(31));
        // 代理人1
        spRe2ExpDto.setAgent1(strList.get(32));
        // 代理人2
        spRe2ExpDto.setAgent2(strList.get(33));
        // 通関士コード
        spRe2ExpDto.setReportCustomsSpecialistUserCd(strList.get(34));
        // 検査立会者
        spRe2ExpDto.setInspectionWitness(strList.get(35));
        // HAWB番号先頭3桁
        spRe2ExpDto.setCwbNo1(strList.get(36).substring(0, 3));
        // HAWB番号
        spRe2ExpDto.setCwbNo2(strList.get(36).substring(3, strList.get(36).length()));
        // 最終仕向地1
        spRe2ExpDto.setLastDestinationCd1(strList.get(37));
        // 最終仕向地2
        spRe2ExpDto.setLastDestinationCd2(strList.get(38));
        // 積込港1
        spRe2ExpDto.setDepPortCd1(strList.get(39));
        // 積込港2
        spRe2ExpDto.setDepPortCd2(strList.get(40));
        // 貨物個数
        spRe2ExpDto.setCarryingPiece(commaStr(strList.get(41)));
        // 貨物重量1
        spRe2ExpDto.setCarryingWeight(commaStr(strList.get(42)));
        // 保税地域1
        spRe2ExpDto.setCustomsPlaceCd1(strList.get(43));
        // 保税地域2
        spRe2ExpDto.setCustomsPlaceCd2(strList.get(44));
        // 蔵置税関1
        spRe2ExpDto.setBondedWareHouseCustomsCd1(strList.get(45));
        // 蔵置税関2
        spRe2ExpDto.setBondedWareHouseCustomsCd2(strList.get(46));
        // 通貨レート1
        spRe2ExpDto.setCurrencyRate1(strList.get(47));
        // 通貨レート2
        spRe2ExpDto.setCurrencyRate2(strList.get(48));
        // ＦＯＢ価格1
        spRe2ExpDto.setFobAmo1(strList.get(49));
        // ＦＯＢ価格2
        spRe2ExpDto.setFobAmo2(commaStr(strList.get(50)));
        // 品名
        spRe2ExpDto.setItem(strList.get(51));
        // 申告価格1
        spRe2ExpDto.setReportValue1(plusYen(strList.get(52)));
        // 申告価格2
        spRe2ExpDto.setReportValue2(strList.get(53));
        // 記事
        spRe2ExpDto.setNews1(strList.get(54));
        // 荷主セクションコード
        spRe2ExpDto.setMerchantSectionCode(strList.get(55));
        // 荷主Ｒｅｆ Ｎｏ．
        spRe2ExpDto.setShipperReferenceNumber(strList.get(56));
        // 社内整理番号
        spRe2ExpDto.setInHouseRefNumber(strList.get(57));
        // 輸出者(入力)1
        spRe2ExpDto.setExpCustomerNameInput1(strList.get(58).substring(0, 8));
        // 輸出者(入力)2
        spRe2ExpDto.setExpCustomerNameInput2(strList.get(58).substring(8, 12));
        spRe2ExpDtoList.add(spRe2ExpDto);

        return spRe2ExpDtoList;

    }

    /**
     * 輸入マニフェスト通関申告控に該当するDtoに、取得した電文内容を設定する。
     *
     * @param strList 文字列リスト化した電文
     * @return 輸入マニフェスト通関申告控に該当するDto
     */
    private List<OCCS006SpRe2ImpDto> setImpDto(List<String> strList) {
        // マニフェスト通関申告控Dtoへ電文内容をセット
        List<OCCS006SpRe2ImpDto> spRe2ImpDtoList = new ArrayList<OCCS006SpRe2ImpDto>();
        OCCS006SpRe2ImpDto spRe2ImpDto = new OCCS006SpRe2ImpDto();
        // 輸入・輸出・イメージクラス設定
        spRe2ImpDto.setImpExpClass(impExpClass);
        spRe2ImpDto.setImageClassName(imageClassName);
        //　日付
        // 日付1 */
        spRe2ImpDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
        // 日付2 */
        spRe2ImpDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
        // 日付3 */
        spRe2ImpDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
        // 日付4 */
        spRe2ImpDto.setDateyyyyMMdd(yyyyMMdd);

        //　ヘッダー部
        // 申告先種別
        spRe2ImpDto.setCustomsKindCd(strList.get(1));
        // 識別符号
        spRe2ImpDto.setDiscernmentMark(strList.get(2));
        // 区分
        spRe2ImpDto.setReportClass(strList.get(3));
        // あて先税関
        spRe2ImpDto.setReportDepCd(strList.get(4));
        // 提出先
        spRe2ImpDto.setReportDivisionCd(strList.get(5));
        // 申告年月日
        spRe2ImpDto.setReportDate(dateFormyyyyMMdd(strList.get(6)));
        // 申告番号
        spRe2ImpDto.setReportNo(strList.get(7));

        // ボディー部
        // 申告条件
        spRe2ImpDto.setReportCondition(strList.get(8));
        // 申告予定年月日
        spRe2ImpDto.setReportPlanDate(dateFormyyyyMMdd(strList.get(9)));
        // 本申告
        spRe2ImpDto.setDeclara(strList.get(10));
        // 輸入者1
        spRe2ImpDto.setImpCustomerName1(strList.get(11).substring(0, 13));
        // 輸入者2
        spRe2ImpDto.setImpCustomerName2(strList.get(11).substring(13, 17));
        // 輸入者3
        spRe2ImpDto.setImpCustomerName3(strList.get(12));
        // 住所1
        spRe2ImpDto.setImpCustomerAdd1(strList.get(13));
        // 住所2
        spRe2ImpDto.setImpCustomerAdd2(strList.get(14));
        // 住所3
        spRe2ImpDto.setImpCustomerAdd3(strList.get(15));
        // 住所4
        spRe2ImpDto.setImpCustomerAdd4(strList.get(16));
        // 住所5
        spRe2ImpDto.setImpCustomerAdd5(strList.get(17));
        // 住所6
        spRe2ImpDto.setImpCustomerAdd6(strList.get(18));
        // 電話
        spRe2ImpDto.setImpCustomerTel(strList.get(19));
        // 税関事務管理人1
        spRe2ImpDto.setCustomsOfficeJanitorName1(strList.get(20).substring(0, 13));
        // 税関事務管理人2
        spRe2ImpDto.setCustomsOfficeJanitorName2(strList.get(20).substring(4, 17));
        // 税関事務管理人3
        spRe2ImpDto.setCustomsOfficeJanitorName3(strList.get(21).substring(0, 4));
        // 税関事務管理人4
        spRe2ImpDto.setCustomsOfficeJanitorName4(strList.get(21).substring(4, 6));
        // 税関事務管理人5
        spRe2ImpDto.setCustomsOfficeJanitorName5(strList.get(21).substring(6, 10));
        // 税関事務管理人6
        spRe2ImpDto.setCustomsOfficeJanitorName6(strList.get(22));
        // 仕出人1
        spRe2ImpDto.setExpCustomerNo1(strList.get(23).substring(0, 8));
        // 仕出人2
        spRe2ImpDto.setExpCustomerNo2(strList.get(23).substring(8, 12));
        // 仕出人3
        spRe2ImpDto.setExpCustomerNo3(strList.get(24));
        // 住所1
        spRe2ImpDto.setExpCustomerAdd1(strList.get(25));
        // 住所2
        spRe2ImpDto.setExpCustomerAdd2(strList.get(26));
        // 住所3
        spRe2ImpDto.setExpCustomerAdd3(strList.get(27));
        // 住所4
        spRe2ImpDto.setExpCustomerAdd4(strList.get(28));
        // 住所5
        spRe2ImpDto.setExpCustomerAdd5(strList.get(29));
        // 国コード
        spRe2ImpDto.setExpCustomerCountry(strList.get(30));
        // 住所6
        spRe2ImpDto.setExpCustomerAdd6(strList.get(31));
        // 代理人1
        spRe2ImpDto.setAgent1(strList.get(32));
        // 代理人2
        spRe2ImpDto.setAgent2(strList.get(33));
        // 通関士コード
        spRe2ImpDto.setReportCustomsSpecialistUserCd(strList.get(34));
        // 検査立会者
        spRe2ImpDto.setInspectionWitness(strList.get(35));
        // HAWB番号先頭3桁
        spRe2ImpDto.setCwbNo1(strList.get(36).substring(0, 4));
        // HAWB番号
        spRe2ImpDto.setCwbNo2(strList.get(36).substring(4, strList.get(36).length()));
        // MAWB番号先頭3桁
        spRe2ImpDto.setAwbNo1(strList.get(37).substring(0, 4));
        // MAWB番号
        spRe2ImpDto.setAwbNo2(strList.get(37).substring(4, strList.get(37).length()));
        // 取卸港1
        spRe2ImpDto.setPortName1(strList.get(38));
        // 取卸港2
        spRe2ImpDto.setPortName2(strList.get(39));
        // 積出地1
        spRe2ImpDto.setShipmentCd1(strList.get(40));
        // 積出地2
        spRe2ImpDto.setShipmentCd2(strList.get(41));
        // 積載機名
        spRe2ImpDto.setLoadingPlanFlt(strList.get(42));
        // 入港年月日
        spRe2ImpDto.setArrPortDate(dateFormyyyyMMdd(strList.get(43)));
        // 蔵置税関1
        spRe2ImpDto.setBondedWareHouseCustomsCd1(strList.get(44));
        // 蔵置税関2
        spRe2ImpDto.setBondedWareHouseCustomsCd2(strList.get(45));
        // 貨物個数
        spRe2ImpDto.setCarryingPiece(commaStr(strList.get(46)));
        // 貨物重量1
        spRe2ImpDto.setCarryingWeight(commaStr(strList.get(47)));
        // 保税地域1
        spRe2ImpDto.setCustomsPlaceCd1(strList.get(48));
        // 保税地域2
        spRe2ImpDto.setCustomsPlaceCd2(strList.get(49));
        // 通貨レートA1
        spRe2ImpDto.setCurrencyRateA1(strList.get(50));
        // 通貨レートA2
        spRe2ImpDto.setCurrencyRateA2(strList.get(51));
        // 通貨レートB1
        spRe2ImpDto.setCurrencyRateB1(strList.get(52));
        // 通貨レートB2
        spRe2ImpDto.setCurrencyRateB2(strList.get(53));
        // 通貨レートC1
        spRe2ImpDto.setCurrencyRateC1(strList.get(54));
        // 通貨レートC2
        spRe2ImpDto.setCurrencyRateC2(strList.get(55));
        // 仕入書価格1
        spRe2ImpDto.setInvoiceValue1(strList.get(56));
        // 仕入書価格2
        spRe2ImpDto.setInvoiceValue2(strList.get(57));
        // 仕入書価格3
        spRe2ImpDto.setInvoiceValue3(strList.get(58));
        // 仕入書価格4
        spRe2ImpDto.setInvoiceValue4(commaStr(strList.get(59)));
        // 運賃1
        spRe2ImpDto.setFareCurrencyCd1(strList.get(60));
        // 運賃2
        spRe2ImpDto.setFareCurrencyCd2(strList.get(61));
        // 運賃3
        spRe2ImpDto.setFareCurrencyCd3(commaStr(strList.get(62)));
        // 保険1
        spRe2ImpDto.setInsurance1(strList.get(63));
        // 保険2
        spRe2ImpDto.setInsurance2(strList.get(64));
        // 保険3
        spRe2ImpDto.setInsurance3(commaStr(strList.get(65)));
        // 品名
        spRe2ImpDto.setItem(strList.get(66));
        // 申告価格1
        spRe2ImpDto.setReportValue1(plusYen(strList.get(67)));
        // 申告価格2
        spRe2ImpDto.setReportValue2(strList.get(68));
        // 原産地1
        spRe2ImpDto.setOrigin1(strList.get(69));
        // 原産地2
        spRe2ImpDto.setOrigin2(strList.get(70));
        // 記事
        spRe2ImpDto.setNews1(strList.get(71));
        // 輸入者(入力)1
        spRe2ImpDto.setImpCustomerNameInput1(strList.get(72).substring(0, 8));
        // 輸入者(入力)2
        spRe2ImpDto.setImpCustomerNameInput2(strList.get(72).substring(8, 12));
        // 社内整理番号
        spRe2ImpDto.setInHouseRefNumber(strList.get(73));
        // 荷主セクションコード
        spRe2ImpDto.setMerchantSectionCode(strList.get(74));
        // 荷主Ｒｅｆ Ｎｏ．
        spRe2ImpDto.setShipperReferenceNumber(strList.get(75));
        spRe2ImpDtoList.add(spRe2ImpDto);

        return spRe2ImpDtoList;
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
        List<String> fileNameList = occs006SpRe2Mapper.selectFileName(cwbNo);

        // マニフェスト通関申告控の電文種類名リスト
        List<String> spReFileTypeList = occs006SpRe2Mapper.selectSpReFileName();

        // マニフェスト通関申告控に該当する電文名取得
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
        String key = OCCS006SpRe2Constants.GET_SP_RE_S3PATH + addPath + spReFileName;
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
     * 輸出マニフェスト通関申告控のpdfファイル作成メソッド
     *
     * @return 処理結果としては、OCCS006ResultDtoをreturnする
     * @parm spReExpLDtoList 輸出マニフェスト通関申告控に該当するDto
     * @parm cwbNo ハウス番号
     */
    private OCCS006ResultDto makePdfSpRe2Exp(List<OCCS006SpRe2ExpDto> spRe2ExpDtoList, String cwbNo) throws Exception {
        // 輸出マニフェスト通関申告控のrrpt取得及びpdf作成
        Report report = new Report(ReadUtil.readJson(OCCS006SpRe2Constants.SP_RE_EXP_PATH));
        report.fill(new ReportDataSource(spRe2ExpDtoList));
        ReportPages pages = report.getPages();
        String fileName = spRe2ExpDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + spRe2ExpDtoList.get(0).getImageClassName() + ".pdf";
        String folderPath = OCCS006SpRe2Constants.SP_RE_OUT_PATH;
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
        if ("SP".equals(spRe2ExpDtoList.get(0).getImageClassName())) {
            keyName = OCCS006SpRe2Constants.SP_EXP_S3PATH;
        } else {
            keyName = OCCS006SpRe2Constants.RE_EXP_S3PATH;
        }
        keyName += spRe2ExpDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
        keyName += spRe2ExpDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
        keyName += spRe2ExpDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
        keyName += fileName;

        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
        if (uploadCnt < 1) {
            String msg = OCCS006SpRe2Constants.SP_RE_ERROR106 + "[filePath:" + fileName + "]";
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }
        log.info("S3 Upload件数： " + uploadCnt + "件");

        // CS_IMAGE_MANAGEMENTテーブル更新
        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸出", keyName, "SYSTEM", spRe2ExpDtoList.get(0).getDateyyyyMMddHHmmss());

        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
        if (insertCnt < 1) {
            String msg = OCCS006SpRe2Constants.SP_RE_ERROR107;
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
     * 輸入マニフェスト通関申告控のpdfファイル作成メソッド
     *
     * @return 処理結果としては、OCCS006ResultDtoをreturnする
     * @parm spReExpLDtoList 輸入マニフェスト通関申告控に該当するDto
     * @parm cwbNo ハウス番号
     */
    private OCCS006ResultDto makePdfSpRe2Imp(List<OCCS006SpRe2ImpDto> spRe2ImpDtoList, String cwbNo) throws Exception {
        // 輸出マニフェスト通関申告控のrrpt取得及びpdf作成
        Report report = new Report(ReadUtil.readJson(OCCS006SpRe2Constants.SP_RE_IMP_PATH));
        report.fill(new ReportDataSource(spRe2ImpDtoList));
        ReportPages pages = report.getPages();
        String fileName = spRe2ImpDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + cwbNo + "_" + spRe2ImpDtoList.get(0).getImageClassName() + ".pdf";
        String folderPath = OCCS006SpRe2Constants.SP_RE_OUT_PATH;
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
        if ("SP".equals(spRe2ImpDtoList.get(0).getImageClassName())) {
            keyName = OCCS006SpRe2Constants.SP_EXP_S3PATH;
        } else {
            keyName = OCCS006SpRe2Constants.RE_EXP_S3PATH;
        }
        keyName += spRe2ImpDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
        keyName += spRe2ImpDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
        keyName += spRe2ImpDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
        keyName += fileName;

        byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
        int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
        if (uploadCnt < 1) {
            String msg = OCCS006SpRe2Constants.SP_RE_ERROR106 + "[filePath:" + fileName + "]";
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }
        log.info("S3 Upload件数： " + uploadCnt + "件");

        // CS_IMAGE_MANAGEMENTテーブル更新
        OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸入", keyName, "SYSTEM", spRe2ImpDtoList.get(0).getDateyyyyMMddHHmmss());

        int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
        if (insertCnt < 1) {
            String msg = OCCS006SpRe2Constants.SP_RE_ERROR107;
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

