package com.kse.wmsv2.oa_iw_002.dto;
import lombok.Data;
@Data
public class OAIW002DoBondedOutResultDto {
    String message;
    String errorMessage;
    boolean result;
    OAIW002DoBondedOutDto bondedOutDto;
}
