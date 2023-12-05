package com.kse.wmsv2.oa_it_003.mapper;

import com.kse.wmsv2.oa_it_003.dao.OAIT003PrintDao;
import com.kse.wmsv2.oa_it_003.dao.OAIT003SearchResultDao;
import com.kse.wmsv2.oa_it_003.dao.OAIT003InHouseListDao;
import com.kse.wmsv2.oa_it_003.dto.OAIT003PrintDto;
import com.kse.wmsv2.oa_it_003.dto.OAIT003SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OAIT003ScreenMapper {
    List<OAIT003SearchResultDao> selectSearchResult(OAIT003SearchDto params);
    List<OAIT003InHouseListDao> inHouseList();
    List<OAIT003PrintDao> printType(OAIT003PrintDto params);
    List<OAIT003PrintDao> printTypeMultiple();

    OAIT003PrintDao getImagePath(OAIT003PrintDto params);
}
