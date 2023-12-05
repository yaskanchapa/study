package com.kse.wmsv2.oc_cs_003.mapper;

import com.kse.wmsv2.oc_cs_003.dto.UserInfoDto;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface UserInfoMapper {

    UserInfoDto select(String userCd);

}
