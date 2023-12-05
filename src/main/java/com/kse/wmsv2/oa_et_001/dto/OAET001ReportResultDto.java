package com.kse.wmsv2.oa_et_001.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OAET001ReportResultDto {
    String errorFlag;
    String message;
    List<String> folderList;

    List<String> s3FolderList;
    int cntCdb;
    int cntMec;
    int cntEDC;
    int cntEDA;
    int cntFileCDB;
    int cntFileMEC;
    int cntFileEDC;
    int cntFileEDA;

    public OAET001ReportResultDto(){
        this.cntCdb= 0;
        this.cntMec= 0;
        this.cntEDC= 0;
        this.cntEDA= 0;
        this.cntFileCDB= 0;
        this.cntFileMEC= 0;
        this.cntFileEDC= 0;
        this.cntFileEDA= 0;
        this.folderList = new ArrayList<>();
        this.s3FolderList = new ArrayList<>();
    }
}
