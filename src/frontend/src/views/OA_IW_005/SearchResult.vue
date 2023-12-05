<!-- eslint-disable vue/no-unused-components -->
<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 500px;" id="print">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue
            style="width: 100%; height: 100%;"
            :columnDefs="columnDefs"
            :rowData="resultRowData"
            @grid-ready="onGridReady"
            :defaultColDef="defaultColDef"
            :suppressRowClickSelection="true"
            :rowSelection="rowSelection"
            :pagination="true"
            :stopEditingWhenCellsLoseFocus="true"
      ></ag-grid-vue>
    </div>
    </div>
    <select style="width: 160px;" v-on:change="onPageSizeChanged()" id="page-size">
        <option value="30">30/page</option>
        <option value="50">50/page</option>
        <option value="100" selected="">100/page</option>
      </select>
   
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium"> 印刷 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium"> CSVダウンロード </va-button>
      </div>
   </div>
  </va-card>
</template>


<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import { setAllRowData, getSelectdRowsData, getSelectedNodes } from '../../components/commonGrid/CmnGridApi.vue'
import GridButton from './OAIW005GridButton.vue'
import GridSwitch from './OAIW005GridSwitch.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { dateFormat } from '../../utils/utilities'
import StatusModal from "./StatusModal.vue"

export default {
 name: 'App',
 props: {
  resultRowData: Array,
 },
 components: {
  AgGridVue,
  numericGridEditor: NumericGridEditor, // eslint-disable-line
  gridSwitch: GridSwitch, // eslint-disable-line
  gridButton: GridButton, // eslint-disable-line
  // eslint-disable-next-line vue/no-unused-components
  childMessageRenderer: StatusModal, 

 },
 data() {
  return {
    rowSelection: null,
    columnDefs: null,
    rowData: null,
    gridApi: null,
    columnApi: null,
    defaultColDef: {
      resizable: true,
      cellStyle: {'border-right': '1px dotted'} // eslint-disable-line
    },
    value1:false,
    value2:false,
  }
 },
 beforeMount() {
  this.columnDefs = [
    { // checkbox
        headerName: 'select',
        field: 'select',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        suppressMenu: true,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        cellClass: 'cellCenter'
      },
    { // MAWBNo
      headerName:'MAWBNo',
      field: 'mawbNo' 
    },
    { // FLT
     headerName:'FLT',
     field:'flt'
    },
    { // ORG
     headerName:'ORG',
     field:'org'
    },
    { // HAWBNo   // メモ：GTXTCWBNOとどちらか
     headerName:'HAWBNo', 
     field:'cwbNo'
    },
    { // HPK保留 // メモ：GBTNHPKRESERVEとどちらか
     headerName:'HPK保留',
     field:'hpkStatus'
    },
    { // 個数
     headerName:'個数',
     field:'cargoPiece'
    },
    { // HPK済個数
     headerName:'HPK済個数',
     field:'cargoInPiece'
    },
    { // スキャン済個数
     headerName:'スキャン済個数',
     field:'cargoInScanPiece',
     
    },
    { // 重量  // メモ：field表記を要確認
     headerName:'重量',
     field:'cargoWeight'
    },
    { // 借物STS
     headerName: '貨物STS',
        field: 'cargoStatus',
        cellRenderer: 'childMessageRenderer',
    },
    { // 到着
     headerName:'到着',
     field:'currentArrFlt1',
     suppressMovable: true,
    
    },
    { // 搬入
     headerName:'搬入',
     field:'inFlight',
     
     
    },
    { // HPK作成
     headerName:'HPK作成',
     field:'hpkSts'
     
    },
    { // インベントリー
     headerName:'インベントリー',
     field:'inventory'
     
    },
    { // 許可
     headerName:'許可',
     field:'permit'
     
    },
    { // 搬出
     headerName:'搬出',
     field:'carryout'
     
    },
    { // OUT
     headerName:'OUT',
     field:'aiOut',
    },
    { // ロケ
     headerName:'ロケ',
     field:'locationCd'
     
    },
    { // 申告区分
     headerName:'申告区分',
     field:'aiReportClass'
     
    },
    { // 許可区分
     headerName:'許可区分',
     field:'aiPermitClass'
    
    },
    { // 仕分条件
     headerName:'仕分条件',
     field:'classifyClass'
     
    }

  ]
  this.rowData = []
 },
created() {
  this.rowSelection = 'multiple'
 },
 methods: {
    openStatusModal() {
        console.log("test");
      },
  onPageSizeChanged() {
      // eslint-disable-next-line no-var
      var value = document.getElementById('page-size').value;
      this.gridApi.paginationSetPageSize(Number(value));
    },
  onChgAllRowBtnClicked() {
      const allData = [
      ]
      setAllRowData(this.gridApi, allData)
    },
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },
    onCellValueChanged(params) {
      const columnId = params.colDef.field
      const rowData = params.data
      const newValue = params.newValue
      const oldValue = params.oldValue
      console.log('onCellValueChanged columnId:', columnId)
      console.log('onCellValueChanged newValue:', newValue)
      console.log('onCellValueChanged oldValue:', oldValue)
      console.log('onCellValueChanged rowData:', rowData)
    },
    onSelectionChanged(params) {
      console.log(params)
      const selectedRows = getSelectdRowsData(this.gridApi)
      console.log('selectedRows: ', selectedRows)

      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes: ', selectedNodes)
    },

    getParams() {
      const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
            return {
              fileName: '上屋データ照会_' + currentDate,
            }
          },
      onCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },
 },
 provide: {
    providedValue: 'testValue', // provide this value to grid components
    provideGridSwitchAgreeLabel: '対象',
    provideGridSwitchDisAgreeLabel: '対象外',
    provideGridButtonLabel: 'ConsoleOutRowData'
  },
}
</script>

<style>
</style>