package com.kse.wmsv2.telegram.service;

import com.kse.wmsv2.telegram.dto.*;
import com.kse.wmsv2.telegram.mapper.TelegramMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Slf4j
@Service
public class TelegramService {
    @Autowired
    TelegramMapper telegramMapper;
    final String singleSpace = " ";
    final String singleZero = "0";

    private TelegramCommonResultDto selectTelegramCommonHeadData(TelegramCommonParamDto param) {
        return telegramMapper.selectTelegramCommonHeadData(param);
    }
    private TelegramOutResultDto selectTelegramOutData(TelegramOutParamDto param) {
        return telegramMapper.selectTelegramOutData(param);
    }
    private String selectCharacterItem1(SelectCharacterItem1ParamDto param) {
        return telegramMapper.selectCharacterItem1(param);
    }
    public String makeCommonTelegramHeader(TelegramCommonParamDto telegramCommonParamDto) throws Exception {
        String strCommon = "";

        // 電文共通項目の必須情報取得
        TelegramCommonResultDto telegramCommonResultDto = this.selectTelegramCommonHeadData(telegramCommonParamDto);

        // 制御情報
        strCommon += StringUtils.rightPad("", 3, singleSpace);
        // 業務コード
        strCommon += StringUtils.rightPad(telegramCommonParamDto.getBusinessCd(), 5, singleSpace);
        // 予約エリア１
        strCommon += StringUtils.rightPad("", 21, singleSpace);
        // 利用者コード
        strCommon += StringUtils.rightPad(telegramCommonResultDto.getUserCode(), 5, singleSpace);
        // 識別番号
        strCommon += StringUtils.rightPad(telegramCommonResultDto.getUserNo(), 3, singleSpace);
        // 利用者パスワード
        strCommon += StringUtils.rightPad(telegramCommonResultDto.getUserPwd(), 8, singleSpace);
        // 予約エリア２
        strCommon += StringUtils.rightPad("", 174, singleSpace);
        // 電文引継情報
        strCommon += StringUtils.rightPad(telegramCommonParamDto.getFileName(), 26, singleSpace);
        // 予約エリア３
        strCommon += StringUtils.rightPad("", 8, singleSpace);
        // 入力情報特定番号
        strCommon += StringUtils.rightPad("", 10, singleSpace);
        // 索引引継情報
        strCommon += StringUtils.rightPad("", 100, singleSpace);
        // 予約エリア４
        strCommon += StringUtils.rightPad("", 1, singleSpace);
        // システム識別
        strCommon += StringUtils.rightPad("1", 1, singleSpace);
        // 予約エリア５
        strCommon += StringUtils.rightPad("", 27, singleSpace);

        // 電文長　＝　400(共通部電文長) + 25(OUT共通部レコード長) + 77(OUT繰返部レコード長) ×　明細部出力データ数
        String telegramLength = StringUtils.leftPad(Integer.toString(400 + 25 + (77 * telegramCommonParamDto.getRecordCnt())), 6, singleZero);
        strCommon += telegramLength;
        return strCommon;
    }

