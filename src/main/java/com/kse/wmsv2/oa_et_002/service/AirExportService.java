package com.kse.wmsv2.oa_et_002.service;

import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kse.wmsv2.oa_et_002.mapper.AirExportAEDataMapper;
import com.kse.wmsv2.oa_et_002.mapper.AirExportAEStatusHistoryMapper;
import com.kse.wmsv2.oa_et_002.mapper.AirExportAmNameMapper;
import com.kse.wmsv2.oa_et_002.mapper.AirExportCmControlMapper;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.log.ApplicationLogger;
import com.kse.wmsv2.oa_et_002.dto.AEStatusHistoryDto;
import com.kse.wmsv2.oa_et_002.dto.AMNameDto;
import com.kse.wmsv2.oa_et_002.dto.AirExportDto;
import com.kse.wmsv2.oa_et_002.dto.ComBoBoxDto;
import com.kse.wmsv2.oa_et_002.dto.request.AEStatusHistoryRequest;
import com.kse.wmsv2.oa_et_002.dto.request.AirExportRequest;
import com.kse.wmsv2.oa_et_002.dto.response.AEStatusHistoryResponse;
import com.kse.wmsv2.oa_et_002.dto.response.AirExportResponse;
import com.kse.wmsv2.oa_et_002.dto.response.MesssageResponse;

@Service
public class AirExportService {

    @Autowired
    private AirExportAmNameMapper airExportAmNameMapper;

    @Autowired
    private AirExportCmControlMapper airExportCmControlMapper;

    @Autowired
    private AirExportAEDataMapper airExportAEDataMapper;

    @Autowired
    private AirExportAEStatusHistoryMapper airExportAEStatusHistoryMapper;


    private final ApplicationLogger logger = new ApplicationLogger(LoggerFactory.getLogger(AirExportService.class));

    /**
     * Get air export data.
     * 
     * @return AirExportRequest
     */
    public AirExportResponse getDataAE(AirExportRequest airExportRequest, String userCd, String authorityCd, String departmentCd) {
        List<AirExportDto> listAirExportDto = this.getListDataExport(airExportRequest, userCd, authorityCd, departmentCd);
        AirExportResponse airExportResponse = new AirExportResponse(listAirExportDto);
        if(listAirExportDto.size() > 0) {
            airExportResponse.setOutCsvEnabled(true);
            airExportResponse.setCountRecord(listAirExportDto.size());
        } else {
            airExportResponse.setCountRecord(0);
            airExportResponse.setSearchEnabled(true);
            airExportResponse.setFocusDate(true);
            airExportResponse.setOutCsvEnabled(false);
        }
        
        return airExportResponse;
    }

    private List<AirExportDto> getListDataExport(AirExportRequest airExportRequest, String userCd, String authorityCd, String departmentCd) {
        

        //1.1 画面上の各項目の入力・選択と以下のパラメータにより、該当のクエリを生成して、データ取得する。
        String operator = this.airExportAmNameMapper.getOperator("0073", airExportRequest.getPermitClassCd());
        
        // 1.2 1.1で取得出来たデータの件数が0以下の場合
        
        airExportRequest.setOperator(operator);
        airExportRequest.setAuthorityCd(authorityCd);
        airExportRequest.setDepartmentCd(departmentCd);
        
        return this.airExportAEDataMapper.getDataAE(AirExportRequest.setPaging(airExportRequest));

    }
    
