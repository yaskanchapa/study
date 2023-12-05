package com.kse.wmsv2.oa_ew_002.controller;

import com.kse.wmsv2.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import com.kse.wmsv2.oa_ew_002.dao.*;
import com.kse.wmsv2.oa_ew_002.dto.*;
import com.kse.wmsv2.oa_ew_002.service.OAEW002ScreenService;

import java.util.*;

import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Slf4j
@RestController
@RequestMapping("/api/oaew002")
public class OAEW002Controller {
    @Autowired
    OAEW002ScreenService screenServ;
    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/getInitData")
    public OAEW002InitParameterDto getInitData(@RequestHeader HttpHeaders headers) {
        OAEW002InitParameterDto initParameterDto = new OAEW002InitParameterDto();
        OAEW002GetParameterListDao getParameterListDao = new OAEW002GetParameterListDao();

        try {
            final String accessToken = (String) headers.get("authorization").get(0);
            // final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
            final String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

            // パラメータ取得(通関場所)
            getParameterListDao.setDepartmentCd(departmentCd);
            getParameterListDao.setNameClass("0028");
            List<OAEW002GetParameterListDto> customsPlaceList = screenServ.getParameterList(getParameterListDao);
            initParameterDto.setCustomsPlaceList(customsPlaceList);

            // パラメータ取得(蔵置場所)
            getParameterListDao.setDepartmentCd(departmentCd);
            getParameterListDao.setNameClass("0029");
            List<OAEW002GetParameterListDto> bondedWarehouseCdList = screenServ.getParameterList(getParameterListDao);
            initParameterDto.setBondedWarehouseCdList(bondedWarehouseCdList);

            // パラメータ取得(積込港)
            getParameterListDao.setDepartmentCd(departmentCd);
            getParameterListDao.setNameClass("0030");
            List<OAEW002GetParameterListDto> depPortList = screenServ.getParameterList(getParameterListDao);
            initParameterDto.setDepPortList(depPortList);

            // パラメータ取得(コンテナ区分)
            getParameterListDao.setDepartmentCd("ALL");
            getParameterListDao.setNameClass("0045");
            List<OAEW002GetParameterListDto> containerClassCdList = screenServ.getParameterList(getParameterListDao);
            initParameterDto.setContainerClassCdList(containerClassCdList);

            // パラメータ取得(出発便)
            getParameterListDao.setDepartmentCd(departmentCd);
            getParameterListDao.setNameClass("0160");
            List<OAEW002GetParameterListDto> departureTruckNoList = screenServ.getParameterList(getParameterListDao);
            initParameterDto.setDepartureTruckNoList(departureTruckNoList);
            initParameterDto.setResult(true);
        } catch(Exception e){
            initParameterDto.setResult(false);
            initParameterDto.setMessage("画面パラメータ取得に失敗しました。");
            initParameterDto.setErrorMessage(e.getMessage());
            log.error("画面パラメータ取得に失敗しました。" + e.getMessage());
            return initParameterDto;
        } finally {
            return initParameterDto;
        }
    }

    @PostMapping("/doSearch")
    public OAEW002SearchResultDto doSearch(@RequestHeader HttpHeaders headers,
                                                @RequestBody OAEW002SelectContainer1Dao container1Dao) {
        OAEW002SearchResultDto searchResultDto = new OAEW002SearchResultDto();
        final String accessToken = (String) headers.get("authorization").get(0);
        // final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        final String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");

        try {
            // ULDNoが指定された場合、ULDNoのみで検索を行う。
            if (!container1Dao.getUldNo().equals("")) {
                container1Dao.setBondedWarehouseCd("");
                container1Dao.setCustomsPlace("");
                container1Dao.setContainerDate("");
                container1Dao.setDepPort("");
                container1Dao.setDepartureTruckNoStart("");
                container1Dao.setDepartureTruckNoEnd("");
                container1Dao.setHtDisplayFlg01(false);
                container1Dao.setHtDisplayFlg02(false);
            }
            container1Dao.setUserDepartmentCd(departmentCd);

            List<OAEW002SelectContainer1Dto> selectContainer1DtoList = screenServ.selectContainer1(container1Dao);
            searchResultDto.setResultRowData(selectContainer1DtoList);
            searchResultDto.setResult(true);
            searchResultDto.setMessage("コンテナ検索処理が正常終了しました。");
        } catch(Exception e) {
            String errMsg = "コンテナ検索処理が異常しました。";
            searchResultDto.setResult(false);
            searchResultDto.setMessage(errMsg);
            searchResultDto.setErrorMessage(e.getMessage());
            log.error(errMsg + e.getMessage());
        } finally {
            return searchResultDto;
        }
    }

