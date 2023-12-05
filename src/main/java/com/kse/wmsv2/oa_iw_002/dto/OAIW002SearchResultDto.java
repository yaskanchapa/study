package com.kse.wmsv2.oa_iw_002.dto;
import lombok.Data;
@Data
public class OAIW002SearchResultDto {

    int _rowIndex;
    String rowType; // top:1行目、permit:許可(2行目)、bondedOut：搬出(3行目)、carryOut:搬出済み(4行目)
    boolean select;
    String arrFtl; // 日付(出発日)
    String origin; // 出発空港(airport of origin)
    String awbNo; // 航空運送状番号
    String awbCnt; // HAWB件数
    String emergency; // 緊急
    String claim; // 請求
    String dutyFree; // 無税
    String manifest; // マニフェスト
    boolean lineEnable; // 編集可フラグ
    String _awbNo; // (データ保持用) 航空運送状番号
    String _origin; // (データ保持用) 出発空港(airport of origin)
    String _arrFtl1; // (データ保持用) 便名
    String _arrFtl2; // (データ保持用) 便・日付(出発日)
    String _id; // ag-GridのnodeID、画面側の操作時に使う
    String _columnId; // イベントが発生したag-GridのカラムID
}
