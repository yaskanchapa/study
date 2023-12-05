package com.kse.wmsv2.oa_it_002.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oa_it_002.dto.AirImportDto;
import com.kse.wmsv2.oa_it_002.dto.ComBoBoxDto;

@Data
public class AirImportResponse {

    /* 通関STS */
    private List<ComBoBoxDto> listCustomsClearanceSTS;

    /* 書類STS */
    private List<ComBoBoxDto> listDocumentSTS;

    /* 賃物STS */
    private List<ComBoBoxDto> listRentSTS;

    /* 通関業者 */
    private List<ComBoBoxDto> listCustomsBroker;

    /* 許可区分 */
    private List<ComBoBoxDto> listPermitCategory;

    /* 提出有無 */
    private List<ComBoBoxDto> listSubmission;

    /* 代理店選択 */
    private List<ComBoBoxDto> listAgencySelection;
    
    private String textCustomsTraderCd;
    
    private String textCustomSplaceCd;

    private String textObjectDateArrival;

    private String textOobjectDateCarrying;

    private String textObjectDateReport;

    private String textObjectDatePermission;

    private String textObjectDateCarry;
 
    private String textReportFlg01;

    private String textReportFlg02;

    private String textPermitFlg01;

    private String textPermitFlg02;

    private String textC4regFlg01;

    private String textC4regFlg02;

    private String textSet;

    private String textDeclarationOutput01;

    private String textDeclarationOutput02;

    private String textImage01;

    private String textImage02;

    private String textReportMic;

    private String textReportIda;

    private String textCertificate01;

    private String textCertificate02;

    private String textBigSmallFlg01;

    private String textBigSmallFlg02;

    private String textOptional01;

    private String textOptional02;

    private String numberOfIDA;

    private String numberOfMIC;

    private String numberOfPending;

    private String numberOfUndeclared;

    private String theTotalNumberOfCase;

    private String totalNumber;

    private Boolean searchEnable;

    private Boolean clearEnable;

    private Boolean agencySelectionEnable;

    private Boolean hawbNoFocus;

    private Boolean csvKEnable;

    private Boolean csvLEnable;

    private Boolean pdfEnable;

    private Boolean cButtonEnable;

    private List<AirImportDto> airImportDto;

    private String departmentCd;

    public AirImportResponse() {
    }

    public AirImportResponse(List<AirImportDto> airImportDto) {
        this.airImportDto = airImportDto;
    }
}