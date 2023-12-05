package com.kse.wmsv2.oa_iw_005.service;

import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDateTime;

import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005DropDownDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005HistoryStatusDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005SearchDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005StatusDao;
import com.kse.wmsv2.oa_iw_005.dto.*;
import com.kse.wmsv2.oa_iw_005.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.log.ApplicationLogger;

@Service
@Slf4j
public class OAIW005ScreenService {
    @Autowired
    OAIW005ScreenMapper oaiw005ScreenMapper;
    @Autowired
    private RedisUtil redisUtil;

    public List<OAIW005SearchDao> selectSearchAi(OAIW005SearchDto params) {
        return oaiw005ScreenMapper.selectSearchAi(params);
    }

    public List<OAIW005HistoryStatusDao> cargoStatus(String cwbNo){
        return oaiw005ScreenMapper.cargoStatus(cwbNo);
    }

    public List<OAIW005StatusDto> getStatusHistoryByCwbno(String cwbNo){
        return oaiw005ScreenMapper.getStatusHistoryByCwbno(cwbNo);
    }

    public List<OAIW005DropDownDao> selectCargoName(String departmentCd){
        return oaiw005ScreenMapper.selectCargoName(departmentCd);
    }

    public List<OAIW005DropDownDao> selectCargoStatus(){
        return oaiw005ScreenMapper.selectCargoStatus();
    }

    public List<OAIW005DropDownDao> selectSortCondition(){
        return oaiw005ScreenMapper.selectSortCondition();
    }

    public List<OAIW005DropDownDao> selectOtherSortCondition(){
        return oaiw005ScreenMapper.selectOtherSortCondition();
    }


}


