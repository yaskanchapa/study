package com.kse.wmsv2.oa_iw_004.mapper;


import com.kse.wmsv2.oa_iw_004.dao.*;
import com.kse.wmsv2.oa_iw_004.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface OAIW004ScreenMapper {

    // 蔵置場所確認
    OAIW004SearchWarehouseDao searchWarehouse(OAIW004SearchDto params);

    // 搬入チェック
    List<OAIW004InSearchMawbDao> inSearchMawb(OAIW004SearchDto params);
    OAIW004InResultDao inSearch(OAIW004SearchDto params);
    int inUpdateScanpiece(OAIW004SearchDto params);
    int inUpdateOverFlg(OAIW004SearchDto params);
    int inUpdateStatus(OAIW004SearchDto params);
    int inUpdateAiData(OAIW004SearchDto params);
    int inInsertHistory(OAIW004SearchDto params);
    OAIW004InCountDao inSearchCount(OAIW004SearchDto params);
    int inUpdateHead(OAIW004SearchDto params);
    int inUpdateTemp(OAIW004SearchDto params);

    // 搬出チェック
    List<OAIW004OutSearchMawbDao> outSearchMawb(OAIW004SearchDto params);
    OAIW004OutResultDao outSearch(OAIW004SearchDto params);
    int outUpdateScanpiece(OAIW004SearchDto params);
    int outUpdateOverFlg(OAIW004SearchDto params);
    int outUpdateStatus(OAIW004SearchDto params);
    int outUpdateAiData(OAIW004SearchDto params);
    int outInsertHistory(OAIW004SearchDto params);
    int outUpdateHead(OAIW004SearchDto params);
    OAIW004OutCountDao outSearchCount(OAIW004SearchDto params);

    // インベントリー
    List<OAIW004InvSearchMawbDao> invSearchMawb(OAIW004SearchDto params);
    OAIW004InvResultDao invSearch(OAIW004SearchDto params);
    int invUpdate(OAIW004SearchDto params);
    int invUpdatePiece(OAIW004SearchDto params);
    int invUpdateHead(OAIW004SearchDto params);
    OAIW004InvCountDao invSearchCount(OAIW004SearchDto params);
}
