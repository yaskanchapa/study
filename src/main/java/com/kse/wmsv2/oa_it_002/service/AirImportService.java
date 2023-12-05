package com.kse.wmsv2.oa_it_002.service;

import java.util.List;

import static com.kse.wmsv2.oa_it_002.constants.Constant.STRING_VALUE_1;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.time.LocalDateTime;

import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kse.wmsv2.oa_it_002.dto.request.AiStatusHistoryRequest;
import com.kse.wmsv2.oa_it_002.dto.request.AirImportRequest;
import com.kse.wmsv2.oa_it_002.dto.request.AmCustomerNumberRequest;
import com.kse.wmsv2.oa_it_002.dto.request.ArrangementDetailRequest;
import com.kse.wmsv2.oa_it_002.dto.request.CsOptionalServiceFormRequest;
import com.kse.wmsv2.oa_it_002.dto.request.HawbNoCheckRequest;
import com.kse.wmsv2.oa_it_002.dto.response.AiStatusHistoryResponse;
import com.kse.wmsv2.oa_it_002.dto.response.AirImportResponse;
import com.kse.wmsv2.oa_it_002.dto.response.AmCustomerNumberResponse;
import com.kse.wmsv2.oa_it_002.dto.response.ArrangementDetailResponse;
import com.kse.wmsv2.oa_it_002.dto.response.MesssageResponse;
import com.kse.wmsv2.oa_it_002.dto.response.HawbNoCheckResponse;
import com.kse.wmsv2.oa_it_002.dto.AirImportDto;
import com.kse.wmsv2.oa_it_002.dto.AiStatusHistoryDto;
import com.kse.wmsv2.oa_it_002.dto.AirImportCSVDto;
import com.kse.wmsv2.oa_it_002.mapper.AirImportAIDataMapper;
import com.kse.wmsv2.oa_it_002.mapper.AirImportAiStatusHistoryMapper;
import com.kse.wmsv2.oa_it_002.mapper.AirImportAmNameMapper;
import com.kse.wmsv2.oa_it_002.mapper.AirImportAmCustomerNumberMapper;
import com.kse.wmsv2.oa_it_002.mapper.AirImportCsOptionalServiceMapper;
import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.log.ApplicationLogger;
import com.kse.wmsv2.oa_it_002.dto.ArrangementDetailDto;
import com.kse.wmsv2.oa_it_002.dto.ComBoBoxDto;
import com.kse.wmsv2.oa_it_002.dto.CsOptionalServiceDto;
import com.kse.wmsv2.oa_it_002.dto.HawbDto;

@Service
public class AirImportService {

    @Autowired
    private AirImportAIDataMapper aIDataMapper;

    @Autowired
    private AirImportAmNameMapper amNameMapper;

    @Autowired
    private AirImportAmCustomerNumberMapper amCustomerNumberMapper;

    @Autowired
    private AirImportCsOptionalServiceMapper csOptionalServiceMapper;

    @Autowired
    private AirImportAiStatusHistoryMapper airImportAiStatusHistoryMapper;

    private final ApplicationLogger logger = new ApplicationLogger(LoggerFactory.getLogger(AirImportService.class));

