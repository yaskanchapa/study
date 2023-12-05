<script setup>
// eslint-disable-next-line no-unused-vars
import SearchCondition from './SearchCondition.vue'
import SearchResult from './SearchResult.vue'
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
</script>

<template>
  <div class="container" >
      <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索条件</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <va-tabs v-model="currentView">
                <template #tabs>
                  <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                </template>
              </va-tabs>
              <!--bilの２箇所要確認--><!--ここの使用方法が不明-->
              <component :is="currentView" 
              v-bind:searchBondInData="searchBondInData" 
              v-bind:bilResult="bilResult"
              v-bind:recreateBilResult="recreateBilResult"
              v-bind:createBilFlag="createBilFlag"
              v-bind:isLoading="isLoading"
              >
              </component>
            </div>
        </div>
      

      <!-- 検索結果 -->
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <SearchResult 
              v-bind:resultRowData="resultRowData" 
              v-bind:createBilFlag="createBilFlag" 
              v-bind:bilResult="bilResult" 
              v-bind:recreateBilResult="recreateBilResult"
              v-bind:isLoading="isLoading"
              v-bind:changeLoading="changeLoading"/>
            </div>
        </div>

    </va-inner-loading>
  </div>
</template>

<script>
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: '検索', name: 'searchCondition', color: 'success' }

]

export default {
  components: {
    searchCondition: SearchCondition, // eslint-disable-line
  },
  data: () => ({
    resultRowData:[],
    tabs: TABS,
    currentView: 'searchCondition',
    isLoading: false,
    createBilFlag: true
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods: {
    changeLoading() {
      this.isLoading = !this.isLoading
    },
    searchBondInData(schCondition) {
      this.changeLoading()
      this.axios({
        url: '/api/oaew001/search',
        method: 'get',        
        params: schCondition,
      })
        .then(res => {
          const errorFlag = res.data.errorFlag
          if (errorFlag === 'warning') {
            notificationWarn(res.data.message)
            this.resultRowData = []
          } else if (errorFlag === 'error') { 
            this.resultRowData = []
            notificationError(res.data.message)
          } else {
            this.resultRowData = res.data.searchResultList
            const userAuth = loginStore.getters.getAuthPermission.userAuthorityCd
            if (userAuth === '02') {
              this.createBilFlag = true
            } else {
              this.createBilFlag = false
            }
            notificationSuccess(res.data.message, "", 3)
          }
        }).catch(error => {
          console.log('error' + error)
          notificationError("対象表示処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
  
    },
    // Bil伝票作成
    bilResult(bilList){
      this.changeLoading()
      if(bilList.length === 0){
        notificationError('BIL作成対象を選択してください。')
        this.changeLoading()
      } else {
        this.axios.post('/api/oaew001/bil', bilList)
          .then(res => {
            if (res.data.errorFlag === 'false') {
              notificationSuccess('ファイル' + res.data.cntFile + '件作成完了')
            } else if(res.data.errorFlag === 'waring') {
              notificationWarn(res.data.message)
            } else if(res.data.errorFlag === 'error') {
              notificationError(res.data.message)
            }
          }).catch(error => {
            console.log(error)
            notificationError("BIL作成処理でエラーが発生しました。", error.message)
          }).finally(() => {
            this.changeLoading()
          })
      }
    },
    // Bil再作成  
    recreateBilResult(recreateBilList){
      this.changeLoading()
      if(recreateBilList.length === 0){
        notificationError('BIL再作成対象を選択してください。')
        this.changeLoading()
      } else {
        this.axios.post('/api/oaew001/recreateBil', recreateBilList)
          .then(res => {
            if (res.data.errorFlag === 'false') {
              notificationSuccess('ファイル' + res.data.cntFile + '件作成完了')
            } else if(res.data.errorFlag === 'waring') {
              notificationWarn(res.data.message)
            } else if(res.data.errorFlag === 'error') {
              notificationError(res.data.message)
            }
          }).catch(error => {
            console.log(error)
            notificationError("BIL再作成処理でエラーが発生しました。", error.message)
          }).finally(() => {
            this.changeLoading()
          })
      }
    },
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

</style>
