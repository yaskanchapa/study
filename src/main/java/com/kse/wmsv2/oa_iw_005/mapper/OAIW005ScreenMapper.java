package com.kse.wmsv2.oa_iw_005.mapper;

import com.kse.wmsv2.oa_iw_005.dao.OAIW005DropDownDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005HistoryStatusDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005SearchDao;
import com.kse.wmsv2.oa_iw_005.dto.OAIW005SearchDto;
import com.kse.wmsv2.oa_iw_005.dto.OAIW005StatusDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface OAIW005ScreenMapper {

    List<OAIW005SearchDao> selectSearchAi(OAIW005SearchDto params);

    List<OAIW005HistoryStatusDao> cargoStatus(String cwbNo);

    List<OAIW005StatusDto> getStatusHistoryByCwbno(String cwbNo);

    List<OAIW005DropDownDao> selectCargoName(String departmentCd);

    List<OAIW005DropDownDao> selectCargoStatus();

    List<OAIW005DropDownDao> selectSortCondition();

    List<OAIW005DropDownDao> selectOtherSortCondition();

}

