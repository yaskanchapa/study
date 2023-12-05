package com.kse.wmsv2.oa_it_001.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class OAIT001ReportResultDto {

    String errorFlag;
    String message;
    List<String> folderList;
    List<String> s3FolderList;
    int cntFolder;
    int cntFile;


    public OAIT001ReportResultDto(){
        this.cntFolder= 0;
        this.cntFile= 0;
        this.folderList = new ArrayList<>();
        this.s3FolderList = new ArrayList<>();
    }
}
