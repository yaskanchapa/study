package com.kse.wmsv2.oa_it_001.service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.kse.wmsv2.common.error.Error;
import com.kse.wmsv2.common.exception.exceptions.InternalServerErrorException;
import com.kse.wmsv2.common.service.StatusService;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIConditionConfirmDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIConditionDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIDataDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIHeaderDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIStatusHistoryDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001HawbRowInfoDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001HawbScanDataDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001HawbScanDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ItemTextDto;
import com.kse.wmsv2.oa_it_001.mapper.OAIT001HAWBScanMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OAIT001HAWBScanService  {

    @Autowired
    StatusService stsServ;

    @Autowired
    OAIT001HAWBScanMapper mapper ;

    @Autowired
    OAIT001CommonService commonService;

    // HAWBスキャン画面の定数
    final String COND_SCANNED_PART = "1";
    final String COND_UNSCANNED_PART = "2";
    final String COND_ALL = "3";

    public OAIT001HawbScanDto initHawbScan(HttpHeaders headers) {
        OAIT001HawbScanDto oAET001HawbScanDto = new OAIT001HawbScanDto();
        try
        {
            String departmentCd = commonService.getDeptCd(headers);
            
            List<OAIT001ItemTextDto> listCondition = mapper.getListCodeAndAmName("0063");
            List<OAIT001ItemTextDto> listSubject = mapper.getListCodeAndAmName("0064");
            List<OAIT001ItemTextDto> listConfirm = mapper.getListCodeAndAmName("0066");
            List<OAIT001ItemTextDto> listCustomsBroker = mapper.getListByNameClassAndDepartmentCd("0007", departmentCd);
            List<OAIT001ItemTextDto> listCustomsClearanceStatus = mapper.getListCodeAndAmName("0015");

            oAET001HawbScanDto.setListCondition(listCondition);
            oAET001HawbScanDto.setListSubject(listSubject);
            oAET001HawbScanDto.setListConfirm(listConfirm);
            oAET001HawbScanDto.setListCustomsBroker(listCustomsBroker);
            oAET001HawbScanDto.setListCustomsClearanceStatus(listCustomsClearanceStatus);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return oAET001HawbScanDto;
    }

    public OAIT001HawbScanDto getDataList(HttpHeaders headers, String confirmationItem) {
        OAIT001HawbScanDto oAET001HawbScanDto = new OAIT001HawbScanDto();
        try
        {
            List<OAIT001ItemTextDto> listDataList = mapper.getListByNameClassAndNameCd("0067", confirmationItem + "%");
            oAET001HawbScanDto.setListDataList(listDataList);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }

        return oAET001HawbScanDto;
    }

    @Transactional
    public OAIT001HawbScanDto releaseHawbScan(HttpHeaders headers, OAIT001HawbScanDataDto parameter) {
        OAIT001HawbScanDto result = new OAIT001HawbScanDto();
        List<OAIT001HawbRowInfoDto> gridList = parameter.getListHawbNo();
        List<String> messList = new ArrayList<>();
        String departmentCd = commonService.getDeptCd(headers);
        String loginUserCD = commonService.getUserCd(headers);
        String systemDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String idaStatus = (parameter.getIdaFlg() || parameter.getDocCheckAndIDA()) ? "1" : "0" ;
        List<OAIT001AIDataDto> sdataList = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIDataDto> updateQueryList = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIDataDto> tblData1 = new ArrayList<OAIT001AIDataDto>();

        try {
            if(parameter.getMic() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                parameter.setIdaFlg(false);
            } else {
                parameter.setIdaFlg(true);
            }
            if(parameter.getReservation()) {
                parameter.setCmbCustomsClearanceSituation("ID00010");
            } else {
                if(parameter.getRdoClearanceSituation()) {
                    parameter.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
                } else {
                    parameter.setCmbCustomsClearanceSituation(mapper.getTextCustomsClearanceSituation(parameter.getCustomsBroker(), parameter.getCustomsClearancePlace()));
                }
            }
            if (parameter.getListHawbNo().size() == 0 ) {
                messList.add("HAWBNoリストを入れてください。");
                result.setCheckResult(false);
                result.setMessageList(messList);
                return result;
            }
            // 2.2	「ヘッダ.スキャン分を対象」のラジオが選択された場合の処理	
            if (parameter.getRdoCondition().equals(COND_SCANNED_PART)) {
                for (OAIT001HawbRowInfoDto gridRow : gridList) {
                    if(!gridRow.getHawb().equals("")) {
                        // 2.2.1.1		該当レコードのHAWBNoにより、データ取得する
                        String[] cwb= gridRow.getHawb().split("/");
                        List<OAIT001AIDataDto> dataList = mapper.getListAIDataByCondition(cwb[0], cwb.length > 1 ? cwb[1] : "000");
                        // 2.2.1.2		パラメータ.DocCheckAndIDA　＝　True　Or　パラメータ.DocCheck　＝　True　Or　パラメータ.DocCheckAndMIC　＝　True　の場合、下記の処理を実施する	
                        if (parameter.getDocCheckAndIDA() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                            if (dataList.size() == 0) {
                                // 2.2.1.2.1	2.2.1.1の取得データが0件の場合、エラーメッセージを表示して、処理終了する。
                                messList.add("HAWBNo : " + cwb[0] + " は存在しません。");
                                result.setCheckResult(false);
                                result.setMessageList(messList);
                                return result;
                            } else {
                                // 2.2.1.2.2	2.2.1.1の取得データが1件以上の場合、ループで各件のデータを処理する	
                                for (OAIT001AIDataDto data : dataList) {
                                    if (!data.getAwbNo().equalsIgnoreCase(parameter.getAwbNo() ) || !data.getCurrentarrFlt_1().equals(parameter.getArrflt_1()) || !data.getCurrentarrFlt_2().equals(parameter.getArrflt_2())) {
                                        messList.add("HAWBNo："+ data.getCwbNo() +"は他AWB・FLTのCWBです：" + data.getAwbNo() + "-" + data.getCurrentarrFlt_1() + "-"+ data.getCurrentarrFlt_2() + "。");
                                    }
                                }
                                if (messList.size() > 0) {
                                    result.setCheckResult(false);
                                    result.setMessageList(messList);
                                    return result;
                                }
                            }
                        }
                    }
                }
            } else {
                // 2.3	2.2以外の場合の処理
                // 2.3.1	補足シートの「データクエリ③」を実施して、データ取得する。
                List<OAIT001AIDataDto> dataList = mapper.getDataCompareList2(parameter.getAwbNo(), parameter.getArrflt_1(), parameter.getArrflt_2());
                // 2.3.2	2.3.1で取得したデータリストとヘッダ.HAWBNoリストを2重ループして、下記の処理を実施する
                List<OAIT001AIDataDto> deleteList = new ArrayList<OAIT001AIDataDto>();
                for (OAIT001AIDataDto data : dataList) {
                    for (OAIT001HawbRowInfoDto gridRow : gridList) {
                        if (data.getCwbNo().equals(gridRow.getHawb().split("/")[0])) {
                            deleteList.add(data);
                        }
                    }
                }
                for (OAIT001AIDataDto data : deleteList) {
                    dataList.remove(data);
                }

                // 2.3.3	2.3.2の処理が完了後に、2.3.1で取得したデータリストの件数が0件になった場合、エラーメッセージを表示して、処理終了する。
                if (dataList.size() == 0) {
                    messList.add("対象データが存在しません。");
                    result.setCheckResult(false);
                    result.setMessageList(messList);
                    return result;
                }
            }
            // 3.1	「ヘッダ.スキャン分を対象」が選択された場合、下記の処理を実施する。
            if (parameter.getRdoCondition().equals(COND_SCANNED_PART)) { 
                 for (OAIT001HawbRowInfoDto gridRow : gridList) { 
                    if (!gridRow.getHawb().equals("") && !gridRow.getEventFlg().equals("2")) {
                        OAIT001AIDataDto newData = createDataModel2(parameter,"", gridRow.getHawb().split("/"),gridRow.getEventFlg(), departmentCd, idaStatus, 3);
                        sdataList.add(newData);
                    }
                 }
            } else {
                // 3.2	3.1以外の場合、下記の処理を実施する。
                List<OAIT001AIDataDto> dataList = mapper.getDataCompareList2(parameter.getAwbNo(), parameter.getArrflt_1(), parameter.getArrflt_2());
                // 3.2.1	補足シートの「データクエリ③」により、データを取得する。
                for (OAIT001AIDataDto data : dataList) {
                    // 3.2.2.1	「ヘッダ.スキャン以外を対象」が選択された場合、ヘッダ.HAWBNoリストをループして、処理する。		
                    if (parameter.getRdoCondition().equals(COND_UNSCANNED_PART)) {
                        for (OAIT001HawbRowInfoDto gridRow : gridList) {
                            if (data.getCwbNo().equals(gridRow.getHawb().split("/")[0])) { 
                                data.setObjectFlg("2");
                            }
                        }
                    }
                    // 3.2.2.2	取得データ「OBJECTFLG」　!=　’2’の場合、補足シートの「データモデル③」のケース④により、データを作成して、パラメータ.SDataListに追加する。
                    if ( !data.getObjectFlg().equals("2")) {
                        String[] cwb = {data.getCwbNo(), data.getCwbNoDeptCD()};
                        OAIT001AIDataDto newData = createDataModel2(parameter, data.getBwbNo(), cwb, 0, departmentCd, idaStatus, 4);
                        sdataList.add(newData);
                    }
                }
            }
            // 3.3	パラメータ.DocCheckAndIDA　＝　True　Or　パラメータ.DocCheck　＝　True　Or　パラメータ.DocCheckAndMIC　＝　True　の場合、下記の処理を実施する	
            if (parameter.getDocCheckAndIDA() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                String temp = "";
                // 3.3.2	「ヘッダ.スキャン分を対象」が選択された場合、パラメータ.SDataListをループする。	
                if (parameter.getRdoCondition().equals(COND_SCANNED_PART)) {
                    for (OAIT001AIDataDto sData  : sdataList) {
                        if (sData.getInsFlg() != 1) {
                            if (!sData.getCwbNo().equals(temp)) {
                                OAIT001AIDataDto updateData = new OAIT001AIDataDto();
                                updateData.setDocumentCheck("0");
                                updateData.setUpdateUserCd(loginUserCD);
                                updateData.setUpdateDate(systemDate);
                                updateData.setAwbNo(sData.getAwbNo());
                                updateData.setCwbNo(sData.getCwbNo());
                                updateData.setCurrentarrFlt_1(sData.getCurrentarrFlt_1());
                                updateData.setCurrentarrFlt_2(sData.getCurrentarrFlt_2());
                                updateQueryList.add(updateData);
                                temp = sData.getCwbNo();
                            }
                        }
                    }
                } else { // 3.3.3	3.3.2以外の場合、パラメータ.SDataListをループして、処理実施する。	
                    for (OAIT001AIDataDto sData  : sdataList) { 
                        OAIT001AIDataDto newData = new OAIT001AIDataDto();
                        newData.setAwbNo(sData.getAwbNo());
                        newData.setCwbNo(sData.getCwbNo());
                        newData.setCwbNoDeptCD(sData.getCwbNoDeptCD());
                        newData.setDocumentCheck("0");
                        tblData1.add(newData);
                    }
                }
                // 3.4		データ登録のトランザクションを開始する。
                for (OAIT001AIDataDto updateData : updateQueryList) {
                    mapper.updateQueryList2(updateData);
                }
                
                for (OAIT001AIDataDto updateData : tblData1) {
                    mapper.updateAIData(updateData);
                }
            }
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
            log.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        result.setCheckResult(true);
        messList.add("データ解除が完了しました。");
        result.setMessageList(messList);
        return  result;
    }

    @Transactional
    public OAIT001HawbScanDto registHawbScan(HttpHeaders headers, OAIT001HawbScanDataDto parameter) {
        
        OAIT001HawbScanDto result = new OAIT001HawbScanDto();
        String departmentCd = commonService.getDeptCd(headers);
        String loginUserCD = commonService.getUserCd(headers);
        String systemDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String reportPersonCD = mapper.getValueByNameCD("024", departmentCd);
        String weightUnitCD = mapper.getValueByNameCD("025", departmentCd);
        String destination = mapper.getValueByNameCD("039", departmentCd);
        String idaStatus = (parameter.getIdaFlg() || parameter.getDocCheckAndIDA()) ? "1" : "0" ;
        List<OAIT001AIDataDto> sdataList1 = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIConditionDto> sdataList2 = new ArrayList<OAIT001AIConditionDto>();
        List<OAIT001AIStatusHistoryDto> sdataList3 = new ArrayList<OAIT001AIStatusHistoryDto>();
        List<OAIT001AIDataDto> updateQueryList1 = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIDataDto> updateQueryList2 = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIDataDto> tblData1 = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIDataDto> tblData2 = new ArrayList<OAIT001AIDataDto>();
        List<OAIT001AIConditionDto> tblData3 = new ArrayList<OAIT001AIConditionDto>();
        List<OAIT001AIConditionConfirmDto> tblData4 = new ArrayList<OAIT001AIConditionConfirmDto>();
        List<OAIT001AIStatusHistoryDto> tblData5 = new ArrayList<OAIT001AIStatusHistoryDto>();

        try
        {
            if(parameter.getMic() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                parameter.setIdaFlg(false);
            } else {
                parameter.setIdaFlg(true);
            }
            if(parameter.getReservation()) {
                parameter.setCmbCustomsClearanceSituation("ID00010");
            } else {
                if(parameter.getRdoClearanceSituation()) {
                    parameter.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
                } else {
                    String situationString = mapper.getTextCustomsClearanceSituation(parameter.getCustomsBroker(), parameter.getCustomsClearancePlace());
                    parameter.setCmbCustomsClearanceSituation( situationString != null ? situationString : "");
                }
            }

            // 3.1 「ヘッダ.スキャン分を対象」が選択された場合、「ヘッダ.HAWBNoリスト」をループして、下記の処理を実施する
            if(parameter.getRdoCondition().equals(COND_SCANNED_PART)) {
                for (OAIT001HawbRowInfoDto record : parameter.getListHawbNo()) {
                    if(!record.getHawb().equals("") && record.getEventFlg() != 2) {
                        String[] cwb = record.getHawb().split("/");
                        OAIT001AIDataDto row = new OAIT001AIDataDto();
                        row.setAwbNo(parameter.getAwbNo());
                        row.setCurrentarrFlt_1(parameter.getArrflt_1());
                        row.setCurrentarrFlt_2(parameter.getArrflt_2());
                        row.setDetail(parameter.getDetail());
                        row.setCustomBroker(parameter.getCustomsBroker());
                        row.setCustomsClearancePlace(parameter.getCustomsClearancePlace());
                        row.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
                        row.setDepartmentCd(departmentCd);
                        row.setIdaFlg(idaStatus); 
                        row.setCwbNo(cwb[0]);
                        row.setCwbNoDeptCD(cwb.length == 2 ? cwb[1] : "000");
                        row.setInsFlg(record.getEventFlg());
                        row.setDocumentCheck("1");
                        sdataList1.add(row);
                    }
                }
            } else { // 3.2 3.1以外の場合、補足シートの「データクエリ③」により、データを取得して、下記の処理を実施する。
                List<OAIT001AIDataDto> selectResult = mapper.getDataCompareList2(parameter.getAwbNo(), parameter.getArrflt_1(), parameter.getArrflt_2());
                if(parameter.getRdoCondition().equals(COND_UNSCANNED_PART)) {
                    for (OAIT001AIDataDto record : selectResult) {
                        for (OAIT001HawbRowInfoDto row : parameter.getListHawbNo()) {
                            // 3.2.1.1  取得データリストの「CWBNO」がヘッダ.HAWBNoリストの「CWBNO」（Split（”/”）「0」）と一致する場合、取得データの該当レコードの「OBJECTFLG」　＝　”2”　を設定する。
                            String cwbNo = row.getHawb().split("/")[0];
                            if(record.getCwbNo().equals(cwbNo)) {
                                record.setObjectFlg("2");
                            }
                        }
                    }
                }
                // 3.2.2  3.2での取得データリストをループして、レコードの「OBJECTFLG」　!=  "2" の場合、補足シートの「データモデル②」により、ケース②のデータを新規作成して、パラメータ.SDataList1に追加する。
                for (OAIT001AIDataDto record : selectResult) {
                    if(!record.getObjectFlg().equals("2")) {
                        OAIT001AIDataDto row = new OAIT001AIDataDto();
                        row.setAwbNo(parameter.getAwbNo());
                        row.setCurrentarrFlt_1(parameter.getArrflt_1());
                        row.setCurrentarrFlt_2(parameter.getArrflt_2());
                        row.setConfirmationItem(parameter.getConfirmationItem());
                        row.setDetail(parameter.getDetail());
                        row.setCustomBroker(parameter.getCustomsBroker());
                        row.setCustomsClearancePlace(parameter.getCustomsClearancePlace());
                        row.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
                        row.setDepartmentCd(departmentCd);
                        row.setIdaFlg(idaStatus); 
                        row.setBwbNo(record.getBwbNo());
                        row.setCwbNo(record.getCwbNo());
                        row.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        row.setInsFlg(0);
                        row.setDocumentCheck("1");
                        sdataList1.add(row);
                    }
                }
            }
            // 3.3 パラメータ.DocCheckAndIDA　|| パラメータ.DocCheck || パラメータ.DocCheckAndMIC　の場合、条件グループの選択されたラジオにより処理実施する。
            if (parameter.getDocCheckAndIDA() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                // 3.3.1 「ヘッダ.スキャン分を対象」が選択された場合、補足シートの「データクエリ④」を実施して、データ取得する。
                if (parameter.getRdoCondition().equals(COND_SCANNED_PART)) {
                    OAIT001AIHeaderDto selectResult = mapper.getDataQuery4(parameter.getAwbNo(), parameter.getArrflt_1(), parameter.getArrflt_2()).get(0);
                    OAIT001AIDataDto newData = createModal5Row(selectResult, departmentCd, idaStatus, reportPersonCD, destination, weightUnitCD);
                    newData.setUpdateDate(systemDate);
                    newData.setUpdateUserCd(loginUserCD);
                    String cwbNo = "";
                    
                    for (OAIT001AIDataDto record : sdataList1) {
                        // '3.3.1.2.1 レコードの「InsFlg」　＝　1　の場合、3.3.1.1で作成された新規Rowデータの下記変数を変更する。
                        if (record.getInsFlg() == 1) {
                            newData.setCwbNo(record.getCwbNo());
                            newData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                            int num = mapper.getDataQuery6(newData.getAwbNo(), newData.getCwbNo(), newData.getCwbNoDeptCD());
                            if (num < 1) {
                                // 3.3.1.2.1.1.1  3.3.1.2.1で変更された新規Rowデータを利用して、補足シートの「データモデル⑧」により、登録予備のDataRowを作成して、パラメータ.tblData1に追加する。
                                if(newData.getArrPortDate().equals("")) newData.setArrPortDate(null);
                                if(newData.getInvoiceValue() == -1.0) newData.setInvoiceValue(null);
                                if(newData.getFare() == -1.0) newData.setFare(null);
                                tblData1.add(newData);

                                // 3.3.1.2.1.1.2  3.3.1.2.1で変更された新規Rowデータを利用して、補足シートの「データモデル⑨」のケース①により、データを作成して、パラメータ.SDataList2に追加する。
                                OAIT001AIConditionDto conditionData = new OAIT001AIConditionDto();
                                conditionData.setAwbNo(newData.getAwbNo());
                                conditionData.setBwbNo(newData.getBwbNo());
                                conditionData.setCwbNo(newData.getCwbNo());
                                conditionData.setCwbNoDeptCD(newData.getCwbNoDeptCD());
                                conditionData.setBussinessClass("01");
                                conditionData.setStatusCD("IC00100");
                                conditionData.setUpdateUserCD(loginUserCD);
                                conditionData.setUpdateDate(systemDate);
                                sdataList2.add(conditionData);
                            } else { // 3.3.1.2.1.2  '3.3.1.2.1のクエリ結果が1件以上の場合、3.3.1.2で変更された新規Rowデータを利用して、補足シートの「データクエリ⑩」のクエリを生成して、パラメータ.UpdateQueryList1に追加する。
                                OAIT001AIDataDto updateData = new OAIT001AIDataDto();
                                updateData.setAwbNo(newData.getAwbNo());
                                updateData.setCwbNo(newData.getCwbNo());
                                updateData.setCwbNoDeptCD(newData.getCwbNoDeptCD());
                                updateQueryList1.add(updateData);
                            }
                        }
                        // 3.3.1.2.2  レコードの「CWBNo」　!＝　前のレコードの「CWBNo」　の場合、下記の処理を実施する。
                        if (!record.getCwbNo().equals(cwbNo)) {
                            OAIT001AIDataDto updateData = new OAIT001AIDataDto();
                            if (parameter.getDocCheckAndIDA() || parameter.getDocCheckAndMIC()) {
                                // 3.3.1.2.2.1  パラメータ.DocCheckAndIDA　||　パラメータ.DocCheckAndMIC　の場合、レコードのデータにより、補足シートの「データクエリ⑪」のクエリを生成して、パラメータ.UpdateQueryList2に追加する。  
                                updateData.setDocumentCheck(newData.getDocumentCheck());
                                updateData.setIdaFlg(newData.getIdaFlg());
                            } else {
                                // 3.3.1.2.2.2  '3.3.1.2.2.1以外の場合、レコードのデータにより、補足シートの「データクエリ⑫」のクエリを生成して、パラメータ.UpdateQueryList2に追加する。
                                updateData.setDocumentCheck("1");
                                
                            }
                            updateData.setUpdateUserCd(loginUserCD);
                            updateData.setUpdateDate(systemDate);
                            updateData.setAwbNo(record.getAwbNo());
                            updateData.setCwbNo(record.getCwbNo());
                            updateData.setCurrentarrFlt_1(record.getCurrentarrFlt_1());
                            updateData.setCurrentarrFlt_2(record.getCurrentarrFlt_2());
                            updateQueryList2.add(updateData);
                        }
                    }

                    
                } else { // 3.3.2  3.1.1以外の場合、以下の処理を実施する。
                    // 3.3.2.1	パラメータ.SDataList1をループして、処理実施する。	
                    for (OAIT001AIDataDto record : sdataList1) {
                        OAIT001AIDataDto newData = new OAIT001AIDataDto();
                        if(parameter.getDocCheckAndIDA()) {
                            newData.setIdaFlg("1");
                        } 
                        newData.setAwbNo(record.getAwbNo());
                        newData.setCwbNo(record.getCwbNo());
                        newData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        tblData2.add(newData);
                    }
                }
            }
            // 3.4  パラメータ.IDA　||　パラメータ.MIC　の場合、パラメータ.SDataList1をループして、レコードのデータを利用して、補足シートの「データクエリ⑭」のクエリを生成して、パラメータ.UpdateQueryList2に追加する。	
            if (parameter.getIda() || parameter.getMic()) {
                for (OAIT001AIDataDto record : sdataList1) {
                    OAIT001AIDataDto updateData = new OAIT001AIDataDto();
                    updateData.setIdaFlg(record.getIdaFlg());
                    updateData.setUpdateUserCd(loginUserCD);
                    updateData.setUpdateDate(systemDate);
                    updateData.setAwbNo(record.getAwbNo());
                    updateData.setCwbNo(record.getCwbNo());
                    updateData.setCurrentarrFlt_1(record.getCurrentarrFlt_1());
                    updateData.setCurrentarrFlt_2(record.getCurrentarrFlt_2());
                    updateQueryList2.add(updateData);
                }
            }
            // 3.5	パラメータ.Reservation　|| パラメータ.ClearancePlaceChange || パラメータ.rdoClearanceSituation　の場合、下記の処理を実施する。
            if (parameter.getReservation() || parameter.getClearancePlaceChange() || parameter.getRdoClearanceSituation()) {
                // 3.5.1  パラメータ.SDataList1をループして、下記の処理を実施する。	
                for (OAIT001AIDataDto record : sdataList1) {
                    OAIT001AIStatusHistoryDto newData ;
                    // 3.5.1.1	「ヘッダ.スキャン分を対象」が選択された場合、レコードのデータを利用して、補足シートの「データクエリ⑮」でデータ取得する。	
                    if (parameter.getRdoCondition().equals(COND_SCANNED_PART)) {
                        List<OAIT001AIDataDto> dataList =  mapper.getDataQuery15(record.getAwbNo(), record.getCwbNo());
                        if(dataList.size() > 0) {
                            newData = createDataModal16(record, dataList.get(0).getBwbNo(), dataList.get(0).getCwbNoDeptCD());
                        } else {
                            newData = createDataModal16(record, "00000000000", "000");
                        }
                    } else {
                    // 3.5.1.2	3.5.1.1以外の場合、レコードのデータを利用して、補足シートの「データモデル⑯」のケース③により新規データを作成して、パラメータ.SDataList3に追加する。
                        newData = createDataModal16(record, record.getBwbNo(), record.getCwbNoDeptCD());
                    }
                    newData.setUpdateUserCD(loginUserCD);
                    newData.setUpdateDate(systemDate);
                    sdataList3.add(newData);
                }
                // 3.5.2	パラメータ.SDataList3をループして、下記の処理を実施する。	
                for (OAIT001AIStatusHistoryDto record : sdataList3) {
                    // 3.5.2.1	レコードのデータを利用して、補足シートの「データクエリ⑰」でデータを取得する。	
                    List<OAIT001AIConditionDto> dataList = mapper.getDataQuery17(record.getCwbNo(), record.getCwbNoDeptCD());
                    // 3.5.2.2	レコード.Seq　を3.5.2.1の取得データにより設定する。	
                    record.setSeq(dataList.size() > 0 ? dataList.get(0).getSeq() + 1 : 1);
                    // 3.5.2.3	パラメータ.Reservationの場合、下記の処理を実施する。	
                    if (parameter.getReservation()) {
                        OAIT001AIConditionDto newData = new OAIT001AIConditionDto();
                        newData.setCwbNo(record.getCwbNo());
                        newData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        newData.setSeq(record.getSeq());
                        newData.setDocstatusCd(record.getCmbCustomsClearanceSituation());
                        newData.setConfirmPlaceCd("");
                        newData.setConfirmPersonNm("");
                        newData.setContact("");
                        newData.setComment("");
                        newData.setUpdateUserCD(loginUserCD);
                        newData.setUpdateDate(systemDate);
                        tblData3.add(newData);

                        if (!parameter.getConfirmationItem().equals("") && !parameter.getDetail().equals("")) {
                            OAIT001AIConditionConfirmDto aicondConfirmData = new OAIT001AIConditionConfirmDto();
                            aicondConfirmData.setCwbNo(record.getCwbNo());
                            aicondConfirmData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                            aicondConfirmData.setSeq(record.getSeq());
                            aicondConfirmData.setConfirmItemCd(record.getConfirmationItem());
                            aicondConfirmData.setConfirmItemDetailCd(record.getDetail());
                            aicondConfirmData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                            aicondConfirmData.setConfirmPersonCd("");
                            aicondConfirmData.setUpdateUserCD(loginUserCD);
                            aicondConfirmData.setUpdateDate(systemDate);
                            tblData4.add(aicondConfirmData);
                        }
                    }
                    // 3.5.2.4	パラメータ.ClearancePlaceChangeの場合、下記の処理を実施する。	
                    if (parameter.getClearancePlaceChange()) {
                        OAIT001AIConditionDto conditionData = new OAIT001AIConditionDto();
                        OAIT001AIDataDto aiData = new OAIT001AIDataDto();
                        conditionData.setCwbNo(record.getCwbNo());
                        conditionData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        conditionData.setSeq(record.getSeq());
                        conditionData.setDocstatusCd(record.getCmbCustomsClearanceSituation());
                        conditionData.setContact("");
                        conditionData.setComment("");
                        conditionData.setUpdateUserCD(loginUserCD);
                        conditionData.setUpdateDate(systemDate);
                        tblData3.add(conditionData);

                        aiData.setAwbNo(record.getAwbNo());
                        aiData.setCwbNo(record.getCwbNo());
                        aiData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        aiData.setCustomsTraderCd(record.getCustomsBroker());
                        aiData.setCustomsPlaceCd(record.getCustomsClearancePlace());
                        aiData.setReportPersonCd(record.getCustomsClearancePlace());
                        tblData2.add(aiData);
                    }
                    // 3.5.2.5	パラメータ.rdoClearanceSituationの場合、レコードのデータを利用して、補足シートの「データモデル㉒」により、データを作成して、パラメータ.tblData3に追加する。
                    if (parameter.getRdoClearanceSituation()) {
                        OAIT001AIConditionDto conditionData = new OAIT001AIConditionDto();
                        conditionData.setCwbNo(record.getCwbNo());
                        conditionData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                        conditionData.setSeq(record.getSeq());
                        conditionData.setDocstatusCd(record.getCmbCustomsClearanceSituation());
                        conditionData.setConfirmPlaceCd("");
                        conditionData.setConfirmPersonNm("");
                        conditionData.setContact("");
                        conditionData.setComment("");
                        conditionData.setUpdateUserCD(loginUserCD);
                        conditionData.setUpdateDate(systemDate);
                        tblData3.add(conditionData);
                    }
                    // 3.5.2.6	レコードのデータを利用して、補足シートの「データモデル⑨」のケース②により、データを作成して、パラメータ.SDataList2に追加する。
                    OAIT001AIConditionDto conditionData = new OAIT001AIConditionDto();
                    conditionData.setBussinessClass("03");
                    conditionData.setAwbNo(record.getAwbNo());
                    conditionData.setBwbNo(record.getBwbNo());
                    conditionData.setCwbNo(record.getCwbNo());
                    conditionData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                    conditionData.setStatusCD(record.getCmbCustomsClearanceSituation());
                    conditionData.setUpdateUserCD(loginUserCD);
                    conditionData.setUpdateDate(systemDate);
                    sdataList2.add(conditionData);
                }
            }
            // 3.6	パラメータ.SDataList2.Count > 0 　の場合、パラメータ.SDataList2をループして、各レコードのデータを利用して、補足シートの「データモデル㉓」により、データを作成して、パラメータ.tblData5に追加する。
            if(sdataList2.size() > 0) {
                for (OAIT001AIConditionDto record : sdataList2) {
                    OAIT001AIStatusHistoryDto newData =new OAIT001AIStatusHistoryDto();
                    newData.setBussinessClass(record.getBussinessClass());
                    newData.setAwbNo(record.getAwbNo());
                    newData.setBwbNo(record.getBwbNo());
                    newData.setCwbNo(record.getCwbNo());
                    newData.setCwbNoDeptCD(record.getCwbNoDeptCD());
                    newData.setStatusCd(record.getStatusCD());
                    newData.setUpdateUserCD(loginUserCD);
                    newData.setUpdateDate(systemDate);
                    tblData5.add(newData);
                }
            }
            // 3.7	DB更新のトランザクションを開始する
            // 3.7.1	パラメータ.ｔblData1.count  > 0 の場合、パラメータ.tblData1をループして、DBの「AI_DATA」テーブルに登録・更新する。 
            for (OAIT001AIDataDto record : tblData1) {
                mapper.updateAIData(record);
            }
            // 3.7.2	パラメータ.UpdateQueryList1.count > 0 の場合、ループして、各クエリを実施する。	
            for (OAIT001AIDataDto record : updateQueryList1) {
                mapper.updateQueryList1(record);
            }
            // 3.7.3	パラメータ.UpdateQueryList2.count > 0 の場合、ループして、各クエリを実施する。
            for (OAIT001AIDataDto record : updateQueryList2) {
                mapper.updateQueryList2(record);
            }
            // 3.7.4	パラメータ.ｔblData2.count  > 0 の場合、パラメータ.tblData2をループして、DBの「AI_DATA」テーブルに登録・更新する。 
            for (OAIT001AIDataDto record : tblData2) {
                mapper.updateAIData(record);
            }
            // 3.7.5	パラメータ.ｔblData3.count  > 0 の場合、パラメータ.tblData3をループして、DBの「AI_CONDITION」テーブルに登録・更新する。 	
            for (OAIT001AIConditionDto record : tblData3) {
                mapper.updateAICondition(record);
            }
            // 3.7.6	パラメータ.ｔblData4.count  > 0 の場合、パラメータ.tblData4をループして、DBの「AI_CONDITION_CONFIRM」テーブルに登録・更新する。  	
            for (OAIT001AIConditionConfirmDto record : tblData4) {
                mapper.updateAIConditionConfirm(record);
            }
            // 3.7.7	パラメータ.ｔblData5.count  > 0 の場合、パラメータ.tblData5をループして、DBの「AI_STATUS_HISTORY」テーブルに登録・更新する。 
            for (OAIT001AIStatusHistoryDto record : tblData5) {
                mapper.updateAIStatusHistory(record);
            }

        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly(); // SQL失敗時のトランザクション処理
            log.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        result.setCheckResult(true);
        List<String> messList = new ArrayList<String>();
        messList.add("データ登録が成功しました。");
        result.setMessageList(messList);
        return result;
    }

     public OAIT001HawbScanDto checkRegistHawbScan(HttpHeaders headers, OAIT001HawbScanDataDto parameter) {
        OAIT001HawbScanDto hawbScanDto = new OAIT001HawbScanDto();
        List<OAIT001HawbRowInfoDto> listHawbRowInfoDto = parameter.getListHawbNo();
        List<String> errMessList = new ArrayList<>();
        List<String> confirmMessList = new ArrayList<>();

        try {
            if(parameter.getMic() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                parameter.setIdaFlg(false);
            } else {
                parameter.setIdaFlg(true);
            }
            if(parameter.getReservation()) {
                parameter.setCmbCustomsClearanceSituation("ID00010");
            } else {
                if(parameter.getRdoClearanceSituation()) {
                    parameter.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
                } else {
                    parameter.setCmbCustomsClearanceSituation(mapper.getTextCustomsClearanceSituation(parameter.getCustomsBroker(), parameter.getCustomsClearancePlace()));
                }
            }
            if (parameter.getConfirmationItem().equals("")) {
                errMessList.add("確認項目を入力してください。" );
                parameter.setControlCheckFlag(false);
            } else if (parameter.getDetail().equals("")) {
                errMessList.add("詳細を入力してください。" );
                parameter.setControlCheckFlag(false);
            } else if (parameter.getCustomsClearancePlace().equals("")) {
                errMessList.add("通関場所を入力してください。" );
                parameter.setControlCheckFlag(false);
            } else if (parameter.getListHawbNo().size() == 0 && !parameter.getRdoCondition().equals(COND_ALL)) {
                errMessList.add("HAWBNoリストを入力してください。" );
                parameter.setControlCheckFlag(false);
            }
            //3 パラメータ.ControlCheckFlag　＝　Trueの場合 
            if(parameter.getControlCheckFlag()) {
                //3.1 「ヘッダ.スキャン分を対象」のラジオが選択された場合の処理 
                if(parameter.getRdoCondition().equals(COND_SCANNED_PART)){
                    for (OAIT001HawbRowInfoDto record : listHawbRowInfoDto) {
                        //3.1.1 ループでヘッダ.HAWBNoリストの各レコードのCWBNOの値が空白ではない場合、下記の処理を実施する
                        if(!record.getHawb().isBlank()) {
                            String[] parts = record.getHawb().split("/");
                            String cwbNoPart = parts[0];
                            String cwbNoDeptCdPart = "";
                            if(parts.length > 1) {
                                cwbNoDeptCdPart = parts[1];
                            } else {
                                cwbNoDeptCdPart = "000";
                            }
                            //3.1.1.1 該当レコードのHAWBNoにより、データ取得する
                            List<OAIT001AIDataDto> listDataByCWB = mapper.getListAIDataByCondition(cwbNoPart, cwbNoDeptCdPart);
                            //3.1.1.2 パラメータ.DocCheckAndIDA　＝　True　Or　パラメータ.DocCheck　＝　True　Or　パラメータ.DocCheckAndMIC　＝　True　の場合、下記の処理を実施する		
                            if(parameter.getDocCheckAndIDA() || parameter.getDocCheck() || parameter.getDocCheckAndMIC()) {
                                //3.1.1.2.1 3.1.1.1の取得データが0件の場合、確認ダイアログを表示する
                                if(listDataByCWB.size() == 0) {
                                    record.setEventFlg(1);
                                    confirmMessList.add("HAWBNo：" + record.getHawb() + "は登録されていません、登録しますか？");
                                    continue;
                                } else {
                                    for (OAIT001AIDataDto row : listDataByCWB) {
                                        //3.1.1.2.2.1 下記のロジックに結果がTrueの場合、パラメータ.Messageにエラーメッセージをプラスして、レコードの「InsFlg]　＝　2　を設定する
                                        if(!row.getAwbNo().equalsIgnoreCase(parameter.getAwbNo()) || !row.getCurrentarrFlt_1().equals(parameter.getArrflt_1()) || !row.getCurrentarrFlt_2().equals(parameter.getArrflt_2())) {
                                            record.setEventFlg(2);
                                            parameter.setControlCheckFlag(false);
                                            errMessList.add("HAWBNo：" + row.getCwbNo() + "は他AWB・FLTのCWBです：" + row.getAwbNo() + "-" + row.getCurrentarrFlt_1() + "-"+ row.getCurrentarrFlt_2() + "。");
                                        } else {
                                            boolean micRegisted = row.getEditFlg().equals("1") && parameter.getIdaFlg() && row.getIdaFlg().equals("0");
                                            boolean idaRegisted = row.getEditFlg().equals("1") && parameter.getDocCheckAndMIC() && row.getIdaFlg().equals("1");
                                            if (idaRegisted | micRegisted ) {
                                                if(micRegisted) {
                                                    confirmMessList.add("HAWBNo：" + row.getCwbNo() + "は「MIC」登録済みです、変更しますか？");
                                                } else if(idaRegisted) {
                                                    confirmMessList.add("HAWBNo：" + row.getCwbNo() + "は「IDA」登録済みです、変更しますか？");
                                                }
                                                record.setEventFlg(0);
                                            } else {
                                                record.setEventFlg(2);
                                                parameter.setControlCheckFlag(false);
                                                errMessList.add("HAWBNo：" + row.getCwbNo() + "は"+ (row.getIdaFlg().equals("1") ?"IDA" : "MIC") + "で登録済みです。");
                                            }
                                        }
                                    }
                                }
                            }
                            //3.1.1.3 パラメータ.IDA ＝ True Or パラメータ.MIC ＝ True の場合、下記の処理を実施する
                            if(parameter.getIda() || parameter.getMic()) {
                                //3.1.1.3.1 3.1.1.1の取得データが0件の場合、パラメータ.Messageに下記のエラーメッセージを追加する
                                if(listDataByCWB.size() == 0) {
                                    record.setEventFlg(2);
                                    errMessList.add("HAWBNo："+ record.getHawb() +"は存在しません。");
                                    parameter.setControlCheckFlag(false);
                                    continue; 
                                } else {
                                    for (OAIT001AIDataDto row : listDataByCWB) {
                                        if(!row.getAwbNo().equalsIgnoreCase(parameter.getAwbNo()) || !row.getCurrentarrFlt_1().equals(parameter.getArrflt_1()) || !row.getCurrentarrFlt_2().equals(parameter.getArrflt_2())) {
                                            record.setEventFlg(2);
                                            parameter.setControlCheckFlag(false);
                                            errMessList.add("HAWBNo："+ row.getCwbNo() +"は他AWB・FLTのCWBです：" + row.getAwbNo() + "-" + row.getCurrentarrFlt_1() + "-"+ row.getCurrentarrFlt_2() + "。");
                                        } else {
                                            boolean micRegisted = row.getEditFlg().equals("1") && parameter.getIdaFlg() && row.getIdaFlg().equals("0");
                                            boolean idaRegisted = row.getEditFlg().equals("1") && !parameter.getIdaFlg() && row.getIdaFlg().equals("1");
                                            if (idaRegisted | micRegisted ) {
                                                if(micRegisted) {
                                                    confirmMessList.add("HAWBNo：" + row.getCwbNo() + "は「MIC」登録済みです、変更しますか？");
                                                } else if(idaRegisted) {
                                                    confirmMessList.add("HAWBNo：" + row.getCwbNo() + "は「IDA」登録済みです、変更しますか？");
                                                }
                                                record.setEventFlg(0);
                                            } else {
                                                record.setEventFlg(2);
                                                parameter.setControlCheckFlag(false);
                                                errMessList.add("HAWBNo：" + row.getCwbNo() + "は"+ (row.getIdaFlg().equals("1") ?"IDA" : "MIC") + "で登録済みです。");
                                            }
                                        }
                                    }
                                }
                            }
                            //3.1.1.4 パラメータ.Reservation ＝ True Or パラメータ.ClearancePlaceChange ＝ True Or パラメータ.rdoClearanceSituation ＝ True の場合、下記の処理を実施する
                            if(parameter.getReservation() || parameter.getClearancePlaceChange() || parameter.getRdoClearanceSituation()) {
                                // 3.1.1.4.1
                                if(listDataByCWB.size() == 0) {
                                    record.setEventFlg(2);
                                    errMessList.add("HAWBNo：" + record.getHawb() +"は存在しません。");
                                    parameter.setControlCheckFlag(false);
                                    continue;
                                } else {
                                    // 3.1.1.4.2
                                    for (OAIT001AIDataDto row : listDataByCWB) {
                                        if(!row.getAwbNo().equalsIgnoreCase(parameter.getAwbNo()) || !row.getCurrentarrFlt_1().equals(parameter.getArrflt_1()) || !row.getCurrentarrFlt_2().equals(parameter.getArrflt_2())){
                                            record.setEventFlg(2);
                                            parameter.setControlCheckFlag(false);
                                            errMessList.add("HAWBNo："+ row.getCwbNo() +"は他AWB・FLTのCWBです：" + row.getAwbNo() + "-" + row.getCurrentarrFlt_1() + "-"+ row.getCurrentarrFlt_2() + "。");
                                        } else {
                                            record.setEventFlg(0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else { // 3.2 : 3.1以外の場合の処理
                    //3.2.1 パラメータ.num  = 0 を定義して、下記のクエリで、データ取得する。
                    Integer num = 0 ;
                    List<OAIT001AIDataDto> listDataCompare2 = mapper.getDataCompareList2(parameter.getAwbNo(), parameter.getArrflt_1(), parameter.getArrflt_2());

                    // 3.2.2 「ヘッダ.スキャン以外を対象」のラジオが選択された場合、下記の処理を実施する
                    if(parameter.getRdoCondition().equals(COND_UNSCANNED_PART)) {
                        for (OAIT001AIDataDto compareRow : listDataCompare2) {
                            for (OAIT001HawbRowInfoDto gridVRow : listHawbRowInfoDto) {
                                String cwbNo = gridVRow.getHawb().split("/")[0];
                                if(compareRow.getCwbNo().equals(cwbNo)) {
                                    num++;
                                } else {
                                    gridVRow.setEventFlg(0);
                                }
                            }
                        }
                    }
                    // 3.2.3 3.2.1の取得データの数とパラメータ.numが一致の場合、パラメータ.Messageに下記のメッセージを追加する
                    if(listDataCompare2.size() == num) {
                        errMessList.add("対象データが存在しません。");
                        parameter.setControlCheckFlag(false);
                    }
                }
            }
            
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException( new Error("500", e.getMessage()));
        }
        hawbScanDto.setListHawbRowInfoDto(listHawbRowInfoDto);
        hawbScanDto.setMessageList(parameter.getControlCheckFlag() ? confirmMessList : errMessList);
        hawbScanDto.setCheckResult(parameter.getControlCheckFlag());
        return hawbScanDto;
     }

    private OAIT001AIDataDto createModal5Row(OAIT001AIHeaderDto selectResult, String departmentCd, String idaStatus, String reportPersonCD, String destination, String weightUnitCD) {
        OAIT001AIDataDto row = new OAIT001AIDataDto();
        row.setAwbNo(selectResult.getAwbNo());
        row.setCwbNo("");
        row.setCwbNoDeptCD("000");
        row.setRemodelingFlg("0");
        row.setBwbNo("00000000000");
        row.setCurrentarrFlt_1(selectResult.getArrFlt1());
        row.setCurrentarrFlt_2(selectResult.getArrFlt2());
        row.setGetPort(selectResult.getGetport());
        row.setShipmentCd(selectResult.getShipmentCD());
        row.setArrPortDate(selectResult.getArrportDate());
        row.setCustomsTraderCd("KKJ");
        row.setCustomsPlaceCd(departmentCd);
        row.setBondedWarehouseCd(selectResult.getClearPlanPlace());
        row.setEditFlg("0");
        row.setDocumentCheck("0");
        row.setOrigin(selectResult.getCatereringPlace());
        row.setSpsdocClassCd("0");
        row.setDocIncompleteFlg("0");
        row.setDeclarationOutputFlg("0");
        row.setCarryInObjectFlg("0");
        row.setWarehouseClassCd(selectResult.getBondedwareClass());
        row.setLocationCd("SPT,");
        row.setCurrentCustomsStatusCd("IC00100");
        row.setCurrentCargoStatusCd("IB00000");
        row.setCurrentDocStatusCd("ID00000");
        row.setInClassifyClassName("");
        row.setOutClassifyClassName("");
        row.setDomesticClassifyClassName("");
        row.setSpecialPrepareName01("");
        row.setEntryType(selectResult.getEntryType());
        row.setIdaFlg(idaStatus);
        row.setReportCondition(selectResult.getReportCondition());
        row.setReportDivCd(selectResult.getCustomDiv());
        row.setReportDepCd(selectResult.getPresent());
        row.setReportPersonCd(reportPersonCD);
        row.setDestination(destination);
        row.setAgent(selectResult.getCatereringPlace());
        row.setCargoPiece(-1);
        row.setCargoInPiece(0);
        row.setCargoInScanPiece(0);
        row.setWeightUnitCd(weightUnitCD);
        row.setInvoiceValue(-1.0);
        row.setFare(-1.0);
        row.setCarBondedWarehouse(selectResult.getClearPlanPlace());
        row.setBondedWarehouse(selectResult.getBondedwareHouse());
        row.setCustomsPlaceLoginCd(departmentCd);
        row.setDefaultSet("1");
        row.setInspectDocFlag("0");
        row.setConfirmDocFlag("0");
        row.setEditHold("0");
        row.setCustomsCheckCount("1");
        row.setCustomsCheckHold("0");
        row.setCustomsCheckCorrect("0");
        row.setCustomsCheckClass1("0");
        row.setCustomsCheckClass2("0");
        row.setIdaMessageMakeFlag("0");
        row.setHardRecvFlag("0");
        return row;
    }

    private OAIT001AIStatusHistoryDto createDataModal16(OAIT001AIDataDto record, String bwbNo, String cwbNoDeptCD) {
        OAIT001AIStatusHistoryDto newData = new OAIT001AIStatusHistoryDto();
        newData.setAwbNo(record.getAwbNo());
        newData.setBwbNo(bwbNo);
        newData.setCwbNo(record.getCwbNo());
        newData.setCwbNoDeptCD(cwbNoDeptCD);
        newData.setImportExportClass("1");
        newData.setDocumentCheck(record.getDocumentCheck());
        newData.setSeq(0);
        newData.setDepartMentCd(record.getDepartmentCd());
        newData.setInsFlg(record.getInsFlg());
        newData.setIdaFlg(record.getIdaFlg());
        newData.setConfirmationItem(record.getConfirmationItem());
        newData.setDetail(record.getDetail());
        newData.setCustomsBroker(record.getCustomBroker());
        newData.setCustomsClearancePlace(record.getCustomsClearancePlace());
        newData.setCmbCustomsClearanceSituation(record.getCmbCustomsClearanceSituation());
        return newData;
    }

    private OAIT001AIDataDto createDataModel2(OAIT001HawbScanDataDto parameter, String bwbNo, String[] cwb, Integer insFlg, String departmentCD, String idaStatus, Integer caseNum) {
        OAIT001AIDataDto data = new OAIT001AIDataDto();
        data.setAwbNo(parameter.getAwbNo());
        data.setCurrentarrFlt_1(parameter.getArrflt_1());
        data.setCurrentarrFlt_2(parameter.getArrflt_2());
        data.setConfirmationItem(parameter.getConfirmationItem());
        data.setDetail(parameter.getDetail());
        data.setCustomBroker(parameter.getCustomsBroker());
        data.setCustomsClearancePlace(parameter.getCustomsClearancePlace());
        data.setCmbCustomsClearanceSituation(parameter.getCmbCustomsClearanceSituation());
        data.setCwbNoDeptCD(departmentCD);
        data.setIdaFlg(idaStatus);
        switch (caseNum) {
            case 1:
            data.setCwbNo(cwb[0]);
            data.setCwbNoDeptCD(cwb.length > 1 ? cwb[1] : "000");
            data.setInsFlg(insFlg);
            data.setDocumentCheck("1");
            case 2:
            data.setBwbNo(bwbNo);
            data.setCwbNo(cwb[0]);
            data.setCwbNoDeptCD(cwb.length > 1 ? cwb[1] : "000");
            data.setInsFlg(0);
            data.setDocumentCheck("1");
            case 3:
            data.setCwbNo(cwb[0]);
            data.setCwbNoDeptCD(cwb.length > 1 ? cwb[1] : "000");
            data.setInsFlg(0);
            data.setDocumentCheck("0");
            case 4:
            data.setBwbNo(bwbNo);
            data.setCwbNo(cwb[0]);
            data.setCwbNoDeptCD(cwb.length > 1 ? cwb[1] : "000");
            data.setInsFlg(0);
            data.setDocumentCheck("0");
        }
        return data;
    }
}