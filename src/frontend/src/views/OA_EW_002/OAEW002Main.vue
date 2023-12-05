<script setup>
import SearchCondition from './SearchCondition.vue'
import HeaderAndDetail from './HeaderAndDetail.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>  
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索条件</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <va-tabs v-model="currentView">
          <template #tabs>
            <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
          </template>
        </va-tabs>
        <template v-if="currentView === SEARCH_CONDITION">
          <component :is="currentView"
            v-bind:disabled="isLoading"
            v-bind:isLoading="isLoading"
            v-bind:isSearch="isSearch"
            v-bind:customsPlaceList="customsPlaceList"
            v-bind:bondedWarehouseCdList="bondedWarehouseCdList"
            v-bind:depPortList="depPortList"
            v-bind:containerClassCdList="containerClassCdList"
            v-bind:departureTruckNoList="departureTruckNoList"
            v-bind:searchContainer="searchContainer"
            v-bind:changeSearch="changeSearch"
            v-bind:clearAll="clearAll"
            v-bind:fromParentSchCondition="fromChildSchCondition"
            v-bind:saveSearchCondigiton="saveSearchCondigiton"
            :ref="currentView">
         </component>
        </template>
        <template v-else>
          <component :is="currentView"
            v-bind:isLoading="isLoading"
            v-bind:isSearch="isSearch"
            v-bind:changeSearch="changeSearch"
            v-bind:clearAll="clearAll"
            v-bind:fromParentSchCondition="fromChildSchCondition"
            v-bind:fromParentRegistCondition="fromChildRegistCondition"
            v-bind:saveRegistCondition="saveRegistCondition"
            v-bind:customsPlaceList="customsPlaceList"
            v-bind:bondedWarehouseCdList="bondedWarehouseCdList"
            v-bind:depPortList="depPortList"
            v-bind:containerClassCdList="containerClassCdList"
            v-bind:departureTruckNoList="departureTruckNoList"
            v-bind:registContainer="registContainer"
            v-bind:deleteContainer="deleteContainer"
            v-bind:selectedRowData="selectedRowData"
            v-bind:clearSelectedRowData="clearSelectedRowData"
            :ref="currentView">
          </component>
        </template>
      </div>
      <!-- 検索結果 -->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <SearchResult
          v-bind:resultRowData="resultRowData"
          v-bind:updateContainer="updateContainer"
          v-bind:isLoading="isLoading"
          v-bind:disabled="isLoading"
          v-bind:isSearch="isSearch"
          v-bind:changeSearch="changeSearch"
          v-bind:clearAll="clearAll"
          v-bind:setRowDataToHeader="setRowDataToHeader"
          ref="schResult"
        />
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import _ from 'lodash' // eslint-disable-line
import { notificationSuccess, notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const SEARCH_CONDITION = 'searchCondition'
const HEADER_AND_DETAIL = 'headerAndDetail'

const TABS = [
  { title: '検索', name: SEARCH_CONDITION, color: 'success', ref: SEARCH_CONDITION},
  { title: 'ヘッダー・明細', name: HEADER_AND_DETAIL, color: 'success', ref: HEADER_AND_DETAIL }
]

export default {
  components: {
    searchCondition: SearchCondition, 
    headerAndDetail: HeaderAndDetail
  },
  data: () => ({
    tabs: TABS,
    currentView: SEARCH_CONDITION,
    fromChildSchCondition: null,
    fromChildRegistCondition: null,
    resultRowData: [],
    customsPlaceList: [],
    bondedWarehouseCdList: [],
    depPortList: [],
    containerClassCdList: [],
    departureTruckNoList: [],
    isLoading: false,
    isSelected: false,
    isSearch: false,
    selectedRowData: null,
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  beforeMount() {    
    if(!this.customsPlaceList.length || !this.bondedWarehouseCdList.length
        || !this.depPortList.length || !this.containerClassCdList.length
        || !this.departureTruckNoList.length) {
      // 初期化されてない場合のみパラメータを取得する。
      this.axios.post('/api/oaew002/getInitData')
        .then(res => {
          if(res.data.result){
            this.customsPlaceList = res.data.customsPlaceList
            this.bondedWarehouseCdList = res.data.bondedWarehouseCdList
            this.depPortList = res.data.depPortList
            this.containerClassCdList = res.data.containerClassCdList
            this.departureTruckNoList = res.data.departureTruckNoList
          } else {
            notificationWarn(res.data.message, res.data.errorMessage)
          }
        }).catch(error => {
          console.log(error)
          notificationError("画面パラメータ取得に失敗しました。", error.message)
        }).finally(() => {
        })
    }
  },
  methods: {
    changeLoading () {
      this.isLoading = !this.isLoading
    },
    changeSearch () {
      this.isSearch = !this.isSearch
    },
    saveSearchCondigiton(schCondition) {
      this.fromChildSchCondition = schCondition
    },
    saveRegistCondition(registCondition) {
      this.fromChildRegistCondition = registCondition
    },
    clearSelectedRowData() {
      this.selectedRowData = null
    },
    setRowDataToHeader(rowData) {
      if(this.currentView !== HEADER_AND_DETAIL) {        
        this.selectedRowData = rowData
        this.currentView = HEADER_AND_DETAIL
      } else {
        this.$refs.headerAndDetail.unFix()
        this.$refs.headerAndDetail.setData(rowData)
      }
    },
    searchContainer(schCondition) {
      this.changeLoading()
      this.fromChildSchCondition = schCondition
      this.axios.post('/api/oaew002/doSearch', schCondition)
      .then(res => {
        if(res.data.result){
          this.resultRowData = res.data.resultRowData
          this.changeSearch()

          if(this.resultRowData.length !== 0) {
            notificationSuccess(res.data.message, "", 3)  
          } else {
            notificationWarn("対象情報がありません。新規登録してください。", "", 3)
            this.currentView = HEADER_AND_DETAIL
          }
        } else {
          this.resultRowData = []
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("コンテナ検索処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    registContainer(registCondition) {
      this.changeLoading()
      this.axios.post('/api/oaew002/doRegist', registCondition)
      .then(res => {
        if(res.data.result) {          
          if(res.data.new) {
            this.$refs.schResult.addNewRow(res.data.newRowData)
          }
          notificationSuccess(res.data.message, "", 3)
        } else {
          this.resultRowData = []
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("コンテナ登録処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    deleteContainer(deleteCondition) {
      if(!this.$refs.schResult.chkDelCondition(deleteCondition)) {
        return
      }
      this.changeLoading()
      this.axios.post('/api/oaew002/doDelete', {
        containerDate: deleteCondition.containerDate,
        awbNo: deleteCondition.awbNo,
        uldNo: deleteCondition.uldNo
      })
      .then(res => {
        if(res.data.result) {
          this.$refs.schResult.delRow(deleteCondition)
          notificationSuccess(res.data.message, "", 3)
        } else {
          this.resultRowData = []
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("コンテナ削除処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    updateContainer(updateCondition) {
      this.changeLoading()
      this.axios.post('/api/oaew002/doUpdate', updateCondition)
      .then(res => {
        if(res.data.result) {
          notificationSuccess(res.data.message, "", 3)
        } else {
          notificationWarn(res.data.message, res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("コンテナ登録(HT非表示・更新)処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
    clearAll() {
      this.changeLoading()
      this.changeSearch()
      this.resultRowData = [] // clear ag-grid data
      this.fromChildSchCondition = null
      this.fromChildRegistCondition = null
      if(this.$refs.searchCondition){
        this.$refs.searchCondition.clear()
      }
      if(this.$refs.headerAndDetail){
        this.$refs.headerAndDetail.clear()
      }      
      this.changeLoading()
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
</style>
