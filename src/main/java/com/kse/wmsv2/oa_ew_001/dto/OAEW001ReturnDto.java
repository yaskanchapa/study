package com.kse.wmsv2.oa_ew_001.dto;

import com.kse.wmsv2.oa_ew_001.dao.OAEW001SearchResultDao;
import lombok.Data;

import java.util.List;

@Data
public class OAEW001ReturnDto {
    String errorFlag;
    String message;
    List<OAEW001SearchResultDao> searchResultList;
    int cntFolder;
    int cntFile;
}
