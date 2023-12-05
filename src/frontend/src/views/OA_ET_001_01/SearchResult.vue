<!-- eslint-disable vue/no-unused-components -->
<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 400px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue style="width: 100%; height: 100%;" :columnDefs="columnDefs" @grid-ready="onGridReady"
          :rowData="resultRowData" :defaultColDef="defaultColDef" :suppressSizeToFit="true"
          :suppressRowClickSelection="true" :suppressDragLeaveHidesColumns="true" :rowSelection="rowSelection"
          :stopEditingWhenCellsLoseFocus="false" :pagination="true" :context="gridContext" :getRowStyle="getRowStyle"
          :paginationPageSize="paginationPageSize">
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
        <va-button color="info" size="medium"> 印刷 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="onCsvDownload"> CSVダウンロード </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="onClickDelete"> 行削除 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="onClickEdit"> 編集済 </va-button>
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
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import StatusModal from './StatusModal.vue'
import DetailModal from './DetailModal.vue'
import AbleCheckBoxCell from './AbleCheckBox.vue'
import DisableCheckBoxCell from './DisableCheckBox.vue'
import { dateFormat } from '@/utils/utilities'

export default {
  props: {
    resultRowData: Array,
    editExportData: Function,
    deleteExportData: Function
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
  data() {
    return {
      chkList: [],
      showModalNoOutsideDismiss: false,
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        cellStyle: { 'border-right': '1px dotted' },
        sortable: true,
        filter: 'agTextColumnFilter',
        menuTabs: ['filterMenuTab']
      },
      rowSelection: null,
      gridContext: {

      },
      selectedRowList: [],
      stsLabel: 'ステータス照会',
      paginationPageSize: 500,
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
      {
        headerName: '未/済',
        field: 'miSumi',
        minWidth: 100,
        maxWidth: 100,
        resizable: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: 'ハード',
        field: 'hard',
        minWidth: 100,
        maxWidth: 100,
        resizable: false,
        editable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },

      },
      {
        headerName: 'マニ不可',
        field: 'maniHuka',
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: 'コメント',
        field: 'comment',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: '申告不可',
        field: 'sinkokuHuka',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'EDA',
        field: 'eda',
        editable: false,
        minWidth: 80,
        maxWidth: 80,
        resizable: false,
        cellRenderer: 'ableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: '差異',
        field: 'sai',
        editable: false,
        minWidth: 80,
        maxWidth: 80,
        resizable: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: 'E判断',
        field: 'eHandan',
        editable: false,
        minWidth: 100,
        maxWidth: 100,
        resizable: false,
        cellRenderer: 'disableCheckBoxCell',
        suppressMovable: true,
      },
      {
        headerName: 'STS',
        cellRenderer: 'childMessageRenderer',
        editable: false,
        minWidth: 200,
        maxWidth: 200,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: 'HAWB番号',
        field: 'cwbNo',
        cellRenderer: 'detailModal',
        colId: 'params',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '輸出者',
        field: 'expCustomer',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者名',
        field: 'expCustomerName',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者住所１',
        field: 'expCustomerAdd1',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者住所２',
        field: 'expCustomerAdd2',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者住所３',
        field: 'expCustomerAdd3',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者住所４',
        field: 'expCustomerAdd4',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '輸出者〒',
        field: 'expCustomerPostcode',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '輸出者電話番号',
        field: 'expCustomerTelNo',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '個数',
        field: 'carryingPiece',
        editable: false,
        minWidth: 55,
        maxWidth: 80,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: '重量',
        field: 'carryingWeight',
        editable: false,
        minWidth: 55,
        maxWidth: 80,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'LINK個数',
        field: 'linkPiece',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'LINK重量',
        field: 'linkWeight',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: '仕向人',
        field: 'consignee',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人名',
        field: 'consigneeName',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人住所１',
        field: 'consigneeAdd1',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人住所２',
        field: 'consigneeAdd2',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人住所３',
        field: 'consigneeAdd3',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人住所４',
        field: 'consigneeAdd4',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '仕向人国',
        field: 'consigneeCountry',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '仕向人〒',
        field: 'consigneePostcode',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '有償・無償',
        field: 'invoiceValClassCd',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '建値',
        field: 'invoiceValConCd',
        editable: false,
        minWidth: 55,
        maxWidth: 100,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '通貨',
        field: 'invoiceCurCd',
        editable: false,
        minWidth: 55,
        maxWidth: 100,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '仕入書価格',
        field: 'invoiceValue',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'FOB通貨',
        field: 'fobCurrencyCd',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: 'FOB価格',
        field: 'fobAmo',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'LINK FOB価格',
        field: 'linkFobAmo',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: '品名',
        field: 'item',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '申告価格',
        field: 'reportValue',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        cellStyle: { textAlign: "right" },
        resizable: false,
        suppressMovable: true,
      },
      {
        headerName: 'LINK仕向地',
        field: 'linkLastDestinationCd',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '最終仕向地',
        field: 'lastDestinationCd',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '記事',
        field: 'news2',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: true,
        suppressMovable: true,
      },
      {
        headerName: '申告条件',
        field: 'dataReportCondition',
        editable: false,
        minWidth: 120,
        maxWidth: 120,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '申告先種別',
        field: 'customsKindCd1',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: 'あて先官署',
        field: 'reportDepCd',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: 'あて先部門',
        field: 'reportDivisionCd',
        editable: false,
        minWidth: 55,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '申告予定年月日',
        field: 'expReportPlanDate',
        editable: false,
        minWidth: 75,
        maxWidth: 200,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '社内整理番号',
        field: 'inHouseRefNumber',
        editable: false,
        minWidth: 75,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: 'BWB番号',
        field: 'bwbNo',
        editable: false,
        minWidth: 75,
        maxWidth: 150,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
      },
      {
        headerName: '編集者',
        field: 'userName',
        editable: false,
        minWidth: 75,
        maxWidth: 100,
        resizable: false,
        suppressMovable: true,
        cellStyle: { textAlign: "center" },
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
    onClickDelete() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.deleteExportData(selectedRows)
    },
    onClickEdit() {
      const selectedRows = getSelectdRowsData(this.gridApi)
      this.editExportData(selectedRows)
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
        fileName: '輸出データ編集_' + currentDate,
      }
    },
    getRowStyle: params => {
      if (params.data.miSumi === '0') {
        return { background: '#cce0ff' };
      } else if (params.data.miSumi === '1') {
        return { background: '#ffcccc' };
      }
      return null;
    }
  },
  provide: {
    provideStatusButtonLabel: 'ステータス照会'
  }
}
</script>

<style>
.ag-header-cell-label {
  justify-content: center;
}
</style>