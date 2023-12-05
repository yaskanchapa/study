package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.dto.COMMONStatusDto;
import com.kse.wmsv2.common.mapper.COMMONMapper;
import com.kse.wmsv2.common.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatusService {

    @Autowired
    COMMONMapper mapper ;


    public void updateStatusMaster(COMMONStatusDto params) throws Exception{
        int updateMaster = 0;
        int insertHistory = 0;

        if(StringUtil.isStringEmpty(params.getBwbNo())){
            params.setBwbNo("00000000000");
        }
        if(StringUtil.isStringEmpty(params.getCwbNoDeptCd())){
            params.setCwbNoDeptCd("000");
        }

        updateMaster = mapper.updateStatusMasterTable(params);
        insertHistory = insertStatusHistory(params);
        if( updateMaster + insertHistory < 2 ){
            throw new Exception();
        }
    }


    public void insertStatusMaster(COMMONStatusDto params) throws  Exception{
        int insertMaster = 0;
        int insertHistory = 0;
        if(StringUtil.isStringEmpty(params.getBwbNo())){
            params.setBwbNo("00000000000");
        }
        insertMaster = mapper.insertStatusMasterTable(params);
        insertHistory = insertStatusHistory(params);
        if( insertMaster + insertHistory < 2 ){
            throw new Exception();
        }
    }


    private int insertStatusHistory(COMMONStatusDto params) throws Exception{
        String checkStr = "";
        int insertCnt = 0;
        if(StringUtil.isStringEmpty(params.getBwbNo())){
            params.setBwbNo("00000000000");
        }
        if(!StringUtil.isStringEmpty(params.getCustomStatusCd())){
            params.setHistoryClass("01");
            insertCnt = insertCnt + mapper.insertStatusHistoryTable(params);
        }
        if(!StringUtil.isStringEmpty(params.getBondStatusCd())){
            params.setHistoryClass("02");
            insertCnt = insertCnt + mapper.insertStatusHistoryTable(params);
        }
        return insertCnt;
    }


}
