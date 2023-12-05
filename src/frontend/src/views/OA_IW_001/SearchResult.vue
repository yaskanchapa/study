<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 400px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue
            style="width: 100%; height: 100%;"
            :columnDefs="columnDefs"
            :rowData="resultRowData"
            @grid-ready="onGridReady"
            :defaultColDef="defaultColDef"
            :suppressRowClickSelection="true"
            :rowSelection="rowSelection"
            :stopEditingWhenCellsLoseFocus="true"
            @cell-value-changed="onCellValueChanged"
            @selection-changed="onSelectionChanged"
            :context="gridContext">
        </ag-grid-vue>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs2">
        <va-button color="info" size="medium" :disabled="isLoading" @click="onBatchClear"> バッチクリア </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium" :disabled="isLoading" @click="onClickRegist"> 登録 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" :disabled="isLoading" @click="onCsvDownload"> CSVダウンロード </va-button>
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
import { setAllRowData, getSelectdRowsData, getSelectedNodes, setSingleCellData } from '../../components/commonGrid/CmnGridApi.vue'
import GridSwitch from './OAIW001GridSwitch.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import _ from 'lodash' // eslint-disable-line

export default {
  props: {
    isLoading: Boolean,
    resultRowData: Array,
    doRegist: Function,
    onBatch: Function,
    offBatch: Function,
    batchClear: Function
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridSwitch: GridSwitch, // eslint-disable-line
  },
  data() {
    return {
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        resizable: true,
        cellStyle: {'border-right': '1px dotted'}, // eslint-disable-line
        suppressMenu: true
      },
      rowSelection: null,
      gridContext: {
        'onBatch': this.onBatch,
        'offBatch': this.offBatch
      }
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: 'select',
        field: 'select',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        cellClass: 'cellCenter'
      },
      {
        headerName:'バッチ',
        field: 'carryInActionFlg',
        cellRenderer: 'gridSwitch',
        minWidth: 150,
        maxWidth: 150,
        resizable: false
      },
      { // 便名/日付(出発日)
        headerName:'FTL',
        field: 'arrFtl',
        minWidth: 150,
        maxWidth: 150,
        resizable: false,
      },
      { // 出発空港(airport of origin)
        headerName:'ORG',
        field: 'origin',
        minWidth: 150,
        maxWidth: 150,
        resizable: false,
      },
      { // 航空運送状番号
        headerName:'MAWB No.',
        field: 'awbNo',
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
      },
      { // M個数(貨物個数)
        headerName:'M個数',
        field: 'awbPiece',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // M重量(貨物重量)
        headerName:'M重量',
        field: 'awbWeight',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // H件数
        headerName:'H件数',
        field: 'cntCWBCount',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // H個数
        headerName:'H個数',
        field: 'cntCWBPiece',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 件数OK
        headerName:'件数OK',
        field: 'cntOK',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 件数SH
        headerName:'件数SH',
        field: 'cntSH',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 件数OV
        headerName:'件数OV',
        field: 'cntOV',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 個数OK
        headerName:'個数OK',
        field: 'cntOKPiece',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 個数SH
        headerName:'個数SH',
        field: 'cntSHPiece',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 個数OV
        headerName:'個数OV',
        field: 'cntOVPiece',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 一般
        headerName:'一般',
        field: 'cntGeneral',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // マニ
        headerName:'マニ',
        field: 'cntMani',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 許可
        headerName:'許可',
        field: 'cntPermission',
        minWidth: 110050,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 入力
        headerName:'入力',
        field: 'cntInput',
        minWidth: 100,
        maxWidth: 100,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      }
    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  methods: {
    onChgAllRowBtnClicked() {
      setAllRowData(this.gridApi, [])
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
    }, onClickRegist() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.doRegist(selectedRows)
    }, setCarryInActionFlg(_id, _data) {
      setSingleCellData(this.gridApi, _id, 'carryInActionFlg', _data)
    }, onBatchClear() {
      const params = []
      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes', selectedNodes)
      _.forEach(selectedNodes, function(node) {
        params.push({
          id: node.id,
          data: node.data
        })
      })
      this.batchClear(params)
    }, onCsvDownload() {
      this.gridApi.exportDataAsCsv()
    }
  },
  provide: {
    provideGridSwitchAgreeLabel: '対象',
    provideGridSwitchDisAgreeLabel: '対象外'
  }
}
</script>

<style>
</style>
