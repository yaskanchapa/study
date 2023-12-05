package com.kse.wmsv2.oa_iw_002.controller;
import com.kse.wmsv2.common.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import static com.kse.wmsv2.oa_iw_002.common.OAIW001CommonConstants.*;
import com.kse.wmsv2.oa_iw_002.dao.*;
import com.kse.wmsv2.oa_iw_002.dto.*;
import com.kse.wmsv2.oa_iw_002.service.OAIW002ScreenService;
import java.util.*;
import static com.kse.wmsv2.common.util.DateUtil.getTimeStampNow;

@Slf4j
@RestController
@RequestMapping("/api/oaiw002")
public class OAIW002Controller {
    @Autowired
    OAIW002ScreenService screenServ;
    @Autowired
    private RedisUtil redisUtil;

    @PostMapping("/doSearch")
    public OAIW002DoSearchResultDto selectBondedOutList(@RequestBody OAIW002SelectBondedOutListDao param) {
        OAIW002DoSearchResultDto result = new OAIW002DoSearchResultDto();

        try {
            List<OAIW002SearchResultDto> searchResultDtoList = screenServ.searchBondedOutData(param);

            if (searchResultDtoList == null) {
                result.setResult(true);
                result.setMessage("検索条件のデータが0件です。");
                result.setResultData(new ArrayList<OAIW002SearchResultDto>());
            } else {
                result.setResult(true);
                result.setMessage("検索処理を完了しました。");
                result.setResultData(searchResultDtoList);
            }
            return result;
        } catch (Exception e) {
            String errMsg = "検索処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            result.setResult(false);
            result.setMessage(errMsg);
            result.setErrorMessage("");
            return result;
        }
    }

    @PostMapping("/doPermit")
    public OAIW002DoPermitResultDto doPermit(@RequestHeader HttpHeaders headers,
                                             @RequestBody OAIW002DoPermitDto doPermitDto) {

        try {
            return screenServ.doPermitBusiness(headers, doPermitDto);
        } catch (Exception e) {
            // 戻り値設定
            OAIW002DoPermitResultDto resultDto = new OAIW002DoPermitResultDto();
            String errMsg = "許可処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
            return resultDto;
        }
    }

    @PostMapping("/doBondedOut")
    public OAIW002DoBondedOutResultDto doBondedOut(@RequestHeader HttpHeaders headers,
                                             @RequestBody OAIW002DoBondedOutDto doBondedOutDto) {
        OAIW002DoBondedOutResultDto resultDto = new OAIW002DoBondedOutResultDto();

        try {
            return screenServ.doBondedOutBusiness(headers, doBondedOutDto);
        } catch (Exception e) {
            String errMsg = "搬出処理でエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
            return resultDto;
        }
    }

    @PostMapping("/doOut")
    public OAIW002DoOutResultDto doOut(@RequestHeader HttpHeaders headers, @RequestBody OAIW002DoOutDto doOutDto) {
        OAIW002DoOutResultDto resultDto = new OAIW002DoOutResultDto();

        final String accessToken = (String)headers.get("authorization").get(0);
        final String userCd = redisUtil.loadRedis(accessToken, "USERCD");
        final String now = getTimeStampNow();
        OAIW002SelectBondedOutListDao param = doOutDto.getParam();
        List<OAIW002SearchResultDto> gridDataList = doOutDto.getGridDataList();
        OAIW002SelectBondedOutListDao reSearchParam = doOutDto.getReSearchParam();

        try {
            log.info("doOutBusiness START");
            // 業務処理(本体)
            OAIW002DoOutBusinessDto doOutBusinessDto = screenServ.doOutBusiness(userCd, now, param, gridDataList, reSearchParam);
            log.info("doOutBusiness END");

            resultDto.setResult(true);
            if(doOutBusinessDto.isReSearchResult()) {
                resultDto.setMessage("OUT処理が正常終了しました。");
                resultDto.setErrorMessage("");
            } else {
                resultDto.setReSearchErr(true); //　再検索時にエラーが発生
                resultDto.setMessage("OUT処理が正常終了しました。");
                resultDto.setErrorMessage("OUT処理が正常終了後、一覧検索処理でエラーが発生しました。対象表示ボタンにて再検索を実施してください。");
            }
            resultDto.setGridDataList(doOutBusinessDto.getResultDtoList());
            resultDto.setAwbNoCnt(Integer.toString(doOutBusinessDto.getAwbNoCnt()));
            resultDto.setOutFileCnt(Integer.toString(doOutBusinessDto.getOutFileCnt()));
        } catch (Exception e) {
            String errMsg = "OUT処理にエラーが発生しました。";
            log.error(errMsg + e.getMessage());
            resultDto.setResult(false);
            resultDto.setMessage(errMsg);
            resultDto.setErrorMessage("");
        } finally {
            return resultDto;
        }
    }
}
