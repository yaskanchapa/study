<!-- eslint-disable vue/no-unused-components -->

<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 400px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue style="width: 100%; height: 100%;" :columnDefs="columnDefs" @grid-ready="onGridReady"
          :rowData="resultRowData" :defaultColDef="defaultColDef" :suppressSizeToFit="true"
          :suppressRowClickSelection="true" :suppressDragLeaveHidesColumns="true" :rowSelection="rowSelection"
          :stopEditingWhenCellsLoseFocus="false" :pagination="paginationFlag" :context="gridContext"
          :getRowStyle="getRowStyle"
          @cell-value-changed="onCellValueChanged" :paginationPageSize="paginationPageSize"
        >
        </ag-grid-vue>
        <div style="max-width: 500px;">
          <select style="width: 160px;" v-on:change="onPageSizeChanged()" id="page-size">
            <option value="30">30/page</option>
            <option value="50">50/page</option>
            <option value="500" selected="">500/page</option>
            <option value="ALL">ALL</option>
          </select>
        </div>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="openHawbScanModal" :disabled="!isDataAvailable"> HAWBスキャン </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="handleDeleteButtonClicked" :disabled="!isDataAvailable"> 削除 </va-button>
      </div>
        <div class="flex xs5"></div>
      <div class="item flex xs1">
        <va-button color="info" size="medium"> 印刷 </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="small" style="font-size: 1em;" @click="onCsvDownload"> CSV<br/>ダウンロード </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="onClickDelete"> 行削除 </va-button>
      </div>
      <div class="item flex xs1">
        <va-button  color="info" size="medium" @click="onClickEdit"> 編集済 </va-button>
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
// eslint-disable-next-line no-unused-vars
import { setAllRowData, getSelectdRowsData, getSelectedNodes } from '../../components/commonGrid/CmnGridApi.vue'
import StatusModal from './StatusModal.vue'
import DetailModal from './DetailModal.vue'
import AbleCheckBoxCell from './AbleCheckBox.vue'
import DisableCheckBoxCell from './DisableCheckBox.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { dateFormat } from '@/utils/utilities'


