package com.kse.wmsv2.oa_it_002.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oa_it_002.dto.ComBoBoxDto;

@Repository
@Mapper
public interface AirImportAmNameMapper {
    List<ComBoBoxDto> getListCodeAndAmName(String nameClass);
    List<String> getListAmName(String nameClass);
    String getOperator(@Param("nameClass") String nameClass,@Param("nameCd") String nameCd);
    List<ComBoBoxDto> getListByNameClassAndDepartmentCd(@Param("nameClass") String nameClass,@Param("departmentCd") String departmentCd);
}
