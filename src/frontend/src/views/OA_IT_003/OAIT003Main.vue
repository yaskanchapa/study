<script setup>
import SearchConditionMain from './SearchConditionMain.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="grid md:grid-cols-1 sm:grid-cols-1">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索条件</div>
          <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
            <va-tabs v-model="currentView">
              <template #tabs>
                <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
              </template>
            </va-tabs>
            <component :is="currentView" v-bind:searchImpData="searchImpData"></component>
        </div>
      </div>

      <!-- 検索結果 -->
      <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
        <div>
          <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white" align="left">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
              <SearchResult 
              v-bind:resultRowData="resultRowData"
              v-bind:isLoading="isLoading" 
              v-bind:changeLoading="changeLoading"
              />
            </div>
        </div>
      </div>
    </div>
    </va-inner-loading>
  </div>
</template>

<script>
import {notificationError} from "@/components/Notification/NotificationApi";

const TABS = [
  { title: '検索', name: 'searchCondition', color: 'success' }
]
export default {
  components: {
    searchCondition: SearchConditionMain, // eslint-disable-line
  },
  data: () => ({
    resultRowData:[],
    tabs: TABS,
    currentView: 'searchCondition',
    isLoading: false,
    inHouseListData: SearchConditionMain
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods:{
    changeLoading() {
      this.isLoading = !this.isLoading;
    },
    searchImpData(schCondition){
      console.log('schCondition', schCondition);
      this.changeLoading();
      this.axios({
        url: '/api/oait003/search',
        method: 'get',
        params: schCondition,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          console.log(res.data)
          if (res.data[0] === "Error") {
            notificationError(res.data[1]);
            this.resultRowData = [];
          } else {
            this.resultRowData = res.data;
          }
        }).catch(error => {
        console.log('error' + error)
        notificationError("Error 管理者にお問い合わせください : "+ error);
      }).finally(() => {
        this.changeLoading();
        console.log('finally')
      })
    },
    clearResult() {
      this.rowData = []
    },
    onOffBatch(param) {
      console.log(param)
    }
  }
}

</script>
<style>
.bar-color1-blue {
  --tw-bg-opacity: 1;
  background-color: #4278EB;
}
.bar-color2-blue {
  --tw-bg-opacity: 1;
  background-color: #4278EB;
}
</style>
