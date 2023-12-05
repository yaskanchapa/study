<script setup>
</script>
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
            :paginationPageSize="paginationPageSize"
            :pagination="true"
            :stopEditingWhenCellsLoseFocus="true"
            @cell-value-changed="onCellValueChanged"
            @selection-changed="onSelectionChanged"
            :changeLoading="this.changeLoading"
            :isLoading="this.isLoading"
            :context="gridContext">
        </ag-grid-vue>
      </div>
    </div>
    <div style="max-width: 500px;">
      <select style="width: 160px;" v-on:change="onPageSizeChanged()" id="page-size">
        <option value="30">30/page</option>
        <option value="50">50/page</option>
        <option value="500" selected="">500/page</option>
      </select>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <!--<va-button color="info" class="mr-3 mb-0" @click="showCustomContent = !showCustomContent" >印刷</va-button> -->
       <va-button color="info" class="mr-3 mb-0" @click = "getList" >印刷</va-button> 
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium" @click="onCsvDownload">CSV出力</va-button>
      </div>
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
import { notificationSuccess, notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import '../../components/commonGrid/GridCss.css'
import {  getSelectdRowsData, getSelectedNodes } from '../../components/commonGrid/CmnGridApi.vue'
import GridButton from './OAET003GridButton.vue'
import GridSwitch from './OAET003GridSwitch.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { dateFormat } from '@/utils/utilities'

export default {
    props: {        
    resultRowData: Array,
    
    
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridSwitch: GridSwitch, // eslint-disable-line
    gridButton: GridButton // eslint-disable-line
  },
  data () {
    return {
      mawbNo: '',
      value: false,
      value2: false,
      columnDefs: null,
      gridApi: null,
      columnApi: null,
      isLoading: false,
      printType : [],
      printData : [],
      checkedPrintType : [],
      names: [],
      showPopup: false,
      schCondition: {
      hawbNo: '',
      printType:'',
      imageTypeCd:'',
      cwbNoDeptCd : ''
      },
      defaultColDef: {
      resizable: true,
      
      cellStyle: {'border-right': '1px dotted'} // eslint-disable-line
      
    },
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
      gridContext: {
        'onOffBatch': this.onOffBatch
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
      { // MAWBNo
        headerName:'MAWBNo',
        field: 'awbNo',
        minWidth: 150,
        width: 150
      },
      { 
        headerName:'行No',
        field: 'rowNo',
        minWidth: 90,
        width: 90
      },
      { // HAWBNo
        headerName:'HAWBNo',
        field: 'cwbNo',
        minWidth: 150,
        width: 150
      },
      { // 輸出者コード
        headerName:'輸出者コード',
        field: 'empCustomerNo',
        minWidth: 150,
        width: 150
      },
      { // 輸出者名
        headerName:'輸出者名',
        field: 'empCustomerName',
        minWidth: 150,
        width: 150
      },
      { // 輸出者住所
        headerName:'輸出者住所',
        field: 'expCustomerAddFull',
        minWidth: 280,
        width: 280
      },
      { // 電話番号
        headerName:'電話番号',
        field: 'empCustomerTelNo',
        minWidth: 120,
        width: 120
      },
      { // 申告番号
        headerName:'申告番号',
        field: 'reportNo',
        minWidth: 120,
        width: 120
      },
      { // 許可日時
        headerName:'許可日時',
        field: 'permitDate2',
        minWidth: 160,
        width: 160
      },
      { // 審査区分
        headerName:'審査区分',
        field: 'examinationFlgDisc',
        minWidth: 120,
        width: 120
      },
      { // 個数
        headerName:'個数',
        field: 'cargoPiece',
        minWidth: 100,
        width: 100
      },
      { // 重量
        headerName:'重量',
        field: 'cargoWeight',
        minWidth: 100,
        width: 100
      },
      { // 申告種別
        headerName:'申告種別',
        field: 'kindCd',
        minWidth: 120,
        width: 120
      },
      { // 宛先税関
        headerName:'宛先税関',
        field: 'reportDepCd',
        minWidth: 120,
        width: 120
      },
      { // 提出先
        headerName:'提出先',
        field: 'reportDivisionCd',
        minWidth: 120,
        width: 120
      },
      { // 記事（税関）
        headerName:'記事(税関)',
        field: 'news1',
        minWidth: 130,
        width: 130
      },
      { // 記事（通関）
        headerName:'記事(通関)',
        field: 'news2',
        minWidth: 130,
        width: 130
      },
      { // 記事（荷主）
        headerName:'記事（荷主）',
        field: 'newsShipper',
        minWidth: 130,
        width: 130
      },
      { // 社内整理番号
        headerName:'社内整理番号',
        field: 'inHouseRefNumber',
        minWidth: 150,
        width: 150
      },
      { // 識別
        headerName:'識別',
        field: 'largeSmallFlg',
        minWidth: 120,
        width: 120
      },
      { // 上屋
        headerName:'上屋',
        field: 'clearPlanPlaceCd',
        minWidth: 120,
        width: 120
      },
      { // 仕向地
        headerName:'仕向地',
        field: 'lastDestName',
        minWidth: 120,
        width: 120
      },
      { // FOB価格
        headerName:'FOB価格',
        field: 'fobAmo',
        minWidth: 120,
        width: 120
      },
      { // 申告方法
        headerName:'申告方法',
        field: 'esaFlagName',
        minWidth: 120,
        width: 120
      },
      { // 申告日時
        headerName:'申告日時',
        field: 'reportDate2',
        minWidth: 160,
        width: 160
      },          
    ]
  },
  created() {
    this.rowSelection = 'multiple'
    this.paginationPageSize = 500;
  },
  methods: {
        changeLoading() {
      this.isLoading = !this.isLoading;
    },
    onPageSizeChanged() {
      // eslint-disable-next-line no-var
      var value = document.getElementById('page-size').value;
      this.gridApi.paginationSetPageSize(Number(value));
    },
    onBtNext() {
      this.gridApi.paginationGoToNextPage();
    },
    onBtPrevious() {
      this.gridApi.paginationGoToPreviousPage();
    },
    onBtPageone() {
      this.gridApi.paginationGoToPage(0);
    },
    onBtPagetwo() {
      this.gridApi.paginationGoToPage(1);
    },
    onBtPagethree() {
      this.gridApi.paginationGoToPage(2);
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
      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes: ', selectedNodes)
    },
    onCsvDownload() {
      this.gridApi.exportDataAsCsv(this.getCsvFileName())
    },
    getCsvFileName() {
      const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
      return {
        fileName: '輸出情報検索_' + currentDate,
      }
    },
    getList(){
      this.printType = [];
      this.showCustomContent = true; // eslint-disable-line
      const selectedRows = getSelectdRowsData(this.gridApi);
      console.log('selectedRows: ', selectedRows);
    //  const selectedRows = getSelectdRowsData(this.gridApi);
    this.axios({
        url: '/api/oaet003/searchPrint',
        method: 'get',
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data);
        //  this.names = res.data.name
          this.names=res.data.resultData
          for (let i = 0; i < res.data.length; i++) {
            this.printType.push(res.data[i]);
          }
        }).catch(error => {
           console.log('error' + error);
      }).finally(() => {
        console.log('finally')
        this.showPopup = true;
        
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
    
  }
    
}
</script>