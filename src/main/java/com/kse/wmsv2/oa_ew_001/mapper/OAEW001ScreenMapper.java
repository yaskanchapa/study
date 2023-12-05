package com.kse.wmsv2.oa_ew_001.mapper;

import com.kse.wmsv2.oa_ew_001.dao.*;
import com.kse.wmsv2.oa_ew_001.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAEW001ScreenMapper {
    List<OAEW001SearchResultDao> selectSearchResult(OAEW001SearchDto params);
}