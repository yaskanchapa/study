package com.kse.wmsv2.oa_iw_002.service;

import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.common.util.RedisUtil;
import static com.kse.wmsv2.oa_iw_002.common.OAIW001CommonConstants.*;
import com.kse.wmsv2.oa_iw_002.mapper.OAIW002ScreenMapper;
import com.kse.wmsv2.telegram.dto.OutTelegramParamDto;
import com.kse.wmsv2.telegram.dto.TelegramCommonParamDto;
import com.kse.wmsv2.telegram.service.TelegramService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import com.kse.wmsv2.oa_iw_002.dao.*;
import com.kse.wmsv2.oa_iw_002.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Service
@Slf4j
public class OAIW002ScreenService {
    @Autowired
    OAIW002ScreenMapper oaiw002ScreenMapper;

    @Autowired
    TelegramService telegramServ;

    @Autowired
    private AwsS3 awsS3;

    @Autowired
    private RedisUtil redisUtil;

    public List<OAIW002OutFilePathDao> OutFilePathList(OAIW002OutFilePathDto params) {
        return oaiw002ScreenMapper.selectOutFilePathList(params);
    }
    public List<OAIW002SelectBondedOutListDto> selectBondedOutList(OAIW002SelectBondedOutListDao params) {
        return oaiw002ScreenMapper.selectBondedOutList(params);
    }
    public List<OAIW002SelectAiData1Dto> selectAiData1(OAIW002SelectAiData1Dao params) {
        return oaiw002ScreenMapper.selectAiData1(params);
    }

    public List<OAIW002SelectAiCargoOutHead1Dto> selectAiCargoOutHead1(OAIW002SelectAiCargoOutHead1Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutHead1(params);
    }
    public int insertAiCargoInData1(OAIW002InsertAiCargoInData1Dao params) {
        return oaiw002ScreenMapper.insertAiCargoInData1(params);
    }
    public int insertAiCargoOutData1(List<OAIW002InsertAiCargoOutData1Dao> paramList) {
        return oaiw002ScreenMapper.insertAiCargoOutData1(paramList);
    }
    public List<OAIW002SelectAiCargoOutDataCnt1Dto> OAIW002SelectAiCargoOutDataCnt1Dao(OAIW002SelectAiCargoOutDataCnt1Dao params) {
        return oaiw002ScreenMapper.OAIW002SelectAiCargoOutDataCnt1Dao(params);
    }
    public OAIW002SelectAiCargoOutDataCnt1Dto selectAiCargoOutDataCnt1(OAIW002SelectAiCargoOutDataCnt1Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutDataCnt1(params);
    }
    public int updateAiCargoOutHead1(OAIW002UpdateAiCargoOutHead1Dao params) {
        return oaiw002ScreenMapper.updateAiCargoOutHead1(params);
    }
    public int updateAiData1(OAIW002UpdateAiData1Dao params) {
        return oaiw002ScreenMapper.updateAiData1(params);
    }
    public int insertAiStatusHistory1(List<OAIW002InsertAiStatusHistory1Dao> insertAiStatusHistory1DaoList) {
        return oaiw002ScreenMapper.insertAiStatusHistory1(insertAiStatusHistory1DaoList);
    }
    public int updateAiCargoOutData1(OAIW002UpdateAiCargoOutData1Dao params) {
        return oaiw002ScreenMapper.updateAiCargoOutData1(params);
    }
    public List<OAIW002SelectAiCargoOutData1Dto> selectAiCargoOutData1(OAIW002SelectAiCargoOutData1Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutData1(params);
    }
    public int deleteAiStatusHistory1(OAIW002DeleteAiStatusHistory1Dao params) {
        return oaiw002ScreenMapper.deleteAiStatusHistory1(params);
    }
    public int deleteAiCargoOutData1(OAIW002DeleteAiCargoOutData1Dao params) {
        return oaiw002ScreenMapper.deleteAiCargoOutData1(params);
    }
    public int updateAiCargoOutHead2(OAIW002UpdateAiCargoOutHead2Dao params) {
        return oaiw002ScreenMapper.updateAiCargoOutHead2(params);
    }
    public OAIW002SelectAiCargoOutHead2Dto selectAiCargoOutHead2(OAIW002SelectAiCargoOutHead2Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutHead2(params);
    }
    public int deleteAiCargoOutHead1(OAIW002DeleteAiCargoOutHead1Dao params) {
        return oaiw002ScreenMapper.deleteAiCargoOutHead1(params);
    }
    public int updateAiData2(OAIW002UpdateAiData2Dao params) {
        return oaiw002ScreenMapper.updateAiData2(params);
    }

    public List<OAIW002SelectAiCargoOutData2Dto> selectAiCargoOutData2(OAIW002SelectAiCargoOutData2Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutData2(params);
    }
    public int deleteAiCargoOutData2(OAIW002DeleteAiCargoOutData2Dao params) {
        return oaiw002ScreenMapper.deleteAiCargoOutData2(params);
    }
    public List<OAIW002SelectAiData3Dto> selectAiData3(OAIW002SelectAiData3Dao params) {
        return oaiw002ScreenMapper.selectAiData3(params);
    }
    public OAIW002SelectAiCargoOutHead3Dto selectAiCargoOutHead3(OAIW002SelectAiCargoOutHead3Dao params) {
        return oaiw002ScreenMapper.selectAiCargoOutHead3(params);
    }
    public int updateAiCargoOutHead4(OAIW002UpdateAiCargoOutHead4Dao params) {
        return oaiw002ScreenMapper.updateAiCargoOutHead4(params);
    }
    public int deleteAiCargoOutHead2(OAIW002DeleteAiCargoOutHead2Dao params) {
        return oaiw002ScreenMapper.deleteAiCargoOutHead2(params);
    }
    public int updateAiData3(OAIW002UpdateAiData3Dao params) {
        return oaiw002ScreenMapper.updateAiData3(params);
    }
    public int insertAiStatusHistory3(List<OAIW002InsertAiStatusHistory3Dao> insertAiStatusHistory3DaoList) {
        return oaiw002ScreenMapper.insertAiStatusHistory3(insertAiStatusHistory3DaoList);
    }
    public int insertCsSendMessage(OAIW002InsertCsSendMessageDao insertCsSendMessageDaoList) {
        return oaiw002ScreenMapper.insertCsSendMessage(insertCsSendMessageDaoList);
    }

