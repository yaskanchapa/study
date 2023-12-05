package com.kse.wmsv2.oc_cm_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.CatererSenderMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.CatererSenderMasterRequest;

@Repository
@Mapper
public interface CatererSenderMasterAmCCENumberMapper {

    List<CatererSenderMasterDto> getDataAmConsignerCosigneeNumber(CatererSenderMasterRequest catererSenderMasterRequest);
    String getOcsDeptCdAmCCENumber(CatererSenderMasterDto catererSenderMasterDto);
    void registCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto);
    CatererSenderMasterDto detailCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto);
    void deleteCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto);
    void updateCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto);
    Integer countCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto);
}
