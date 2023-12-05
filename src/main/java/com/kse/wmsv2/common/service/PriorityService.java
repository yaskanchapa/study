package com.kse.wmsv2.common.service;

import com.kse.wmsv2.common.mapper.COMMONMapper;
import com.kse.wmsv2.common.mapper.PriorityMapper;
import com.kse.wmsv2.common.util.StringUtil;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDADao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDAMainDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PriorityService {

    @Autowired
    PriorityMapper mapper;

    @Autowired
    COMMONMapper commonMapper;

    public int getInvoiceSpecialItem(Object param) {
        List<String> invoiceItemList = mapper.getInvoiceItem(param);
        int resultVal = 0;
        for(int i = 0; i < invoiceItemList.size(); i++){
            int specialItemList = mapper.getSpecialItem(invoiceItemList.get(i));
            if(specialItemList > 0){
                resultVal = resultVal + (specialItemList*5);
            }
        }
        return  resultVal;
    }

    public int checkSpecialOrder(Object param) {
        Integer resultVal;
        if(param instanceof OAIT001MICDao){
            resultVal = mapper.checkSpecialOrder(((OAIT001MICDao) param).getCwbNo());
        } else {
            resultVal = mapper.checkSpecialOrder(((OAIT001IDADao) param).getMainDao().getCwbNo());
        }
        if(resultVal != null){
            return resultVal.intValue()* 5;
        }
        return  0 ;
    }

    public int checkImporter(Object param) {
        String importer = "";
        if(param instanceof OAIT001MICDao){
            importer = ((OAIT001MICDao) param).getImpCustomerNo();
        } else {
            importer = ((OAIT001IDADao) param).getMainDao().getImpCustomerNo();
            OAIT001IDADao idaDao = (OAIT001IDADao) param;
        }

        int resultVal = 0;
        if(!StringUtil.isStringEmpty(importer)){
            resultVal = 5;
        }
        return resultVal;
    }

    public int checkSpecialImporter(Object param) {
        int resultVal = 0;
        Map<String,Object> importer = mapper.checkSpecialImporter(param);
        if(importer != null){
            List<Object> valueList = new ArrayList<>(importer.values());
            for(Object obj : valueList) {
                resultVal = resultVal + Integer.parseInt(obj.toString());
            }
        }
        return resultVal;
    }

    public int countInvoiceDetailData(Object param) {
        int resultVal = 0;
        Map<String,Object> dataList = mapper.countInvoiceDetailData(param);
        if(dataList != null){
            resultVal = 5;
        }
        return resultVal;
    }

    public int checkInvoiceAmount(Object param) {
        Map<String,String> paramMap = new HashMap<>();
        int resultVal = 0;
        String systemDate = commonMapper.getBusinessDate();


        if(param instanceof OAIT001MICDao){
            OAIT001MICDao dao = (OAIT001MICDao) param;
            paramMap.put("currencyCd", dao.getInvoiceCurCd());
            paramMap.put("invoiceValue", dao.getInvoiceValue());
            if(StringUtil.isStringEmpty(dao.getReportPlaningDate())){
                paramMap.put("date", commonMapper.getBusinessDate());
            } else {
                paramMap.put("date", dao.getReportPlaningDate());
            }
        } else {
            OAIT001IDAMainDao dao = (OAIT001IDAMainDao) param;
            paramMap.put("currencyCd", dao.getInvoiceCurCD());
            paramMap.put("invoiceValue", dao.getInvoiceValue());
            if(StringUtil.isStringEmpty(dao.getReportPlaningDate())){
                paramMap.put("date", commonMapper.getBusinessDate());
            } else {
                paramMap.put("date", dao.getReportPlaningDate());
            }
        }

        Map<String,Object> dataList = mapper.checkInvoiceAmount(paramMap);
        if(dataList != null){
            resultVal = 5;
        }
        return resultVal;
    }
}
