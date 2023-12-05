package com.kse.wmsv2.oa_it_001.dao;

import lombok.Data;

@Data
public class OAIT001DetailDataDao {

    String awbNo;
    String cwbNo;
    String cwbNoDeptCd;
    String remodelingFlg;
    String bwbNo;
    String currentArrFlt1;
    String currentArrFlt2;
    String getPort;
    String customsTraderCd;
    String customsPlaceCd;
    String bondedWareHouseCd;
    String editFlg;
    String documentCheck;
    String origin;
    String spsDocClassCd;
    String docInCompleteFlg;
    String declarationOutputFlg;
    String carryInObjectFlg;
    String locationCd;
    String currentCustomsStatusCd;
    String currentCargoStatusCd;
    String currentDocStatusCd;
    String entryType;
    String idaFlg;
    String reportCondtion;
    String bigSmallFlg;
    String reportKindCd1;
    String reportKindCd2;
    String cargoDisc;
    String discernmentMark;
    String reportDivCd;
    String reportDepCd;
    String impCustomerNo;
    String impCustomerDeptCd;
    String impCustomerOcsDeptCd;
    String impCustomerName;
    String impPostCode;
    String impCustomerAdd1;
    String impCustomerAdd2;
    String impCustomerAdd3;
    String impCustomerAdd4;
    String impCustomerAddLump;
    String impCustomerAdd;
    String impCustomerTelNo;
    String reportPersonCd;
    String expCustomerNo;
    String expCustomerDeptNo;
    String expCustomerOcsDeptNo;
    String expCustomerName;
    String expCustomerAdd1;
    String expCustomerAdd2;
    String expCustomerAdd3;
    String expCustomerAdd4;
    String expCustomerAddLump;
    String expCustomerAdd;
    String expCustomerPostCode;
    String expCustomerCountry;
    String expCustomerTelNo;
    String cargoPiece;
    String cargoInPiece;
    String cargoInScanPiece;
    String permitCargoPiece;
    String cargoOutPiece;
    String bondedCargoOutPiece;
    String bondedCargoInPiece;
    String docScanPiece;
    String cargoWeight;
    String weightUnitCd;
    String tradeTypeMark;
    String containerPiece;
    String invoiceDiscernment;
    String invoiceValClassCd;
    String invoiceValConCd;
    String invoiceCurCd;
    String invoiceValue;
    String fareFlg;
    String fareCurrencyCd;
    String fare;
    String insuranceClassCd;
    String insuranceAmount;
    String incInsureRegNo;
    String estimationFlgCd;
    String incEstRePreNo;
    String increviseFlgCd;
    String increvBase;
    String incRevBaseAmo;
    String taxablesAmoDivto;
    String longKeepBondedWarehouse;
    String deliveryDateExtCd;
    String bpApplicationCd;
    String payMethodDisc;
    String accountNo;
    String securityRegNo1;
    String securityRegNo2;
    String news2;
    String newsShipper;
    String inHouseRefNumber;
    String item;
    String countryOfOriginCd;
    String taxablesAmo;
    String destination;
    String carBondedWarehouse;
    String managedDataFlg;
    String mixedForwarder;
    String agent;
    String diffDays;
    String checkedFlg;
    String collectFare;
    String jpyFare;
    String totalPiece;
    String totalWeight;
    String taxAmo;
    String stationCd;
    String couRefNo;
    String pickupInst;
    String delivInst;
    String shipmentCd;
    String wareHouseClassCd;
    String reportPlaningDate;
    String incEstRePreNo2;
    String incEstRepReNo3;
    String customsOfficeJanitorCd;
    String customsOfficeJanitorReno;
    String customsOfficeJanitorName;
    String inspectionWitness;
    String shippersSecCd;
    String shippersRefNo;
    String advanceRulingValuation1;
    String advanceRulingValuation2;
    String otherLawRecDisc6;
    String otherLawRecDisc7;
    String otherLawRecDisc8;
    String otherLawRecDisc9;
    String otherLawRecDisc10;
    String otherLawRecNo6;
    String otherLawRecNo7;
    String otherLawRecNo8;
    String otherLawRecNo9;
    String otherLawRecNo10;
    String specialPrepareName;
    String arrAirportCd;
    String defaultSet;
    String reportNayClassCd;
    String inspectDocFlag;
    String confirmDocFlag;
    String editHold;
    String customsCheckPriorityEdit;
    String customsCheckCount;
    String customsCheckHold;
    String customsCheckCorrect;
    String customsCheckClass1;
    String customsCheckClass2;
    String idaMessageMakeFlag;
    String hardRecvFlag;
    String userCd;
    String date;
    String editDifficulty;
    String editSeverity;
    String customsCheckPriority;
    String inClassifyClassName;
    String outClassifyClassName;
    String domesticClassifyClassName;
    String specialPrepareName01;
    String specialPrepareName02;
    String specialPrepareName03;
    String specialPrepareName04;
    String specialPrepareName05;
    String specialPrepareName06;
    String specialPrepareName07;
    String specialPrepareName08;
    String specialPrepareName09;
    String specialPrepareName10;
    String arrPortDate;



    /**
     * 初期値設定
     */
    public OAIT001DetailDataDao() {
        this.bwbNo = "00000000000";
        this.cwbNoDeptCd = "000";
        this.remodelingFlg = "0";
        this.currentArrFlt1 = "999999";
        this.currentArrFlt2 = "999999";
        this.editFlg = "0";
        this.documentCheck = "0";
        this.spsDocClassCd = "0";
        this.docInCompleteFlg = "0";
        this.declarationOutputFlg = "0";
        this.carryInObjectFlg = "0";
        this.currentCustomsStatusCd = "IC00100";
        this.currentCargoStatusCd = "IB00000";
        this.currentDocStatusCd = "ID00000";
        this.entryType = "1";
        this.cargoInPiece = "0";
        this.cargoInScanPiece = "0";
        this.permitCargoPiece = "0";
        this.cargoOutPiece = "0";
        this.bondedCargoOutPiece = "0";
        this.bondedCargoInPiece = "0";
        this.docScanPiece = "0";
        this.wareHouseClassCd = "2";
        this.defaultSet = "1";
        this.reportNayClassCd = "00";
        this.inspectDocFlag = "0";
        this.confirmDocFlag = "0";
        this.editHold = "0";
        this.customsCheckPriorityEdit = "0";
        this.customsCheckCount = "1";
        this.customsCheckHold = "0";
        this.customsCheckCorrect = "0";
        this.customsCheckClass1 = "0";
        this.customsCheckClass2 = "0";
        this.idaMessageMakeFlag = "0";
        this.hardRecvFlag = "0";
    }


}
