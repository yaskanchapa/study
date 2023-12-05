package com.kse.wmsv2.oa_iw_004.service;


import org.springframework.stereotype.Service;

@Service
public class OAIW004CommonService {

    // Refactoring
    public boolean checkCodaBar(String hawbNo){
        String regex = "^[a-z].*[a-z]$";
        return hawbNo.matches(regex);
    }

    // Refactoring
    public String castAwbNo(String hawbNo){
        if (hawbNo == null || hawbNo.length() < 2) {
            return "";
        }
        return hawbNo.substring(1, hawbNo.length() - 1);
    }




}
