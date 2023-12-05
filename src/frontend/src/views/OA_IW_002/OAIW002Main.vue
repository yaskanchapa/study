<script setup>
import SearchCondition from './SearchCondition.vue'
import OutFileCreateCnt from './OutFileCreateCnt.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>  
  <div class="container" >
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left" >検索条件</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <va-tabs v-model="currentView">
          <template #tabs>
            <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
          </template>
        </va-tabs>
        <component :is="currentView"
          v-bind:isLoading="isLoading"
          v-bind:searchBondoutData="searchBondoutData"
          v-bind:awbNoList="awbNoList"
          v-bind:clearResult="clearResult"
          v-bind:createOutFile="createOutFile"
          v-bind:disabled="isLoading"
          ref="schCondition">
        </component>
      </div>
      <!-- 検索結果 -->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <SearchResult
          v-bind:resultRowData="resultRowData"          
          v-bind:isLoading="isLoading"
          v-bind:doPermit="doPermit"
          v-bind:doBondedOut="doBondedOut"
          v-bind:disabled="isLoading"
          ref="schResult"
        />
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: '検索', name: 'searchCondition', color: 'success' },
  { title: '作成件数', name: 'outFileCreateCnt', color: 'success' }
]

export default {
  components: {
    searchCondition: SearchCondition, 
    outFileCreateCnt: OutFileCreateCnt
  },
  data: () => ({
    tabs: TABS,
    currentView: 'searchCondition',
    fromChildSchCondition: [], // 子コンポーネントからsearchBondInDataメソッドがコールする際にセット
    resultRowData: [],
    awbNoList: [],
    isLoading: false,
    isSelected: false
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods: {
    changeLoading () {
      this.isLoading = !this.isLoading
    },
    searchBondoutData(schCondition) {
      console.log("schCondition", schCondition)
      this.changeLoading()
      this.fromChildSchCondition = schCondition
      if(!schCondition.carryOutDate) {
        notificationError('搬出年月日は必須です。')
        this.changeLoading()
        return false
      }
      if(!schCondition.bondedWarehouseCd) {
        notificationError('蔵置場所は必須です。')
        this.changeLoading()
        return false
      }
      let _awbNoList = []
      this.axios.post('/api/oaiw002/doSearch', schCondition)
      .then(res => {
        console.log("res", res)
        if(res.data.result){
          this.resultRowData = res.data.resultData          
          this.resultRowData.forEach(rowData => {
            _awbNoList.push(rowData._awbNo)
          })

          // MAWB No.のリスト設定
          _awbNoList.push("") // 空白文字列セット
          _awbNoList = _awbNoList.concat(this.awbNoList)
          this.awbNoList = [...new Set(_awbNoList)].sort() // 重複削除、ソート
          
          notificationSuccess(res.data.message, "", 3)
        } else {
          this.resultRowData = []
          this.awbNoList = []
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("対象表示処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    clearResult() {
      this.changeLoading()
      this.resultRowData = []
      this.awbNoList = []
      this.$refs.schCondition.clearAwbNoSelect()
      this.changeLoading()
    },
    doPermit(rowDataPermit, rowDataBondedOut) {      
      this.changeLoading()
      this.axios.post('/api/oaiw002/doPermit', {
        param1: this.fromChildSchCondition,
        param2: rowDataPermit,
        param3: rowDataBondedOut,
      })
      .then(res => {
        console.log("doPermit res", res)
        if(res.data.result){
          // 許可行のデータ更新
          const permitRowData = res.data.permitDto.param2
          this.$refs.schResult.setRowData(permitRowData._id, permitRowData)

          // 搬出行のデータ更新
          const bondedOutRowData = res.data.permitDto.param3
          this.$refs.schResult.setRowData(bondedOutRowData._id, bondedOutRowData)
          notificationSuccess(res.data.message, "", 3)
        } else {
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("許可処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    doBondedOut(rowDataPermit, rowDataBondedOut) {      
      this.changeLoading()
      this.axios.post('/api/oaiw002/doBondedOut', {
        param1: this.fromChildSchCondition,
        param2: rowDataPermit,
        param3: rowDataBondedOut,
      })
      .then(res => {
        console.log("doBondedOut res", res)
        if(res.data.result){
          // 許可行のデータ更新
          const permitRowData = res.data.bondedOutDto.param2
          this.$refs.schResult.setRowData(permitRowData._id, permitRowData)

          // 搬出行のデータ更新
          const bondedOutRowData = res.data.bondedOutDto.param3
          this.$refs.schResult.setRowData(bondedOutRowData._id, bondedOutRowData)
          notificationSuccess(res.data.message, "", 3)
        } else {
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("搬出処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    }, 
    createOutFile() {
      const _gridDataList = this.$refs.schResult.getAllGridData();
      console.log("_gridDataList",_gridDataList)
      // Gridのデータ数チェック
      if(_gridDataList.length === 0) {
        notificationWarn("OUT処理対象データを選択してください。", "", 3)
        return
      }
      // Gridのデータの選択チェック
      this.isSelected = false
      _gridDataList.forEach((rowData) => {
        if(rowData.select){
          this.isSelected = true
        }
      })
      if(!this.isSelected){
        notificationWarn("OUT処理対象データを選択してください。", "", 3)
        return
      }
      this.changeLoading()
      console.log("this.fromChildSchCondition", this.fromChildSchCondition)
      this.axios.post('/api/oaiw002/doOut', {
        param: this.fromChildSchCondition,
        gridDataList: this.$refs.schResult.getAllGridData(),
        reSearchParam: this.fromChildSchCondition // OUTファイル作成処理後、一覧の再検索用パラメータ
      })
      .then(res => {
        if(res.data.result) {
          this.$refs.schCondition.setOutResult(res.data.awbNoCnt, res.data.outFileCnt)
          this.resultRowData = res.data.gridDataList
          notificationSuccess(res.data.message, "", 3)

          if(res.data.reSearchErr) { // OUT処理後、一覧の再検索処理でエラー発生
            notificationWarn(res.data.errorMessage, "")
          }

        } else {
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("OUT処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    }
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
</style>
