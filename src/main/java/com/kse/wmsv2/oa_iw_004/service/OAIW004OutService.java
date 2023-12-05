package com.kse.wmsv2.oa_iw_004.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_iw_004.dao.*;
import com.kse.wmsv2.oa_iw_004.dto.OAIW004SearchDto;
import com.kse.wmsv2.oa_iw_004.mapper.OAIW004ScreenMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OAIW004OutService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OAIW004ScreenMapper oaiw004ScreenMapper;

    @Autowired
    OAIW004CommonService commonService;

    @Autowired
    StatusService statusService;

    // 搬出可能なマスター検索
    public List<OAIW004OutSearchMawbDao> outSearchMawb(OAIW004SearchDto params) {
        logger.info(params.toString());
        return oaiw004ScreenMapper.outSearchMawb(params);
    }

    // 搬出スキャン処理
    @Transactional
    public OAIW004OutReturnValDao outSearch(OAIW004SearchDto params) {

        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        // Refactoring
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo());
            }
        }

        // HTに値を返すための変数
        OAIW004OutReturnValDao returnVal = new OAIW004OutReturnValDao();
        returnVal.setAWBNO(params.getMawbNo());
        returnVal.setCWBNO(params.getHawbNo());

        // 入力値が空白か確認するための変数
        String mawbInputCheck = params.getMawbNo().toString().replaceAll(" ","");
        String hawbInputCheck = params.getHawbNo().toString().replaceAll(" ", "");

        // HT端末ID確認のための変数
        String terminalIdCheck = params.getTermNo().toString().replaceAll(" ","");
        OAIW004SearchWarehouseDao terminalAuthCheck = oaiw004ScreenMapper.searchWarehouse(params);

        // 通関・保税コード判定のための変数設定
        String bondedStatusCD = ""; // 保税ステータスコード
        String customStatusCD = ""; // 通関ステータスコード
        String overFlagIn = ""; // オーバーフラグ(搬入時)
        String overFlagOut = ""; // オーバーフラグ(搬出時)


        // 重複チェックのための変数設定
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new java.util.Date();

        // 端末ID確認
        if ("".equals(terminalIdCheck)) {
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setSCANPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID無し");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");
        } else if (!("".equals(terminalIdCheck)) && terminalAuthCheck == null) {
            returnVal.setSCANPICEC("0");
            returnVal.setSCANPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID未許可");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");

            // マスター番号・ハウス番号の入力確認
        } else if ("".equals(mawbInputCheck)) { // マスター番号が空白か確認
            logger.info("MAWB無し");
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setSCANPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("マスター番号確認");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");
        } else if ("".equals(hawbInputCheck)) { // ハウス番号が空白か確認
            logger.info("HAWB無し");
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setSCANPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("ハウス番号確認");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");

        } else { // Timeout, 入力値確認、HT端末に問題ない場合続行

            try {

                // 搬出データを検索するSQL
                OAIW004OutResultDao searchResult = oaiw004ScreenMapper.outSearch(params);

                if (searchResult != null) { // 搬出データ検索SQLでデータが存在する場合

                    logger.info("searchResult : ");
                    logger.info(searchResult.toString());

                    bondedStatusCD = searchResult.getBONDEDSTATUSCD();
                    customStatusCD = searchResult.getCUSTOMSTATUSCD();
                    overFlagIn = searchResult.getOVERFLGIN();
                    overFlagOut = searchResult.getOVERFLGOUT();

                    List<String> mikyokaList = Arrays.asList("IC00100", "IC00200", "IC00300", "IC00301", "IC00310", "IC00401", "IC00402", "IC00500", "IC00600", "IC00700", "IC00800", "IC00900", "IC00900", "IC01000", "IC01011", "IC01012", "IC01013", "IC01014", "IC01021", "IC01022", "IC01023", "IC01024", "IC01031", "IC01032", "IC01033", "IC01034", "IC01041");
                    List<String> kensaList = Arrays.asList("IC00010", "IC01100");
                    List<String> mishinkokuList = Arrays.asList("IC00000","IC90010","IC90020");
                    List<String> outCheckList = Arrays.asList("IC01200", "IC01211", "IC01211", "IC01221", "IC01221", "IC01230", "IC01231", "IC01231", "IC01241");

                    logger.info("bondedStatusCD : "+bondedStatusCD);
                    logger.info("customStatusCD : "+customStatusCD);
                    logger.info("未許可コード一覧 : "+mikyokaList.toString());
                    logger.info("検査コード一覧 : "+kensaList.toString());
                    logger.info("未申告コード一覧 : "+mishinkokuList.toString());
                    logger.info("搬出確認コード一覧 : "+outCheckList.toString());

                    long scanPiece = Long.parseLong(searchResult.getSCANPICEC());
                    long dataPiece = Long.parseLong(searchResult.getDATAPICEC());

                    if (scanPiece < dataPiece) {
                        returnVal.setSCANPICEC(String.valueOf(scanPiece + 1));
                    } else {
                        searchResult.setSCANPICEC(String.valueOf(scanPiece));
                    }
                    returnVal.setSCANPICEC(String.valueOf(dataPiece));
                    if ("2".equals(overFlagIn) || "2".equals(overFlagOut)) {
                        logger.info("オーバー");
                        returnVal.setSCANRESULT("オーバー");
                        returnVal.setAUDIOPATH("/Audio/Over.mp3");
                    } else if (mikyokaList.contains(customStatusCD)) {
                        logger.info("未許可");
                        returnVal.setSCANRESULT("未許可");
                        returnVal.setAUDIOPATH("/Audio/mikyoka.mp3");
                    } else if (kensaList.contains(customStatusCD)) {
                        logger.info("検査");
                        returnVal.setSCANRESULT("検査");
                        returnVal.setAUDIOPATH("/Audio/kensa.mp3");
                    } else if ("IB00000".equals(bondedStatusCD) || bondedStatusCD == null || "0".equals(overFlagIn)) {
                        logger.info("未搬入");
                        returnVal.setSCANRESULT("未搬入");
                        returnVal.setAUDIOPATH("/Audio/mihannyu.mp3");
                    } else if (mishinkokuList.contains(customStatusCD) || customStatusCD == null) {
                        logger.info("未申告");
                        returnVal.setSCANRESULT("未申告");
                        returnVal.setAUDIOPATH("/Audio/misinkoku.mp3");
                    } else if ("1".equals(overFlagOut) || "IB00720".equals(bondedStatusCD) || scanPiece == dataPiece || "IB00730".equals(bondedStatusCD)) {
                        logger.info("出荷済み");
                        returnVal.setSCANRESULT("出荷済み");
                        returnVal.setAUDIOPATH("/Audio/syukkazumi.mp3");

                    } else if (outCheckList.contains(customStatusCD)) {

                        try { // 更新・削除SQL処理途中エラーが起きた場合の例外処理するためのTry
                            if (!"".equals(params.getInputScanPicec())) { // 複数個口貨物の場合
                                long inputScanPicec = Long.parseLong(params.getInputScanPicec());
                                if (inputScanPicec > dataPiece) {
                                    logger.info("貨物数入力確認");
                                    returnVal.setSCANRESULT("貨物数入力確認");
                                    returnVal.setAUDIOPATH("/Audio/Error.mp3");
                                } else {
                                    int sqlUpdateCount0 = oaiw004ScreenMapper.outUpdateScanpiece(params);
                                    if (sqlUpdateCount0 != 1) { // 正常に1件更新されたかチェック
                                        throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount0));
                                    }
                                    returnVal.setSCANPICEC(String.valueOf(inputScanPicec));
                                }
                            } else {
                                params.setInputScanPicec("1");
                                int sqlUpdateCount0 = oaiw004ScreenMapper.outUpdateScanpiece(params);
                                if (sqlUpdateCount0 != 1) { // 正常に1件更新されたかチェック
                                    throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount0));
                                }
                            }
                            int sqlUpdateCount2 = oaiw004ScreenMapper.outUpdateOverFlg(params);
                            if (sqlUpdateCount2 != 1) { // 正常に2件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount2));
                            }
                            int sqlUpdateCount3 = oaiw004ScreenMapper.outUpdateStatus(params);
                            if (sqlUpdateCount3 != 1) { // 正常に2件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount3));
                            }
                            int sqlUpdateCount4 = oaiw004ScreenMapper.outUpdateAiData(params);
                            if (sqlUpdateCount4 != 1) { // 正常に2件更新されたかチェック
                                throw new Exception("正常な更新件数(2件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount4));
                            }

                            try {

                                COMMONStatusDto commonStatusDto = new COMMONStatusDto();

                                commonStatusDto.setBusinessClass("01");
                                commonStatusDto.setAwbNo(params.getMawbNo());
                                commonStatusDto.setCwbNo(params.getHawbNo());
                                commonStatusDto.setCwbNoDeptCd("000");
                                commonStatusDto.setBondStatusCd("IB00720");
                                commonStatusDto.setHandyTerminalId(params.getTermNo());
                                commonStatusDto.setUserCd(terminalAuthCheck.getREGUSERCD());
                                commonStatusDto.setDate(sf.format(currentDate));

                                statusService.insertStatusMaster(commonStatusDto);

                            } catch (Exception e) {
                                throw new Exception("ステータス履歴の追加に失敗しました。");
                            }

                            int sqlUpdateCount6 = oaiw004ScreenMapper.outUpdateHead(params);
                            if (sqlUpdateCount6 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount6));
                            }
                            logger.info("搬出OK");
                            returnVal.setSCANRESULT("搬出確認");
                            returnVal.setAUDIOPATH("/Audio/Ok.mp3");
                        } catch (Exception e) { // Statusアップデート・削除時にSQLエラーあった場合
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
                            logger.error(e.toString());
                            returnVal.setSCANRESULT("エラー");
                            returnVal.setAUDIOPATH("/Audio/Error.mp3");
                        }
                    } else { // その他(エラー)
                        logger.info("エラー");
                        returnVal.setSCANRESULT("エラー");
                        returnVal.setAUDIOPATH("/Audio/Error.mp3");
                    }
                    logger.info(returnVal.toString());

                } else {
                    // マスター・ハウス両方のキーワードで搬出データ検索SQLでデータが存在しない
                    // ハウス番号だけでも検索できるか確認
                    params.setMawbNo("");
                    searchResult = oaiw004ScreenMapper.outSearch(params);
                    if (searchResult == null) {
                        // ハウス番号だけでも検索できない
                        logger.info("未搬入");
                        returnVal.setSCANPICEC("0");
                        returnVal.setSCANPICEC("0");
                        returnVal.setSCANRESULT("未搬入");
                        returnVal.setAUDIOPATH("/Audio/mihannyu.mp3");
                    } else if (!(searchResult.getAWBNO().toString()).equals(params.getMawbNo().toString())) {
                        // ハウス番号だけでは検索できるが、返されたマスター番号がHTに入力したマスター番号ではない(フライト違い)
                        logger.info(searchResult.getAWBNO().toString());
                        logger.info(params.getMawbNo().toString());
                        logger.info("フライト違い");
                        returnVal.setSCANPICEC("0");
                        returnVal.setSCANPICEC("0");
                        returnVal.setSCANRESULT("フライト違い");
                        returnVal.setAUDIOPATH("/Audio/Flight.mp3");
                    }
                }

            } catch (Exception e) { // SQLエラー
                logger.error(e.toString());
                returnVal.setSCANPICEC("0");
                returnVal.setSCANPICEC("0");
                returnVal.setSCANRESULT("エラー");
                returnVal.setAUDIOPATH("/Audio/Error.mp3");
            } finally {
                logger.info(returnVal.toString());
            }
        }
        return returnVal;
    }

    // 搬入件数取得
    public OAIW004OutCountDao outSearchCount(OAIW004SearchDto params) {
        return oaiw004ScreenMapper.outSearchCount(params);
    }

    // 複数個口の貨物個数の入力処理
    public OAIW004OutDataPicecDao outCheckDataPicec(OAIW004SearchDto params) {
        OAIW004OutDataPicecDao returnVal = new OAIW004OutDataPicecDao();

        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        // Refactoring
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo());
            }
        }

        OAIW004OutResultDao searchResult = oaiw004ScreenMapper.outSearch(params);

        try {
            if (searchResult != null) {
                logger.info("searchResult not null");
                var dataPicec = Long.parseLong(searchResult.getDATAPICEC());
                String overFlg = searchResult.getOVERFLGOUT();
                String awbNo = searchResult.getAWBNO();
                returnVal.setDATAPICEC(String.valueOf(dataPicec));
                returnVal.setOVERFLG(overFlg);
                returnVal.setAWBNO(awbNo);
            } else {
                returnVal.setDATAPICEC("0");
                returnVal.setOVERFLG("0");
                returnVal.setAWBNO("0");
            }
        } catch (Exception e) {
            logger.error(e.toString());
            returnVal.setDATAPICEC("0");
            returnVal.setOVERFLG("0");
            returnVal.setAWBNO("0");
        }
        return returnVal;
    }

}
