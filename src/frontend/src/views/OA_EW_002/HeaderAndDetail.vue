<template>
  <va-card>
    <va-card-title><span style="font-size: 12pt">ヘッダー</span></va-card-title>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">蔵置場所</span>
          <va-select style="width: 260px;"
          v-model="registCondition.bondedWarehouseCd"
          :options="bondedWarehouseCdList"
          :value-by="(option) => option.value"
          placeholder="--Select BondedWarehouseCd--"
          ref="bondedWarehouseCdSelect"
          :disabled="isLoading || !isSearch || isFix"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">通関場所</span>
          <va-select style="width: 260px;"
          v-model="registCondition.customsPlace"
          :options="customsPlaceList"
          :value-by="(option) => option.value"
          placeholder="--Select customsPlace--"
          ref="customsPlaceSelect"
          :disabled="isLoading || !isSearch || isFix"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">積込年月日</span>
          <Datepicker v-model="registCondition.containerDate"
          :auto-apply="true"
          @closed="containerDatePickerClosed"
          :format="DatePickerFormat"
          :enable-time-picker="false"
          :disabled="isLoading || !isSearch || isFix"
          />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">積込港</span>
          <va-select style="width: 260px;"
          v-model="registCondition.depPort"
          :options="depPortList"
          :value-by="(option) => option.value"
          placeholder="--Select depPort--"
          ref="depPortSelect"
          :disabled="isLoading || !isSearch || isFix"/>
      </div>
   </div>
   <div class="row justify-space-between" style="padding: 0.75rem;">
    <div class="item flex xs3">
          <span style="padding: 0.3rem;">MAWB No.</span>
          <va-input style="width: 250px;" :disabled="isLoading || !isSearch || isFix" v-model="registCondition.awbNo" />
      </div>
      <div class="item flex xs2">
          <va-button :disabled="isLoading || !isSearch || isFix" color="info" size="medium" @click="fix()"> 固定 </va-button>
          <span></span>
          <va-button :disabled="isLoading || !isSearch || !isFix" color="info" size="medium" @click="unFix()"> 固定解除 </va-button>
      </div>
  </div> 
  </va-card>
  <va-card>
    <va-card-title><span style="font-size: 12pt">明細</span></va-card-title>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">コンテナ区分</span>
          <va-select style="width: 260px;"
          v-model="registCondition.containerClassCd"
          :options="containerClassCdList"
          :value-by="(option) => option.value"
          placeholder="--Select ContainerClassCd--"
          ref="containerClassCdSelect"
          :disabled="isLoading || !isSearch"/>
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">ULD No.&emsp;&emsp;</span>
          <va-input style="width: 250px;" :disabled="isLoading || !isSearch" v-model="registCondition.uldNo" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">出発便</span>
          <va-select style="width: 260px;"
          v-model="registCondition.departureTruckNo"
          :options="departureTruckNoList"
          :value-by="(option) => option.value"
          placeholder="--Select departureTruckNo--"
          ref="departureTruckNoSelect"
          :disabled="isLoading || !isSearch"/>
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">HT非表示</span>
          <va-switch :disabled="isLoading || !isSearch" v-model="registCondition.htDisplayFlg" true-inner-label="ON" false-inner-label="OFF" size="large"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
        <div class="item flex xs1">
          <va-button :disabled="isLoading || !isSearch" color="info" size="medium" @click="onClear()"> クリア </va-button>
        </div>
        <div class="item flex xs1">
          <va-button :disabled="isLoading || !isSearch" color="info" size="medium" @click="regist()"> 登録 </va-button>
        </div>
        <div class="item flex xs1">
          <va-button :disabled="isLoading || !isSearch" color="info" size="medium" @click="del()"> 削除 </va-button>
        </div>
      </div>
  </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
