<!-- eslint-disable vue/no-mutating-props -->
<template>
<div class="container" style="width:1400px;">
  <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">共通情報</div>
  <div class="flex w-full" style="height:fit-content">
    <div class="grid flex-grow card  place-items-center" style="border-radius: revert; max-width: 50%;">
      <va-modal v-model="showErrorModalFlg" @update:modelValue="closeErrorModal" :title="errorTitle" fixed-layout>
        <va-card>
            <table class="va-table">
                <thead>
                    <tr>
                        <th>エラーコード</th>
                        <th>エラー内容</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="sts in errorRowData"  :key="sts.seq">
                        <td>{{ sts.errorCd }}</td>
                        <td>{{ sts.errorMessage }}</td>
                    </tr>
                </tbody>
            </table>
        </va-card>
      </va-modal>
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs2">
          <span style="padding: 0.3rem; text-align: right;">HAWB No.</span>
        </div>
        <div class="item flex xs4">
          <va-select v-model="detailData.cwbNo" style="width: 60px;" placeholder="Text Input" :options="cwbList"
             />
        </div>
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="searchMecData('S')"> 検索 </va-button>
        </div>
        <div class="item flex xs1">
          <va-button color="info" title="イメージ" size="small" icon="mi-picture_as_pdf" @click="openWinPop" :disabled="detailData.pdfUrl == ''" ></va-button>
          <!-- <va-button color="info" size="small" @click="openWinPop" :disabled="detailData.pdfUrl == '' || detailData.pdfUrl == null"> イメージ </va-button> -->
        </div>
        <div class="item flex xs1">
          <va-button color="info" icon="mi-warning" title="エラー電文" size="small" :disabled="detailData.errorCnt == '0'" @click="showErrorModal()"></va-button>
        </div>
        <div class="item flex xs1">
          <va-button color="info" icon="mi-arrow_back" size="small" @click="moveNextOrPrevious('P')" :disabled="prevFlg"></va-button>
        </div>
        <div class="item flex xs1">
          <va-button color="info" icon="mi-arrow_forward" size="small" @click="moveNextOrPrevious('N')" :disabled="nextFlg"></va-button>
        </div>
      </div>
      <div>
        <winPopup ref="winPopup" @onClose="val => evtCloseWinPopup(val)" @onRecvEvtFromWinPop="val => onRecvWinPop(val)">
        </winPopup>
      </div>
      <!-- <div class="row justify-end" style="width: 100%;">
        <div class="item flex xs3">
          <va-button color="info" size="small" :disabled="detailData.errorCnt == '0'" @click="showErrorModal()"> 電文エラー </va-button>
          
        </div>
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="moveNextOrPrevious('P')"> &lt;&lt; </va-button>
        </div>
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="moveNextOrPrevious('N')"> &gt;&gt; </va-button>
        </div>
      </div> -->
    </div>
    
    <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="padding: 0.75rem;">
        <div class="item flex xs12" align="left">
          <span style="padding: 0.3rem;" >業者No.マスタ条件</span>
        </div>
        <div class="item flex xs12" align="left">
            <span style="padding: 0.3rem;"  >{{ detailData.remarks  }}</span>
        </div>
      </div>
      <div class="row justify-start" style="padding: 0.75rem;">
        <div class="item flex xs12" align="left">
          <va-input  type="textarea"  v-model="detailData.conditionComment" class="mb-6" :disabled="true" />
        </div>
      </div>
    </div>
  </div>


