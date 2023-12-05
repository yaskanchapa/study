<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
    <div class="flex w-full">
      <div class="grid flex-grow card place-items-left" style="border-radius: revert; width: 60%;">
        <div class="row justify-start">
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">変更後MAWB</span>
            <va-input v-model="resultHeaderData.awbNo" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">申告予定日</span>
            <Datepicker v-model="resultHeaderData.reportPlaningDate" :auto-apply="true"
              @closed="reportPlaningDatePickerClosedChange" :format="DatePickerFormat" :enable-time-picker="false" style="width:100px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">代理店</span>
            <va-select ref="currentArrFlt1List" style="width: 10px;" v-model="resultHeaderData.inHouseRefNumber"
              :options="agencyList" :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :track-by="(option) => option.VALUE + ' : ' + option.OPTION" :value-by="(option) => option.VALUE"
              highlight-matched-text />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">依頼元混載業者</span>
            <va-input v-model="resultHeaderData.reqMixedForwarder" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">税関官署</span>
            <va-input v-model="resultHeaderData.customsDiv" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">到着港</span>
            <va-input v-model="resultHeaderData.arrAirportCd" style="width: 10px;" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">積載便名</span>
            <va-input v-model="resultHeaderData.arrFlt1" style="width: 10px;" />
            <span style="padding: 0.3rem;"> / </span>
            <va-input v-model="resultHeaderData.arrFlt2" style="width: 10px;" />
          </div>
          <div class="item flex xs2">
            <span style="padding: 0.3rem;">申告条件</span>
            <va-input v-model="resultHeaderData.reportCondition" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">取卸港</span>
            <va-input v-model="resultHeaderData.getPort" style="width: 10px;" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">到着年月日</span>
            <Datepicker v-model="resultHeaderData.arrDate" :auto-apply="true" @closed="arrDatePickerClosedChange"
              :format="DatePickerFormat" :enable-time-picker="false" style="width:100px;"  />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">提出先</span>
            <va-input v-model="resultHeaderData.present" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">着地港</span>
            <va-input v-model="resultHeaderData.airportCd" style="width: 10px;" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">入港年月日</span>
            <Datepicker v-model="resultHeaderData.arrPortDate" :auto-apply="true" @closed="arrPortDatePickerClosedChange"
              :format="DatePickerFormat" :enable-time-picker="false" style="width:100px;"  />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">孫混載</span>
            <va-input v-model="resultHeaderData.grandChildMixed" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">ジョイント</span>
            <va-input v-model="resultHeaderData.joint" style="width: 10px;" />

          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">仕出地/積出地</span>
            <va-input v-model="resultHeaderData.catereringPlace" style="width: 10px;" />
            <span style="padding: 0.3rem;"> / </span>
            <va-input v-model="resultHeaderData.shipmentCd" style="width: 10px;" />
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">蔵置場所</span>
            <va-input v-model="resultHeaderData.clearPlanPlaceCd" style="width: 10px;" />
          </div>
          <div class="item flex xs2">
            <va-checkbox style="padding: 0.3rem;" v-model="resultHeaderData.split" :label="split" left-label />
          </div>
        </div>
        <div class="row justify-end">
          <div class="item flex xs2">
            <va-button color="info" size="medium" @click="editHeaderData()"> 登録 </va-button>
          </div>
        </div>
      </div>

      <div class="grid flex-grow card place-items-left" style="border-radius: revert; width: 10%;">
        <div class="row justify-start">
          <div class="item flex xs12">
            全体
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs12">
            <span style="padding: 0.3rem;">HAWB</span>
            <va-input v-model="resultHeaderData.cwbCount" style="width: 10px;" :disabled="true" input-class="text-right" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs12">
            <span style="padding: 0.3rem;">個数</span>
            <va-input v-model="resultHeaderData.sumCwbPiece" style="width: 10px;" :disabled="true" input-class="text-right" />
          </div>
        </div>
        <div class="row justify-start">
          <div class="item flex xs12">
            <span style="padding: 0.3rem;">重量</span>
            <va-input v-model="resultHeaderData.sumCwbWeight" style="width: 10px;" :disabled="true" input-class="text-right" />
          </div>
        </div>
      </div>


      <div class="grid flex-grow card place-items-left" style="border-radius: revert; width: 30%;">
        <div class="row justify-start">
          <div class="item flex xs12">
            件数
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">

          </div>
          <div class="item flex xs3 text-right" >
            <span>IDA</span>
          </div>
          <div class="item flex xs3">
            <span>MIC</span>
          </div>
          <div class="item flex xs3">
            <span>TOTAL</span>
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">チェック済</span>
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.checkedIda" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.checkedMic" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.checkedTotal" style="width: 10px;" :disabled="true" />
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">未チェック</span>
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.unCheckedIda" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.unCheckedMic" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.unCheckedTotal" style="width: 10px;" :disabled="true" />
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">TOTAL</span>
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.totalIda" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.totalMic" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.totalTotal" style="width: 10px;" :disabled="true" />
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">登録済</span>
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.registerIda" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.registerMic" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.registerTotal" style="width: 10px;" :disabled="true" />
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">進捗(%)</span>
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.progressIda" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.progressMic" style="width: 10px;" :disabled="true" />
          </div>
          <div class="item flex xs3">
            <va-input v-model="resultHeaderData.progressTotal" style="width: 10px;" :disabled="true" />
          </div>
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


export default {
  components: {
    Datepicker, // eslint-disable-line
  },
  props: {
    resultHeaderData: Object,
    clearResult: Function,
    editHeaderData: Function,
    agencyList: Array,
    setDefaultValue: Function
  },
  data() {
    return {
      headerData: this.resultHeaderData,
      split: 'スプリット',
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  mounted() {
    this.checkDefaultValue()
  },
  methods: {
    checkDefaultValue() {
      if (this.agencyList.length === 0) {
        // eslint-disable-next-line no-unused-expressions
        this.setDefaultValue
      }
    },
    arrPortDatePickerClosedChange() {
      if (this.resultHeaderData.arrPortDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.resultHeaderData.arrPortDate = moment(this.resultHeaderData.arrPortDate).format('YYYY-MM-DD 0:00:00')
      }
    },
    reportPlaningDatePickerClosedChange() {
      if (this.resultHeaderData.reportPlaningDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.resultHeaderData.reportPlaningDate = moment(this.resultHeaderData.reportPlaningDate).format('YYYY-MM-DD 0:00:00')
      }
    },
    arrDatePickerClosedChange() {
      if (this.resultHeaderData.arrDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.resultHeaderData.arrDate = moment(this.resultHeaderData.arrDate).format('YYYY-MM-DD 0:00:00')
      }
    }
  }
}
</script>

<style></style>
