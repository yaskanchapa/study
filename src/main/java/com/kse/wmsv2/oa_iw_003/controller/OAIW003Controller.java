package com.kse.wmsv2.oa_iw_003.controller;

import com.kse.wmsv2.oa_iw_003.dao.OAIW003SearchData2Dao;
import com.kse.wmsv2.oa_iw_003.dao.OAIW003SearchDataDao;
import com.kse.wmsv2.oa_iw_003.dao.OAIW003SearchResultDao;
import com.kse.wmsv2.oa_iw_003.dao.OAIW003SearchStatusDao;
import com.kse.wmsv2.oa_iw_003.dto.*;
import com.kse.wmsv2.oa_iw_003.service.OAIW003ScreenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/oaiw003")
public class OAIW003Controller {
    @Autowired
    OAIW003ScreenService screenServ;

    @PostMapping("/doSearch")
    public OAIW003DoSearchResultDto searchAiInventory(@RequestBody OAIW003SearchDataDao param) {
        OAIW003DoSearchResultDto result = new OAIW003DoSearchResultDto();
        try {

            /* AI_INVENTORYの対象データ有無チェック*/
            int searchCnt = screenServ.selectAiInventoryCnt(param);

            /* 対象データ件数が0の場合はエラー処理*/
            if(searchCnt < 1){

                String errMsg = "検索条件のデータが0件です。";
                result.setResult(false);
                result.setMessage("検索処理が完了しました。");
                result.setErrorMessage(errMsg);
                log.warn(errMsg);
                return result;

            } else {

                /* 検索処理開始*/
                List<OAIW003SearchResultDto> searchResultDtoList = screenServ.selectAiInventory(param);

                /* 検索結果をresultに設定*/
                result.setResult(true);
                result.setMessage("検索処理が完了しました。");
                result.setResultData(searchResultDtoList);

            }

        } catch (Exception e) {

            String errMsg = "検索処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage(e.getMessage());
            log.error(errMsg);

        } finally {

            return result;

        }
    }

    @PostMapping("/doSearch2")
    public OAIW003SearchMawResultDto searchMawb(@RequestBody OAIW003SearchData2Dao param) {
        OAIW003SearchMawResultDto result = new OAIW003SearchMawResultDto();

        try {

            /* 検索処理開始*/
            List<OAIW003SearchResult2Dto> searchResultDtoList = screenServ.searchMawb(param);

            /* 検索結果をresultに設定*/
            result.setResult(true);
            result.setMessage("検索処理が完了しました。");
            result.setResultData(searchResultDtoList);

        } catch (Exception e) {

            String errMsg = "検索処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage(e.getMessage());
            log.error(errMsg);

        } finally {

            return result;

        }
    }

    @PostMapping("/Instraction")
    public OAIW003DoSearchResultDto insertAiInventory(@RequestHeader HttpHeaders header, @RequestBody List<OAIW003SearchData2Dao> params) {

        String accessToken = (String)header.get("authorization").get(0);
        OAIW003DoSearchResultDto result = new OAIW003DoSearchResultDto();

        try {

            /* 入力値チェック*/
            for(OAIW003SearchData2Dao param : params){

                int searchCnt = screenServ.searchAiInventory(param);
                if(searchCnt > 0){
                    String errMsg = "重複データが存在しております。";
                    result.setResult(false);
                    result.setMessage(errMsg);
                    log.warn(errMsg);
                    return result;
                }

            }

            /* 更新処理開始*/
            List<OAIW003SearchResultDto> searchResultDtoList = screenServ.insertMawb(params, accessToken);
            if(searchResultDtoList.size()<1){
                String errMsg = "対象データが見当たりませんでした。";
                result.setResult(false);
                result.setErrorMessage(errMsg);
                result.setResultData(searchResultDtoList);
                log.warn(errMsg);
                return result;
            }

            /* 処理結果をresultに設定*/
            result.setResult(true);
            result.setMessage("指示処理が完了しました。");
            result.setResultData(searchResultDtoList);

        } catch (Exception e) {

            String errMsg = "指示処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage(e.getMessage());
            log.error(errMsg);

        } finally {

            return result;

        }
    }

    @PostMapping("/end")
    public OAIW003DoSearchResultDto end(@RequestHeader HttpHeaders header, @RequestBody OAIW003SearchResultDao params) {

        String accessToken = (String)header.get("authorization").get(0);
        OAIW003DoSearchResultDto result = new OAIW003DoSearchResultDto();

        try {

            /* 終了ボタン処理開始*/
            screenServ.end(params, accessToken);

            /* 検索結果をresultに設定*/
            result.setResult(true);
            result.setMessage("終了処理が完了しました。");

        } catch (Exception e) {

            String errMsg = "終了処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage(e.getMessage());
            log.error(errMsg);

        } finally {

            return result;

        }
    }

    @PostMapping("/searchStatus")
    public OAIW003SearchStatusResultDto searchStatus(@RequestBody OAIW003SearchStatusDao params) {

        OAIW003SearchStatusResultDto result = new OAIW003SearchStatusResultDto();

        try {
            /* 対象データ件数チェック*/
            int searchCnt = screenServ.searchStatusCnt(params);
            if(searchCnt < 1){
                String errMsg = "対象データが見当たりませんでした。";
                result.setResult(false);
                result.setMessage(errMsg);
                log.warn(errMsg);
                return result;
            }

            /* 対象データ検索*/
            List<OAIW003SearchStatusDto> searchResultDtoList =  screenServ.searchStatus(params);

            /* 検索結果をresultに設定*/
            result.setResult(true);
            result.setMessage("履歴照会処理が完了しました。");
            result.setResultData(searchResultDtoList);

        } catch (Exception e) {

            String errMsg = "履歴照会処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage(e.getMessage());
            log.error(errMsg);

        } finally {

            return result;

        }
    }
}
