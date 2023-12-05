<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex md2">
          <span>登録年月日</span>
          <Datepicker v-model="schCondition.arrPortDate"
          :auto-apply="true"
          @closed="arrPortDatePickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          :disabled="isLoading"
          />
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">蔵置場所</span>
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
      <div class="item flex xs7">
          <span style="padding: 0.3rem; padding-left: 38px;">バッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.batchTargetFlg" true-inner-label="対象" false-inner-label="対象" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.batchNotTargetFlg" true-inner-label="対象外" false-inner-label="対象外" size="large"/>
          <span style="padding: 0.3rem;">アンマッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchHawbCntFlg" true-inner-label="HAWB件数" false-inner-label="HAWB件数" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchPieceFlg" true-inner-label="個数" false-inner-label="個数" size="large"/>
          <span style="padding: 0.3rem; ">マッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.matchFlg" true-inner-label="ON" false-inner-label="OFF" size="large"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="searchBondInData(schCondition)"> 対象表示(V) </va-button>
      </div>
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="clearResult()"> クリア(C) </va-button>
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
    searchBondInData: Function,
    clearResult: Function,
  },
  data () {
    return {
      schCondition: {
        arrPortDate: moment().format('YYYY-MM-DD'),
        awbNo: '',
        arrFtl1: '',
        arrFtl2: '',
        bondedWarehouseCd: '',
        batchTargetFlg: false, // バッチ・対象
        batchNotTargetFlg: false, // バッチ・対象外
        unMatchHawbCntFlg: false, // アンマッチ・HAWB件数
        unMatchPieceFlg: false, // アンマッチ・個数
        matchFlg: false // マッチ
      },
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  mounted() {    
    this.schCondition.bondedWarehouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
  },
  methods: {
    arrPortDatePickerClosedChange() {
      if (this.schCondition.arrPortDate) {
        this.schCondition.arrPortDate = moment(this.schCondition.arrPortDate).format('YYYY-MM-DD')
      }
    }, clearAwbNoSelect () {
      this.$refs.awbNoSelect.reset()
    }
  }
}
</script>

<style>
</style>
