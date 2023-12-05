package com.kse.wmsv2.oa_et_003.mapper;

import com.kse.wmsv2.oa_et_003.dao.OAET003SearchResultDao;
import com.kse.wmsv2.oa_et_003.dto.OAET003SearchDto;
import com.kse.wmsv2.oa_et_003.dao.OAET003SearchPrintResultDao;
import com.kse.wmsv2.oa_et_003.dto.OAET003SearchPrintDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAET003ScreenMapper {

    List <OAET003SearchResultDao> selectSearchResult(OAET003SearchDto params);
    List <OAET003SearchPrintResultDao> selectSearchPrintResult(OAET003SearchPrintDto params);

}
