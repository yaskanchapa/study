package com.kse.wmsv2.oa_it_003.service;

import com.kse.wmsv2.oa_it_003.dao.OAIT003PrintDao;
import com.kse.wmsv2.oa_it_003.dto.OAIT003PrintDto;
import com.kse.wmsv2.oa_it_003.dto.OAIT003SearchDto;
import com.kse.wmsv2.oc_cs_006.service.OCCS006PermitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kse.wmsv2.oa_it_003.mapper.OAIT003ScreenMapper;

import java.util.List;

@Service
public class OAIT003PrintService {

    @Autowired
    OAIT003ScreenMapper oait003ScreenMapper;

    public List<OAIT003PrintDao> printType(OAIT003PrintDto params) {
        return oait003ScreenMapper.printType(params);
    }

    public List<OAIT003PrintDao> printTypeMultiple() {
        return oait003ScreenMapper.printTypeMultiple();
    }



}
