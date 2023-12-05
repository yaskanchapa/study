package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003KeyDataDao {
    // 蔵置場所
    private String bondedWareHouseCd;

    // 到着便名1
    private String instArrFlt1;

    // 到着便名2
    private String instArrFlt2;

    // MAWB No.
    private String instAwbNo;

    // 終了分(表示)
    private String instEndDisplay;

    // 対象(全件)
    private boolean instObjectAll;

    // 対象(AWB指定)
    private boolean instObjectAWB;


}
