package com.kse.wmsv2.oc_cs_006.service;

import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.DateUtil;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006BondInBillConstants;
import com.kse.wmsv2.oc_cs_006.constant.OCCS006ExConstants;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006BondInBillDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.mapper.OCCS006BondInBillMapper;
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
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class OCCS006BondInBillService {
    @Autowired
    OCCS006BondInBillMapper occs006BondInBillMapper;
    @Autowired
    OCCS007ScreenMapper occs007ScreenMapper;
    @Autowired
    OCCS007Service occs007Service;
    @Autowired
    AwsS3 awsS3;

    public OCCS006ResultDto printBondInBill(List<?> dataList) throws Exception {

        OCCS006ResultDto resultDto = new OCCS006ResultDto();
        List<String> pdfPathList = new ArrayList<String>();

        // 取得データ有無チェック
        if (dataList.size() < 1) {
            String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR102;
            log.error(msg);
            resultDto.setResult(false);
            resultDto.setMessage(msg);
            resultDto.setErrorMessage(msg);
            return resultDto;
        }

        // 選択したデータに対して繰返し処理
        for (int i = 0; i < dataList.size(); i++) {
            int updateCnt = 0;

            // 搬入伝票番号取得
            if (dataList.get(i) instanceof Map<?, ?>) {
                Map<?, ?> data = (Map<?, ?>) dataList.get(i);
                String bondInBillNo = data.get("bondInBillNo").toString();

                //　搬入伝票番号取得失敗の際にはエラー処理
                if (StringUtils.isEmpty(bondInBillNo)) {
                    String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR103;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("搬入伝票番号取得完了:" + bondInBillNo);

                // CS_IMAGE_MANAGEMENTに該当の搬入伝票PDFが存在しているか確認
                String csImageManagementPath = occs006BondInBillMapper.selectCsImageManagement(bondInBillNo);
                if (!StringUtils.isEmpty(csImageManagementPath)) {
                    // PDFの保存先をpdfPathListへ追加して以下の処理は省略する
                    pdfPathList.add(csImageManagementPath);
                    continue;
                }
                log.info("PDF作成履歴:　無");

                // 該当電文より、PDF作成用Dtoに値をセット
                log.info("戻り電文より必要データ取得開始");
                List<OCCS006BondInBillDto> bondInBillDtoList = getExpandData(bondInBillNo);
                if (bondInBillDtoList.size() < 1) {
                    String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR104;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("データ件数:　" + bondInBillDtoList.size() + "件");


                // 搬入伝票作成
                log.info("PDF作成開始");
                Report report = new Report(ReadUtil.readJson(OCCS006BondInBillConstants.BONDINBILL_PATH));
                report.fill(new ReportDataSource(bondInBillDtoList));
                ReportPages pages = report.getPages();
                String fileName = bondInBillDtoList.get(0).getDateyyyyMMddHHmmss2() + "_" + bondInBillNo + "_" + OCCS006BondInBillConstants.BONDINBILL_IMAGECLASS + ".pdf";
                String folderPath = OCCS006BondInBillConstants.BONDINBILL_OUT_PATH;
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

                // AE_DATAの更新(印刷履歴登録)
                log.info("AE_DATAテーブル更新開始");
                for (OCCS006BondInBillDto dto : bondInBillDtoList) {
                    Map<String, String> paramMap = new HashMap<>();
                    paramMap.put("bondInBillNo", bondInBillNo);
                    paramMap.put("cwbNo", dto.getCwbNo());
                    paramMap.put("date", bondInBillDtoList.get(0).getDateyyyyMMddHHmmss());
                    updateCnt += occs006BondInBillMapper.updateBondInBill(paramMap);
                    if (updateCnt < 1) {
                        String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR105;;
                        log.error(msg);
                        resultDto.setResult(false);
                        resultDto.setMessage(msg);
                        resultDto.setErrorMessage(msg);
                        return resultDto;
                    }
                }
                log.info("AE_DATAテーブル更新件数: " + updateCnt + "件");

                // S3 Upload
                log.info("S3 Upload開始");
                String keyName = OCCS006BondInBillConstants.BONDINBILL_S3_PATH;
                keyName += bondInBillDtoList.get(0).getDateyyyyMMdd().substring(0, 4) + "/";
                keyName += bondInBillDtoList.get(0).getDateyyyyMMdd().substring(5, 7) + "/";
                keyName += bondInBillDtoList.get(0).getDateyyyyMMdd().substring(8, 10) + "/";
                keyName += fileName;

                byte[] fileBytes = Files.readAllBytes(new File(filePath).toPath());
                int uploadCnt = awsS3.uploadPdf(fileBytes, keyName);
                if (uploadCnt < 1) {
                    String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR106 + "[filePath:" + fileName + "]";
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("S3 Upload件数： " + uploadCnt + "件");


                // CS_IMAGE_MANAGEMENT更新
                log.info("CS_IMAGE_MANAGEMENT更新開始");
                OCCS007InsertCsImageManagementDao parm = occs007Service.setCsImageManagementDao("輸出", keyName, "SYSTEM", bondInBillDtoList.get(0).getDateyyyyMMddHHmmss());
                int insertCnt = occs007ScreenMapper.insertCsImageManagement(parm);
                if (insertCnt < 1) {
                    String msg = OCCS006BondInBillConstants.BONDINBILL_ERROR107;
                    log.error(msg);
                    resultDto.setResult(false);
                    resultDto.setMessage(msg);
                    resultDto.setErrorMessage(msg);
                    return resultDto;
                }
                log.info("CS_IMAGE_MANAGEMENT追加データ件数： " + insertCnt + "件");

                // S3保存先をpdfPathリストへ
                pdfPathList.add(keyName);

                // ETCファイル削除
                File file = new File(filePath);
                boolean deleted = file.delete();
            }
        }

        // 正常return値セット
        String msg = OCCS006BondInBillConstants.BONDINBILL_SUCCESS;
        log.info(msg);
        resultDto.setResult(true);
        resultDto.setMessage(msg);
        resultDto.setFilePathList(pdfPathList);

        return resultDto;
    }

    private List<OCCS006BondInBillDto> getExpandData(String bondInBillNo) throws Exception {
        List<OCCS006BondInBillDto> occs006BondInBillDtoList = new ArrayList<OCCS006BondInBillDto>();
        // bondInBillNoに該当する戻り電文リスト
        List<String> fileNameList = occs006BondInBillMapper.selectFileName(bondInBillNo);
        if (fileNameList.size() < 1) {
            return occs006BondInBillDtoList;
        }

        // 搬入伝票に該当する電文種類名リスト
        List<String> biFileTypeList = occs006BondInBillMapper.selectBiFileName(bondInBillNo);

        // 戻り電文リストに搬入伝票に該当する電文があるのかチェック
        List<String> biFileNameList = new ArrayList<String>();
        for (String fileName : fileNameList) {
            for (String exFileType : biFileTypeList) {
                if (exFileType.equals(fileName.substring(0, 6))) {
                    biFileNameList.add(fileName);
                }
            }
        }

        // S3より該当の戻り電文を取得
        for (String biFileName : biFileNameList) {
            // S3から該当ファイル取得
            String addPath = biFileName.substring(12, 16) + "/" + biFileName.substring(16, 18) + "/" + biFileName.substring(18, 20) + "/";
            String key = OCCS006BondInBillConstants.GET_BONDINBILL_S3PATH + addPath + biFileName;
            byte[] data = awsS3.get(key);
            // Shift_JSへEncoding
            String strValue = new String(data, Charset.forName("Shift_JIS"));
            String[] strArr = strValue.split("\n");

            //　取得した戻り電文の内容を文字列リストで編集
            List<String> strList = new ArrayList<String>();
            for (String str : strArr) {
                strList.add(str);
            }

            // 搬入伝票Dto設定用値編集
            // 日付編集
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
            Date date = format.parse(biFileName.substring(12, 24));
            String yyyyMMddHHmm = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm");
            String yyyyMMddHHmmss = DateUtil.dateFormatChange(date, "yyyy/MM/dd HH:mm:ss");
            String yyyyMMddHHmmss2 = DateUtil.dateFormatChange(date, "yyyyMMddHHmmss");
            String yyyyMMdd = DateUtil.dateFormatChange(date, "yyyy.MM.dd");

            // 搬入伝票Dtoへ電文内容をセット
            /** */
            for(int i = 5; i < strList.size(); i++){
                OCCS006BondInBillDto bondInBillDto = new OCCS006BondInBillDto();
                //　日付
                bondInBillDto.setDateyyyyMMddHHmm(yyyyMMddHHmm);
                bondInBillDto.setDateyyyyMMddHHmmss(yyyyMMddHHmmss);
                bondInBillDto.setDateyyyyMMddHHmmss2(yyyyMMddHHmmss2);
                // 搬入伝票番号
                bondInBillDto.setBondInBillNo(strList.get(1).trim());
                // H/W
                bondInBillDto.setBondedWareHouseCd(strList.get(2).trim());
                // yyyy.MM.dd
                bondInBillDto.setDateyyyyMMdd(yyyyMMdd);
                // A/G
                bondInBillDto.setReportPlanPersonCd(strList.get(4).trim());
                // CWBNO
                bondInBillDto.setCwbNo(strList.get(i++).trim());
                // PCS
                bondInBillDto.setTotalPiece(strList.get(i++).trim());
                // GPCS(あて先官署コード)
                bondInBillDto.setCarryingPiece(strList.get(i++).trim());
                // WT（KGM）
                bondInBillDto.setCarryingWeight(strList.get(i++).trim());
                // PORT
                bondInBillDto.setOrigin(strList.get(i++).trim());
                // DST
                bondInBillDto.setFltDestination(strList.get(i++).trim());
                // J
                bondInBillDto.setTotalWeight(strList.get(i++).trim());
                // B.O
                bondInBillDto.setAgentDiv(strList.get(i++).trim());
                // SPC
                bondInBillDto.setSpecialCargoSign(strList.get(i++).trim());
                // COMMODITY
                bondInBillDto.setSpsDocClassCd(strList.get(i++).trim());
                // A/L
                bondInBillDto.setAirLine(strList.get(i).trim());
                occs006BondInBillDtoList.add(bondInBillDto);
            }
        }
        return occs006BondInBillDtoList;
    }

}