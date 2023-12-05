<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import SearchResult from './SearchResult.vue'
import SearchComponent from './SearchConditionSearch.vue'
import UploadComponent from './SearchConditionUpload.vue'
import ReportComponent from './SearchConditionReport.vue'
import HeaderComponent from './HeaderInformation.vue'
import HawbScanModal from './HawbScanModal.vue'
import WinPopup from '../OC_CS_001_01/winPop.vue'
import { releaseAllLockByUserCd } from '../../components/HawbLogicalLock/HawbLogicalLock.vue' // eslint-disable-line
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
</script>

<template>
  <div class="container" >
    <va-inner-loading :loading="isLoading" :size="60">
      <div>
        <template>
          <winPopup ref="winPopup" @onRecvEvtFromWinPop="val => onRecvWinPop(val)">
          </winPopup>
        </template>
      </div>
      <!--検索条件-->
      <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left" >検索条件</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <va-tabs v-model="currentView">
          <template #tabs>
            <va-tab v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
          </template>
        </va-tabs>
        <component :is="currentView" v-bind:currentArrFlt1List="currentArrFlt1List"
          v-bind:searchCondition="searchCondition" v-bind:searchImportData="searchImportData"
          v-bind:clearResult="clearResult" v-bind:resettingDefaultValue="resettingDefaultValue"
          v-bind:selectFltList="selectFltList" v-bind:writeReport="writeReport" v-bind:cntReportResult="cntReportResult"
          v-bind:cntReportFileResult="cntReportFileResult" v-bind:changeLoading="changeLoading"
          v-bind:insertExcel="insertExcel" v-bind:setDefaultValue="setDefaultValue" v-bind:agencyList="agencyList"
          v-bind:deptCdList="deptCdList" v-bind:checkAwbNo="checkAwbNo">
        </component>
      </div>

      <!-- 情報表示 -->
      <div class="ripple w-1340 py-2 px-5  text-white bg-darkgrey-500" style="background-color: lightslategrey;"
        align="left">
        <button type="button" @click="showComponenet"><va-icon name="add_circle_outline" /></button> 情報表示
        <div v-show="hidCom" class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
          <HeaderComponent v-model:resultHeaderData="resultHeaderData" v-bind:editHeaderData="editHeaderData"
            v-bind:agencyList="agencyList" v-bind:setDefaultValue="setDefaultValue" />
        </div>
      </div>

      <!-- 検索結果 -->
      <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索結果</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <SearchResult v-bind:resultRowData="resultRowData" v-bind:editImportData="editImportData"
          v-bind:deleteImportData="deleteImportData" v-bind:onCellValueChanged="onCellValueChanged" v-bind:getPdfUrl="getPdfUrl" v-bind:resultHeaderData="resultHeaderData" @openHawbScanModal="openHawbScanModal" @handleDeleteButtonClicked="handleDeleteButtonClicked"/>
      </div>

      <!--  HAWBスキャン   -->
      <HawbScanModal :showModal="showHawbScanModal" @closeHawbScanModal="closeHawbScanModal" :hawbInitData="hawbInitData" :hawbScanProps="hawbScanProps" />
    </va-inner-loading>
  </div>
</template>

<script>
import oait001store from '@/store/OA_IT_001/OAIT001Store'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import { NOTIFICATION_DURATION } from '../../utils/utilities'
import { SYSTEM_ERROR_MESSAGE } from '../../utils/messages'



const TABS = [
  { title: '検索', name: 'searchComponent', color: 'success' },
  { title: '取込', name: 'uploadComponent', color: 'success' },
  { title: '申告', name: 'reportComponent', color: 'success' }
]

