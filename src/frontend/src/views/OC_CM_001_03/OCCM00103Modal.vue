<template>
    <div>
        <va-modal :modelValue="showModal" @update:modelValue="handleCloseModal" class="p-0" size="large" no-outside-dismiss hide-default-actions no-padding>
            <template #header>
                <div class="flex justify-between items-center w-full pl-4 p-3">
                    <span class="m-0 text-lg w-fit flex-grow-0">{{ modalTitle }}</span>
                    <va-icon name="va-clear" class="flex-grow-0" @click="handleCloseModal" />
                </div>
                <div class="bg-slate-200 h-[1px]"></div>
            </template>

            <template #default>
                <va-inner-loading :loading="isLoading" :size="60">
                    <div class="w-[1400px] px-4 pb-4">
                        <div class="row justify-start items-center">
                            <div class="flex items-center py-3 xs7">
                                <span style="min-width: 15%">仕出仕向人No</span>
                                <va-input ref="customerNo" v-model.trim="senderReceiverInfo.customerNo" :error="v$.senderReceiverInfo.customerNo.$error" :maxlength="13" />
                                <span class="mx-2">ー</span>
                                <va-input ref="deptCd" v-model.trim="senderReceiverInfo.deptCd" :error="v$.senderReceiverInfo.deptCd.$error" :maxlength="4" />
                                <span class="mx-2">ー</span>
                                <va-select
                                    ref="csrCseFlag"
                                    v-model.trim="senderReceiverInfo.csrCseFlag"
                                    :options="modalDropdownOptions?.listSenderFlag"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    :error="v$.senderReceiverInfo.csrCseFlag.$error"
                                />
                                <span class="mx-2">ー</span>
                                <va-select
                                    ref="shiyoBashoFlag"
                                    v-model.trim="senderReceiverInfo.shiyoBashoFlag"
                                    :options="modalDropdownOptions?.listPlaceOfUse"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    :error="v$.senderReceiverInfo.shiyoBashoFlag.$error"
                                />
                            </div>
                            <div class="flex items-center py-3 xs5">
                                <span style="max-width: 25%">英文会社名</span>
                                <va-input ref="customerNamee" v-model.trim="senderReceiverInfo.customerNamee" :maxlength="70" :error="v$.senderReceiverInfo.customerNamee.$error" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span class="text-red-600">{{
                                    v$.senderReceiverInfo.customerNo.$errors[0]?.$message ||
                                    v$.senderReceiverInfo.deptCd.$errors[0]?.$message ||
                                    v$.senderReceiverInfo.csrCseFlag.$errors[0]?.$message ||
                                    v$.senderReceiverInfo.shiyoBashoFlag.$errors[0]?.$message
                                }}</span>
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%"></span>
                                <span class="text-red-600">{{ v$.senderReceiverInfo.customerNamee.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center py-2">
                            <div class="flex items-center xs7">
                                <span style="min-width: 15%">国連国コード</span>
                                <va-input ref="countryCd" v-model.trim="senderReceiverInfo.countryCd" :maxlength="2" :error="v$.senderReceiverInfo.countryCd.$error" />
                                <span style="max-width: 10%; margin-left: 20px">郵便番号</span>
                                <va-input ref="zipCd" v-model.trim="senderReceiverInfo.zipCd" :maxlength="9" :error="v$.senderReceiverInfo.zipCd.$error" />
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%">英文部署名</span>
                                <va-input ref="division" v-model.trim="senderReceiverInfo.division" :maxlength="60" :error="v$.senderReceiverInfo.division.$error" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span style="max-width: 35%" class="text-red-600">{{ v$.senderReceiverInfo.countryCd.$errors[0]?.$message }}</span>
                                <span style="max-width: 14%"></span>
                                <span style="max-width: 35%" class="text-red-600">{{ v$.senderReceiverInfo.zipCd.$errors[0]?.$message }}</span>
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%"></span>
                                <span class="text-red-600">{{ v$.senderReceiverInfo.division.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center py-2">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%">英文住所（一括）</span>
                                <va-input v-model.trim="senderReceiverInfo.customerAddeblAnket" :maxlength="105" />
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%">担当者名</span>
                                <va-input v-model.trim="senderReceiverInfo.charger" :maxlength="40" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span class="text-red-600">{{ v$.senderReceiverInfo.customerAddeBlanket.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center py-2">
                            <div class="flex items-center xs7">
                                <span style="min-width: 15%">英文住所</span>
                                <va-input ref="customerAdde1" v-model.trim="senderReceiverInfo.customerAdde1" :maxlength="35" :error="v$.senderReceiverInfo.customerAdde1.$error" />
                                <div class="px-3"></div>
                                <va-input ref="customerAdde2" v-model.trim="senderReceiverInfo.customerAdde2" :maxlength="35" :error="v$.senderReceiverInfo.customerAdde2.$error" />
                                <div class="px-3"></div>
                                <va-input ref="customerAdde3" v-model.trim="senderReceiverInfo.customerAdde3" :maxlength="35" :error="v$.senderReceiverInfo.customerAdde3.$error" />
                            </div>
                            <div class="flex items-center xs5">
                                <span style="min-width: 25%">TEL NO</span>
                                <va-input ref="telNo" v-model.trim="senderReceiverInfo.telNo" :maxlength="20" input-class="hide-input-number-arrow" :error="v$.senderReceiverInfo.telNo.$error" />
                                <span style="min-width: 10%; margin-left: 14px">FAX NO</span>
                                <va-input ref="faxNo" v-model.trim="senderReceiverInfo.faxNo" :maxlength="20" input-class="hide-input-number-arrow" :error="v$.senderReceiverInfo.faxNo.$error" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7"></div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%"></span>
                                <span class="text-red-600">{{ v$.senderReceiverInfo.telNo.$errors[0]?.$message || v$.senderReceiverInfo.faxNo.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center" v-show="v$.senderReceiverInfo.telNo.$error">
                            <div class="flex items-center xs7"></div>
                            <div class="flex items-center xs5">
                                <span class="text-red-600">{{ (v$.senderReceiverInfo.telNo.maxLength.$invalid && v$.senderReceiverInfo.telNo.maxLength.$message) || '' }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center" v-show="v$.senderReceiverInfo.faxNo.$error">
                            <div class="flex items-center xs7"></div>
                            <div class="flex items-center xs5">
                                <span class="text-red-600">{{ (v$.senderReceiverInfo.faxNo.maxLength.$invalid && v$.senderReceiverInfo.faxNo.maxLength.$message) || '' }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center py-2">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <va-input ref="customerAdde4" v-model.trim="senderReceiverInfo.customerAdde4" :maxlength="35" :error="v$.senderReceiverInfo.customerAdde4.$error" />
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 25%">メールアドレス</span>
                                <va-input ref="emailAdd" v-model.trim="senderReceiverInfo.emailAdd" :maxlength="100" :error="v$.senderReceiverInfo.emailAdd.$error" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span class="text-red-600">
                                    {{
                                        v$.senderReceiverInfo.customerAdde1.$errors[0]?.$message ||
                                        v$.senderReceiverInfo.customerAdde2.$errors[0]?.$message ||
                                        v$.senderReceiverInfo.customerAdde3.$errors[0]?.$message ||
                                        v$.senderReceiverInfo.customerAdde4.$errors[0]?.$message
                                    }}
                                </span>
                            </div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 18%"></span>
                                <span class="text-red-600">{{ v$.senderReceiverInfo.emailAdd.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center py-2">
                            <div class="flex items-center xs12 pt-2">
                                <span style="max-width: 9%">備考</span>
                                <va-input type="textarea" v-model.trim="senderReceiverInfo.remarks" :maxlength="1000" />
                            </div>
                        </div>

                        <template v-if="isUpdating">
                            <div class="row justify-start items-center py-2">
                                <div class="flex items-center xs3">
                                    <span style="min-width: 37%">登録者</span>
                                    <va-input readonly v-model.trim="senderReceiverInfo.regUserCd" />
                                </div>
                                <div class="flex items-center xs3">
                                    <span style="min-width: 30%">登録日時</span>
                                    <va-input readonly v-model.trim="senderReceiverInfo.regDate" />
                                </div>
                            </div>

                            <div class="row items-center py-2">
                                <div class="flex items-center xs3">
                                    <span style="min-width: 37%">変更者</span>
                                    <va-input readonly v-model.trim="senderReceiverInfo.updateUserCd" />
                                </div>
                                <div class="flex items-center xs3">
                                    <span style="min-width: 30%">変更日時</span>
                                    <va-input readonly v-model.trim="senderReceiverInfo.updateDate" />
                                </div>
                            </div>
                        </template>

                        <div class="row justify-end pt-4">
                            <div v-if="isUpdating" class="item flex xs1">
                                <va-button @click="handleDelBtnClicked" color="danger" size="medium">削除</va-button>
                            </div>
                            <div v-if="isUpdating" class="item flex xs1">
                                <va-button @click="handleUpdateBtnClicked" color="info" size="medium">登録</va-button>
                            </div>
                            <div class="item flex xs1">
                                <va-button @click="handleRegisterBtnClicked" color="info" size="medium">{{ registerBtnText }}</va-button>
                            </div>
                            <div class="item flex xs1">
                                <va-button color="secondary" size="medium" @click="handleCloseModal">閉じる</va-button>
                            </div>
                        </div>
                    </div>
                </va-inner-loading>
            </template>
        </va-modal>
    </div>
</template>

<script>
import { useVuelidate } from '@vuelidate/core'
import { notificationSuccess, notificationError } from '../../components/Notification/NotificationApi.vue'
import { requiredRules, fieldMaxLength, fieldInputTextValidate, ONE_BYTE_REGEX, NUMBER_REGEX } from '@/utils/validator'
import { NOTIFICATION_DURATION } from '@/utils/utilities'
import {
    SYSTEM_ERROR_MESSAGE,
    REGISTER_ERROR_MESSAGE,
    DELETE_ERROR_MESSAGE,
    UPDATE_ERROR_MESSAGE,
    DATA_NOT_EXIST_MESSAGE,
    INVALID_INPUT_MESSAGE,
    DELETE_CONFIRM_ACTION_MESSAGE,
} from '@/utils/messages'

export default {
    props: {
        showModal: Boolean,
        modalAction: String,
        modalDropdownOptions: Object,
        senderReceiverQueryDetail: Object,
    },
    data() {
        return {
            v$: useVuelidate(),
            TOGGLE: {
                TRUE: '1',
                FALSE: '0',
            },
            isLoading: false,
            senderReceiverInfo: {
                customerNo: '',
                deptCd: '',
                csrCseFlag: '',
                shiyoBashoFlag: '',
                seq: '',
                countryCd: '',
                division: '',
                zipCd: '',
                customerNamee: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeblAnket: '',
                charger: '',
                telNo: '',
                faxNo: '',
                emailAdd: '',
                remarks: '',
                regUserCd: '',
                regDate: '',
            },
            currentSelectedRow: undefined,
        }
    },
    computed: {
        isUpdating() {
            return this.modalAction === 'update'
        },
        modalTitle() {
            return this.isUpdating ? '明細' : '新規登録'
        },
        registerBtnText() {
            return this.isUpdating ? '新規登録' : '登録'
        },
        ocsdeptCd() {
            return this.senderReceiverInfo.csrCseFlag + this.senderReceiverInfo.shiyoBashoFlag + this.senderReceiverInfo.seq
        },
    },
    watch: {
        showModal(currentVal) {
            if (currentVal) {
                if (!this.isUpdating) this.clearData()
                else this.initCurrentSenderReceiver(this.senderReceiverQueryDetail)
            }
        },
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },
        handleCloseModal() {
            this.$emit('closeDetailModal')
            this.clearData()
            this.v$.$reset()
        },

        // Get current selected row
        onTableRowSelection(selectedRow) {
            this.currentSelectedRow = selectedRow
        },

        // E007: Register new user
        async handleRegisterBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.changeLoading()
                this.axios
                    .post('api/occm001/caterer/regist', this.senderReceiverInfo)
                    .then((res) => {
                        notificationSuccess(res.data.message, '', NOTIFICATION_DURATION)
                        this.changeLoading()
                        this.handleCloseModal()
                        if (this.isUpdating) this.$emit('searchImportData', null)
                    })
                    .catch((error) => {
                        console.log(error)
                        if (error.request.status === 400) notificationError(error.response.data.message, error.message)
                        else notificationError(REGISTER_ERROR_MESSAGE, error.message)
                        this.changeLoading()
                    })
            } else {
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        // E008: Get a specific senderReceiver info
        initCurrentSenderReceiver(senderReceiverQueryDetail) {
            this.changeLoading()
            this.axios
                .get(`api/occm001/caterer/detail`, { params: senderReceiverQueryDetail })
                .then((res) => {
                    this.senderReceiverInfo = res.data
                })
                .catch((error) => {
                    if (error.request.status === 400) notificationError(DATA_NOT_EXIST_MESSAGE, error.message)
                    else notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                    this.handleCloseModal()
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        async handleDelBtnClicked() {
            const warningText = DELETE_CONFIRM_ACTION_MESSAGE
            if (confirm(warningText)) this.handleDeleteUser()
        },

        // E009: Delete selected user
        handleDeleteUser() {
            this.changeLoading()
            this.axios
                .delete(`api/occm001/caterer/delete`, {
                    data: {
                        customerNo: this.senderReceiverInfo.customerNo,
                        deptCd: this.senderReceiverInfo.deptCd,
                        ocsdeptCd: this.ocsdeptCd,
                    },
                })
                .then((res) => {
                    notificationSuccess(res.data.message, '', NOTIFICATION_DURATION)
                    this.changeLoading()
                    this.handleCloseModal()
                    this.$emit('searchImportData', null)
                })
                .catch((error) => {
                    this.changeLoading()
                    notificationError(DELETE_ERROR_MESSAGE, error.message)
                })
        },

        // E010: Update user info
        async handleUpdateBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.changeLoading()
                this.axios
                    .put('api/occm001/caterer/update', {
                        ...this.senderReceiverInfo,
                        ocsdeptCd: this.ocsdeptCd,
                    })
                    .then((res) => {
                        notificationSuccess(res.data.message, '', NOTIFICATION_DURATION)
                        this.changeLoading()
                        this.handleCloseModal()
                        this.$emit('searchImportData', null)
                    })
                    .catch((error) => {
                        console.log(error)
                        if (error.request.status === 400) notificationError(INVALID_INPUT_MESSAGE, error.message)
                        else notificationError(UPDATE_ERROR_MESSAGE, error.message)
                        this.changeLoading()
                    })
            } else {
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        // Reset Modal data fields
        clearData() {
            this.senderReceiverInfo = {
                customerNo: '',
                deptCd: '',
                csrCseFlag: '',
                shiyoBashoFlag: '',
                countryCd: '',
                division: '',
                zipCd: '',
                customerNamee: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeblAnket: '',
                charger: '',
                telNo: '',
                faxNo: '',
                emailAdd: '',
                remarks: '',
                regUserCd: '',
                regDate: '',
            }
        },
    },
    validations() {
        return {
            senderReceiverInfo: {
                customerNo: { required: requiredRules('仕出仕向人No'), regex: fieldInputTextValidate('仕出仕向人No', ONE_BYTE_REGEX) },
                deptCd: { required: requiredRules('N枝番'), regex: fieldInputTextValidate('N枝番', ONE_BYTE_REGEX) },
                csrCseFlag: { required: requiredRules('仕出向フラグ') },
                shiyoBashoFlag: { required: requiredRules('使用場所') },
                countryCd: { regex: fieldInputTextValidate('国連国コード', ONE_BYTE_REGEX) },
                customerNamee: { regex: fieldInputTextValidate('英文会社名', ONE_BYTE_REGEX) },
                division: { regex: fieldInputTextValidate('英文部署名', ONE_BYTE_REGEX) },
                zipCd: { regex: fieldInputTextValidate('郵便番号', ONE_BYTE_REGEX) },
                customerAdde1: { regex: fieldInputTextValidate('英文住所1', ONE_BYTE_REGEX) },
                customerAdde2: { regex: fieldInputTextValidate('英文住所2', ONE_BYTE_REGEX) },
                customerAdde3: { regex: fieldInputTextValidate('英文住所3', ONE_BYTE_REGEX) },
                customerAdde4: { regex: fieldInputTextValidate('英文住所4', ONE_BYTE_REGEX) },
                customerAddeBlanket: { regex: fieldInputTextValidate('英文住所(一括)', ONE_BYTE_REGEX) },
                telNo: { maxLength: fieldMaxLength('Tel No', 20), regex: fieldInputTextValidate('Tel No', NUMBER_REGEX) },
                faxNo: { maxLength: fieldMaxLength('Fax No', 20), regex: fieldInputTextValidate('Fax No', NUMBER_REGEX) },
                emailAdd: { regex: fieldInputTextValidate('メールアドレス', ONE_BYTE_REGEX) },
            },
        }
    },
}
</script>
