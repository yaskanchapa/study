package com.kse.wmsv2.oc_cs_006.mapper;

import com.kse.wmsv2.oc_cs_006.dto.OCCS006BondInBillDto;
import com.kse.wmsv2.oc_cs_006.dto.OCCS006ExDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OCCS006ExMapper {
    List<String> selectFileName(String cwbNo);
    List<String> selectExFileName(String cwbNo);
    String selectCsImageManagement(String parm);


}
