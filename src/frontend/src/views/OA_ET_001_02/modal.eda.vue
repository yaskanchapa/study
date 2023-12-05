<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/valid-v-model -->
<!-- eslint-disable vue/no-parsing-error -->
<template>
    <div class="container" style="width:1400px;">
        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">共通情報</div>
        <div class="flex w-full" style="height:fit-content">
            <div class="grid flex-grow card  place-items-center" style="border-radius: revert; max-width: 50%;">
                <winPopup ref="winPopup" @onRecvEvtFromWinPop="val => onRecvWinPop(val)">
                </winPopup>
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem; text-align: right;">HAWB No.</span>
                    </div>
                    <div class="item flex xs4">
                        <va-select v-model="detailData.cwbNo" style="width: 60px;" placeholder="Text Input" :options="cwbList"
                             />
                    </div>
                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="searchEdaData('S')"> 検索 </va-button>
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
            </div>
            <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 50%;">
                <div class="row justify-start" style="padding: 0.75rem;">
                    <div class="item flex xs12" align="left">
                        <span style="padding: 0.3rem;">業者No.マスタ条件</span>
                    </div>
                    <div class="item flex xs12" align="left">
                        <span style="padding: 0.3rem;"  >{{ detailData.remarks }}</span>
                    </div>
                </div>
                <div class="row justify-start" style="padding: 0.75rem;">
                    <div class="item flex xs12" align="left">
                        <va-input type="textarea" v-model="detailData.conditionComment" :disabled="true" class="mb-6" />
                    </div>
                </div>
            </div>
        </div>


        <div class="flex flex-col w-full ">
            <div class="grid card place-items-left" style="border-radius: revert;">
                <div class="row justify-start" style="padding: 0.75rem;">
                    <div class="item flex xs3" align="left">
                        <span style="padding: 0.1rem;  text-align: right;">大額・少額識別</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.largeSmallFlg" style="width: 5px;" />
                        <span style="padding: 0.1rem;  text-align: right;">申告等種別</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.reportKindCd" style="width: 5px;" />
                    </div>
                    <div class="item flex xs3" align="left">
                        <span style="padding: 0.1rem;  text-align: right;">申告先種別</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.customsKindCd1" style="width: 5px;" />
                        <span style="padding: 0.1rem;  text-align: right;">貨物識別</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.customsKindCd2" style="width: 5px;" />
                    </div>
                    <div class="item flex xs3" align="left">
                        <span style="padding: 0.1rem;  text-align: right;">あて先官署</span>
                        <va-input input-class="text-center" maxlength="2" v-model="detailData.reportDepCd" style="width: 5px;" />
                        <span style="padding: 0.1rem;  text-align: right;">あて先部門</span>
                        <va-input input-class="text-center" maxlength="2" v-model="detailData.reportDivisionCd" style="width: 5px;" />
                    </div>
                    <div class="item flex xs3" align="left">
                        <span style="padding: 0.1rem;  text-align: right;">申請予定年月日</span>
                        <Datepicker style="width: 70px;" v-model="detailData.expReportPlanDate" :auto-apply="true"
                            @closed="expReportPlanDatePickerClosedChange" :format="DatePickerFormat" :enable-time-picker="false" />
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
                        <va-input v-model="detailData.expCustomerNo" maxlength="13" style="width: 80px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expCustomerDeptCd" maxlength="4" style="width: 5px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expCustomerOcsDeptCd" maxlength="5" style="width: 5px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
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
                        <va-input v-model="detailData.expCustomerName" maxlength="70" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs1">
                        <span maxlength="7" style="padding: 0.3rem;">〒</span>
                    </div>
                    <div class="item flex xs5">
                        <va-input v-model="detailData.expCustomerPostcode" maxlength="7" style="width: 60px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs1">
                        <span style="padding: 0.3rem;">TEL</span>
                    </div>
                    <div class="item flex xs5">
                        <va-input v-model="detailData.expCustomerTelNo" maxlength="14" style="width: 60px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">住所</span>
                        <va-input v-model="detailData.expCustomerAdd1" maxlength="14" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <va-input v-model="detailData.expCustomerAdd2" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs6">
                        <va-input v-model="detailData.expCustomerAdd3" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <va-input v-model="detailData.expCustomerAdd4" maxlength="70" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">取込</span>
                        <va-input v-model="detailData.expCustomerAdd" maxlength="74" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    
                </div>

            </div>


            <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">
                <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
                    <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">仕向人</div>
                </div>
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs8">
                        <va-input v-model="detailData.consigneeNo" maxlength="8" style="width: 80px;padding: 0.1rem;" />
                        <va-input v-model="detailData.consigneeDeptCd" maxlength="4" style="width: 5px;padding: 0.1rem;" />
                        <va-input v-model="detailData.consigneeOcsDeptCd" maxlength="5" style="width: 5px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <SearchConsignorConsigneeModal color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionCon" :parentProps="parentPropsImp">
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
                        <va-input v-model="detailData.consigneeName" maxlength="70" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs1">
                        <span style="padding: 0.3rem;">〒</span>
                    </div>
                    <div class="item flex xs5">
                        <va-input v-model="detailData.consigneePostcode" maxlength="7" style="width: 60px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs1">
                        <span style="padding: 0.3rem;">国</span>
                    </div>
                    <div class="item flex xs5">
                        <va-input v-model="detailData.consigneeCountry" maxlength="2" style="width: 60px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">住所</span>
                        <va-input v-model="detailData.consigneeAdd1" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <va-input v-model="detailData.consigneeAdd2" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs6">
                        <va-input v-model="detailData.consigneeAdd3" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <va-input v-model="detailData.consigneeAdd4" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">取込</span>
                        <va-input v-model="detailData.consigneeAdd" maxlength="80" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>
            </div>
        </div>


        <div class="flex flex-col w-full ">
            <div class="grid card place-items-center" style="border-radius: revert;">
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">申告予定者</span>
                        <va-input v-model="detailData.reportPlanPersonCd" maxlength="5" style="width: 100px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs8">
                        <span style="padding: 0.3rem;">税関事務管理人</span>
                        <va-input v-model="detailData.customsOfficeJanitorCd" maxlength="17" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.customsOfficeJanitorName" maxlength="70" style="width: 500px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">受理番号</span>
                        <va-input v-model="detailData.customsOfficeJanitorReNo" maxlength="10" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">通関予定蔵置場</span>
                        <va-input v-model="detailData.bondedWarehouseCd" maxlength="5" style="width: 100px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">貨物個数</span>
                        <va-input input-class="text-right" v-model="detailData.carryingPiece" maxlength="8" type="number" style="width: 100px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">荷主セクションコード</span>
                        <va-input v-model="detailData.shippersSecCd" maxlength="20" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">荷主リファレンスナンバー</span>
                        <va-input v-model="detailData.shippersRefNo" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">最終仕向地</span>
                        <va-input v-model="detailData.lastDestinationCd" maxlength="5" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.lastDestName" maxlength="20" style="width: 100px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">積込港</span>
                        <va-input v-model="detailData.depPort" maxlength="3" style="width: 100px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">貿易形態別符号</span>
                        <va-input v-model="detailData.tradeTypeMark" maxlength="3" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">出港予定年月日</span>
                        <Datepicker style="width: 200px;" v-model="detailData.depPlaningDate" :auto-apply="true"
                            @closed="depPlaningDatePickerClosedChange" :format="DatePickerFormat" :enable-time-picker="false" />
                    </div>
                    <div class="item flex xs2">
                            <span style="padding: 0.3rem;">事前検査済貨物等識別</span>
                            <va-input v-model="detailData.preExamCargoDisc" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                        </div>
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs7">
                        <span style="padding: 0.3rem;">インボイス番号等</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.invoiceDiscernment" style="width: 30px;padding: 0.1rem;" />
                        <va-input input-class="text-center" maxlength="10" v-model="detailData.eleInvoiceReNo" style="width: 100px;padding: 0.1rem;" />
                        <va-input v-model="detailData.invoiceNo" maxlength="35" style="width: 200px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">識別符号</span>
                        <va-input input-class="text-center" maxlength="1" v-model="detailData.discernmentMark" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">検査立会者</span>
                        <va-input v-model="detailData.inspectionWitness" maxlength="5" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">インボイス価格等</span>
                        <va-input input-class="text-center" v-model="detailData.invoiceValConCd" maxlength="3" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.invoiceCurCd" maxlength="3" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-right" v-model="detailData.invoiceValue" maxlength="18" type="number" style="width: 100px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.invoiceValClassCd" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">FOB価格等</span>
                        <va-input input-class="text-center" v-model="detailData.fobCurrencyCd" maxlength="3" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-right" v-model="detailData.fobAmo"  maxlength="18" type="number" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">輸出統計品目番号</span>
                        <va-input v-model="detailData.expStatItemNo"  maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.naccsCD" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">品名</span>
                        <va-input v-model="detailData.item" maxlength="40" style="width: 400px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">BPR合計</span>
                        <va-input input-class="text-right" maxlength="10" v-model="detailData.basicPriceTotal" type="number" step="0.00" style="width: 300px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                            <span style="padding: 0.3rem;">要船積(搭載)確認識別</span>
                            <va-input input-class="text-center" v-model="detailData.needLoadingRecDisc" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                        </div>
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">記事(税関)</span>
                        <va-input v-model="detailData.news1" maxlength="140" style="width: 1200px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">記事(通関業者)</span>
                        <va-input v-model="detailData.news2" maxlength="70" style="width: 1100px;padding: 0.1rem;" />
                    </div>
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs10">
                        <span style="padding: 0.3rem;">記事(荷主)</span>
                        <va-input v-model="detailData.newsShipper" maxlength="70" style="width: 800px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">社内整理番号</span>
                        <va-input v-model="detailData.inHouseRefNumber" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

            </div>
        </div>

        <div class="flex flex-col w-full " >
            <div class="grid card place-items-center" style="border-radius: revert;">
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">繰返部</div>
                </div>

                <div class="w-1340 py-2 px-5" style="width: 100%;" >
                    <div class="grid card place-items-center" v-for="(edaSub, index) in edaSubList" :key="edaSub" style="border-radius: revert;">
                        <div class="row justify-start"  style="width: 100%; padding: 0.1rem;">
                            <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">{{ index + 1 }}欄</div>
                        </div>
                        <Meisai v-model:edaSub="edaSubList[index]" />
                    </div>
                </div>

                <div class="row justify-end" style="width: 100%;">
                    <div class="item flex xs2">
                            <va-button color="info" size="small" @click="deleteSubDiv()"> 削除  </va-button>
                        </div>
                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="addDiv"> 追加 </va-button>
                    </div>
                </div>
            </div>
        </div>

        
        <!-- <div class="flex flex-col w-full " v-for="(edaSub,index) in edaSubList" :key="edaSub">
            <div class="grid card place-items-center" style="border-radius: revert;">
                <div class="row justify-start" style="width: 100%; padding: 0.1rem;">
                    <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">{{ index+1 }}欄</div>
                </div>
                <Meisai v-model:edaSub="edaSubList[index]" />
            </div>
        </div> -->
        
        

        <div class="flex flex-col w-full ">
            <div class="grid card place-items-left" style="border-radius: revert;">
                
                <div class="row justify-start"  style="width: 100%;">
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">税関調査用符号</span>
                        <va-input v-model="detailData.customsExamMark" maxlength="5" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">輸出承認証等区分</span>
                        <va-input v-model="detailData.expRecoFlg" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">輸出承認証番号等</span>
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">1</span>
                        <va-input v-model="detailData.expRecDisc1" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo1" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">2</span>
                        <va-input v-model="detailData.expRecDisc2" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo2" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">3</span>
                        <va-input v-model="detailData.expRecDisc3" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo3" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">4</span>
                        <va-input v-model="detailData.expRecDisc4" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo4" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">5</span>
                        <va-input v-model="detailData.expRecDisc5" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo5" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">6</span>
                        <va-input v-model="detailData.expRecDisc6" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo6" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>
                
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">7</span>
                        <va-input v-model="detailData.expRecDisc7" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo7" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">8</span>
                        <va-input v-model="detailData.expRecDisc8" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo8" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">9</span>
                        <va-input v-model="detailData.expRecDisc9" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo9" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">10</span>
                        <va-input v-model="detailData.expRecDisc10" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo10" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">11</span>
                        <va-input v-model="detailData.expRecDisc11" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo11" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">12</span>
                        <va-input v-model="detailData.expRecDisc12" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo12" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs2">
                
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">13</span>
                        <va-input v-model="detailData.expRecDisc13" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo13" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">14</span>
                        <va-input v-model="detailData.expRecDisc14" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo14" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">15</span>
                        <va-input v-model="detailData.expRecDisc15" maxlength="4" style="width: 50px;padding: 0.1rem;" />
                        <va-input v-model="detailData.expRecNo15" maxlength="20" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>
            </div>
        </div>


        <div class="flex flex-col w-full ">
            <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">少額用他法令等入力項目</div>

            <div class="grid card place-items-left" style="border-radius: revert;">
                <!-- <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
                    <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">少額用他法令等入力項目</div>
                </div> -->
                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">他法令</span>
                        <va-input input-class="text-center" v-model="detailData.syogakuOtherLawCd1" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.syogakuOtherLawCd2" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.syogakuOtherLawCd3" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.syogakuOtherLawCd4" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                        <va-input input-class="text-center" v-model="detailData.syogakuOtherLawCd5" maxlength="2" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">輸出貿易管理令別表コード</span>
                        <va-input input-class="text-center" v-model="detailData.syogakuExpTradeContCd" maxlength="5" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">外為法第48条コード</span>
                        <va-input v-model="detailData.syogakuForExchLow48" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">関税減免令税コード</span>
                        <va-input v-model="detailData.syogakuCustomsExemptCd" maxlength="5" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>

                <div class="row justify-start" style="width: 100%;">
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">内国消費税免税コード</span>
                        <va-input v-model="detailData.syogakuInConsTaxExempCd" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                    </div>
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">内国消費税免税識別コード</span>
                        <va-input v-model="detailData.syogakuInConsTaxExempDisc" maxlength="1" style="width: 50px;padding: 0.1rem;" />
                    </div>
                </div>
        
            </div>
        </div>


        <div class="flex flex-col w-full ">
            <div class="grid card place-items-center" style="border:none;">
                <div class="row justify-end" style="width: 100%;">
                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="changeModalLayout"> MECへ変更 </va-button>
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
import winPopup from '../OC_CS_001_01/winPop.vue'
import Meisai from './meisai.vue'
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
        changeModalLayout:Function,
        detailData: Object,
        edaSubList: Array,
        cwbList: Array,
        insertModalData: Function,
        changeDetailDataEDA: Function,
        saveModalData: Function,
        prevFlg: Boolean,
        nextFlg: Boolean,
        disabledFlg: Boolean,
        disabledFlg2: Boolean,
    },
    components: {
        Datepicker,
        winPopup,
        Meisai,
        SearchMerchantModal,
        SearchConsignorConsigneeModal
    },
    data: function () {
        return {
            DatePickerFormat: 'yyyy-MM-dd',
            errorRowData: [],
            errorTitle: '電文エラー詳細',
            showErrorModalFlg: false,
            tmpSubList: [],
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
            console.log('test: ',this.detailData.pdfUrl)
            this.$refs.winPopup.openWinPop(uri, 1560, 700);
        },
        sendToChild(val,val2) {
            this.$refs.winPopup.sendEvtToChild(val, val2);
        }, 
        searchEdaData(evt) {
            this.changeDetailDataEDA(evt,this.detailData.awbNo, this.detailData.cwbNo)
        },
        moveNextOrPrevious(evt) {
            this.changeDetailDataEDA(evt, this.detailData.awbNo, this.detailData.cwbNo)
        }, 
        expReportPlanDatePickerClosedChange() {
            if (this.detailData.expReportPlanDate) {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.expReportPlanDate = moment(this.detailData.expReportPlanDate).format('YYYY-MM-DD')
            }
        },
        depPlaningDatePickerClosedChange() {
            if (this.detailData.depPlaningDate) {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.depPlaningDate = moment(this.detailData.depPlaningDate).format('YYYY-MM-DD')
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
            this.detailData.expCustomerNo = selectedRowData.customerNo
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerDeptCd = selectedRowData.deptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerOcsDeptCd = selectedRowData.ocsdeptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerName = selectedRowData.customerNamee
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expPostCode = selectedRowData.zipCd
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
        },
        addDiv() {
            const tmp = {
                cwbNo: this.detailData.cwbNo,
                seq: this.edaSubList.length + 1,
                expStatItemNo: null,
                naccsCd: null,
                item: null,
                amo1: null,
                amoUnit1: null,
                amoUnit_2: null,
                basicPriceDivCoef: null,
                basicPriceCurCd: null,
                basicPriceAmo: null,
                otherLawCd1: null,
                otherLawCd2: null,
                otherLawCd3: null,
                otherLawCd4: null,
                otherLawCd5: null,
                expTradeContCd: null,
                forexchLow48: null,
                customsExemptCd: null,
                inconsTaxExempCd: null,
                inconsTaxExempDisc: null
            }
            // eslint-disable-next-line vue/no-mutating-props
            this.edaSubList.push(tmp)
        },
        deleteSubDiv() {
            if (this.edaSubList.length > 0) { 
                // eslint-disable-next-line vue/no-mutating-props
                this.edaSubList.splice(this.edaSubList.length-1)
            }
        }
    }
};
</script>
<style>
span {
    max-width: fit-content;
    line-height: 2;
}
</style>