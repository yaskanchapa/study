package com.kse.wmsv2.oa_iw_004.controller;

import com.kse.wmsv2.oa_iw_004.dao.*;
import com.kse.wmsv2.oa_iw_004.dto.OAIW004SearchDto;

import com.kse.wmsv2.oa_iw_004.service.OAIW004InvService;
import com.kse.wmsv2.oa_iw_004.service.OAIW004OutService;
import com.kse.wmsv2.oa_iw_004.service.OAIW004ScreenService;
import com.kse.wmsv2.oa_iw_004.service.OAIW004InService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestController
@RequestMapping("/api/oaiw004")
public class OAIW004Controller {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    OAIW004ScreenService screenServ;

    @Autowired
    OAIW004InService inServ;

    @Autowired
    OAIW004OutService outServ;

    @Autowired
    OAIW004InvService invServ;

    // 蔵置場情報検索
    @RequestMapping("/searchWarehouse")
    public OAIW004SearchWarehouseDao searchWarehouse (OAIW004SearchDto params) {
        OAIW004SearchWarehouseDao returnVal = screenServ.searchWarehouse(params);
        return returnVal;
    }

    // 搬入対象マスター検索
    @RequestMapping("/searchInMawb")
    public List<OAIW004InSearchMawbDao> inSearchMawb(OAIW004SearchDto params) {
        List<OAIW004InSearchMawbDao> returnVal = inServ.inSearchMawb(params);
        return returnVal;
    }

    // 搬出対象マスター検索
    @RequestMapping("/searchOutMawb")
    public List<OAIW004OutSearchMawbDao> outSearchMawb(OAIW004SearchDto params) {
        List<OAIW004OutSearchMawbDao> returnVal = outServ.outSearchMawb(params);
        return returnVal;
    }

    // インベン対象マスター検索
    @RequestMapping("/searchInvMawb")
    public List<OAIW004InvSearchMawbDao> invSearchMawb(OAIW004SearchDto params) {
        List<OAIW004InvSearchMawbDao> returnVal = invServ.invSearchMawb(params);
        return returnVal;
    }

    // 搬入件数取得
    @RequestMapping("/countIn")
    public OAIW004InCountDao inCountList(OAIW004SearchDto params) {
        OAIW004InCountDao returnVal = inServ.inSearchCount(params);
        return returnVal;
    }

    // インベン件数取得
    @RequestMapping("/countInv")
    public OAIW004InvCountDao invCountList(OAIW004SearchDto params) {
        OAIW004InvCountDao returnVal = invServ.invSearchCount(params);
        return returnVal;
    }

    // 搬出件数取得
    @RequestMapping("/countOut")
    public OAIW004OutCountDao outCountList(OAIW004SearchDto params) {
        OAIW004OutCountDao returnVal = outServ.outSearchCount(params);
        return returnVal;
    }

    // 搬入 - 複数個口貨物判定
    @RequestMapping("/checkDataPicecIn")
    public OAIW004InDataPicecDao inCheckDataPicec(OAIW004SearchDto params) {
        OAIW004InDataPicecDao returnVal = inServ.inCheckDataPicec(params);
        return returnVal;
    }

    // 搬出 - 複数個口貨物判定
    @RequestMapping("/checkDataPicecOut")
    public OAIW004OutDataPicecDao outCheckDataPicec(OAIW004SearchDto params) {
        OAIW004OutDataPicecDao returnVal = outServ.outCheckDataPicec(params);
        return returnVal;
    }

    // インベン - 複数個口貨物判定
    @RequestMapping("/checkDataPicecInv")
    public OAIW004InvDataPicecDao invCheckDataPicec(OAIW004SearchDto params) {
        OAIW004InvDataPicecDao returnVal = invServ.invCheckDataPicec(params);
        return returnVal;
    }

    // 搬入処理
    @RequestMapping("/searchIn")
    public OAIW004InReturnValDao inSearchList(OAIW004SearchDto params) {
        OAIW004InReturnValDao returnVal = inServ.inSearch(params);
        return returnVal;
    }

    // 搬出処理
    @RequestMapping("/searchOut")
    public OAIW004OutReturnValDao outSearchList(OAIW004SearchDto params) {
        OAIW004OutReturnValDao returnVal = outServ.outSearch(params);
        return returnVal;
    }

    // インベン処理
    @RequestMapping("/searchInv")
    public OAIW004InvReturnValDao invSearchList(OAIW004SearchDto params) {
        OAIW004InvReturnValDao returnVal = invServ.invSearch(params);
        return returnVal;
    }

}
