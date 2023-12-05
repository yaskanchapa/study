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
            @selection-changed="onSelectionChanged">
        </ag-grid-vue>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs2">
        <va-button color="info" size="medium" :disabled="isLoading" @click="onPrint"> 印刷 </va-button>
        <!-- <bondInBillPrintButton
        v-bind:isLoading="this.isLoading"
        v-bind:changeLoading="this.changeLoading"
        v-bind:printData="printData"
        /> -->
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
import GridButton from './OAIW003GridButton.vue'
import GridButton2 from './OAIW003GridButton2.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import _ from 'lodash' // eslint-disable-line

// import bondInBillPrintButton from '../OC_CS_006/OCCS006printCarryingSlipButton.vue'


export default {
  props: {
    changeLoading: Function,
    isLoading: Boolean,
    resultRowData: Array,
  },

  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridButton: GridButton, // eslint-disable-line
    gridButton2: GridButton2, // eslint-disable-line
    // bondInBillPrintButton
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
      printData: [],
    }
  },
  beforeMount() {
    this.columnDefs = [
      { // 選択
        headerName: 'select',
        field: 'c',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        cellClass: 'cellCenter'
      },
      { // ステータス
        headerName:'ステータス',
        field: 'btn',
        minWidth: 130,
        maxWidth: 130,
        cellRenderer: 'gridButton',
        cellClass: 'cellCenter'
      },
      { // 開始時間 yyyy/MM/dd HH:mm:ss
        headerName:'開始時間',
        field: 'workStartTime',
        minWidth: 160,
        maxWidth: 160,
        resizable: false
      },
      { // 終了時間 yyyy/MM/dd HH:mm:ss
        headerName:'終了時間',
        field: 'workEndTime',
        minWidth: 160,
        maxWidth: 160,
        resizable: false
      },
      { // 対象
        headerName:'対象',
        field: 'obj',
        minWidth: 120,
        maxWidth: 120,
        resizable: false
      },
      { // 便名/日付
        headerName:'FLT',
        field: 'flt',
        minWidth: 140,
        maxWidth: 140,
        resizable: false,
      },
      { // 航空運送状番号
        headerName:'MAWB No.',
        field: 'awbNo',
        minWidth: 250,
        maxWidth: 250,
        resizable: false,
      },
      { // H件数
        headerName:'H件数',
        field: 'cwbCnt',
        minWidth: 110,
        maxWidth: 110,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // インベン済
        headerName:'インベン済',
        field: 'scanPiece',
        minWidth: 110,
        maxWidth: 110,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // オーバー
        headerName:'オーバー',
        field: 'overCnt',
        minWidth: 110,
        maxWidth: 110,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // ショート
        headerName:'ショート',
        field: 'shortCnt',
        minWidth: 110,
        maxWidth: 110,
        cellClass: ['ag-right-aligned-cell'],
        resizable: false,
      },
      { // 終了
        headerName:'終了',
        field: 'endFlg',
        minWidth: 110,
        maxWidth: 110,
        cellRenderer: 'gridButton2',
        resizable: false,
        cellClass: 'cellCenter'
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

      this.printData = selectedRows
    }, 
    setCarryInActionFlg(_id, _data) {
      setSingleCellData(this.gridApi, _id, 'carryInActionFlg', _data)
    },
    onPrint() {
      console.log('Data: ', this.printData)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv()
    }
  },
  provide: {
    provideGridButtonLabel: 'ステータス',
  }
}
</script>

<style>
</style>
