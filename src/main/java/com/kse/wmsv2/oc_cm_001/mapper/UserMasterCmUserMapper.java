package com.kse.wmsv2.oc_cm_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.UserMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.UserMasterRequest;

@Repository
@Mapper
public interface UserMasterCmUserMapper {

    UserMasterDto getCmUserInfo(String userCd);

    List<UserMasterDto> getListDataCmUser(UserMasterRequest userMasterRequest);

    List<UserMasterDto> getListDataCsvCmUser(UserMasterRequest userMasterRequest);

    List<String> getListCmUserByUserCd(@Param("userCd") String userCd);

    void registCmUser(UserMasterDto userMasterDto);

    void updateCmUserByUserCd(UserMasterDto userMasterDto);

    void deleteCmUserByUserCd(String userCd);

}
