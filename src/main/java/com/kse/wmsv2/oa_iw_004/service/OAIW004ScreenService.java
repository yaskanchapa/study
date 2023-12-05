package com.kse.wmsv2.oa_iw_004.service;

import com.kse.wmsv2.oa_iw_004.dao.*;
import com.kse.wmsv2.oa_iw_004.dto.*;
import com.kse.wmsv2.oa_iw_004.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class OAIW004ScreenService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OAIW004ScreenMapper oaiw004ScreenMapper;

    // 蔵置場所確認
    public OAIW004SearchWarehouseDao searchWarehouse(OAIW004SearchDto params) {
        return oaiw004ScreenMapper.searchWarehouse(params);
    }

}
