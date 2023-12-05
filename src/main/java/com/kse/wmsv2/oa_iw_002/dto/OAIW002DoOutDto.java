package com.kse.wmsv2.oa_iw_002.dto;

import com.kse.wmsv2.oa_iw_002.dao.OAIW002SelectBondedOutListDao;
import java.util.List;
import lombok.Data;

@Data
public class OAIW002DoOutDto {
    OAIW002SelectBondedOutListDao param;
    List<OAIW002SearchResultDto> gridDataList;
    OAIW002SelectBondedOutListDao reSearchParam;
}
