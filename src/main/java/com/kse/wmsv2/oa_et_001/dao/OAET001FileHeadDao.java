package com.kse.wmsv2.oa_et_001.dao;

import lombok.Data;

@Data
public class OAET001FileHeadDao {

    //　固定値
    String stepsDate;
    String trackService;


    // ファイルから取得
    String awbNo;
    String bonWareHoCurDate;
    String loadingPlanFlt1;
    String loadingPlanFlt2;
    String airline;
    String fltDestination;

    // デフォルト値から取得
    String carSlipMakeFlg;
    String agent;
    String reportCondition;
    String depPortCd;
    String bondedWarehouseCd;
    String joint;
    String loadingWorker;

    // NULLセット項目
    String carryingSlipNo;
    String carryingConPerson;
    String reportDepCd;

    String userCd;
    String date;
}
