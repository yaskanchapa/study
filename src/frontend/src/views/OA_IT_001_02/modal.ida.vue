<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/valid-v-model -->
<!-- eslint-disable vue/no-parsing-error -->
<template>
    <div class="container" style="width:1400px;">

        <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">共通情報</div>

        <div class="flex w-full" style="height:fit-content">

            <div class="grid flex-grow card  place-items-center" style="border-radius: revert; max-width: 50%;">

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">MAWB No.</span>
                    </div>

                    <div class="item flex xs5">
                        <va-input v-model="detailData.awbNo" style="width: 150px;" placeholder="Text Input"  :disabled="true" />
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" title="イメージ" size="small" icon="mi-picture_as_pdf" @click="openWinPop" :disabled="detailData.pdfUrl == '' && detailData.pdfUrl2 == ''" ></va-button>
                        <!-- <va-button color="info" size="small" @click="openWinPop" :disabled="detailData.pdfUrl == '' || detailData.pdfUrl == null"> イメージ </va-button> -->
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" icon="mi-warning" title="エラー電文" size="small" :disabled="detailData.errorCnt == '0'" @click="showErrorModal()"></va-button>
                        <!-- <va-button color="info" size="small" :disabled="detailData.errorCnt == '0'" @click="showErrorModal()"> <ErrorIcon /> </va-button>     -->
                    </div>

                </div>

                <winPopup ref="winPopup" @onRecvEvtFromWinPop="val => onRecvWinPop(val)">
                </winPopup>
                
                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">HAWB No.</span>
                    </div>

                    <div class="item flex xs5">
                        <va-select v-model="detailData.cwbNo" style="width: 150;" placeholder="Text Input" :options="cwbList" />
                        <va-input maxlength="3" v-model="detailData.cwbNoDeptCd" style="width: 50px;" placeholder="Text Input" />
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="searchIdaData('S')"> 検索 </va-button>
                    </div>

                    <div class="item flex xs1">
                        <va-button color="info" icon="mi-arrow_back" size="small" @click="moveNextOrPrevious('P')" :disabled="prevFlg"></va-button>
                        <!-- <va-button color="info" size="small" @click="moveNextOrPrevious('P')" :disabled="prevFlg" > <ArrowLeft /> </va-button> -->
                    </div>

                    <div class="item flex xs1">
                        <va-button color="info" icon="mi-arrow_forward" size="small" @click="moveNextOrPrevious('N')" :disabled="nextFlg"></va-button>
                        <!-- <va-button color="info" size="small" @click="moveNextOrPrevious('N')" :disabled="nextFlg"> <ArrowRight /> </va-button> -->
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
                        <va-input type="textarea"  v-model="detailData.conditionComment" :disabled="true" class="mb-6"  />
                    </div>

                </div>

            </div>

        </div>

        <div class="flex flex-col w-full ">

            <div class="grid card place-items-left" style="border-radius: revert;">

                <div class="row justify-start" style="padding: 0.75rem;">

                    <div class="item flex xs2" align="left">
                        <span style="padding: 0.1rem;">貨物個数</span>
                        <va-input maxlength="10" mask="numeral" input-class="text-right" v-model="detailData.cargoPiece" style="width: 5px;" :disabled="disabledFlg == 1"  />
                    </div>

                    <div class="item flex xs2" align="left">
                    </div>

                    <div class="item flex xs3" align="left">
                        <span style="padding: 0.1rem;">貨物重量</span>
                        <va-input maxlength="13" mask="numeral" input-class="text-right" v-model="detailData.cargoWeight" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1"  />
                        <va-input maxlength="3"  v-model="detailData.weightUnitCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1"  />
                    </div>

                    <div class="item flex xs2" align="left">
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
                        <va-input maxlength="13" v-model="detailData.impCustomerNo" style="width: 80px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                        <va-input maxlength="4" v-model="detailData.impCustomerDeptCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                        <va-input maxlength="5" v-model="detailData.impCustomerOcsDeptCd" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <SearchMerchantModal color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionImp" :parentProps="parentPropsImp">
                            検索
                        </SearchMerchantModal>
                    </div>

                    <!-- 断面１では未使用  -->
                    <!-- <div class="item flex xs2">
                        <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1"> M登録 </va-button>
                    </div> -->

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <va-input maxlength="70" v-model="detailData.impCustomerName" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">〒</span>
                        <va-input maxlength="7" v-model="detailData.impPostcode" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">TEL</span>
                        <va-input maxlength="14" v-model="detailData.impCustomerTelNo" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">住所</span>
                        <va-input maxlength="15" v-model="detailData.impCustomerAdd1" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <va-input maxlength="35" v-model="detailData.impCustomerAdd2" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                    <div class="item flex xs6">
                        <va-input maxlength="35" v-model="detailData.impCustomerAdd3" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <va-input maxlength="70" v-model="detailData.impCustomerAdd4" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">取込</span>
                        <va-input maxlength="74" v-model="detailData.impCustomerAdd" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg2 == 1" />
                    </div>

                </div>

            </div>


            <div class="grid  flex-grow card place-items-center" style="border-radius: revert; max-width: 50%;">

                <div class="row justify-start" style="width: 100%;padding-bottom: 2px;">
                    <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">仕出人</div>
                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs8">
                        <va-input maxlength="8" v-model="detailData.expCustomerNo" style="width: 80px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                        <va-input maxlength="4" v-model="detailData.expCustomerDeptNo" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                        <va-input maxlength="5" v-model="detailData.expCustomerOCSDeptNo" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <SearchConsignorConsigneeModal color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg2 == 1" isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionCon" :parentProps="parentPropsExp">
                            検索
                        </SearchConsignorConsigneeModal>
                    </div>

                    <!-- 断面１では未使用  -->
                    <!-- <div class="item flex xs2">
                        <va-button color="info" size="small" :disabled="disabledFlg == 1 || disabledFlg3 == 1"> M登録 </va-button>
                    </div> -->
                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <va-input maxlength="70" v-model="detailData.expCustomerName" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">〒</span>
                        <va-input maxlength="9" v-model="detailData.expCustomerPostCode" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">国</span>
                        <va-input maxlength="2" v-model="detailData.expCustomerCountry" style="width: 60px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">住所</span>
                        <va-input maxlength="35" v-model="detailData.expCustomerAdd1" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs6">
                        <va-input maxlength="35" v-model="detailData.expCustomerAdd2" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                    <div class="item flex xs6">
                        <va-input maxlength="35" v-model="detailData.expCustomerAdd3" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <va-input maxlength="35" v-model="detailData.expCustomerAdd4" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">取込</span>
                        <va-input maxlength="80" v-model="detailData.expCustomerAdd" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg3 == 1"/>
                    </div>

                </div>

            </div>

        </div>


        <div class="flex flex-col w-full ">

            <div class="grid card place-items-center" style="border-radius: revert;">

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs5" align="left">
                        <span style="padding: 0.1rem;">輸入取引者</span>
                        <va-input maxlength="13" v-model="detailData.impDealingsCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1" />
                        <va-input maxlength="4" v-model="detailData.impDealingsDeptCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1"/>
                        <va-input maxlength="5" v-model="detailData.impDealingsOCSDeptCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1"/>
                    </div>

                    <div class="item flex xs1" style="padding: 0.1rem;" align="left">
                        <SearchMerchantModal isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchActionImp2"  color="info" size="small" :disabled="disabledFlg == 1">
                                検索
                        </SearchMerchantModal>
                    </div>

                    <div class="item flex xs5" align="left">
                        <va-input maxlength="70" v-model="detailData.impDealingsName" style="width:500px;padding: 0.1rem;" :disabled="disabledFlg == 1" />
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

                    <div class="grid card place-items-center" v-for="(idaSub, index) in idaSubList" :key="idaSub" style="border-radius: revert;">

                        <div class="row justify-start"  style="width: 100%; padding: 0.1rem;">
                            <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">{{ index + 1 }}欄</div>
                        </div>

                        <Meisai v-model:idaSub="idaSubList[index]" v-bind:disabledFlg="disabledFlg" v-bind:disabledFlg4="disabledFlg4" />
                    </div>

                </div>

                <div class="row justify-end" style="width: 100%;">

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="deleteSubDiv"> 削除  </va-button>
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="addDiv"> 追加 </va-button>
                    </div>

                </div>

            </div>

        </div>

        <div class="flex flex-col w-full ">

            <div class="grid card place-items-left" style="border-radius: revert;">

                <div class="row justify-start"  style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">仕入書識別</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.invoiceDiscernment" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">電子仕入書受付番号</span>
                        <va-input maxlength="10"  v-model="detailData.invoiceReceiptNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1" />
                    </div>

                    <div class="item flex xs6">
                        <span style="padding: 0.3rem;">仕入書番号</span>
                        <va-input maxlength="35" v-model="detailData.invoiceNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1" />
                    </div>

                </div>

                <div class="row justify-start"  style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">仕入書価格</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.invoiceValClassCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.invoiceValConCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.invoiceCurCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="detailData.invoiceValue" style="width: 300px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>
                    
                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs5">
                        <span style="padding: 0.3rem;">運賃</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.fareFlg" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.fareCurrencyCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="detailData.fare" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs5">
                        <span style="padding: 0.3rem;">保険</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.insuranceClassCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1" />
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.insuranceCurCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="18" mask="numeral" input-class="text-right" v-model="detailData.insuranceAmount" style="width: 200px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="8" input-class="text-center" v-model="detailData.incInsuRegNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">課税価格</span>
                        <va-input maxlength="8" mask="numeral" input-class="text-right" v-model="detailData.taxablesAmo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start"  style="width: 100%;">

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">評価</span>
                        <va-input maxlength="1" input-class="text-right" v-model="detailData.estimationFlgCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs5">
                        <span style="padding: 0.3rem;">包括評価番号</span>
                        <va-input maxlength="12" v-model="detailData.incEstRepReNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="12" v-model="detailData.incEstRepReNo2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="12" v-model="detailData.incEstRepReNo3" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs5">
                        <span style="padding: 0.3rem;">補正</span>
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.incReviseFlgCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="3" input-class="text-center" v-model="detailData.incRevBaseCurCD" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="detailData.incRevBaseAmo" style="width: 150px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="11" input-class="text-center" v-model="detailData.incRevBase" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start"  style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">BPR係数合計</span>
                        <va-input maxlength="24" mask="numeral" input-class="text-right" v-model="detailData.taxablesAmoPdivTo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">搬入予定</span>
                        <va-input maxlength="5" v-model="detailData.longKeepBondedWarehouse" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">納期限延長</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.deliveryDateExtCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">BP申告理由</span>
                        <va-input maxlength="2" input-class="text-center" v-model="detailData.bpApplicationCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>
                    
                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">納付方法</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.payMethodDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>
                    
                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">口座番号</span>
                        <va-input maxlength="14" v-model="detailData.accountNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">担保番号</span>
                        <va-input maxlength="9" v-model="detailData.securityRegNo1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="9" v-model="detailData.securityRegNo2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">大額/少額識別</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.bigSmallFlg" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">申告等種別</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.reportKindCD1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">申告先種別</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.reportKindCD2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">貨物識別</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.cargoDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">識別符号</span>
                        <va-input maxlength="1" input-class="text-center" v-model="detailData.discernmentMark" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">あて先官署</span>
                        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDivCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">あて先部門</span>
                        <va-input maxlength="2" input-class="text-center" v-model="detailData.reportDepCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">申告予定年月日</span>
                        <Datepicker v-model="detailData.reportPlaningDate" :auto-apply="true" @closed="reportPlaningDatePickerClosedChange"
                        :format="DatePickerFormat" :enable-time-picker="false" style="width: 120px;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">積載機名</span>
                        <va-input maxlength="6" v-model="detailData.currentArrFLT1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <span style="padding: 0.3rem;">/</span>
                        <va-input maxlength="5" v-model="detailData.currentArrFLT2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">入港年月日</span>
                        <Datepicker v-model="detailData.arrPortDate" :auto-apply="true" @closed="arrPortDatePickerClosedChange"
                            :format="DatePickerFormat" :enable-time-picker="false" style="width: 120px;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">取卸港</span>
                        <va-input maxlength="3" v-model="detailData.getPort" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">積出地</span>
                        <va-input maxlength="5" v-model="detailData.shipmentCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.shipmentPlaceName" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">貿易形態別符号</span>
                        <va-input maxlength="3" v-model="detailData.tradeTypeMark" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">戻税申告</span>
                        <va-input maxlength="1" v-model="detailData.backTaxReportDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">貿易管理令</span>
                        <va-input maxlength="1" v-model="detailData.impTradeCont3Disc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">輸入承認証</span>
                        <va-input maxlength="1" v-model="detailData.impRecoAttachDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">内容点検結果</span>
                        <va-input maxlength="1" v-model="detailData.contCheckReOther" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">調査用符号</span>
                        <va-input maxlength="5" v-model="detailData.customsExamMark" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs8">
                        <span style="padding: 0.3rem;">他法令</span>
                        <va-input maxlength="2" v-model="detailData.otherLaw1" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="2" v-model="detailData.otherLaw2" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="2" v-model="detailData.otherLaw3" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="2" v-model="detailData.otherLaw4" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="2" v-model="detailData.otherLaw5" style="width: 5px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">共通管理番号</span>
                        <va-input maxlength="10" v-model="detailData.otherMiniContNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">食品</span>
                        <va-input maxlength="1" v-model="detailData.fhProofDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>
                    
                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">植防</span>
                        <va-input maxlength="1" v-model="detailData.plantPEDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs4">
                        <span style="padding: 0.3rem;">動検</span>
                        <va-input maxlength="1" v-model="detailData.animalQuaraDisc" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs9">
                        <span style="padding: 0.3rem;">税関事務管理人</span>
                        <va-input maxlength="17" v-model="detailData.customsOfficeJanitorCd" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="70" v-model="detailData.customsOfficeJanitorName" style="width: 500px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">受理番号</span>
                        <va-input maxlength="10" v-model="detailData.customsOfficeJanitorReNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">検査立会者</span>
                        <va-input maxlength="5" v-model="detailData.inspectionWitness" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">荷主セクションコード</span>
                        <va-input maxlength="20" v-model="detailData.shippersSecCd" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">荷主リファレンスナンバー</span>
                        <va-input maxlength="35" v-model="detailData.shippersRefNo" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">事前教示（評価）</span>
                        <va-input maxlength="7" v-model="detailData.advanceRulingValuation1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="7" v-model="detailData.advanceRulingValuation2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                        <span style="padding: 0.3rem;">輸入承認証番号等</span>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">1</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo1" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">2</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo2" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">3</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc3" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo3" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">4</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc4" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo4" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">5</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc5" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo5" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">6</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc6" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo6" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">7</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc7" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo7" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">8</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc8" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo8" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">9</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc9" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo9" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs2">
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">10</span>
                        <va-input maxlength="4" v-model="detailData.otherLawRecDisc10" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                        <va-input maxlength="20" v-model="detailData.otherLawRecNo10" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">蔵置場所</span>
                        <va-input maxlength="5" v-model="detailData.bondedWarehouseCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                    <div class="item flex xs3">
                        <span style="padding: 0.3rem;">申告予定者</span>
                        <va-input maxlength="5" v-model="detailData.reportPersonCD" style="width: 50px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">記事（税関）</span>
                        <va-input maxlength="140" v-model="detailData.news1" style="width: 500px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">記事（通関業者）</span>
                        <va-input maxlength="70" v-model="detailData.news2" style="width: 500px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">記事（荷主）</span>
                        <va-input maxlength="70" v-model="detailData.newsShipper" style="width: 500px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

                <div class="row justify-start" style="width: 100%;">

                    <div class="item flex xs12">
                        <span style="padding: 0.3rem;">社内整理番号</span>
                        <va-input maxlength="20" v-model="detailData.inHouseRefNumber" style="width: 500px;padding: 0.1rem;" :disabled="disabledFlg == 1 || disabledFlg5 == 1"/>
                    </div>

                </div>

            </div>

        </div>

        <div class="flex flex-col w-full ">

            <div class="grid card place-items-center" style="border:none;">

                <div class="row justify-end" style="width: 100%;">

                    <div class="item flex xs2">
                        <!-- <va-checkbox v-model="idaChecked" :label="idaLabel" right-label @click="onChangeIdaCheck" :disabled="disabledFlg == 1"/> -->
                        <va-checkbox v-model="idaChecked" :label="idaLabel" right-label :disabled="disabledFlg == 1"/>
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="changeModalLayout" :disabled="disabledFlg == 1"> MICへ変更 </va-button>
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="saveModalData" :disabled="disabledFlg == 1"> 保存 </va-button>
                    </div>

                    <div class="item flex xs2">
                        <va-button color="info" size="small" @click="updateData" :disabled="disabledFlg == 1"> 登録 </va-button>
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
                this.parentPropsImp.customerNamee = newValue.impCustomerName
                this.parentPropsExp.customerNamee = newValue.expCustomerName
            }
        )
    },
    props: {
        changeModalLayout:Function,
        detailData: Object,
        idaSubList: Array,
        cwbList: Array,
        insertModalData: Function,
        changeDetailDataIDA: Function,
        onChangeIdaCheck: Function,
        writeIDA: Function,
        saveModalData: Function,
        prevFlg: Boolean,
        nextFlg: Boolean,
        disabledFlg: String,
    },
    components: {
        winPopup,
        Meisai,
        Datepicker,
        SearchMerchantModal,
        SearchConsignorConsigneeModal
    },
    data: function () {
        return {
            idaLabel: 'IDA作成',
            idaChecked: false,
            // disabledFlg: 0,
            disabledFlg2: 0,
            disabledFlg3: 0,
            disabledFlg4: 0,
            disabledFlg5: 0,
            DatePickerFormat: 'yyyy-MM-dd',
            showErrorModalFlg: false,
            errorRowData: [],
            errorTitle: '電文エラー詳細',
            modalValue1: '',
            parentPropsExp: {
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
    filters: {
        toLocale(num) { 
            return num.toLocaleString()
        }
    },
    methods: {
        updateData() { 
            this.insertModalData(this.idaChecked)
        },
        openWinPop() {
            const uri = '/pdfViewer?param1=' + this.detailData.pdfUrl + '&param2=' + this.detailData.pdfUrl2
            this.$refs.winPopup.openWinPop(uri, 1560, 700);
        },
        sendToChild(val,val2) {
            this.$refs.winPopup.sendEvtToChild(val,val2);
        }, 
        searchIdaData(evt) {
            this.changeDetailDataIDA(evt, this.detailData.awbNo, this.detailData.cwbNo)
        },
        moveNextOrPrevious(evt) {
            this.changeDetailDataIDA(evt, this.detailData.awbNo, this.detailData.cwbNo)
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
            this.detailData.impCustomerNo = selectedRowData.customerNo
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impCustomerDeptCd = selectedRowData.deptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impCustomerOcsDeptCd = selectedRowData.ocsdeptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impCustomerName = selectedRowData.customerNamee
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impPostcode = selectedRowData.zipCd
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

            if (selectedRowData.deliveryDateExtCd !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.deliveryDateExtCD = selectedRowData.deliveryDateExtCd
            }
            if (selectedRowData.paymethodDisc !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.payMethodDisc = selectedRowData.paymethodDisc
            }
            if (selectedRowData.conditionBankAccountNo !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.accountNo = selectedRowData.conditionBankAccountNo
            }
            if (selectedRowData.conditionCollateralNo1 !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.securityRegNo1 = selectedRowData.conditionCollateralNo1
            }
            if (selectedRowData.conditionCollateralNo2 !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.securityRegNo2 = selectedRowData.conditionCollateralNo2
            }
            if (selectedRowData.estimationFlgCd !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.estimationFlgCD = selectedRowData.estimationFlgCd
            }
            if (selectedRowData.incestrepreNo !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.incEstRepReNo = selectedRowData.incestrepreNo
            }
            if (selectedRowData.incestrepreNo2 !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.incEstRepReNo2 = selectedRowData.incestrepreNo2
            }
            if (selectedRowData.incestrepreNo3 !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.incEstRepReNo3 = selectedRowData.incestrepreNo3
            }
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
                this.detailData.insuranceClassCD = selectedRowData.insuranceClassCd
            }
            if (selectedRowData.incinsuregNo !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.incInsuRegNo = selectedRowData.incinsuregNo
            }
            if (selectedRowData.conditionComment !== '') {
                // eslint-disable-next-line vue/no-mutating-props
                this.detailData.conditionComment = selectedRowData.conditionComment
            }

        },
        handleClickToSearchActionImp2(selectedRowData) {
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsCD = ''
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsDeptCD = ''
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsOCSDeptCD = ''
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsName = ''

            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsCD = selectedRowData.customerNo
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsDeptCD = selectedRowData.deptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsOCSDeptCD = selectedRowData.ocsdeptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.impDealingsName = selectedRowData.customerNamee
        },
        handleClickToSearchActionCon(selectedRowData) {
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerNo = ''
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerDeptNo = ''
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerOCSDeptNo = ''
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
            this.detailData.expCustomerNo = selectedRowData.customerNo
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerDeptNo = selectedRowData.deptCd
            // eslint-disable-next-line vue/no-mutating-props
            this.detailData.expCustomerOCSDeptNo = selectedRowData.ocsdeptCd
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
            
        },
        addDiv() {
            const tmp = {
                awbNo: this.detailData.awbNo,
                cwbNo: this.detailData.cwbNo,
                cwbNoDeptCd: this.detailData.cwbNoDeptCd,
                seq: this.idaSubList.length+1,
                itemCD: null,
                naccsCD: null,
                item: null,
                countryOfOriginCD: null,
                originCertifiDisc: null,
                amo_1: null,
                amoUnit_1: null,
                amo_2: null,
                amoUnit_2: null,
                impTradeContCD: null,
                taxablesAmoPdivTo: null,
                fareDivDisc: null,
                fobCurrencyCD: null,
                taxablesAmo: null,
                customsExemptCD: null,
                customsExemptAmo: null,
                advanceRulingClassification: null,
                advanceRulingOrigin: null,
                inConsTaxKindCD1: null,
                inConsTaxExemCD1: null,
                inConsTaxExemAmo1: null,
                inConsTaxKindCD2: null,
                inConsTaxExemCD2: null,
                inConsTaxExemAmo2: null,
                inConsTaxKindCD3: null,
                inConsTaxExemCD3: null,
                inConsTaxExemAmo3: null,
                inConsTaxKindCD4: null,
                inConsTaxExemCD4: null,
                inConsTaxExemAmo4: null,
                inConsTaxKindCD5: null,
                inConsTaxExemCD5: null,
                inConsTaxExemAmo5: null,
                inConsTaxKindCD6: null,
                inConsTaxExemCD6: null,
                inConsTaxExemAmo6: null
            }
            // eslint-disable-next-line vue/no-mutating-props
            this.idaSubList.push(tmp)
        },
        deleteSubDiv() {
            if (this.idaSubList.length > 0) {
                // eslint-disable-next-line vue/no-mutating-props
                this.idaSubList.splice(this.idaSubList.length - 1)
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