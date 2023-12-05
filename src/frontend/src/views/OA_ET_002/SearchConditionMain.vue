<template>
    <va-card>
        <div class="row justify-start items-center px-3 py-2">
            <div class="flex items-center xs3">
                <div v-for="(option, index) in radioOptions" :key="index">
                    <input name="objectDate" type="radio" v-model.trim="searchCondition.objectDate" :value="index" :id="'objectDate-' + option" />
                    <label class="mx-1" :for="'objectDate-' + option">
                        {{ option }}
                    </label>
                </div>
            </div>
            <div class="flex items-center xs2">
                <Datepicker
                    v-model.trim="searchCondition.truckNoDate"
                    :auto-apply="true"
                    :format="DatePickerFormat"
                    :enable-time-picker="false"
                    style="width: 100px"
                    :clearable="false"
                    :state="v$.searchCondition.truckNoDate.$invalid ? false : null"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">対査確認</span>
                <va-switch
                    v-model.trim="searchCondition.arrival01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.arrival01 || '未'"
                    :false-inner-label="initData?.arrival01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.arrival02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.arrival02 || '済'"
                    :false-inner-label="initData?.arrival02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">CDB作成</span>
                <va-switch
                    v-model.trim="searchCondition.cdb01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.cdb01 || '未'"
                    :false-inner-label="initData?.cdb01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.cdb02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.cdb02 || '済'"
                    :false-inner-label="initData?.cdb02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 40%">CDB戻り</span>
                <va-switch
                    v-model.trim="searchCondition.cdbre01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.cdbre01 || '未'"
                    :false-inner-label="initData?.cdbre01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.cdbre02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.cdbre02 || '済'"
                    :false-inner-label="initData?.cdbre02 || '済'"
                    size="small"
                />
                <div class="w-24"></div>
            </div>
        </div>

        <div class="row justify-start items-center px-3 py-2">
            <div class="flex items-center xs2">
                <span style="min-width: 40%">積込港</span>
                <va-select
                    v-model.trim="searchCondition.depPort"
                    :options="initData?.listDepPort"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span style="min-width: 40%">シャトル/トラック便</span>
                <va-input v-model.trim="searchCondition.linkTruckNOFrom" :maxlength="7" /> ～ <va-input v-model.trim="searchCondition.linkTruckNOTo" :maxlength="7" />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">BIL作成</span>
                <va-switch
                    v-model.trim="searchCondition.bil01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.bil01 || '未'"
                    :false-inner-label="initData?.bil01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.bil02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.bil02 || '済'"
                    :false-inner-label="initData?.bil02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">EDA作成</span>
                <va-switch
                    v-model.trim="searchCondition.eda01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.eda01 || '未'"
                    :false-inner-label="initData?.eda01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.eda02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.eda02 || '済'"
                    :false-inner-label="initData?.eda02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 40%">MEC/EDC作成</span>
                <va-switch
                    v-model.trim="searchCondition.mecedc01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.mecedc01 || '未'"
                    :false-inner-label="initData?.mecedc01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.mecedc02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.mecedc02 || '済'"
                    :false-inner-label="initData?.mecedc02 || '済'"
                    size="small"
                />
                <va-switch v-model.trim="searchCondition.possible01" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-12">
                            {{ initData?.possible01 || '申告可' }}
                        </div>
                    </template>
                </va-switch>
                <va-switch
                    v-model.trim="searchCondition.possible02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.possible02"
                    :false-inner-label="initData?.possible02"
                    size="small"
                >
                    <template #innerLabel>
                        <div class="va-text-center w-14">
                            {{ initData?.possible02 || '申告不可' }}
                        </div>
                    </template>
                </va-switch>
            </div>
        </div>

        <div class="row justify-start items-center px-3 py-2">
            <div class="flex items-center xs2">
                <span style="min-width: 40%">仕向地</span>
                <va-input ref="destination" v-model.trim="searchCondition.destination" :maxlength="3" :error="v$.searchCondition.destination.$error" />
            </div>
            <div class="flex items-center xs3">
                <span style="min-width: 40%">MAWB No.</span>
                <va-input ref="awbno" v-model.trim="searchCondition.awbno" :maxlength="50" :error="v$.searchCondition.awbno.$error" />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">搬前申告</span>
                <va-switch
                    v-model.trim="searchCondition.declara01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.declara01 || '未'"
                    :false-inner-label="initData?.declara01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.declara02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.declara02 || '済'"
                    :false-inner-label="initData?.declara02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">搬後申告</span>
                <va-switch
                    v-model.trim="searchCondition.reserveDeclara01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.reserveDeclara01 || '未'"
                    :false-inner-label="initData?.reserveDeclara01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.reserveDeclara02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.reserveDeclara02 || '済'"
                    :false-inner-label="initData?.reserveDeclara02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 40%">許可</span>
                <va-switch
                    v-model.trim="searchCondition.permission01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.permission01 || '未'"
                    :false-inner-label="initData?.permission01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.permission02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.permission02 || '済'"
                    :false-inner-label="initData?.permission02 || '済'"
                    size="small"
                />
                <va-switch v-model.trim="searchCondition.esaflg01" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-12">
                            {{ initData?.esaflg01 || 'EDA' }}
                        </div>
                    </template>
                </va-switch>
                <va-switch v-model.trim="searchCondition.esaflg02" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-14">
                            {{ initData?.esaflg02 || 'MEC' }}
                        </div>
                    </template>
                </va-switch>
            </div>
        </div>
        <div v-show="v$.searchCondition.destination.$error || v$.searchCondition.awbno.$error" class="row justify-start items-center pl-3">
            <div class="flex items-center xs2">
                <span v-show="v$.searchCondition.destination.$error" style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.destination.$errors[0]?.$message }}</span>
            </div>
            <div class="flex items-center xs3">
                <span v-show="v$.searchCondition.awbno.$error" style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.awbno.$errors[0]?.$message }}</span>
            </div>
        </div>
        <div class="row justify-start items-center px-3 py-2">
            <div class="flex items-center xs2">
                <span style="min-width: 40%">許可区分</span>
                <va-select
                    v-model.trim="searchCondition.permitClassCd"
                    :options="initData?.listPermitClassCd"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span style="min-width: 40%">HAWB No.</span>
                <va-input ref="cwbno" v-model.trim="searchCondition.cwbno" :maxlength="50" :error="v$.searchCondition.cwbno.$error" />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">積付</span>
                <va-switch
                    v-model.trim="searchCondition.consolidation01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.consolidation01 || '未'"
                    :false-inner-label="initData?.consolidation01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.consolidation02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.consolidation02 || '済'"
                    :false-inner-label="initData?.consolidation02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 35%">HDF作成</span>
                <va-switch
                    v-model.trim="searchCondition.hdf01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.hdf01 || '未'"
                    :false-inner-label="initData?.hdf01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.hdf02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.hdf02 || '済'"
                    :false-inner-label="initData?.hdf02 || '済'"
                    size="small"
                />
            </div>
            <div class="flex items-center xs2">
                <span style="min-width: 40%">ULM作成</span>
                <va-switch
                    v-model.trim="searchCondition.ulm01"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.ulm01 || '未'"
                    :false-inner-label="initData?.ulm01 || '未'"
                    size="small"
                />
                <va-switch
                    v-model.trim="searchCondition.ulm02"
                    :false-value="TOGGLE.FALSE"
                    :true-value="TOGGLE.TRUE"
                    :true-inner-label="initData?.ulm02 || '済'"
                    :false-inner-label="initData?.ulm02 || '済'"
                    size="small"
                />
                <va-switch v-model.trim="searchCondition.largeSmallFlg01" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-12">
                            {{ initData?.largeSmallFlg01 || '大額' }}
                        </div>
                    </template>
                </va-switch>
                <va-switch v-model.trim="searchCondition.largeSmallFlg02" :false-value="TOGGLE.FALSE" :true-value="TOGGLE.TRUE" size="small">
                    <template #innerLabel>
                        <div class="va-text-center w-14">
                            {{ initData?.largeSmallFlg02 || '少額' }}
                        </div>
                    </template>
                </va-switch>
            </div>
        </div>
        <div v-show="v$.searchCondition.cwbno.$error" class="row justify-start items-center pl-3">
            <div class="flex items-center xs2"></div>
            <div class="flex items-center xs3">
                <span v-show="v$.searchCondition.cwbno.$error" style="min-width: 40%" class="text-red-600">{{ v$.searchCondition.cwbno.$errors[0]?.$message }}</span>
            </div>
        </div>

        <div class="row justify-start items-center px-3 py-2">
            <div class="flex items-center xs2">
                <span style="min-width: 40%">提出有無</span>
                <va-select
                    v-model.trim="searchCondition.permitClass"
                    :options="initData?.listPermitClass"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    clearable
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span style="min-width: 40%">代理店</span>
                <va-select
                    v-model.trim="searchCondition.agent"
                    :options="initData?.listAgent"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    :clearable="!agentAuthCheck"
                    :disabled="agentAuthCheck"
                    searchable
                />
            </div>
            <div class="flex items-center xs3">
                <span style="min-width: 40%">蔵置場所</span>
                <va-select
                    v-model.trim="searchCondition.bondedWareHouseCd"
                    :options="initData?.listBondedWareHouseCd"
                    :text-by="(option) => option.value + ' : ' + option.name"
                    :track-by="(option) => option.value + ' : ' + option.name"
                    :value-by="(option) => option.value"
                    :clearable="!bondedWareHouseCdAuthCheck"
                    :disabled="bondedWareHouseCdAuthCheck"
                    searchable
                />
            </div>
            <div class="flex justify-end xs4">
                <div class="item flex xs3">
                    <va-button color="info" size="medium" @click="handleSearchBtnClicked"> 検索 </va-button>
                </div>
            </div>
        </div>
    </va-card>
