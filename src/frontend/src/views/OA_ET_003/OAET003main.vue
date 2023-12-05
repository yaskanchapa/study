<!-- eslint-disable indent -->
<script setup>
import SearchCondition from './SearchConditionMain.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1 text-white" align="left">検索条件</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <va-tabs v-model="currentView">
                <template #tabs>
                  <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                </template>
              </va-tabs>
              <component :is="currentView" v-bind:searchExpData="searchExpData"></component>
            </div>
        </div>
      </div>

      <!-- 検索結果 -->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1 text-white" align="left">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <SearchResult
              v-bind:resultRowData="resultRowData"
               />
            </div>
        </div>
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import _ from 'lodash' // eslint-disable-line
import {notificationSuccess,  notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: '検索', name: 'SearchCondition', color: 'success' }
]

export default {
  components: {
    SearchCondition: SearchCondition, // eslint-disable-line
  },
  data: () => ({
    resultRowData: [],
    tabs: TABS,
    currentView: 'SearchCondition',
    isLoading: false,
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods: {
    changeLoading() {
      this.isLoading = !this.isLoading;
    },
    showComponenet() {
      // eslint-disable-next-line eqeqeq
      if (this.hidCompM == '+') {
        this.hidCom = true;
        this.hidCompM = '-';
      } else {
        this.hidCom = false;
        this.hidCompM = '+';
      }
    },
    searchExpData(expsearchCondition) {
      console.log('expsearchCondition', expsearchCondition);
      this.changeLoading();
      this.axios({
        url: '/api/oaet003/search',
        method: 'get',
        params: expsearchCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {         
          console.log(res.data)
          if (res.data[0] === "Error") {
            notificationError(res.data[1]);
           this.resultRowData = []
          }else{
          this.resultRowData = res.data
          notificationSuccess("検索が完了しました")
          }
        }).catch(error => {
        console.log('error' + error)
        notificationError("Error 管理者にお問い合わせください : "+ error);
      }).finally(() => {
        this.changeLoading();
        console.log('finally')
      })
    },
        learResult() {
      this.resultRowData = []
    },
}
}
</script>
<style>
.bar-color1 {
    --tw-bg-opacity: 1;
    background-color: #2b89e7;
}
</style>
