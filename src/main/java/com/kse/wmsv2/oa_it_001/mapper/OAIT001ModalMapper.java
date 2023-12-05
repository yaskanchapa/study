package com.kse.wmsv2.oa_it_001.mapper;

import com.kse.wmsv2.oa_it_001.dao.OAIT001IDADao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDAMainDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001IDASubDao;
import com.kse.wmsv2.oa_it_001.dao.OAIT001MICDao;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ErrorReportDto;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface OAIT001ModalMapper {
    int updateMicData(OAIT001MICDao param);

    int updateMicHeadData(Map<String,String> paramMap);

    Map<String,Object> getCurrentArrFlt(Map<String,String> paramMap);

    List<String> getInvoiceItem(Object param);

    Integer getSpecialItem(Object value);

    Integer checkSpecialOrder(Object cwbNo);

    Map<String, Object> checkSpecialImporter(Object param);

    Map<String, Object> countInvoiceDetailData(Object param);

    Map<String, Object> checkInvoiceAmount(Map<String, String> paramMap);

    int checkReportIda(Object param);

    int updateIDAMainData(OAIT001IDAMainDao param);

    int deleteIDASubData(Map paramMap);

    int updateIDASubData(OAIT001IDASubDao param);

    int checkIDAHeaderData(OAIT001IDAMainDao param);

    int updateIDAHeadData(Map paramMap);

    Map getIDAHeadData(OAIT001IDAMainDao param);

    List<OAIT001ErrorReportDto> searchErrorReport(Map<String, String> paramMap);

    int saveMicData(OAIT001MICDao param);

    int saveIDAMainData(OAIT001IDAMainDao param);

    int saveIDASubData(OAIT001IDASubDao oait001IDASubDao);

    String getRate(Map<String, String> paramMap);
}
