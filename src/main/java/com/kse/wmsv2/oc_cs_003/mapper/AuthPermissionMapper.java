package com.kse.wmsv2.oc_cs_003.mapper;

import com.kse.wmsv2.oc_cs_003.dto.AuthPermissionDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface AuthPermissionMapper {

    List<AuthPermissionDto> selectAll();

}
