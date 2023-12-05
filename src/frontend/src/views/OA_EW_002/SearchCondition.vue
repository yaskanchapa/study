<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">蔵置場所</span>
          <va-select style="width: 260px;"
          v-model="schCondition.bondedWarehouseCd"
          :options="bondedWarehouseCdList"
          :value-by="(option) => option.value"
          placeholder="--Select BondedWarehouseCd--"
          ref="bondedWarehouseCdSelect"
          :disabled="isLoading || isSearch"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">通関場所</span>
          <va-select style="width: 260px;"
          v-model="schCondition.customsPlace"
          :options="customsPlaceList"
          :value-by="(option) => option.value"
          placeholder="--Select customsPlace--"
          ref="customsPlaceSelect"
          :disabled="isLoading || isSearch"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">積込年月日</span>
          <Datepicker v-model="schCondition.containerDate"
          :auto-apply="true"
          @closed="containerDatePickerClosed"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          :disabled="isLoading || isSearch"
          />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">積込港</span>
          <va-select style="width: 260px;"
          v-model="schCondition.depPort"
          :options="depPortList"
          :value-by="(option) => option.value"
          placeholder="--Select depPort--"
          ref="depPortSelect"
          :disabled="isLoading || isSearch"/>
      </div>
   </div>
   <div class="row justify-start" style="padding: 0.75rem;">
    <div class="item flex xs3">
          <span style="padding: 0.3rem;">出発便&emsp;</span>
          <va-input style="width: 110px;" :disabled="isLoading || isSearch" v-model="schCondition.departureTruckNoStart" />
          <span style="padding: 0.3rem;">/</span>
          <va-input style="width: 110px;" :disabled="isLoading || isSearch" v-model="schCondition.departureTruckNoEnd" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">HT表示</span>
          <va-switch :disabled="isLoading || isSearch" v-model="schCondition.htDisplayFlg01" true-inner-label="表示" false-inner-label="表示" size="large"/>
          <va-switch :disabled="isLoading || isSearch" v-model="schCondition.htDisplayFlg02" true-inner-label="非表示" false-inner-label="非表示" size="large"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">ULD No.&emsp;&emsp;&emsp;</span>
          <va-input style="width: 250px;" :disabled="isLoading || isSearch" v-model="schCondition.uldNo" />
      </div>
      <div class="row justify-end" style="padding: 0.5rem;">
        <div class="item flex xs1">
          <va-button :disabled="isLoading || isSearch" color="info" size="medium" @click="onSearch()"> 対象表示 </va-button>
        </div>
        <div class="item flex xs1">
          <va-button :disabled="isLoading || !isSearch" color="info" size="medium" @click="clearAll()"> クリア </va-button>
        </div>
      </div>
    </div>
  </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
  components: {    
    Datepicker, // eslint-disable-line
  },
  props: {        
    isLoading: Boolean,
    isSearch: Boolean,
    changeSearch: Function,
    clearAll: Function,
    customsPlaceList: Array,
    bondedWarehouseCdList: Array,
    depPortList: Array,
    searchContainer: Function,
    fromParentSchCondition: Object,
    saveSearchCondigiton: Function
  },
  data () {
    return {
      schCondition: {
        bondedWarehouseCd: '', 
        customsPlace: '',
        containerDate: moment().format('YYYY-MM-DD'), // 積込年月日
        depPort: '',
        awbNo: '',
        departureTruckNoStart: '',
        departureTruckNoEnd: '',
        htDisplayFlg01: false,
        htDisplayFlg02: false,
        uldNo: ''
      },
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  beforeMount(){ // マウントされる際に、親コンポーネントが保持している検索条件をセットする。
    if(this.fromParentSchCondition) {
      // 前回の検索条件をセット
      this.schCondition = this.fromParentSchCondition
    }
  },
  beforeUnmount(){ // アンマウントされる際に、検索条件を親コンポーネントに保存する。
    this.saveSearchCondigiton(this.schCondition)
  },
  mounted() {
    // this.schCondition.bondedWarehouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
  },
  methods: {
    onSearch() {
      const validationChk = []
      if(!this.schCondition.bondedWarehouseCd) { validationChk.push("蔵置場所") }
      if(!this.schCondition.customsPlace) { validationChk.push("通関場所") }
      if(!this.schCondition.containerDate) { validationChk.push("積込年月日") }
      if(!this.schCondition.depPort) { validationChk.push("積込港") }
      if(validationChk.length > 0) {
        notificationWarn('以下は必須項目です。', _.join(validationChk, ', '))
        return
      }
      this.searchContainer(this.schCondition)
    },
    containerDatePickerClosed() {
      if (this.schCondition.containerDate) {
        this.schCondition.containerDate = moment(this.schCondition.containerDate).format('YYYY-MM-DD')
      }
    }, 
    clear () {
      // do nothing
    }
  }
}
</script>

<style>
</style>
