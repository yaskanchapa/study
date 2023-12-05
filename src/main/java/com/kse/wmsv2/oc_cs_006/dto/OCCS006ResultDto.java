package com.kse.wmsv2.oc_cs_006.dto;

import lombok.Data;

import java.util.List;

@Data
public class OCCS006ResultDto {
    String urlPath;
    String message;
    String errorMessage;
    List<String> filePathList;
    boolean result;
}
