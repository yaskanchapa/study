package com.kse.wmsv2.oa_iw_001.dto;
import lombok.Data;

@Data
public class OAIW001SelectBondedInListDto {
    boolean carryInActionFlg;
    String batch;
    String bondedWareClass;
    String arrFtl;
    String arrFtl1;
    String arrFtl2;
    String awbNo;
    String origin;
    String awbPiece;
    String awbWeight;
    String cntCWBCount;
    String cntCWBPiece;
    String cntCWBWeight;
    String cntOK;
    String cntSH;
    String cntOV;
    String cntOKPiece;
    String cntSHPiece;
    String cntOVPiece;
    String cntGeneral;
    String cntMani;
    String cntPermission;
    String cntInput;
    String cntInGeneral;
    String cntInMani;
    String cntInMisinkoku;
    String cntUnInGeneral;
    String cntUnInMani;
    String cntUnInMisinkoku;
    String RFIDFlag;
}
