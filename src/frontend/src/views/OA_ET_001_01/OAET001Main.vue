<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import SearchResult from './SearchResult.vue'
import SearchComponent from './SearchConditionSearch.vue'
import UploadComponent from './SearchConditionUpload.vue'
import ReportComponent from './SearchConditionReport.vue'
import HeaderComponent from './HeaderInformation.vue'
</script>

<template>
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
    <!--検索条件-->
    <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索条件</div>
    <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
      <va-tabs v-model="currentView">
        <template #tabs>
          <va-tab v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
        </template>
      </va-tabs>
      <component :is="currentView"  
        v-bind:searchCondition="searchCondition"  
        v-bind:searchExportData="searchExportData"  
        v-bind:clearResult="clearResult" 
        v-bind:resettingDefaultValue="resettingDefaultValue"
        v-bind:writeReport="writeReport"
        v-bind:cntReportResult="cntReportResult"
        v-bind:changeLoading="changeLoading"
        v-bind:getAwbNoList="getAwbNoList"
        v-bind:awbNoList="awbNoList"
        v-bind:checkAwbNo="checkAwbNo"
        v-bind:deptCdList="deptCdList"
        v-bind:agencyList="agencyList"
        v-bind:getOtherSearchInfo="getOtherSearchInfo"
        v-bind:plusAwbList="plusAwbList">
      </component>
    </div>

    <!-- 情報表示 -->
    <div class="ripple w-1340 py-2 px-5  text-white bg-darkgrey-500" style="background-color: lightslategrey;" align="left">
      <button type="button" @click="showComponenet"><va-icon name="add_circle_outline" /></button>  情報表示
      <div v-show="hidCom" class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <HeaderComponent v-model:resultHeaderData="resultHeaderData" 
          v-bind:editHeaderData="editHeaderData" 
          v-bind:customsList="customsList"
          v-bind:bonList="bonList"
          v-bind:agencyList="agencyList"
          v-bind:depList="depList"/>
      </div>
    </div>

    <!-- 検索結果 -->
    <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索結果</div>
    <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
      <SearchResult 
        v-bind:resultRowData="resultRowData" 
        v-bind:editExportData="editExportData"
        v-bind:deleteExportData="deleteExportData" />
    </div>
    </va-inner-loading>
  </div>
</template>

<script>
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import oaet001store from '@/store/OA_ET_001/OAET001Store'
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: '検索', name: 'searchComponent', color: 'success' },
  { title: '取込', name: 'uploadComponent', color: 'success' },
  { title: '申告', name: 'reportComponent', color: 'success' }
]

