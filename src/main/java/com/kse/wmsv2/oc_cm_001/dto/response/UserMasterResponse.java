package com.kse.wmsv2.oc_cm_001.dto.response;

import java.util.List;

import lombok.Data;

import com.kse.wmsv2.oc_cm_001.dto.ComBoBoxDto;
import com.kse.wmsv2.oc_cm_001.dto.UserMasterDto;

@Data
public class UserMasterResponse {

    private List<ComBoBoxDto> listAdministrativeAuthority;
    private List<ComBoBoxDto> listBusinessAuthority;
    private List<ComBoBoxDto> listAffiliatedCompany;
    private List<ComBoBoxDto> listBelongingDepartment;
    private List<ComBoBoxDto> listBusinessGroup;
    private Integer countRecord;
    private List<UserMasterDto> listUserMasterDto;
    private Boolean csvEnable;
    private Boolean deleteEnable;

    public UserMasterResponse() {
    }

    public UserMasterResponse(List<UserMasterDto> listUserMasterDto) {
        this.listUserMasterDto = listUserMasterDto;
    }
}