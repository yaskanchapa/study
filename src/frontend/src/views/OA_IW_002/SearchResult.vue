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
            :stopEditingWhenCellsLoseFocus="true"
            @cell-value-changed="onCellValueChanged"
            @selection-changed="onSelectionChanged"
            @cell-clicked="onCellClicked"
            :context="gridContext">
        </ag-grid-vue>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
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
import { setAllRowData, getSelectdRowsData, getSelectedNodes, 
          setSingleRowData, setSingleCellData, getSingleRowData, getAllRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import GridCheckBox from '../../components/commonGrid/GridCheckBox.vue'
import _ from 'lodash' // eslint-disable-line

import { defineComponent } from 'vue'
import { useModal } from 'vuestic-ui'

const COLID_EMERGENCY = 'emergency' // 緊急
const COLID_CLAIM = 'claim' // 請求
const COLID_DUTYFREE = 'dutyFree' // 無税
const COLID_MANIFEST = 'manifest' // マニフェスト

const ROWTYPE_PERMIT = "permit" // 許可
const ROWTYPE_BONDEDOUT = "bondedOut" // 搬出

export default defineComponent({
  setup(props) {
    const { init } = useModal()

    const onOkCallback = (params, colId, rowType) => {
      console.log(params)
      if(rowType === ROWTYPE_PERMIT) {
        // 許可行のRowData取得
        const rowDataPermit = params.data
        rowDataPermit._id = params.node.id // ag-GridのnodeID
        rowDataPermit._columnId = colId // イベントが発生したカラムID

        // 搬出行のRowData取得
        const bondedOutRowId = String(parseInt(params.node.id) + 1)
        const rowDataBondedOut = getSingleRowData(params.api, bondedOutRowId)
        rowDataBondedOut._id = bondedOutRowId
        rowDataBondedOut._columnId = colId
        props.doPermit(rowDataPermit, rowDataBondedOut)
      } else if (rowType === ROWTYPE_BONDEDOUT) {

        // 搬出行のRowData取得
        const rowDataBondedOut = params.data
        rowDataBondedOut._id = params.node.id // ag-GridのnodeID
        rowDataBondedOut._columnId = colId // イベントが発生したカラムID
        
        // 許可行のRowData取得
        const permitRowId = String(parseInt(params.node.id) - 1)
        const rowDataPermit = getSingleRowData(params.api, permitRowId)
        rowDataPermit._id = permitRowId
        rowDataPermit._columnId = colId
        props.doBondedOut(rowDataPermit, rowDataBondedOut)
      }
    }

    return {
      onCellClicked: (params) => {
        console.log(params)
        const colId = params.column.colId
        const rowType = params.data.rowType
        const headerName = params.colDef.headerName
        let isEventRow = false
        let isEvenColumn = false
        let _message = null
        let _title = null
        // カラムチェック(緊急、請求、無税、マニフェスト)
        if([COLID_EMERGENCY, COLID_CLAIM, COLID_DUTYFREE, COLID_MANIFEST].indexOf(colId) !== -1) {
          isEvenColumn = true
        }
        if(rowType === ROWTYPE_PERMIT) {
          isEventRow = true
          _title = `${headerName}・許可`
          _message = `${headerName}の"許可"処理（HTデータ作成）を実施しますか？`
        } else if (rowType === ROWTYPE_BONDEDOUT) {
          isEventRow = true
          _title = `${headerName}・搬出中`
          _message = `${headerName}の"搬出中"処理（HTデータ削除)を実施しますか？`
        }
        console.log(isEventRow, isEvenColumn)
        if(isEventRow && isEvenColumn) {
          init({
            title: _title,
            message: _message,
            onOk: () => onOkCallback(params, colId, rowType)     
          })
        } else {
          // do nothing
        }
      },
    }
  },
  props: {
    isLoading: Boolean,
    resultRowData: Array,
    doPermit: Function,
    doBondedOut: Function,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridCheckBox: GridCheckBox // eslint-disable-line
  },
  data() {
    return {
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        cellStyle: {'border-right': '1px dotted'}, // eslint-disable-line
        suppressMenu: true
      },
      gridContext: {
        // 'onBatch': this.onBatch,
        // 'offBatch': this.offBatch
      }
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: '選',
        field: 'select',
        minWidth: 80, maxWidth: 80,
        resizable: false,
        cellRendererSelector: params => { 
          if (params.data.rowType === 'top') {
            return {
              component: "gridCheckBox"
            }
          } else {
            return undefined
          }
        },
        valueGetter : params => {
          if (params.data.rowType === 'top') {
            return params.data.select
          } else {
            return null
          }
        }
      },
      { // 便名/日付(出発日)
        headerName:'FTL',
        field: 'arrFtl',
        minWidth: 180, maxWidth: 180,
        resizable: false,
      },
      { // 出発空港(airport of origin)
        headerName:'ORG',
        field: 'origin',
        minWidth: 110, maxWidth: 110,
        resizable: false,
      },
      { // 航空運送状番号
        headerName:'MAWB No.',
        field: 'awbNo',
        minWidth: 180, maxWidth: 180,
        resizable: false,
      },
      { // HAWB件数
        headerName:'HAWB件数',
        field: 'awbCnt',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // 緊急
        headerName:'緊急',
        field: COLID_EMERGENCY,
        cellClass: gridCellClassSelector,
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // 請求
        headerName:'請求',
        field: COLID_CLAIM,
        cellClass: gridCellClassSelector,
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // 無税
        headerName:'無税',
        field: COLID_DUTYFREE,
        cellClass: gridCellClassSelector,
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // マニフェスト
        headerName:'マニ',
        field: COLID_MANIFEST,
        cellClass: gridCellClassSelector,
        minWidth: 130, maxWidth: 130,
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
      
    },
    onSelectionChanged(params) {
      console.log(params)
      const selectedRows = getSelectdRowsData(this.gridApi)
      console.log('selectedRows: ', selectedRows)

      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes: ', selectedNodes)
    }, setCarryInActionFlg(_id, _data) {
      setSingleCellData(this.gridApi, _id, 'carryInActionFlg', _data)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv()
    },
    setRowData(id, rowData) {
      setSingleRowData(this.gridApi, id, rowData)
    },
    getAllGridData() {
      return getAllRowsData(this.gridApi)
    }
  }
})

const gridCellClassSelector = params => { 
  if (params.data.rowType === ROWTYPE_PERMIT || params.data.rowType === ROWTYPE_BONDEDOUT) {
    if(params.data.lineEnable){
      return ['ag-right-aligned-cell', 'cell-event']
    }
  }
  return ['ag-right-aligned-cell']
}

</script>

<style>
.cell-event {
  font-weight: bold;
  background-color: #0011ff3d;
}
</style>
