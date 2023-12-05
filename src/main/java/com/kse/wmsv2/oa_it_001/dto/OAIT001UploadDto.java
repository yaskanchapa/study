package com.kse.wmsv2.oa_it_001.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class OAIT001UploadDto  {
    String awbNo;
    MultipartFile uploadFile;
    Map<String,String> defaultList;
    String currentDeptCd;
    String currentTabValue;
    String currentMailOrder;
    String currentAgency;
    String currentDefaultList;
    String deleteOld;
}
