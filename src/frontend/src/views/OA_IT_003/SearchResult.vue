<script setup>
</script>
<template>
  <va-card class="p-1">
    <div class="row justify-start" style="height: 390px;">
      <div class="ag-theme-alpine grid-wrapper">
        <ag-grid-vue
          class="item flex xs12"
          :columnDefs="columnDefs"
          :rowData="resultRowData"
          @grid-ready="onGridReady"
          :defaultColDef="defaultColDef"
          :suppressRowClickSelection="true"
          :rowSelection="rowSelection"
          :pagination="true"
          :paginationPageSize="paginationPageSize"
          :stopEditingWhenCellsLoseFocus="true"
          @cell-value-changed="onCellValueChanged"
          @selection-changed="onSelectionChanged">
        </ag-grid-vue>
      </div>
    </div>
    <div class="item flex xs12" style="padding: 0.5rem;">
      <div>
        <select style="width: 160px;" v-on:change="onPageSizeChanged()" id="page-size">
          <option value="30">30/page</option>
          <option value="50">50/page</option>
          <option value="100">100/page</option>
          <option value="500" selected="">500/page</option>
        </select>
      </div>
      <span style="min-width: 10%;" />
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="onPrintButtonClicked" style="margin-right: 5px;"> 印刷 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="onCsvDownload" style="margin-right: 5px;">CSV出力</va-button>
      </div>
      <!-- <div class="item flex xs1">
          <impPermitBtn
          v-bind:isLoading="this.isLoading"
          v-bind:changeLoading="this.changeLoading"
          v-bind:printData = "this.printData"
          />
      </div> -->
    </div>
    <va-modal
      v-model="showCustomContent"
      no-padding >
      <template #content="{ ok }">
        <va-card-title>
          印刷レイアウト選択
        </va-card-title>
        <div>
          <div v-for="(item, index) in this.printType" :key="index">
            <input type="checkbox" style = "margin: 5px 5px 5px 5px" :id="item.imageTypeCd" :value="item.imageTypeCd" v-model="this.checkedPrintType" />
              <label :for="item.imageTypeCd">{{item.imageType}}</label>
          </div>
        </div>
        <va-card-actions>
          <va-button @click="getPrintData">印刷</va-button>
          <va-button @click="ok" color="danger">閉じる</va-button>
        </va-card-actions>
      </template>
    </va-modal>
  </va-card>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'

import '../../components/commonGrid/GridCss.css'
import { setAllRowData, getSelectdRowsData, getSelectedNodes } from '../../components/commonGrid/CmnGridApi.vue'
import GridButton from './OAIT003GridButton.vue'
import GridSwitch from './OAIT003GridSwitch.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
// import impPermitBtn from '../OC_CS_006/OCCS006printPermitButton.vue'
import {notificationSuccess, notificationError, notificationWarn} from "@/components/Notification/NotificationApi";

