package com.kse.wmsv2.oa_it_001.dto;

import com.kse.wmsv2.oa_it_001.dao.OAIT001HeaderDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001SearchResultDao;
import lombok.Data;

import java.util.List;

@Data
public class OAIT001SearchResultDto {

    OAIT001HeaderDao headerData;
    List<OAIT001SearchResultDao> detailDataList;
    String message;
    String errFlg;
}
