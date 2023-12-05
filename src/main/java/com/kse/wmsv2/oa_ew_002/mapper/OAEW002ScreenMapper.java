package com.kse.wmsv2.oa_ew_002.mapper;
import org.apache.ibatis.annotations.Mapper;
import com.kse.wmsv2.oa_ew_002.dao.*;
import com.kse.wmsv2.oa_ew_002.dto.*;

import java.util.List;

@Mapper
public interface OAEW002ScreenMapper {
    public List<OAEW002GetParameterListDto> getParameterList(OAEW002GetParameterListDao param);
    public List<OAEW002SelectContainer1Dto> selectContainer1(OAEW002SelectContainer1Dao param);
    int selectContainer2(OAEW002SelectContainer2Dao param);
    int insertContainer1(OAEW002InsertUpdateContainer1Dao param);
    int updateContainer1(OAEW002InsertUpdateContainer1Dao param);
    int updateContainer2(OAEW002UpdateContainer2Dao param);
    int deleteContainer1(OAEW002DeleteContainer1Dao param);
}
