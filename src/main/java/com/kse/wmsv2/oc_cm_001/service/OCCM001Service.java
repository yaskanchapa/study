package com.kse.wmsv2.oc_cm_001.service;

import java.util.List;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.QuoteMode;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import com.kse.wmsv2.oc_cm_001.mapper.UserMasterCmUserMapper;
import com.kse.wmsv2.oc_cm_001.mapper.CatererSenderMasterAmCCENumberMapper;
import com.kse.wmsv2.oc_cm_001.mapper.RateMasterCmRateMapper;
import com.kse.wmsv2.oc_cm_001.mapper.TraderNoMasterAmCustomerImageMapper;
import com.kse.wmsv2.oc_cm_001.mapper.TraderNoMasterAmCustomerNumberMapper;
import com.kse.wmsv2.oc_cm_001.mapper.UserMasterAmNameMapper;

import com.kse.wmsv2.common.exception.exceptions.BadRequestException;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.log.ApplicationLogger;
import com.kse.wmsv2.common.util.AwsS3;
import com.kse.wmsv2.oc_cm_001.dto.CatererSenderMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.CatererSenderMasterUdpDto;
import com.kse.wmsv2.oc_cm_001.dto.ComBoBoxDto;
import com.kse.wmsv2.oc_cm_001.dto.ImageTraderNoMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.RateMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.TraderNoMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.UserMasterDto;
import com.kse.wmsv2.oc_cm_001.dto.request.CatererSenderMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.RateMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.TraderNoMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.request.UserMasterRequest;
import com.kse.wmsv2.oc_cm_001.dto.response.CatererSenderMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.ImageTraderNoMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.MesssageResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.RateMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.TraderNoMasterResponse;
import com.kse.wmsv2.oc_cm_001.dto.response.UserMasterResponse;

@Service
public class OCCM001Service {

    @Autowired
    private UserMasterAmNameMapper userMasterAmNameMapper;

    @Autowired
    private UserMasterCmUserMapper userMasterCmUserMapper;

    @Autowired
    private RateMasterCmRateMapper rateMasterCmRateMapper;

    @Autowired
    private TraderNoMasterAmCustomerNumberMapper traderNoMasterAmCustomerNumberMapper;

    @Autowired
    private TraderNoMasterAmCustomerImageMapper traderNoMasterAmCustomerImageMapper;

    @Autowired
    private CatererSenderMasterAmCCENumberMapper catererSenderMasterAmCCENumberMapper;

    @Autowired
    private AwsS3 awsS3;

