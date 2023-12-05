package com.kse.wmsv2.oc_cm_001.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RateMasterDto {

     /** 開始日 */
     @NotNull(message = "開始日を入力して下さい。")
    private String startDate;
    
      /** 通貨コード */
     @NotNull(message = "通貨コードを入力して下さい。")
     @NotEmpty(message = "通貨コードを入力して下さい。")
     @Size(max = 3, message = "入力されたデータが不正。")
    private String nameCd;
    ;
     /** 通貨名称 */
    private String name;

    /** レート */
    @Pattern(regexp = "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?",message="レートは、数字を指定してください。")
    private Float rate;

    /** 登録者 */
    private String  regUserCd;
    private String  regUser;

    /** 登録日時 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
    private Date regDate;

    /** 変更者 */
    private String updUserCd;
    private String updUser;

    /** 変更日時 */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss", timezone = "Asia/Tokyo")
    private Date updateDate;

}