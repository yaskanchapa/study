package com.kse.wmsv2.oa_ew_003.dto;

import lombok.Data;

import javax.validation.constraints.Size;
import java.util.List;

@Data
public class OAEW003examinationSearchDto {
    //@Size(min=15, max=15)
    String cwbNo;
    String awbNo;
    String linkPiece;
    String bondedWarehouseCd;
    String defaultPiece;
    String portName;
    String message;
    String errorMessage;
    boolean result;
    List<OAEW003examinationSearchDto> resultData;

}
