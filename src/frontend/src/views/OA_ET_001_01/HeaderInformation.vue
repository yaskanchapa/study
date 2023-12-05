<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs5">
        <span style="padding: 0.3rem;">申告予定年月日</span>
        <Datepicker v-model="resultHeaderData.expReportPlanDate" :auto-apply="true"
          @closed="expReportPlanDatePickerClosedChange" :format="DatePickerFormat" :enable-time-picker="false" />
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">通関場所</span>
        <va-select  style="width: 260px;" v-model="resultHeaderData.customsPlaceCd" :options="customsList"
          :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :value-by="(option) => option.VALUE"
          placeholder="--Please choose an option--" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">代理店</span>
        <va-select  style="width: 260px;" v-model="resultHeaderData.inHouseRefNumber" :options="agencyList"
          :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :value-by="(option) => option.VALUE"
          placeholder="--Please choose an option--" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs5">
        <span style="padding: 0.3rem;">搬入予定年月日</span>
        <Datepicker v-model="resultHeaderData.bonWareHoCurDate" :auto-apply="true"
          @closed="bonWareHoCurDatePickerClosedChange" :format="DatePickerFormat" :enable-time-picker="false" 
          :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">蔵置場所</span>
        <va-select style="width: 260px;" v-model="resultHeaderData.headBondedWarehouseCd" 
          :options="bonList"
          :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :value-by="(option) => option.VALUE"
          placeholder="--Please choose an option--" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
      
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs4">
        <span style="padding: 0.3rem;">申告条件</span>
        <va-input maxlength="1" v-model="resultHeaderData.headReportCondition" style="width: 150px;" placeholder="Text Input" 
          :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">積込港</span>
        <va-select style="width: 260px;" v-model="resultHeaderData.depPort" :options="depList"
          :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :value-by="(option) => option.VALUE"
          placeholder="--Please choose an option--" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs4">
        <span style="padding: 0.3rem;">積載便名</span>
        <va-input v-model="resultHeaderData.loadingPlanFlt1" style="width: 150px;" placeholder="Text Input" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
        <span style="padding: 0.3rem;"> / </span>
        <va-input v-model="resultHeaderData.loadingPlanFlt2" style="width: 150px;" placeholder="Text Input" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">仕向地</span>
        <va-input v-model="resultHeaderData.fltDestination" style="width: 150px;" placeholder="Text Input"  :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="editHeaderData()"> 登録 </va-button>
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
    resultHeaderData: Object,
    clearResult: Function,
    editHeaderData: Function,
    customsList: Array,
    bonList: Array,
    depList: Array,
    agencyList: Array
  },
  data() {
    return {
      headerData: this.resultHeaderData,
      split: 'スプリット',
      DatePickerFormat: 'yyyy-MM-dd',
      disabledFlg:  0,
      disabledFlg2: 0,
      disabledFlg3: 1
    }
  },
  methods: {
    expReportPlanDatePickerClosedChange() {
      if (this.resultHeaderData.expReportPlanDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.resultHeaderData.expReportPlanDate = moment(this.resultHeaderData.expReportPlanDate).format('YYYY-MM-DD')
      }
    },
    bonWareHoCurDatePickerClosedChange() {
      if (this.resultHeaderData.bonWareHoCurDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.resultHeaderData.bonWareHoCurDate = moment(this.resultHeaderData.bonWareHoCurDate).format('YYYY-MM-DD')
      }
    },
  }
}
</script>

<style>

</style>