<div class="flex flex-col w-full ">
  <div class="grid card place-items-left" style="border-radius: revert;">
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs2" >
        <span style="padding: 0.3rem; text-align: right;">申告件数</span>
        <va-input maxlength="1" input-class="text-center" v-model="detailData.reportCondition" style="width: 5px;"  />
      </div>
      <div class="item flex xs2" align="left">
        <span style="padding: 0.3rem; text-align: right;">申告先種別</span>
        <va-input maxlength="1" input-class="text-center" v-model="detailData.customsKindCd1" style="width: 5px;" />  
      </div>
      <div class="item flex xs2" align="left">
        <span style="padding: 0.3rem; text-align: right;">あて先官署</span>
        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDepCd" style="width: 5px;" />
      </div>
      <div class="item flex xs2" align="left">
        <span style="padding: 0.3rem; text-align: right;">あて先部門</span>
        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDivisionCd" style="width: 5px;" />
      </div>
      <div class="item flex xs3" align="left">
        <span style="padding: 0.3rem; text-align: right;">申告予定年月日</span>
        <Datepicker style="width: 70px;" v-model="detailData.expReportPlanDate" :auto-apply="true" @closed="expReportPlanDatePickerClosedChange"
          :format="DatePickerFormat" :enable-time-picker="false" />
      </div>
    </div>
  </div>