</template>
<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import { useVuelidate } from '@vuelidate/core'
import { notificationError } from '../../components/Notification/NotificationApi.vue'
import { requiredRules, fieldInputTextValidate, ONE_BYTE_REGEX } from '@/utils/validator'
import { DATE_PICKER_FORMAT, currentDateTime } from '@/utils/utilities'
import { MISSING_DATE_MESSAGE } from '@/utils/messages'

export default {
    components: {
        Datepicker,
    },
    props: {
        initData: Object,
        searchImportData: Function,
    },
    data() {
        return {
            TOGGLE: {
                TRUE: '1',
                FALSE: '0',
            },
            radioOptions: ['対査', 'シャトル', 'トラック', '許可', '申告'],
            v$: useVuelidate(),
            searchCondition: {
                objectDate: 0,
                truckNoDate: currentDateTime(),
                arrival01: '0',
                arrival02: '0',
                cdb01: '0',
                cdb02: '0',
                cdbre01: '0',
                cdbre02: '0',

                depPort: '',
                linkTruckNOFrom: '',
                linkTruckNOTo: '',
                bil01: '0',
                bil02: '0',
                eda01: '0',
                eda02: '0',
                mecedc01: '0',
                mecedc02: '0',
                possible01: '0',
                possible02: '0',

                destination: '',
                awbno: '',
                declara01: '0',
                declara02: '0',
                reserveDeclara01: '0',
                reserveDeclara02: '0',
                permission01: '0',
                permission02: '0',
                esaflg01: '0',
                esaflg02: '0',

                permitClassCd: '',
                cwbno: '',
                consolidation01: '0',
                consolidation02: '0',
                hdf01: '0',
                hdf02: '0',
                ulm01: '0',
                ulm02: '0',
                largeSmallFlg01: '0',
                largeSmallFlg02: '0',

                permitClass: '',
                agent: '',
                bondedWareHouseCd: '',
            },

            agentAuthCheck: false,
            bondedWareHouseCdAuthCheck: false,
            DatePickerFormat: DATE_PICKER_FORMAT,
        }
    },

    beforeMount() {
        const localStorageData = localStorage.getItem('vuex')
        const authInfo = JSON.parse(localStorageData)?.authPermission

        if (!authInfo) return

        if (authInfo.userAuthorityCd === '52') {
            this.searchCondition.agent = authInfo.departmentCd
            this.agentAuthCheck = true
        }
        if (authInfo.userAuthorityCd === '04') {
            this.searchCondition.bondedWareHouseCd = authInfo.departmentCd
            this.bondedWareHouseCdAuthCheck = true
        }
    },

    watch: {
        initData() {
            if (this.initData.objectDate) {
                this.radioOptions = this.initData.objectDate
            }
        },
    },

    methods: {
        async handleSearchBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.$emit('searchImportData', this.searchCondition)
            } else {
                if (this.v$.searchCondition.truckNoDate.$invalid) {
                    notificationError(MISSING_DATE_MESSAGE)
                    return
                }
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },
    },
    validations() {
        return {
            searchCondition: {
                truckNoDate: { required: requiredRules('日付') },
                destination: { regex: fieldInputTextValidate('仕向地', ONE_BYTE_REGEX) },
                awbno: { regex: fieldInputTextValidate('MAWB No.', ONE_BYTE_REGEX) },
                cwbno: { regex: fieldInputTextValidate('HAWB No.', ONE_BYTE_REGEX) },
            },
        }
    },
}
</script>
