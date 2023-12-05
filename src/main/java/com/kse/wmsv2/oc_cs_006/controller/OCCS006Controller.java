package com.kse.wmsv2.oc_cs_006.controller;

import com.kse.wmsv2.oc_cs_006.constant.*;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.service.OCCS006BondInBillService;
import com.kse.wmsv2.oc_cs_006.service.OCCS006ExService;
import com.kse.wmsv2.oc_cs_006.service.OCCS006HdService;
import com.kse.wmsv2.oc_cs_006.service.OCCS006PermitService;
import com.kse.wmsv2.oc_cs_006.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/occs006")
public class OCCS006Controller {

    @Autowired
    private OCCS006BondInBillService occs006BondInBillService;
    @Autowired
    private OCCS006ExService occs006ExService;
    @Autowired
    private OCCS006HdService occs006HdService;
    @Autowired
    private OCCS006SpReService occs006SpReService;
    @Autowired
    private OCCS006SpRe2Service occs006SpRe2Service;
    @Autowired
    private OCCS006PermitService occs006PermitService;

    /**
     * 搬入伝票の印刷を行う
     * @param dataList 画面で選択したデータリスト
     * @return 処理結果とS3内のPDFファイルパスをreturnする。
     */
    @PostMapping("/printBondInBill")
    public OCCS006ResultDto printBondInBill(@RequestBody Object dataList) {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();

        try {
            log.info("搬入伝票作成処理を開始します。");
            resultDto = occs006BondInBillService.printBondInBill((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = OCCS006BondInBillConstants.BONDINBILL_ERROR101;
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }
    @PostMapping("/printPermit")
    public OCCS006ResultDto printPermit(@RequestBody Object dataList){
        OCCS006ResultDto resultDto = new OCCS006ResultDto();
        try {

            log.info("許可作成処理を開始します。");
            resultDto = occs006PermitService.permitPermit((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = "許可票作成処理でエラーが発生しました。";
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

    /**
     * 検査指定票の印刷を行う
     * @param dataList 画面で選択したデータリスト
     * @return 処理結果とS3内のPDFファイルパスをreturnする。
     */
    @PostMapping("/printExam")
    public OCCS006ResultDto printExam(@RequestBody Object dataList) {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();

        try {
            log.info("検査指定票の作成処理を開始します。");
            resultDto = occs006ExService.printExam((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = OCCS006ExConstants.EX_ERROR101;
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

    /**
     * 申告入力控の印刷を行う
     * @param dataList 画面で選択したデータリスト
     * @return 処理結果とS3内のPDFファイルパスをreturnする。
     */
    @PostMapping("/printHd")
    public OCCS006ResultDto printHd(@RequestBody Object dataList) {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();

        try {

            log.info("申告入力控の作成処理を開始します。");
            resultDto = occs006HdService.printHd((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = OCCS006HdConstants.HD_ERROR101;
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

    /**
     * 申告控の印刷を行う
     * @param dataList 画面で選択したデータリスト
     * @return 処理結果とS3内のPDFファイルパスをreturnする。
     */
    @PostMapping("/printSpRe")
    public OCCS006ResultDto printSpRe(@RequestBody Object dataList) {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();

        try {

            log.info("申告控の作成処理を開始します。");
            resultDto = occs006SpReService.printSpRe((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = OCCS006SpReConstants.SP_RE_ERROR101;
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

    /**
     * マニフェスト通関申告控の印刷を行う
     * @param dataList 画面で選択したデータリスト
     * @return 処理結果とS3内のPDFファイルパスをreturnする。
     */
    @PostMapping("/printSpRe2")
    public OCCS006ResultDto printSpRe2(@RequestBody Object dataList) {
        OCCS006ResultDto resultDto = new OCCS006ResultDto();

        try {

            log.info("マニフェスト通関申告控の作成処理を開始します。");
            resultDto = occs006SpRe2Service.printSpRe2((List<?>) dataList);

            if(!resultDto.isResult()){
                log.warn(resultDto.getErrorMessage());
                return resultDto;
            }

        } catch(Exception e) {

            String errMsg = OCCS006SpRe2Constants.SP_RE_ERROR101;
            log.error(errMsg + "\\n" + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage(e.getMessage());

        } finally {

            return resultDto;

        }
    }

}


