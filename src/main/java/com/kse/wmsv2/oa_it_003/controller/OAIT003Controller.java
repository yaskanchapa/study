package com.kse.wmsv2.oa_it_003.controller;

import com.kse.wmsv2.oa_it_003.dao.OAIT003PrintDao;
import com.kse.wmsv2.oa_it_003.dao.OAIT003SearchResultDao;
import com.kse.wmsv2.oa_it_003.dao.OAIT003InHouseListDao;
import com.kse.wmsv2.oa_it_003.dto.OAIT003PrintDto;
import com.kse.wmsv2.oa_it_003.dto.OAIT003SearchDto;
import com.kse.wmsv2.oa_it_003.service.OAIT003PrintService;
import com.kse.wmsv2.oa_it_003.service.OAIT003ScreenService;

import com.kse.wmsv2.oc_cs_006.dto.OCCS006ResultDto;
import com.kse.wmsv2.oc_cs_006.service.OCCS006PermitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/oait003")
public class OAIT003Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OAIT003ScreenService screenServ;
    @Autowired
    OCCS006PermitService occs006PermitService;
    @Autowired
    OAIT003PrintService printServ;

    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");

    @GetMapping("/inHouseList")
    public List<OAIT003InHouseListDao> inHouseList(){
        List<OAIT003InHouseListDao> returnVal = screenServ.inHouseList();
        return returnVal;
    }

    // 検索条件に何も設定されていないか判定するためのメソッド
    public Boolean paramsEmpty(OAIT003SearchDto params) {
        if (params.getDeclareDate1() == null &&
                params.getDeclareDate2() == null &&
                params.getPermitDate1() == null &&
                params.getPermitDate2() == null &&
                params.getCustomsCode().isEmpty() &&
                params.getClearPlaceCode().isEmpty() &&
                params.getMawbNo().isEmpty() &&
                params.getHawbNo().isEmpty() &&
                params.getFlt1().isEmpty() &&
                params.getFlt2().isEmpty() &&
                params.getCompanyName().isEmpty() &&
                params.getImportCustomerName().isEmpty() &&
                params.getConsignorName().isEmpty() &&
                params.getItemName().isEmpty() &&
                params.getItemNo().isEmpty() &&
                params.getNaccsCode().isEmpty() &&
                params.getConsignorCountryName().isEmpty() &&
                params.getConsignCertifiDisc().isEmpty() &&
                params.getImportCustomerAdd().isEmpty() &&
                params.getExportCustomerAdd().isEmpty() &&
                params.getInHouseNo().isEmpty() &&
                params.getInHouseCd().isEmpty() &&
                params.getUnloadPlace().isEmpty() &&
                params.getConsignorAdd().isEmpty() &&
                params.getClearPlaceName().isEmpty() &&
                params.getNews1().isEmpty() &&
                params.getNews2().isEmpty()) {
            return true;
        } else {
            return false;
        }
    }
    @GetMapping("/search")
    public List<OAIT003SearchResultDao> getSearchList(OAIT003SearchDto params) {

        // エラーになった場合のFrontendへの戻り値設定
        List checkErrorList = new ArrayList<>();
        checkErrorList.add("");
        checkErrorList.add("");

        try {
            // 検索条件に何も設定されていないか判定
            if (paramsEmpty(params) == true) {
                checkErrorList.set(0, "Error");
                checkErrorList.set(1, "検索条件が設定されておりません。検索条件を確認してください。");

            // 検索条件が完全に空ではない場合
            } else { // 申告日・許可日入力確認
                if (params.getDeclareDate1() != null && params.getDeclareDate2() != null) {
                    Long declareDate1 = Long.parseLong(params.getDeclareDate1().replace("-", ""));
                    Long declareDate2 = Long.parseLong(params.getDeclareDate2().replace("-", ""));
                    if (declareDate1 > declareDate2) { // 申告日(FROM)より前の日付が申告日(TO)として設定されている場合
                        checkErrorList.set(0, "Error");
                        checkErrorList.set(1, "申告日を確認してください。申告日(FROM)より前の日付が申告日(TO)として設定されております。");
                    }
                }
                if (params.getPermitDate1() != null && params.getPermitDate2() != null) {
                    Long permitDate1 = Long.parseLong(params.getPermitDate1().replace("-", ""));
                    Long permitDate2 = Long.parseLong(params.getPermitDate2().replace("-", ""));
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
                List<OAIT003SearchResultDao> returnVal = screenServ.searchResultList(params);
                if (returnVal.size() == 0) { // SQL検索して件数が0件な場合
                    checkErrorList.set(0,"Error");
                    checkErrorList.set(1,"検索結果が0件です。");
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

    @GetMapping("/printList")
    public List<OAIT003PrintDao> printType(OAIT003PrintDto params) {
        logger.info(params.toString());
        List<OAIT003PrintDao> returnVal = printServ.printType(params);
        return returnVal;
    }

    @GetMapping("/printListMultiple")
    public List<OAIT003PrintDao> printTypeMultiple() {
        logger.info("Multiple");
        return printServ.printTypeMultiple();
    }
}
