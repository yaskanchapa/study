package com.kse.wmsv2.oa_ew_001.controller;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_ew_001.dao.*;
import com.kse.wmsv2.oa_ew_001.dto.*;
import com.kse.wmsv2.oa_ew_001.service.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@RestController
@RequestMapping("/api/oaew001/")
public class OAEW001Controller {

    @Autowired
    OAEW001ScreenService screenServ;

    @Autowired
    OAEW001BILService bilCreateFileService;

    @GetMapping("/search")
    public OAEW001ReturnDto getSearchList(@RequestHeader HttpHeaders header, OAEW001SearchDto params) {
        OAEW001ReturnDto returnVal = screenServ.searchResultList(header,params);
        return returnVal;
    }

    @PostMapping("/bil")
    public OAEW001ReturnDto getBilList(@RequestHeader HttpHeaders header, @RequestBody List<OAEW001SelectedDataDao> params){
        OAEW001ReturnDto resultDto = bilCreateFileService.writeBIL(header,params,"0");
        return resultDto;
    }

    @PostMapping("/recreateBil")
    public OAEW001ReturnDto getRecreateBilList(@RequestHeader HttpHeaders header, @RequestBody List<OAEW001SelectedDataDao> params) {
        OAEW001ReturnDto resultDto = bilCreateFileService.writeBIL(header,params,"1");
        return resultDto;
    }
}

