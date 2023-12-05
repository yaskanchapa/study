package com.kse.wmsv2.oa_it_004.mapper;

import com.kse.wmsv2.oa_it_003.dao.OAIT003SearchResultDao;
import com.kse.wmsv2.oa_it_003.dto.OAIT003SearchDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAIT004ScreenMapper {

    List<Map> getDefaultValueList(Map<String, String> paramMap);

    int insertOneShot(Map<String, String> paramMap);

    int updateOneShot(Map<String, String> paramMap);

    int deleteOneShot(Map<String, String> paramMap);

    int updateDocStatusMaster(Map<String, String> paramMap);

    int updateDocStatusHistory(Map<String, String> paramMap);

}
