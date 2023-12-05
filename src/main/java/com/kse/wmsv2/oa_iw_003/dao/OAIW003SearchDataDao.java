package com.kse.wmsv2.oa_iw_003.dao;

import lombok.Data;

@Data
public class OAIW003SearchDataDao {
    private String workingDays;
    private String bondedWareHouseCd;
    private String arrFlt1;
    private String arrFlt2;
    private String awbNo;
    private boolean endDisplay;
    private boolean objectAll;
    private boolean objectAWB;
    private boolean unMatchOver;
    private boolean unMatchShort;
    private boolean match;


}
