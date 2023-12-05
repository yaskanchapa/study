package com.kse.wmsv2.oa_iw_004.service;

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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class OAIW004InvService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    long workTime = 0; // Timeout確認用変数

    @Autowired
    OAIW004ScreenMapper oaiw004ScreenMapper;

    @Autowired
    OAIW004CommonService commonService;

    // インベン対象なマスター検索
    public List<OAIW004InvSearchMawbDao> invSearchMawb(OAIW004SearchDto params) {
        logger.info(params.toString());
        return oaiw004ScreenMapper.invSearchMawb(params);
    }

    // インベンスキャン処理
    @Transactional
    public OAIW004InvReturnValDao invSearch(OAIW004SearchDto params) {
        logger.info(params.toString());

        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        // Refactoring
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo().toString());
            }
        }

        // HTに値を返すための変数
        OAIW004InvReturnValDao returnVal = new OAIW004InvReturnValDao();
        returnVal.setAWBNO(params.getMawbNo());
        returnVal.setCWBNO(params.getHawbNo());

        // 重複チェックのための変数設定
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date currentDate = new java.util.Date();

        // 貨物個数のための変数設定
        long dataPiece = 0;
        long scanPiece = 0;

        // 入力値が空白か確認するための変数
        String mawbInputCheck = params.getMawbNo().toString().replaceAll(" ","");
        String hawbInputCheck = params.getHawbNo().toString().replaceAll(" ", "");

        // HT端末ID確認のための変数
        String terminalIdCheck = params.getTermNo().toString().replaceAll(" ","");
        OAIW004SearchWarehouseDao terminalAuthCheck = oaiw004ScreenMapper.searchWarehouse(params);

        if ("".equals(terminalIdCheck)) {
            // 貨物個数として0を設定
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID無し");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");
        } else if (terminalAuthCheck == null) {
            returnVal.setSCANPICEC("0");
            returnVal.setDATAPICEC("0");
            // Timeout判定をViewに返す
            returnVal.setSCANRESULT("端末ID未許可");
            returnVal.setAUDIOPATH("/Audio/Error.mp3");

            // マスター番号・ハウス番号の入力確認
        } else if ("".equals(mawbInputCheck)) { // マスター番号が空白か確認
            logger.info("MAWB無し");
            // 貨物個数として0を設定
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

            OAIW004InvResultDao searchResult = oaiw004ScreenMapper.invSearch(params);

            if (searchResult == null) {
                logger.info("データ無しA");
                params.setMawbNo("");
                OAIW004InvResultDao searchResult2 = oaiw004ScreenMapper.invSearch(params);
                if (searchResult2 == null) {
                    logger.info("データ無しB");
                    // 貨物個数として0を設定
                    returnVal.setSCANPICEC("0");
                    returnVal.setDATAPICEC("0");
                    // Timeout判定をViewに返す
                    returnVal.setSCANRESULT("データ無し");
                    returnVal.setAUDIOPATH("/Audio/Error.mp3");
                } else {
                    logger.info("フライト違い");
                    // 貨物個数として0を設定
                    returnVal.setSCANPICEC("0");
                    returnVal.setDATAPICEC("0");
                    // Timeout判定をViewに返す
                    returnVal.setSCANRESULT("フライト違い");
                    returnVal.setAUDIOPATH("/Audio/Flight.mp3");
                }
            } else if ((searchResult.getSCANPICEC()).equals(searchResult.getDATAPICEC()) || "1".equals(searchResult.getOVERFLG()) || "2".equals(searchResult.getOVERFLG())) {
                logger.info(searchResult.getSCANPICEC());
                logger.info(searchResult.getDATAPICEC());
                logger.info(searchResult.getOVERFLG());
                logger.info("インベン済み");
                // 貨物個数として0を設定
                returnVal.setSCANPICEC("0");
                returnVal.setDATAPICEC("0");
                // Timeout判定をViewに返す
                returnVal.setSCANRESULT("インベン済み");
                returnVal.setAUDIOPATH("/Audio/zumi.mp3");

            } else {

                try { // Update, Delete最中にエラーがあった時に備えたTry & Transaction
                    logger.info("インベンOK");
                    dataPiece = Long.parseLong(searchResult.getDATAPICEC());

                    returnVal.setSCANPICEC(String.valueOf(scanPiece));
                    returnVal.setDATAPICEC(String.valueOf(dataPiece));

                    if (!"".equals(params.getInputScanPicec())) { // 複数個口貨物の場合
                        long inputScanPicec = Long.parseLong(params.getInputScanPicec());

                        if (inputScanPicec > dataPiece) {
                            logger.info("貨物数入力確認");
                            returnVal.setSCANRESULT("貨物数入力確認");
                            returnVal.setAUDIOPATH("/Audio/Error.mp3");
                        } else {
                            int sqlUpdateCount1 = oaiw004ScreenMapper.invUpdatePiece(params);
                            if (sqlUpdateCount1 != 1) { // 正常に1件更新されたかチェック
                                throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount1));
                            }
                            returnVal.setSCANPICEC(String.valueOf(inputScanPicec));
                        }
                    } else {
                        params.setInputScanPicec("1");
                        returnVal.setSCANPICEC("1");
                        int sqlUpdateCount1 = oaiw004ScreenMapper.invUpdatePiece(params);
                        if (sqlUpdateCount1 != 1) { // 正常に1件更新されたかチェック
                            throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount1));
                        }
                    }
                    if ((params.getInputScanPicec().toString()).equals(searchResult.getDATAPICEC())) {
                        int sqlUpdateCount2 = oaiw004ScreenMapper.invUpdate(params);
                        if (sqlUpdateCount2 != 1) { // 正常に1件更新されたかチェック
                            throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount2));
                        }
                        int sqlUpdateCount3 = oaiw004ScreenMapper.invUpdateHead(params);
                        if (sqlUpdateCount3 != 1) { // 正常に1件更新されたかチェック
                            throw new Exception("正常な更新件数(1件)ではございません。更新件数 : " + String.valueOf(sqlUpdateCount3));
                        }
                    }
                    logger.info("インベンOK");
                    returnVal.setSCANRESULT("インベンOK");
                    returnVal.setAUDIOPATH("/Audio/invPerfect.mp3");
                } catch (Exception e) { // SQLエラー
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
                    logger.error(e.toString());
                    returnVal.setSCANPICEC("0");
                    returnVal.setDATAPICEC("0");
                    returnVal.setSCANRESULT("エラー");
                    returnVal.setAUDIOPATH("/Audio/Error.mp3");
                }
            }
        }
        return returnVal;
    }

    // インベン件数取得
    public OAIW004InvCountDao invSearchCount(OAIW004SearchDto params) {
        return oaiw004ScreenMapper.invSearchCount(params);
    }

    // 複数個口の貨物個数の入力処理
    public OAIW004InvDataPicecDao invCheckDataPicec(OAIW004SearchDto params) {
        // List resultList = new ArrayList<>();
        OAIW004InvDataPicecDao returnVal =  new OAIW004InvDataPicecDao();
        // 入力したハウスバーコードがCodabarであり、両端にアルファベットがある場合
        // Refactoring
        String hawbNo = StringUtil.isStringNull(params.getHawbNo());
        if (hawbNo.length() > 0) {
            if (commonService.checkCodaBar(hawbNo)) {
                params.setHawbNo(commonService.castAwbNo(hawbNo));
                logger.info("hawbNo(Codabar) : "+params.getHawbNo().toString());
            }
        }

        OAIW004InvResultDao searchResult = oaiw004ScreenMapper.invSearch(params);

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
