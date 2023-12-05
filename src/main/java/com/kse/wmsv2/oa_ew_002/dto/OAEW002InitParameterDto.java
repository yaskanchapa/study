package com.kse.wmsv2.oa_ew_002.dto;

import lombok.Data;

import java.util.List;

@Data
public class OAEW002InitParameterDto {
    String message;
    String errorMessage;
    boolean result;
    List<OAEW002GetParameterListDto> customsPlaceList;
    List<OAEW002GetParameterListDto> bondedWarehouseCdList;
    List<OAEW002GetParameterListDto> depPortList;
    List<OAEW002GetParameterListDto> containerClassCdList;
    List<OAEW002GetParameterListDto> departureTruckNoList;
}
