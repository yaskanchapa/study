<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">搬入伝票番号</span>
          <va-input style="width: 230px;" v-model="schCondition.bondInBillNo" placeholder="" />
      </div>
      <div class="item flex xs3">
          <span>対査日</span>
          <Datepicker v-model="schCondition.arrivalDate"
          :auto-apply="true"
          @closed="arrivalDatePickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          style="width: 200px;"/>
          <!-- <va-date-input placeholder="Text Input" style="width: 130px;"/> -->
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem; padding-left: 38px;">搬伝発行</span>
          <va-switch v-model="schCondition.bondInBillNotEnd" true-inner-label="未" false-inner-label="未" size="small"/>
          <va-switch v-model="schCondition.bondInBillEnd" true-inner-label="済" false-inner-label="済" size="small"/>
      </div>
      <div class="item flex xs4">
          <span style="padding: 0.3rem;">HAWB No.</span>
          <va-input style="width: 400px;" v-model="schCondition.cwbNo" placeholder="" />
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">MAWB No.</span>
          <va-input style="width: 200px;" v-model="schCondition.awbNo" placeholder="" />
      </div>
      <div class="item flex xs3">
          <span>搬伝発行日</span>
          <Datepicker v-model="schCondition.bondInBillIssueDate"
          :auto-apply="true"
          @closed="bondInBillIssueDatePickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          />
          <!-- <va-date-input placeholder="Text Input" style="width: 130px;"/> -->
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem; padding-left: 38px;">BIL伝票</span>
          <va-switch  v-model="schCondition.bilMakeNotEnd" true-inner-label="未" false-inner-label="未" size="small" style="margin-left: 10px;"/>
          <va-switch  v-model="schCondition.bilMakeEnd" true-inner-label="済" false-inner-label="済" size="small"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium" :disabled="isLoading" @click="searchBondInData(schCondition)"> 対象表示 </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium" :disabled="isLoading" > クリア </va-button>
      </div>
   </div>

  </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line


export default {
  components: {
    Datepicker, // eslint-disable-line
  },
  props: {
    searchBondInData: Function,
    createBilFlag: Boolean,
    isLoading: Boolean,

  },
  data () {
    return {
      schCondition:{
        bondInBillNo:'', 
        arrivalDate: moment().format('YYYY-MM-DD'),   
        cwbNo:'',         
        awbNo: '',          
        bondInBillEnd: false,
        bondInBillNotEnd: true,
        bilMakeEnd: false,
        bilMakeNotEnd: false,
        bondInBillIssueDate: '',
      },
      labelNotEnd: '未',
      labelEnd: '済',
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },

  methods: {
    arrivalDatePickerClosedChange() {
      if (this.schCondition.arrivalDate) {
        this.schCondition.arrivalDate = moment (this.schCondition.arrivalDate).format('YYYY-MM-DD')
      }
    },
    bondInBillIssueDatePickerClosedChange(){
      if (this.schCondition.bondInBillIssueDate) {
        this.schCondition.bondInBillIssueDate = moment (this.schCondition.bondInBillIssueDate).format('YYYY-MM-DD')
      }
    }

  }
}
</script>

<style>
</style>
