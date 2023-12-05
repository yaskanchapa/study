<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <va-card>        
    <div class="item flex xs12" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 8%;">申告日</span>
        <Datepicker v-model="expsearchCondition.declarationDate1"
          :auto-apply="true"
          @closed="declarationDate1PickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          style="width: 11%;"
          placeholder="申告日(FROM)"
        />
      <span style="padding: 0.5rem;width: 3%;">～</span>
        <Datepicker v-model="expsearchCondition.declarationDate2"
          :auto-apply="true"
          @closed="declarationDate2PickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          style="width: 11%;"
          placeholder="申告日(TO)"
        />
      <span style="padding: 0.3rem;width: 8%;">許可日</span>
        <Datepicker v-model="expsearchCondition.permissionDate1"
          :auto-apply="true"
          @closed="permissionDate1PickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          style="width: 11%;"
          placeholder="許可日(FROM)"
        />
      <span style="padding: 0.5rem;width: 3%;">～</span>
        <Datepicker v-model="expsearchCondition.permissionDate2"
          :auto-apply="true"
          @closed="permissionDate2PickerClosedChange"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          style="width: 11%;"
          placeholder="許可日(TO)"
        />
      <span style="padding: 0.3rem;width: 8%;">通関コード</span>
        <va-input v-model="expsearchCondition.bondedCd" style="width: 9%;" placeholder="通関コード"/>
      <span style="padding: 0.3rem;width: 8%;">上屋コード</span>
        <va-input v-model="expsearchCondition.shedCd" style="width: 9%;" placeholder="上屋コード"/>
    </div>
    <div class="item flex xs12" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 8%;">HAWBNo</span>
      <va-input ref="cwbNo" v-model="expsearchCondition.cwbNo" style="width: 25%;" placeholder="HAWBNo"/>
      <span style="padding: 0.3rem;width: 8%;">MAWBNo</span>
      <va-input ref="awbNo" v-model="expsearchCondition.awbNo" style="width: 25%;" placeholder="MAWBNo"/>
      <span style="padding: 0.3rem;width: 8%;">FLT</span>
      <va-input v-model="expsearchCondition.flt1" style="width: 11%;" placeholder="FLT1"/>
      <span style="padding: 0.4rem;width: 3%;">/</span>
      <va-input v-model="expsearchCondition.flt2" style="width: 11%;" placeholder="FLT2"/>
    </div>
    <div class="item flex xs12" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 8%;">積出地</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.depPort" placeholder="積出地"/>
      <span style="padding: 0.3rem;width: 8%;">社内整理番号</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.referenceNo" placeholder="社内整理番号"/>
      <span style="padding: 0.3rem;width: 8%;">輸出入者コード</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.inputExpCustomerNo" placeholder="輸出入者コード"/>
      </div>
      <div class="item flex xs8" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 4%;">輸入者名</span>
      <va-input style="width: 29%;" v-model="expsearchCondition.consigneeName" placeholder="輸入者名"/>
      <span style="padding: 0.3rem;width: 4%;">輸入者住所</span>
      <va-input style="width: 29%;" v-model="expsearchCondition.consigneeAddLump" placeholder="輸入者住所"/>
    </div>
      <div class="item flex xs12" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 8%;">輸出者名</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.exporterName" placeholder="輸出者名"/>
      <span style="padding: 0.3rem;width: 8%;">輸出者住所</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.exporterAdd" placeholder="輸出者住所"/>
      <span style="padding: 0.3rem;width: 8%;">品名</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.item" placeholder="品名"/>
    </div>
      <div class="item flex xs12" style="padding: 0.5rem;">
      <span style="padding: 0.3rem;width: 8%;">記事（税関）</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.news1" placeholder="記事（税関）"/>
      <span style="padding: 0.3rem;width: 8%;">記事（通関）</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.news2" placeholder="記事（通関）"/>
      <span style="padding: 0.3rem;width: 8%;">記事（荷主）</span>
      <va-input style="width: 25%;" v-model="expsearchCondition.newsShipper" placeholder="記事（荷主）"/>
    </div>    
    <div align="right">
      <va-button color="info" class="mr-4 mb-2 mt-2" @click="searchExpData(expsearchCondition)" >検索</va-button>
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
    searchExpData: Function,
  },
  data () {
    return {
        expsearchCondition: {
        declarationDate1: null,
        declarationDate2: null,
        permissionDate1: null,
        permissionDate2: null,
        bondedCd: '',
        shedCd: '',
        awbNo: '',
        cwbNo: '',
        flt1: '',
        flt2: '',
        referenceNo: '',
        item: '',
        exporterName: '',
        exporterAdd: '',
        news1: '',
        news2: '',
        newsShipper: '',
        depPort: '',
        inputExpCustomerNo: '',
        consigneeName: '',
        consigneeAddLump: '',
      },     
      mawbNoOption: ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine'],
      declarationDate1: {
        arrPortDate: '',
        awbNo: '',
        isBatchTarget: true,
        isBatchNotTarget: true,
        isUnMatchHawbCnt: true,
        isUnMatchPieceCnt: true,
        isMatch: true
      },
      declarationDate2: {
        arrPortDate1: '',
        awbNo: '',
        isBatchTarget: true,
        isBatchNotTarget: true,
        isUnMatchHawbCnt: true,
        isUnMatchPieceCnt: true,
        isMatch: true
      },
      permissionDate1: {
        permissionDate1: '',
        awbNo: '',
        isBatchTarget: true,
        isBatchNotTarget: true,
        isUnMatchHawbCnt: true,
        isUnMatchPieceCnt: true,
        isMatch: true
      },
      permissionDate2: {
        permissionDate2: '',
        awbNo: '',
        isBatchTarget: true,
        isBatchNotTarget: true,
        isUnMatchHawbCnt: true,
        isUnMatchPieceCnt: true,
        isMatch: true
      },
      DatePickerFormat: 'yyyy-MM-dd'
    }
  },
  methods: {
    declarationDate1PickerClosedChange() {
      if (this.expsearchCondition.declarationDate1) {
        this.expsearchCondition.declarationDate1 = moment(this.expsearchCondition.declarationDate1).format('YYYY-MM-DD')
      }
    },
    declarationDate2PickerClosedChange() {
      if (this.expsearchCondition.declarationDate2) {
        this.expsearchCondition.declarationDate2 = moment(this.expsearchCondition.declarationDate2).format('YYYY-MM-DD')
      }
    },
    permissionDate1PickerClosedChange() {
      if (this.expsearchCondition.permissionDate1) {
        this.expsearchCondition.permissionDate1 = moment(this.expsearchCondition.permissionDate1).format('YYYY-MM-DD')
      }
    },
    permissionDate2PickerClosedChange() {
      if (this.expsearchCondition.permissionDate2) {
        this.expsearchCondition.permissionDate2 = moment(this.expsearchCondition.permissionDate2).format('YYYY-MM-DD')
      }
    }
  }
}
</script>
<style>
</style>