export default {
  components: {    
    Datepicker, // eslint-disable-line
  },
  props: {        
    isLoading: Boolean,
    isSearch: Boolean,
    changeSearch: Function,
    clearAll: Function,
    fromParentSchCondition: Object,
    fromParentRegistCondition: Object,
    saveRegistCondition: Function,
    customsPlaceList: Array,
    bondedWarehouseCdList: Array,
    depPortList: Array,
    containerClassCdList: Array,
    departureTruckNoList: Array,
    registContainer: Function,
    deleteContainer: Function,
    selectedRowData: Object,
    clearSelectedRowData: Function
  },
  data () {
    return {      
      registCondition: {
        containerDate: moment().format('YYYY-MM-DD'), // 積込年月日
        awbNo: '',
        uldNo: '',
        customsPlace: '',
        bondedWarehouseCd: '', 
        depPort: '',        
        containerClassCd: '',        
        departureTruckNo: '',
        htDisplayFlg: false        
      },
      DatePickerFormat: 'yyyy-MM-dd',
      isFix: false
    }
  },
  beforeMount() {
    if(this.fromParentSchCondition && this.isSearch) {
      this.registCondition.bondedWarehouseCd = this.fromParentSchCondition.bondedWarehouseCd
      this.registCondition.customsPlace = this.fromParentSchCondition.customsPlace
      this.registCondition.depPort = this.fromParentSchCondition.depPort
      this.registCondition.containerDate = this.fromParentSchCondition.containerDate
    }
    if(this.fromParentRegistCondition && this.isSearch) {
      this.registCondition.awbNo = this.fromParentRegistCondition.awbNo
      this.registCondition.uldNo = this.fromParentRegistCondition.uldNo
      this.registCondition.containerClassCd = this.fromParentRegistCondition.containerClassCd
      this.registCondition.departureTruckNo = this.fromParentRegistCondition.departureTruckNo
      this.registCondition.htDisplayFlg = this.fromParentRegistCondition.htDisplayFlg
    }
  },
  beforeUnmount(){ // アンマウントされる際に、検索条件を親コンポーネントに保存する。
    this.saveRegistCondition(this.registCondition)
  },
  mounted() {
    if(this.selectedRowData) {      
      this.setData(this.selectedRowData)
      this.clearSelectedRowData()
    }
  },
  methods: {
    changeFix () {
      this.isFix = !this.isFix
    },
    containerDatePickerClosed() {
      if (this.registCondition.containerDate) {
        this.registCondition.containerDate = moment(this.registCondition.containerDate).format('YYYY-MM-DD')
      }
    },
    onOut() {
      this.createOutFile()
    },
    setData(rowData) {
      this.registCondition.awbNo = rowData.awbNo
      this.registCondition.containerClassCd = rowData.containerClassCd
      this.registCondition.uldNo = rowData.uldNo
      this.registCondition.depPort = rowData.depPort
      this.registCondition.departureTruckNo = rowData.departureTruckNo
      this.registCondition.htDisplayFlg = rowData.htDisplayFlg
    },
    fix() {
      if(!this.registCondition.awbNo) {
        notificationWarn('MAWB No.を入力してください。')
        return
      }
      this.changeFix()
    },
    unFix() {
      this.isFix = false
    },
    onClear() {
      this.$refs.containerClassCdSelect.reset()
      this.$refs.departureTruckNoSelect.reset()
      this.registCondition.uldNo = ''
      this.registCondition.awbNo = ''
      this.registCondition.htDisplayFlg = false
    },
    clear() {
      this.$refs.bondedWarehouseCdSelect.reset()
      this.$refs.customsPlaceSelect.reset()
      this.$refs.depPortSelect.reset()
      this.$refs.containerClassCdSelect.reset()
      this.$refs.departureTruckNoSelect.reset()
      this.registCondition.containerDate = moment().format('YYYY-MM-DD') // 積込年月日
      this.registCondition.awbNo = ''
      this.registCondition.uldNo = ''
      this.registCondition.htDisplayFlg = false
      this.registCondition.isFix = false
    },  
    regist() {
      const validationChk = []
      if(!this.registCondition.bondedWarehouseCd) { validationChk.push("蔵置場所") }
      if(!this.registCondition.customsPlace) { validationChk.push("通関場所") }
      if(!this.registCondition.containerDate) { validationChk.push("積込年月日") }
      if(!this.registCondition.depPort) { validationChk.push("積込港") }
      if(!this.registCondition.awbNo) { validationChk.push("MAWB No.") }
      if(!this.registCondition.containerClassCd) { validationChk.push("コンテナ区分")  }
      if(!this.registCondition.uldNo) { validationChk.push("ULD No.") }
      if(!this.registCondition.departureTruckNo) { validationChk.push("出発便") }
      if(validationChk.length > 0) {
        notificationWarn('以下は必須項目です。', _.join(validationChk, ', '))
        return
      }
      this.registContainer(this.registCondition)
    },
    del() {
      const validationChk = []
      if(!this.registCondition.containerDate) { validationChk.push("積込年月日") }
      if(!this.registCondition.awbNo) { validationChk.push("MAWB No.") }
      if(!this.registCondition.uldNo) { validationChk.push("ULD No.") }
      if(validationChk.length > 0) {
        notificationWarn('削除処理で以下は必須項目です。', _.join(validationChk, ', '))
        return
      }
      this.deleteContainer(this.registCondition)
    }
  }
}
</script>

<style>
</style>
