package com.kse.wmsv2.oa_it_002.dto;

import java.util.Date;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CsOptionalServiceDto {

    @NotNull(message = "輸入輸出クラスは必須です。")
    private String importExportClass;

    @NotNull(message = "特別手配Noは必須です。")
    private String optionalServiceNo;

    @NotNull(message = "CWBNoは必須です。")
    private String cwbNo;

    @NotNull(message = "OPコードは必須です。")
    private String optionalServiceCd;

    @NotNull(message = "確は必須です。")
    private Integer aposManagementFlag;

    private String updateUserCd;

    private Date updateDate;

}