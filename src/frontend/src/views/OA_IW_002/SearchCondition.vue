<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex md2">
          <span>登録年月日</span>
          <Datepicker v-model="schCondition.carryOutDate"
          :auto-apply="true"
          @closed="carryOutDatePickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          :disabled="isLoading"
          />
      </div>
      <div class="item flex xs2 ">
          <span style="padding: 0.3rem; width: fit-content;">蔵置場所</span>
          <va-input style="width: 150px;" :disabled="isLoading" v-model="schCondition.bondedWarehouseCd" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">到着便名</span>
          <va-input style="width: 110px;" :disabled="isLoading" v-model="schCondition.arrFtl1" />
          <span style="padding: 0.3rem;">/</span>
          <va-input style="width: 110px;" :disabled="isLoading" v-model="schCondition.arrFtl2" />
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">ORIGIN</span>
          <va-input style="width: 150px;" :disabled="isLoading" v-model="schCondition.origin" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">MAWB No.</span>
          <va-select style="width: 260px;"
          v-model="schCondition.awbNo"
          :options="awbNoList"
          placeholder="--Select MAWB No.--"
          ref="awbNoSelect"
          :disabled="isLoading"/>
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">アンマッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchHawbCntFlg" true-inner-label="HAWB件数" false-inner-label="HAWB件数" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchPieceFlg" true-inner-label="個数" false-inner-label="個数" size="large"/>
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem; ">マッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.matchFlg" true-inner-label="ON" false-inner-label="OFF" size="large"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem; ">許可なしMAWB</span>
          <va-switch :disabled="isLoading" v-model="schCondition.noPermitAwbHiddenFlg" true-inner-label="非表示" false-inner-label="非表示" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.noPermitAwbDisplayFlg" true-inner-label="表示" false-inner-label="表示" size="large"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="searchBondoutData(schCondition)"> 対象表示 </va-button>
      </div>
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="clearResult()"> クリア </va-button>
      </div>
   </div>
  </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import loginStore from '../../store/OC_CS_003/loginUserInfoStore'

export default {
  components: {    
    Datepicker, // eslint-disable-line
  },
  props: {        
    isLoading: Boolean,
    awbNoList: Array,
    searchBondoutData: Function,
    clearResult: Function,
  },
  data () {
    return {
      schCondition: {
        carryOutDate: moment().format('YYYY-MM-DD'),
        awbNo: '',
        arrFtl1: '',
        arrFtl2: '',
        bondedWarehouseCd: '',
        unMatchHawbCntFlg: false, // アンマッチ・HAWB件数
        unMatchPieceFlg: false, // アンマッチ・個数
        matchFlg: false, // マッチ
        noPermitAwbHiddenFlg: false, // 許可なしMAWB・非表示
        noPermitAwbDisplayFlg: false, // 許可なしMAWB・表示

      },
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  mounted() {    
    this.schCondition.bondedWarehouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
  },
  methods: {
    carryOutDatePickerClosedChange() {
      if (this.schCondition.carryOutDate) {
        this.schCondition.carryOutDate = moment(this.schCondition.carryOutDate).format('YYYY-MM-DD')
      }
    }, clearAwbNoSelect () {
      this.$refs.awbNoSelect.reset()
    }
  }
}
</script>

<style>
</style>
