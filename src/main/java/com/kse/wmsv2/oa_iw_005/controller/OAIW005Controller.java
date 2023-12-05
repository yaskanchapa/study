package com.kse.wmsv2.oa_iw_005.controller;

import com.kse.wmsv2.common.util.RedisUtil;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005DropDownDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005HistoryStatusDao;
import com.kse.wmsv2.oa_iw_005.dao.OAIW005SearchDao;
import com.kse.wmsv2.oa_iw_005.dto.*;
import com.kse.wmsv2.oa_iw_005.service.OAIW005ScreenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/oaiw005") // メモ：参照先はOAET002
public class OAIW005Controller {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OAIW005ScreenService oaiw005ScreenService;

    @Autowired
    private RedisUtil redisUtil;


    @GetMapping("/dropDowns")
    public Map<String, List<OAIW005DropDownDao>> getDropDowns(@RequestHeader HttpHeaders header) {

        String accessToken = (String)header.get("authorization").get(0);
        String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        Map<String, List<OAIW005DropDownDao>> dropdowns = new HashMap<>();
        dropdowns.put("cargoName", oaiw005ScreenService.selectCargoName(departmentCd));
        dropdowns.put("cargoStatus", oaiw005ScreenService.selectCargoStatus());
        dropdowns.put("inClassifyClassName", oaiw005ScreenService.selectSortCondition());
        dropdowns.put("permitClassCd", oaiw005ScreenService.selectOtherSortCondition());
        return dropdowns;
    }

    @GetMapping("/historyStatus")
    public List<OAIW005HistoryStatusDao> cargoStatus(@RequestParam String cwbNo) {
        List<OAIW005HistoryStatusDao> statusList = oaiw005ScreenService.cargoStatus(cwbNo);
        return statusList;
    }

    @PostMapping("/search")
    public List<OAIW005SearchDao> searchSelect(@RequestBody OAIW005SearchDto params) {
        List<OAIW005SearchDao> result = oaiw005ScreenService.selectSearchAi(params);
        return result;
    }
}