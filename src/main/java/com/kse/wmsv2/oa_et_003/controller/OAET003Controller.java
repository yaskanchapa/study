package com.kse.wmsv2.oa_et_003.controller;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_et_003.dao.OAET003SearchResultDao;
import com.kse.wmsv2.oa_et_003.dto.OAET003SearchDto;
import com.kse.wmsv2.oa_et_003.dao.OAET003SearchPrintResultDao;
import com.kse.wmsv2.oa_et_003.dto.OAET003SearchPrintDto;
import com.kse.wmsv2.oa_et_003.service.OAET003ScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/oaet003")
public class OAET003Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OAET003ScreenService screenServ;
    @Autowired
    private RedisUtil redisUtil;
    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

    public Boolean paramsEmpty(OAET003SearchDto params) {
        if (params.getDeclarationDate1() == null &&
                params.getDeclarationDate2() == null &&
                params.getPermissionDate1() == null &&
                params.getPermissionDate2() == null &&
                params.getBondedCd().isEmpty() &&
                params.getShedCd().isEmpty() &&
                params.getAwbNo().isEmpty() &&
                params.getCwbNo().isEmpty() &&
                params.getFlt1().isEmpty() &&
                params.getFlt2().isEmpty() &&
                params.getReferenceNo().isEmpty() &&
                params.getItem().isEmpty() &&
                params.getExporterName().isEmpty() &&
                params.getExporterAdd().isEmpty() &&
                params.getNews1().isEmpty() &&
                params.getNews2().isEmpty() &&
                params.getNewsShipper().isEmpty() &&
                params.getDepPort().isEmpty() &&
                params.getInputExpCustomerNo().isEmpty() &&
                params.getConsigneeName().isEmpty() &&
                params.getConsigneeAddLump().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    @GetMapping("/search")
    public List<OAET003SearchResultDao> getSearchList(OAET003SearchDto params) {

        // エラーになった場合のFrontendへの戻り値設定
        List checkErrorList = new ArrayList<>();
        checkErrorList.add("");
        checkErrorList.add("");

        // Frontendからの申告日入力値を確認
        try {
            // 検索条件に何も設定されていないか判定
            if (paramsEmpty(params) == true) {
                checkErrorList.set(0, "Error");
                checkErrorList.set(1, "検索条件が設定されておりません。検索条件を確認してください。");

                // 検索条件が完全に空ではない場合
            } else {
                if (params.getDeclarationDate1() != null && params.getDeclarationDate2() != null) {
                    Long declareDate1 = Long.parseLong(params.getDeclarationDate1().replace("-", ""));
                    Long declareDate2 = Long.parseLong(params.getDeclarationDate2().replace("-", ""));
                    if (declareDate1 > declareDate2) { // 申告日(FROM)より前の日付が申告日(TO)として設定されている場合
                        checkErrorList.set(0, "Error");
                        checkErrorList.set(1, "申告日を確認してください。申告日(FROM)より前の日付が申告日(TO)として設定されております。");
                    }
                }
                if (params.getPermissionDate1() != null && params.getPermissionDate2() != null) {
                    Long permitDate1 = Long.parseLong(params.getPermissionDate1().replace("-", ""));
                    Long permitDate2 = Long.parseLong(params.getPermissionDate2().replace("-", ""));
                    if (permitDate1 > permitDate2) { // 許可日(FROM)より前の日付が許可日(TO)として設定されている場合
                        checkErrorList.set(0, "Error");
                        checkErrorList.set(1, "許可日を確認してください。許可日(FROM)より前の日付が許可日(TO)として設定されております。");
                    }
                }
            }

            // 検索処理
            logger.info("変数 : " + params.toString());
            if ("Error".equals(checkErrorList.get(0).toString())) { // 上記の処理でエラー用戻り値が設定されている場合
                return checkErrorList;
            } else { // 上記の処理でエラーがない場合、DB検索をするため、Serviceにparams情報を渡す
                List<OAET003SearchResultDao> returnVal = screenServ.searchResultList(params);
                if (returnVal.size() == 0) { // SQL検索して件数が0件な場合
                    checkErrorList.set(0, "Error");
                    checkErrorList.set(1, "検索結果が0件です。");
                    return checkErrorList;
                } else {
                    return returnVal;
                }
            }
        } catch (Exception e) { // データ検索時にエラーが起きた場合
            // エラー用戻り値の設定
            checkErrorList.set(0,"Error");
            checkErrorList.set(1,"システムエラーです。管理者にお問い合わせください。" + e.toString());
            logger.debug(e.toString());
            return checkErrorList;
        }
    }


    @GetMapping("/searchPrint")
    public List<OAET003SearchPrintResultDao> getPrintList(OAET003SearchPrintDto param) {
        // エラーになった場合のFrontendへの戻り値設定
        List checkErrorList = new ArrayList<>();
        checkErrorList.add("");
        checkErrorList.add("");
        try {
            List<OAET003SearchPrintResultDao> returnVal = screenServ.searchPrintResultList(param);
            return returnVal;
        } catch (Exception e) {
            checkErrorList.set(0, "Error");
            checkErrorList.set(1, "システムエラーです。管理者にお問い合わせください。" + e.toString());
            return checkErrorList;
        }
    }
}