export default {
  mounted() {
    this.$watch(
      () => this.detailData,
      (newValue, oldValue) => {
        this.sendToChild(newValue.pdfUrl)
      }
    )
    this.setDefaultValue()
    this.initHawbScanData()
  },
  created() { 
    window.addEventListener('beforeunload', this.unLoadEvent);
  },
  beforeUnmount() {
    window.removeEventListener('beforeunload', this.unLoadEvent);
  },
  components: {
    winPopup: WinPopup,
    searchComponent: SearchComponent, // eslint-disable-line
    uploadComponent: UploadComponent,
    reportComponent: ReportComponent,
    HawbScanModal
  },
  data: () => ({
    DatePickerFormat: 'yyyy-MM-dd',
    tabs: TABS,
    currentView: 'searchComponent',
    resultHeaderData: {
      awbNo: '',
      awbNoOriginal: '',
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
      split: false,
      catereringPlace: '',
      shipmentCd: '',
      clearPlanPlaceCd: '',
      joint: '',
    },
    selectedReportList: [
      'MIC', 'HCH'
    ],
    searchCondition: {
      awbNo: '',
      currentArrFlt1: '',
      cwbNo: '',
      reportPlaningDate: '',
      idaFlg: false,
      micFlg: false,
      chaFlg: false,
      hchFlg: false,
      hdmFlg: false,
      reCreate: false
    },
    currentArrFlt1List: [

    ],
    resultRowData: [

    ],
    cntReportResult: {
      cntHCH: '0',
      cntHDM: '0',
      cntCHA: '0',
      cntMIC: '0',
      cntFileHCH: '0',
      cntFileHDM: '0',
      cntFileCHA: '0',
      cntFileMIC: '0'
    },
    cntReportFileResult: {
      cntHCH: '0',
      cntHDM: '0',
      cntCHA: '0',
      cntMIC: '0',
      cntFileHCH: '0',
      cntFileHDM: '0',
      cntFileCHA: '0',
      cntFileMIC: '0'
    },
    micCwbList: [],
    idaCwbList: [],
    hidCom: true,
    hidCompM: '+',
    isLoading: false,
    agencyList: [],
    deptCdList: [],

    showHawbScanModal: false,
    hawbInitData: {},
    hawbScanProps: {},
    savedSearchCondition: {}
  }),
  computed: {
    currentTab() {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods: {
    unLoadEvent(event) {
      releaseAllLockByUserCd(this.axios, loginStore.getters.getAuthPermission.userCd)
    },
    changeLoading() {
      this.isLoading = !this.isLoading
    },
    showComponenet() {
      this.hidCom ? this.hidCom = false : this.hidCom = true
    },
    sendToChild(val) {
      this.$refs.winPopup.sendEvtToChild(val);
    },
    searchImportData(searchCondition) {
      if (!searchCondition.awbNo) {
        notificationError('MAWBは必須です。')
        return false
      }
      this.changeLoading()
      this.searchConditon = searchCondition
      this.axios({
        url: '/api/oait001/search',
        method: 'get',
        params: searchCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          if (res.data.errFlg === '1') {
            notificationError(res.data.message)
          } else if (res.data.errFlg === '2') {
            notificationWarn(res.data.message)
          } else if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
            this.resetResult()
          } else if (res.data.errFlg === 'warning') {
            notificationWarn(res.data.message, '', 3)
          } else {
            if (res.data.headerData.errFlg !== 'false') { 
              notificationWarn(res.data.headerData.msg)
            } else {
              notificationSuccess(res.data.message, "", 3)
            }

            this.resultRowData = res.data.detailDataList
            this.resultHeaderData = res.data.headerData
            const awbNo = this.resultHeaderData.awbNo
            this.resultHeaderData.awbNoOriginal = awbNo
            if (res.data.headerData.split === '1') {
              this.resultHeaderData.split = true
            }
            
            this.divisionCwbList()
          }
        }).catch(error => {
          if (error.response.status === 401) {
            notificationError('本機能の利用権限がないです。');
          }
        }).finally(() => {
          this.savedSearchCondition = this.searchCondition
          this.changeLoading()
        })
    },
    editImportData(selectedList) {
      this.changeLoading()
      if (!selectedList || selectedList.length === 0) {
        notificationError('登録するデータを選択してください。')
        this.changeLoading()
        return
      }

      for (let i = 0; i < selectedList.length; i++) {
        if (selectedList[i].editFlg === '1') {
          notificationWarn("既に編集済みのデータです。")
          this.changeLoading()
          return
        }
      }

      this.axios.post('/api/oait001/editImportData', selectedList)
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
            notificationError("登録処理でエラーが発生しました。", error.message)
          }
        }).finally(() => {
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
          if (res.data.errorFlg === 'error') {
            notificationError(res.data.message)
          } else if (res.data.errorFlg === 'warning') {
            notificationWarn(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
          }
        })
        .catch(error => {
          if (error.response.status === 401) {
            notificationError('本機能の利用権限がないです。');
          } else {
            notificationError("更新処理でエラーが発生しました。", error.message)
          }
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    editHeaderData() {
      if (this.resultHeaderData.awbNo === '') {
        notificationError("変更後MAWBは必須です。")
        return
      } else if (this.resultHeaderData.reportCondition === '') {
        notificationError("申告条件は必須です。")
        return
      } else if (this.resultHeaderData.arrFlt1 === '') {
        notificationError("積載便名(FROM)は必須です。")
        return
      } else if (this.resultHeaderData.arrFlt2 === '') {
        notificationError("積載便名(TO)は必須です。")
        return
      }
      this.changeLoading()
      this.axios.post('/api/oait001/editHeaderData', this.resultHeaderData)
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
            notificationError("登録処理でエラーが発生しました。", error.message)
          }
        }).finally(() => {
          this.changeLoading()
        })
    },
    divisionCwbList() {
      oait001store.commit('clearIdaCwbList')
      oait001store.commit('clearMicCwbList')
      this.resultRowData.forEach(param => {
        if (param.idaFlg === '1') {
          oait001store.commit('setIdaCwbList', param)
        } else {
          oait001store.commit('setMicCwbList', param)
        }
      })
    },
    clearResult() {
      this.resultRowData = []
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
        })
    },
    writeReport(reportList) {
      this.changeLoading()
      this.axios.post('/api/oait001/writeReport', this.searchCondition, {
        responseType: 'arraybuffer'
      })
        .then(res => {
          const headerMap = new Map(Object.entries(JSON.parse(res.headers['custom-header'])))
          if (headerMap.get('errorFlg') === 'error') {
            notificationError("登録処理でエラーが発生しました。")
          } else {
            notificationSuccess('電文ファイル作成を完了しました。', "", 3)
            this.cntReportResult.cntHCH = headerMap.get('cntHCH')
            this.cntReportResult.cntFileHCH = headerMap.get('cntFileHCH')

            this.cntReportResult.cntHDM = headerMap.get('cntHDM')
            this.cntReportResult.cntFileHDM = headerMap.get('cntFileHDM')

            this.cntReportResult.cntCHA = headerMap.get('cntCHA')
            this.cntReportResult.cntFileCHA = headerMap.get('cntFileCHA')

            this.cntReportResult.cntMIC = headerMap.get('cntMIC')
            this.cntReportResult.cntFileMIC = headerMap.get('cntFileMIC')
          }
        }).catch(error => {
          if (error.response.status === 401) {
            notificationError('本機能の利用権限がないです。');
          } else {
            notificationError("登録処理でエラーが発生しました。", error.message)
          }
        }).finally(() => {
          this.changeLoading()
        })
    },
    insertExcel(frm) {
      this.changeLoading()
      this.axios.post('/api/oait001/uploadExcel', frm, {
        headers: { 'Content-Type': 'multipart/form-data' }
      })
        .then(res => {
          if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
          } else {
            notificationSuccess(res.data.message, "", 3)
            this.resultRowData = res.data.detailDataList
            this.resultHeaderData = res.data.headerData
            if (res.data.headerData.split === '1') {
              this.resultHeaderData.split = true
            }
            this.divisionCwbList()
          }
        })
        .catch(error => {
          if (error.response.status === 401) {
            notificationError('本機能の利用権限がないです。');
          } else {
            notificationError('Excelファイル登録処理でエラーが発生しました。');
          }
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    setDefaultValue() {
      this.changeLoading()
      this.axios.get('/api/oait001/getDefaultValueUpload')
        .then(res => {
          this.deptCdList = res.data.deptCdList
          this.agencyList = res.data.agencyList
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    checkAwbNo(frm) {
      this.changeLoading()
      this.awbMessage = ''
      this.axios.get('/api/oait001/searchAwbNo', {
        params: {
          awbNo: frm.get('awbNo')
        }
      }).then(res => {
        // eslint-disable-next-line eqeqeq
        if (res.data.errorFlg === 'warning') {
          if (confirm(res.data.msg)) {
            frm.append('deleteOld', '1')
            this.insertExcel(frm)
          }
        } else {
          frm.append('deleteOld', '0')
          this.insertExcel(frm)
        }
      }).catch(error => {
        notificationError("検索処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    deleteImportData(selectedList) {
      this.changeLoading()
      if (!selectedList || selectedList.length === 0) {
        notificationError('削除するデータを選択してください。')
        this.changeLoading()
        return
      }
      this.axios.post('/api/oait001/deleteImportData', selectedList)
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
    onCellValueChanged(params) {
      const cellValue = {
        awbNo: params.data.awbNo,
        cwbNo: params.data.cwbNo,
        cwbNoDeptCd: params.data.cwbNoDeptCd,
        targetCol: params.colDef.field,
        newValue: params.newValue,
        oldValue: params.oldValue
      }
      this.changeLoading()
      this.axios({
        url: '/api/oait001/changeColumnValue',
        method: 'get',
        params: cellValue,
      })
        .then(res => {
          if (res.data.errFlg === "false") {
            notificationSuccess(res.data.message, "", 3)
            if (params.column.colId === 'idaFlg') {
              if (params.newValue === '1') {
                oait001store.commit('deleteMicCwbList', params.data.cwbNo)
                oait001store.commit('setIdaCwbList', params.data)
              } else if (params.newValue === '0') { 
                oait001store.commit('deleteIdaCwbList', params.data.cwbNo)
                oait001store.commit('setMicCwbList', params.data)
              }
            }
          } else {
            notificationWarn(res.data.message)
          }
        }).catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    getPdfUrl(params) {
      if (params.data.img === '無') {
        return
      }
      this.changeLoading()
      this.axios.get('/api/oait001/getPdfUrl', {
        params: {
          awbNo: params.data.awbNo,
          cwbNo: params.data.cwbNo,
          cwbNoDeptCd: params.data.cwbNoDeptCd
        }
      }).then(res => {
        if (res.data.errorFlag === 'false') {
          this.openWinPop(res.data.pdfUrl1, res.data.pdfUrl2)
        }
      }).catch(error => {
        notificationError("検索処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    openWinPop(param1, param2) {
      const uri = '/pdfViewer?param1=' + param1 + '&param2=' + param2
      // this.winPopup.openWinPop(uri, 1560, 700);
      window.open(uri,'PDF VIEWER');
    },
    testFile() {
      this.changeLoading()
      this.axios.get('/api/oait001/fileDown', {
        headers: { responseType: 'blob' }
      })
        .then(res => {
          const url = window.URL.createObjectURL(new Blob([res.data]));
          const link = document.createElement('a');
          link.href = url;
          link.setAttribute('download', res.headers['content-disposition']);
          document.body.appendChild(link);
          link.click();
        }).catch(error => {
          notificationError("登録処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },

    openHawbScanModal() {
      this.hawbScanProps = {
        awbNo: this.resultHeaderData.awbNo,
        arrflt_1: this.resultHeaderData.arrFlt1,
        arrflt_2: this.resultHeaderData.arrFlt2,
        boundedWarehouseCd: this.resultHeaderData.clearPlanPlaceCd,
      }
      this.showHawbScanModal = true
    },

    closeHawbScanModal() {
      this.showHawbScanModal = false
    },
    initHawbScanData() {
      this.changeLoading()
      this.axios
        .get(`api/oait001/hawbScan/init`)
        .then((res) => {
          this.hawbInitData = res.data
        })
        .catch((error) => {
          console.log(error)
          notificationError(SYSTEM_ERROR_MESSAGE, error.message)
        })
        .finally(() => {
          this.changeLoading()
        })
    },
    callCheckDeleteAllAPI() {
      return this.axios.get('api/oait001/checkDeleteAll', { params: this.savedSearchCondition })
    },
    callDeleteAllAPI() {
      return this.axios.get('api/oait001/deleteAll', { params: this.savedSearchCondition })
    },
    async handleDeleteButtonClicked() {
      this.changeLoading()
       try {
        // CHECK API CALL
        const checkResponse = await this.callCheckDeleteAllAPI()
        if (!checkResponse.data.result) {
          notificationError(checkResponse.data.msg)
        } else {
          // DELETE API CALL
            if (confirm(checkResponse.data.msg)) {
              const registResponse = await this.callDeleteAllAPI()
              notificationSuccess(registResponse.data.msg, '', NOTIFICATION_DURATION)
            }
        }
    } catch (error) {
        console.log(error)
        notificationError(SYSTEM_ERROR_MESSAGE, error.message)
    } finally {
        this.changeLoading()
        this.searchImportData(this.savedSearchCondition)
    }
    },
    resetResult() {
      this.resultRowData = []
      this.resultHeaderData = []
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