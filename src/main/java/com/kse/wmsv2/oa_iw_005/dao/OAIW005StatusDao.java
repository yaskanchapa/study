package com.kse.wmsv2.oa_iw_005.dao;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAIW005StatusDao {
    private String statusCd;
    private String regUserCd;
    private String regDate;
    private String userName;
    private String cwbNo;

    public OAIW005StatusDao(String cwbNo){
        this.cwbNo = cwbNo;
    }
}
