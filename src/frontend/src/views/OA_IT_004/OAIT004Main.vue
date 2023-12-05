<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import InsertList from './InsertList.vue'
</script>

<template>
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">通関書類状況登録</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <va-tabs v-model="currentView">
          <template #tabs>
            <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
          </template>
        </va-tabs>
        <component :is="currentView" 
          v-bind:traderList="traderList" 
          v-bind:registerList="registerList"
          v-bind:conditionList="conditionList"
          v-bind:insertCondition="insertCondition"
          v-bind:getOneShotDefVal="getOneShotDefVal"
          v-bind:insertOneShot="insertOneShot"
          v-bind:deleteOneShot="deleteOneShot"></component>
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'

const TABS = [
  { title: '一括登録', name: 'insertList', color: 'success' }
]
export default {
  components: {
    insertList: InsertList, // eslint-disable-line
  },
  data: () => ({
    resultRowData:[],
    tabs: TABS,
    currentView: 'insertList',
    isLoading: false,
    traderList: [],
    registerList: [],
    conditionList: [],
    insertCondition: {
      trader: '',
      register: '',
      condition: '',
      customsCd: '',
      comment:'',
      hawbList: [
        {
          cwbNo: '',
          deptCd: ''
        }
      ]
    }
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  mounted() {
    this.getOneShotDefVal()
  },
  methods:{
    changeLoading() {
      this.isLoading = !this.isLoading;
    },
    getOneShotDefVal() { 
      this.changeLoading()

      const departmentCd = loginStore.getters.getAuthPermission.departmentCd
      this.insertCondition.customsCd = departmentCd

      this.axios.post('/api/oait004/searchOneShotDefList')
        .then(res => {
          if (res.data.result[0].errFlg === 'false') {
            this.traderList = res.data.traderList
            this.registerList = res.data.registerList
            this.conditionList = res.data.conditionList
          } else { 
            notificationError(res.data.result.message)
          }
          
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    insertOneShot() { 
      this.changeLoading()
      console.log(this.insertCondition)
      this.axios.post('/api/oait004/insertOneShot', this.insertCondition)
        .then(res => {
          if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
          } else if (res.data.errFlg === 'warning') {
            notificationWarn(res.data.message)
          } else { 
            notificationSuccess(res.data.message)
          }
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    deleteOneShot() {
      this.changeLoading()
      console.log(this.insertCondition)
      this.axios.post('/api/oait004/deleteOneShot', this.insertCondition)
        .then(res => {
          if (res.data.errFlg === 'error') {
            notificationError(res.data.message)
          } else if (res.data.errFlg === 'warning') {
            notificationWarn(res.data.message)
          } else {
            notificationSuccess(res.data.message)
          }
        })
        .catch(error => {
          notificationError("処理でエラーが発生しました。", error.message)
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
</style>