</div>


  <div class="flex w-full">

    <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">輸出者</div>
      </div>
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs8">
          <va-input maxlength="13"  v-model="detailData.expCustomerNo" style="width: 80px;padding: 0.1rem;"  />
          <va-input maxlength="4"  v-model="detailData.expCustomerDeptCd" style="width: 5px;padding: 0.1rem;"  />
          <va-input maxlength="5"  v-model="detailData.expCustomerOcsDeptCd" style="width: 5px;padding: 0.1rem;"  />
        </div>
        <div class="item flex xs2">
          <SearchMerchantModal color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionExp" :parentProps="parentPropsExp">
            検索
          </SearchMerchantModal>
          <!-- <va-button color="info" size="small"> 検索 </va-button> -->
        </div>
        <!-- <div class="item flex xs2">
          <va-button color="info" size="small"> M登録 </va-button>
        </div> -->
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <va-input maxlength="70"  v-model="detailData.expCustomerName" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">〒</span>
          <va-input maxlength="7"  v-model="detailData.expCustomerPostcode" style="width: 60px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">電話</span>
          <va-input maxlength="14"  v-model="detailData.expCustomerTelNo" style="width: 60px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">住所</span>
          <va-input maxlength="15"  v-model="detailData.expCustomerAdd1" style="width: 200px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.expCustomerAdd2" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.expCustomerAdd3" style="width: 200px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="70"  v-model="detailData.expCustomerAdd4" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">一括</span>
          <va-input maxlength="105"  v-model="detailData.expCustomerAddLump" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">取込</span>
          <va-input maxlength="74"  v-model="detailData.expCustomerAdd" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>

    </div>


    <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">仕向人</div>
      </div>
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs8">
          <va-input maxlength="8"  v-model="detailData.consigneeNo" style="width: 80px;padding: 0.1rem;" />
          <va-input maxlength="4"  v-model="detailData.consigneeDeptCd" style="width: 5px;padding: 0.1rem;" />
          <va-input maxlength="5"  v-model="detailData.consigneeOcsDeptCd" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <SearchConsignorConsigneeModal color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionCon" :parentProps="parentPropsExp">
            検索
          </SearchConsignorConsigneeModal>
          <!-- <va-button color="info" size="small"> 検索 </va-button> -->
        </div>
        <!-- <div class="item flex xs2">
          <va-button color="info" size="small"> M登録 </va-button>
        </div> -->
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <va-input maxlength="70"  v-model="detailData.consigneeName" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">〒</span>
          <va-input maxlength="9"  v-model="detailData.consigneePostcode" style="width: 60px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">国</span>
          <va-input maxlength="2"  v-model="detailData.consigneeCountry" style="width: 60px;padding: 0.1rem;" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">住所</span>
          <va-input maxlength="35"  v-model="detailData.consigneeAdd1" style="width: 200px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="35" v-model="detailData.consigneeAdd2" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.consigneeAdd3" style="width: 200px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.consigneeAdd4" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">一括</span>
          <va-input maxlength="105"  v-model="detailData.consigneeAddLump" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">取込</span>
          <va-input maxlength="80"  v-model="detailData.consigneeAdd" style="width: 200px;padding: 0.1rem;" />
        </div>
      </div>
    </div>


  </div>


  <div class="flex flex-col w-full ">
    <div class="grid card place-items-left" style="border-radius: revert;">
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">通関予定蔵置場</span>
          <va-input maxlength="5"  v-model="detailData.bondedWarehouseCd" style="width: 50px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs8">
          <span style="padding: 0.3rem;">税関事務管理人</span>
          <va-input maxlength="17"  v-model="detailData.customsOfficeJanitorCd" style="width: 5px;padding: 0.1rem;" />
          <va-input maxlength="70" v-model="detailData.customsOfficeJanitorName" style="width: 200px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">受理番号</span>
          <va-input maxlength="10"  v-model="detailData.customsOfficeJanitorReNo" style="width: 5px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">貨物個数</span>
          <va-input maxlength="6"  type="number" input-class="text-right" v-model="detailData.carryingPiece" style="width: 50px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">貨物重量</span>
          <va-input maxlength="8" type="number" step="0.0" input-class="text-right" v-model="detailData.carryingWeight" style="width: 50px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
          <span style="padding: 0.3rem;">荷主セクションコード</span>
          <va-input maxlength="20"  v-model="detailData.shippersSecCd" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs5">
          <span style="padding: 0.3rem;">荷主リファレンスナンバー</span>
          <va-input maxlength="35"  v-model="detailData.shippersRefNo" style="width: 50px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">最終仕向地</span>
          <va-input maxlength="5"  v-model="detailData.lastDestinationCd" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">積込港</span>
          <va-input maxlength="3"  v-model="detailData.depPort" style="width: 5px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">識別符号</span>
          <va-input maxlength="1" input-class="text-center" v-model="detailData.discernmentMark" style="width: 10px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">検査立会者</span>
          <va-input maxlength="5" v-model="detailData.inspectionWitness" style="width: 10px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs4">
          <span style="padding: 0.3rem;">FOB価格等</span>
          <va-input maxlength="3" input-class="text-center" v-model="detailData.fobCurrencyCd" style="width: 180px;padding: 0.1rem;" />
          <va-input maxlength="18" type="number" input-class="text-right" v-model="detailData.fobAmo" style="width: 180px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs4">
          <span style="padding: 0.3rem;">申告価格</span>
          <va-input maxlength="6" type="number" input-class="text-right" v-model="detailData.reportValue" style="width: 180px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
          <span style="padding: 0.3rem;">社内整理番号</span>
          <va-input maxlength="20"  v-model="detailData.inHouseRefNumber" style="width: 50px;padding: 0.1rem;" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs9">
          <span style="padding: 0.3rem;">品名</span>
          <va-input maxlength="40"  v-model="detailData.item" style="width: 100px;padding: 0.1rem;" />
        </div>
        <div class="item flex xs3">
          <span style="padding: 0.3rem;">記事</span>
          <va-input maxlength="35"  v-model="detailData.news2" style="width: 150px;padding: 0.1rem;" />
        </div>
      </div>

      

    </div>
  </div>

  <div class="flex flex-col w-full ">
    <div class="grid card place-items-center" style="border-radius: revert;">
      <div class="row justify-end" style="width: 100%;">
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="changeModalLayout"> EDAへ変更 </va-button>
        </div>
        <div class="item flex xs2">
            <va-button color="info" size="small" @click="saveModalData"> 保存 </va-button>
        </div>
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="insertModalData"> 登録 </va-button>
        </div>
      </div>
    </div>
  </div>
</div>

</template>
<script>
// eslint-disable-next-line no-unused-vars
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
// eslint-disable-next-line no-unused-vars
import winPopup from '../OC_CS_001_01/winPop.vue'
import SearchMerchantModal from '../../components/SearchModal/SearchMerchantModal.vue'
import SearchConsignorConsigneeModal from '../../components/SearchModal/SearchConsignorConsigneeModal.vue'


