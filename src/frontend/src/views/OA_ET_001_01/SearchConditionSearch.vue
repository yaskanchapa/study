<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">トラック便日付</span>
        <Datepicker  v-model="searchCondition.truckNoDate" :auto-apply="true" @closed="truckNoDatePickerClosedChange"
          :format="DatePickerFormat" :enable-time-picker="false" />
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">トラック便</span>
        <va-select v-model="searchCondition.linkTruckNo"  @update:model-value="getAwbNoList"  style="width: 260px;"  
          :options="linkTruckNoList" 
          :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
          :value-by="(option) => option.VALUE"
        /> 
      </div>
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">MAWB</span>
        <va-select  style="width: 260px;" v-model="searchCondition.awbNo" :options="awbNoList" 
          allow-create="unique"
          @create-new="addNewOption"  
        />
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
        <span style="padding: 0.3rem;">HAWB</span>
        <va-input ref="cwbNo" v-model="searchCondition.cwbNo" style="width: 250px;" placeholder="Text Input" />
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium" @click="searchExportData()"> 検索 </va-button>
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
    awbNoList: Array,
    searchExportData: Function,
    getAwbNoList: Function,
    plusAwbList:Function,
    getOtherSearchInfo: Function
  },
  data() {
    return {
      DatePickerFormat: 'yyyy-MM-dd',
      tmpVal: '',
      tmpAWbList:[],
      linkTruckNoList: [
        '',
        { VALUE: '0', OPTION: '0便' },
        { VALUE: '1', OPTION: '1便' },
        { VALUE: '2', OPTION: '2便' },
        { VALUE: '3', OPTION: '3便' },
        { VALUE: '4', OPTION: '4便' },
        { VALUE: '5', OPTION: '5便' },
        { VALUE: '6', OPTION: '6便' },
        { VALUE: '7', OPTION: '7便' },
        { VALUE: '8', OPTION: '8便' },
        { VALUE: '9', OPTION: '9便' },
        { VALUE: '99', OPTION: 'H T' },
        { VALUE: '00', OPTION: '指示' },
      ],
    }
  },
  methods: {
    truckNoDatePickerClosedChange() {
      if (this.searchCondition.truckNoDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.searchCondition.truckNoDate = moment(this.searchCondition.truckNoDate).format('YYYY-MM-DD 00:00:00')
        this.getAwbNoList()
      }
    },
    addNewOption(newOption) {
      this.plusAwbList(newOption)
    }
  }
}
</script>
