package com.kse.wmsv2.oc_cm_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.TraderNoMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.TraderNoMasterRequest;

@Repository
@Mapper
public interface TraderNoMasterAmCustomerNumberMapper {

    List<TraderNoMasterDto> getDataAmCustomerNumber(TraderNoMasterRequest traderNoMasterRequest);
    String getOcsDeptCdAmCustomerNumber(TraderNoMasterDto traderNoMasterDto);
    void registTraderNoMaster(TraderNoMasterDto traderNoMasterDto);
    TraderNoMasterDto detailTraderNoMaster(TraderNoMasterDto traderNoMasterDto);
    void updateTraderNoMaster(TraderNoMasterDto traderNoMasterDto);
    void deleteTraderNoMaster(TraderNoMasterDto traderNoMasterDto);
    Integer countAmCustomerNumber(TraderNoMasterDto traderNoMasterDto);
    Integer countAmCustomerNumberByCustomerNo(String customerNo);
}
