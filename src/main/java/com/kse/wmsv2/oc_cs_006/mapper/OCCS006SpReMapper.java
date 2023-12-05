package com.kse.wmsv2.oc_cs_006.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OCCS006SpReMapper {
    List<String> selectFileName(String cwbNo);
    List<String> selectSpReFileName();
    String selectCsImageManagement(String parm);


}
