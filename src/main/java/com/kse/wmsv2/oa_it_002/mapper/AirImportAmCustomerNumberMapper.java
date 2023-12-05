package com.kse.wmsv2.oa_it_002.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_it_002.dto.request.AmCustomerNumberRequest;

@Repository
@Mapper
public interface AirImportAmCustomerNumberMapper {
    Integer countAmCustomerNumber(AmCustomerNumberRequest amCustomerNumberRequest);
    String getCustomerName(AmCustomerNumberRequest amCustomerNumberRequest);
}
