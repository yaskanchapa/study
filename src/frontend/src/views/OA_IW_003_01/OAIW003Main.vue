<script setup>
import SearchCondition from './SearchCondition.vue'
import SearchResult from './SearchResult.vue'
import UploadComponent from './SearchConditionUpload.vue'
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
        <component :is="currentView"
          v-bind:searchBondInData="searchBondInData"
          v-bind:changeLoading="changeLoading"
          v-bind:isLoading="isLoading"
          v-bind:resultRowData="resultRowData"
          ref="schCondition">
        </component>
      </div>
      <!-- 検索結果 -->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <SearchResult
          v-bind:resultRowData="resultRowData"
          v-bind:changeLoading="changeLoading"
          v-bind:isLoading="isLoading"
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

// TABページ設定
const TABS = [
  { title: '検索', name: 'searchCondition', color: 'success' },
  { title: '指示内容', name: 'uploadComponent', color: 'success' }
]

export default {
  components: {
    searchCondition: SearchCondition, // eslint-disable-line
    uploadComponent: UploadComponent
  },
  data: () => ({
    tabs: TABS,
    currentView: 'searchCondition',
    fromChildSchCondition: [], // 子コンポーネントからsearchBondInDataメソッドがコールする際にセット
    resultRowData: [],
    awbNoList: [],
    isLoading: false
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  mounted(){
    this.emitter.on('myEvent', (arr) => {
      console.log(arr)
      this.resultRowData = arr
      console.log('resultRowData: ', this.resultRowData)
    });
  },
  methods: {
    changeLoading () { // Loding中操作ロック処理
      this.isLoading = !this.isLoading
    },
    searchBondInData(schCondition) { // 検索実行
      console.log('条件入力値: ', schCondition)
      this.changeLoading()
      this.fromChildSchCondition = schCondition
      // 必修項目入力チェック
      if(!schCondition.workingDays || schCondition.workingDays === '') {
        notificationError('登録年月日は必須です。')
        this.changeLoading()
        return false
      }
      if(!schCondition.bondedWareHouseCd || schCondition.bondedWareHouseCd === '') {
        notificationError('蔵置場所は必須です。')
        this.changeLoading()
        return false
      }

      // サーバー通信
      this.axios.post('/api/oaiw003/doSearch', schCondition)
      .then(res => {
          console.log('【検索結果1】:',  res.data.resultData)
          this.resultRowData = res.data.resultData
          console.log('【検索結果2】:',  this.resultRowData)
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
</style>