    /**
     * Get air import data.
     * 
     * @return AirImportResponse
     */
    public AirImportResponse getDataAI(AirImportRequest airImportRequest, String userCd, String authorityCd, String departmentCd) {
        
        //1.1 画面上の各項目の入力・選択と以下のパラメータにより、該当のクエリを生成して、データ取得する。
            
        AirImportResponse airImportResponse;
        String operator= "";    
        if(!airImportRequest.getPermitClassCd().isEmpty()) {
            operator = this.amNameMapper.getOperator("0073", airImportRequest.getPermitClassCd());
            
            if(operator == null) {
                airImportResponse = new AirImportResponse();
                setItemInit(airImportResponse, authorityCd, departmentCd);
                return airImportResponse;
            } 
        }
        // 1.2 1.1で取得出来たデータの件数が0以下の場合

        airImportRequest.setOperator(operator);
        
        if (airImportRequest.getArrHawb() != null && !"".equals(airImportRequest.getArrHawb())) {
            List<HawbDto> listHawb = new ArrayList<>();

            String[] partsArrHawb = airImportRequest.getArrHawb().split(";");
            for (String value : partsArrHawb) {
                String[] parts = value.split(",");
                HawbDto hawbDto = new HawbDto();
                hawbDto.setHawbNo(parts[0]);
                if(parts.length > 1) {
                    hawbDto.setHawbNoDeptCd(parts[1]);
                } else {
                    hawbDto.setHawbNoDeptCd("000");
                }
                listHawb.add(hawbDto);
            }
            airImportRequest.setListHawb(listHawb);
        }

        List<AirImportDto> listAirImportDto = this.aIDataMapper.getDataAI(AirImportRequest.setPaging(airImportRequest));
        Integer countIda = 0;
        Integer countImc = 0;
        Integer countUndeclared = 0;
        Integer countPending = 0;
        Integer totalNumber = 0;
        for (AirImportDto airImportDto : listAirImportDto) {
            if(airImportDto.getStsCount() != null && airImportDto.getStsCount() > 0) {
                if(STRING_VALUE_1.equals(airImportDto.getIdaFlg())) {
                    countIda++;
                } else {
                    countImc++;
                }
            } else {
                countUndeclared++;
            }
            if(airImportDto.getConCount() != null && airImportDto.getConCount() > 0) {
                countPending++;
            }
            if(airImportDto.getCargoPiece() != null && airImportDto.getCargoPiece() > 0) {
                totalNumber++;
            }
        }

        airImportResponse = new AirImportResponse(listAirImportDto);
        airImportResponse.setNumberOfIDA(String.format("%,d", countIda));
        airImportResponse.setNumberOfMIC(String.format("%,d", countImc));
        airImportResponse.setNumberOfPending(String.format("%,d", countPending));
        airImportResponse.setNumberOfUndeclared(String.format("%,d", countUndeclared));
        airImportResponse.setTheTotalNumberOfCase(String.format("%,d", listAirImportDto.size()));
        airImportResponse.setTotalNumber(String.format("%,d", totalNumber));
        airImportResponse.setSearchEnable(true);
        airImportResponse.setClearEnable(true);
        if ("51".equals(authorityCd)) {
            airImportResponse.setAgencySelectionEnable(false);
            airImportResponse.setPdfEnable(false);
        } else {
            airImportResponse.setAgencySelectionEnable(true);
            if(listAirImportDto.size() > 0) {
                airImportResponse.setPdfEnable(true);
            } else {
                airImportResponse.setPdfEnable(false);
            }
        }
        
        airImportResponse.setHawbNoFocus(true);
        if(listAirImportDto.size() > 0) {
            airImportResponse.setCsvKEnable(true);
        } else {
            airImportResponse.setCsvKEnable(false);
        }
        if(airImportRequest.getPermitFlg02() != null && airImportRequest.getPermitFlg02() == 1) {
            if(listAirImportDto.size() > 0) {
                airImportResponse.setCsvLEnable(true);
            } else {
                airImportResponse.setCsvLEnable(false);
            }
        } else {
            airImportResponse.setCsvLEnable(false);
        }
        airImportResponse.setCButtonEnable(true);
        airImportResponse.setDepartmentCd(departmentCd);
        return airImportResponse;
    }
    