    public void makeOutTelegramFile(String outBackupDir, TelegramCommonParamDto telegramCommonParamDto,
                                    List<OutTelegramParamDto> outTelegramParamDtoList) throws Exception {

        String newLine = "\r\n";
        log.info("outTelegramParamDtoList:" + outTelegramParamDtoList.toString());
        File file  = new File(outBackupDir+ File.separator + telegramCommonParamDto.getFileName());
        // PrintWriter作成、文字コード指定(EUC_JP)
        PrintWriter p_writer = new PrintWriter(new BufferedWriter
                (new OutputStreamWriter(new FileOutputStream(file),"EUC_JP")));
        // 電文共通項目
        String strCommon = "";

        /* 共通項目作成 START */
        strCommon = this.makeCommonTelegramHeader(telegramCommonParamDto);

        // 交通項目行を出力
        p_writer.print(strCommon + newLine);
        /* 共通項目作成 END */

        /* OUT電文ヘッド(共通部)出力 START */
        // 搬出年月日
        String carryOutDate = outTelegramParamDtoList.get(0).getCarryOutDate().substring(0,10).replaceAll("-", "");
        String _carryOutDate = StringUtils.leftPad(carryOutDate, 8, singleSpace);
        p_writer.print(_carryOutDate + newLine);
        // 取消表示
        p_writer.print(singleSpace + newLine);
        // 集荷業者
        String collectTrader = outTelegramParamDtoList.get(0).getCollectTrader();
        p_writer.print( StringUtils.rightPad(collectTrader != null ? collectTrader : "", 3, singleSpace) + newLine);
        // 他所蔵置場所
        String otherWareHouse = outTelegramParamDtoList.get(0).getOtherWareHouse();
        p_writer.print( StringUtils.rightPad( otherWareHouse != null ? otherWareHouse : "", 5, singleSpace) + newLine);
        /* OUT電文ヘッド(共通部)出力 END */

        /* OUT電文の個別項目出力 START　*/
        for (OutTelegramParamDto outData : outTelegramParamDtoList) {
            TelegramOutParamDto telegramOutParamDto = new TelegramOutParamDto();
            telegramOutParamDto.setAwbNo(outData.getAwbNo());
            telegramOutParamDto.setCwbNo(outData.getCwbNo());
            telegramOutParamDto.setCwbNoDeptCd(outData.getCwbNoDeptCd());
            // OUT電文データ取得(輸入通関データ)取得
            TelegramOutResultDto telegramOutResultDto = this.selectTelegramOutData(telegramOutParamDto);
            log.info("telegramOutParamDto:" + telegramOutParamDto.toString());
            log.info("telegramOutResultDto:" + telegramOutResultDto.toString());

            // OUT電文データが0の場合、以下のOUT繰返部レコードを作成してOUT電文ファイルに出力する。
            if(telegramOutResultDto == null) {
                // AWB番号(CWBNo)
                p_writer.print(StringUtils.rightPad("", 20, singleSpace) + newLine);
                // 航空貨物代理店
                p_writer.print(StringUtils.rightPad("", 5, singleSpace) + newLine);
                // 時間外搬出表示
                p_writer.print(StringUtils.rightPad("", 1, singleSpace) + newLine);
                // 搬出時刻
                p_writer.print(StringUtils.rightPad("", 4, singleSpace) + newLine);
                // 支払種別
                p_writer.print(StringUtils.rightPad("", 1, singleSpace) + newLine);
                // アディショナルチャージ
                p_writer.print(StringUtils.rightPad("", 30, singleSpace) + newLine);
                // 差日数
                p_writer.print(StringUtils.rightPad("", 2, singleSpace) + newLine);
            } else {
                // OUT電文データが1件以上の場合、以下のOUT繰返部レコードを作成してOUT電文ファイルに出力する。
                // AWB番号(CWBNo)
                String cwbNo = outData.getCwbNo();
                if(outData.getCwbNoDeptCd().equals("000")) {
                    p_writer.print(StringUtils.rightPad(cwbNo, 20, singleSpace) + newLine);
                } else {
                    p_writer.print(StringUtils.rightPad(cwbNo +"/"+outData.getCwbNoDeptCd(), 20, singleSpace) + newLine);
                }
                // 航空貨物代理店
                SelectCharacterItem1ParamDto outAgentDto = new SelectCharacterItem1ParamDto();
                outAgentDto.setNameClass("0112");
                outAgentDto.setNameCd("004");
                outAgentDto.setDepartmentCd(outData.getBondedWarehouseCd());
                String outAgent = this.selectCharacterItem1(outAgentDto);
                p_writer.print(StringUtils.rightPad(outAgent != null ? outAgent : "", 5, singleSpace) + newLine);

                // 時間外搬出表示
                p_writer.print(StringUtils.rightPad("", 1, singleSpace) + newLine);
                // 搬出時刻
                p_writer.print(StringUtils.rightPad(outData.getBondedOutHHmm(), 4, singleSpace) + newLine);
                // 支払種別
                SelectCharacterItem1ParamDto payKindDto = new SelectCharacterItem1ParamDto();
                payKindDto.setNameClass("0112");
                payKindDto.setNameCd("005");
                payKindDto.setDepartmentCd(outData.getBondedWarehouseCd());
                String payKind = this.selectCharacterItem1(payKindDto);

                p_writer.print(StringUtils.rightPad(payKind != null ? payKind : "", 1, singleSpace) + newLine);
                // アディショナルチャージ
                p_writer.print(StringUtils.rightPad("", 30, singleSpace) + newLine);
                // 差日数
                p_writer.print(StringUtils.rightPad(telegramOutResultDto.getDiffDays() != null ? telegramOutResultDto.getDiffDays() : "", 2, singleSpace) + newLine);
            }
        }
        //ファイルをクローズする
        p_writer.close();
        /* OUT電文の個別項目出力 END　*/
    }
}