export default {
  name: 'App',
  props: {
    resultRowData: Array,
    changeLoading: Function,
    isLoading: Boolean,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridSwitch: GridSwitch, // eslint-disable-line
    gridButton: GridButton, // eslint-disable-line
    // impPermitBtn,
  },
  data () {
    return {
      columnDefs: null,
      rowData: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        resizable: true,
        cellStyle: {'border-right': '1px dotted'} // eslint-disable-line
      },
      printData : [],
      printType : [],
      checkedPrintType : [],
      rowSelection: null,
      showCustomContent: false,
      pLayoutCk1 : 0,
      pLayoutCk2 : 0,
      pLayoutCk3 : 0,
      pLayoutCk4 : 0,
      pLayoutCk5 : 0,
      pLayoutCk6 : 0,
      pLayoutCk7 : 0,
      pLayoutCk8 : 0,
      paginationPageSize: 500,
      schCondition: {
        hawbNo: '',
        cwbNoDeptCd : '',
      },
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: 'select',
        field: 'ccheck',
        minWidth: 55,
        maxWidth: 55,
        resizable: false,
        headerCheckboxSelection: true,
        headerCheckboxSelectionFilteredOnly: true,
        checkboxSelection: true,
        cellClass: 'cellCenter'
      },
      {
        headerName: 'MAWB No',
        field: 'awbno',
        minWidth: 120,
        width: 120
      },
      {
        headerName: 'HAWB No',
        field: 'cwbno',
        minWidth: 120,
        width: 120
      },
      {
        headerName: '輸入者コード',
        field: 'impcustomerno',
        minWidth: 140,
        width: 140
      },
      {
        headerName: '輸入者名',
        field: 'impcustomername',
        minWidth: 120,
        width: 120
      },
      {
        headerName: '輸入者住所',
        field: 'impcustomeradd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '電話番号',
        field: 'impcustomertelno',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '申告番号',
        field: 'reportno',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '許可日時',
        field: 'permitdate2',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '審査区分',
        field: 'examinationflgdisc',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '申告価格',
        field: 'amo',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '品名',
        field: 'item',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '個数',
        field: 'cargopiece',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '関税',
        field: 'kanzei',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '消費税',
        field: 'shouhizei',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '地方消費税',
        field: 'chihoshouhizei',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '税額合計',
        field: 'taxamo',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '郵便番号',
        field: 'impcustomerpostcode',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'エリア',
        field: 'area',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '送り先住所',
        field: 'destadd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '重量',
        field: 'cargoweight',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'FLTNo',
        field: 'loadingflt1',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'FLTDate',
        field: 'arrportdate',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'Payment',
        field: 'payment',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'Freight',
        field: 'freight',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'Contact Person',
        field: 'contactperson',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '原産地',
        field: 'consignorcountry',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '荷送人名',
        field: 'consignorname',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '荷送人住所',
        field: 'consignoradd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '通貨',
        field: 'farecurrencycd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'Invoice価格',
        field: 'invoicevalue',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '申告日',
        field: 'reportdate',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '記事',
        field: 'news',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '識別',
        field: 'shikibetsu',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '会社名',
        field: 'expcustomername',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  'L/S',
        field: 'largesmallflg',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '通関コード',
        field: 'representativecd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '上屋コード',
        field: 'clearplacecd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '口座識別',
        field: 'preliminaryreport',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '納付方法',
        field: 'paymethoddisc',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '官署',
        field: 'reportdivcd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '部門',
        field: 'reportdepcd',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '輸入者（入力）',
        field: 'inputimpcustomerno',
        minWidth: 120,
        width: 120
      },
      {
        headerName:  '納期限延長コード',
        field: 'deliverydateextcd',
        minWidth: 120,
        width: 120
      }
    ]
    this.rowData = [
    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  methods: {
    onPrintButtonClicked(){

      this.printType = [];

      this.showCustomContent = true; // eslint-disable-line
      const selectedRows = getSelectdRowsData(this.gridApi);
      console.log('selectedRows: ', selectedRows);

      if (selectedRows.length === 0) {
        this.getPrintTypeListMultiple();
      } else if (selectedRows.length === 1) {
        this.schCondition.hawbNo = selectedRows[0].cwbno;
        this.schCondition.cwbNoDeptCd = selectedRows[0].cwbnodeptcd;
        this.getPrintTypeList(this.schCondition);
      } else {
        this.getPrintTypeListMultiple();
      }
    },
    getPrintTypeList(schCondition) {
      this.axios({
        url: '/api/oait003/printList',
        method: 'get',
        params: schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(schCondition);
          console.log(res.data);
          for (let i = 0; i < res.data.length; i++) {
            this.printType.push(res.data[i]);
          }
          console.log(this.printType);
        }).catch(error => {
        console.log('error' + error)
      }).finally(() => {
        console.log('finally')
      })
    },
    getPrintData() {
      console.log(this.checkedPrintType);
      if (this.checkedPrintType.includes('HD')) {
        // 申告入力控え(ハード)
        this.changeLoading()
        // 搬入伝票作成開始
        this.axios.post('/api/occs006/printHd', this.printData)
          .then(res => {
            console.log('【印刷結果】:',  res.data.result)
            // 作成結果正常の場合
            if(res.data.result){
              notificationSuccess("印刷処理が完了致しました。")
              // S3保存先Path取得
              for (let i = 0; i < res.data.filePathList.length; i++) {
                this.pdfUrl = res.data.filePathList[i]
                this.axios({
                  method: 'post',
                  url: '/api/oait001/getPdf',
                  data: { pdfUrl: this.pdfUrl },
                  responseType: 'arraybuffer',
                })
                  .then(response => {
                    // Popより作成した搬入伝票PDF出力
                    const file = new Blob([(response.data)], { type: 'application/pdf' })
                    const fileUrl = URL.createObjectURL(file)
                    window.open(fileUrl, '_blank', 'width=1560,height=800');
                  })
              }
            } else {
              notificationError(res.data.message)
            }
          }).catch(error => {
          console.log(error)
          notificationError("サーバ通信でエラーが発生しました。",error.message)
        }).finally(() => {
          this.changeLoading()
        })
      }
      if (this.checkedPrintType.includes('ID')) {
       // 内点書
      }
      if (this.checkedPrintType.includes('IN')) {
        // INVOICE
      }
      if (this.checkedPrintType.includes('ME')) {
        // MEMO
      }
      if (this.checkedPrintType.includes('PE')) {
        // 許可
        this.changeLoading()
        // 搬入伝票作成開始
        this.axios.post('/api/occs006/printPermit', this.printData)
          .then(res => {
            console.log('【印刷結果】:',  res.data.result)
            // 作成結果正常の場合
            if(res.data.result){
              notificationSuccess("印刷処理が完了致しました。")
              // S3保存先Path取得
              for (let i = 0; i < res.data.filePathList.length; i++) {
                this.pdfUrl = res.data.filePathList[i]
                console.log('test Url : ' + this.pdfUrl)
                this.axios({
                  method: 'post',
                  url: '/api/oait001/getPdf',
                  data: { pdfUrl: this.pdfUrl },
                  responseType: 'arraybuffer',
                })
                  .then(response => {
                    // Popより作成した搬入伝票PDF出力
                    const file = new Blob([(response.data)], { type: 'application/pdf' })
                    const fileUrl = URL.createObjectURL(file)
                    window.open(fileUrl, '_blank', 'width=1560,height=800');
                  })
              }
            } else {
              notificationWarn(res.data.message, res.data.errorMessage)
            }
          }).catch(error => {
          console.log(error)
          notificationError("印刷処理でエラーが発生しました。",error.message)
        }).finally(() => {
          this.changeLoading()
        })
      }
      if (this.checkedPrintType.includes('BI')) {
        // 搬入伝票
        this.changeLoading()
        // 搬入伝票作成開始
        this.axios.post('/api/occs006/printBondInBill', this.printData)
          .then(res => {
            // 作成結果正常の場合
            console.log('【印刷結果】:',  res.data.result)
            if(res.data.result){
              notificationSuccess("印刷処理が完了致しました。")
              // S3保存先Path取得
              for (let i = 0; i < res.data.filePathList.length; i++) {
                this.pdfUrl = res.data.filePathList[i]
                this.axios({
                  method: 'post',
                  url: '/api/oait001/getPdf',
                  data: { pdfUrl: this.pdfUrl },
                  responseType: 'arraybuffer',
                })
                  .then(response => {
                    // Popより作成した搬入伝票PDF出力
                    const file = new Blob([(response.data)], { type: 'application/pdf' })
                    const fileUrl = URL.createObjectURL(file)
                    window.open(fileUrl, '_blank', 'width=1560,height=800');
                  })
              }
            } else {
              notificationError(res.data.message)
            }
          }).catch(error => {
          console.log(error)
          notificationError("サーバ通信でエラーが発生しました。",error.message)
        }).finally(() => {
          this.changeLoading()
        })
      }
      if (this.checkedPrintType.includes('BP')) {
        // BP承認
      }
      if (this.checkedPrintType.includes('CD')) {
        // 確認書
      }
      if (this.checkedPrintType.includes('CI')) {
        // HAWB/INVOICE
      }
      if (this.checkedPrintType.includes('CO')) {
        // 訂正
      }
      if (this.checkedPrintType.includes('CW')) {
        // HAWB
      }
      if (this.checkedPrintType.includes('EX')) {
        // 検査指定表
        this.changeLoading()
        // 搬入伝票作成開始
        this.axios.post('/api/occs006/printExam', this.printData)
          .then(res => {
            console.log('【印刷結果】:',  res.data.result)
            // 作成結果正常の場合
            if(res.data.result){
              notificationSuccess("印刷処理が完了致しました。")
              // S3保存先Path取得
              for (let i = 0; i < res.data.filePathList.length; i++) {
                this.pdfUrl = res.data.filePathList[i]
                this.axios({
                  method: 'post',
                  url: '/api/oait001/getPdf',
                  data: { pdfUrl: this.pdfUrl },
                  responseType: 'arraybuffer',
                })
                  .then(response => {
                    // Popより作成した搬入伝票PDF出力
                    const file = new Blob([(response.data)], { type: 'application/pdf' })
                    const fileUrl = URL.createObjectURL(file)
                    window.open(fileUrl, '_blank', 'width=1560,height=800');
                  })
              }
            } else {
              notificationError(res.data.message)
            }
          }).catch(error => {
          console.log(error)
          notificationError("サーバ通信でエラーが発生しました。",error.message)
        }).finally(() => {
          this.changeLoading()
        })
      }
      if (this.checkedPrintType.includes('RE')) {
        // 申告
      }
      if (this.checkedPrintType.includes('RP')) {
        // 申告・許可

      }
      if (this.checkedPrintType.includes('SP')) {
        // 予備
      }

    },
    getPrintTypeListMultiple() {
      this.axios({
        url: '/api/oait003/printListMultiple',
        method: 'get',
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
          for (let i = 0; i < res.data.length; i++) {
            this.printType.push(res.data[i]);
          }
          console.log(this.printType);
        }).catch(error => {
        console.log('error' + error)
      }).finally(() => {
        console.log('finally')
      })
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
      this.printData = selectedRows
      console.log("printData :", this.printData)
      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes: ', selectedNodes)
    },
    onCsvDownload() {
      const date = new Date();
      const csvFileName = "輸入情報検索_"
        + date.getFullYear().toString()
        + ('00' + (date.getMonth()+1)).slice(-2)
        + ('00' + (date.getDate())).slice(-2)
        + ('00' + (date.getHours())).slice(-2)
        + ('00' + (date.getMinutes())).slice(-2)
        + ('00' + (date.getSeconds())).slice(-2)
        + ".csv"
      console.log(csvFileName);
      const csvParams = {
        fileName: csvFileName
      }
      this.gridApi.exportDataAsCsv(csvParams);
    },
  },
  provide: {
    providedValue: 'testValue', // provide this value to grid components
    provideGridSwitchAgreeLabel: '対象',
    provideGridSwitchDisAgreeLabel: '対象外',
    provideGridButtonLabel: 'ConsoleOutRowData'
  }
}
</script>

<style>
</style>
