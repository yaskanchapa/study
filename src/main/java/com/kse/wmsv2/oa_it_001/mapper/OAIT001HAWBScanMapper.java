package com.kse.wmsv2.oa_it_001.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kse.wmsv2.oa_it_001.dto.OAIT001AIConditionConfirmDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIConditionDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIDataDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIHeaderDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001AIStatusHistoryDto;
import com.kse.wmsv2.oa_it_001.dto.OAIT001ItemTextDto;

@Mapper
public interface OAIT001HAWBScanMapper {
    
    List<OAIT001ItemTextDto> getListCodeAndAmName(String nameClass);

    List<OAIT001ItemTextDto> getListByNameClassAndDepartmentCd(@Param("nameClass") String nameClass,@Param("departmentCd") String departmentCd);

    List<OAIT001ItemTextDto> getListByNameClassAndNameCd(@Param("nameClass") String nameClass,@Param("nameCd") String nameCd);

    String getTextCustomsClearanceSituation(@Param("customBroker") String customBroker,@Param("CustomsClearancePlace") String CustomsClearancePlace);

    List<OAIT001AIDataDto> getListAIDataByCondition(@Param("cwbNo") String cwbNo,@Param("cwbNoDeptCd") String ncwbNoDeptCdameCd);

    // 3.2.1 「スキャン分を対象」以外が選択された場合の処理データ
    List<OAIT001AIDataDto> getDataCompareList2(@Param("awbNo") String awbNo, @Param("currFlt1") String currFlt1, @Param("currFlt2") String currFlt2);

    String getValueByNameCD (@Param("nameCD") String nameCD, @Param("deptCD") String deptCD);

    List<OAIT001AIHeaderDto> getDataQuery4(@Param("awbNo") String awbNo, @Param("arrFlt1") String arrFlt1, @Param("arrFlt2") String arrFlt2);

    Integer getDataQuery6(@Param("awbNo") String awbNo, @Param("cwbNo") String cwbNo, @Param("cwbNoDeptCD") String cwbNoDeptCD);

    List<OAIT001AIDataDto> getDataQuery15(@Param("awbNo") String awbNo, @Param("cwbNo") String cwbNo);

    List<OAIT001AIConditionDto> getDataQuery17(@Param("cwbNo") String cwbNo, @Param("cwbNoDeptCD") String cwbNoDeptCD);

    Integer updateQueryList1(OAIT001AIDataDto dataObj);

    Integer updateQueryList2(OAIT001AIDataDto dataObj);

    Integer updateAIData(OAIT001AIDataDto dataObj);

    Integer updateAICondition(OAIT001AIConditionDto dataObj);

    Integer updateAIConditionConfirm(OAIT001AIConditionConfirmDto dataObj);

    Integer updateAIStatusHistory(OAIT001AIStatusHistoryDto dataObj);
}
