package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003UpdateAiInventoryDao {
    // ログインユーザーの蔵置場所
    private String bondedWareHouseCd;
    // CWB
    private String cwbNo;
    // CWB枝番
    private String cwbNoDeptCd;
    private String workEndTime;


}
