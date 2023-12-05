<script setup>
import SearchCondition from './SearchCondition.vue'
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
        <component :is="currentView"
          v-bind:awbNoList="awbNoList"
          v-bind:searchBondInData="searchBondInData"
          v-bind:clearResult="clearResult"
          v-bind:isLoading="isLoading"
          ref="schCondition">
        </component>
      </div>
      <!-- 検索結果 -->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">検索結果</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <SearchResult
          v-bind:resultRowData="resultRowData"
          v-bind:doRegist="doRegist"
          v-bind:onBatch="onBatch"
          v-bind:offBatch="offBatch"
          v-bind:batchClear="batchClear"
          v-bind:isLoading="isLoading"
          ref="schResult"
        />
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import _ from 'lodash' // eslint-disable-line
import { notificationSuccess, notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: '検索', name: 'searchCondition', color: 'success' }
]

export default {
  components: {
    searchCondition: SearchCondition // eslint-disable-line
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
  methods: {
    changeLoading () {
      this.isLoading = !this.isLoading
    },
    searchBondInData(schCondition) {
      this.changeLoading()
      this.fromChildSchCondition = schCondition
      if(!schCondition.arrPortDate) {
        notificationError('登録年月日は必須です。')
        this.changeLoading()
        return false
      }
      if(!schCondition.bondedWarehouseCd) {
        notificationError('蔵置場所は必須です。')
        this.changeLoading()
        return false
      }
      let _awbNoList = []
      this.axios.post('/api/oaiw001/search', schCondition)
      .then(res => {
          this.resultRowData = res.data
          this.resultRowData.forEach(rowData => {
            _awbNoList.push(rowData.awbNo)
          })
          // MAWB No.のリスト設定
          _awbNoList.push("") // 空白文字列セット
          _awbNoList = _awbNoList.concat(this.awbNoList)
          this.awbNoList = [...new Set(_awbNoList)].sort() // 重複削除、ソート
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
    doRegist(selectedRows) {
      const awbNoArr = []
      const paramList = []
      this.changeLoading()
      if(!selectedRows || selectedRows.length === 0) {
        notificationError('登録するデータを選択してください。')
        this.changeLoading()
        return
      }      
      selectedRows.forEach(param => {
        awbNoArr.push(`${param.awbNo}(${param.arrFtl1}/${param.arrFtl2})`)
        paramList.push({
          awbNo: param.awbNo,
          arrFtl1: param.arrFtl1,
          arrFtl2: param.arrFtl2,
          bondedWareClass: param.bondedWareClass,
          bondedWarehouseCd: null // サーバサイドでログインユーザのDEPARTMENTCDをセットする。
        })
      })

      this.axios.post('/api/oaiw001/regist', paramList)
      .then(res => {
        if(res.data.result) {
          notificationSuccess(res.data.message, `MAWBNO:${_.join(awbNoArr, ', ')}`)
        } else {
          notificationError(res.data.message, res.data.errorMessage)
        }        
      }).catch(error => {
        console.log(error)
        notificationError("登録処理でエラーが発生しました。", error.message)
      }).finally(() => {
        this.changeLoading()
      })

    },
    onBatch(param, id) { // 対象処理
      this.changeLoading()
      let ret = false
      const awbNo = `${param.awbNo}(${param.arrFtl1}/${param.arrFtl2})`
      this.axios.post('/api/oaiw001/bondedInSetTarget', {
        awbNo: param.awbNo,
        arrFtl1: param.arrFtl1,
        arrFtl2: param.arrFtl2,
        bondedWarehouseCd: this.fromChildSchCondition.bondedWarehouseCd
      })
      .then(res => {
        ret = res.data.result
        if(ret) {
          notificationSuccess(res.data.message, `MAWBNO:${awbNo}`)
        } else {
          notificationError(res.data.message, `${res.data.errorMessage}\nMAWBNO:${awbNo}`)
        }        
      }).catch(error => {
        console.log(error)
        notificationError("バッチ対象処理でエラーが発生しました。", `MAWBNO:${awbNo}\n${error.message}`)
      }).finally(() => {
        if(!ret) {
          // バッチ処理が失敗した場合、Switchを戻す
          this.$refs.schResult.setCarryInActionFlg(id, false)
        }
        this.changeLoading()
      })
    },
    offBatch(param, id) { // 対象外処理
      this.changeLoading()
      let ret = false
      const awbNo = `${param.awbNo}(${param.arrFtl1}/${param.arrFtl2})`
      this.axios.post('/api/oaiw001/bondedInUnsetTarget', {
        awbNo: param.awbNo,
        arrFtl1: param.arrFtl1,
        arrFtl2: param.arrFtl2,
        bondedWarehouseCd: this.fromChildSchCondition.bondedWarehouseCd
      })
      .then(res => {
        ret = res.data.result
        if(ret) {
          notificationSuccess(res.data.message, `MAWBNO:${awbNo}`)
        } else {
          notificationError(res.data.message, `${res.data.errorMessage}\nMAWBNO:${awbNo}`)
        }        
      }).catch(error => {
        notificationError("バッチ対象外処理でエラーが発生しました。", `MAWBNO:${awbNo}\n${error.message}`)
      }).finally(() => {
        if(!ret) {
          // バッチ処理が失敗した場合、Switchを戻す
          this.$refs.schResult.setCarryInActionFlg(id, true)
        }
        this.changeLoading()
      })
    }, async batchClear(params) {
      this.changeLoading()

      if (params.length === 0) {
        notificationWarn('バッチ対象データを選択してください。')
        this.changeLoading()
        return
      }

      const _offBatch = (postParam) => this.axios.post('/api/oaiw001/bondedInUnsetTarget', postParam) // バッチ対象外のPostApi
      const promiseArr = [] // axios.postの配列
      const idArr = [] // ag-gridのID
      const unSetFlgArr = [] // 既に対象外になっている行のAWBNO配列
      const successArr = [] // バッチ対象外処理OK
      const failureArr = [] // バッチ対象外処理NG

      _.forEach(params, function(param) {
        promiseArr.push(_offBatch(param.data))
        idArr.push(param.id)
        if(param.data.carryInActionFlg === false) {
          unSetFlgArr.push(`${param.data.awbNo}(${param.data.arrFtl})`)
        }
      })

      if(unSetFlgArr.length > 0) {
        notificationWarn('既に対象外になっているデータがあります。', `MAWBNO:${_.join(unSetFlgArr, ', ')}`)
        this.changeLoading()
        return
      }

      const results = await Promise.allSettled(promiseArr)
      console.log("results:", results)
      _.forEach(results, (result, idx) => {
        const id = idArr[idx]
        const param = params[idx]
        const awbNo = `${param.data.awbNo}(${param.data.arrFtl})`
        if (result.status === 'fulfilled') {
          if(result.value.data.result) {
            successArr.push(awbNo)
            this.$refs.schResult.setCarryInActionFlg(id, false) // set switch off
            notificationSuccess(result.value.data.message, `MAWBNO:${awbNo}`)
          } else {
            failureArr.push(awbNo)
            this.$refs.schResult.setCarryInActionFlg(id, true) // set switch on
            notificationError(result.value.data.message, `${result.value.data.errorMessage}\nMAWBNO:${awbNo}`)
          }          
        } 
        if (result.status === 'rejected') {          
          console.log(result)
          failureArr.push(awbNo)
          this.$refs.schResult.setCarryInActionFlg(id, true) // set switch on
          notificationError("バッチクリア処理が失敗しました。", `${result.reason.message}\nMAWBNO:${awbNo}`)
        }
      })
      this.changeLoading()
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
