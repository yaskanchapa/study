package com.kse.wmsv2.oa_it_003.service;


import com.kse.wmsv2.oa_it_003.dao.OAIT003InHouseListDao;
import com.kse.wmsv2.oa_it_003.dao.OAIT003SearchResultDao;
import com.kse.wmsv2.oa_it_003.dto.OAIT003SearchDto;
import com.kse.wmsv2.oa_it_003.mapper.OAIT003ScreenMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.processor.SpringTextareaFieldTagProcessor;

import java.util.List;

@Service
public class OAIT003ScreenService {

    @Autowired
    OAIT003ScreenMapper oait003ScreenMapper;

    public List<OAIT003SearchResultDao> searchResultList(OAIT003SearchDto params){
        return oait003ScreenMapper.selectSearchResult(params);
    }
    public List<OAIT003InHouseListDao> inHouseList() {
        return oait003ScreenMapper.inHouseList();
    }
}
