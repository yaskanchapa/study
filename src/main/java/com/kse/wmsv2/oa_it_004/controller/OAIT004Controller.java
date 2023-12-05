package com.kse.wmsv2.oa_it_004.controller;


import com.kse.wmsv2.common.dto.Auth;
import com.kse.wmsv2.oa_it_004.dto.OAIT004OneShotDto;
import com.kse.wmsv2.oa_it_004.service.OAIT004ScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/oait004")
public class OAIT004Controller {

    @Autowired
    OAIT004ScreenService screenServ;

    @PostMapping("/searchOneShotDefList")
    public Map<String, List> searchOneShotDefList(@RequestHeader HttpHeaders headers){
        return screenServ.searchOneShotDefList(headers);
    }

    @PostMapping("/insertOneShot")
    public Map<String, String> insertOneShot(@RequestHeader HttpHeaders headers,@RequestBody OAIT004OneShotDto dto){
        return screenServ.insertOneShot(headers,dto);
    }

    @PostMapping("/deleteOneShot")
    public Map<String, String> deleteOneShot(@RequestHeader HttpHeaders headers,@RequestBody OAIT004OneShotDto dto){
        return screenServ.deleteOneShot(headers,dto);
    }

}
