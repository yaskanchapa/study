<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">MAWB</span>
        <va-input ref="awbNo" v-model="searchCondition.awbNo" style="width: 150px;"  @change="selectFltList" />
      </div>
      <div class="item flex xs2">
        <span style="padding: 0.3rem;">FLT</span>
        <va-select ref="currentArrFlt1List" style="width: 260px;" v-model="searchCondition.currentArrFlt1" :options="currentArrFlt1List"/>
      </div>
      <div class="item flex xs4">
        <span style="padding: 0.3rem 0px 0.3rem 0.3rem;width: min-content;">申告予定日</span>
        <Datepicker v-model="searchCondition.reportPlaningDate" :auto-apply="true" @closed="reportPlaningDatePickerClosedChange"
          :format="DatePickerFormat" :enable-time-picker="false" />
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">HAWB</span>
        <va-input ref="cwbNo" v-model="searchCondition.cwbNo" style="width: 150px;" />
      </div>
      <div class="item flex xs2">
        <span style="padding: 0.3rem;">申告区分</span>
        <va-switch ref="idaFlg" v-model="searchCondition.idaFlg" true-inner-label="一般" false-inner-label="一般"
          size="small" />
        <va-switch ref="micFlg" v-model="searchCondition.micFlg" true-inner-label="マニ" false-inner-label="マニ"
          size="small" />
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="searchImportData(searchCondition)"> 検索 </va-button>
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
    searchCondition: Object,
    currentArrFlt1List : Array,
    searchImportData: Function,
    clearResult: Function,
    selectFltList: Function
  },
  data() {
    return {
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  methods: {
    reportPlaningDatePickerClosedChange() {
      if (this.searchCondition.reportPlaningDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.searchCondition.reportPlaningDate = moment(this.searchCondition.reportPlaningDate).format('YYYY-MM-DD 00:00:00')
      }
    }
  }
}
</script>

<style>

</style>
