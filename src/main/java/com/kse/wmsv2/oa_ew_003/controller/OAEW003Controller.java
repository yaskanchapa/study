package com.kse.wmsv2.oa_ew_003.controller;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.oa_ew_003.dao.*;
import com.kse.wmsv2.oa_ew_003.dto.*;

import com.kse.wmsv2.oa_ew_003.service.OAEW003ScreenService;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

//Test
@RestController
@RequestMapping("/api/oaew003")
public class OAEW003Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    OAEW003ScreenService screenServ;

    @Autowired
    StatusService stsServ;

    @GetMapping("/searchPort")
    public OAEW003portSearchResultDto searchPort(OAEW003portSearchDto params) {
        OAEW003portSearchResultDto result = new OAEW003portSearchResultDto();
        List<OAEW003portSearchResultListDto> resultDtoList = new ArrayList<OAEW003portSearchResultListDto>(); //データ保持用
        try {
            List<OAEW003portSearchResultDao> returnVal = screenServ.searchPort(params);
            if (returnVal == null || returnVal.size() == 0) {// 積込港の情報がない場合
                result.setResult(false);
                result.setMessage("貨物確認処理でエラーが発生しました。");
                result.setErrorMessage("選択できる港がありません。");
                return result;
            } else {
                for (OAEW003portSearchResultDao resultRow : returnVal) {
                    OAEW003portSearchResultListDto searchResultRow = new OAEW003portSearchResultListDto();
                    searchResultRow.setNameCd(resultRow.getNameCd());
                    resultDtoList.add(searchResultRow);
                    result.setResultData(resultDtoList);
                }
                result.setResult(true);
            }
        } catch (Exception e) {
            String errMsg = "検索処理中にエラーが発生しました。";
            logger.error(errMsg + e.getMessage());
            result.setResult(false);
            result.setMessage(errMsg);
        } finally {
            return result;
        }
    }

    @GetMapping("/searchFlight")
    public OAEW003flightSearchResultDto searchFlight(OAEW003flightSearchDto params) {

        OAEW003flightSearchResultDto result = new OAEW003flightSearchResultDto();
        List<OAEW003flightSearchResultListDto> resultDtoList = new ArrayList<OAEW003flightSearchResultListDto>(); //データ保持用
        try {
            List<OAEW003flightSearchResultDao> returnVal = screenServ.searchFlight(params);
            if (returnVal == null || returnVal.size() == 0) {// 出発便の情報がない場合
                result.setResult(false);
                result.setMessage("貨物確認処理でエラーが発生しました。");
                result.setErrorMessage("選択できる便がありません。");
                return result;
            } else {
                for (OAEW003flightSearchResultDao resultRow : returnVal) {
                    OAEW003flightSearchResultListDto searchResultRow = new OAEW003flightSearchResultListDto();
                   // searchResultRow.setShuttleNo(resultRow.getShuttleNo());
                    searchResultRow.setLinkTruckNo(resultRow.getLinkTruckNo());
                    resultDtoList.add(searchResultRow);
                    result.setResultData(resultDtoList);
                }
                result.setResult(true);
            }
        } catch (Exception e) {
            String errMsg = "出発便検索処理中にエラーが発生しました。";
            logger.error(errMsg + e.getMessage());
            result.setResult(false);
            result.setMessage(errMsg);
        } finally {
            return result;
        }
    }

    @GetMapping("/searchContainer")
    public OAEW003containerSearchResultDto searchContainer(OAEW003containerSearchDto params) {
        OAEW003containerSearchResultDto result = new OAEW003containerSearchResultDto();
        List<OAEW003containerSearchResultListDto> resultDtoList = new ArrayList<OAEW003containerSearchResultListDto>(); //データ保持用
        try {
            List<OAEW003containerSearchResultDao> returnVal = screenServ.searchContainer(params);

            if (returnVal == null || returnVal.size() == 0) {// コンテナの情報がない場合
                result.setResult(false);
                result.setMessage("貨物確認処理でエラーが発生しました。");
                result.setErrorMessage("選択できるコンテナがありません。");
                return result;
            } else {
                for (OAEW003containerSearchResultDao resultRow : returnVal) {
                    OAEW003containerSearchResultListDto searchResultRow = new OAEW003containerSearchResultListDto();
                    searchResultRow.setUldNo(resultRow.getUldNo());
                    resultDtoList.add(searchResultRow);
                    result.setResultData(resultDtoList);
                }
                result.setResult(true);
            }
        } catch (Exception e) {
            String errMsg = "検索処理中にエラーが発生しました。";
            logger.error(errMsg + e.getMessage());
            result.setResult(false);
            result.setMessage(errMsg);
        } finally {
            return result;
        }
    }

    @RequestMapping("/searchWarehouse")
    public OAEW003SearchWarehouseResultDto searchWarehouse(OAEW003SearchWarehouseDto params) {
        OAEW003SearchWarehouseResultDto result = new OAEW003SearchWarehouseResultDto();
        List<OAEW003SearchWarehouseResultListDto> resultDtoList = new ArrayList<OAEW003SearchWarehouseResultListDto>(); //データ保持用
        try {
            List<OAEW003SearchWarehouseDao> returnVal = screenServ.searchWarehouse(params);
            if (returnVal == null || returnVal.size() == 0) {// 端末の情報がない場合
                result.setResult(false);
                result.setMessage("未許可の端末です");
                return result;
            } else {
                for (OAEW003SearchWarehouseDao resultRow : returnVal) {
                    OAEW003SearchWarehouseResultListDto searchResultRow = new OAEW003SearchWarehouseResultListDto();
                    searchResultRow.setRegUserCd(resultRow.getRegUserCd());
                    searchResultRow.setBondedWarehouseCd(resultRow.getBondedWarehouseCd());
                    result.setMessage("端末確認OK");
                    resultDtoList.add(searchResultRow);
                    result.setResultData(resultDtoList);
                }
                result.setResult(true);
            }
        } catch (Exception e) {
            String errMsg = "ユーザコード確認中にエラーが発生しました。";
            logger.error(errMsg + e.getMessage());
            result.setResult(false);
            result.setMessage(errMsg);
        } finally {
            return result;
        }
    }

    @GetMapping("/searchExamination")
    public OAEW003examinationSearchDto searchExamination(@Validated OAEW003examinationSearchDto params, BindingResult dto) {
        OAEW003examinationSearchDto result = new OAEW003examinationSearchDto();
        try {
          /*  if (dto.hasErrors()) {
                result.setResult(false);
                result.setMessage("バーコードの入力値が不正です。");
                result.setErrorMessage("バーコードを確認してください。");
                return result;
            } */
            OAEW003examinationSearchResultDao returnVal = screenServ.searchExamination(params);
            result.setResult(true);
            if (returnVal == null) { // 貨物情報がない場合
                result.setResult(false);
                result.setMessage("貨物情報無し");
                result.setErrorMessage("貨物情報がありません。");
                return result;
            } else {
                result.setAwbNo(returnVal.getAwbNo());
                result.setPortName(returnVal.getDepPort());
                result.setLinkPiece(returnVal.getLinkPiece());
                // 追加　積込港が一致しているか確認
                if (!(params.getPortName()).equals(returnVal.getDepPort().toString())) {
                    result.setResult(false);
                    result.setMessage("積込港間違い");
                    result.setErrorMessage("貨物情報がありません。");
                    return result;
                } else if ("EB00200".equals(returnVal.getCurrentCargoStatusCd())) {
                    result.setResult(false);
                    result.setMessage("対査済み");
                    result.setErrorMessage("確認してください。");
                    return result;
                } else if(!("EB00100".equals(returnVal.getCurrentCargoStatusCd()))){
                    result.setResult(false);
                    result.setMessage("対査対象貨物ではありません");
                    result.setErrorMessage("確認してください。");
                    return result;
                }
                // LinkPieceが1の場合デフォルトで1を設定する
                else if ("1".equals(returnVal.getLinkPiece())) {
                    result.setLinkPiece(returnVal.getLinkPiece());
                    result.setDefaultPiece(returnVal.getLinkPiece());
                    return result;
                }
            }
        } catch (Exception e) {
            String Msg = "貨物確認処理でエラーが発生しました。";
            logger.error(Msg + e.getMessage());
            result.setResult(false);
            result.setMessage(Msg);
        } finally {
            return result;
        }
    }

    @GetMapping("/updateExamination")
    public OAEW003examinationUpdateDto updateExamination(OAEW003examinationUpdateDto params) {
        OAEW003examinationUpdateDto result = new OAEW003examinationUpdateDto();
        if (!(params.getPiece()).equals(params.getLinkPiece())) {
            result.setResult(false);
            result.setMessage("個数間違い");
            return result;
        }
        try {
            screenServ.updateExamination(params);// AE_DATEテーブル更新
            //共通部品へ送るステータス（AE_STATUS、AE_STATUS_HISTORY更新用）

            result.setResult(true);
        } catch (Exception e) {
            String Msg = "更新処理でエラーが発生しました。";
            result.setResult(false);
            result.setMessage(Msg);
            result.setErrorMessage(e.getMessage());
            return result;
        } finally {
            return result;
        }
    }

    @GetMapping("/updateStowage")
    public OAEW003stowageSearchDto updateStowage(@Validated OAEW003stowageSearchDto params, BindingResult dto, OAEW003stowageUpdateDto param) {
        OAEW003stowageSearchDto result = new OAEW003stowageSearchDto();
        try {
          /*  if (dto.hasErrors()) {
                result.setResult(false);
                result.setMessage("バーコードの入力値が不正です。");
                result.setErrorMessage("バーコードを確認してください。");
                return result;
            } */
            OAEW003stowageSearchResultDao returnVal = screenServ.searchStowage(params);
            result.setResult(true);

            if (returnVal == null) { // 貨物情報がない場合
                result.setResult(false);
                result.setMessage("貨物情報無し");
                return result;
            } else {
                String containerClassCd = returnVal.getContainerClassCd();
                param.setContainerClassCd(containerClassCd);
                if (returnVal != null) {
                    String currentCargoStatusCd = returnVal.getCurrentCargoStatusCd();
                    String currentCustomsStatusCd = returnVal.getCurrentCustomsStatusCd();
                    if (!(params.getContainerName()).equals(returnVal.getUldNo().toString())) {
                        result.setResult(false);
                        result.setMessage("コンテナ間違い");
                        result.setErrorMessage("コンテナ間違い");
                        return result;
                    } else if ("EB00500".equals(currentCargoStatusCd)) {// 貨物がすでに登録済みの場合
                        result.setResult(false);
                        result.setMessage("積付済み");
                        result.setErrorMessage("貨物情報が登録済です。");
                        return result;
                    } else if ("EC00900".equals(currentCargoStatusCd)) {// 貨物のステータスが未許可の場合 参照先が正しいかは要確認
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EC01100".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EC01210".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EC01220".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EC01310".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EC01320".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("未許可の貨物");
                        result.setErrorMessage("貨物のステータスが未許可です。");
                        return result;
                    } else if ("EB00100".equals(currentCargoStatusCd)) {
                        result.setResult(false);
                        result.setMessage("対査確認を行っていない貨物です。");
                        result.setErrorMessage("対査確認を行っていない貨物です。");
                        return result;
                    } else {
                        try {
                            // serviceへ
                            param.setAwbNo(returnVal.getAwbNo());
                            screenServ.updateStowage(param,params);
                            //共通部品へ送るステータス（AE_STATUS、AE_STATUS_HISTORY更新用）

                            result.setResult(true);
                        } catch (Exception e) {
                            String Msg = "更新処理でエラーが発生しました。";
                            logger.error(Msg + e.getMessage());
                            result.setResult(false);
                            result.setMessage(Msg);
                            return result;
                        }
                    }
                }
            }
        } catch (Exception e) {
            String Msg = "貨物確認処理でエラーが発生しました。";
            logger.error(Msg + e.getMessage());
            result.setResult(false);
            result.setMessage(Msg);
            return result;
        } finally {
            return result;
        }
    }
}
