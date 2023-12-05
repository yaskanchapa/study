<!-- eslint-disable vue/no-mutating-props -->
<template>
<div class="container"  style="width:1400px;">
  <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">共通情報</div>
  <div class="flex w-full" style="height:fit-content">
    <div class="grid flex-grow card  place-items-center" style="border-radius: revert; max-width: 50%;">
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs3">
          <span style="padding: 0.3rem;">MAWB No.</span>
        </div>
        <div class="item flex xs5">
          <va-input   v-model="detailData.awbNo" style="width: 150px;" placeholder="Text Input" :disabled="true" />
        </div>
        <div class="item flex xs2">
          <va-button color="info" title="イメージ" size="small" icon="mi-picture_as_pdf" @click="openWinPop" :disabled="detailData.pdfUrl == '' &&  detailData.pdfUrl2 == '' " ></va-button>
        </div>
        <div class="item flex xs2">
            <va-button color="info" icon="mi-warning" title="エラー電文" size="small" :disabled="detailData.errorCnt == '0'" @click="showErrorModal()"></va-button>
          </div>
      </div>
    <winPopup ref="winPopup"  @onRecvEvtFromWinPop="val => onRecvWinPop(val)">
    </winPopup>
          
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs3">
          <span style="padding: 0.3rem;">HAWB No.</span>
        </div>
        <div class="item flex xs5">
          <va-select v-model="detailData.cwbNo" style="width: 200px;" placeholder="Text Input" :options="cwbList"  />
          <va-input  v-model="detailData.cwbNoDeptCd" style="width: 5px;" placeholder="Text Input" :disabled="disabledFlg == 1"/>
        </div>
        <div class="item flex xs2">
          <va-button color="info" size="small" @click="searchMicData('S')"> 検索 </va-button>
        </div>
        <div class="item flex xs1">
            <va-button color="info" icon="mi-arrow_back" size="small" @click="moveNextOrPrevious('P')" :disabled="prevFlg"></va-button>
          </div>
          <div class="item flex xs1">
            <va-button color="info" icon="mi-arrow_forward" size="small" @click="moveNextOrPrevious('N')" :disabled="nextFlg"></va-button>
          </div>
      </div>
      <va-modal v-model="showErrorModalFlg" :title="errorTitle" fixed-layout @ok="closeErrorModal()">
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
    </div>
    <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="padding: 0.75rem;">
        <div class="item flex xs12" align="left">
          <span style="padding: 0.3rem;" >業者No.マスタ条件</span>
        </div>
        <div class="item flex xs12" align="left">
          <span style="padding: 0.3rem;"  >{{ detailData.remarks }}</span>
        </div>
      </div>
      <div class="row justify-start" style="padding: 0.75rem;">
        <div class="item flex xs12" align="left">
          <va-input  type="textarea" v-model="detailData.conditionComment" :disabled="true" class="mb-6" />
        </div>
      </div>
    </div>
  </div>


<div class="flex flex-col w-full ">
  <div class="grid card place-items-left" style="border-radius: revert;">
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs9" align="left">
        <span style="padding: 0.3rem;">申告件数</span>
        <va-input maxlength="1" input-class="text-center" v-model="detailData.reportCondition" style="width: 5px;" :disabled="disabledFlg == 1" />
        <span style="padding: 0.3rem;">申告先種別</span>
        <va-input maxlength="1" input-class="text-center" v-model="detailData.reportKindCd2" style="width: 5px;" :disabled="disabledFlg == 1" />
        <span style="padding: 0.3rem;">識別符号</span>
        <va-input maxlength="1" input-class="text-center" v-model="detailData.discernmentMark" style="width: 5px;" :disabled="disabledFlg == 1" />
        <span style="padding: 0.3rem;">あて先官署</span>
        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDivCd" style="width: 5px;" :disabled="disabledFlg == 1" />
        <span style="padding: 0.3rem;">あて先部門</span>
        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDepCd" style="width: 5px;" :disabled="disabledFlg == 1"/>
      </div>
      <div class="item flex xs3" align="left">
        <span style="padding: 0.3rem;">申告予定年月日</span>
        <Datepicker v-model="detailData.reportPlaningDate" :auto-apply="true" @closed="reportPlaningDatePickerClosedChange"
            :format="DatePickerFormat" :enable-time-picker="false" style="width: 120px;" :disabled="disabledFlg == 1"/>
      </div>
    </div>
  </div>
