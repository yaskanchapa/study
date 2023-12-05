
<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 500px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue style="width: 100%; height: 100%;" :columnDefs="columnDefs" :rowData="resultRowData"
          @grid-ready="onGridReady" 
          :defaultColDef="defaultColDef" 
          :suppressRowClickSelection="true"
          :rowSelection="rowSelection" 
          :stopEditingWhenCellsLoseFocus="true"
          @selection-changed="onSelectionChanged"
          >
        </ag-grid-vue>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs2">
        <va-button color="info" :disabled="createBilFlag || isLoading" size="medium" @click="onRecreateBilClick"> BIL再作成 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" :disabled="createBilFlag || isLoading" size="medium" @click="onBilClick"> BIL新規作成 </va-button>
      </div>
      <div class="item flex xs1">
        <bondInBillPrintButton
        v-bind:isLoading="this.isLoading"
        v-bind:changeLoading="this.changeLoading"
        v-bind:printData="printData"
        />
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
import { setAllRowData, getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { dateFormat } from '@/utils/utilities'

import bondInBillPrintButton from '../OC_CS_006/OCCS006printBondInBillButton.vue'


export default {
  name: 'App',
  props: {
    resultRowData: Array,
    bilResult: Function,
    recreateBilResult: Function,
    createBilFlag: Boolean,
    changeLoading: Function,
    isLoading: Boolean,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    bondInBillPrintButton,
  },
  data() {
    return {
      value: false,
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        cellStyle: { 'border-right': '1px dotted' },
        sortable: true,
        filter: 'agTextColumnFilter',
        menuTabs: ['filterMenuTab']
      },
      gridContext: {

      },
      rowSelection: null,
      printData: []
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: 'select',
        field: 'cCheck',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        suppressMovable: true,
        cellClass: 'cellCenter'
      },

      {
        headerName: 'B',
        field: 'bilMake',
        minWidth: 75,
        maxWidth: 75,
        suppressMovable: true,
        resizable: false
      },
      {
        headerName: '伝',
        field: 'bondInBill',
        minWidth: 75,
        maxWidth: 75,
        suppressMovable: true,
        resizable: false
      },
      {
        headerName: '搬入伝票番号',
        field: 'bondInBillNo',
        suppressMovable: true,
      },
      {
        headerName: 'MAWBNo',
        field: 'awbNo',
        suppressMovable: true,
      },
      {
        headerName: '対査日',
        field: 'arrivalDate',
        suppressMovable: true,
      },
      {
        headerName: '搬伝発行日',
        field: 'bondInBillIssueDate',
        suppressMovable: true,
      },
      {
        headerName: 'HAWB件数',
        field: 'cwbCount',
        suppressMovable: true,
      },
      {
        headerName: '個数',
        field: 'carryingPiece',
        suppressMovable: true,
      },
      {
        headerName: '重量',
        field: 'carryingWeight',
        suppressMovable: true,
      }

    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  methods: {
    onChgAllRowBtnClicked() {
      const allData = [
      ]
      setAllRowData(this.gridApi, allData)
    },
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },
    onBilClick() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.bilResult(selectedRows)
    },
    onRecreateBilClick() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.recreateBilResult(selectedRows)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv(this.getCsvFileName())
    },
    getCsvFileName() {
      // eslint-disable-next-line no-undef
      const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
      return {
        fileName: '搬入伝票照会_' + currentDate,
      }
    },
    onSelectionChanged(params) {
      console.log(params)
      const selectedRows = getSelectdRowsData(this.gridApi)
      console.log('selectedRows: ', selectedRows)
      this.printData = selectedRows
    }, 
  },
}

</script>

