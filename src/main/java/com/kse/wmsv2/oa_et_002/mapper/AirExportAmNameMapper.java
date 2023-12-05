package com.kse.wmsv2.oa_et_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_et_002.dto.AMNameDto;
import com.kse.wmsv2.oa_et_002.dto.ComBoBoxDto;

@Repository
@Mapper
public interface AirExportAmNameMapper {
    List<ComBoBoxDto> getListCodeAndAmName(String nameClass);
    List<String> getListAmName(String nameClass);
    String getOperator(@Param("nameClass") String nameClass,@Param("nameCd") String nameCd);
    List<ComBoBoxDto> getListNameClassAndDepartmentCd(@Param("nameClass")String nameClass, @Param("departmentCd")String departmentCd);
    List<AMNameDto> getDataAmNameData(String nameCd);
}
