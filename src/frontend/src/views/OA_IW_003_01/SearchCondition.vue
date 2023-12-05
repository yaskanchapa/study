<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex md2">
          <span style="width: 120px; padding: 0.3rem;">作業日</span>
          <Datepicker v-model="schCondition.workingDays"
          :auto-apply="true"
          @closed="workingDaysPickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          :disabled="isLoading"
          />
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">蔵置場所</span>
          <va-input style="width: 150px;" :disabled="isLoading" v-model="schCondition.bondedWareHouseCd" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">到着便名</span>
          <va-input style="width: 110px;" :disabled="isLoading" v-model="schCondition.arrFlt1" />
          <span style="padding: 0.3rem;">/</span>
          <va-input style="width: 110px;" :disabled="isLoading" v-model="schCondition.arrFlt2" />
      </div>
      <div class="item flex xs5">
          <span style="padding: 0.3rem;">MAWB No.</span>
          <va-input style="width: 400px;" :disabled="isLoading" v-model="schCondition.awbNo" />
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs10">
          <span style="padding: 0.5rem; padding-left: 5px; width: 60px;">終了分</span>
          <va-switch :disabled="isLoading" v-model="schCondition.endDisplay" true-inner-label="表示" false-inner-label="表示" size="large"/>
          <span style="padding: 0.5rem; padding-left: 70px; width: 120px;">対象</span>
          <va-switch :disabled="isLoading" v-model="schCondition.objectAll" true-inner-label="全件" false-inner-label="全件" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.objectAWB" true-inner-label="MAWB指定" false-inner-label="MAWB指定" size="large"/>
          <span style="padding: 0.5rem; padding-left: 70px; width: 150px;">アンマッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchOver" true-inner-label="オーバー" false-inner-label="オーバー" size="large"/>
          <va-switch :disabled="isLoading" v-model="schCondition.unMatchShort" true-inner-label="ショート" false-inner-label="ショート" size="large"/>
          <span style="padding: 0.5rem; padding-left: 70px; width: 100px;">マッチ</span>
          <va-switch :disabled="isLoading" v-model="schCondition.match" true-inner-label="マッチ" false-inner-label="マッチ" size="large"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="searchBondInData(schCondition)"> 検索 </va-button>
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
    searchBondInData: Function,
  },
  data () {
    return {
      schCondition: {
        workingDays: moment().format('YYYY-MM-DD'),
        bondedWareHouseCd: '',
        arrFlt1: '',
        arrFlt2: '',
        endDisplay: false, // 終了分表示
        objectAll: true, // 対象(全件)
        objectAWB: true,// 対象(MAWB指定)
        unMatchOver: true, // アンマッチ(オーバー)
        unMatchShort: true,// アンマッチ(ショート)
        match: true// マッチ
      },
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  mounted() {    
    this.schCondition.bondedWareHouseCd = _.cloneDeep(loginStore.getters.getAuthPermission.departmentCd)
  },
  methods: {
    workingDaysPickerClosedChange() {
      if (this.schCondition.workingDays) {
        this.schCondition.workingDays = moment(this.schCondition.workingDays).format('YYYY-MM-DD')
      }
    }, 
  }
}
</script>

<style>
</style>
