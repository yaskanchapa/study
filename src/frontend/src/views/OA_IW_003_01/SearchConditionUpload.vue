<!-- eslint-disable indent -->
<template>
  <va-card style="height:fit-content;">
    <div class="flex w-full">
      <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 100%;">
       
        <div class="row justify-start" >
          <div class="item flex xs12">
            <h1 style="padding: 0.3rem;">MAWB選択</h1>
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs5">
            <span style="padding: 0.3rem;">登録年月日</span>
            <Datepicker v-model="schCondition.recordDateFrom"
            :auto-apply="true"
            @closed="recordDateFromPickerClosedChange"
            :format="DatePickerFormat"
            :enable-time-picker="false"
            :disabled="isLoading"
            />

            <div style="padding: 0.5rem; width: 10px;">～</div>

            <Datepicker v-model="schCondition.recordDateTo"
            :auto-apply="true"
            @closed="recordDateToPickerClosedChange"
            :format="DatePickerFormat"
            :enable-time-picker="false"
            :disabled="isLoading"
            />
          </div>

          <div class="item flex xs3">
            <span style="padding: 0.3rem;">到着便名</span>
            <va-input style="width: 80px;" :disabled="isLoading" :readonly="schCondition.objectAll" v-model="schCondition.arrFlt1"/>
            <span style="padding: 0.3rem;">/</span>
            <va-input style="width: 80px;" :disabled="isLoading" :readonly="schCondition.objectAll" v-model="schCondition.arrFlt2" />
          </div>

          <div class="item flex xs3">
            <span style="padding: 0.3rem; width: 50px;">MAWB No.</span>
            <va-input style="width: 200px; margin-right: 7px;" :disabled="isLoading" :readonly="schCondition.objectAll" v-model="schCondition.awbNo" />
          </div>

          <div class="item flex xs1" style="padding: 0rem;">
            <va-switch :disabled="isLoading" v-model="schCondition.objectAll" true-inner-label="対象(全件)" false-inner-label="対象(全件)" size="medium"/>
          </div>

        </div>

        <div class="row justify-end" style="padding: 1.0rem;">
          
          <div class="item flex xs1">
            <va-button color="info" type="submit" size="medium" :disabled="schCondition.objectAll" @click="searchDefault"> MAWB検索 </va-button>
          </div>

          <div class="item flex xs1">
            <va-button color="info" type="submit" size="medium"  @click="selectInsertMawb"> 指示 </va-button>
          </div>

        </div>

        <div class="row justify-start">
          <div class="ag-theme-alpine grid-wrapper">
              <ag-grid-vue 
              style="width: 100%; height: 200px" class="ag-theme-alpine" 
              :columnDefs="columnDefs" 
              :rowData="defaultSearchCond.defaultRowData" 
              @grid-ready="onGridReady"
              :suppressRowClickSelection="true"
              :rowSelection="rowSelection"
              :stopEditingWhenCellsLoseFocus="true"
              >
              </ag-grid-vue>
          </div>
        </div>
      </div>

    </div>
  </va-card>
</template>
<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import loginStore from '../../store/OC_CS_003/loginUserInfoStore'

