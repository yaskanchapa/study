<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
        <div class="row justify-start" style="padding: 0.75rem;">
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">MAWB</span>
            <va-input style="width: 150px;" v-model="searchCondition.awbNo" placeholder="Text Input"/>
          </div>
          <!-- <div class="item flex xs2">
            <va-input style="width: 150px;" v-model="searchCondition.awbNo" placeholder="Text Input"/>
          </div> -->
        </div>
        <div class="row justify-start" style="padding: 0.75rem;">
            <div class="item flex xs3">
              <span style="padding: 0.3rem;">トラック便日付</span>
              <Datepicker  v-model="searchCondition.truckNoDate" :auto-apply="true" @closed="truckNoDatePickerClosedChange"
              :format="DatePickerFormat" :enable-time-picker="false" />
            </div>
            <div class="item flex xs2">
              <span style="padding: 0.3rem;">トラック便</span>
              <va-input v-model="searchCondition.truckNoFrom" style="width: 10px;text-align: center;"  />
              <span style="padding: 0.3rem;text-align: center;">~</span>
              <va-input v-model="searchCondition.truckNoTo" style="width: 10px;text-align: center;" />
            </div>
          </div>
        <div class="row justify-start" style="padding: 0.75rem;">
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.cdbFlg" :label="cdbLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.mecFlg" :label="mecLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.edaFlg" :label="edaLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.edcFlg" :label="edcLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.remakeFlg" :label="remakeLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs2">
            <va-button color="info" size="medium" @click="writeReport"> 作成 </va-button>
          </div>
        </div>
        <va-divider />
        <div>
          <div class="row justify-start">
            <div class="item flex xs12">
              <span style="padding: 0.3rem;">電文作成結果</span>
            </div>
          </div>
          <div class="row justifiy-start" style="padding: 0.3rem">
            <div class="item flex xs1">
                <span style="padding: 0.3rem;text-align: right;">CDB</span>
              </div>
              <div class="item flex xs1" >
                <va-input v-model="cntReportResult.cntCDB" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
                <span style="padding: 0.3rem;">件</span>
              </div>
              <div class="item flex xs2">
                <va-input v-model="cntReportResult.cntFileCDB" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
                <span style="padding: 0.3rem;">ファイル</span>
              </div>
            <div class="item flex xs1">
              <span style="padding: 0.3rem;text-align: right;">MEC</span>
            </div>
            <div class="item flex xs1" >
              <va-input v-model="cntReportResult.cntMEC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">件</span>
            </div>
            <div class="item flex xs2">
              <va-input v-model="cntReportResult.cntFileMEC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">ファイル</span>
            </div>
          </div>
        <div class="row justifiy-start" style="padding: 0.3rem">
          <div class="item flex xs1">
            <span style="padding: 0.3rem;text-align: right;">EDA</span>
          </div>
          <div class="item flex xs1">
            <va-input v-model="cntReportResult.cntEDA" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">件</span>
          </div>
          <div class="item flex xs2">
            <va-input v-model="cntReportResult.cntFileEDA" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">ファイル</span>
          </div>
          <div class="item flex xs1">
            <span style="padding: 0.3rem;text-align: right;">EDC</span>
          </div>
          <div class="item flex xs1">
            <va-input v-model="cntReportResult.cntEDC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">件</span>
          </div>
          <div class="item flex xs2">
            <va-input v-model="cntReportResult.cntFileEDC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">ファイル</span>
          </div>
        </div>
      </div>
  </va-card>
</template>
<script>
import Datepicker from '@vuepic/vue-datepicker'

export default {
  components: {
    Datepicker, // eslint-disable-line
  },
  props: {
    writeReport: Function,
    searchCondition: Object,
    currentArrFlt1List: Array,
    searchImportData: Function,
    clearResult: Function,
    selectFltList: Function,
    cntReportResult: Object
  },
  data() {
    return {
      DatePickerFormat: 'yyyy-MM-dd',
      cdbLabel: 'CDB',
      mecLabel: 'MEC',
      edaLabel: 'EDA',
      edcLabel: 'EDC',
      remakeLabel: '再作成',
    }
  },
  methods: {
    truckNoDatePickerClosedChange() {
      if (this.searchCondition.truckNoDate) {
        // eslint-disable-next-line vue/no-mutating-props, no-undef
        this.searchCondition.truckNoDate = moment(this.searchCondition.truckNoDate).format('YYYY-MM-DD 00:00:00')
        this.getAwbNoList()
      }
    }

  }
}
</script>

<style>
span {
  max-width: fit-content;
  line-height: 2;
}
</style>
