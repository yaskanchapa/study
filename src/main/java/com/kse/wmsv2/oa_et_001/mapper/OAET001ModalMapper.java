package com.kse.wmsv2.oa_et_001.mapper;

import com.kse.wmsv2.oa_et_001.dao.OAET001EDADetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001EDAHeaderDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001MECDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ErrorReportDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAET001ModalMapper {


    String[] selectExpCustomer(OAET001MECDao param);

    List<String> getInvoiceItem(String value);

    int getSpecialItem(String s);

    String[] checkSpecialOrder(String awbNo);

    int getPriority(Map<String, String> paramMap);

    int countInvoiceDetailData(Map<String, String> paramMap);

    int checkInvoiceAmount(Map<String, String> paramMap);

    int updateMecData(OAET001MECDao param);

    Map<String, Object> checkSpecialExporter(Map<String, String> paramMap);

    void updateEdaHeaderData(OAET001EDAHeaderDao headerData);

    int updateEdaDetailData(OAET001EDADetailDao oaet001EDADetailDao);

    List<OAET001ErrorReportDto> searchErrorReport(Map<String, String> paramMap);

    int saveMecData(OAET001MECDao param);

    int saveEdaHeaderData(OAET001EDAHeaderDao headerData);

    int saveEdaDetailData(OAET001EDADetailDao oaet001EDADetailDao);
}
