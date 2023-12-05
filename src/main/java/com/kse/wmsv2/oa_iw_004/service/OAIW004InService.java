package com.kse.wmsv2.oa_iw_004.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_iw_004.dao.*;
import com.kse.wmsv2.oa_iw_004.dto.*;
import com.kse.wmsv2.oa_iw_004.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OAIW004InService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    long workTime = 0; // Timeout確認用変数

    @Autowired
    OAIW004ScreenMapper oaiw004ScreenMapper;

    @Autowired
    OAIW004CommonService commonService;

    @Autowired
    StatusService statusService;

    // 搬入可能なマスター検索
    public List<OAIW004InSearchMawbDao> inSearchMawb(OAIW004SearchDto params) {
        logger.info(params.toString());
        return oaiw004ScreenMapper.inSearchMawb(params);
    }

    // 搬入スキャン処理
    @Transactional
    public OAIW004InReturnValDao inSearch(OAIW004SearchDto params) {

        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        // Refactoring
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo().toString());
            }
        }
        OAIW004InReturnValDao returnVal = new OAIW004InReturnValDao();

        // HT端末ID確認のための変数
        String terminalIdCheck = params.getTermNo().toString().replaceAll(" ","");
        OAIW004SearchWarehouseDao terminalAuthCheck = oaiw004ScreenMapper.searchWarehouse(params);

        // 入力値が空白か確認するための変数
        String mawbInputCheck = params.getMawbNo().toString().replaceAll(" ","");
        String hawbInputCheck = params.getHawbNo().toString().replaceAll(" ", "");

        // 重複・搬入済み・搬入確認判定のための変数設定
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new java.util.Date();

        if ("".equals(terminalIdCheck)) {
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID無し");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");
        } else if (!("".equals(terminalIdCheck)) && terminalAuthCheck == null) {
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID未許可");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");

            // マスター番号・ハウス番号の入力確認
        } else if ("".equals(mawbInputCheck)) { // マスター番号が空白か確認
            logger.info("MAWB無し");
            // 貨物個数として0を設定1
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("マスター番号確認");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");
        } else if ("".equals(hawbInputCheck)) { // ハウス番号が空白か確認
            logger.info("HAWB無し");
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("ハウス番号確認");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");

        } else { // Timeout, 入力値確認、HT端末に問題ない場合続行

            try {

                // マスター番号・ハウス番号をSQLで検索
                OAIW004InResultDao searchResult = oaiw004ScreenMapper.inSearch(params);

                // オーバー・フライト違い判定
                if (searchResult == null) {
                    // ハウス番号、マスター番号両方でSQL検索したらデータがない
                    params.setMawbNo(""); // ハウス番号だけても一旦検索
                    searchResult = oaiw004ScreenMapper.inSearch(params);
                    if (searchResult == null) {
                        // ハウス番号のみでも検索がヒットしない
                        logger.info("オーバー");
                        returnVal.setSCANRESULT("オーバー");
                        returnVal.setAUDIOPATH("/Audio/Over.mp3");
                        returnVal.setSCANPICEC("0");
                        returnVal.setDATAPICEC("0");
                    } else if (!(params.getMawbNo().toString()).equals(searchResult.getAWBNO().toString())) {
                        // ハウス番号のみのSQL検索でからデータは取得できるが、HTに入力されたマスター番号ではない
                        // 貨物個数として0を設定
                        returnVal.setSCANPICEC("0");
                        returnVal.setDATAPICEC("0");
                        logger.info("フライト違い");
                        returnVal.setSCANRESULT("フライト違い");
                        returnVal.setAUDIOPATH("/Audio/Flight.mp3");
                    }

                } else { // オーバー・フライト違いどちらでもない

                    // 貨物個数のための変数設定
                    long dataPiece = Long.parseLong(searchResult.getDATAPICEC());
                    long scanPiece = Long.parseLong(searchResult.getSCANPICEC());

                    if (scanPiece < dataPiece) {
                        returnVal.setSCANPICEC(String.valueOf(scanPiece + 1));
                    } else {
                        returnVal.setSCANPICEC(String.valueOf(scanPiece));
                    }
                    returnVal.setSCANPICEC(String.valueOf(dataPiece));

                    // 重複・搬入済み・搬入確認判定のための変数設定
                    String bondedStatusCD = searchResult.getBONDEDSTATUSCD();
                    String overFlg = searchResult.getOVERFLG();
                    String updDateString = searchResult.getUPDATEDATE();
                    long currentMin = TimeUnit.MILLISECONDS.toMinutes(currentDate.getTime());
                    long updMin = TimeUnit.MILLISECONDS.toMinutes(sf.parse(updDateString).getTime());

                    logger.info("scanPiece : " + String.valueOf(scanPiece));
                    logger.info("dataPiece : " + String.valueOf(dataPiece));

                    // 重複・搬入済み・搬入確認判定
                    if ((!"IB00120".equals(bondedStatusCD) && updMin >= currentMin) ||
                            "IB00130".equals(bondedStatusCD) || // BONDEDSTATUSが"IB00130"(搬入スキャン)
                            "IB00120".equals(bondedStatusCD) || // BONDEDSTATUSが"IB00120"(搬入スキャン) または OVERFLGが"1"(搬入確認)
                            "1".equals(overFlg) ||
                            scanPiece == dataPiece) {
                        logger.info("搬入済み");
                        returnVal.setSCANRESULT("搬入済み");
                        returnVal.setAUDIOPATH("/Audio/hannyuzumi.mp3");
                    } else if ("2".equals(overFlg)) {
                        // OVERFLGが"2"(オーバーに設定されている時)
                        logger.info("オーバー");
                        returnVal.setSCANRESULT("オーバー");
                        returnVal.setAUDIOPATH("/Audio/Over.mp3");

                    } else { // 上記のどれにも引っかからない場合(搬入OK)

                        try { // UPDATE & INSERTがあるので、Try&Catch及びTransaction処理を使う

                            logger.info("getInputScanPicec : " + params.getInputScanPicec());

                            if (!"".equals(params.getInputScanPicec())) { // 複数個口貨物の場合
                                long inputScanPicec = Long.parseLong(params.getInputScanPicec());
                                if (inputScanPicec > dataPiece) {
                                    logger.info("貨物数入力確認");
                                    returnVal.setSCANRESULT("貨物数入力確認");
                                    returnVal.setAUDIOPATH("/Audio/Error.mp3");
                                } else {
                                    int sqlUpdateCount0 = oaiw004ScreenMapper.inUpdateScanpiece(params);
                                    if (sqlUpdateCount0 != 1) { // 正常に1件更新されたかチェック
                                        throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount0));
                                    }
                                    returnVal.setSCANPICEC(params.getInputScanPicec());
                                }
                            } else {
                                params.setInputScanPicec("1");
                                int sqlUpdateCount0 = oaiw004ScreenMapper.inUpdateScanpiece(params);
                                if (sqlUpdateCount0 != 1) { // 正常に1件更新されたかチェック
                                    throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount0));
                                }
                            }

                            int sqlUpdateCount1 = oaiw004ScreenMapper.inUpdateOverFlg(params);
                            if (sqlUpdateCount1 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount1));
                            }
                            int sqlUpdateCount2 = oaiw004ScreenMapper.inUpdateStatus(params);
                            if (sqlUpdateCount2 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount2));
                            }
                            int sqlUpdateCount3 = oaiw004ScreenMapper.inUpdateAiData(params);
                            if (sqlUpdateCount3 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount3));
                            }

                            try {

                                COMMONStatusDto commonStatusDto = new COMMONStatusDto();

                                commonStatusDto.setBusinessClass("01");
                                commonStatusDto.setAwbNo(params.getMawbNo());
                                commonStatusDto.setCwbNo(params.getHawbNo());
                                commonStatusDto.setCwbNoDeptCd("000");
                                commonStatusDto.setBondStatusCd("IB00120");
                                commonStatusDto.setHandyTerminalId(params.getTermNo());
                                commonStatusDto.setUserCd(terminalAuthCheck.getREGUSERCD());
                                commonStatusDto.setDate(sf.format(currentDate));

                                logger.info("commonStatusDto : " + String.valueOf(commonStatusDto));
                                statusService.insertStatusMaster(commonStatusDto);

                            } catch (Exception e) {
                                throw new Exception("ステータス履歴の追加に失敗しました。");
                            }

                            int sqlUpdateCount4 = oaiw004ScreenMapper.inUpdateHead(params);
                            if (sqlUpdateCount4 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount4));
                            }
                            int sqlUpdateCount5 = oaiw004ScreenMapper.inUpdateTemp(params);
                            if (sqlUpdateCount5 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount5));
                            }

                            logger.info("搬入OK");
                            returnVal.setSCANRESULT("搬入確認");
                            returnVal.setAUDIOPATH("/Audio/Ok.mp3");
                        } catch (Exception e) {
                            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
                            // 貨物個数として0を設定
                            returnVal.setSCANPICEC("0");
                            returnVal.setDATAPICEC("0");
                            // エラーを表示
                            logger.info("エラー");
                            returnVal.setERRORFLG("1");
                            returnVal.setSCANRESULT("エラー");
                            returnVal.setAUDIOPATH("/Audio/Error.mp3");
                            logger.error(e.toString());
                            throw new Exception(e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                logger.error(e.toString());
                // 貨物個数として0を設定
                returnVal.setSCANPICEC("0");
                returnVal.setDATAPICEC("0");
                // エラーを表示
                logger.info("エラー");
                returnVal.setSCANRESULT("エラー");
                returnVal.setERRORFLG("1");
                returnVal.setAUDIOPATH("/Audio/Error.mp3");

            } finally {
                logger.info("resultList : " + returnVal);
            }
        }
        return returnVal;
    }

    // 搬入件数取得
    public OAIW004InCountDao inSearchCount(OAIW004SearchDto params) {
        return oaiw004ScreenMapper.inSearchCount(params);
    }

    // 複数個口の貨物個数の入力処理
    public OAIW004InDataPicecDao inCheckDataPicec(OAIW004SearchDto params) {
        // List resultList = new ArrayList<>();
        OAIW004InDataPicecDao returnVal =  new OAIW004InDataPicecDao();
        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo().toString());
            }
        }

        OAIW004InResultDao searchResult = oaiw004ScreenMapper.inSearch(params);
        if (searchResult == null) {
            returnVal.setDATAPICEC(null);
            returnVal.setOVERFLG(null);
            returnVal.setAWBNO(null);
        } else {
            try {
                long dataPicec = Long.parseLong(searchResult.getDATAPICEC());
                String overFlg = searchResult.getOVERFLG();
                String awbNo = searchResult.getAWBNO();
                returnVal.setDATAPICEC(String.valueOf(dataPicec));
                returnVal.setSCANPICEC(String.valueOf(searchResult.getSCANPICEC()));
                returnVal.setOVERFLG(overFlg);
                returnVal.setAWBNO(awbNo);
            } catch (Exception e) {
                logger.error(e.toString());
            }
        }
        logger.info(returnVal.toString());
        return returnVal;
    }
    

}
