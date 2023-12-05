<template>
    <va-modal :modelValue="showModal" @update:modelValue="closeHistoryStatusModal" no-outside-dismiss no-padding size="large">
        <template #content>
            <va-inner-loading :loading="isLoading" :size="60">
                <div class="flex justify-between items-center bg-[#538ce2] p-3 text-white">
                    <p class="m-0 font-bold text-lg">{{ historyStatusProps.cwbno }}</p>
                    <va-icon name="va-clear" class="flex-grow-0" @click="closeHistoryStatusModal" />
                </div>
                <div class="p-3">
                    <div class="min-h-[100px] max-h-[800px] p-3">
                        <div class="row justify-start items-center border border-solid relative p-4">
                            <div class="absolute top-[-10px] left-[-10px] bg-white w-fit">条件</div>
                            <div class="row items-center">
                                <div class="flex items-center py-1 xs4">
                                    <span class="min-w-[20%]">業務</span>
                                    <va-select
                                        ref="businessClass"
                                        :modelValue="businessClass"
                                        @update:modelValue="onBusinessClassChange($event)"
                                        :options="modalDropdownOptions?.businessClass"
                                        :text-by="(option) => option.value + ' : ' + option.name"
                                        :track-by="(option) => option.value + ' : ' + option.name"
                                        :value-by="(option) => option.value"
                                        :clearable="true"
                                        :error="v$.businessClass.$error"
                                    />
                                </div>
                                <div class="flex items-center py-1 xs8">
                                    <span class="max-w-[15%]">ステータス</span>
                                    <va-select
                                        ref="status"
                                        v-model.trim="status"
                                        :options="modalDropdownOptions?.status"
                                        :text-by="(option) => option.value + ' : ' + option.name"
                                        :track-by="(option) => option.value + ' : ' + option.name"
                                        :value-by="(option) => option.value"
                                        :clearable="true"
                                        :error="v$.status.$error"
                                        searchable
                                    />
                                </div>
                            </div>
                            <div class="row items-center mt-2">
                                <div class="flex items-center py-1 xs4">
                                    <span class="min-w-[20%]">日付</span>
                                    <Datepicker
                                        ref="regDate"
                                        v-model.trim="regDate"
                                        class="w-full"
                                        show-now-button
                                        enable-seconds
                                        :flow="datePickerFlow"
                                        :format="DatePickerFormat"
                                        :state="v$.regDate.$error ? false : null"
                                    />
                                </div>
                                <div class="flex items-center justify-end py-1 xs8 gap-4">
                                    <div class="item flex xs2">
                                        <va-button color="info" size="medium" @click="handleClearBtnClicked">クリア</va-button>
                                    </div>
                                    <div class="item flex xs2">
                                        <va-button color="info" size="medium" @click="handleRegisterBtnClicked">{{ registerUpdateBtnText }}</va-button>
                                    </div>
                                    <div class="item flex xs2">
                                        <va-button color="danger" size="medium" @click="handleDeleteBtnClicked">削除</va-button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <HistoryStatusResult :resultRowData="rowData" @onTableRowSelection="onTableRowSelection" ref="historyStatusModal"></HistoryStatusResult>
                    <div class="row items-end justify-end pt-4">
                        <div class="item flex xs2">
                            <va-button color="secondary" size="medium" @click="closeHistoryStatusModal">閉じる</va-button>
                        </div>
                    </div>
                </div>
            </va-inner-loading>
        </template>
    </va-modal>
</template>

<script>
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import {
    UPDATE_SUCCESS_MESSAGE,
    REGISTER_SUCCESS_MESSAGE,
    EMPTY_SELECTION_ERROR_MESSAGE,
    EMPTY_FIELD_INPUT_ERROR_MESSAGE,
    DELETE_SUCCESS_MESSAGE,
    DIFFERENT_DATA_ERROR_MESSAGE,
    DELETE_CONFIRM_ACTION_MESSAGE,
    SYSTEM_ERROR_MESSAGE,
} from '@/utils/messages'
import { useVuelidate } from '@vuelidate/core'
import { requiredRules } from '@/utils/validator'
import { notificationError, notificationSuccess } from '../../components/Notification/NotificationApi.vue'
import HistoryStatusResult from './HistoryStatusResult.vue'
import { dateFormat } from '@/utils/utilities'

