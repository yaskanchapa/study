package com.kse.wmsv2.oc_cs_007.mapper;

import com.kse.wmsv2.oa_iw_001.dao.*;
import com.kse.wmsv2.oa_iw_001.dto.*;
import com.kse.wmsv2.oc_cs_007.dao.OCCS007InsertCsImageManagementDao;
import com.kse.wmsv2.oc_cs_007.dao.OCCS007SelectCsImageManagementDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OCCS007ScreenMapper {
    int insertCsImageManagement(OCCS007InsertCsImageManagementDao params);
    String selectCsImageManagement(OCCS007SelectCsImageManagementDao params);

}
