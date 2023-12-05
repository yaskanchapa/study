package com.kse.wmsv2.oa_it_004.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class OAIT004OneShotDto {
    String trader;
    String register;
    String condition;
    String customsCd;
    String comment;
    List<Map<String,String>> hawbList;
}
