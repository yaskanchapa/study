<template>
    <va-card>
        <div class="row justify-start items-center" style="padding: 0.75rem">
            <div class="item flex xs5">
                <span style="padding: 0.1rem" class="mr-2" >対象日</span>
                <span v-for="(option, index) in initRdoObject" :key="index" >
                    <input class="xs5" name="rdoObject" type="radio" v-model="searchOption" :value="index" :id="'rdoObject-' + option" />
                    <label class="xs5" :for="'rdoObject-' + option" @click="convertOption">
                        {{ option }}
                    </label>
                </span>
            </div>
            <div class="flex xs3">
                <Datepicker v-model="searchCondition.targetStartDate"
                :auto-apply="true"
                @closed="arrPortDatePickerClosedChange"
                :format="DatePickerFormat"
                :enable-time-picker="false"
                :disabled="isLoading"
                />
                <span class="mx-2"> ~ </span>
                <Datepicker v-model="searchCondition.targetEndDate"
                :auto-apply="true"
                @closed="arrPortDatePickerClosedChange"
                :format="DatePickerFormat"
                :enable-time-picker="false"
                :disabled="isLoading"
                />
            </div>
            <div class="item flex xs3">
              <span style="min-width: 40%">蔵置場</span>  <!-- メモ：optionsの記述がコンボボックスリストに表示される記述 -->
              <va-select 
                placeholder="--Please choose an option--"
                v-model="searchCondition.cargoName" 
                :options="initData?.cargoName"  
                :text-by="(option) => option.value + ' : ' + option.name"
                :track-by="(option) => option.value + ' : ' + option.name"
                :value-by="(option) => option.value"
                :disabled="bondedWareHouseCdAuthCheck"
              /> 
            </div>
        </div>
        <div class="row justify-start items-center" style="padding: 0.75rem">
            <div class="item flex xs3">
                <span style="padding: 0.3rem;">到着便名</span>
                <va-input style="width: 80px;" v-model.trim="searchCondition.currentArrFlt1"/>
                <span class="mx-3"> ／ </span>
                <va-input style="width: 80px;" v-model.trim="searchCondition.currentArrFlt2"/>
            </div>
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">ORIGIN</span>
                <va-input style="width: 80px;"  v-model.trim="searchCondition.org"/>
            </div>
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">MAWB No.</span>
                <va-input style="width: 100px;" v-model.trim="searchCondition.awbNo"/>
            </div>
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">HAWB No.</span>
                <va-input style="width: 110px;" v-model.trim="searchCondition.cwbNo"/>
            </div>
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">貨物STS</span>
                <va-select style="width: 150px;" 
                 placeholder="--Please choose an option--" 
                 v-model="searchCondition.cargoStatus" 
                 :options="initData?.cargoStatus"
                 :text-by="(option) => option.value + ' : ' + option.name"
                 :track-by="(option) => option.value + ' : ' + option.name"
                 :value-by="(option) => option.value"
                 />
                
            </div>
        </div>   
        <div class="row justify-start items-center" style="padding: 0.75rem">
            <div class="item flex xs2">
                <span style="padding: 0.2rem;">仕分条件</span>
                <va-select style="width: 150px;"
                 placeholder="--Please choose an option--"
                 v-model="searchCondition.inClassifyClassName"
                 :options="initData?.inClassifyClassName"
                 :text-by="(option) => option.value + ' : ' + option.name"
                 :track-by="(option) => option.value + ' : ' + option.name"
                 :value-by="(option) => option.value"
                />
            </div>
            <div class="item flex xs4">
            <span style="padding: 0.3rem;">INVアンマッチ</span> 
            <va-switch :disable="isLoading" v-model="searchCondition.invHwbNo"  true-inner-label="HAWB件数" false-inner-label="HAWB件数"
              size="small" />
            <va-switch :disable="isLoading" v-model="searchCondition.invCargoPiece" true-inner-label="個数" false-inner-label="個数"
              size="small" />
            <va-switch :disable="isLoading" v-model="searchCondition.invMatch" true-inner-label="マッチ" false-inner-label="マッチ"
              size="small" />
            </div> 
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">ロケ</span>
                <va-input style="width: 80px;" v-model="searchCondition.locateFrom" />
                <span class="mx-1"> ～ </span>
                <va-input style="width: 80px;" v-model="searchCondition.locateTo" />
            </div>
            <div class="item flex xs2">
            <span style="padding: 0.2rem;">申告区分</span> 
            <va-switch :disable="isLoading" v-model="searchCondition.permitNormal" true-inner-label="一般" false-inner-label="一般"
              size="small" />
            <va-switch :disable="isLoading" v-model="searchCondition.permitMatch" true-inner-label="マニ" false-inner-label="マニ"
              size="small" />
            </div>
            <div class="item flex xs2">
                <span style="padding: 0.3rem;">許可区分</span>
                <va-select style="width: 150px;"
                 placeholder="--Please choose an option--"
                 v-model="searchCondition.permitClassCd"
                 :options="initData?.permitClassCd"
                 :text-by="(option) => option.value + ' : ' + option.name"
                 :track-by="(option) => option.value + ' : ' + option.name"
                 :value-by="(option) => option.value"
                />     
            </div>
        </div>
        <div class="row justify-end" style="padding: 0.5rem;">
          <div class="item flex xs1">
            <va-button color="info" size="medium" @click="handleSearchBtnClicked"> 対象表示 </va-button>
          </div>  
        </div>    

    </va-card>