    /**
     * init.
     * 
     * @return AirImportInitResponse
     */
    public AirExportResponse init(String userCd, String authorityCd, String departmentCd) {


            //1.3 画面項目定義シートにより、各項目に初期値を設定する
        AirExportResponse airExportResponse = new AirExportResponse();
       
        try
        {
            List<ComBoBoxDto> listPermitClassCD = this.airExportAmNameMapper.getListCodeAndAmName("0073");
            List<ComBoBoxDto> listPermitClass = this.airExportAmNameMapper.getListCodeAndAmName("0292");
            List<ComBoBoxDto> listAgent = this.airExportAmNameMapper.getListCodeAndAmName("0404");
            List<ComBoBoxDto> listBondedWareHouseCd = this.airExportAmNameMapper.getListNameClassAndDepartmentCd("0029", departmentCd);

            List<String> listDateCmControl = this.airExportCmControlMapper.getListcontrolDate("1");
            List<String> listNameClass0076 = this.airExportAmNameMapper.getListAmName("0076");
            List<String> listNamePossible = this.airExportAmNameMapper.getListAmName("0086");
            List<String> listNameLA = this.airExportAmNameMapper.getListAmName("0085");
            List<String> listNameESAFlg = this.airExportAmNameMapper.getListAmName("0289");
            List<String> listNameLargeSmallFlg = this.airExportAmNameMapper.getListAmName("0176");
            List<String> listNameClass0274 = this.airExportAmNameMapper.getListAmName("0274");
            List<ComBoBoxDto> listDepPort = this.airExportAmNameMapper.getListNameClassAndDepartmentCd("0030", departmentCd);

            airExportResponse.setListPermitClassCd(listPermitClassCD);
            airExportResponse.setListPermitClass(listPermitClass);
            airExportResponse.setListAgent(listAgent);
            airExportResponse.setListBondedWareHouseCd(listBondedWareHouseCd);
            airExportResponse.setTruckNoDate(listDateCmControl.get(0));
            airExportResponse.setArrival01(listNameClass0076.get(0));
            airExportResponse.setArrival02(listNameClass0076.get(1));
            airExportResponse.setCDB01(listNameClass0076.get(0));
            airExportResponse.setCDB02(listNameClass0076.get(1));
            airExportResponse.setEDA01(listNameClass0076.get(0));
            airExportResponse.setEDA02(listNameClass0076.get(1));
            airExportResponse.setMECEDC01(listNameClass0076.get(0));
            airExportResponse.setMECEDC02(listNameClass0076.get(1));
            airExportResponse.setReserveDeclara01(listNameClass0076.get(0));
            airExportResponse.setReserveDeclara02(listNameClass0076.get(1));
            airExportResponse.setDeclara01(listNameClass0076.get(0));
            airExportResponse.setDeclara02(listNameClass0076.get(1));
            airExportResponse.setPermission01(listNameClass0076.get(0));
            airExportResponse.setPermission02(listNameClass0076.get(1));
            airExportResponse.setHDF01(listNameClass0076.get(0));
            airExportResponse.setHDF02(listNameClass0076.get(1));
            airExportResponse.setConsolidation01(listNameClass0076.get(0));
            airExportResponse.setConsolidation02(listNameClass0076.get(1));
            airExportResponse.setULM01(listNameClass0076.get(0));
            airExportResponse.setULM02(listNameClass0076.get(1));
            airExportResponse.setPossible01(listNamePossible.get(0));
            airExportResponse.setPossible02(listNamePossible.get(1));
            airExportResponse.setLA01(listNameLA.get(0));
            airExportResponse.setLA02(listNameLA.get(1));
            airExportResponse.setESAFlg01(listNameESAFlg.get(0));
            airExportResponse.setESAFlg02(listNameESAFlg.get(1));
            airExportResponse.setLargeSmallFlg01(listNameLargeSmallFlg.get(0));
            airExportResponse.setLargeSmallFlg02(listNameLargeSmallFlg.get(1));
            
            airExportResponse.setLinkTruckNOFrom("");
            airExportResponse.setLinkTruckNOTo("");
            airExportResponse.setAWBNo("");
            airExportResponse.setCWBNo("");
            airExportResponse.setCount(0);
            airExportResponse.setArrivalDate(listNameClass0274.get(0));
            airExportResponse.setShuttleDate(listNameClass0274.get(1));
            airExportResponse.setTracDate(listNameClass0274.get(2));
            airExportResponse.setPermitDate(listNameClass0274.get(3));
            airExportResponse.setReportDate(listNameClass0274.get(4));
            airExportResponse.setChkArrivalDate(true);
            airExportResponse.setListDepPort(listDepPort);
            airExportResponse.setDestination("");
            airExportResponse.setCDBre01(listNameClass0076.get(0));
            airExportResponse.setCDBre02(listNameClass0076.get(1));
            airExportResponse.setBIL01(listNameClass0076.get(0));
            airExportResponse.setBIL02(listNameClass0076.get(1));

            if(authorityCd == "51") {
                airExportResponse.setCmbAgent(departmentCd);
            }

            if(authorityCd == "04") {
                airExportResponse.setCmbBondedWarehouseCd(departmentCd);
            }

            airExportResponse.setSearchEnabled(true);
            airExportResponse.setClearEnabled(false);
            airExportResponse.setOutCsvEnabled(false);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return airExportResponse;
    }

    /**
     * 許可後CSVボタン押下処理
     * 
     * @return ByteArrayInputStream 
     */
    public ByteArrayInputStream csvDataAe(AirExportRequest airExportRequest, String userCd, String authorityCd, String departmentCd) {
        List<AirExportDto> listAirExportDto = this.getListDataExport(airExportRequest, userCd, authorityCd, departmentCd);
        try (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, "SJIS")), 
                CSVFormat.Builder.create().setQuoteMode(QuoteMode.MINIMAL).build());) {

            List<String> header = Arrays.asList(
                "トラック便日付",
                "トラック便",
                "MAWB No.",
                "HAWB No.",
                "仕向地",
                "総個数",
                "総重量",
                "対査日時",
                "対査個数",
                "CDB作成日時",
                "CDB戻り日時",
                "BIL作成日時",
                "EDA作成日時",
                "ハード済",
                "MEC/EDC作成日時",
                "搬入前申告日時",
                "区分",
                "搬入後申告日時",
                "許可日時",
                "HDF作成日時",
                "コンテナNo",
                "ULM作成日時"
            );
            csvPrinter.printRecord(header);

            for (AirExportDto airExportDto : listAirExportDto) {
                List<String> data = Arrays.asList(
                    airExportDto.getTruckNoDate(),
                    airExportDto.getLinkTruckNoName(),
                    airExportDto.getAWBNo(),
                    airExportDto.getCWBNo(),
                    airExportDto.getDestination(),
                    airExportDto.getCarryingPiece(),
                    airExportDto.getCarryingWeight(),
                    airExportDto.getArrivalDate(),
                    airExportDto.getArrivalPiece(),
                    airExportDto.getCDBCreateDate(),
                    airExportDto.getCDBReturnDate(),
                    airExportDto.getBILCreateDate(),
                    airExportDto.getEDACreateDate(),
                    airExportDto.getHard(),
                    airExportDto.getMECEDCCreateDate(),
                    airExportDto.getReserveDeclarationDate(),
                    airExportDto.getPermitClassCd(),
                    airExportDto.getDeclarationDate(),
                    airExportDto.getPermissionDate(),
                    airExportDto.getHDFCreationDate(),
                    airExportDto.getUldNo(),
                    airExportDto.getULMCreateDate()
                    );
                    csvPrinter.printRecord(data);
                
          }
          csvPrinter.flush();
          return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
    }

    /**
     * Get ae status history data.
     * 
     * @return AirExportRequest
     */
    public AEStatusHistoryResponse getDataAEStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest) {
        List<AEStatusHistoryDto> listAEStatusHistoryDto = this.airExportAEStatusHistoryMapper.getDataAEStatusHistory(aEStatusHistoryRequest);
        AEStatusHistoryResponse aEStatusHistoryResponse = new AEStatusHistoryResponse(listAEStatusHistoryDto);
        
        List<ComBoBoxDto> listBusinessClassName = this.airExportAmNameMapper.getListCodeAndAmName("0057");
        List<ComBoBoxDto> list01 = this.airExportAmNameMapper.getListCodeAndAmName("0010");
        List<ComBoBoxDto> list02 = this.airExportAmNameMapper.getListCodeAndAmName("0011");
        List<ComBoBoxDto> list03 = this.airExportAmNameMapper.getListCodeAndAmName("0012");

        aEStatusHistoryResponse.setListBusinessClassName(listBusinessClassName);
        aEStatusHistoryResponse.setList01(list01);
        aEStatusHistoryResponse.setList02(list02);
        aEStatusHistoryResponse.setList03(list03);

        return aEStatusHistoryResponse;
    }

    /**
     * delete ae status history data.
     * 
     * @return MesssageResponse
     */
    @Transactional
    public MesssageResponse deleteAeStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest, String userCd) {
        try
        {
            aEStatusHistoryRequest.setUseHeader(false);
            if(this.airExportAEStatusHistoryMapper.countAEStatusHistory(aEStatusHistoryRequest) <= 0) {
                return new MesssageResponse("対象データが存在しません。");
            }

            if(this.airExportAmNameMapper.getDataAmNameData(aEStatusHistoryRequest.getStatusCd()).size() > 0 ) {
                return new MesssageResponse("選択したステータスによる削除は出来ません。");
            }
        
            this.airExportAEStatusHistoryMapper.deleteAEStatusHistory(aEStatusHistoryRequest);

            if("01".equals(aEStatusHistoryRequest.getBusinessClass()) || "02".equals(aEStatusHistoryRequest.getBusinessClass())) {
                if("EC00800".equals(aEStatusHistoryRequest.getStatusCd())) {
                    aEStatusHistoryRequest.setUpdateFlag(true);
                } else {
                    aEStatusHistoryRequest.setUpdateFlag(false);
                }
                aEStatusHistoryRequest.setDeleteFlag(true);
                aEStatusHistoryRequest.setUserCd(userCd);
                Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
                aEStatusHistoryRequest.setSystemDate(dateNow);
                this.airExportAEDataMapper.updateAEData(aEStatusHistoryRequest);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return new MesssageResponse("削除が完了しました。");
    }

    /**
     * regist ae status history data.
     * 
     * @return MesssageResponse
     */
    @Transactional
    public MesssageResponse registAeStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest, String userCd) {
        try
        {
            aEStatusHistoryRequest.setUseHeader(true);
            aEStatusHistoryRequest.setUserCd(userCd);
            List<AMNameDto> listAMNameDto = this.airExportAmNameMapper.getDataAmNameData(aEStatusHistoryRequest.getHeaderStatusCd());
            if(listAMNameDto.size() <= 0 ) {
                if(this.airExportAEStatusHistoryMapper.countAEStatusHistory(aEStatusHistoryRequest) <= 0) {
                    this.airExportAEStatusHistoryMapper.insertAEStatusHistory(aEStatusHistoryRequest);
                }
            } else {
                AMNameDto aMNameDto = listAMNameDto.get(0);
                List<String> listCharacterItem = Arrays.asList(aMNameDto.getCharacterItem1(),
                    aMNameDto.getCharacterItem2(), 
                    aMNameDto.getCharacterItem3(), 
                    aMNameDto.getCharacterItem4(), 
                    aMNameDto.getCharacterItem5(),
                    aMNameDto.getCharacterItem6(),
                    aMNameDto.getCharacterItem7(),
                    aMNameDto.getCharacterItem8(),
                    aMNameDto.getCharacterItem9(),
                    aMNameDto.getCharacterItem10()
                );
                for (String characterItem : listCharacterItem) {
                    if(StringUtils.isEmpty(characterItem)) {
                        break;
                    }
                    aEStatusHistoryRequest.setHeaderStatusCd(characterItem);
                    if(this.airExportAEStatusHistoryMapper.countAEStatusHistory(aEStatusHistoryRequest) > 0) {
                        continue;
                    }
                    this.airExportAEStatusHistoryMapper.insertAEStatusHistory(aEStatusHistoryRequest);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return new MesssageResponse("登録が完了しました。");
    }


    /**
     * update ae status history data.
     * 
     * @return MesssageResponse
     */
    @Transactional
    public MesssageResponse updateAeStatusHistory(AEStatusHistoryRequest aEStatusHistoryRequest, String userCd) {
        try
        {

            if(this.airExportAmNameMapper.getDataAmNameData(aEStatusHistoryRequest.getHeaderStatusCd()).size() > 0 ) {
                return new MesssageResponse("選択したステータスによる削除は出来ません。");
            }
            aEStatusHistoryRequest.setUserCd(userCd);
            this.airExportAEStatusHistoryMapper.updateAEStatusHistory(aEStatusHistoryRequest);

            if(aEStatusHistoryRequest.getBusinessClass() != aEStatusHistoryRequest.getHeaderBusinessClass()) {
                if(!("03".equals(aEStatusHistoryRequest.getBusinessClass()))) {
                    if("EC00800".equals(aEStatusHistoryRequest.getStatusCd())) {
                        aEStatusHistoryRequest.setUpdateFlag(true);
                    } else {
                        aEStatusHistoryRequest.setUpdateFlag(false);
                    }
                    aEStatusHistoryRequest.setDeleteFlag(false);
                    aEStatusHistoryRequest.setUserCd(userCd);
                    Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
                    aEStatusHistoryRequest.setSystemDate(dateNow);
                    aEStatusHistoryRequest.setUseHeader(false);
                    this.airExportAEDataMapper.updateAEData(aEStatusHistoryRequest);
                }
                if(!("03".equals(aEStatusHistoryRequest.getHeaderBusinessClass()))) {
                    if("EC00800".equals(aEStatusHistoryRequest.getHeaderStatusCd())) {
                        aEStatusHistoryRequest.setUpdateFlag(true);
                    } else {
                        aEStatusHistoryRequest.setUpdateFlag(false);
                    }
                    aEStatusHistoryRequest.setDeleteFlag(false);
                    aEStatusHistoryRequest.setUserCd(userCd);
                    Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
                    aEStatusHistoryRequest.setSystemDate(dateNow);
                    aEStatusHistoryRequest.setUseHeader(true);
                    this.airExportAEDataMapper.updateAEData(aEStatusHistoryRequest);
                }
            } else {
                if(!("03".equals(aEStatusHistoryRequest.getHeaderBusinessClass()))) {
                    if("EC00800".equals(aEStatusHistoryRequest.getHeaderStatusCd())) {
                        aEStatusHistoryRequest.setUpdateFlag(true);
                    } else {
                        aEStatusHistoryRequest.setUpdateFlag(false);
                    }
                    aEStatusHistoryRequest.setDeleteFlag(false);
                    aEStatusHistoryRequest.setUserCd(userCd);
                    Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
                    aEStatusHistoryRequest.setSystemDate(dateNow);
                    aEStatusHistoryRequest.setUseHeader(true);
                    this.airExportAEDataMapper.updateAEData(aEStatusHistoryRequest);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return new MesssageResponse("登録が完了しました。");
    }
}
