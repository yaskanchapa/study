<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 400px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue
            style="width: 100%; height: 100%;"
            :columnDefs="columnDefs"
            :rowData="resultRowData"
            rowSelection='multiple'
            @grid-ready="onGridReady"
            :defaultColDef="defaultColDef"
            :suppressRowClickSelection="true"
            :stopEditingWhenCellsLoseFocus="true"
            @cell-double-clicked="onCellDoubleClicked"
            :context="gridContext"
            suppressContextMenu="false"
            :getRowId="getRowId">
        </ag-grid-vue>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium" :disabled="isLoading" @click="onRegist"> 登録 </va-button>
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
import { setAllRowData, getSelectdRowsData, 
          setSingleRowData, setSingleCellData,  getAllRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import GridCheckBox from '../../components/commonGrid/GridCheckBox.vue'
import GridHeaderCheckBox from './GridHeaderCheckBox.vue'
import { notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line

export default {  
  props: {
    isLoading: Boolean,
    isSearch: Boolean,
    changeSearch: Function,
    clearAll: Function,
    resultRowData: Array,
    updateContainer: Function,
    setRowDataToHeader: Function,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridCheckBox: GridCheckBox, // eslint-disable-line
    gridHeaderCheckBox: GridHeaderCheckBox // eslint-disable-line

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
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        checkboxSelection: true,
        cellClass: 'cellCenter'
      },
      { // 通関場所
        headerName:'通関場所',
        field: 'customsPlaceName',
        minWidth: 180, maxWidth: 180,
        resizable: false,
      },
      { // 蔵置場所
        headerName:'蔵置場所',
        field: 'bondedWarehouse',
        minWidth: 110, maxWidth: 110,
        resizable: false,
      },
      { // 積込年月日
        headerName:'積込年月日',
        field: 'containerDate',
        minWidth: 180, maxWidth: 180,
        resizable: false,
      },
      { // 積込港
        headerName:'積込港',
        field: 'depPortName',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // MAWB No.
        headerName:'MAWB No.',
        field: 'awbNo',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // ULD No.
        headerName:'ULD No.',
        field: 'uldNo',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // コンテナ区分
        headerName:'コンテナ区分',
        field: 'containerClassName',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // 出発便
        headerName:'出発便',
        field: 'departureTruckName',
        minWidth: 130, maxWidth: 130,
        resizable: false,
      },
      { // HT非表示
        headerName:'HT非表示',
        field: 'htDisplayFlg',
        minWidth: 150, maxWidth: 150,
        resizable: false,
        cellRenderer: 'gridCheckBox',
        headerComponent: 'gridHeaderCheckBox'        
      },
    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  methods: {
    getRowId(params) {
      return params.data.delKey;
    },
    onChgAllRowBtnClicked() {
      setAllRowData(this.gridApi, [])
    },
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },
   setCarryInActionFlg(_id, _data) {
      setSingleCellData(this.gridApi, _id, 'carryInActionFlg', _data)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv()
    },
    onRegist() {
      const selectedRowData = getSelectdRowsData(this.gridApi)
      if(selectedRowData.length === 0) {
        notificationWarn('登録(HT非表示・更新)するデータを選択してください。')
        return false
      }
      this.updateContainer(selectedRowData);
    },
    onCellDoubleClicked(param){
      this.setRowDataToHeader(param.data)
    },
    setRowData(id, rowData) {
      setSingleRowData(this.gridApi, id, rowData)
    },
    getAllGridData() {
      return getAllRowsData(this.gridApi)
    },
    addNewRow(newRowData) {
      this.gridApi.applyTransaction({add: [newRowData]})
    },
    chkDelCondition(deleteCondition) {
      const selectedRows = getSelectdRowsData(this.gridApi)
      if(selectedRows.length === 0) {
        notificationWarn('削除対象データを一覧から選択してください。')
        return false
      }       
      if(selectedRows.length !== 1) {
        notificationWarn('削除対象データは１つのみです。')
        return false
      }
      if( selectedRows[0].containerDate !== deleteCondition.containerDate
          || selectedRows[0].awbNo !== deleteCondition.awbNo
          || selectedRows[0].uldNo !== deleteCondition.uldNo) {
            notificationWarn('一覧から選択したデータと明細・ヘッダーで入力したデータが一致しません。')
        return false
      }
      return true
    },
    delRow(deleteCondition) {
      const key = `${deleteCondition.awbNo}${deleteCondition.uldNo}${deleteCondition.containerDate}`
      const deleteTransaction = { 
      remove: [
          {delKey: key}
      ] }
      this.gridApi.applyTransaction(deleteTransaction)
    }
  }
}
</script>

<style>
.cell-event {
  font-weight: bold;
  background-color: #0011ff3d;
}
</style>
