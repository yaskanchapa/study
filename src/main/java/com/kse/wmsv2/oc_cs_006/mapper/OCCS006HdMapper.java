package com.kse.wmsv2.oc_cs_006.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OCCS006HdMapper {
    List<String> selectFileName(String cwbNo);
    List<String> selectHdFileName();
    String selectCsImageManagement(String parm);


}