export default {
  components: {
    searchComponent: SearchComponent, // eslint-disable-line
    uploadComponent: UploadComponent,
    reportComponent: ReportComponent,
  },
  data: () => ({
    tabs: TABS,
    currentView: 'searchComponent',
    awbMessage: '',
    hidCom: true,
    isLoading: false,
    selectedReportList: [
      'MIC','HCH'
    ],
    resultRowData: [],
    micCwbList: [],
    idaCwbList: [],
    awbNoList: [],
    customsList: [],
    bonList: [],
    depList: [],
    agencyList: [],
    deptCdList: [],
    cntReportResult: {
      cntMEC: '0',
      cntEDA: '0',
      cntEDC: '0',
      cntFileMEC: '0',
      cntFileEDA: '0',
      cntFileEDC: '0'
    },
    searchCondition: {
      awbNo: '',
      truckNoDate: moment().format('YYYY-MM-DD'),
      cwbNo: '',
      linkTruckNo: '',
      cdbFlg: false,
      mecFlg: false,
      edaFlg: false,
      edcFlg: false,
      remakeFlg: false,
      truckNoFrom: '',
      truckNoTo: ''
    },
    resultHeaderData: {
      expReportPlanDate: moment().format('YYYY-MM-DD'),
      customsPlaceCd: '',
      inHouseRefNumber: '',
      bonWareHoCurDate: moment().format('YYYY-MM-DD'),
      headReportCondition: '',
      depPort: '',
      loadingPlanFlt1: '',
      loadingPlanFlt2: '',
      fltDestination: ''
    },
  }),
  computed: {
    currentTab() {
      return this.tabs.find(({ title }) => title === this.tabValue)
    },
  },
  mounted() {
    this.searchHeaderDefList()
    this.setDefaultValue()
    this.$watch(
      () => this.searchCondition.awbNo,
      (newValue, oldValue) => {
        this.getOtherSearchInfo(newValue)
      }
    )
  },
  methods: {
    changeLoading() {
      this.isLoading = !this.isLoading
    },
    showComponenet() {
      this.hidCom = !this.hidCom
    },
    getAwbNoList() {
      this.searchCondition.awbNo = ''
      if (this.searchCondition.linkTruckNo === null || this.searchCondition.linkTruckNo === ''
        || this.searchCondition.truckNoDate === null || this.searchCondition.truckNoDate === '') {
        return
      }
      this.axios({
        url: '/api/oaet001/getAwbNoList',
        method: 'get',
        params: this.searchCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          this.awbNoList = res.data
        }).catch(error => {
          notificationError("検索処理でエラーが発生しました。", error.message)
        })
    },
    searchExportData() {
      if (this.searchCondition.awbNo === null || this.searchCondition.awbNo === '') {
        if (this.searchCondition.cwbNo === null || this.searchCondition.cwbNo === '') {
          notificationError('MAWB番号又はHAWB番号は必須です。')
          return false

        }
      }
      this.changeLoading()
      this.clearCwbList()
      this.axios({
        url: '/api/oaet001/search',
        method: 'get',
        params: this.searchCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          if (res.data.errFlg === 'error') {
            this.clearResult()
            notificationError(res.data.message)
          } else if (res.data.errFlg === 'warning') { 
            this.clearResult()
            notificationWarn(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
            this.resultRowData = res.data.searchResult
            this.resultHeaderData = res.data.searchResult[0]
            this.divisionCwbList()
          }
        }).catch(error => {
          notificationError("検索処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    divisionCwbList() {
      oaet001store.commit('clearEdaCwbList')
      oaet001store.commit('clearMecCwbList')
      this.resultRowData.forEach(param => {
        if (param.eda === '1') {
          oaet001store.commit('setEdaCwbList', param)
        } else {
          oaet001store.commit('setMecCwbList', param)
        }
      })
    },
    searchHeaderDefList() { 
      this.changeLoading()
      this.axios.post('/api/oaet001/searchHeaderDefList')
        .then(res => {
          this.customsList = res.data.customsList
          this.bonList = res.data.bonList
          this.depList = res.data.depList
          this.agencyList= res.data.agencyList
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    setDefaultValue() {
      this.changeLoading()
      this.axios.get('/api/oaet001/getDefaultValueUpload')
        .then(res => {
          this.deptCdList = res.data.deptCdList
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    clearCwbList() { 
      oaet001store.commit('clearEdaCwbList')
      oaet001store.commit('clearMecCwbList')
    },
    editExportData(selectedList) {
      this.changeLoading()
      if (!selectedList || selectedList.length === 0) {
        notificationError('登録するデータを選択してください。')
        this.changeLoading()
        return
      }
      for (let i = 0; i < selectedList.length; i++) {
        if (selectedList[i].miSumi === '1') { 
          notificationWarn("既に編集済みのデータです。")
          this.changeLoading()
          return
        }
       }
      if (!confirm("チェックしたデータを全部編集済に変更しますか？")) {
        this.changeLoading()
        return
       }
      this.axios.post('/api/oaet001/editExportData', selectedList)
        .then(res => {
          if (res.data.errFlg === 'false') { 
            this.searchExportData()
            notificationSuccess(res.data.message, "", 3)
          } else {
            notificationError(res.data.message)
          }
        }).catch(error => {
          notificationError("登録処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    insertExcel(frm) {
      this.changeLoading()
      this.axios.post('/api/oaet001/uploadExcel', frm, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
        .then(res => {
          if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
            this.resultRowData = res.data.searchResult
            this.resultHeaderData = res.data.searchResult[0]
          }
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    // デフォルト値再設定
    resettingDefaultValue(defaultRowData, awbNo) {
      this.changeLoading()
      if (!defaultRowData || defaultRowData.length === 0) {
        notificationError('デフォルト値を選択してください')
        this.changeLoading()
        return
      }
      if (awbNo === '' || awbNo === null) {
        notificationError('MAWB番号を入力してください')
        this.changeLoading()
        return
      }
      const paramList = defaultRowData
      const tmp = {
        nameCd: 'awbNo',
        name: 'awbNo',
        characterItem: awbNo
      }
      paramList.push(tmp)
      this.axios.post('/api/oait001/resettingDefVal', paramList)
        .then(res => {
          notificationSuccess(res.data + "件のデータを更新しました。", "", 3)
        })
        .catch(error => {
          notificationError("更新処理でエラーが発生しました。", error.message)
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    editHeaderData() {
      this.changeLoading()
      this.axios.post('/api/oaet001/editHeaderData', this.resultHeaderData)
        .then(res => {
          if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
          } else if (res.data.errFlg === 'warning') {
            notificationWarn(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
          }
        }).catch(error => {
          notificationError("登録処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    clearResult() {
      this.resultRowData = []
      this.resultHeaderData = {
        awbNo: '',
        reportPlaningDate: moment().format('YYYY-MM-DD'),
        inHouseRefNumber: '',
        reqMixedForwarder: '',
        customsDiv: '',
        arrAirportCd: '',
        arrFlt1: '',
        arrFlt2: '',
        reportCondition: '',
        getPort: '',
        arrDate: moment().format('YYYY-MM-DD'),
        present: '',
        arrPortDate: moment().format('YYYY-MM-DD'),
        grandChildMixed: '',
        split: '',
        catereringPlace: '',
        shipmentCd: '',
        clearPlanPlaceCd: '',
        joint: ''
      }
    },
    selectFltList() {
      this.currentArrFlt1List = []
      this.searchCondition.currentArrFlt1 = ''
      const awbMap = {
        'awbNo': this.searchCondition.awbNo
      }
      this.axios.post('/api/oait001/selectFltList', awbMap)
        .then(res => {
          this.currentArrFlt1List = res.data
        }).catch(error => {
          notificationError("登録処理でエラーが発生しました。", error.message)
        }).finally(() => {
          
        })

    },
    writeReport(reportList) {
      if (this.checkReport()) { 
        notificationError("「MAWB番号」又は「トラック便日付・トラック便」は必要です。")
        return
      }
      this.changeLoading()
      this.axios.post('/api/oaet001/writeReport', this.searchCondition)
        .then(res => {
          if (res.data.errorFlag === 'error') {
            notificationError(res.data.message)
          } else if (res.data.errorFlag === 'warning') {
            notificationWarn(res.data.message)
           }
          else {
            notificationSuccess(res.data.message, "", 3)
            this.cntReportResult.cntEDA = res.data.cntEDA
            this.cntReportResult.cntFileEDA = res.data.cntFileEDA

            this.cntReportResult.cntEDC = res.data.cntEDC
            this.cntReportResult.cntFileEDC = res.data.cntFileEDC

            this.cntReportResult.cntMEC = res.data.cntMec
            this.cntReportResult.cntFileMEC = res.data.cntFileMEC

            this.cntReportResult.cntCDB = res.data.cntCdb
            this.cntReportResult.cntFileCDB = res.data.cntFileCDB
          }
        }).catch(error => {
          notificationError("登録処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    checkReport() { 
      if (this.searchCondition.awbNo === '')
      {
        if (this.searchCondition.truckNoDate === '' ||  this.searchCondition.truckNoFrom === '' || this.searchCondition.truckNoTo === '') {
          return true
        }
      }
      return false
    },
    checkAwbNo(frm) {
      this.changeLoading()
      this.awbMessage = ''
      this.axios({
        url: '/api/oaet001/searchAwbNo',
        method: 'get',
        params: this.searchCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          // eslint-disable-next-line eqeqeq
          if (res.data.errFlg === 'error') {
            if (confirm(res.data.message)) { 
              frm.append('deleteOld', '1')
              this.insertExcel(frm)
            }
          } else { 
            frm.append('deleteOld', '1')
            this.insertExcel(frm)
          }
        }).catch(error => {
          notificationError("検索処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    deleteExportData(selectedList) {
      this.changeLoading()
      if (!selectedList || selectedList.length === 0) {
        notificationError('削除するデータを選択してください。')
        this.changeLoading()
        return
      }
      this.axios.post('/api/oaet001/deleteExportData', selectedList)
        .then(res => {
          if (res.data.errorFlg === 'error') {
            notificationError(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
          }
        }).catch(error => {
          if (error.response.status === 401) {
            notificationError('本機能の利用権限がないです。');
          } else {
            notificationError("削除処理でエラーが発生しました。", error.message)
          }
        }).finally(() => {
          this.changeLoading()
        })
    },
    getOtherSearchInfo(value) {
      this.changeLoading()

      this.searchCondition.awbNo = value

      this.axios({
        url: '/api/oaet001/getOtherSearchInfo',
        method: 'get',
        params: this.searchCondition
      })
        .then(res => {
          if (res.data.errFlg === 'false') { 
            this.searchCondition.truckNoDate = res.data.searchCond.truckNoDate
            this.searchCondition.linkTruckNo = res.data.searchCond.linkTruckNo
          }
          
        }).catch(error => {
          notificationError("検索処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    plusAwbList(value) {
      const option = value
      this.searchCondition.awbNo = option
    }
  }
}
</script>

<style>
.bar-color1-blue {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.bar-color2-blue {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}

.ant-notification-top {
  margin-top: 30%;
}


.bar-color3 {
  --tw-bg-opacity: 1;
  background-color: #858d96
}
</style>