</div>


  <div class="flex w-full">

    <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">輸入者</div>
      </div>
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs8">
          <va-input maxlength="13"  v-model="detailData.impCustomerNo" style="width: 80px;padding: 0.1rem;" :disabled="disabledFlg == 1 ||  disabledFlg2 == 1" />
          <va-input maxlength="4"  v-model="detailData.impCustomerDeptCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
          <va-input maxlength="5"  v-model="detailData.impCustomerOcsDeptCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
        <div class="item flex xs3">
          <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1">検索  </va-button> -->
          <SearchMerchantModal  isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionImp" color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" :parentProps="parentPropsImp">
            検索
              <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1"> 検索 </va-button> -->
          </SearchMerchantModal>
          <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1"> 検索 </va-button> -->
        </div>
        <!-- 未使用 -->
        <!-- <div class="item flex xs2">
          <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1"> M登録 </va-button>
        </div> -->
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <va-input maxlength="70"  v-model="detailData.impCustomerName" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">〒</span>
        
          <va-input maxlength="7"  v-model="detailData.impPostCode" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
        <div class="item flex xs6">
          <span style="padding: 0.3rem;">TEL</span>
        
          <va-input maxlength="14"  v-model="detailData.impCustomerTelNo" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs7">
          <span style="padding: 0.3rem;">住所</span>
          <va-input maxlength="15"  v-model="detailData.impCustomerAdd1" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
        <div class="item flex xs5">
          <va-input maxlength="35"  v-model="detailData.impCustomerAdd2" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.impCustomerAdd3" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="70"  v-model="detailData.impCustomerAdd4" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">一括</span>
          <va-input maxlength="105"  v-model="detailData.impCustomerAddLump" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">取込</span>
          <va-input maxlength="74"  v-model="detailData.impCustomerAdd" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
        </div>
      </div>

    </div>


    <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">
      <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">仕出人</div>
      </div>
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs8">
          <va-input maxlength="8"  v-model="detailData.expCustomerNo" style="width: 80px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
          <va-input maxlength="4"  v-model="detailData.expCustomerDeptNo" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
          <va-input maxlength="5"  v-model="detailData.expCustomerOcsDeptNo" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
        <div class="item flex xs3">
          <SearchConsignorConsigneeModal isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionCon" color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg3 == 1" :parentProps="parentPropsExp">
            検索  
            <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg3 == 1"> 検索 </va-button> -->
          </SearchConsignorConsigneeModal>
          <!-- <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg3 == 1"> 検索 </va-button> -->
        </div>
        <!-- 未使用 -->
        <!-- <div class="item flex xs2">
          <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg3 == 1"> M登録 </va-button>
        </div> -->
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <va-input maxlength="70"  v-model="detailData.expCustomerName" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs1">
          <span style="padding: 0.3rem;">〒</span>
        </div>
        <div class="item flex xs5">
          <va-input maxlength="9"  v-model="detailData.expCustomerPostCode" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
        <div class="item flex xs1">
          <span style="padding: 0.3rem;">国</span>
        </div>
        <div class="item flex xs5">
          <va-input maxlength="2"  v-model="detailData.expCustomerCountry" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs7">
          <span style="padding: 0.3rem;">住所</span>
          <va-input maxlength="35"  v-model="detailData.expCustomerAdd1" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
        <div class="item flex xs5">
          <va-input maxlength="35" v-model="detailData.expCustomerAdd2" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.expCustomerAdd3" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
        <div class="item flex xs6">
          <va-input maxlength="35"  v-model="detailData.expCustomerAdd4" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">一括</span>
          <va-input maxlength="105"  v-model="detailData.expCustomerAddLump" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
      
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs12">
          <span style="padding: 0.3rem;">取込</span>
          <va-input maxlength="80"  v-model="detailData.expCustomerAdd" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1" />
        </div>
      </div>
    </div>


  </div>


  <div class="flex flex-col w-full ">
    <div class="grid card place-items-left" style="border-radius: revert;">
      <div class="row justify-start" style="width: 100%;">
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">貨物個数</span>
          <va-input maxlength="6" type="number" input-class="text-right"  v-model="detailData.cargoPiece" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>
        <div class="item flex xs2">
          <span style="padding: 0.3rem;">貨物重量</span>
          <va-input maxlength="8" type="number" step="0.00" input-class="text-right" v-model="detailData.cargoWeight" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>
        <div class="item flex xs4">
          <span style="padding: 0.3rem;">積載機名</span>
          <va-input maxlength="6"  v-model="detailData.currentArrFlt1" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
          <span style="padding: 0.3rem;">/</span>
          <va-input maxlength="5"  v-model="detailData.currentArrFlt2" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>
        <div class="item flex xs4">
          <span style="padding: 0.3rem;">入港年月日</span>
          <Datepicker v-model="detailData.arrPortDate" :auto-apply="true" @closed="arrPortDatePickerClosedChange"
              :format="DatePickerFormat" :enable-time-picker="false" style="width: 120px;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>
      </div>

      <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs2">
          <span style="padding: 0.3rem;">取卸港</span>
          <va-input maxlength="3"  v-model="detailData.getPort" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs2">
          <span style="padding: 0.3rem;">積出地</span>
          <va-input maxlength="5"  v-model="detailData.shipmentCd" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs5">
          <span style="padding: 0.3rem;">仕入書価格</span>
          <va-input maxlength="1" input-class="text-center" v-model="detailData.invoiceValClassCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <va-input maxlength="3" input-class="text-center" v-model="detailData.invoiceValConCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <va-input maxlength="3" input-class="text-center"  v-model="detailData.invoiceCurCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <!-- <va-input maxlength="13" type="number" step="0.00" input-class="text-right" v-model="detailData.invoiceValue" style="width: 30px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"  /> -->
          <va-input maxlength="17" input-class="text-right" mask="numeral" v-model="detailData.invoiceValue" style="width: 30px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"  />
        </div>

        <div class="item flex xs3">
          <span style="padding: 0.3rem;">課税価格</span>
          <va-input maxlength="8" input-class="text-right" mask="numeral" v-model="detailData.taxablesAmo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

      </div>

      <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs4">
          <span style="padding: 0.3rem;">運賃</span>
          <va-input maxlength="1" input-class="text-center" v-model="detailData.fareFlg" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
          <va-input maxlength="3" input-class="text-center" v-model="detailData.fareCurrencyCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
          <va-input maxlength="17" mask="numeral" input-class="text-right" v-model="detailData.fare" style="width: 30px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs4">
          <span style="padding: 0.3rem;">保険</span>
          <va-input maxlength="1" input-class="text-center" v-model="detailData.insuranceClassCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <va-input maxlength="3" input-class="text-center" v-model="detailData.insuranceCurCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <va-input maxlength="12" mask="numeral" input-class="text-right" v-model="detailData.insuranceAmount" style="width: 30px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs2">
          <span style="padding: 0.3rem;">原産地</span>
          <va-input maxlength="2" v-model="detailData.countryOfOriginCD" style="width: 10px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

      </div>

      <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs4">
          <span style="padding: 0.3rem;">品名</span>
          <va-input maxlength="40"  v-model="detailData.item" style="width: 180px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs4">
          <span style="padding: 0.3rem;">記事</span>
          <va-input maxlength="35"  v-model="detailData.news2" style="width: 180px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs3">
          <span style="padding: 0.3rem;">社内整理番号</span>
          <va-input maxlength="20"  v-model="detailData.inhouseRefNumber" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

      </div>

      <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs9">
          <span style="padding: 0.3rem;">税関事務管理人名</span>
          <va-input maxlength="17"  v-model="detailData.customsOfficeJanitorCd" style="width: 100px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
          <va-input maxlength="70"  v-model="detailData.customsOfficeJanitorName" style="width: 400px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs3">
          <span style="padding: 0.3rem;">受理番号</span>
          <va-input maxlength="10"  v-model="detailData.customsOfficeJanitorReNo" style="width: 150px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

      </div>

      <div class="row justify-start" style="width: 100%;">

        <div class="item flex xs3">
          <span style="padding: 0.3rem;">検査立会者</span>
          <va-input maxlength="5"  v-model="detailData.inspectionWitness" style="width: 100px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

        <div class="item flex xs4">
          <span style="padding: 0.3rem;">荷主セクションコード</span>
          <va-input maxlength="20"  v-model="detailData.shippersSecCd" style="width: 150px;padding: 0.1rem;"  :disabled="disabledFlg == 1 || disabledFlg4 == 1"/>
        </div>

        <div class="item flex xs5">
          <span style="padding: 0.3rem;">荷主リファレンスナンバー</span>
          <va-input maxlength="35"  v-model="detailData.shippersRefNo" style="width: 150px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg4 == 1" />
        </div>

      </div>

    </div>

  </div>

  <div class="flex flex-col w-full ">

    <div class="grid card place-items-center" style="border-radius: revert; border:none;">

      <div class="row justify-end" style="width: 100%;">

        <div class="item flex xs2">
          <va-button color="info" size="small" @click="changeModalLayout" :disabled="disabledFlg == 1"> IDAへ変更 </va-button>
        </div>

        <div class="item flex xs2">
          <va-button color="info" size="small" @click="saveModalData" :disabled="disabledFlg == 1"> 保存 </va-button>
        </div>

        <div class="item flex xs2">
          <va-button color="info" size="small" @click="insertModalData" :disabled="disabledFlg == 1"> 登録 </va-button>
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
        this.sendToChild(newValue.pdfUrl)
        this.parentPropsImp.customerNamee = newValue.impCustomerName
        this.parentPropsExp.customerNamee = newValue.expCustomerName
      }
    )
  },
  props: {
    changeModalLayout: Function,
    detailData: Object,
    cwbList: Array,
    insertModalData: Function,
    changeDetailDataMIC: Function,
    saveModalData: Function,
    prevFlg: Boolean,
    nextFlg: Boolean,
    getPdf: Function,
    disabledFlg: String,
  },
  components: {
    winPopup,
    Datepicker,
    SearchMerchantModal,
    SearchConsignorConsigneeModal
  },
  data: function () {
    return {
      micData: this.detailData,
      // disabledFlg: 0,
      disabledFlg2: 0,
      disabledFlg3: 0,
      disabledFlg4: 0,
      DatePickerFormat: 'yyyy-MM-dd',
      showErrorModalFlg: false,
      errorRowData:[],
      errorTitle: '電文エラー詳細',
      pdfData: null,
      parentPropsExp : {
        customerNo: '',
        deptCd: '',
        customerNamee: this.detailData.expCustomerName
      },
      parentPropsImp: {
        customerNo: '',
        deptCd: '',
        customerNamee: this.detailData.impCustomerName
      }
    };
  },
  methods: {
    openWinPop() {
      const uri = '/pdfViewer?param1=' + this.detailData.pdfUrl + '&param2=' + this.detailData.pdfUrl2
      this.$refs.winPopup.openWinPop(uri, 1560, 700);
    },
    sendToChild(val) {
      this.$refs.winPopup.sendEvtToChild(val);
    }, 
    searchMicData(evt) {
      this.changeDetailDataMIC(evt, this.detailData.awbNo, this.detailData.cwbNo)
    },
    moveNextOrPrevious(evt) { 
      this.changeDetailDataMIC(evt, this.detailData.awbNo, this.detailData.cwbNo)
      // this.sendToChild(evt)
    },
    reportPlaningDatePickerClosedChange() {
      if (this.detailData.reportPlaningDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.reportPlaningDate = moment(this.detailData.reportPlaningDate).format('YYYY-MM-DD')
      }
    },
    arrPortDatePickerClosedChange() {
      if (this.detailData.arrPortDate) {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.arrPortDate = moment(this.detailData.arrPortDate).format('YYYY-MM-DD')
      }
    },
    showErrorModal() {
      this.axios.get('/api/oait001/searchErrorReport', {
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
    handleClickToSearchActionImp(selectedRowData) {
      console.log(selectedRowData)
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerDeptCd = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerOcsDeptCd = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerName = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impPostcode = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerTelNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd1 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd2 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd3 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd4 = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAddLump = ''

      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerNo = selectedRowData.customerNo
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerDeptCd = selectedRowData.deptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerOcsDeptCd = selectedRowData.ocsdeptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerName = selectedRowData.customerNamee
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impPostCode = selectedRowData.zipCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerTelNo = selectedRowData.telNo
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd1 = selectedRowData.customerAdde1
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd2 = selectedRowData.customerAdde2
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd3 = selectedRowData.customerAdde3
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAdd4 = selectedRowData.customerAdde4
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.impCustomerAddLump = selectedRowData.customerAddeblAnket

      if (selectedRowData.customsOfficeJanitorCd !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.customsOfficeJanitorCd = selectedRowData.customsOfficeJanitorCd
      }
      if (selectedRowData.customsOfficeJanitorName !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.customsOfficeJanitorName = selectedRowData.customsOfficeJanitorName
      }
      if (selectedRowData.customsOfficeJanitorreNo !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.customsOfficeJanitorReNo = selectedRowData.customsOfficeJanitorreNo
      }
      if (selectedRowData.shipperssecCd !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.shippersSecCd = selectedRowData.shipperssecCd
      }
      if (selectedRowData.shippersrefNo !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.shippersRefNo = selectedRowData.shippersrefNo
      }
      if (selectedRowData.insuranceClassCd !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.insuranceClassCd = selectedRowData.insuranceClassCd
      }
      if (selectedRowData.conditionComment !== '') {
        // eslint-disable-next-line vue/no-mutating-props
        this.detailData.conditionComment = selectedRowData.conditionComment
      }
    },
    handleClickToSearchActionCon(selectedRowData) {
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerDeptNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerOcsDeptNo = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerName = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerPostCode = ''
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerCountry = ''
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
      this.detailData.expCustomerDeptNo = selectedRowData.deptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerOcsDeptNo = selectedRowData.ocsdeptCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerName = selectedRowData.customerNamee
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerPostCode = selectedRowData.zipCd
      // eslint-disable-next-line vue/no-mutating-props
      this.detailData.expCustomerCountry = selectedRowData.countryCd
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
    }
  }
};
</script>

<style>
span {
  max-width: fit-content;
  line-height:2;
}
</style>