    /**
     * init.
     * 
     * @return AirImportInitResponse
     */
    public AirImportResponse init(String userCd, String authorityCd, String userCompanyCd, String departmentCd) {

        //1.3 画面項目定義シートにより、各項目に初期値を設定する
        AirImportResponse airImportResponse = new AirImportResponse();
        try
        {

            List<ComBoBoxDto> listCustomsClearanceSTS = this.amNameMapper.getListCodeAndAmName("0013");
            List<ComBoBoxDto> listDocumentSTS = this.amNameMapper.getListCodeAndAmName("0015");
            List<ComBoBoxDto> listRentSTS = this.amNameMapper.getListCodeAndAmName("0014");
            List<ComBoBoxDto> listCustomsBroker = this.amNameMapper.getListByNameClassAndDepartmentCd("0007", departmentCd);
            List<ComBoBoxDto> listPermitCategory = this.amNameMapper.getListCodeAndAmName("0073");
            List<ComBoBoxDto> listSubmission = this.amNameMapper.getListCodeAndAmName("0292");
            List<ComBoBoxDto> listAgencySelection = this.amNameMapper.getListCodeAndAmName("0403");

            List<String> listName = this.amNameMapper.getListAmName("0036");
            List<String> listNameReport = this.amNameMapper.getListAmName("0075");
            List<String> listNameDeClarationOutput = this.amNameMapper.getListAmName("0076");
            List<String> listObjectDate = this.amNameMapper.getListAmName("0091");
            List<String> listNameSet = this.amNameMapper.getListAmName("0174");
            List<String> listNameReportNo = this.amNameMapper.getListAmName("0178");
            List<String> listPermitFlg = this.amNameMapper.getListAmName("0179");
            List<String> listNameC4regFlg = this.amNameMapper.getListAmName("0180");
            List<String> listNameBigSmallFlg = this.amNameMapper.getListAmName("0176");

            airImportResponse.setTextCustomsTraderCd(userCompanyCd);
            airImportResponse.setTextCustomSplaceCd(departmentCd);
            
                       
            airImportResponse.setListCustomsClearanceSTS(listCustomsClearanceSTS);
            airImportResponse.setListDocumentSTS(listDocumentSTS);
            airImportResponse.setListRentSTS(listRentSTS);
            airImportResponse.setListCustomsBroker(listCustomsBroker);
            airImportResponse.setListPermitCategory(listPermitCategory);
            airImportResponse.setListSubmission(listSubmission);
            airImportResponse.setListAgencySelection(listAgencySelection);
            airImportResponse.setTextObjectDateArrival(listObjectDate.get(0));
            airImportResponse.setTextOobjectDateCarrying(listObjectDate.get(1));
            airImportResponse.setTextObjectDateReport(listObjectDate.get(2));
            airImportResponse.setTextObjectDatePermission(listObjectDate.get(3));
            airImportResponse.setTextObjectDateCarry(listObjectDate.get(4));
            airImportResponse.setTextReportFlg01(listNameReportNo.get(0));
            airImportResponse.setTextReportFlg02(listNameReportNo.get(1));
            airImportResponse.setTextPermitFlg01(listPermitFlg.get(0));
            airImportResponse.setTextPermitFlg02(listPermitFlg.get(1));
            airImportResponse.setTextC4regFlg01(listNameC4regFlg.get(0));
            airImportResponse.setTextC4regFlg02(listNameC4regFlg.get(1));
            airImportResponse.setTextSet(listNameSet.get(0));
            airImportResponse.setTextDeclarationOutput01(listNameDeClarationOutput.get(0));
            airImportResponse.setTextDeclarationOutput02(listNameDeClarationOutput.get(1));
            airImportResponse.setTextImage01(listName.get(0));
            airImportResponse.setTextImage02(listName.get(1));
            airImportResponse.setTextReportMic(listNameReport.get(0));
            airImportResponse.setTextReportIda(listNameReport.get(1));
            airImportResponse.setTextCertificate01(listName.get(0));
            airImportResponse.setTextCertificate02(listName.get(1));
            airImportResponse.setTextBigSmallFlg02(listNameBigSmallFlg.get(0));
            airImportResponse.setTextBigSmallFlg01(listNameBigSmallFlg.get(1));
            airImportResponse.setTextOptional01(listName.get(0));
            airImportResponse.setTextOptional02(listName.get(1));
            setItemInit(airImportResponse, authorityCd, departmentCd);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return airImportResponse;
    }

    /**
     * 許可後CSVボタン押下処理
     * 
     * @return ByteArrayInputStream 
     */
    public ByteArrayInputStream csvAfterPermission(AirImportRequest airImportRequest) {

        String operator = this.amNameMapper.getOperator("0073", airImportRequest.getPermitClassCd());
        airImportRequest.setOperator(operator);
        
        if (airImportRequest.getArrHawb() != null) {
            List<HawbDto> listHawb = new ArrayList<>();

            String[] partsArrHawb = airImportRequest.getArrHawb().split(";");
            for (String value : partsArrHawb) {
                String[] parts = value.split(",");
                HawbDto hawbDto = new HawbDto();
                hawbDto.setHawbNo(parts[0]);
                if(parts.length > 1) {
                    hawbDto.setHawbNoDeptCd(parts[1]);
                } else {
                    hawbDto.setHawbNoDeptCd("000");
                }
                listHawb.add(hawbDto);
            }
            airImportRequest.setListHawb(listHawb);
        }

        List<AirImportDto> listAirImportDto = this.aIDataMapper.getDataAI(AirImportRequest.setPaging(airImportRequest));
        try (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, "SJIS")), 
            CSVFormat.Builder.create().setQuoteMode(QuoteMode.MINIMAL).build());) {
                List<String> header = Arrays.asList(
                "MAWBNo.",
                "行No.",
                "HAWBNo.",
                "輸入者CD",
                "輸入者名",
                "輸入者住所",
                "電話番号",
                "申告番号",
                "許可日",
                "許可時間",
                "審査区分",
                "申告価格",
                "品名",
                "個数",
                "関税",
                "消費税",
                "地方消費税",
                "税額合計",
                "郵便番号",
                "エリア",
                "送り先住所",
                "重量",
                "FLIGHTNo.",
                "FLIGHT Date",
                "Payment",
                "Freight",
                "Contact Person",
                "Country of Origin",
                "SHIPPER'S NAME",
                "Shipper's Address",
                "Currency",
                "Invoice value",
                "申告日",
                "記事",
                "識別",
                "会社名",
                "L/S"
            );
            csvPrinter.printRecord(header);
            for (AirImportDto airImportDto : listAirImportDto) {
                String countHawb = "";
                String hawb1 = "";
                String hawb2 = "";

                String[] parts = airImportDto.getHawbNo().split("/");
                if(parts.length < 2 ){
                    hawb1 = parts[0];
                } else if(parts.length == 2 ){
                    countHawb = "2";
                    hawb1 = parts[0];
                    hawb2 = parts[1];
                } else {
                    hawb1 = parts[0];
                }

                List<AirImportCSVDto> listAirImportCSVDto = this.aIDataMapper.getDataCSVAfterPermission(countHawb, hawb1, hawb2);

                Integer index = 1;
                for (AirImportCSVDto airImportCSVDto : listAirImportCSVDto) {
                List<String> data = Arrays.asList(
                    airImportCSVDto.getAwbNo(),
                    String.valueOf(index),
                    airImportCSVDto.getCwbNo(),
                    airImportCSVDto.getImpCustomerNo(),
                    airImportCSVDto.getImpCustomerName(),
                    airImportCSVDto.getImpCustomerAdd(),
                    airImportCSVDto.getImpCustomerTelNo(),
                    airImportCSVDto.getReportNo(),
                    airImportCSVDto.getPermitDate2Ymd(),
                    airImportCSVDto.getPermitDate2Hm(),
                    airImportCSVDto.getExaminationFlgDisc(),
                    airImportCSVDto.getAmo(),
                    airImportCSVDto.getItem(),
                    airImportCSVDto.getCargopiece(),
                    airImportCSVDto.getSumKanzei(),
                    airImportCSVDto.getSumShouhizei(),
                    airImportCSVDto.getSumChihoushouhizei(),
                    airImportCSVDto.getTaxamo(),
                    airImportCSVDto.getImpCustomerPostcode(),
                    airImportCSVDto.getArea(),
                    airImportCSVDto.getDestAdd(),
                    airImportCSVDto.getCargoWeight(),
                    airImportCSVDto.getLoadingFlt1(),
                    airImportCSVDto.getArrportdateYmd(),
                    airImportCSVDto.getPayment(),
                    airImportCSVDto.getFreight(),
                    airImportCSVDto.getContactPerson(),
                    airImportCSVDto.getConsignorCountry(),
                    airImportCSVDto.getConsignorName(),
                    airImportCSVDto.getConsignorAdd(),
                    airImportCSVDto.getFareCurrencyCd(),
                    airImportCSVDto.getFare(),
                    airImportCSVDto.getReportdateYmd(),
                    airImportCSVDto.getNews2(),
                    airImportCSVDto.getShikibetsu(),
                    airImportCSVDto.getInHouseRefNumber(),
                    airImportCSVDto.getLargeSmallFlg()
                    );
                    csvPrinter.printRecord(data);
                    index++;
                }
          }
          csvPrinter.flush();
          return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
    }

    /**
     * 許可後CSVボタン押下処理
     * 
     * @return ByteArrayInputStream 
     */
    public ByteArrayInputStream csvDataAi(AirImportRequest airImportRequest) {

        String operator = this.amNameMapper.getOperator("0073", airImportRequest.getPermitClassCd());
        airImportRequest.setOperator(operator);
        
        if (airImportRequest.getArrHawb() != null) {
            List<HawbDto> listHawb = new ArrayList<>();

            String[] partsArrHawb = airImportRequest.getArrHawb().split(";");
            for (String value : partsArrHawb) {
                String[] parts = value.split(",");
                HawbDto hawbDto = new HawbDto();
                hawbDto.setHawbNo(parts[0]);
                if(parts.length > 1) {
                    hawbDto.setHawbNoDeptCd(parts[1]);
                } else {
                    hawbDto.setHawbNoDeptCd("000");
                }
                listHawb.add(hawbDto);
            }
            airImportRequest.setListHawb(listHawb);
        }

        List<AirImportDto> listAirImportDto = this.aIDataMapper.getDataAI(AirImportRequest.setPaging(airImportRequest));
        try (
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, "SJIS")), 
                CSVFormat.Builder.create().setQuoteMode(QuoteMode.MINIMAL).build());) {

            List<String> header = Arrays.asList(
                "MAWB No.",
                "HAWB No.",
                "ORG",
                "業者",
                "場所",
                "便名",
                "申告番号",
                "確認/内点",
                "通関STS",
                "書類STS",
                "貨物STS",
                "到着日",
                "搬入日",
                "申告日",
                "許可日",
                "搬出年月日",
                "品名",
                "許可区分",
                "輸入者コード1",
                "輸入者コード2",
                "輸入者コード3",
                "輸入者名",
                "仕出人名",
                "個数",
                "重量",
                "編集者",
                "チェック者"
            );
            csvPrinter.printRecord(header);

            for (AirImportDto airImportDto : listAirImportDto) {
                List<String> data = Arrays.asList(
                    airImportDto.getMawbNo(),
                    airImportDto.getHawbNo(),
                    airImportDto.getOrg(),	
                    airImportDto.getCustomer(),
                    airImportDto.getPlace(),
                    airImportDto.getArrName(),
                    airImportDto.getReportdate(),
                    airImportDto.getNaitenKakunIn(),
                    airImportDto.getNameCustomsStatus(),
                    airImportDto.getNamedocStatus(),
                    airImportDto.getNameCarGoStatus(),
                    airImportDto.getArrivedDate(),
                    airImportDto.getImpDate(),
                    airImportDto.getReportdate(),
                    airImportDto.getPermitDate(),
                    airImportDto.getCarryOutDate(),
                    airImportDto.getItem(),
                    airImportDto.getPermitClassCd(),
                    airImportDto.getImpCustomerNo(),
                    airImportDto.getImpCustomerDeptCd(),
                    airImportDto.getImpCustomerOcsDeptCd(),
                    airImportDto.getImpCustomerName(),
                    airImportDto.getExpCustomerName(),
                    String.valueOf(airImportDto.getCargoPiece()),
                    airImportDto.getCargoWeight(),
                    airImportDto.getEditUser(),
                    airImportDto.getCheckUser()
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
     * Get air import data.
     * 
     * @return AirImportResponse
     */
    public AmCustomerNumberResponse getNameAmCustomerNumber(AmCustomerNumberRequest amCustomerNumberRequest) {
        String customerName = this.amCustomerNumberMapper.getCustomerName(amCustomerNumberRequest);
        AmCustomerNumberResponse airImportResponse = new AmCustomerNumberResponse();
        airImportResponse.setCustomerName(customerName);
        return airImportResponse;
    }

    private void setItemInit(AirImportResponse airImportResponse, String authorityCd, String departmentCd) {
        airImportResponse.setNumberOfIDA("0");
        airImportResponse.setNumberOfMIC("0");
        airImportResponse.setNumberOfPending("0");
        airImportResponse.setNumberOfUndeclared("0");
        airImportResponse.setTheTotalNumberOfCase("0");
        airImportResponse.setTotalNumber("0");
        airImportResponse.setSearchEnable(true);
        airImportResponse.setClearEnable(false);
        airImportResponse.setDepartmentCd(departmentCd);
        if (authorityCd == "51") {
            airImportResponse.setAgencySelectionEnable(false);
        } else {
            airImportResponse.setAgencySelectionEnable(true);
        }
        
        airImportResponse.setHawbNoFocus(true);
        airImportResponse.setCsvKEnable(false);
        airImportResponse.setCsvLEnable(false);
        airImportResponse.setPdfEnable(false);
        airImportResponse.setCButtonEnable(false);
    }

    /**
     * arrangement detail init.
     * 
     * @return AirImportInitResponse
     */
    public ArrangementDetailResponse arrangementDetailInit(ArrangementDetailRequest arrangementDetailRequest) {

        ArrangementDetailResponse arrangementDetailResponse = new ArrangementDetailResponse();
        try
        {
            List<ArrangementDetailDto> listArrangementDetailDto = this.csOptionalServiceMapper.getArrangementDetails(arrangementDetailRequest);
            arrangementDetailResponse.setArrangementDetailDto(listArrangementDetailDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return arrangementDetailResponse;
    }

    /**
     * arrangement detail init.
     * 
     * @return AirImportInitResponse
     */
    @Transactional
    public MesssageResponse updateCsOptionalService(CsOptionalServiceFormRequest csOptionalServiceFormRequest, String userCd) {
        if(csOptionalServiceFormRequest.getListCsOptionalServiceDto() == null || csOptionalServiceFormRequest.getListCsOptionalServiceDto().size() <= 0 ) {
            logger.error("対象データが無い。");
            throw new BadRequestException( new Error("E001", "対象データが無い。"));
        }
        try
        {
            
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());

            for (CsOptionalServiceDto csOptionalServiceDto : csOptionalServiceFormRequest.getListCsOptionalServiceDto()) {
                csOptionalServiceDto.setUpdateUserCd(userCd);
                csOptionalServiceDto.setUpdateDate(dateNow);
                this.csOptionalServiceMapper.updateCsOptionalService(csOptionalServiceDto);
            }
            
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        MesssageResponse messsageResponse = new MesssageResponse("success");
        return messsageResponse;
    }

    /**
     * arrangement detail init.
     * 
     * @return AirImportInitResponse
     */
    public HawbNoCheckResponse checkHawbNo(HawbNoCheckRequest hawbNoCheckRequest) {
        String[] parts = hawbNoCheckRequest.getHawbNo().split("/");
        HawbDto hawbDto = new HawbDto();
        if(parts.length == 2 ){
            hawbDto.setHawbNo(parts[0]);
            hawbDto.setHawbNoDeptCd(parts[1]);
        } else {
            hawbDto.setHawbNo(parts[0]);
            hawbDto.setHawbNoDeptCd("000");
        }
        List<HawbDto> listHawbDto =  this.aIDataMapper.checkHawbNo(hawbDto);
        HawbNoCheckResponse hawbNoCheckResponse = new HawbNoCheckResponse();
        hawbNoCheckResponse.setCountRecord(listHawbDto.size());
        return hawbNoCheckResponse;
    }

    @Transactional
    public MesssageResponse updateHawbNo(HawbNoCheckRequest hawbNoCheckRequest) {
        if(hawbNoCheckRequest.getListHawb() != null) {
            for (String hawbNo : hawbNoCheckRequest.getListHawb()) {
                String[] parts = hawbNo.split("/");
                HawbDto hawbDto = new HawbDto();
                if(parts.length == 2 ){
                    hawbDto.setHawbNo(parts[0]);
                    hawbDto.setHawbNoDeptCd(parts[1]);
                } else {
                    hawbDto.setHawbNo(parts[0]);
                    hawbDto.setHawbNoDeptCd("000");
                }
                this.aIDataMapper.updateHawbNo(hawbDto);
            }
        }
        MesssageResponse messsageResponse = new MesssageResponse("success");
        return messsageResponse;
    }

    /**
     * Get ai status history data.
     * 
     * @return AirImportResponse
     */
    public AiStatusHistoryResponse getDataAiStatusHistory(AiStatusHistoryRequest aiStatusHistoryRequest) {
        List<AiStatusHistoryDto> listAiStatusHistoryDto = this.airImportAiStatusHistoryMapper.getDataAiStatusHistory(aiStatusHistoryRequest);
        AiStatusHistoryResponse aiStatusHistoryResponse = new AiStatusHistoryResponse(listAiStatusHistoryDto);
        return aiStatusHistoryResponse;
    }

}
