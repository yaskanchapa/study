package com.kse.wmsv2.oa_iw_001.dto;

import lombok.Data;

@Data
public class OAIW001GetDefaultStatusAmName1Dto {
    String nameClass; //名称分類
    String departmentCd; //所属コード
    String nameCd; // 名称コード
    String columnValue; // 初期値
    String columnName; // 対象項目
}
