package com.kse.wmsv2.oa_ew_002.service;

import com.kse.wmsv2.oa_ew_002.mapper.OAEW002ScreenMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.kse.wmsv2.oa_ew_002.dao.*;
import com.kse.wmsv2.oa_ew_002.dto.*;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class  OAEW002ScreenService {
    @Autowired
    OAEW002ScreenMapper oaew002ScreenMapper;

    public List<OAEW002GetParameterListDto> getParameterList(OAEW002GetParameterListDao param) {
        return oaew002ScreenMapper.getParameterList(param);
    }
    public List<OAEW002SelectContainer1Dto> selectContainer1(OAEW002SelectContainer1Dao param) {
        return oaew002ScreenMapper.selectContainer1(param);
    }
    public int selectContainer2(OAEW002SelectContainer2Dao param) {
        return oaew002ScreenMapper.selectContainer2(param);
    }
    public int insertContainer1(OAEW002InsertUpdateContainer1Dao param) {
        return oaew002ScreenMapper.insertContainer1(param);
    }
    public int updateContainer1(OAEW002InsertUpdateContainer1Dao param) {
        return oaew002ScreenMapper.updateContainer1(param);
    }
    public int updateContainer2(OAEW002UpdateContainer2Dao param) {
        return oaew002ScreenMapper.updateContainer2(param);
    }
    public int deleteContainer1(OAEW002DeleteContainer1Dao param) {
        return oaew002ScreenMapper.deleteContainer1(param);
    }

    @Transactional(rollbackFor = Exception.class)
    public int doUpdateBusiness (List<OAEW002UpdateContainer2Dao> container1DaoList) throws Exception{
        int updateContainer2Cnt = 0;
        for(OAEW002UpdateContainer2Dao container1Dao: container1DaoList) {
            updateContainer2Cnt+= this.updateContainer2(container1Dao);
            log.info("updateContainer2Cnt: " + updateContainer2Cnt);
        }
        return updateContainer2Cnt;
    }
}
