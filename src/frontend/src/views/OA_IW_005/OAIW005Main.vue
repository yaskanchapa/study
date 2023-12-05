<script setup>

import SearchCondition from './SearchCondition.vue'
import SearchResult from './SearchResult.vue'
import StatusModal from './StatusModal.vue'

</script>
<template>
<div class="container">
  <va-inner-loading :loading="isLoading" :size="60">
    <div class="w-90%">
      <!--検索条件-->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索条件</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <va-tabs v-model="currentView">
                <template #tabs>
                  <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                </template>
              </va-tabs>
              <!--v-bind定義-->
              <component :is="currentView" :initData="initData" @searchImportData="searchImportData" :countRecord="countRecord">
              </component>
            
            </div>
        </div>
      </div>
    
            <!-- 検索結果 -->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
             
             <SearchResult
                :resultRowData="resultRowData"
                :isLoading="isLoading" 
                :isModalOpen="isModalOpen"
                @openStatusModal="openStatusModal"
              />

     
            </div>
        </div>
      </div>

      
    </div>
  </va-inner-loading>
  </div>
</template>

<script>
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { dateFormat, NOTIFICATION_DURATION } from '../../utils/utilities'
import {  NO_RESULT_FOUND_MESSAGE} from '@/utils/messages'
import { notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
const TABS = [ { title: '検索', name: 'searchCondition', color: 'success' }]
export default {
    components:{
        searchCondition: SearchCondition, // eslint-disable-line
        statusModal: StatusModal,
    },
    data: () => ({
        resultRowData:[], 
        tabs: TABS,  
        currentView: 'searchCondition',
        initData: {},  // メモ：コンボボックスのため記述
        searchCondition: {},  // メモ：コンボボックスのため記述
        isLoading: false,
        countRecord :0,
        isModalOpen: true,
        cwbNo:''
    }),
    computed:{
        currentTab () {
            return this.tabs.find(({ title }) => title === this.tabValue)
        }
    },
    
    // メモ：コンボボックスのため記述
    mounted() {
      this.setDefaultValue()
    },
    methods:{
      changeLoading() {
            this.isLoading = !this.isLoading
        },
        formatDate(value) {
           return moment(value).format('yyyy-MM-DD')
        },
      setDefaultValue() {
        this.changeLoading(); 
       
       // for dropdowns api
       this.axios({ 
          url: 'api/oaiw005/dropDowns',
          method: 'get',
        })
        .then((res) => {
                    this.initData = res.data;
                    const arrayObjectDate = [
                      res.data.cargoName ,  // 蔵置場
                      res.data.cargoStatus,         // 借物STS
                      res.data.inClassifyClassName,  // 仕分条件
                      res.data.permitClassCdD,        // 許可区分
                    ]
                    this.initData.objectDate = arrayObjectDate;
                })
                .catch((error) => {
                    notificationError('画面初期処理でエラーが発生しました。', error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
      },
      // search Condition parameters
        searchImportData(searchCondition) {      
            this.changeLoading()
              const {       
                  searchOption,
                  // 蔵置場
                  // // 到着便名1
                  currentArrFlt1,
                  // // 到着便名2
                  currentArrFlt2,                
                  // // オリジン
                  orgin,
                  // // MAWB番号
                  awbNo,
                  // // HAWBNo
                  cwbNo,
                  // INV HAWB件数
                  invHwbNo,
                  // // INV 個数 マッチ
                  invCargoPiece,
                  // // INV アンマッチ
                  invMatch,
                  // // ロケーション1(要確認)
                  locateFrom,
                  // // ロケーション2(要確認)
                  locateTo,
                  // // 申告区分
                  permitNormal,
                  // // 申告区分
                  permitMatch,
              } = searchCondition
              
            this.searchCondition = {
                  targetStartDate: dateFormat(searchCondition.targetStartDate, 'yyyy-MM-DD'),
                  targetEndDate: dateFormat(searchCondition.targetEndDate, 'yyyy-MM-DD'),
                  searchOption,
                  // 蔵置場
                  cargoName: searchCondition.cargoName ? searchCondition.cargoName.substring(0, 6).trim() : '',
                  // // 到着便名1
                  currentArrFlt1,
                  // // 到着便名2
                  currentArrFlt2,                
                  // // オリジン
                  orgin,
                  // // MAWB番号
                  awbNo,
                  // // HAWBNo
                  cwbNo,
                  // // 借物STS
                  cargoStatus: searchCondition.cargoStatus ? searchCondition.cargoStatus.substring(0, 7).trim() : '',
                  // // 仕分条件
                  inClassifyClassName: searchCondition.inClassifyClassName ? searchCondition.inClassifyClassName.substring(5).trim() : '',
                  // // INV HAWB件数
                  invHwbNo,
                  // // INV 個数 マッチ
                  invCargoPiece,
                  // // INV アンマッチ
                  invMatch,
                  // // ロケーション1(要確認)
                  locateFrom,
                  // // ロケーション2(要確認)
                  locateTo,
                  // // 申告区分
                  permitNormal,
                  // // 申告区分
                  permitMatch,
                  // // 許可区分
                  permitClassCd: searchCondition.permitClassCd ? searchCondition.permitClassCd.substring(0, 2).trim() : '',
            }

          // search api sending
          this.axios({
              url: '/api/oaiw005/search',
              method: 'post',
              data: this.searchCondition
            }).then((res) => {
              console.log(res)
              console.log(this.searchCondition)
              this.resultRowData = res.data;
              if (Number(this.resultRowData) === 0) notificationWarn(NO_RESULT_FOUND_MESSAGE, '', NOTIFICATION_DURATION)


            }).catch((error) => {
              notificationError('検索処理でエラーが発生しました。');
              console.log(error);
            }).finally(() => {
              this.changeLoading();
            });
    },
        openStatusModal() {
          this.isModalOpen = true;
        },
        closeStatusModal() {
          this.isModalOpen = false;
        }
    }
}

</script>

<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #2b89e7;
}
.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #337ecc
}
</style>