    public List<OAIW002SearchResultDto> searchBondedOutData(OAIW002SelectBondedOutListDao param) throws Exception {
        List<OAIW002SearchResultDto> searchResultDtoList = new ArrayList<OAIW002SearchResultDto>();

        List<OAIW002SelectBondedOutListDto> bondedOutList = this.selectBondedOutList(param);
        // 取得したデータが0件の場合、、処理を終了する。
        if (bondedOutList == null || bondedOutList.size() == 0) {
            return null;
        }

        // 緊急
        int e01 = 0; // 未許可（未申告含む）件数
        int e02 = 0; // 許可件数
        int e03 = 0; // 搬出中（搬出済含む）

        // 請求
        int c01 = 0; // 未許可（未申告含む）件数
        int c02 = 0; // 許可件数
        int c03 = 0; // 搬出中（搬出済含む）

        //無税
        int d01 = 0; // 未許可（未申告含む）件数
        int d02 = 0; // 許可件数
        int d03 = 0; // 搬出中（搬出済含む）

        //マニ
        int m01 = 0; // 未許可（未申告含む）件数
        int m02 = 0; // 許可件数
        int m03 = 0; // 搬出中（搬出済含む）

        //申告済み件数
        int count1 = 0;
        //未搬出件数
        int count2 = 0;

        // 未申告件数計算用変数
        int e012 = 0; // 緊急
        int c012 = 0; // 請求
        int d012 = 0; // 無税
        int m012 = 0; // マニフェスト

        // 搬出スキャン済み件数計算用変数
        int e04 = 0; // 緊急
        int c04 = 0; // 請求
        int d04 = 0; // 無税
        int m04 = 0; // マニフェスト

        // 仕分区分名先頭２文字 = "許可"件数計算用変数
        int e042 = 0;  // 緊急
        int c042 = 0;  // 請求
        int d042 = 0;  // 無税
        int m042 = 0;  // マニフェスト

        // '申告条件"S","U"存在フラグ
        boolean suRepConFLG = false;
        // RFIDフラグ
        boolean rfidFlg = false;

        String ftl1 = bondedOutList.get(0).getArrFtl1();
        String ftl2 = bondedOutList.get(0).getArrFtl2();
        String origin = bondedOutList.get(0).getOrigin();
        String awbNo = bondedOutList.get(0).getAwbNo();
        String key = ftl1 + ftl2 + origin + awbNo;

        int idx = 0; // forループのインデックス
        int rowIndex = -1; // Gridデータのインデックス
        boolean isResetKey = false;
        for (OAIW002SelectBondedOutListDto dto : bondedOutList) {
            if(isResetKey) {
                ftl1 = dto.getArrFtl1();
                ftl2 = dto.getArrFtl2();
                origin = dto.getOrigin();
                awbNo = dto.getAwbNo();
                key = ftl1 + ftl2 + origin + awbNo;
                isResetKey = false;
            }
            String inClassifyClassName = dto.getInClassifyClassName();

            switch (inClassifyClassName) {
                case "緊急":
                case "Ｓ仕分緊急":
                    // 未許可（未申告含む）　許可(IB00600)がないもの
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            e01 += 1;
                        } else {
                            e012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        // 許可(IB00600)があり、搬出中(IB00710)がないもの = 許可件数
                        e02 += 1;
                    }
                    // 搬出中(IB00710)があり、搬出済(IB00720)がないもの
                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        e03 += 1;
                    }
                    if (dto.getIb00720Cnt() > 0) {
                        e04 += 1;
                    }
                    break;

                case "請求":
                case "Ｓ仕分請求":
                case "検査":
                case "S/U検査":
                    // 未許可（未申告含む）
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            c01 += 1;
                        } else {
                            c012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        // 許可件数
                        c02 += 1;
                    }
                    // 搬出中(IB00710)があり、搬出済(IB00720)がないもの
                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        c03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        c04 += 1;
                    }
                    break;

                case "無税":
                case "Ｓ無税":
                    // 未許可（未申告含む）
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            d01 += 1;
                        } else {
                            d012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        // 許可件数
                        d02 += 1;
                    }
                    // 搬出中(IB00710)があり、搬出済(IB00720)がないもの
                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        d03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        d04 += 1;
                    }
                    break;
                case "マニ申告":
                case "Ｓマニ申告":
                    // 未許可（未申告含む）
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            m01 += 1;
                        } else {
                            m012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        // '許可件数
                        m02 += 1;
                    }

                    // 搬出中(IB00710)があり、搬出済(IB00720)がないもの
                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        m03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        m04 += 1;
                    }
                    break;
                case "許可緊急":
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            e01 += 1;
                        } else {
                            e012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        e02 += 1;
                    }

                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        e03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        e042 += 1;
                    }
                    break;
                case "許可請求":
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            c01 += 1;
                        } else {
                            c012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        c02 += 1;
                    }

                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        c03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        c042 += 1;
                    }
                    break;
                case "許可無税":
                    if (dto.getIb00600Cnt() == 0) {
                        if (dto.getIb00500Cnt() > 0) {
                            d01 += 1;
                        } else {
                            d012 += 1;
                        }
                    } else if (dto.getIb00710Cnt() == 0) {
                        d02 += 1;
                    }

                    if (dto.getIb00710Cnt() > 0 && dto.getIb00720Cnt() == 0) {
                        d03 += 1;
                    }

                    if (dto.getIb00720Cnt() > 0) {
                        d042 += 1;
                    }
                    break;
                default:
                    // do nothing
                    break;
            }

            // 申告済件数
            if (dto.getIb00500Cnt() > 0 || dto.getIb00600Cnt() > 0) {
                count1 += 1;
            }
            // 未搬出件数
            count2 += 1;

            if ("S".equals(dto.getReportCondition()) || "U".equals(dto.getReportCondition())) {
                suRepConFLG = true;
            }

            if ("1".equals(dto.getBbm())) {
                rfidFlg = true;
            }

            String nextKey = "";
            if(bondedOutList.size() - 1 > idx) {
                //キーチェック対象取得
                String nextFtl1 = bondedOutList.get(idx+1).getArrFtl1();
                String nextFtl2 = bondedOutList.get(idx+1).getArrFtl2();
                String nextOrigin = bondedOutList.get(idx+1).getOrigin();
                String nextAwbNo = bondedOutList.get(idx+1).getAwbNo();
                nextKey = nextFtl1 + nextFtl2 + nextOrigin + nextAwbNo;
            }
            //キーが変わる又は最終行の場合リストに出力
            if(!key.equals(nextKey)) {
                if (param.isNoPermitAwbHiddenFlg() == true
                        && (e02 + c02 + d02 + m02) == 0
                        && (e03 + c03 + d03 + m03) == 0
                        && (e04 + c04 + d04 + m04) == 0
                        && (e042 + e042 + e042 + e042) == 0
                ) {
                    // グリッドに出力しない
                    // do nothing
                } else {
                    // 1行目作成
                    rowIndex += 1;
                    OAIW002SearchResultDto searchResultRow1 = new OAIW002SearchResultDto();
                    searchResultRow1.set_rowIndex(rowIndex);
                    searchResultRow1.setLineEnable(false);
                    searchResultRow1.setRowType(ROW_TYPE_TOP);
                    searchResultRow1.setSelect(false); // 選
                    searchResultRow1.setArrFtl(ftl1 + "/" + ftl2); // FTL
                    searchResultRow1.setOrigin(origin);
                    searchResultRow1.setAwbNo(awbNo);
                    searchResultRow1.setAwbCnt(count1 + "/" + count2); // CWB件数(申告済件数/未搬出件数)
                    searchResultRow1.setEmergency(e01 + "/" + e012); // 緊急
                    searchResultRow1.setClaim(c01 + "/" + c012); // 請求
                    searchResultRow1.setDutyFree(d01 + "/" + d012); // 無税
                    searchResultRow1.setManifest(m01 + "/" + m012); // マニフェスト
                    searchResultRow1.set_origin(dto.getOrigin()); // データ保持用
                    searchResultRow1.set_awbNo(dto.getAwbNo()); // データ保持用
                    searchResultRow1.set_arrFtl1(dto.getArrFtl1()); // データ保持用
                    searchResultRow1.set_arrFtl2(dto.getArrFtl2()); // データ保持用
                    searchResultDtoList.add(searchResultRow1);

                    // 2行目作成(許可)
                    rowIndex += 1;
                    OAIW002SearchResultDto searchResultRow2 = new OAIW002SearchResultDto();
                    searchResultRow2.set_rowIndex(rowIndex);
                    searchResultRow2.setRowType(ROW_TYPE_PERMIT);
                    if (rfidFlg) {
                        searchResultRow2.setLineEnable(false);
                    } else {
                        searchResultRow2.setLineEnable(true);
                    }
                    searchResultRow2.setSelect(false); // 選
                    if (suRepConFLG) {
                        searchResultRow2.setArrFtl("(S/U申有り)"); // FTL
                    } else if (rfidFlg) {
                        searchResultRow2.setArrFtl("(RFID)"); // FTL
                    } else {
                        searchResultRow2.setArrFtl("");
                    }

                    searchResultRow2.setOrigin("");
                    searchResultRow2.setAwbNo("許可件数");
                    searchResultRow2.setAwbCnt(Integer.toString(e02 + c02 + d02 + m02)); // CWB件数
                    searchResultRow2.setEmergency(Integer.toString(e02)); // 緊急
                    searchResultRow2.setClaim(Integer.toString(c02));
                    searchResultRow2.setDutyFree(Integer.toString(d02)); // 無税
                    searchResultRow2.setManifest(Integer.toString(m02)); // マニフェスト
                    searchResultRow2.set_origin(dto.getOrigin()); // データ保持用
                    searchResultRow2.set_awbNo(dto.getAwbNo()); // データ保持用
                    searchResultRow2.set_arrFtl1(dto.getArrFtl1()); // データ保持用
                    searchResultRow2.set_arrFtl2(dto.getArrFtl2()); // データ保持用
                    searchResultDtoList.add(searchResultRow2);

                    // 3行目作成(搬出中)
                    rowIndex += 1;
                    OAIW002SearchResultDto searchResultRow3 = new OAIW002SearchResultDto();
                    searchResultRow3.set_rowIndex(rowIndex);
                    searchResultRow3.setRowType(ROW_TYPE_BONDEDOUT);
                    searchResultRow3.setLineEnable(true);
                    searchResultRow3.setSelect(false); // 選
                    searchResultRow3.setArrFtl(""); // FTL
                    searchResultRow3.setOrigin(""); // ORIGIN
                    searchResultRow3.setAwbNo("搬出中");
                    searchResultRow3.setAwbCnt(Integer.toString(e03 + c03 + d03 + m03)); // CWB件数
                    searchResultRow3.setEmergency(Integer.toString(e03)); // 緊急
                    searchResultRow3.setClaim(Integer.toString(c03));
                    searchResultRow3.setDutyFree(Integer.toString(d03)); // 無税
                    searchResultRow3.setManifest(Integer.toString(m03)); // マニフェスト
                    searchResultRow3.set_origin(dto.getOrigin()); // データ保持用
                    searchResultRow3.set_awbNo(dto.getAwbNo()); // データ保持用
                    searchResultRow3.set_arrFtl1(dto.getArrFtl1()); // データ保持用
                    searchResultRow3.set_arrFtl2(dto.getArrFtl2()); // データ保持用
                    searchResultDtoList.add(searchResultRow3);

                    // 4行目作成(搬出済み)
                    rowIndex += 1;
                    OAIW002SearchResultDto searchResultRow4 = new OAIW002SearchResultDto();
                    searchResultRow4.set_rowIndex(rowIndex);
                    searchResultRow4.setRowType(ROW_TYPE_CARRYOUT);
                    searchResultRow4.setLineEnable(false);
                    searchResultRow4.setSelect(false); // 選
                    searchResultRow4.setArrFtl(""); // FTL
                    searchResultRow4.setOrigin(""); // ORIGIN
                    searchResultRow4.setAwbNo("搬出済");
                    searchResultRow4.setAwbCnt(Integer.toString(e04 + c04 + d04 + m04) + "/" + Integer.toString(e042 + c042 + d042 + m042)); // CWB件数
                    searchResultRow4.setEmergency(e04 + "/" + e042); // 緊急
                    searchResultRow4.setClaim(c04 + "/" + c042);
                    searchResultRow4.setDutyFree(d04 + "/" + d042); // 無税
                    searchResultRow4.setManifest(m04 + "/" + m042); // マニフェスト
                    searchResultRow4.set_origin(dto.getOrigin()); // データ保持用
                    searchResultRow4.set_awbNo(dto.getAwbNo()); // データ保持用
                    searchResultRow4.set_arrFtl1(dto.getArrFtl1()); // データ保持用
                    searchResultRow4.set_arrFtl2(dto.getArrFtl2()); // データ保持用
                    searchResultDtoList.add(searchResultRow4);

                    // 変数初期化
                    ftl1 = "";
                    ftl2 = "";
                    origin = "";
                    awbNo = "";
                    // 件数
                    e01 = 0;
                    e02 = 0;
                    e03 = 0;
                    // 請求
                    c01 = 0;
                    c02 = 0;
                    c03 = 0;
                    // 無税
                    d01 = 0;
                    d02 = 0;
                    d03 = 0;
                    // マニ
                    m01 = 0;
                    m02 = 0;
                    m03 = 0;
                    //申告済み件数
                    count1 = 0;
                    //未搬出件数
                    count2 = 0;
                    //未申告件数計算用変数
                    e012 = 0;
                    c012 = 0;
                    d012 = 0;
                    m012 = 0;
                    // 搬出スキャン済み件数計算用変数
                    e04 = 0;
                    c04 = 0;
                    d04 = 0;
                    m04 = 0;
                    // 仕分区分名先頭２文字 = "許可"件数計算用変数
                    e042 = 0;
                    c042 = 0;
                    d042 = 0;
                    m042 = 0;
                    // 申告条件"S","U"存在フラグ
                    suRepConFLG = false;
                    // RFIDフラグ
                    rfidFlg = false;
                }
            isResetKey = true;
        }
        idx = idx + 1;
    }
        return searchResultDtoList;
}

    @Transactional(rollbackFor = Exception.class)
    public OAIW002DoPermitResultDto doPermitBusiness(HttpHeaders headers, OAIW002DoPermitDto doPermitDto) throws Exception {
        OAIW002DoPermitResultDto resultDto = new OAIW002DoPermitResultDto();
        final OAIW002SelectBondedOutListDao param1 = doPermitDto.getParam1(); // 検索条件
        final OAIW002SearchResultDto param2 = doPermitDto.getParam2(); // 許可行のRowData
        final OAIW002SearchResultDto param3 = doPermitDto.getParam3(); // 搬出行のRowData

        final String accessToken = (String)headers.get("authorization").get(0);
        final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        String inClassifyClassName = "";
        String cargoOutDate = "";
        final String now = getTimeStampNow();
        final String awbNo = param2.get_awbNo();
        final String arrFtl1 = param2.get_arrFtl1();
        final String arrFtl2 = param2.get_arrFtl2();
        final String bondedWarehouseCd = param1.getBondedWarehouseCd();

        switch (param2.get_columnId()) {
            case COLID_EMERGENCY:
                inClassifyClassName = "緊急";
                break;
            case COLID_CLAIM:
                inClassifyClassName = "請求";
                break;
            case COLID_DUTYFREE:
                inClassifyClassName = "無税";
                break;
            case COLID_MANIFEST:
                inClassifyClassName = "マニ";
                break;
            default:
                // do nothing
                break;
        }
        log.info("inClassifyClassName: " + inClassifyClassName);

        //1.1 輸入通関データ(AI_DATA)から搬出データを取得して、搬出用一時データを取得する。
        OAIW002SelectAiData1Dao selectAiData1Dao = new OAIW002SelectAiData1Dao();
        selectAiData1Dao.setAwbNo(awbNo);
        selectAiData1Dao.setOrigin(param1.getOrigin());
        selectAiData1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        selectAiData1Dao.setArrFtl1(arrFtl1);
        selectAiData1Dao.setArrFtl2(arrFtl2);
        selectAiData1Dao.setCarryOutDate("");
        selectAiData1Dao.setInClassifyClassName(inClassifyClassName);
        selectAiData1Dao.setUnMatchHawbCntFlg(param1.isUnMatchHawbCntFlg());
        selectAiData1Dao.setUnMatchPieceFlg(param1.isUnMatchPieceFlg());
        selectAiData1Dao.setMatchFlg(param1.isMatchFlg());

        // 1.1　輸入通関データ(AI_DATA)から搬出データを取得して、搬出用一時データに登録する。
        List<OAIW002SelectAiData1Dto> selectAiData1DtoList = this.selectAiData1(selectAiData1Dao);
        log.info("selectAiData1Dao:" + selectAiData1Dao.toString());

        // 1.2 「1.1」から取得したデータが0件の場合、ロールバックした後、処理を終了する。
        if (selectAiData1DtoList == null || selectAiData1DtoList.size() == 0) {
            resultDto.setResult(false);
            resultDto.setMessage("許可処理が失敗しました。");
            resultDto.setErrorMessage("輸入通関データ(AI_DATA)がありません。");
            return resultDto;
        }

        // 1.3 輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)の登録又は更新処理を行う。
        // 1.3.1 輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)データを取得する。
        OAIW002SelectAiCargoOutHead1Dao selectAiCargoOutHead1Dao = new OAIW002SelectAiCargoOutHead1Dao();
        selectAiCargoOutHead1Dao.setAwbNo(awbNo);
        selectAiCargoOutHead1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        selectAiCargoOutHead1Dao.setArrFtl1(arrFtl1);
        selectAiCargoOutHead1Dao.setArrFtl2(arrFtl2);
        selectAiCargoOutHead1Dao.setCargoOutDate("");
        List<OAIW002SelectAiCargoOutHead1Dto> selectAiCargoOutHead1DtoList = this.selectAiCargoOutHead1(selectAiCargoOutHead1Dao);

        if (selectAiCargoOutHead1DtoList != null && selectAiCargoOutHead1DtoList.size() > 0) {
            cargoOutDate = selectAiCargoOutHead1DtoList.get(0).getCargoOutDate();
        } else {
            // 1.3.2 「1.3.1」のデータが0の場合輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を登録する。
            OAIW002InsertAiCargoInData1Dao insertAiCargoInData1Dao = new OAIW002InsertAiCargoInData1Dao();
            insertAiCargoInData1Dao.setAwbNo(awbNo);
            insertAiCargoInData1Dao.setArrFtl1(arrFtl1);
            insertAiCargoInData1Dao.setArrFtl2(arrFtl2);
            insertAiCargoInData1Dao.setBondedWarehouseCd(bondedWarehouseCd);
            insertAiCargoInData1Dao.setUserCd(userCd);
            insertAiCargoInData1Dao.setUpdateDate(now);
            int insertAiCargoInData1Cnt = this.insertAiCargoInData1(insertAiCargoInData1Dao);
            log.info("insertAiCargoInData1Cnt: " + insertAiCargoInData1Cnt);
            cargoOutDate = now;
        }
        log.info("cargoOutDate: " + cargoOutDate);

        int carringCount = 0;

        // 1.4 搬出用一時データから更新パラメータ１(輸入通関データ更新)を取得する。
        List<OAIW002UpdateAiData1Dao> updateAiData1DaoList = new ArrayList<OAIW002UpdateAiData1Dao>();
        for (OAIW002SelectAiData1Dto selectAiData1Dto : selectAiData1DtoList) {
            carringCount += 1;
            OAIW002UpdateAiData1Dao updateAiData1Dao = new OAIW002UpdateAiData1Dao();
            updateAiData1Dao.setAwbNo(selectAiData1Dto.getAwbNo());
            updateAiData1Dao.setCwbNo(selectAiData1Dto.getCwbNo());
            updateAiData1Dao.setCwbNoDeptCd(selectAiData1Dto.getCwbNoDeptCd());
            updateAiData1Dao.setCurrentCargoStatusCd("IB00710");
            updateAiData1Dao.setUserCd(userCd);
            updateAiData1Dao.setUpdateDate(now);
            updateAiData1DaoList.add(updateAiData1Dao);
        }

        // 1.5 搬出用一時データのデータから輸入搬出データ(AI_CARGO_OUT_DATA)を登録を取得する。
        List<OAIW002InsertAiCargoOutData1Dao> insertAiCargoOutData1DaoList = new ArrayList<OAIW002InsertAiCargoOutData1Dao>();
        for (OAIW002SelectAiData1Dto selectAiData1Dto : selectAiData1DtoList) {
            OAIW002InsertAiCargoOutData1Dao insertAiCargoOutData1Dao = new OAIW002InsertAiCargoOutData1Dao();
            insertAiCargoOutData1Dao.setBondedWarehouseCd(selectAiData1Dto.getBondedWarehouseCd());
            insertAiCargoOutData1Dao.setArrFtl1(selectAiData1Dto.getCurrentArrflt1());
            insertAiCargoOutData1Dao.setArrFtl2(selectAiData1Dto.getCurrentArrflt2());
            insertAiCargoOutData1Dao.setAwbNo(selectAiData1Dto.getAwbNo());
            insertAiCargoOutData1Dao.setCargoOutDate(cargoOutDate);
            insertAiCargoOutData1Dao.setCwbNo(selectAiData1Dto.getCwbNo());
            insertAiCargoOutData1Dao.setCwbNoDeptCd(selectAiData1Dto.getCwbNoDeptCd());
            insertAiCargoOutData1Dao.setReModelingFlg(selectAiData1Dto.getRemodelingFlg());
            insertAiCargoOutData1Dao.setBwbNo(selectAiData1Dto.getBwbNo());
            insertAiCargoOutData1Dao.setCargoInPiece(selectAiData1Dto.getCargoInPiece());
            insertAiCargoOutData1Dao.setCargoWeight(selectAiData1Dto.getCargoWeight());
            insertAiCargoOutData1Dao.setOutClassifyClassName(selectAiData1Dto.getOutClassifyClassName());
            insertAiCargoOutData1Dao.setSpecialPrepareName01(selectAiData1Dto.getSpecialPrepareName01());
            insertAiCargoOutData1Dao.setSpecialPrepareName02(selectAiData1Dto.getSpecialPrepareName02());
            insertAiCargoOutData1Dao.setSpecialPrepareName03(selectAiData1Dto.getSpecialPrepareName03());
            insertAiCargoOutData1Dao.setSpecialPrepareName04(selectAiData1Dto.getSpecialPrepareName04());
            insertAiCargoOutData1Dao.setSpecialPrepareName05(selectAiData1Dto.getSpecialPrepareName05());
            insertAiCargoOutData1Dao.setSpecialPrepareName06(selectAiData1Dto.getSpecialPrepareName06());
            insertAiCargoOutData1Dao.setSpecialPrepareName07(selectAiData1Dto.getSpecialPrepareName07());
            insertAiCargoOutData1Dao.setSpecialPrepareName08(selectAiData1Dto.getSpecialPrepareName08());
            insertAiCargoOutData1Dao.setSpecialPrepareName09(selectAiData1Dto.getSpecialPrepareName09());
            insertAiCargoOutData1Dao.setSpecialPrepareName10(selectAiData1Dto.getSpecialPrepareName10());
            insertAiCargoOutData1Dao.setUserCd(userCd);
            insertAiCargoOutData1Dao.setUpdateDate(now);
            insertAiCargoOutData1DaoList.add(insertAiCargoOutData1Dao);
        }
        log.info("insertAiCargoOutData1DaoList: " + insertAiCargoOutData1DaoList.toString());
        int insertAiCargoOutData1Cnt = this.insertAiCargoOutData1(insertAiCargoOutData1DaoList);
        log.info("insertAiCargoOutData1Cnt: " + insertAiCargoOutData1Cnt);

        // 1.6 登録した輸入搬出データ件数を取得する。
        OAIW002SelectAiCargoOutDataCnt1Dao selectAiCargoOutDataCnt1Dao = new OAIW002SelectAiCargoOutDataCnt1Dao();
        selectAiCargoOutDataCnt1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        selectAiCargoOutDataCnt1Dao.setCargoOutDate(cargoOutDate);
        selectAiCargoOutDataCnt1Dao.setAwbNo(awbNo);
        selectAiCargoOutDataCnt1Dao.setArrFtl1(arrFtl1);
        selectAiCargoOutDataCnt1Dao.setArrFtl2(arrFtl2);
        OAIW002SelectAiCargoOutDataCnt1Dto selectAiCargoOutDataCnt1Dto = this.selectAiCargoOutDataCnt1(selectAiCargoOutDataCnt1Dao);

        // 1.7 登録した輸入搬出データ件数が1件以上の場合、輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を更新する。
        if (selectAiCargoOutDataCnt1Dto != null) {
            OAIW002UpdateAiCargoOutHead1Dao updateAiCargoOutHead1Dao = new OAIW002UpdateAiCargoOutHead1Dao();
            updateAiCargoOutHead1Dao.setCwbCnt(selectAiCargoOutDataCnt1Dto.getCwbCnt());
            updateAiCargoOutHead1Dao.setDataPiece(selectAiCargoOutDataCnt1Dto.getDataPiece());
            updateAiCargoOutHead1Dao.setAwbNo(awbNo);
            updateAiCargoOutHead1Dao.setCargoOutDate(cargoOutDate);
            updateAiCargoOutHead1Dao.setBondedWarehouseCd(bondedWarehouseCd);
            updateAiCargoOutHead1Dao.setArrFtl1(arrFtl1);
            updateAiCargoOutHead1Dao.setArrFtl2(arrFtl2);
            updateAiCargoOutHead1Dao.setUserCd(userCd);
            updateAiCargoOutHead1Dao.setUpdateDate(now);
            int updateAiCargoOutHead1Cnt = this.updateAiCargoOutHead1(updateAiCargoOutHead1Dao);
            log.info("updateAiCargoOutHead1Cnt: " + updateAiCargoOutHead1Cnt);
        }

        // 1.8 更新パラメータ１(輸入通関データ更新)から輸入通関データ(AI_DATA)を更新する。
        int updateAiData1Cnt = 0;
        for (OAIW002UpdateAiData1Dao updateAiData1Dao : updateAiData1DaoList) {
            updateAiData1Cnt += this.updateAiData1(updateAiData1Dao);
        }
        log.info("updateAiData1Cnt : " + updateAiData1Cnt);

        // 1.9 搬出用一時データを輸入申告状況履歴(HISTORY)に登録する。
        List<OAIW002InsertAiStatusHistory1Dao> insertAiStatusHistory1DaoList = new ArrayList<OAIW002InsertAiStatusHistory1Dao>();
        for (OAIW002SelectAiData1Dto selectAiData1Dto : selectAiData1DtoList) {
            OAIW002InsertAiStatusHistory1Dao insertAiStatusHistory1Dao = new OAIW002InsertAiStatusHistory1Dao();
            insertAiStatusHistory1Dao.setAwbNo(selectAiData1Dto.getAwbNo());
            insertAiStatusHistory1Dao.setBwbNo(selectAiData1Dto.getBwbNo());
            insertAiStatusHistory1Dao.setCwbNo(selectAiData1Dto.getCwbNo());
            insertAiStatusHistory1Dao.setCwbNoDeptCd(selectAiData1Dto.getCwbNoDeptCd());
            insertAiStatusHistory1Dao.setUserCd(userCd);
            insertAiStatusHistory1Dao.setUpdateDate(now);
            insertAiStatusHistory1DaoList.add(insertAiStatusHistory1Dao);
        }
        int insertAiStatusHistory1Cnt = this.insertAiStatusHistory1(insertAiStatusHistory1DaoList);
        log.info("insertAiStatusHistory1Cnt : " + insertAiStatusHistory1Cnt);

        switch (param2.get_columnId()) {
            case COLID_EMERGENCY:
                // 許可
                int emergency1 = Integer.parseInt(param2.getEmergency()) - carringCount;
                param2.setEmergency(Integer.toString(emergency1));
                //搬出
                int emergency2 = Integer.parseInt(param3.getEmergency()) + carringCount;
                param3.setEmergency(Integer.toString(emergency2));
                break;
            case COLID_CLAIM:
                int claim1 = Integer.parseInt(param2.getClaim()) - carringCount;
                param2.setClaim(Integer.toString(claim1));
                //搬出
                int claim2 = Integer.parseInt(param3.getClaim()) + carringCount;
                param3.setClaim(Integer.toString(claim2));
                break;
            case COLID_DUTYFREE:
                int dutyFree1 = Integer.parseInt(param2.getDutyFree()) - carringCount;
                param2.setDutyFree(Integer.toString(dutyFree1));
                //搬出
                int dutyFree2 = Integer.parseInt(param3.getDutyFree()) + carringCount;
                param3.setDutyFree(Integer.toString(dutyFree2));
                break;
            case COLID_MANIFEST:
                int manifest1 = Integer.parseInt(param2.getManifest()) - carringCount;
                param2.setManifest(Integer.toString(manifest1));
                //搬出
                int manifest2 = Integer.parseInt(param3.getManifest()) + carringCount;
                param3.setManifest(Integer.toString(manifest2));
                break;
            default:
                // do nothing
                break;
        }

        //許可行のcwbカウント更新
        int cwbCount1 = Integer.parseInt(param2.getEmergency())
                + Integer.parseInt(param2.getClaim())
                + Integer.parseInt(param2.getDutyFree())
                + Integer.parseInt(param2.getManifest());
        param2.setAwbCnt(Integer.toString(cwbCount1));

        // 搬出行のcwbカウント更新
        int cwbCount2 = Integer.parseInt(param3.getEmergency())
                + Integer.parseInt(param3.getClaim())
                + Integer.parseInt(param3.getDutyFree())
                + Integer.parseInt(param3.getManifest());
        param3.setAwbCnt(Integer.toString(cwbCount2));

        // 戻り値設定
        doPermitDto.setParam2(param2);
        doPermitDto.setParam3(param3);
        resultDto.setResult(true);
        resultDto.setMessage("許可処理が正常終了しました。");
        resultDto.setPermitDto(doPermitDto);
        return resultDto;
    }

    @Transactional(rollbackFor = Exception.class)
    public OAIW002DoBondedOutResultDto doBondedOutBusiness(HttpHeaders headers,
                                                           OAIW002DoBondedOutDto doBondedOutDto) throws Exception {
        OAIW002DoBondedOutResultDto resultDto = new OAIW002DoBondedOutResultDto();
        final OAIW002SelectBondedOutListDao param1 = doBondedOutDto.getParam1(); // 検索条件
        final OAIW002SearchResultDto param2 = doBondedOutDto.getParam2(); // 許可行のRowData
        final OAIW002SearchResultDto param3 = doBondedOutDto.getParam3(); // 搬出行のRowData

        final String accessToken = (String)headers.get("authorization").get(0);
        final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        String inClassifyClassName = "";
        String carryOutDate = param1.getCarryOutDate();
        String cargoOutDate = "";
        final String now = getTimeStampNow();
        final String awbNo = param3.get_awbNo();
        final String arrFtl1 = param3.get_arrFtl1();
        final String arrFtl2 = param3.get_arrFtl2();
        final String bondedWarehouseCd = param1.getBondedWarehouseCd();

        switch (param3.get_columnId()) {
            case COLID_EMERGENCY:
                inClassifyClassName = "緊急";
                break;
            case COLID_CLAIM:
                inClassifyClassName = "請求";
                break;
            case COLID_DUTYFREE:
                inClassifyClassName = "無税";
                break;
            case COLID_MANIFEST:
                inClassifyClassName = "マニ";
                break;
            default:
                // do nothing
                break;
        }
        log.info("inClassifyClassName: " + inClassifyClassName);

        // 1.2.1 搬出対象データ1を取得
        OAIW002SelectAiCargoOutData1Dao selectAiCargoOutData1Dao = new OAIW002SelectAiCargoOutData1Dao();
        selectAiCargoOutData1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        selectAiCargoOutData1Dao.setArrFtl1(arrFtl1);
        selectAiCargoOutData1Dao.setArrFtl2(arrFtl2);
        selectAiCargoOutData1Dao.setAwbNo(awbNo);
        selectAiCargoOutData1Dao.setCarryOutDate(carryOutDate);
        selectAiCargoOutData1Dao.setInClassifyClassName(inClassifyClassName);
        selectAiCargoOutData1Dao.setUnMatchHawbCntFlg(param1.isUnMatchHawbCntFlg());
        selectAiCargoOutData1Dao.setUnMatchPieceFlg(param1.isUnMatchPieceFlg());
        selectAiCargoOutData1Dao.setMatchFlg(param1.isMatchFlg());
        List<OAIW002SelectAiCargoOutData1Dto> selectAiCargoOutData1DtoList = this.selectAiCargoOutData1(selectAiCargoOutData1Dao);

        // 1.2.2 「1.2.1」取得したデータ件数が0の場合、搬出処理を終了する。
        if (selectAiCargoOutData1DtoList == null || selectAiCargoOutData1DtoList.size() == 0) {
            String errMsg = "輸入搬出データ(AI_CARGO_OUT_DATA)がありません。";
            log.error(errMsg+"selectAiCargoOutData1Dao: " + selectAiCargoOutData1Dao.toString());
            throw new Exception(errMsg);
        }

        cargoOutDate = selectAiCargoOutData1DtoList.get(0).getCargoOutDate();

        // 1.3.2 輸入申告状況履歴（HISTORY）を削除する。
        // AI_STATUS_HISTORYから対象データの搬出開始(IB00710)ステータスを削除
        OAIW002DeleteAiStatusHistory1Dao deleteAiStatusHistory1Dao = new OAIW002DeleteAiStatusHistory1Dao();
        deleteAiStatusHistory1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        deleteAiStatusHistory1Dao.setArrFtl1(arrFtl1);
        deleteAiStatusHistory1Dao.setArrFtl2(arrFtl2);
        deleteAiStatusHistory1Dao.setAwbNo(awbNo);
        deleteAiStatusHistory1Dao.setInClassifyClassName(inClassifyClassName);
        deleteAiStatusHistory1Dao.setCarryOutDate(carryOutDate);
        deleteAiStatusHistory1Dao.setUnMatchHawbCntFlg(param1.isUnMatchHawbCntFlg());
        deleteAiStatusHistory1Dao.setUnMatchPieceFlg(param1.isUnMatchPieceFlg());
        deleteAiStatusHistory1Dao.setMatchFlg(param1.isMatchFlg());

        int deleteAiStatusHistory1Cnt = this.deleteAiStatusHistory1(deleteAiStatusHistory1Dao);
        log.info("deleteAiStatusHistory1Cnt: " + deleteAiStatusHistory1Cnt);
        if (!(deleteAiStatusHistory1Cnt > 0)) {
            String errMsg = "削除対象の輸入申告状況履歴データ( AI_STATUS_HISTORY)がありません。";
            log.error(errMsg+"deleteAiStatusHistory1Dao: " + deleteAiStatusHistory1Dao.toString());
            throw new Exception(errMsg);
        }

        // 1.3.3 輸入搬入データ（AI_CARGO_OUT_DATA）を削除する。
        // 搬出チェックデータから対象データを削除
        OAIW002DeleteAiCargoOutData1Dao deleteAiCargoOutData1Dao = new OAIW002DeleteAiCargoOutData1Dao();
        deleteAiCargoOutData1Dao.setInClassifyClassName(inClassifyClassName);
        deleteAiCargoOutData1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        deleteAiCargoOutData1Dao.setArrFtl1(arrFtl1);
        deleteAiCargoOutData1Dao.setArrFtl2(arrFtl2);
        deleteAiCargoOutData1Dao.setAwbNo(awbNo);
        deleteAiCargoOutData1Dao.setCarryOutDate(carryOutDate);
        deleteAiCargoOutData1Dao.setUnMatchHawbCntFlg(param1.isUnMatchHawbCntFlg());
        deleteAiCargoOutData1Dao.setUnMatchPieceFlg(param1.isUnMatchPieceFlg());
        deleteAiCargoOutData1Dao.setMatchFlg(param1.isMatchFlg());
        int deleteAiCargoOutData1Cnt = this.deleteAiCargoOutData1(deleteAiCargoOutData1Dao);
        log.info("deleteAiCargoOutData1Cnt: " + deleteAiCargoOutData1Cnt);

        if (!(deleteAiCargoOutData1Cnt > 0)) {
            String errMsg = "削除対象の輸入搬出データ(AI_CARGO_OUT_DATA)がありません。";
            log.error(errMsg+"deleteAiCargoOutData1Dao: " + deleteAiCargoOutData1Dao.toString());
            throw new Exception(errMsg);
        }

        int carringCount = 0;
        // 1.4 搬出対象データ1から更新パラメータ1(輸入通関データ)を取得する。
        List<OAIW002UpdateAiData2Dao> updateAiData2DaoList = new ArrayList<OAIW002UpdateAiData2Dao>();
        for (OAIW002SelectAiCargoOutData1Dto aiCargoOutData1Dto : selectAiCargoOutData1DtoList) {
            OAIW002UpdateAiData2Dao updateAiData2Dao = new OAIW002UpdateAiData2Dao();
            updateAiData2Dao.setAwbNo(aiCargoOutData1Dto.getAwbNo());
            updateAiData2Dao.setCwbNo(aiCargoOutData1Dto.getCwbNo());
            updateAiData2Dao.setCwbNoDeptCd(aiCargoOutData1Dto.getCwbNoDeptCd());
            updateAiData2Dao.setCurrentCargoStatusCd("IB00600"); // 許可
            updateAiData2Dao.setUserCd(userCd);
            updateAiData2Dao.setUpdateDate(now);
            updateAiData2DaoList.add(updateAiData2Dao);
            carringCount += 1;
        }

        // 1.5 搬出対象データ2を取得する。
        OAIW002SelectAiCargoOutDataCnt1Dao selectAiCargoOutDataCnt1Dao = new OAIW002SelectAiCargoOutDataCnt1Dao();
        selectAiCargoOutDataCnt1Dao.setBondedWarehouseCd(bondedWarehouseCd);
        selectAiCargoOutDataCnt1Dao.setCargoOutDate(cargoOutDate);
        selectAiCargoOutDataCnt1Dao.setAwbNo(awbNo);
        selectAiCargoOutDataCnt1Dao.setArrFtl1(arrFtl1);
        selectAiCargoOutDataCnt1Dao.setArrFtl2(arrFtl2);
        OAIW002SelectAiCargoOutDataCnt1Dto selectAiCargoOutDataCnt1Dto = this.selectAiCargoOutDataCnt1(selectAiCargoOutDataCnt1Dao);

        // 1.6 搬出対象データ2の件数が1以上の場合、輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を更新する。
        if (selectAiCargoOutDataCnt1Dto != null) {
            OAIW002UpdateAiCargoOutHead2Dao updateAiCargoOutHead2Dao = new OAIW002UpdateAiCargoOutHead2Dao();
            updateAiCargoOutHead2Dao.setCargoOutDate(cargoOutDate);
            updateAiCargoOutHead2Dao.setCwbCnt(selectAiCargoOutDataCnt1Dto.getCwbCnt());
            updateAiCargoOutHead2Dao.setDataPiece(selectAiCargoOutDataCnt1Dto.getDataPiece());
            updateAiCargoOutHead2Dao.setBondedWarehouseCd(bondedWarehouseCd);
            updateAiCargoOutHead2Dao.setArrFtl1(arrFtl1);
            updateAiCargoOutHead2Dao.setArrFtl2(arrFtl2);
            updateAiCargoOutHead2Dao.setAwbNo(awbNo);
            updateAiCargoOutHead2Dao.setUserCd(userCd);
            updateAiCargoOutHead2Dao.setUpdateDate(now);
            int updateAiCargoOutHead2Cnt = this.updateAiCargoOutHead2(updateAiCargoOutHead2Dao);
            log.info("updateAiCargoOutHead2: " + updateAiCargoOutHead2Cnt);

        } else {
            // 1.7 搬出対象データ2の件数が0の場合、輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を削除
            // 1.7.1 輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)データを取得する。
            OAIW002SelectAiCargoOutHead2Dao selectAiCargoOutHead2Dao = new OAIW002SelectAiCargoOutHead2Dao();
            selectAiCargoOutHead2Dao.setArrFtl1(arrFtl1);
            selectAiCargoOutHead2Dao.setArrFtl2(arrFtl2);
            selectAiCargoOutHead2Dao.setBondedWarehouseCd(bondedWarehouseCd);
            selectAiCargoOutHead2Dao.setCargoOutDate(cargoOutDate);
            selectAiCargoOutHead2Dao.setAwbNo(awbNo);
            OAIW002SelectAiCargoOutHead2Dto selectAiCargoOutHead2Dto = this.selectAiCargoOutHead2(selectAiCargoOutHead2Dao);

            // 1.7.2「1.7.1」で取得したデータ件数が1以上の場合、輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を削除する。
            if (selectAiCargoOutHead2Dto != null) {
                OAIW002DeleteAiCargoOutHead1Dao deleteAiCargoOutHead1Dao = new OAIW002DeleteAiCargoOutHead1Dao();
                deleteAiCargoOutHead1Dao.setCargoOutDate(cargoOutDate);
                deleteAiCargoOutHead1Dao.setBondedWarehouseCd(bondedWarehouseCd);
                deleteAiCargoOutHead1Dao.setAwbNo(awbNo);
                deleteAiCargoOutHead1Dao.setArrFtl1(arrFtl1);
                deleteAiCargoOutHead1Dao.setArrFtl2(arrFtl2);
                int deleteAiCargoOutHeadCnt1 = this.deleteAiCargoOutHead1(deleteAiCargoOutHead1Dao);
                log.info("deleteAiCargoOutHeadCnt1: " + deleteAiCargoOutHeadCnt1);
            }
        }
        // AI_DATA カレント貨物ステータスUPDATE
        int updateAiData2Cnt = 0;
        for (OAIW002UpdateAiData2Dao updateAiData2Dao : updateAiData2DaoList) {
            updateAiData2Cnt += this.updateAiData2(updateAiData2Dao);
        }
        log.info("updateAiData2Cnt: " + updateAiData2Cnt);

        switch (param3.get_columnId()) {
            case COLID_EMERGENCY:
                // 許可
                int emergency1 = Integer.parseInt(param2.getEmergency()) + carringCount;
                param2.setEmergency(Integer.toString(emergency1));
                //搬出
                int emergency2 = Integer.parseInt(param3.getEmergency()) - carringCount;
                param3.setEmergency(Integer.toString(emergency2));
                break;
            case COLID_CLAIM:
                int claim1 = Integer.parseInt(param2.getClaim()) + carringCount;
                param2.setClaim(Integer.toString(claim1));
                //搬出
                int claim2 = Integer.parseInt(param3.getClaim()) - carringCount;
                param3.setClaim(Integer.toString(claim2));
                break;
            case COLID_DUTYFREE:
                int dutyFree1 = Integer.parseInt(param2.getDutyFree()) + carringCount;
                param2.setDutyFree(Integer.toString(dutyFree1));
                //搬出
                int dutyFree2 = Integer.parseInt(param3.getDutyFree()) - carringCount;
                param3.setDutyFree(Integer.toString(dutyFree2));
                break;
            case COLID_MANIFEST:
                int manifest1 = Integer.parseInt(param2.getManifest()) + carringCount;
                param2.setManifest(Integer.toString(manifest1));
                //搬出
                int manifest2 = Integer.parseInt(param3.getManifest()) - carringCount;
                param3.setManifest(Integer.toString(manifest2));
                break;
            default:
                // do nothing
                break;
        }

        //許可行のcwbカウント更新
        int cwbCount1 = Integer.parseInt(param2.getEmergency())
                + Integer.parseInt(param2.getClaim())
                + Integer.parseInt(param2.getDutyFree())
                + Integer.parseInt(param2.getManifest());
        param2.setAwbCnt(Integer.toString(cwbCount1));

        // 搬出行のcwbカウント更新
        int cwbCount2 = Integer.parseInt(param3.getEmergency())
                + Integer.parseInt(param3.getClaim())
                + Integer.parseInt(param3.getDutyFree())
                + Integer.parseInt(param3.getManifest());
        param3.setAwbCnt(Integer.toString(cwbCount2));

        // 戻り値設定
        doBondedOutDto.setParam2(param2);
        doBondedOutDto.setParam3(param3);
        resultDto.setResult(true);
        resultDto.setMessage("搬出中処理(戻し処理)が正常終了しました。");
        resultDto.setBondedOutDto(doBondedOutDto);
        return resultDto;
    }

    // 業務処理(本体)
    @Transactional(rollbackFor = Exception.class)
    public OAIW002DoOutBusinessDto doOutBusiness(String userCd, String now,
                                                 OAIW002SelectBondedOutListDao param,
                                                 List<OAIW002SearchResultDto> gridDataList,
                                                 OAIW002SelectBondedOutListDao reSearchParam) throws Exception {

        OAIW002DoOutBusinessDto doOutBusinessDto = new OAIW002DoOutBusinessDto();
        int awbNoCnt = 0;
        int outFileCnt = 0;
        final String bondedWarehouseCd = param.getBondedWarehouseCd();
        final String carryOutDate = param.getCarryOutDate();

        // 電文格納用パスの指定
        OAIW002OutFilePathDto OAIW002OutFilePathDto = new OAIW002OutFilePathDto();
        OAIW002OutFilePathDto.setNameClass("0051");
        // SimGate自動送信用パス
        OAIW002OutFilePathDto.setNameCd("SIMAUTO");
        List<OAIW002OutFilePathDao> OutFilePathList = this.OutFilePathList(OAIW002OutFilePathDto);
        String outSendDir = OutFilePathList.get(0).getCHARACTERITEM1().toString();
        log.info("OUT電文 SimGate自動送信用パス : " + outSendDir);

        // SimGate自動送信Backup用パス
        OAIW002OutFilePathDto.setNameCd("SIMAUTOBK");
        List<OAIW002OutFilePathDao> OutFileBkPathList = this.OutFilePathList(OAIW002OutFilePathDto);
        String outBackupDir = OutFileBkPathList.get(0).getCHARACTERITEM1().toString();
        log.info("OUT電文 SimGate自動送信Backupパス : " + outBackupDir);

        List<OAIW002SelectAiCargoOutData2Dto> selectAiCargoOutData2DtoList = new ArrayList<OAIW002SelectAiCargoOutData2Dto>();

        for (OAIW002SearchResultDto dataRow : gridDataList) {
            // 1行目のみ処理対象
            if(ROW_TYPE_TOP.equals(dataRow.getRowType())) {
                if (dataRow.isSelect()) {
                    final String awbNo = dataRow.get_awbNo();
                    // 2.1.2 OUT作成対象データを取得する。
                    OAIW002SelectAiCargoOutData2Dao selectAiCargoOutData2Dao = new OAIW002SelectAiCargoOutData2Dao();
                    selectAiCargoOutData2Dao.setAwbNo(awbNo);
                    selectAiCargoOutData2Dao.setBondedWarehouseCd(bondedWarehouseCd);
                    List<OAIW002SelectAiCargoOutData2Dto> _selectAiCargoOutData2DtoList = this.selectAiCargoOutData2(selectAiCargoOutData2Dao);
                    if(_selectAiCargoOutData2DtoList != null && _selectAiCargoOutData2DtoList.size() > 0 ) {
                        selectAiCargoOutData2DtoList.addAll(_selectAiCargoOutData2DtoList);
                    }
                }
            }
        }

        List<OAIW002OutTelegramDto> outTelegramDtoList = new ArrayList<OAIW002OutTelegramDto>();
        List<OAIW002SelectAiCargoOutHead3Dao> selectAiCargoOutHead3DaoList = new ArrayList<>();
        if(selectAiCargoOutData2DtoList.size() > 0) {
            for (OAIW002SearchResultDto dataRow : gridDataList) {
                if(ROW_TYPE_TOP.equals(dataRow.getRowType())) {
                    if (dataRow.isSelect()) {
                        final String awbNo = dataRow.get_awbNo();

                        // 2.1.3 OUT作成対象搬出チェックデータを削除する。
                        // OUT作成対象搬出チェックデータDELETE
                        int deleteAiCargoOutData2Cnt = 0;
                        OAIW002DeleteAiCargoOutData2Dao deleteAiCargoOutData2Dao = new OAIW002DeleteAiCargoOutData2Dao();
                        deleteAiCargoOutData2Dao.setAwbNo(awbNo);
                        deleteAiCargoOutData2Dao.setBondedWarehouseCd(bondedWarehouseCd);
                        deleteAiCargoOutData2Cnt = this.deleteAiCargoOutData2(deleteAiCargoOutData2Dao);
                        log.info("deleteAiCargoOutData2Cnt: " + deleteAiCargoOutData2Dao.toString()+ " " +deleteAiCargoOutData2Cnt);
                    }
                }
            }
            for(OAIW002SelectAiCargoOutData2Dto selectAiCargoOutData2Dto : selectAiCargoOutData2DtoList) {
                // 2.1.4 OUT作成対象データから検索パラメータ１、検索パラメータ2を取得する。
                // 検索パラメータ１
                OAIW002SelectAiCargoOutHead3Dao selectAiCargoOutHead3Dao = new OAIW002SelectAiCargoOutHead3Dao();
                selectAiCargoOutHead3Dao.setBondedWarehouseCd(selectAiCargoOutData2Dto.getBondedWarehouseCd());
                selectAiCargoOutHead3Dao.setArrFtl1(selectAiCargoOutData2Dto.getFtl1());
                selectAiCargoOutHead3Dao.setArrFtl2(selectAiCargoOutData2Dto.getFtl2());
                selectAiCargoOutHead3Dao.setAwbNo(selectAiCargoOutData2Dto.getAwbNo());
                selectAiCargoOutHead3Dao.setCargoOutDate(selectAiCargoOutData2Dto.getCargoOutDate());
                selectAiCargoOutHead3DaoList.add(selectAiCargoOutHead3Dao);

                // 検索パラメータ2
                OAIW002SelectAiData3Dao selectAiData3Dao = new OAIW002SelectAiData3Dao();
                selectAiData3Dao.setAwbNo(selectAiCargoOutData2Dto.getAwbNo());
                selectAiData3Dao.setCwbNo(selectAiCargoOutData2Dto.getCwbNo());
                selectAiData3Dao.setCwbNoDeptCd(selectAiCargoOutData2Dto.getCwbNoDeptCd());

                // 2.1.5 検索パラメータ2(輸入データSELECT条件)に対して輸入搬出データを取得(AI_CARGO_OUT_DATA)する。
                List<OAIW002SelectAiData3Dto> selectAiData3DtoList = this.selectAiData3(selectAiData3Dao);

                //2.1.6 「2.1.5」輸入搬出データからOUT電文作成パラメータを作成する。
                if (selectAiData3DtoList != null && selectAiData3DtoList.size() > 0) {
                    OAIW002SelectAiData3Dto selectAiData3Dto = selectAiData3DtoList.get(0);
                    OAIW002OutTelegramDto outTelegramDto = new OAIW002OutTelegramDto();
                    outTelegramDto.setAwbNo(selectAiData3Dto.getAwbNo());
                    outTelegramDto.setCwbNo(selectAiData3Dto.getCwbNo());
                    outTelegramDto.setBwbNo(selectAiData3Dto.getBwbNo());
                    outTelegramDto.setCwbNoDeptCd(selectAiData3Dto.getCwbNoDeptCd());
                    outTelegramDto.setCarryOutDate(carryOutDate);
                    outTelegramDto.setCollectTrader(selectAiData3Dto.getCollectTrader());
                    outTelegramDto.setOtherWareHouse(selectAiData3Dto.getOtherWareHouse());
                    outTelegramDto.setCargoOutPiece(selectAiData3Dto.getCargoOutPiece());
                    outTelegramDto.setDataPiece(selectAiCargoOutData2Dto.getDataPiece());
                    outTelegramDto.setCustomPlaceCd(selectAiData3Dto.getCustomsPlaceCd());
                    outTelegramDto.setBondedWarehouseCd(selectAiData3Dto.getBondedWarehouseCd());
                    outTelegramDto.setCurrentCargoStatusCd(selectAiData3Dto.getCurrentCargoStatusCd());

                    int lenCwbNo = selectAiData3Dto.getCwbNo().length();
                    String _cwbNo = selectAiData3Dto.getCwbNo();
                    if (lenCwbNo > 1) {
                        outTelegramDto.setSortCwbNo(_cwbNo.substring(lenCwbNo - 1, lenCwbNo));
                        outTelegramDto.setSortCwbNo2(_cwbNo.substring(lenCwbNo - 2, lenCwbNo - 1));
                    }
                    outTelegramDtoList.add(outTelegramDto);
                }
            }
        }

        // 2.1.6.2 OUT電文作成パラメータを下記の順でソートする。
        // ソート順：CARRYOUTDATE, COLLECTTRADER, OTHERWAREHOUSE, SORTCWBNO, SORTCWBNO2, CWBNO, CWBNODEPTCD
        Collections.sort(
                outTelegramDtoList,
                new Comparator<OAIW002OutTelegramDto>() {
                    @Override
                    public int compare(OAIW002OutTelegramDto obj1, OAIW002OutTelegramDto obj2) {
                        List<String> obj1SortKeyList = new ArrayList<String>(
                                Arrays.asList(obj1.getCarryOutDate() != null ? obj1.getCarryOutDate() : "",
                                        obj1.getCollectTrader() != null ? obj1.getCollectTrader() : "",
                                        obj1.getOtherWareHouse() != null ? obj1.getOtherWareHouse() : "",
                                        obj1.getSortCwbNo() != null ? obj1.getSortCwbNo() : "",
                                        obj1.getSortCwbNo2() != null ? obj1.getSortCwbNo2() : "",
                                        obj1.getCwbNo() != null ? obj1.getCwbNo() : "",
                                        obj1.getCwbNoDeptCd() != null ? obj1.getCwbNoDeptCd() : ""));

                        List<String> obj2SortKeyList = new ArrayList<String>(
                                Arrays.asList(obj2.getCarryOutDate() != null ? obj2.getCarryOutDate() : "",
                                        obj2.getCollectTrader() != null ? obj2.getCollectTrader() : "",
                                        obj2.getOtherWareHouse() != null ? obj2.getOtherWareHouse() : "",
                                        obj2.getSortCwbNo() != null ? obj2.getSortCwbNo() : "",
                                        obj2.getSortCwbNo2() != null ? obj2.getSortCwbNo2() : "",
                                        obj2.getCwbNo() != null ? obj2.getCwbNo() : "",
                                        obj2.getCwbNoDeptCd() != null ? obj2.getCwbNoDeptCd() : ""));

                        int result = 0;
                        for (int i = 0; i < obj1SortKeyList.size(); i++) {
                            result = obj1SortKeyList.get(i).compareTo(obj2SortKeyList.get(i));
                            if (result == 0) {
                                continue;
                            } else {
                                return result;
                            }
                        }
                        return result;
                    }
                }
        );
        log.info("outTelegramDtoList:" + outTelegramDtoList.toString());
        // 2.1.7OUT電文作成パラメータから更新パラメータ１(輸入データカレント貨物ステータス)を作成する。
        // 更新パラメータ１:updateAiData3DaoList
        List<OAIW002UpdateAiData3Dao> updateAiData3DaoList = new ArrayList<OAIW002UpdateAiData3Dao>();
        for(OAIW002OutTelegramDto telegramData : outTelegramDtoList) { // dt
            OAIW002UpdateAiData3Dao updateAiData3Dao = new OAIW002UpdateAiData3Dao();
            updateAiData3Dao.setAwbNo(telegramData.getAwbNo());
            updateAiData3Dao.setCwbNo(telegramData.getCwbNo());
            updateAiData3Dao.setCwbNoDeptCd(telegramData.getCwbNoDeptCd());

            String statusCd = telegramData.getCurrentCargoStatusCd().charAt(0) == 'D' ?telegramData.getCurrentCargoStatusCd() : "IB00800";
            updateAiData3Dao.setCurrentCargoStatusCd(statusCd);
            updateAiData3Dao.setCarryOutDate(carryOutDate);

            // set CargoOutPiece
            if(telegramData.getCargoOutPiece()  != null) {
                int cargoOutPiece = 0;
                try {
                    cargoOutPiece = Integer.parseInt(telegramData.getCargoOutPiece());
                } catch (Exception e) {
                    String errMsg = "OUT電文データのCargoOutPieceに不正な値がセットされています。";
                    log.error(errMsg + " error data;" + telegramData.toString());
                    throw new Exception(errMsg);
                }
                updateAiData3Dao.setCargoOutPiece(String.valueOf(cargoOutPiece));
            }

            // set DataPiece
            if(telegramData.getDataPiece()  != null) {
                int cargoOutPiece = 0;
                int dataPiece = 0;
                try { cargoOutPiece = Integer.parseInt(updateAiData3Dao.getCargoOutPiece()); } catch (Exception e) { /* do nothing */ }
                try {
                    dataPiece = Integer.parseInt(telegramData.getDataPiece());
                } catch (Exception e) {
                    String errMsg = "OUT電文データのdataPieceに不正な値がセットされています。";
                    log.error(errMsg + " error data;" + telegramData.toString());
                    throw new Exception(errMsg);
                }
                updateAiData3Dao.setCargoOutPiece(String.valueOf(cargoOutPiece + dataPiece));
            }
            updateAiData3Dao.setUserCd(userCd);
            updateAiData3Dao.setUpdateDate(now);
            updateAiData3DaoList.add(updateAiData3Dao);
        }

        if(outTelegramDtoList.size() > 0) {
            // 2.1.8.1 検索パラメータ１(輸入搬出データヘッダ検索・更新時に使用)に対して

            // 削除する輸入搬出データのキーで搬出ヘッダを読み，再計算対象のヘッダを取得する。
            // ① 輸入搬出ヘデータッダ情報取得
            // ➁ 輸入搬出ヘデータッダ情報取得
            for (OAIW002SelectAiCargoOutHead3Dao selectAiCargoOutHead3Dao : selectAiCargoOutHead3DaoList) {
                OAIW002SelectAiCargoOutHead3Dto selectAiCargoOutHead3Dto = this.selectAiCargoOutHead3(selectAiCargoOutHead3Dao);
                log.info("selectAiCargoOutHead3Dao: " + selectAiCargoOutHead3Dao.toString());

                // 2.1.8.2 「2.1.7.1」の輸入搬出ヘデータッダ情報取得から輸入搬出データヘッダの再計算を行う。
                // ① 搬出チェックデータの件数合計を取得する。
                if (selectAiCargoOutHead3Dto != null) {
                    OAIW002SelectAiCargoOutDataCnt1Dao selectAiCargoOutDataCnt1Dao = new OAIW002SelectAiCargoOutDataCnt1Dao();
                    selectAiCargoOutDataCnt1Dao.setBondedWarehouseCd(selectAiCargoOutHead3Dto.getBondedWarehouseCd());
                    selectAiCargoOutDataCnt1Dao.setCargoOutDate(selectAiCargoOutHead3Dto.getCargoOutDate());
                    selectAiCargoOutDataCnt1Dao.setAwbNo(selectAiCargoOutHead3Dto.getAwbNo());
                    selectAiCargoOutDataCnt1Dao.setArrFtl1(selectAiCargoOutHead3Dto.getFtl1());
                    selectAiCargoOutDataCnt1Dao.setArrFtl2(selectAiCargoOutHead3Dto.getFtl2());
                    OAIW002SelectAiCargoOutDataCnt1Dto selectAiCargoOutDataCnt1Dto = this.selectAiCargoOutDataCnt1(selectAiCargoOutDataCnt1Dao);

                    // ➁ ①の件数が１以上の場合、更新パラメータ2(輸入搬出データヘッダ更新)を作成する。
                    // 更新パラメータ2:updateAiCargoOutHead4Dao
                    OAIW002UpdateAiCargoOutHead4Dao updateAiCargoOutHead4Dao = null;
                    // 削除パラメータ１:deleteAiCargoOutHead2Dao
                    OAIW002DeleteAiCargoOutHead2Dao deleteAiCargoOutHead2Dao = null;

                    if (selectAiCargoOutDataCnt1Dto != null) {
                        updateAiCargoOutHead4Dao = new OAIW002UpdateAiCargoOutHead4Dao();
                        updateAiCargoOutHead4Dao.setBondedWarehouseCd(selectAiCargoOutHead3Dto.getBondedWarehouseCd());
                        updateAiCargoOutHead4Dao.setArrFtl1(selectAiCargoOutHead3Dto.getFtl1());
                        updateAiCargoOutHead4Dao.setArrFtl2(selectAiCargoOutHead3Dto.getFtl2());
                        updateAiCargoOutHead4Dao.setAwbNo(selectAiCargoOutHead3Dto.getAwbNo());
                        updateAiCargoOutHead4Dao.setCargoOutDate(selectAiCargoOutHead3Dto.getCargoOutDate());
                        updateAiCargoOutHead4Dao.setCwbCount(selectAiCargoOutDataCnt1Dto.getCwbCnt());
                        updateAiCargoOutHead4Dao.setDataPiece(selectAiCargoOutDataCnt1Dto.getDataPiece());
                        updateAiCargoOutHead4Dao.setUserCd(userCd);
                        updateAiCargoOutHead4Dao.setUpdateDate(now);
                    } else {
                        // ➂ ①の件数が0の場合、削除パラメータ１(輸入搬出データヘッダ削除)を作成する。
                        deleteAiCargoOutHead2Dao = new OAIW002DeleteAiCargoOutHead2Dao();
                        deleteAiCargoOutHead2Dao.setBondedWarehouseCd(selectAiCargoOutHead3Dto.getBondedWarehouseCd());
                        deleteAiCargoOutHead2Dao.setArrFtl1(selectAiCargoOutHead3Dto.getFtl1());
                        deleteAiCargoOutHead2Dao.setArrFtl2(selectAiCargoOutHead3Dto.getFtl2());
                        deleteAiCargoOutHead2Dao.setAwbNo(selectAiCargoOutHead3Dto.getAwbNo());
                        deleteAiCargoOutHead2Dao.setCargoOutDate(selectAiCargoOutHead3Dto.getCargoOutDate());
                    }

                    // 2.1.8.3 更新パラメータ2(輸入搬出データヘッダ更新)に対して輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)を更新する。
                    if (updateAiCargoOutHead4Dao != null) {
                        int updateAiCargoOutHead4Cnt = this.updateAiCargoOutHead4(updateAiCargoOutHead4Dao);
                        log.info("updateAiCargoOutHead4Cnt:" + updateAiCargoOutHead4Cnt);
                    }

                    // 2.1.8.4 削除パラメータ１(輸入搬出データヘッダ削除)に対して輸入搬出データヘッダ(AI_CARGO_OUT_HEAD)データを削除する。
                    if (deleteAiCargoOutHead2Dao != null) {
                        int deleteAiCargoOutHead2Cnt = this.deleteAiCargoOutHead2(deleteAiCargoOutHead2Dao);
                        log.info("deleteAiCargoOutHead2Cnt:" + deleteAiCargoOutHead2Cnt);
                    }
                }
            }

            // 2.1.8.5 更新パラメータ１(輸入データカレント貨物ステータス)に対して輸入通関データ(AI_DATA)を更新する。
            // 更新パラメータ１:updateAiData3DaoList(List<OAIW002UpdateAiData3Dao>)
            int updateAiData3Cnt = 0;
            for (OAIW002UpdateAiData3Dao updateAiData3Dao : updateAiData3DaoList) {
                updateAiData3Cnt += this.updateAiData3(updateAiData3Dao);
            }
            log.info("updateAiData3Cnt: " + updateAiData3Cnt);

            // 2.1.8.6 OUT電文作成パラメータに対して輸入申告状況履歴(HISTORY)を登録する。
            // OUT電文作成パラメータ:outTelegramDtoList(List<OAIW002OutTelegramDto>)
            List<OAIW002InsertAiStatusHistory3Dao> insertAiStatusHistory3DaoList = new ArrayList<OAIW002InsertAiStatusHistory3Dao>();
            for (OAIW002OutTelegramDto telegramDto : outTelegramDtoList) {
                OAIW002InsertAiStatusHistory3Dao insertAiStatusHistory3Dao = new OAIW002InsertAiStatusHistory3Dao();
                insertAiStatusHistory3Dao.setAwbNo(telegramDto.getAwbNo());
                insertAiStatusHistory3Dao.setBwbNo(telegramDto.getBwbNo());
                insertAiStatusHistory3Dao.setCwbNo(telegramDto.getCwbNo());
                insertAiStatusHistory3Dao.setCwbNoDeptCd(telegramDto.getCwbNoDeptCd());
                insertAiStatusHistory3Dao.setUserCd(userCd);
                insertAiStatusHistory3Dao.setUpdateDate(now);
                insertAiStatusHistory3DaoList.add(insertAiStatusHistory3Dao);
            }

            int insertAiStatusHistory3Cnt = 0;
            if (insertAiStatusHistory3DaoList.size() > 0) {
                insertAiStatusHistory3Cnt = this.insertAiStatusHistory3(insertAiStatusHistory3DaoList);
            }
            log.info("insertAiStatusHistory3Cnt: " + insertAiStatusHistory3Cnt);
        }
        // 2.1.8.7 OUT電文作成パラメータに対してOUT電文ファイルを作成する。
        if(outTelegramDtoList.size() > 0) {
            log.info("outTelegramDtoList: " + outTelegramDtoList.toString());
            int outSequence = 0;
            String yyyyMMddHHmmss = now.replaceAll("[ :-]", "");
            String HHmm = yyyyMMddHHmmss.substring(8, 12);
            String YY = yyyyMMddHHmmss.substring(2, 4);
            String MM = yyyyMMddHHmmss.substring(4, 6);
            String dd = yyyyMMddHHmmss.substring(6, 8);
            log.info("yyyyMMddHHmmss: " + yyyyMMddHHmmss);

            List<OutTelegramParamDto> outDataList = new ArrayList<OutTelegramParamDto>();

            boolean isResetKey = true;
            String targetCarryOutDate = null;
            String targetCollectTrader = null;
            String targetOtherWarehouse = null;
            for (int i = 0; i < outTelegramDtoList.size(); i++) {
                OAIW002OutTelegramDto targetData = outTelegramDtoList.get(i);
                if(isResetKey == true) {
                    targetCarryOutDate = targetData.getCarryOutDate() != null ? targetData.getCarryOutDate() : "";
                    targetCollectTrader = targetData.getCollectTrader() != null ? targetData.getCollectTrader() : "";
                    targetOtherWarehouse = targetData.getOtherWareHouse() != null ? targetData.getOtherWareHouse() : "";
                    isResetKey = false;
                }

                // OUT電文パラメータ
                OutTelegramParamDto outTelegramParamDto = new OutTelegramParamDto();
                outTelegramParamDto.setBondedOutHHmm(HHmm);
                outTelegramParamDto.setCarryOutDate(targetData.getCarryOutDate());
                outTelegramParamDto.setCollectTrader(targetData.getCollectTrader());
                outTelegramParamDto.setOtherWareHouse(targetData.getCollectTrader());
                outTelegramParamDto.setAwbNo(targetData.getAwbNo());
                outTelegramParamDto.setCwbNo(targetData.getCwbNo());
                outTelegramParamDto.setBondedWarehouseCd(targetData.getBondedWarehouseCd());
                outTelegramParamDto.setCwbNoDeptCd(targetData.getCwbNoDeptCd());

                // OUT電文作成パラメータ10件、又は最終行チェック
                if (outDataList.size() < 9  // 1電文ファイルのデータ個数：10
                        && (outTelegramDtoList.size() - 1 != i) // 最終行チェック
                        && targetCarryOutDate.equals(targetData.getCarryOutDate() != null ? targetData.getCarryOutDate() : "") // 搬出年月日チェック
                        && targetCollectTrader.equals(targetData.getCollectTrader() != null ? targetData.getCollectTrader() : "") // 出荷業者チェック
                        && targetOtherWarehouse.equals(targetData.getOtherWareHouse() != null ? targetData.getOtherWareHouse() : "") // 他所蔵置場所
                ) {
                    outDataList.add(outTelegramParamDto);
                } else { // OUT電文は、OUT電文作成パラメータ10件に対して1ファイルとする。
                    outDataList.add(outTelegramParamDto);
                    outSequence += 1;
                    String seq = String.format("1%03d", outSequence); //連番4桁の先頭を"1"とする("1" + 連番3桁)
                    String outFilename = "OUT" + targetData.getBondedWarehouseCd() + yyyyMMddHHmmss + seq;
                    String _customPlaceCd = targetData.getCustomPlaceCd();
                    String _bondedWarehouseCd = targetData.getBondedWarehouseCd();

                    try {
                        // OUTファイル作成
                        TelegramCommonParamDto telegramCommonParamDto = new TelegramCommonParamDto();
                        telegramCommonParamDto.setBusinessCd("OUT");
                        telegramCommonParamDto.setPlace(targetData.getBondedWarehouseCd());
                        telegramCommonParamDto.setReportCustomsSpecialistUserCd("");
                        telegramCommonParamDto.setFileName(outFilename);
                        telegramCommonParamDto.setRecordCnt(outDataList.size());
                        log.info("telegramCommonParamDto: " + telegramCommonParamDto.toString());
                        log.info("outDataList: " + outDataList.toString());
                        telegramServ.makeOutTelegramFile(outBackupDir, telegramCommonParamDto, outDataList);

                        // ➆ 送信電文管理(CS_SEND_MESSAGE)登録用データを登録する。
                        OAIW002InsertCsSendMessageDao insertCsSendMessageDao = new OAIW002InsertCsSendMessageDao();
                        insertCsSendMessageDao.setUserCd(userCd);
                        insertCsSendMessageDao.setUpdateDate(now);
                        insertCsSendMessageDao.setMessageType("OUT");
                        insertCsSendMessageDao.setMessageFileName(outFilename);
                        insertCsSendMessageDao.setFilePath(outBackupDir);
                        insertCsSendMessageDao.setMessageCreateDATE(now);
                        insertCsSendMessageDao.setCustomsPlaceCD(_customPlaceCd);
                        insertCsSendMessageDao.setBondedWarehouseCD(_bondedWarehouseCd);
                        int insertCsSendMessageCnt = this.insertCsSendMessage(insertCsSendMessageDao);
                        log.info("insertCsSendMessageCnt: " + insertCsSendMessageCnt);

                        File sourceFile = new File(outBackupDir + File.separator + outFilename);
                        File destFile = new File(outSendDir + File.separator + outFilename);

                        awsS3.upload(Files.readAllBytes(sourceFile.toPath()), "AutoMessage_Acc/OUT/" + YY + "/" + MM + "/" + dd + "/" + outFilename);

                        // ⑧ 作成した電文を自動送信用フォルダにコピーする
                        FileCopyUtils.copy(sourceFile, destFile);
                    } catch (Exception e) {
                        String errMsg = "OUTファイル作成に失敗しました。";
                        log.error(errMsg + e.getMessage());
                        throw new Exception(errMsg);
                    }
                    outDataList.clear();
                    isResetKey = true;
                }
            }
            awbNoCnt += outTelegramDtoList.size();
            outFileCnt += outSequence;
        }

        doOutBusinessDto.setOutFileCnt(outFileCnt);
        doOutBusinessDto.setAwbNoCnt(awbNoCnt);
        try {
            List<OAIW002SearchResultDto> searchResultDtoList = this.searchBondedOutData(reSearchParam);
            doOutBusinessDto.setResultDtoList(searchResultDtoList);
            doOutBusinessDto.setReSearchResult(true); //OUT処理後、再検索エラー
        } catch (Exception e) {
            log.warn("一覧の再検索エラー " + "param:"+reSearchParam.toString() + "error:" + e.toString());
            doOutBusinessDto.setResultDtoList(gridDataList);
            doOutBusinessDto.setReSearchResult(false); //OUT処理後、再検索エラー
        }
        return doOutBusinessDto;
    }
}
