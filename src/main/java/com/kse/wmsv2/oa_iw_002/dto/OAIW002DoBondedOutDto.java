package com.kse.wmsv2.oa_iw_002.dto;
import com.kse.wmsv2.oa_iw_002.dao.OAIW002SelectBondedOutListDao;
import lombok.Data;
@Data
public class OAIW002DoBondedOutDto {
    OAIW002SelectBondedOutListDao param1; //　検索条件(検索画面)
    OAIW002SearchResultDto param2; // 許可行のRowData
    OAIW002SearchResultDto param3; // 搬出行のRowData
}