export default {
  mounted() {
    this.$watch(
      () => this.detailData,
      (newValue, oldValue) => {
        this.sendToChild(newValue.pdfUrl, newValue.pdfUrl2)
        this.parentPropsImp.customerNamee = newValue.consigneeName
        this.parentPropsExp.customerNamee = newValue.expCustomerName
      }
    )
  },
  props: {
    changeModalLayout: Function,
    detailData: Object,
    cwbList: Array,
    insertModalData: Function,
    saveModalData:Function,
    changeDetailDataMEC: Function,
    prevFlg: Boolean,
    nextFlg: Boolean,
    disabledFlg: Boolean,
    disabledFlg2: Boolean,
  },
  components: {
    Datepicker,
    winPopup,
    SearchMerchantModal,
    SearchConsignorConsigneeModal
  },
  data: function () {
    return {
      micData: this.detailData,
      DatePickerFormat: 'yyyy-MM-dd',
      errorRowData: [],
      errorTitle: '電文エラー詳細',
      showErrorModalFlg: false,
      parentPropsExp: {
        customerNo: '',
        deptCd: '',
        customerNamee: this.detailData.expCustomerName
      },
      parentPropsImp: {
        customerNo: '',
        deptCd: '',
        customerNamee: this.detailData.consigneeName
      }
    };
  },
  methods: {
    evtCloseWinPopup() {
      
    },
    openWinPop() {
      const uri = '/pdfViewer?param1=' + this.detailData.pdfUrl + '&param2=' + this.detailData.pdfUrl2
      this.$refs.winPopup.openWinPop(uri, 1560, 700);
    },
    sendToChild(val,val2) {
      this.$refs.winPopup.sendEvtToChild(val, val2);
    }, 
    getNextImagePath(val) {
      
    },
    searchMecData(evt) {
      this.changeDetailDataMEC(evt, this.detailData.awbNo, this.detailData.cwbNo)
    },
    moveNextOrPrevious(evt) { 
      this.changeDetailDataMEC(evt, this.detailData.awbNo, this.detailData.cwbNo)
    },
    expReportPlanDatePickerClosedChange() {
      if (this.detailData.expReportPlanDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.expReportPlanDate = moment(this.detailData.expReportPlanDate).format('YYYY-MM-DD')
      }
    },
    showErrorModal() {
      this.axios.get('/api/oaet001/searchErrorReport', {
        params: {
          awbNo: this.detailData.awbNo,
          cwbNo: this.detailData.cwbNo,
          type: this.detailData.category
        }
      })
        .then(res => {
          this.errorRowData = res.data
          this.showErrorModalFlg = !this.showErrorModalFlg
        })
        
    },
    closeErrorModal() {
      
    },
    handleClickToSearchActionCon(selectedRowData) {
      
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeDeptNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeOCSDeptNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeName = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneePostcode = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeCountry = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd1 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd2 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd3 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd4 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAddLump = ''

      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeNo = selectedRowData.customerNo
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeDeptNo = selectedRowData.deptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeOCSDeptNo = selectedRowData.ocsdeptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeName = selectedRowData.customerNamee
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneePostcode = selectedRowData.zipCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeCountry = selectedRowData.countryCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd1 = selectedRowData.customerAdde1
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd2 = selectedRowData.customerAdde2
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd3 = selectedRowData.customerAdde3
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAdd4 = selectedRowData.customerAdde4
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.consigneeAddLump = selectedRowData.customerAddeblAnket
    },
    handleClickToSearchActionExp(selectedRowData) {
    
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerDeptCd = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerOcsDeptCd = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerName = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expPostCode = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerTelNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd1 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd2 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd3 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd4 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAddLump = ''

      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerNo = selectedRowData.customerNo
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerDeptCd = selectedRowData.deptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerOcsDeptCd = selectedRowData.ocsdeptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerName = selectedRowData.customerNamee
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expPostcode = selectedRowData.zipCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerTelNo = selectedRowData.telNo
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd1 = selectedRowData.customerAdde1
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd2 = selectedRowData.customerAdde2
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd3 = selectedRowData.customerAdde3
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAdd4 = selectedRowData.customerAdde4
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerAddLump = selectedRowData.customerAddeblAnket
    },
  }
};
</script>