export default {
  props: {
    resultRowData: Array,
    editImportData: Function,
    deleteImportData: Function,
    onCellValueChanged: Function,
    getPdfUrl: Function,
    resultHeaderData: Object
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    // eslint-disable-next-line vue/no-unused-components
    childMessageRenderer: StatusModal,
    // eslint-disable-next-line vue/no-unused-components
    detailModal: DetailModal,
    // eslint-disable-next-line vue/no-unused-components
    ableCheckBoxCell: AbleCheckBoxCell,
    // eslint-disable-next-line vue/no-unused-components
    disableCheckBoxCell: DisableCheckBoxCell
  },
  computed: {
    isDataAvailable() {
      return this.resultRowData.length > 0
    },
  },
  data() {
    return {
      chkList: [],
      showModalNoOutsideDismiss: false,
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        resizable: true,
        cellStyle: { 'border-right': '1px dotted' },
        sortable: true,
        filter: 'agTextColumnFilter',
        menuTabs: ['filterMenuTab']
      },
      rowSelection: null,
      gridContext: {},
      selectedRowList: [],
      paginationFlag: true,
      paginationPageSize: 500
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: 'AWB',
        field: 'awbNo',
        hide: true
      },
      {
        headerName: 'CH',
        field: 'check',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      // {
      //   headerName: '書類チェック',
      //   field: 'documentCheck',
      //   minWidth: 130,
      //   maxWidth: 130,
      //   resizable: false,
      //   cellRenderer: 'ableCheckBoxCell',
      //   suppressMovable: true,
      // },
      {
        headerName: 'IDA',
        field: 'idaFlg',
        minWidth: 75,
        maxWidth: 75,
        resizable: false,
        editable: false,
        cellRenderer: 'ableCheckBoxCell',
        suppressMovable: true,

      },
      {
        headerName: '済',
        field: 'editFlg',
        minWidth: 70,
        maxWidth: 70,
        resizable: false,
        checkboxSelection: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: '書類STS',
        field: 'currentCustomsStatusCd',
        cellRenderer: 'childMessageRenderer',
        editable: false,
        minWidth: 100,
        maxWidth: 200,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '貨物STS',
        field: 'currentCargoStatusCd',
        cellRenderer: 'childMessageRenderer',
        colId: 'params',
        editable: false,
        minWidth: 100,
        maxWidth: 200,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: 'IMG',
        field: 'img',
        editable: false,
        minWidth: 75,
        maxWidth: 75,
        resizable: false,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
        onCellClicked: this.showPdfViewer,
        sortable: true,
      },
      {
        headerName: '印',
        field: 'chPrint',
        minWidth: 70,
        maxWidth: 70,
        resizable: false,
        checkboxSelection: false,
        cellRenderer: 'disableCheckBoxCell',
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: 'HAWB番号',
        field: 'cwbNo',
        cellRenderer: 'detailModal',
        colId: 'params',
        editable: false,
        minWidth: 250,
        maxWidth: 250,
        // cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: 'S',
        field: 'shortFreight',
        minWidth: 70,
        maxWidth: 70,
        cellRenderer: 'disableCheckBoxCell',
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '搬入個数',
        field: 'cargoInScanPiece',
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "right" },
        suppressMovable: true,
      },
      {
        headerName: '個数',
        field: 'cargoPiece',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "right" },
        suppressMovable: true,
      },
      {
        headerName: '重量',
        field: 'cargoWeight',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "right" },
        suppressMovable: true,
      },
      {
        headerName: 'ORIGIN',
        field: 'origin',
        minWidth: 100,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '品名',
        field: 'item',
        minWidth: 150,
        maxWidth: 250,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
        editable: true
      },
      {
        headerName: '建値',
        field: 'invoiceValConCd',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '通貨',
        field: 'invoiceCurCd',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '金額',
        field: 'invoiceValue',
        minWidth: 60,
        maxWidth: 200,
        cellStyle: { textAlign: "right" },
        suppressMovable: true,
      },
      {
        headerName: '運賃通貨',
        field: 'fareCurrencyCd',
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '運賃',
        field: 'fare',
        minWidth: 60,
        maxWidth: 200,
        cellStyle: { textAlign: "right" },
        suppressMovable: true,
      },
      {
        headerName: '輸入者',
        field: 'importCustomerNo',
        minWidth: 100,
        maxWidth: 300,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '輸入者名',
        field: 'impCustomerName',
        minWidth: 100,
        maxWidth: 350,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
        editable: true
      },
      {
        headerName: '輸入者住所(一括)',
        field: 'impCustomerAddLump',
        minWidth: 100,
        maxWidth: 400,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
        editable: true
      },
      {
        headerName: '仕出人',
        field: 'exportCustomerNo',
        minWidth: 100,
        maxWidth: 150,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '仕出人名',
        field: 'expCustomerName',
        minWidth: 100,
        maxWidth: 350,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
      },
      {
        headerName: '仕出人住所(一括) ',
        field: 'expCustomerAddLump',
        minWidth: 100,
        maxWidth: 400,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
      },
      {
        headerName: '仕向地',
        field: 'destination',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '仕立',
        field: 'mixedForwarder',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '計上',
        field: 'managedDataFlg',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '支払',
        field: 'payKind',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: 'SPC',
        field: 'specialCargoSign',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '邦貨運賃',
        field: 'jpyFare',
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '搬入上屋',
        field: 'carBondedWareHouse',
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '職別等号',
        field: 'discernmentMark',
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "center" },
        suppressMovable: true,
      },
      {
        headerName: '編集者',
        field: 'editRegUserCd',
        minWidth: 60,
        maxWidth: 100,
        cellStyle: { textAlign: "left" },
        suppressMovable: true,
      },
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
    onClickEdit() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.editImportData(selectedRows)
    },
    onClickDelete() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.deleteImportData(selectedRows)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv(this.getCsvFileName())
    },
    onPageSizeChanged() {
      // eslint-disable-next-line no-var
      var value = document.getElementById('page-size').value;
      if (value === 'ALL') {
        value = this.resultRowData.length
      }
      this.gridApi.paginationSetPageSize(Number(value));
    },
    getCsvFileName() {
      const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
      return {
        fileName: '輸入データ編集_' + currentDate,
      }
    },
    numberWithCommas(value) {
      if (value === null || value === undefined) {
        return null;
      }
      return value;
    },
    showPdfViewer(params) {
      this.getPdfUrl(params)
    },
    getRowStyle: params => {
      if (params.data.editFlg === '0') {
        return { background: '#cce0ff' };
      } else if (params.data.editFlg === '1') {
        return { background: '#ffcccc' };
      }
      return null;
    },
    openHawbScanModal(params) {
      this.$emit('openHawbScanModal', params)
    },
    handleDeleteButtonClicked() {
      this.$emit('handleDeleteButtonClicked')
    },
  }
}
</script>