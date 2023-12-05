package com.kse.wmsv2.oa_et_001.dto;

import com.kse.wmsv2.oa_et_001.dao.OAET001DefaultDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001SearchResultDao;
import lombok.Data;

import java.util.List;

@Data
public class OAET001ReturnDto {
    OAET001SearchDto searchCond;
    String message;
    String errFlg;
    List<OAET001SearchResultDao> searchResult;
    List<OAET001DefaultDao> defaultList;
}
