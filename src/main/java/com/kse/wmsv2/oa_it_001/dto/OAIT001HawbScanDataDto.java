package com.kse.wmsv2.oa_it_001.dto;

import java.util.List;

import lombok.Data;

@Data
public class OAIT001HawbScanDataDto {
    String awbNo;
    String arrflt_1;
    String arrflt_2;
    String rdoCondition;
    Boolean docCheckAndIDA;
    Boolean docCheckAndMIC;
    Boolean ida;
    Boolean docCheck;
    Boolean mic;
    Boolean reservation;
    Boolean clearancePlaceChange;
    Boolean rdoClearanceSituation;
    Boolean idaFlg;
    String updateFlg;
    Boolean controlCheckFlag;
    String confirmationItem;
    String detail;
    String customsBroker;
    String customsClearancePlace;
    String cmbCustomsClearanceSituation;
    List<OAIT001HawbRowInfoDto> listHawbNo;
}
