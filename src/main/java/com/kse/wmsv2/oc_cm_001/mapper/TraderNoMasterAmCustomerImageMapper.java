package com.kse.wmsv2.oc_cm_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import com.kse.wmsv2.oc_cm_001.dto.ImageTraderNoMasterDto;

@Repository
@Mapper
public interface TraderNoMasterAmCustomerImageMapper {

    List<ImageTraderNoMasterDto> getListImageTraderNoMaster(ImageTraderNoMasterDto tmageTraderNoMasterDto);
    Integer getSeqImageTraderNoMaster(String customerNo);
    void registImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto);
    void deleteImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto);
}
