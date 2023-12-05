package com.kse.wmsv2.oc_cs_006.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface OCCS006PermitImpMapper {
    //find file Name
    List<String> selectFileName(String cwbNo);
    //  Pe, Rp
    List<String> selectRpFileName(String cwbNo);

    List<String> selectPeFileName(String cwbNo);

    String selectCsImageManagement(String parm);


}


