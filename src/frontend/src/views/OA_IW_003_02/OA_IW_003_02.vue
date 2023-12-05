<!-- eslint-disable indent -->
<template>
  <va-inner-loading :loading="isLoading" :size="60">
    <va-card style="height:fit-content;">
      <div class="flex w-full">
        <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 100%;">

          <div class="row justify-start" style="padding: 0.3rem;">
            <div class="item flex xs6">
              <span style="padding: 0.3rem; width: 80px; ">HAWB No.</span>
              <va-input style="width: 200px; font-size: 5px; margin-right: 7px;" :disabled="isLoading" v-model="schCondition.cwbNo" />
            </div>
            <div class="item flex xs2" style="padding: 0rem;">
              <va-switch :disabled="isLoading" v-model="schCondition.unMatchOver" true-inner-label="オーバー" false-inner-label="オーバー" size="medium"/>
            </div>
            <div class="item flex xs2" style="padding: 0rem;">
              <va-switch :disabled="isLoading" v-model="schCondition.unMatchShort" true-inner-label="ショート" false-inner-label="ショート" size="medium"/>
            </div>
            <div class="item flex xs2" style="padding: 0rem;">
              <va-switch :disabled="isLoading" v-model="schCondition.match" true-inner-label="マッチ" false-inner-label="マッチ" size="medium"/>
            </div>
          </div>

          <div class="row justify-end" style="padding: 1.0rem;">
            <div class="item flex xs3">
              <va-button :disabled="isLoading" color="info" type="submit" size="medium" @click="searchDefault"> HAWB検索 </va-button>
            </div>
          </div>

          <div class="row justify-start">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue 
                style="width: 600px; height: 200px" class="ag-theme-alpine" 
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
  </va-inner-loading>
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
// import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import loginStore from '../../store/OC_CS_003/loginUserInfoStore'

export default {
  name: 'App',
  props: {
    resultMainRowData: Object,
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    Datepicker, // eslint-disable-line
  },
  data() {
    return {
      schCondition: {
        cwbNo: '',
        unMatchOver: true,
        unMatchShort: true,
        match: true,
        workStartTime: '',
        workEndTime: '',
        objectFlg: '',
        bondedWareHouseCd: '',
        endDisplay: '',
        awbNo: '',
        flt1: '',
        flt2: '',
      },
      // schConditionList: [],
      defaultSearchCond: {
        defaultRowData: [],
      },
      // awbNo: '',
      deptCdList: [],
      columnDefs: null,
      defaultRowData: [],
      gridApi: null,
      columnApi: null,
      rowSelection: null,

      isLoading: false,
    }
  },
  beforeMount() {
    this.columnDefs = [
      { // HAWB No.
        headerName:'HAWB No.',
        field: 'cwbNo',
        minWidth: 140,
        maxWidth: 140,
        resizable: false,
      },
      { // 端末ID
        headerName:'端末ID',
        field: 'handyTerminalId',
        minWidth: 100,
        maxWidth: 100,
        resizable: false,
      },
      { // スキャン日時 yyyy/MM/dd HH:mm:ss
        headerName:'スキャン日時',
        field: 'scanDateTime',
        minWidth: 160,
        maxWidth: 160,
        resizable: false
      },
      { // 個数
        headerName:'個数',
        field: 'scanPiece',
        minWidth: 80,
        maxWidth: 80,
        resizable: false
      },
      { // STS
        headerName:'STS',
        field: 'sts',
        minWidth: 100,
        maxWidth: 100,
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
    // this.setDefaultValue()
    this.schCondition.bondedWareHouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
    this.schCondition.workStartTime = this.resultMainRowData.workStartTime
    this.schCondition.workEndTime = this.resultMainRowData.workEndTime
    this.schCondition.objectFlg = this.resultMainRowData.objectFlg
    this.schCondition.endDisplay = this.resultMainRowData.endFlg
    this.schCondition.awbNo = this.resultMainRowData.awbNo
    this.schCondition.flt1 = this.resultMainRowData.flt1
    this.schCondition.flt2 = this.resultMainRowData.flt2
  },
  methods: {
    // 画面ロック処理
    changeLoading () { // Loding中操作ロック処理
      this.isLoading = !this.isLoading
    },
    // GridApi等セット
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },

    // 検索実行メソッド
    searchDefault() {
      this.changeLoading()
      console.log('サーバー転送データ: ', this.schCondition)
      this.axios.post('/api/oaiw003/searchStatus', this.schCondition)
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