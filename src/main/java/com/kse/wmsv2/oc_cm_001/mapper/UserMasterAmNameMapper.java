package com.kse.wmsv2.oc_cm_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.ComBoBoxDto;
import com.kse.wmsv2.oc_cm_001.dto.RateMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.RateMasterRequest;

@Repository
@Mapper
public interface UserMasterAmNameMapper {
    List<ComBoBoxDto> getListCodeAndAmName(String nameClass);
    List<ComBoBoxDto> getListCodeAndAmNameWuthOrderBy(@Param("nameClass") String nameClass,@Param("orderBy") String orderBy);
    List<RateMasterDto> getDataRate(RateMasterRequest rateMasterRequest);
    List<ComBoBoxDto> getListByNameClassAndDepartmentCd(@Param("nameClass") String nameClass,@Param("departmentCd") String departmentCd);
}
