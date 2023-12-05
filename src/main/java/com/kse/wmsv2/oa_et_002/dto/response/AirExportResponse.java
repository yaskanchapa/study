package com.kse.wmsv2.oa_et_002.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oa_et_002.dto.AirExportDto;
import com.kse.wmsv2.oa_et_002.dto.ComBoBoxDto;

@Data
public class AirExportResponse {

    private List<ComBoBoxDto> listPermitClassCd;
    private List<ComBoBoxDto> listPermitClass;
    private List<ComBoBoxDto> listAgent;
    private List<ComBoBoxDto> listBondedWareHouseCd;
    private String truckNoDate;
    private String arrival01;
    private String arrival02;
    private String cDB01;
    private String cDB02;
    private String eDA01;
    private String eDA02;
    private String mECEDC01;
    private String mECEDC02;
    private String reserveDeclara01;
    private String reserveDeclara02;
    private String declara01;
    private String declara02;
    private String permission01;
    private String permission02;
    private String hDF01;
    private String hDF02;
    private String consolidation01;
    private String consolidation02;
    private String uLM01;
    private String uLM02;
    private String possible01;
    private String possible02;
    private String lA01;
    private String lA02;
    private String eSAFlg01;
    private String eSAFlg02;
    private String largeSmallFlg01;
    private String largeSmallFlg02;
    private String linkTruckNOFrom;
    private String linkTruckNOTo;
    private String aWBNo;
    private String cWBNo;
    private Integer count;
    private String arrivalDate;
    private String shuttleDate;
    private String tracDate;
    private String permitDate;
    private String reportDate;
    private Boolean chkArrivalDate;
    private List<ComBoBoxDto> listDepPort;
    private String destination;
    private String cDBre01;
    private String cDBre02;
    private String bIL01;
    private String bIL02;
    private String cmbAgent;
    private String cmbBondedWarehouseCd;
    private Boolean searchEnabled;
    private Boolean clearEnabled;
    private Boolean outCsvEnabled;
    private Boolean focusDate;
    private Integer countRecord;

    private List<AirExportDto> listAirExportDto;


    public AirExportResponse() {
    }

    public AirExportResponse(List<AirExportDto> listAirExportDto) {
        this.listAirExportDto = listAirExportDto;
    }
}