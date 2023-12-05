package com.kse.wmsv2.oa_et_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface AirExportCmControlMapper {
    List<String> getListcontrolDate(String controlCd);
}