    private final ApplicationLogger logger = new ApplicationLogger(LoggerFactory.getLogger(OCCM001Service.class));

    
    /**
     * Get cm user data.
     * 
     * @return UserMasterDto
     */
    public UserMasterDto getCmUserInfo(String userCd) {
        UserMasterDto userMasterDto;
        try {
            userMasterDto = this.userMasterCmUserMapper.getCmUserInfo(userCd);
            if(userMasterDto == null) {
                userMasterDto = new UserMasterDto();
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return userMasterDto;
    }

    /**
     * Get cm user data.
     * 
     * @return UserMasterResponse
     */
    public UserMasterResponse getDataCmUser(UserMasterRequest userMasterRequest) {
        UserMasterResponse userMasterResponse;
        try {
            List<UserMasterDto> listUserMasterDto = this.userMasterCmUserMapper.getListDataCmUser(UserMasterRequest.setPaging(userMasterRequest));
            userMasterResponse = new UserMasterResponse(listUserMasterDto);
            userMasterResponse.setCountRecord(listUserMasterDto.size());
            if(listUserMasterDto.size() > 0) {
                userMasterResponse.setCsvEnable(true);
                userMasterResponse.setDeleteEnable(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return userMasterResponse;
    }

    /**
     * init.
     * 
     * @return UserMasterResponse
     */
    public UserMasterResponse initUserMaster(String departmentCd) {

        //1.1 画面項目定義シートにより、各項目に初期値を設定する
        UserMasterResponse userMasterResponse = new UserMasterResponse();
        
        try
        {
            List<ComBoBoxDto> listAdministrativeAuthority = this.userMasterAmNameMapper.getListCodeAndAmName("0002");
            List<ComBoBoxDto> listBusinessAuthority = this.userMasterAmNameMapper.getListCodeAndAmName("0003");
            List<ComBoBoxDto> listAffiliatedCompany = this.userMasterAmNameMapper.getListByNameClassAndDepartmentCd("0007", departmentCd);
            List<ComBoBoxDto> listBelongingDepartment = this.userMasterAmNameMapper.getListCodeAndAmName("0008");
            userMasterResponse.setListAdministrativeAuthority(listAdministrativeAuthority);
            userMasterResponse.setListBusinessAuthority(listBusinessAuthority);
            userMasterResponse.setListAffiliatedCompany(listAffiliatedCompany);
            userMasterResponse.setListBelongingDepartment(listBelongingDepartment);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return userMasterResponse;
    }

    /**
     * 削除ボタン押下処理
     * 
     * @return Integer 
     */
    public Integer deleteCmUserByUserCd(String userCd) {
        try
        {
            List<String> listCmUser = this.userMasterCmUserMapper.getListCmUserByUserCd(userCd);
            
            if(listCmUser.size() <= 0) {
                return 0;
            }

            this.userMasterCmUserMapper.deleteCmUserByUserCd(userCd);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return 1;
    }

    /**
     * 更新ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse updateUserMaster(UserMasterDto userMasterDto, String userCd) {
        UserMasterDto userMasterCurrent = this.userMasterCmUserMapper.getCmUserInfo(userMasterDto.getUserCd());
        if(userMasterCurrent == null) 
        {
            logger.error("ユーザー：" + userMasterDto.getUserCd() + "がまだ登録されていない。");
            throw new BadRequestException( new Error("E002", "ユーザー：" + userMasterDto.getUserCd() + "がまだ登録されていない。"));
        }
        try
        {
            if(userMasterCurrent.getPassword() != userMasterDto.getPassword()) {
                userMasterDto.setPrePassword(userMasterCurrent.getPassword());
            }

            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            userMasterDto.setUpdateUserCd(userCd);
            userMasterDto.setUpdateDate(dateNow);

            this.userMasterCmUserMapper.updateCmUserByUserCd(userMasterDto);
            
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("ユーザー：" + userMasterDto.getUserCd() + "の更新が成功しました。");
    }

    /**
     * initRegist.
     * 
     * @return UserMasterResponse
     */
    public UserMasterResponse initFormUserMaster(String departmentCd) {

        UserMasterResponse userMasterResponse = new UserMasterResponse();
        
        try
        {
            List<ComBoBoxDto> listAdministrativeAuthority = this.userMasterAmNameMapper.getListCodeAndAmName("0002");
            List<ComBoBoxDto> listBusinessAuthority = this.userMasterAmNameMapper.getListCodeAndAmName("0003");
            List<ComBoBoxDto> listAffiliatedCompany = this.userMasterAmNameMapper.getListByNameClassAndDepartmentCd("0007", departmentCd);
            List<ComBoBoxDto> listBelongingDepartment = this.userMasterAmNameMapper.getListCodeAndAmName("0008");
            List<ComBoBoxDto> listBusinessGroup = this.userMasterAmNameMapper.getListCodeAndAmName("0221");
            userMasterResponse.setListAdministrativeAuthority(listAdministrativeAuthority);
            userMasterResponse.setListBusinessAuthority(listBusinessAuthority);
            userMasterResponse.setListAffiliatedCompany(listAffiliatedCompany);
            userMasterResponse.setListBelongingDepartment(listBelongingDepartment);
            userMasterResponse.setListBusinessGroup(listBusinessGroup);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return userMasterResponse;
    }

     /**
     * 登録ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse registUserMaster(UserMasterDto userMasterDto, String userCd) {
        try
        {
            List<String> listCmUser = this.userMasterCmUserMapper.getListCmUserByUserCd(userMasterDto.getUserCd());
            if(listCmUser.size() > 0) {
                throw new BadRequestException( new Error("エラー", "ユーザー：" + userMasterDto.getUserCd() + "が常に存在している。"));
            }

            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            userMasterDto.setRegUserCd(userCd);
            userMasterDto.setRegDate(dateNow);

            this.userMasterCmUserMapper.registCmUser(userMasterDto);
            
        } catch (BadRequestException e) {
            logger.error("ユーザー：" + userMasterDto.getUserCd() + "が常に存在している。");
            throw new BadRequestException( new Error("エラー", "ユーザー：" + userMasterDto.getUserCd() + "が常に存在している。"));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse(userMasterDto.getUserCd() + "の登録が成功しました。");
    }

    /**
     * 許可後CSVボタン押下処理
     * 
     * @return ByteArrayInputStream 
     */
    public ByteArrayInputStream csvCmUser(UserMasterRequest userMasterRequest) {
        List<UserMasterDto> listUserMasterDto = this.userMasterCmUserMapper.getListDataCsvCmUser(UserMasterRequest.setPaging(userMasterRequest));
        DateFormat formatterYMD = new SimpleDateFormat("yyyy/MM/dd");
        DateFormat formatterYMDYMDHMS = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        
        try (
            
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(new OutputStreamWriter(out, "SJIS")), 
                CSVFormat.Builder.create().setQuoteMode(QuoteMode.MINIMAL).build());) {

            List<String> header = Arrays.asList(
                "ユーザコード",
                "ユーザー名(漢字)",
                "ユーザー名(カナ)",
                "ユーザー名(英)",
                "管理権限コード",
                "管理権限名",
                "業務権限コード",
                "業務権限名",
                "所属会社コード",
                "所属会社名",
                "所属部署コード",
                "所属部署名",
                "パスワード",
                "使用開始日",
                "使用終了日",
                "ユーザー名(登録者)",
                "システム登録日",
                "ユーザー名(更新者)",
                "システム変更日"
            );
            csvPrinter.printRecord(header);

            for (UserMasterDto userMasterDto : listUserMasterDto) {
                String startDate = "";
                String endDate = "";
                String regDate = "";
                String updateDate = "";

                if(userMasterDto.getStartDate() != null) {
                    startDate = formatterYMD.format(userMasterDto.getStartDate());
                }
                if(userMasterDto.getEndDate() != null) {
                    endDate = formatterYMD.format(userMasterDto.getEndDate());
                }
                if(userMasterDto.getRegDate() != null) {
                    regDate = formatterYMDYMDHMS.format(userMasterDto.getRegDate());
                }
                if(userMasterDto.getUpdateDate() != null) {
                    updateDate = formatterYMDYMDHMS.format(userMasterDto.getUpdateDate());
                }

                List<String> data = Arrays.asList(
                    userMasterDto.getUserCd(),
                    userMasterDto.getUsername(),
                    userMasterDto.getUsernameSyllabary(),
                    userMasterDto.getUsernameEng(),
                    userMasterDto.getUsermanagementAuthorityCd(),
                    userMasterDto.getUserManagementAuthorityName(),
                    userMasterDto.getUserAuthorityCd(),
                    userMasterDto.getUserAuthorityName(),
                    userMasterDto.getUserCompanyCd(),
                    userMasterDto.getUserCompanyName(),
                    userMasterDto.getDepartmentCd(),
                    userMasterDto.getDepartmentName(),
                    userMasterDto.getPrePassword(),
                    startDate,
                    endDate,
                    userMasterDto.getRegUsername(),
                    regDate,
                    userMasterDto.getUpdateUsername(),
                    updateDate
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
     * init.
     * 
     * @return TraderNoMasterResponse
     */
    public TraderNoMasterResponse initTraderNoMaster() {

        TraderNoMasterResponse traderNoMasterResponse = new TraderNoMasterResponse();
        
        try
        {
            List<ComBoBoxDto> listImportAndExportFlag = this.userMasterAmNameMapper.getListCodeAndAmName("0021");
            List<ComBoBoxDto> listPlaceOfUse = this.userMasterAmNameMapper.getListCodeAndAmName("0023");
            traderNoMasterResponse.setListImportAndExportFlag(listImportAndExportFlag);
            traderNoMasterResponse.setListPlaceOfUse(listPlaceOfUse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return traderNoMasterResponse;
    }

    /**
     * init form.
     * 
     * @return TraderNoMasterResponse
     */
    public TraderNoMasterResponse initFormTraderNoMaster() {

        TraderNoMasterResponse traderNoMasterResponse = new TraderNoMasterResponse();
        
        try
        {
            List<ComBoBoxDto> listImportAndExportFlag = this.userMasterAmNameMapper.getListCodeAndAmName("0021");
            List<ComBoBoxDto> listPlaceOfUse = this.userMasterAmNameMapper.getListCodeAndAmName("0023");
            List<ComBoBoxDto> listConditionImport = this.userMasterAmNameMapper.getListCodeAndAmNameWuthOrderBy("0024", "ORDER BY NAMECD ASC ");
            List<ComBoBoxDto> listConditionExport = this.userMasterAmNameMapper.getListCodeAndAmNameWuthOrderBy("0025", "ORDER BY NAMECD ASC ");
            traderNoMasterResponse.setListImportAndExportFlag(listImportAndExportFlag);
            traderNoMasterResponse.setListPlaceOfUse(listPlaceOfUse);
            traderNoMasterResponse.setListConditionImport(listConditionImport);
            traderNoMasterResponse.setListConditionExport(listConditionExport);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return traderNoMasterResponse;
    }

    /**
     * Get am customer number.
     * 
     * @return TraderNoMasterResponse
     */
    public TraderNoMasterResponse getDataAmCustomerNumber(TraderNoMasterRequest traderNoMasterRequest) {
        TraderNoMasterResponse traderNoMasterResponse;
        try {
            List<TraderNoMasterDto> listTraderNoMasterDto = this.traderNoMasterAmCustomerNumberMapper.getDataAmCustomerNumber(TraderNoMasterRequest.setPaging(traderNoMasterRequest));
            traderNoMasterResponse = new TraderNoMasterResponse(listTraderNoMasterDto);
            traderNoMasterResponse.setCountRecord(listTraderNoMasterDto.size());
            if(listTraderNoMasterDto.size() > 0) {
                traderNoMasterResponse.setCsvEnable(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return traderNoMasterResponse;
    }

    /**
     * 登録ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse registTraderNoMaster(TraderNoMasterDto traderNoMasterDto, String userCd) {
        try {
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            String sequenceNumber = String.format("%03d", 
                    Integer.valueOf(this.traderNoMasterAmCustomerNumberMapper.
                                getOcsDeptCdAmCustomerNumber(traderNoMasterDto)) + 1);
            traderNoMasterDto.setOcsdeptCd(traderNoMasterDto.getImpExpFlag() + 
                                traderNoMasterDto.getShiyoBashoFlag() + sequenceNumber);
            traderNoMasterDto.setReguserCd(userCd);
            traderNoMasterDto.setRegDate(dateNow);
            this.traderNoMasterAmCustomerNumberMapper.registTraderNoMaster(traderNoMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("業者情報：" + traderNoMasterDto.getCustomerNo() + "の登録が成功しました。");
    }

     /**
     * Get cm user data.
     * 
     * @return TraderNoMasterDto
     */
    public TraderNoMasterDto detailTraderNoMaster(TraderNoMasterDto traderNoMasterDto) {
        TraderNoMasterDto traderNoMasterDtoRespone;
        try {
            traderNoMasterDtoRespone = this.traderNoMasterAmCustomerNumberMapper.
            detailTraderNoMaster(traderNoMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        if (traderNoMasterDtoRespone == null) {
            throw new BadRequestException( new Error("エラー", "該当のデータが存在しない。ご確認ください。"));
        }
        return traderNoMasterDtoRespone;
    }

    /**
     * 更新ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    @Transactional
    public MesssageResponse updateTraderNoMaster(TraderNoMasterDto traderNoMasterDto, String userCd) {
        String messsage = "";
        try {
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            
            if(this.traderNoMasterAmCustomerNumberMapper.countAmCustomerNumber(traderNoMasterDto) > 0) {
                    traderNoMasterDto.setUpdateUserCd(userCd);
                    traderNoMasterDto.setUpdateDate(dateNow);
                this.traderNoMasterAmCustomerNumberMapper.updateTraderNoMaster(traderNoMasterDto);
                messsage = "業者情報：" + traderNoMasterDto.getCustomerNo() + "の更新が成功しました。";
            } else {
                String sequenceNumber = String.format("%03d", 
                            Integer.valueOf(this.traderNoMasterAmCustomerNumberMapper.
                                        getOcsDeptCdAmCustomerNumber(traderNoMasterDto)) + 1);
                    traderNoMasterDto.setOcsdeptCd(traderNoMasterDto.getImpExpFlag() + 
                                        traderNoMasterDto.getShiyoBashoFlag() + sequenceNumber);
                    traderNoMasterDto.setReguserCd(userCd);
                    traderNoMasterDto.setRegDate(dateNow);
                    this.traderNoMasterAmCustomerNumberMapper.registTraderNoMaster(traderNoMasterDto);
                messsage = "業者情報：" + traderNoMasterDto.getCustomerNo() + "の登録が成功しました。";
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse(messsage);
    }

    /**
     * 削除ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse deleteTraderNoMaster(TraderNoMasterDto traderNoMasterDto) {
        try {
            this.traderNoMasterAmCustomerNumberMapper.deleteTraderNoMaster(traderNoMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("業者情報：" + traderNoMasterDto.getCustomerNo() + "の削除が成功しました。");
    }
    
    /**
     * Get image list
     * 
     * @return ImageTraderNoMasterResponse
     */
    public ImageTraderNoMasterResponse getListImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto) {
        ImageTraderNoMasterResponse imageTraderNoMasterResponse;

        if(this.traderNoMasterAmCustomerNumberMapper.countAmCustomerNumberByCustomerNo(imageTraderNoMasterDto.getCustomerNo()) <= 0) {
            throw new BadRequestException( new Error("エラー", "業者No：" + imageTraderNoMasterDto.getCustomerNo() + "がまだ登録されていない。"));
        }
        
        try {
            List<ImageTraderNoMasterDto> listImageTraderNoMasterDto = this.traderNoMasterAmCustomerImageMapper.getListImageTraderNoMaster(imageTraderNoMasterDto);
            imageTraderNoMasterResponse = new ImageTraderNoMasterResponse(listImageTraderNoMasterDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return imageTraderNoMasterResponse;
    }

    /**
     * Upload file into AWS S3
     *
     * @param keyName
     * @param file
     * @return MesssageResponse
     */
    public MesssageResponse uploadImageTraderNoMaster(String customerNo, String keyName, MultipartFile file, String userCd) {
        
        try {
            Integer seq = this.traderNoMasterAmCustomerImageMapper.getSeqImageTraderNoMaster(customerNo);
            String fileNameWithOutExt = FilenameUtils.removeExtension(keyName);
            String fileName = fileNameWithOutExt + "_" + seq.toString() + ".pdf";
            String pathFileName = "master/customer/" + customerNo;
            awsS3.upload(file.getBytes(),pathFileName + "/" + fileName);

            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            ImageTraderNoMasterDto imageTraderNoMasterDto = new ImageTraderNoMasterDto();
            imageTraderNoMasterDto.setCustomerNo(customerNo);
            imageTraderNoMasterDto.setSeq(seq);
            imageTraderNoMasterDto.setImageName(fileName);
            imageTraderNoMasterDto.setImagePath(pathFileName + "/" + fileName);
            imageTraderNoMasterDto.setRegUserCd(userCd);
            imageTraderNoMasterDto.setRegDate(dateNow);
            imageTraderNoMasterDto.setUpdateUserCd(userCd);
            imageTraderNoMasterDto.setUpdateDate(dateNow);
            this.traderNoMasterAmCustomerImageMapper.registImageTraderNoMaster(imageTraderNoMasterDto);
        } catch (IOException ioe) {
            logger.error("IOException: " + ioe.getMessage());
            throw new InternalServerErrorException( new Error("500", ioe.getMessage()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return new MesssageResponse("Success");
    }

    /**
     * view image
     * 
     * @return byte
     */
    public byte[] viewImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto) {
        try {
            return awsS3.get(imageTraderNoMasterDto.getImagePath());
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
    }
    
    /**
     * delete image
     * 
     * @return MesssageResponse
     */
    public MesssageResponse deleteImageTraderNoMaster(ImageTraderNoMasterDto imageTraderNoMasterDto) {
        try {
            awsS3.delete(imageTraderNoMasterDto.getImagePath());
            this.traderNoMasterAmCustomerImageMapper.deleteImageTraderNoMaster(imageTraderNoMasterDto);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("データ削除が完了");
    }


    /**
     * Get cm user data.
     * 
     * @return RateMasterResponse
     */
    public RateMasterResponse getDataRate(RateMasterRequest rateMasterRequest) {
        RateMasterResponse rateMasterResponse;
        try {
            List<RateMasterDto> listRateMasterDto = this.userMasterAmNameMapper.getDataRate(RateMasterRequest.setPaging(rateMasterRequest));
            rateMasterResponse = new RateMasterResponse(listRateMasterDto);
            rateMasterResponse.setCountRecord(listRateMasterDto.size());
            if(listRateMasterDto.size() > 0) {
                rateMasterResponse.setCsvEnable(true);
                rateMasterResponse.setDeleteEnable(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return rateMasterResponse;
    }

    /**
     * 更新ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    @Transactional
    public MesssageResponse updateRateMaster(RateMasterRequest rateMasterRequest, String userCd) {
        try {
            this.rateMasterCmRateMapper.deleteCmRateByStartDate(rateMasterRequest);
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            for (RateMasterDto rateMasterDto : rateMasterRequest.getListRateMasterDto()) {
                if(rateMasterDto.getRegUserCd() == null || rateMasterDto.getRegUserCd().isEmpty()) {
                    rateMasterDto.setRegUserCd(userCd);
                }

                if(rateMasterDto.getRegDate() == null) {
                    rateMasterDto.setRegDate(dateNow);
                } 

                rateMasterDto.setUpdUserCd(userCd);
                rateMasterDto.setUpdateDate(dateNow);
                this.rateMasterCmRateMapper.insertCmRateByStartDateAndNameCd(rateMasterDto);
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("E202:更新");
    }

    /**
     * 削除ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse deleteRateMaster(RateMasterRequest rateMasterRequest) {
        try {
            this.rateMasterCmRateMapper.deleteCmRateByStartDate(rateMasterRequest);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("M201:" + rateMasterRequest.getStartDate().toString() + " のレートが削除");
    }

    /**
     * init.
     * 
     * @return TraderNoMasterResponse
     */
    public CatererSenderMasterResponse initCatererSenderMaster() {

        CatererSenderMasterResponse catererSenderMasterResponse = new CatererSenderMasterResponse();
        
        try
        {
            List<ComBoBoxDto> listSenderFlag = this.userMasterAmNameMapper.getListCodeAndAmName("0022");
            List<ComBoBoxDto> listPlaceOfUse = this.userMasterAmNameMapper.getListCodeAndAmName("0023");
            catererSenderMasterResponse.setListSenderFlag(listSenderFlag);
            catererSenderMasterResponse.setListPlaceOfUse(listPlaceOfUse);
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return catererSenderMasterResponse;
    }

    /**
     * Get am customer Consigner Cosignee number.
     * 
     * @return CatererSenderMasterResponse
     */
    public CatererSenderMasterResponse getDataAmConsignerCosigneeNumber(CatererSenderMasterRequest catererSenderMasterRequest) {
        CatererSenderMasterResponse catererSenderMasterResponse;
        try {
            catererSenderMasterRequest.setCustomerAddeBlanket(catererSenderMasterRequest.getCustomerAddeBlanket().trim());
            List<CatererSenderMasterDto> listCatererSenderMasterDto = this.catererSenderMasterAmCCENumberMapper.getDataAmConsignerCosigneeNumber(CatererSenderMasterRequest.setPaging(catererSenderMasterRequest));
            catererSenderMasterResponse = new CatererSenderMasterResponse(listCatererSenderMasterDto);
            catererSenderMasterResponse.setCountRecord(listCatererSenderMasterDto.size());
            if(listCatererSenderMasterDto.size() > 0) {
                catererSenderMasterResponse.setCsvEnable(true);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return catererSenderMasterResponse;
    }

    /**
     * 登録ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse registCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto, String userCd) {
        try {
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            String sequenceNumber = String.format("%03d", 
                    Integer.valueOf(this.catererSenderMasterAmCCENumberMapper.
                    getOcsDeptCdAmCCENumber(catererSenderMasterDto)) + 1);
                    catererSenderMasterDto.setOcsdeptCd(catererSenderMasterDto.getCsrCseFlag() + 
                    catererSenderMasterDto.getShiyoBashoFlag() + sequenceNumber);
                    catererSenderMasterDto.setRegUserCd(userCd);
                    catererSenderMasterDto.setRegDate(dateNow);
            this.catererSenderMasterAmCCENumberMapper.registCatererSenderMaster(catererSenderMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("仕出仕向人：" + catererSenderMasterDto.getCustomerNo() + "の登録が成功しました。");
    }

    /**
     * Get cm user data.
     * 
     * @return CatererSenderMasterDto
     */
    public CatererSenderMasterDto detailCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto) {
        CatererSenderMasterDto catererSenderMasterDtoRespone;
        try {
            catererSenderMasterDtoRespone = this.catererSenderMasterAmCCENumberMapper.
            detailCatererSenderMaster(catererSenderMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        if (catererSenderMasterDtoRespone == null) {
            throw new BadRequestException( new Error("エラー", "該当のデータが存在しない。ご確認ください。"));
        }
        return catererSenderMasterDtoRespone;
    }

    /**
     * 削除ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    public MesssageResponse deleteCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto) {
        try {
            this.catererSenderMasterAmCCENumberMapper.deleteCatererSenderMaster(catererSenderMasterDto);

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse("仕出仕向人No：" + catererSenderMasterDto.getCustomerNo() + "を削除しました。");
    }

    /**
     * 更新ボタン押下処理
     * 
     * @return MesssageResponse 
     */
    @Transactional
    public MesssageResponse updateCatererSenderMaster(CatererSenderMasterDto catererSenderMasterDto, String userCd) {
        String messsage = "";
        try {
            Date dateNow = java.sql.Timestamp.valueOf(LocalDateTime.now());
            
            if(this.catererSenderMasterAmCCENumberMapper.countCatererSenderMaster(catererSenderMasterDto) > 0) {
                catererSenderMasterDto.setUpdateUserCd(userCd);
                catererSenderMasterDto.setUpdateDate(dateNow);
                this.catererSenderMasterAmCCENumberMapper.updateCatererSenderMaster(catererSenderMasterDto);
                messsage = "仕出仕向人No：" + catererSenderMasterDto.getCustomerNo() + "の更新が成功しました。";
            } else {
                String sequenceNumber = String.format("%03d", 
                Integer.valueOf(this.catererSenderMasterAmCCENumberMapper.
                getOcsDeptCdAmCCENumber(catererSenderMasterDto)) + 1);
                catererSenderMasterDto.setOcsdeptCd(catererSenderMasterDto.getCsrCseFlag() + 
                catererSenderMasterDto.getShiyoBashoFlag() + sequenceNumber);
                catererSenderMasterDto.setRegUserCd(userCd);
                catererSenderMasterDto.setRegDate(dateNow);
                this.catererSenderMasterAmCCENumberMapper.registCatererSenderMaster(catererSenderMasterDto);
                messsage = "仕出仕向人No：" + catererSenderMasterDto.getCustomerNo() + "の登録が成功しました。";
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        return new MesssageResponse(messsage);
    }
}
