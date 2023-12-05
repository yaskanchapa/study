package com.kse.wmsv2.oa_it_002.dto.request;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oa_it_002.dto.CsOptionalServiceDto;

@Data
public class CsOptionalServiceFormRequest {

    List<CsOptionalServiceDto> listCsOptionalServiceDto;

}