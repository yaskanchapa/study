<template>
    <va-card>
        <div class="row justify-start items-center px-3 pt-1 pb-[1px]">
            <div class="flex items-center xs2">
                <span class="px-1 min-w-[40%]">HAWB No.</span>
                <va-input ref="hawbNo" v-model.trim="searchCondition.hawbNo" :maxlength="50" :error="v$.searchCondition.hawbNo.$error" />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">通関STS</span>
                <va-select
                    v-model.trim="searchCondition.currentCustomsStatusCd"
                    :options="initData?.listCustomsClearanceSTS"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">通関業者</span>
                <va-select
                    v-model.trim="searchCondition.customsTraderCd"
                    :options="initData?.listCustomsBroker"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 35%">HPK</span>
                <va-switch v-model.trim="searchCondition.hpkFlg01" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" true-inner-label="未" false-inner-label="未" size="small" />
                <va-switch v-model.trim="searchCondition.hpkFlg02" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" true-inner-label="済" false-inner-label="済" size="small" />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 45%">申告書発行</span>
                <va-switch v-model.trim="searchCondition.declarationOutput01" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-7">
                            {{ initData?.textDeclarationOutput01 || '未' }}
                        </div>
                    </template>
                </va-switch>
                <va-switch v-model.trim="searchCondition.declarationOutput02" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-7">
                            {{ initData?.textDeclarationOutput02 || '済' }}
                        </div>
                    </template>
                </va-switch>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.hawbNo.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 40%">MAWB No. </span>
                <va-input ref="mawbNo" v-model.trim="searchCondition.mawbNo" :maxlength="50" :error="v$.searchCondition.mawbNo.$error" />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">書類STS</span>
                <va-select
                    v-model.trim="searchCondition.currentDocStatusCd"
                    :options="initData?.listDocumentSTS"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">通関場所</span>
                <va-input v-model.trim="searchCondition.customSplaceCd" :maxlength="20" />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 35%">申告</span>
                <va-switch
                    v-model.trim="searchCondition.reportFlg01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textReportFlg01 || '未'"
                    :false-inner-label="initData?.textReportFlg01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.reportFlg02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textReportFlg02 || '済'"
                    :false-inner-label="initData?.textReportFlg02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 45%">イメージ</span>
                <va-switch
                    v-model.trim="searchCondition.image01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textImage01 || '有り'"
                    :false-inner-label="initData?.textImage01 || '有り'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.image02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textImage02 || '無し'"
                    :false-inner-label="initData?.textImage02 || '無し'"
                    size="small"
                />
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.mawbNo.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 40%">Origin</span>
                <va-input ref="origin" v-model.trim="searchCondition.origin" style="max-width: 60%" :maxlength="3" :error="v$.searchCondition.origin.$error" />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">賃物STS </span>
                <va-select
                    v-model.trim="searchCondition.currentCargoStatusCd"
                    :options="initData?.listRentSTS"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">許可区分</span>
                <va-select
                    v-model.trim="searchCondition.permitClassCd"
                    :options="initData?.listPermitCategory"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 35%">許可</span>
                <va-switch
                    v-model.trim="searchCondition.permitFlg01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textPermitFlg01 || '未'"
                    :false-inner-label="initData?.textPermitFlg01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.permitFlg02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textPermitFlg02 || '済'"
                    :false-inner-label="initData?.textPermitFlg02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 45%">申告種別</span>
                <va-switch v-model.trim="searchCondition.reportMic" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-7">
                            {{ initData?.textReportMic.normalize('NFKC') || 'MIC' }}
                        </div>
                    </template>
                </va-switch>
                <va-switch v-model.trim="searchCondition.reportIda" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-7">
                            {{ initData?.textReportIda.normalize('NFKC') || 'IDA' }}
                        </div>
                    </template>
                </va-switch>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.origin.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 40%">到着便名</span>
                <va-input ref="currentArrflt" v-model.trim="searchCondition.currentArrflt" :maxlength="10" :error="v$.searchCondition.currentArrflt.$error" />
            </div>
            <div class="flex items-center xs3">
                <span style="padding: 0.3rem; min-width: 23%">品名</span>
                <va-input v-model.trim="searchCondition.itemName" :maxlength="255" />
            </div>
            <div class="flex items-center xs3">
                <span class="px-1">提出有無</span>
                <va-select
                    v-model.trim="searchCondition.permitClass"
                    :options="initData?.listSubmission"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 35%">C4登録</span>
                <va-switch
                    v-model.trim="searchCondition.c4regFlg01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textC4regFlg01 || '未'"
                    :false-inner-label="initData?.textC4regFlg01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.c4regFlg02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textC4regFlg02 || '済'"
                    :false-inner-label="initData?.textC4regFlg02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 45%">内点/確認書</span>
                <va-switch
                    v-model.trim="searchCondition.certificate01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textCertificate01 || '有り'"
                    :false-inner-label="initData?.textCertificate01 || '有り'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.certificate02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textCertificate02 || '無し'"
                    :false-inner-label="initData?.textCertificate02 || '無し'"
                    size="small"
                />
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs2">
                <span style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.currentArrflt.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center" style="max-width: 25%">
                <span class="px-1 mr-4">対象日</span>
                <span v-for="(option, index) in radioOptions" :key="index">
                    <input name="objectDate" type="radio" v-model.trim="searchCondition.objectDate" :value="index" :id="'objectDate-' + option" />
                    <label class="mx-1" :for="'objectDate-' + option">
                        {{ option }}
                    </label>
                </span>
            </div>
            <div class="flex items-center xs5">
                <Datepicker
                    ref="objectDateFrom"
                    v-model.trim="searchCondition.objectDateFrom"
                    :auto-apply="true"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    :clearable="false"
                    :state="v$.searchCondition.objectDateFrom.$invalid ? false : null"
                />
                <span class="mx-2"> ~ </span>
                <Datepicker
                    ref="objectDateTo"
                    v-model.trim="searchCondition.objectDateTo"
                    :auto-apply="true"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    :clearable="false"
                    :state="v$.searchCondition.objectDateTo.$invalid ? false : null"
                />
                <va-input
                    ref="reportNo"
                    v-model.trim="searchCondition.reportNo"
                    input-class="hide-input-number-arrow"
                    class="pl-2"
                    style="max-width: 30%"
                    placeholder="申告番号"
                    :maxlength="20"
                    :error="v$.searchCondition.reportNo.$error"
                />
            </div>

            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; max-width: 35%">3点SET</span>
                <va-switch
                    ref="idaFlg"
                    v-model.trim="searchCondition.set"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textSet"
                    :false-inner-label="initData?.textSet || '無'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="padding: 0.3rem; min-width: 45%">少額/大額</span>
                <va-switch
                    v-model.trim="searchCondition.bigSmallFlg01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textBigSmallFlg01 || '少額'"
                    :false-inner-label="initData?.textBigSmallFlg01 || '少額'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.bigSmallFlg02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.textBigSmallFlg02 || '大額'"
                    :false-inner-label="initData?.textBigSmallFlg02 || '大額'"
                    size="small"
                />
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center" style="max-width: 25%"></div>
            <div class="flex items-center xs3"></div>
            <div class="flex items-center xs3">
                <span style="max-width: 13%"></span>
                <span style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.reportNo.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-[1px]">
            <div class="flex items-center xs5">
                <span class="px-1">輸入者</span>
                <va-input ref="customerNo" v-model.trim="searchCustomer.customerNo" class="pr-1" style="max-width: 19%" :maxlength="255" />
                <va-input ref="deptCd" v-model.trim="searchCustomer.deptCd" class="pr-1" style="max-width: 15%" :maxlength="255" />
                <va-input ref="ocsDeptCd" v-model.trim="searchCustomer.ocsDeptCd" class="pr-1" style="max-width: 15%" :maxlength="255" />
                <va-button @click="clickSearchCustomerName" color="info" size="medium" class="mx-1">表示</va-button>
                <SearchMerchantModal isModal enableClickToSearch @handleClickToSearchAction="handleClickToSearchAction" :parentProps="searchCustomer">
                    <va-icon name="search" size="medium" />
                </SearchMerchantModal>
            </div>
            <div class="flex items-center xs4">
                <span style="padding: 0.3rem; width: 90px">代理店</span>
                <va-select
                    v-model.trim="searchCondition.agencyCmb"
                    :disabled="agentAuthCheck"
                    :options="initData?.listAgencySelection"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
                <va-input v-model.trim="searchCondition.agencyInput" class="ml-1" style="max-width: 150px" :maxlength="50" />
            </div>
            <div class="flex items-center xs3">
                <span style="padding: 0.3rem; min-width: 35%">HAWB件数</span>
                <va-input :model-value="numberArrHawb" type="text" readonly class="pr-1" input-class="text-right" :maxlength="10" />
            </div>
        </div>
        <span style="margin-left: 105px">
            {{ searchCustomerName }}
        </span>
        <div class="row justify-end px-[1.05rem] pb-1">
            <!-- 情報表示 -->
            <HeaderComponent :resultData="resultData" @handleSearchBtnClicked="handleSearchBtnClicked" />
        </div>
    </va-card>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { useVuelidate } from '@vuelidate/core'
import { notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue'
import { requiredRules, fieldInputTextValidate, ONE_BYTE_REGEX, NUMBER_REGEX } from '@/utils/validator'
import HeaderComponent from './HeaderInformation.vue'
import SearchMerchantModal from '../../components/SearchModal/SearchMerchantModal.vue'
import { containsObject, DATE_PICKER_FORMAT, NOTIFICATION_DURATION, currentDateTime } from '@/utils/utilities'
import { NO_RESULT_FOUND_MESSAGE, SEARCH_ERROR_MESSAGE, MISSING_DATE_MESSAGE } from '@/utils/messages'

export default {
    components: {
        Datepicker,
        HeaderComponent,
        SearchMerchantModal,
    },

    props: {
        changeLoading: Function,
        searchImportData: Function,
        clearResult: Function,
        initData: Object,
        numberArrHawb: Number,
        resultData: Object,
        agentAuthCheck: Boolean,
    },

    data() {
        return {
            TOGGLE: {
                TRUE: '1',
                FALSE: '0',
            },
            radioOptions: ['到着', '搬入', '申告', '許可', '搬出'],
            searchCustomerName: null,
            searchCustomer: {
                customerNo: '',
                deptCd: '',
                ocsDeptCd: '',
            },
            v$: useVuelidate(),
            searchCondition: {
                hawbNo: '',
                mawbNo: '',
                origin: '',
                currentArrflt: '',
                currentCustomsStatusCd: '',
                currentDocStatusCd: '',
                currentCargoStatusCd: '',
                itemName: '',
                customsTraderCd: '',
                customSplaceCd: '',
                permitClassCd: '',
                permitClass: '',
                objectDate: '0',
                objectDateFrom: currentDateTime(),
                objectDateTo: currentDateTime(),
                reportNo: '',
                hpkFlg01: '0',
                hpkFlg02: '0',
                reportFlg01: '0',
                reportFlg02: '0',
                permitFlg01: '0',
                permitFlg02: '0',
                c4regFlg01: '0',
                c4regFlg02: '0',
                set: '0',
                declarationOutput01: '1',
                declarationOutput02: '0',
                image01: '1',
                image02: '1',
                reportMic: '1',
                reportIda: '1',
                certificate01: '0',
                certificate02: '0',
                bigSmallFlg01: '0',
                bigSmallFlg02: '0',
                optional01: '0',
                optional02: '0',
                agencyCmb: '',
                agencyInput: '',

                customerNo: '',
                customerDeptCd: '',
                customerOcsDeptCd: '',

                hawbNoCmb: '',
                arrHawb: '',
            },

            DatePickerFormat: DATE_PICKER_FORMAT,
        }
    },

    watch: {
        initData() {
            const userCustomsTraderCd = this.initData?.textCustomsTraderCd
            const userCustomSplaceCd = this.initData?.textCustomSplaceCd

            const listAgencySelection = this.initData?.listAgencySelection

            if (this.agentAuthCheck) {
                if (containsObject(userCustomSplaceCd, listAgencySelection)) {
                    this.searchCondition.agencyCmb = userCustomSplaceCd
                }
            }

            this.searchCondition.customsTraderCd = userCustomsTraderCd
            this.searchCondition.customSplaceCd = userCustomSplaceCd

            if (this.initData.objectDate) {
                this.radioOptions = this.initData.objectDate
            }
        },
    },

    methods: {
        refocusAfterSearch() {
            this.$refs.hawbNo.focus()
        },

        clickSearchCustomerName() {
            this.v$.$validate()
            this.searchCustomerName = null
            if (this.v$.searchCustomer.$invalid) {
                this.$refs[this.v$.$errors[0].$property].focus()
                notificationError(this.v$.$errors[0].$message)
            } else {
                this.changeLoading()
                this.axios({
                    url: 'api/oait002/get-name-am-customer-number',
                    method: 'get',
                    params: this.searchCustomer,
                })
                    .then((res) => {
                        this.searchCustomerName = res.data.customerName
                        if (this.searchCustomerName === null) notificationWarn(NO_RESULT_FOUND_MESSAGE, '', NOTIFICATION_DURATION)
                    })
                    .catch((error) => {
                        notificationError(SEARCH_ERROR_MESSAGE, error.message)
                    })
                    .finally(() => {
                        this.changeLoading()
                    })
            }
        },

        async handleSearchBtnClicked() {
            await this.v$.$validate()
            if (!this.v$.searchCondition.$invalid) {
                this.$emit(
                    'searchImportData',
                    {
                        ...this.searchCondition,
                        customerNo: this.searchCustomer.customerNo,
                        customerDeptCd: this.searchCustomer.deptCd,
                        customerOcsDeptCd: this.searchCustomer.ocsDeptCd,
                    },
                    this.refocusAfterSearch
                )
            } else {
                if (this.v$.searchCondition.objectDateFrom.$invalid || this.v$.searchCondition.objectDateTo.$invalid) {
                    notificationError(MISSING_DATE_MESSAGE)
                    return
                }
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        // [SearchModal Components] When enableClickToSearch = true, clicking on data rows will return that row's for event usage.
        handleClickToSearchAction(selectedRowData) {
            this.searchCustomer.customerNo = selectedRowData.customerNo
            this.searchCustomer.deptCd = selectedRowData.deptCd
            this.searchCustomer.ocsDeptCd = selectedRowData.ocsdeptCd
            // Generate company name after search
            this.clickSearchCustomerName()
        },
    },

    validations() {
        return {
            searchCondition: {
                hawbNo: { regex: fieldInputTextValidate('HAWB No.', ONE_BYTE_REGEX) },
                mawbNo: { regex: fieldInputTextValidate('MAWB No.', ONE_BYTE_REGEX) },
                origin: { regex: fieldInputTextValidate('Origin', ONE_BYTE_REGEX) },
                reportNo: { regex: fieldInputTextValidate('reportNo', NUMBER_REGEX) },
                currentArrflt: { regex: fieldInputTextValidate('到着便名', ONE_BYTE_REGEX) },
                objectDateFrom: { required: requiredRules('日付') },
                objectDateTo: { required: requiredRules('日付') },
            },
            searchCustomer: {
                customerNo: { required: requiredRules('輸入者コード') },
                deptCd: { required: requiredRules('輸入者コード枝番') },
                ocsDeptCd: { required: requiredRules('輸入者コードOCS') },
            },
        }
    },
}
</script>

<style scoped>
.va-button__content {
    padding: 0 !important;
}
</style>