    @PostMapping("/doRegist")
    public OAEW002RegistResultDto doRegist(@RequestHeader HttpHeaders headers,
                                           @RequestBody OAEW002InsertUpdateContainer1Dao container1Dao) {

        OAEW002RegistResultDto registResultDto = new OAEW002RegistResultDto();
        final String accessToken = (String) headers.get("authorization").get(0);
        final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        final String departmentCd = redisUtil.loadRedis(accessToken, "DEPARTMENTCD");
        final String now = getTimeStampNow();

        try {
            container1Dao.setUserCd(userCd);
            container1Dao.setUpdateDate(now);

            OAEW002SelectContainer2Dao selectContainer2Dao = new OAEW002SelectContainer2Dao();
            selectContainer2Dao.setContainerDate(container1Dao.getContainerDate());
            selectContainer2Dao.setAwbNo(container1Dao.getAwbNo());
            selectContainer2Dao.setUldNo(container1Dao.getUldNo());

            int containerCnt = screenServ.selectContainer2(selectContainer2Dao);
            if (containerCnt == 0) {
                int insertContainer1Cnt = screenServ.insertContainer1(container1Dao);
                log.info("insertContainer1Cnt: " + insertContainer1Cnt);

                // insertしたコンテナデータを取得する。
                OAEW002SelectContainer1Dao selectContainer1Dao = new OAEW002SelectContainer1Dao();
                selectContainer1Dao.setContainerDate(selectContainer2Dao.getContainerDate());
                selectContainer1Dao.setAwbNo(selectContainer2Dao.getAwbNo());
                selectContainer1Dao.setUldNo(selectContainer2Dao.getUldNo());
                selectContainer1Dao.setUserDepartmentCd(departmentCd);
                List<OAEW002SelectContainer1Dto> selectContainer1DtoList = screenServ.selectContainer1(selectContainer1Dao);
                // 戻り値にinsertしたコンテナデータを追加
                registResultDto.setNew(true);
                registResultDto.setNewRowData(selectContainer1DtoList.get(0));
            } else {
                int updateContainer1Cnt = screenServ.updateContainer1(container1Dao);
                log.info("updateContainer1Cnt: " + updateContainer1Cnt);
                registResultDto.setNew(false);
            }
            registResultDto.setMessage("コンテナ登録処理が正常終了しました。");
            registResultDto.setResult(true);
        } catch(Exception e) {
            String errMsg = "コンテナ登録処理でエラーが発生しました。";
            registResultDto.setResult(false);
            registResultDto.setMessage(errMsg);
            registResultDto.setErrorMessage(e.getMessage());
            log.error(errMsg + e.getMessage());
        } finally {
            return registResultDto;
        }
    }

    @PostMapping("/doDelete")
    public OAEWDeleteResultDto doDelete(@RequestHeader HttpHeaders headers,
                                        @RequestBody OAEW002DeleteContainer1Dao container1Dao) {
        OAEWDeleteResultDto deleteResultDto = new OAEWDeleteResultDto();

        try {
            int deleteContainer1Cnt = screenServ.deleteContainer1(container1Dao);
            log.info("deleteContainer1Cnt: " + deleteContainer1Cnt);

            if (deleteContainer1Cnt > 0) {
                deleteResultDto.setResult(true);
                deleteResultDto.setMessage("コンテナ削除処理が正常終了しました。");
            } else {
                deleteResultDto.setResult(false);
                deleteResultDto.setMessage("コンテナ削除処理が異常終了しました。");
                deleteResultDto.setErrorMessage("削除対象データが存在しません。");
            }
        } catch (Exception e) {
            String errMsg = "コンテナ登録処理でエラーが発生しました。";
            deleteResultDto.setResult(false);
            deleteResultDto.setMessage(errMsg);
            deleteResultDto.setErrorMessage(e.getMessage());
            log.error(errMsg + e.getMessage());
        } finally {
            return deleteResultDto;
        }
    }

    @PostMapping("/doUpdate")
    public OAEW002UpdateResultDto doUpdate(@RequestBody List<OAEW002UpdateContainer2Dao> container1DaoList) {
        OAEW002UpdateResultDto updateResultDto = new OAEW002UpdateResultDto();
        log.info("container1DaoList:"+container1DaoList.toString());
        int updateContainer2Cnt = 0;
        try {
            updateContainer2Cnt = screenServ.doUpdateBusiness(container1DaoList);
            if (container1DaoList.size() == updateContainer2Cnt) {
                updateResultDto.setResult(true);
                updateResultDto.setMessage("コンテナ登録(HT非表示・更新)が正常終了しました。");
            } else {
                updateResultDto.setResult(false);
                updateResultDto.setMessage("コンテナ登録(HT非表示・更新)が異常終了しました。");
                updateResultDto.setErrorMessage("更新対象データが存在しません。");
            }
        } catch (Exception e) {
            String errMsg = "コンテナ登録処理でエラーが発生しました。";
            updateResultDto.setResult(false);
            updateResultDto.setMessage(errMsg);
            updateResultDto.setErrorMessage(e.getMessage());
            log.error(errMsg + e.getMessage());
        } finally {
            return updateResultDto;
        }
    }
}
