package com.kse.wmsv2.oa_et_001.dto;

import com.kse.wmsv2.oa_et_001.dao.OAET001EDADetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001EDAHeaderDao;
import lombok.Data;

import java.util.List;

@Data
public class OAET001EDADto {
    OAET001EDAHeaderDao headerData ;
    List<OAET001EDADetailDao> detailDataList;
    String edaCheckFlag;
}