export default {
  name: 'App',
  props: {
    changeLoading: Function,
    resultRowData: Array,
    isLoading: Boolean,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    Datepicker, // eslint-disable-line
  },
  data() {
    return {
      schCondition: {
        recordDateFrom: moment().format('YYYY-MM-DD'),
        recordDateTo: moment().format('YYYY-MM-DD'),
        awbNo: '',
        arrFlt1: '',
        arrFlt2: '',
        bondedWareHouseCd: '',
        objectAll: false, // 対象(全件)
      },

      // 登録年月日フォマード設定
      DatePickerFormat: 'yyyy-MM-dd',

      schConditionList: [],
      defaultSearchCond: {
        defaultRowData: [],
      },
      awbNo: '',
      deptCdList: [],
      columnDefs: null,
      defaultRowData: [],
      gridApi: null,
      columnApi: null,
      rowSelection: null
      
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
      { // 便名/日付
        headerName:'FLT',
        field: 'gtxtFlt',
        minWidth: 300,
        maxWidth: 300,
        resizable: false,
      },
      { // 航空運送状番号
        headerName:'MAWB No.',
        field: 'gtxtAwbNo',
        minWidth: 700,
        maxWidth: 700,
        resizable: false,
      },
      { // 登録年月日 yyyy/MM/dd HH:mm:ss
        headerName:'登録年月日',
        field: 'gtxtArrportDate',
        minWidth: 300,
        maxWidth: 300,
        resizable: false
      },
      { // 対象件数
        headerName:'対象件数',
        field: 'gtxtCount',
        minWidth: 200,
        maxWidth: 200,
        resizable: false
      },
    ]
    this.rowData = [

    ]
  },
  created() {
    this.rowSelection = 'multiple' // 複数選択可能
  },
  computed: {
  },
  mounted() { 
    this.schCondition.bondedWareHouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
  },
  methods: {
    // GridApi等セット
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },

    // 登録年月日フォマード設定
    recordDateFromPickerClosedChange() {
      if (this.schCondition.recordDateFrom) {
        this.schCondition.recordDateFrom = moment(this.schCondition.recordDateFrom).format('YYYY-MM-DD')
      }
    }, 
    recordDateToPickerClosedChange() {
      if (this.schCondition.recordDateTo) {
        this.schCondition.recordDateTo = moment(this.schCondition.recordDateTo).format('YYYY-MM-DD')
      }
    }, 

    // 検索実行メソッド
    searchDefault() {
      if(!this.schCondition.recordDateFrom || !this.schCondition.recordDateTo) {
        notificationError('登録年月日は必須です。')
        return false
      }
      if(this.schCondition.recordDateFrom > this.schCondition.recordDateTo) {
        notificationError('登録年月日に誤りがあります。')
        return false
      }
      this.changeLoading()
      this.axios.post('/api/oaiw003/doSearch2', this.schCondition)
      .then(res => {
        this.defaultSearchCond.defaultRowData = res.data.resultData
        console.log('検索結果: ', this.defaultSearchCond.defaultRowData)
      }).catch(error => {
        console.log(error)
        notificationError("対象表示処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },

    // 指示ボタン
    selectInsertMawb() {
      const paramList= []
      this.changeLoading()
      // 選択したデータ取得
      const selectedRows = getSelectdRowsData(this.gridApi)
      console.log('selectedRows: ', selectedRows)
      // 入力値有無チェック
      if(!this.schCondition.objectAll){
        if(!this.schCondition.awbNo){
          if(!selectedRows || selectedRows.length === 0) {
            notificationError('登録するデータを選択してください。')
            this.changeLoading()
            return
          }
        }
      }
      // Mawb選択の場合、選択したデータを指示欄の条件項目に代入
      selectedRows.forEach(param => {
        console.log('param: ', param.gtxtAwbNo)

        paramList.push({
          recordDateFrom: this.schCondition.recordDateFrom,
          recordDateTo: this.schCondition.recordDateTo,
          awbNo: param.gtxtAwbNo,
          arrFlt1:param.gtxtFlt.split('/')[0],
          arrFlt2: param.gtxtFlt.split('/')[1],
          bondedWareHouseCd:this.schCondition.bondedWareHouseCd,
          objectAll:this.schCondition.objectAll
        })
        console.log('paramList: ', paramList)
      })
      // Mawb選択とMawb手入力チェック
      if(paramList.length > 0){
        if(this.schCondition.awbNo){
          notificationError('選択されているMawb Noがあります。')
          this.changeLoading()
          return
        }
      }
      this.changeLoading()
      this.schConditionList = paramList
      this.insertMawb()
    },

    // MawbのInsert処理メソッド
    insertMawb() {
      // 追加したデータのArray
      let insertRstArr = []
      // 既存検索結果欄のデータのArray
      let mainRstArr = []
      // 追加データ+既存検索結果欄のデータArray
      let totalArr = []

      this.changeLoading()

      // Mawb選択処理の場合
      if(!this.schCondition.objectAll){
        // 選択件数0件の場合は手入力かをチェック
        if(this.schConditionList < 1){
          // 手入力値チェック
          if(!this.schCondition.awbNo) {
            notificationError('MAWB No.は必須です。')
            this.changeLoading()
            return false
          }
          // 手入力値をschConditionListへ追加
          this.schConditionList.push(this.schCondition)
        }
      } else {
        this.schConditionList.push(this.schCondition)
      }
      console.log('手入力処理', this.schConditionList)

      // objectAllがtrue, paramListが0件の場合も正常処理(対象(全件)処理)

      // サーバー通信処理
      this.axios.post('/api/oaiw003/Instraction', this.schConditionList)
      .then(res => {
        console.log('指示処理結果データ：',res.data)
        if(!res.data.result){
          console.log(res.data.errorMessage)
          notificationError("対象表示処理でエラーが発生しました。", res.data.errorMessage)
        }
        insertRstArr = res.data.resultData
        if(this.resultRowData){
          mainRstArr = this.resultRowData
        }
        // mainRstArr = this.resultRowData
        totalArr = insertRstArr.concat(mainRstArr);
        this.emitter.emit('myEvent', totalArr);
        console.log('指示結果: ', res.data.resultData)
        console.log('totalArr: ', totalArr)
      }).catch(error => {
        console.log(error)
        notificationError("対象表示処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },


  }
}

</script>
<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.ant-notification-top {
  margin-top: 30%;
}
</style>


<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #2b89e7;
}

.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #337ecc
}

.bar-color3 {
  --tw-bg-opacity: 1;
  background-color: #858d96
}

.button-middle{
  height: 80px;
}
</style>