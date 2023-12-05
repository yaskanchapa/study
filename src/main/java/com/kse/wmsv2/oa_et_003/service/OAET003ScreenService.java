package com.kse.wmsv2.oa_et_003.service;

import com.kse.wmsv2.oa_et_003.dto.OAET003SearchDto;
import com.kse.wmsv2.oa_et_003.mapper.OAET003ScreenMapper;
import com.kse.wmsv2.oa_et_003.dao.OAET003SearchResultDao;
import com.kse.wmsv2.oa_et_003.dto.OAET003SearchPrintDto;
import com.kse.wmsv2.oa_et_003.dao.OAET003SearchPrintResultDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class OAET003ScreenService {

    @Autowired
    OAET003ScreenMapper oaet003ScreenMapper;


    public List<OAET003SearchResultDao> searchResultList(OAET003SearchDto params) {
    return oaet003ScreenMapper.selectSearchResult(params);
    }
    public List<OAET003SearchPrintResultDao> searchPrintResultList(OAET003SearchPrintDto param) {
        return oaet003ScreenMapper.selectSearchPrintResult(param);
    }
}
