package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003SearchData2Dao {
    // 登録年月日(FROM)
    private String recordDateFrom;

    // 登録年月日(TO)
    private String recordDateTo;

    //MAWB No.
    private String awbNo;

    // 到着便名(FLT1)
    private String arrFlt1;

    // 到着便名(FLT2)
    private String arrFlt2;

    // 蔵置場所
    private String bondedWareHouseCd;

    // 対象(全件)
    private boolean objectAll;

}
