package com.kse.wmsv2.common.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@Mapper
public interface PriorityMapper {

    List<String> getInvoiceItem(Object param);

    Integer getSpecialItem(Object value);

    Integer checkSpecialOrder(Object cwbNo);

    Map<String, Object> checkSpecialImporter(Object param);

    Map<String, Object> countInvoiceDetailData(Object param);

    Map<String, Object> checkInvoiceAmount(Map<String, String> paramMap);
}