export default {
    props: {
        showModal: Boolean,
        historyStatusProps: Object,
    },
    components: {
        Datepicker,
        HistoryStatusResult,
    },
    data() {
        return {
            v$: useVuelidate(),
            momentDateConvertFormat: 'yyyy/MM/DD HH:mm:ss',
            DatePickerFormat: 'yyyy/MM/dd HH:mm:ss',
            datePickerFlow: ['calendar', 'time'],

            rowData: [],
            isLoading: false,
            authInfo: null,
            currentSelectedRow: null,

            businessClass: '',
            status: '',
            regDate: null,
            initStatusList: {
                '01': [],
                '02': [],
                '03': [],
            },
            modalDropdownOptions: {
                businessClass: [],
                status: [],
            },
        }
    },

    computed: {
        registerUpdateBtnText() {
            return this.currentSelectedRow ? '更新' : '登録'
        },
    },

    beforeMount() {
        const localStorageData = localStorage.getItem('vuex')
        this.authInfo = JSON.parse(localStorageData).authPermission
    },

    watch: {
        // Trigger when modal open
        showModal(currentVal) {
            if (currentVal === true) {
                this.getHistoryStatusData(this.historyStatusProps.cwbno)
            }
        },

        businessClass(currentVal) {
            switch (currentVal) {
                case '01':
                    this.modalDropdownOptions.status = this.initStatusList['01']
                    break
                case '02':
                    this.modalDropdownOptions.status = this.initStatusList['02']
                    break
                case '03':
                    this.modalDropdownOptions.status = this.initStatusList['03']
                    break
                default:
                    this.modalDropdownOptions.status = []
                    this.status = ''
                    break
            }
        },
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        clearFormData() {
            this.currentSelectedRow = null
            this.businessClass = ''
            this.status = ''
            this.regDate = null
            this.$refs.historyStatusModal.clearSelection()
            this.v$.$reset()
        },

        // list ステータスメンテ API
        getHistoryStatusData(hawbNo) {
            this.changeLoading()
            this.axios({
                url: '/api/oaet002/status-history/list',
                method: 'get',
                params: {
                    cwbNo: hawbNo,
                    cwbNoDeptCd: '000',
                },
            })
                .then((res) => {
                    const { listAEStatusHistoryDtoDto, listBusinessClassName, list01, list02, list03 } = res.data
                    this.rowData = listAEStatusHistoryDtoDto
                    this.modalDropdownOptions.businessClass = listBusinessClassName
                    this.initStatusList = {
                        '01': list01,
                        '02': list02,
                        '03': list03,
                    }
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError(SYSTEM_ERROR_MESSAGE)
                    this.closeHistoryStatusModal()
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // delete ステータスメンテ API
        deleteSelectedHistoryStatus() {
            this.changeLoading()

            const deleteData = {
                ...this.currentSelectedRow,
                cwbNo: this.historyStatusProps.cwbno,
            }
            this.axios
                .delete('/api/oaet002/status-history/delete', {
                    data: deleteData,
                })
                .then((res) => {
                    if (res.data.message === '対象データが存在しません。' || res.data.message === '選択したステータスによる削除は出来ません。') notificationError(res.data.message)
                    else {
                        this.getHistoryStatusData(this.historyStatusProps.cwbno)
                        this.clearFormData()
                        notificationSuccess(DELETE_SUCCESS_MESSAGE, '', 3)
                    }
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError(SYSTEM_ERROR_MESSAGE)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // Regist ステータスメンテ API
        registHistoryStatus() {
            this.changeLoading()

            const registData = {
                awbNo: this.historyStatusProps.awbno,
                bwbNo: this.historyStatusProps.bwbno,
                cwbNo: this.historyStatusProps.cwbno,
                cwbNoDeptCd: this.historyStatusProps.cwbnoDeptCd,
                headerBusinessClass: this.businessClass,
                headerStatusCd: this.status,
                headerRegDate: dateFormat(this.regDate, this.momentDateConvertFormat),
            }

            this.axios
                .post('/api/oaet002/status-history/regist', registData)
                .then((res) => {
                    this.getHistoryStatusData(this.historyStatusProps.cwbno)
                    this.clearFormData()
                    notificationSuccess(REGISTER_SUCCESS_MESSAGE, '', 3)
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError(SYSTEM_ERROR_MESSAGE)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // Update ステータスメンテ API
        updateSelectedHistoryStatus() {
            this.changeLoading()

            const updateData = {
                headerStatusCd: this.status,
                headerRegDate: dateFormat(this.regDate, this.momentDateConvertFormat),
                headerBusinessClass: this.businessClass,
                regUserCd: this.currentSelectedRow.regUserCd,

                businessClass: this.currentSelectedRow.businessClass,
                awbNo: this.historyStatusProps.awbno,
                bwbNo: this.historyStatusProps.bwbno,
                cwbNo: this.historyStatusProps.cwbno,
                cwbNoDeptCd: this.historyStatusProps.cwbnoDeptCd,
                statusCd: this.currentSelectedRow.statusCd,
                regDate: this.currentSelectedRow.regDate,
            }
            this.axios
                .put('/api/oaet002/status-history/update', updateData)
                .then((res) => {
                    this.getHistoryStatusData(this.historyStatusProps.cwbno)
                    this.clearFormData()
                    notificationSuccess(UPDATE_SUCCESS_MESSAGE, '', 3)
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError(SYSTEM_ERROR_MESSAGE)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        handleClearBtnClicked() {
            this.clearFormData()
        },

        validateDeleteInfo() {
            const rowData = this.currentSelectedRow
            const formData = {
                businessClass: this.businessClass,
                status: this.status,
                regDate: this.regDate,
            }

            if (!rowData) {
                notificationError(EMPTY_SELECTION_ERROR_MESSAGE)
                return false
            }

            if (rowData.businessClass !== formData.businessClass || rowData.statusCd !== formData.status || rowData.regDate !== formData.regDate) {
                notificationError(DIFFERENT_DATA_ERROR_MESSAGE)
                return false
            }

            return true
        },

        async handleRegisterBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                if (this.currentSelectedRow) {
                    this.updateSelectedHistoryStatus()
                } else {
                    this.registHistoryStatus()
                }
            } else {
                notificationError(EMPTY_FIELD_INPUT_ERROR_MESSAGE)
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        handleDeleteBtnClicked() {
            const warningText = DELETE_CONFIRM_ACTION_MESSAGE
            if (!this.validateDeleteInfo()) return false
            if (confirm(warningText)) this.deleteSelectedHistoryStatus()
        },

        closeHistoryStatusModal() {
            this.rowData = []
            this.clearFormData()
            this.$emit('closeHistoryStatusModal')
        },

        onTableRowSelection(rowData) {
            if (rowData) {
                this.currentSelectedRow = rowData
                this.businessClass = rowData.businessClass
                this.regDate = rowData.regDate
                this.status = rowData.statusCd
            }
        },

        onBusinessClassChange(e) {
            this.businessClass = e
            this.status = ''
        },
    },

    validations() {
        return {
            businessClass: { required: requiredRules('業務') },
            status: { required: requiredRules('ステータス') },
            regDate: { required: requiredRules('日付') },
        }
    },
}
</script>