</template>
<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { useVuelidate } from '@vuelidate/core'
import { notificationError } from '@/components/Notification/NotificationApi.vue'
import { requiredRules, fieldInputTextValidate, ONE_BYTE_REGEX } from '@/utils/validator' // fieldInputTextValidate, ONE_BYTE_REGEX 
export default {
    components:{
        Datepicker, // eslint-disable-line
    },
    props:{
        initData: Object,
        searchImportData: Function,
        countRecord: Number,
        isLoading: Boolean,
    },
    mounted() {
            if (this.searchCondition.searchOption === 0) {
                this.searchCondition.searchOption = "searchArr";
            }
    },
    watch: {
        searchOption(newVal) {
            this.searchCondition.searchOption = newVal;
            if(this.searchCondition.searchOption === 0){
                this.searchCondition.searchOption = "searchArr";
            }else if(this.searchCondition.searchOption === 1){
                this.searchCondition.searchOption = "searchInFlight";
            }else if(this.searchCondition.searchOption === 2){
                this.searchCondition.searchOption = "searchHPK";
            }else if(this.searchCondition.searchOption === 3){
                this.searchCondition.searchOption = "searchInventory";
            }else if(this.searchCondition.searchOption === 4){
                this.searchCondition.searchOption = "searchPermit";
            }else if(this.searchCondition.searchOption === 5){
                this.searchCondition.searchOption = "searchCarryout";
            }else if(this.searchCondition.searchOption === 6){
                this.searchCondition.searchOption = "searchOut";
            }else if(this.searchCondition.searchOption === 7){
                this.searchCondition.searchOption = "searchHdnUpdateDate";
            }
         }  
        },
    data() {
        return {
            initRdoObject:['到着','搬入','HPK','インベントリー','許可','搬出','OUT','日報'],
            searchOption: 0,
            TOGGLE: {
                TRUE: '1',
                FALSE: '0',
            },
            v$: useVuelidate(),
            searchCondition:{
                // radioBox search
                searchOption: 0,
                // コンボボックスに必要記述
                targetStartDate: moment().format('YYYY-MM-DD'),
                targetEndDate: moment().format('YYYY-MM-DD'),
                // 蔵置場
                cargoName: "",
                // // 到着便名1
                currentArrFlt1:"",
                // // 到着便名2
                currentArrFlt2:"",                
                // // オリジン
                org: "",
                // // MAWB番号
                awbNo: "",
                // // HAWBNo
                cwbNo:"",
                // // 借物STS
                cargoStatus: "",
                // // 仕分条件
                inClassifyClassName: "",
                // // INV HAWB件数
                invHwbNo:"false",
                // // INV 個数 マッチ
                invCargoPiece: "false",
                // // INV アンマッチ
                invMatch: "false",
                // // ロケーション1(要確認)
                locateFrom: "",
                // // ロケーション2(要確認)
                locateTo: "",
                // // 申告区分
                permitNormal: "false",
                // // 申告区分
                permitMatch: "false",
                // // 許可区分
                permitClassCd: "",

            },
            agentAuthCheck: false,
            bondedWareHouseCdAuthCheck: false,
            DatePickerFormat: 'yyyy-MM-dd',
        }
    },

    methods: {     
        checkData(){
            console.log(this.searchCondition);
        },
        arrPortDatePickerClosedChange(){
            if (this.searchCondition.targetStartDate){
                this.searchCondition.targetStartDate = moment (this.searchCondition.targetStartDate).format('YYYY-MM-DD')
            }
             if (this.searchCondition.targetEndDate){
                this.searchCondition.targetEndDate = moment (this.searchCondition.targetEndDate).format('YYYY-MM-DD')
            }
        },
        async handleSearchBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.$emit('searchImportData', this.searchCondition)
            } else {
                if (this.v$.searchCondition.targetStartDate.$invalid) {
                    notificationError('日付を入力してください。')
                    return
                }
                else {
                    if (this.v$.searchCondition.targetEndDate.$invalid) {
                        notificationError('日付を入力してください。')
                        return
                    }
                }
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },
    },
    validations() {
        return {
            searchCondition: {
                targetStartDate: { required: requiredRules('日付') },
                targetEndDate: { required: requiredRules('日付') },
                destination: { regex: fieldInputTextValidate('仕向地', ONE_BYTE_REGEX) },
                awbno: { regex: fieldInputTextValidate('MAWB No.', ONE_BYTE_REGEX) },
                cwbno: { regex: fieldInputTextValidate('HAWB No.', ONE_BYTE_REGEX) },
            },
        }
    },
}
</script>