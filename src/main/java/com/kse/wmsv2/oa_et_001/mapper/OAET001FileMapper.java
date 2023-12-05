package com.kse.wmsv2.oa_et_001.mapper;

import com.kse.wmsv2.oa_et_001.dao.OAET001FileDetailDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001FileHeadDao;
import com.kse.wmsv2.oa_et_001.dao.OAET001SearchResultDao;
import com.kse.wmsv2.oa_et_001.dto.OAET001ExcelHead;
import com.kse.wmsv2.oa_et_001.dto.OAET001UploadDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface OAET001FileMapper {


    List<OAET001SearchResultDao> searchData(Map<String, String> setParam);

    Map customerMapping(OAET001FileDetailDao param);

    String getMaximumExportValue();

    String getRate(OAET001FileDetailDao detailData);

    Integer checkProhibitionItem(OAET001FileDetailDao detailData);

    Integer getMaximumPiece(OAET001FileDetailDao detailData);

    Integer getMaximumWeight(OAET001FileDetailDao detailData);

    String checkProhibitionCountry(OAET001FileDetailDao detailData);

    String checkExporterCondition(OAET001FileDetailDao detailData);

    Integer checkReShipping(OAET001FileDetailDao detailData);

    String getConsigneeCountry(OAET001ExcelHead head);

    int insertHeadData(OAET001FileHeadDao headerData);

    int insertDetailData(OAET001FileDetailDao detail);

    int insertRepData(OAET001FileDetailDao detail);

    int deleteOldData(OAET001UploadDto fileParams);

    String getCommonRate(Map paramMap);
}
