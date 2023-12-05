package com.kse.wmsv2.oa_it_001.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.core.io.InputStreamResource;

import java.io.File;

@EqualsAndHashCode(callSuper=false)
@Data
public class OAIT001ReportReturnDto extends OAIT001ReturnDto{
    InputStreamResource reportFile;
    String cntHCH;
    String cntFileHCH;
    String cntHDM;
    String cntFileHDM;
    String cntCHA;
    String cntFileCHA;
    String cntMIC;
    String cntFileMIC;
    String cntIDC;
    String cntFileIDC;
    String cntIDA;
    String cntFileIDA;
}
