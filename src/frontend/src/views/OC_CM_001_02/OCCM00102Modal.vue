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
                    <div class="w-[1200px] px-4 pb-4 max-h-[850px] overflow-auto">
                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="min-width: 15%">業者No</span>
                                <va-input ref="customerNo" v-model.trim="vendorInfo.customerNo" :error="v$.vendorInfo.customerNo.$error" :maxlength="13" />
                                <span class="mx-2">ー</span>
                                <va-input ref="deptCd" v-model.trim="vendorInfo.deptCd" :error="v$.vendorInfo.deptCd.$error" :maxlength="4" />
                                <span class="mx-2">ー</span>
                                <va-select
                                    ref="impExpFlag"
                                    :modelValue="vendorInfo.impExpFlag"
                                    @update:modelValue="handleimpExpFlagChangeEvent($event)"
                                    :options="modalDropdownOptions?.listImportAndExportFlag"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    :error="v$.vendorInfo.impExpFlag.$error"
                                />
                                <span class="mx-2">ー</span>
                                <va-select
                                    ref="shiyoBashoFlag"
                                    v-model.trim="vendorInfo.shiyoBashoFlag"
                                    :options="modalDropdownOptions?.listPlaceOfUse"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    :error="v$.vendorInfo.shiyoBashoFlag.$error"
                                />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="max-width: 23%">和文会社名</span>
                                <va-input v-model.trim="vendorInfo.customerNamej" :maxlength="80" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span class="text-red-600">{{
                                    v$.vendorInfo.customerNo.$errors[0]?.$message ||
                                    v$.vendorInfo.deptCd.$errors[0]?.$message ||
                                    v$.vendorInfo.impExpFlag.$errors[0]?.$message ||
                                    v$.vendorInfo.shiyoBashoFlag.$errors[0]?.$message
                                }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="max-width: 15%">英文会社名</span>
                                <va-input ref="customerNamee" v-model.trim="vendorInfo.customerNamee" :error="v$.vendorInfo.customerNamee.$error" :maxlength="70" />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="max-width: 23%">和文部署名</span>
                                <va-input v-model.trim="vendorInfo.division" :maxlength="60" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7">
                                <span style="max-width: 15%"></span>
                                <span class="text-red-600">{{ v$.vendorInfo.customerNamee.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="max-width: 15%">郵便番号</span>
                                <va-input v-model.trim="vendorInfo.zipCd" :maxlength="7" />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="max-width: 23%">和文住所</span>
                                <va-input v-model.trim="vendorInfo.customerAddej" :maxlength="100" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="min-width: 15%">英文住所</span>
                                <va-input v-model.trim="vendorInfo.customerAdde1" :maxlength="15" />
                                <div class="px-3"></div>
                                <va-input v-model.trim="vendorInfo.customerAdde2" :maxlength="35" />
                                <div class="px-3"></div>
                                <va-input v-model.trim="vendorInfo.customerAdde3" :maxlength="35" />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="max-width: 23%">担当者名</span>
                                <va-input v-model.trim="vendorInfo.charger" :maxlength="40" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="max-width: 15%"></span>
                                <va-input v-model.trim="vendorInfo.customerAdde4" :maxlength="70" />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="min-width: 23%">TEL NO</span>
                                <va-input ref="telNo" v-model.trim="vendorInfo.telNo" :error="v$.vendorInfo.telNo.$error" :maxlength="14" input-class="hide-input-number-arrow" />
                                <span style="min-width: 10%; margin-left: 14px">FAX NO</span>
                                <va-input ref="faxNo" v-model.trim="vendorInfo.faxNo" :error="v$.vendorInfo.faxNo.$error" :maxlength="14" input-class="hide-input-number-arrow" />
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center xs7"></div>
                            <div class="flex items-center xs5">
                                <span style="max-width: 23%"></span>
                                <span class="text-red-600">{{ v$.vendorInfo.telNo.$errors[0]?.$message || v$.vendorInfo.faxNo.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs7">
                                <span style="max-width: 15%">英文住所(一括)</span>
                                <va-input v-model.trim="vendorInfo.customerAddeblAnket" :maxlength="105" />
                            </div>
                            <div class="flex items-center py-2 xs5">
                                <span style="max-width: 23%">メールアドレス</span>
                                <va-input v-model.trim="vendorInfo.emailAdd" :maxlength="100" />
                            </div>
                        </div>

                        <div class="row justify-start items-center mt-1">
                            <div class="flex items-center xs12">
                                <span style="max-width: 9%">備考</span>
                                <va-input type="textarea" v-model.trim="vendorInfo.remarks" :maxlength="1000" />
                            </div>
                        </div>
                        <div class="row justify-start items-center">
                            <div class="flex items-center py-2 xs12">
                                <span style="max-width: 10%"></span>
                                <va-button v-if="isUpdating" class="w-24 flex-grow-0" color="info" size="medium" @click="handleOpenImageModal">イメージ</va-button>
                            </div>
                        </div>

                        <div class="row justify-start items-center border border-solid relative">
                            <div class="absolute top-[-10px] left-[-10px] bg-white w-fit">条件</div>
                            <div class="pl-20 py-2" v-show="conditions.length > 0">
                                <va-switch
                                    v-for="(item, index) in conditions"
                                    :key="index"
                                    v-model.trim="vendorInfo[`conditionFlg${item.value}`]"
                                    :false-value="TOGGLE.FALSE"
                                    :true-value="TOGGLE.TRUE"
                                    size="medium"
                                    class="m-2"
                                >
                                    <template #innerLabel>
                                        <div class="va-text-center w-28">
                                            {{ item.name }}
                                        </div>
                                    </template>
                                </va-switch>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-1 xs8">
                                    <span style="max-width: 15%">記事(荷主)</span>
                                    <va-input v-model.trim="vendorInfo.conditionComment" :maxlength="100" />
                                </div>
                                <div class="flex items-center py-1 xs4">
                                    <span style="min-width: 30%">包括評価番号</span>
                                    <va-input v-model.trim="vendorInfo.incestrepreNo" :maxlength="12" :error="v$.vendorInfo.incestrepreNo.$error" />
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-1 xs8"></div>
                                <div class="flex items-center py-1 xs4">
                                    <span style="min-width: 30%"></span>
                                    <va-input v-model.trim="vendorInfo.incestrepreNo2" :maxlength="12" :error="v$.vendorInfo.incestrepreNo2.$error" />
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-1 xs8">
                                    <span style="min-width: 15%">納期限延長</span>
                                    <va-input v-model.trim="vendorInfo.deliveryDateExtCd" :maxlength="1" />
                                    <span style="min-width: 8%; margin-left: 20px">納付方法</span>
                                    <va-input v-model.trim="vendorInfo.paymethodDisc" :maxlength="1" />
                                    <span style="min-width: 12%; margin-left: 20px">口座番号</span>
                                    <va-input v-model.trim="vendorInfo.conditionBankAccountNo" :maxlength="14" />
                                    <span style="min-width: 8%; margin-left: 20px">担保番号</span>
                                    <va-input v-model.trim="vendorInfo.conditionCollateralNo1" :maxlength="9" />
                                    <span class="mr-1"> </span>
                                    <va-input v-model.trim="vendorInfo.conditionCollateralNo2" :maxlength="9" />
                                </div>
                                <div class="flex items-center py-1 xs4">
                                    <span style="min-width: 30%"></span>
                                    <va-input v-model.trim="vendorInfo.incestrepreNo3" :maxlength="12" :error="v$.vendorInfo.incestrepreNo3.$error" />
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-1 xs8">
                                    <span style="min-width: 15%">評価区分</span>
                                    <va-input v-model.trim="vendorInfo.estimationFlgCd" :maxlength="1" />
                                    <span style="min-width: 8%; margin-left: 20px">保険区分</span>
                                    <va-input v-model.trim="vendorInfo.insuranceClassCd" :maxlength="1" />
                                    <span style="min-width: 12%; margin-left: 20px">包括保険番号</span>
                                    <va-input v-model.trim="vendorInfo.incinsuregNo" :maxlength="8" />
                                    <span style="width: 100%; margin-left: 10px"></span>
                                </div>
                                <div class="flex items-center py-1 xs4">
                                    <span style="min-width: 30%"></span>
                                    <span class="text-red-600">{{
                                        v$.vendorInfo.incestrepreNo.$errors[0]?.$message || v$.vendorInfo.incestrepreNo2.$errors[0]?.$message || v$.vendorInfo.incestrepreNo3.$errors[0]?.$message
                                    }}</span>
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-3 xs4">
                                    <span style="min-width: 47%">税関事務管理人コード</span>
                                    <va-input v-model.trim="vendorInfo.customsOfficeJanitorCd" :maxlength="17" />
                                </div>

                                <div class="flex items-center py-3 xs4">
                                    <span style="min-width: 50%">荷主セクションコード</span>
                                    <va-input v-model.trim="vendorInfo.shipperssecCd" :maxlength="20" />
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-3 xs4">
                                    <span style="min-width: 47%">税関事務管理人受理番号</span>
                                    <va-input v-model.trim="vendorInfo.customsOfficeJanitorreNo" :maxlength="10" />
                                </div>

                                <div class="flex items-center py-3 xs4">
                                    <span style="min-width: 50%">荷主リファレンスナンバー</span>
                                    <va-input v-model.trim="vendorInfo.shippersrefNo" :maxlength="35" />
                                </div>
                            </div>

                            <div class="row items-center">
                                <div class="flex items-center py-3 xs6">
                                    <span style="max-width: 30%">税関事務管理人名</span>
                                    <va-input v-model.trim="vendorInfo.customsOfficeJanitorName" :maxlength="70" />
                                </div>
                            </div>
                        </div>

                        <div v-if="isUpdating" class="px-10 pt-2">
                            <div class="row items-center">
                                <div class="flex items-center py-2 xs3">
                                    <span style="min-width: 30%">登録者</span>
                                    <va-input readonly v-model.trim="vendorInfo.reguserCd" />
                                </div>
                                <div class="flex items-center py-2 xs3">
                                    <span style="min-width: 30%">登録日時</span>
                                    <va-input readonly v-model.trim="vendorInfo.regDate" />
                                </div>
                            </div>
                            <div class="row items-center">
                                <div class="flex items-center py-2 xs3">
                                    <span style="min-width: 30%">変更者</span>
                                    <va-input readonly v-model.trim="vendorInfo.updateUserCd" />
                                </div>
                                <div class="flex items-center py-2 xs3">
                                    <span style="min-width: 30%">変更日時</span>
                                    <va-input readonly v-model.trim="vendorInfo.updateDate" />
                                </div>
                            </div>
                        </div>
                        <div class="row justify-end p-2">
                            <div v-if="isUpdating" class="item flex w-[120px]">
                                <va-button @click="handleDelBtnClicked" color="danger" size="medium">削除</va-button>
                            </div>
                            <div v-if="isUpdating" class="item flex w-[120px]">
                                <va-button @click="handleUpdateBtnClicked" color="info" size="medium">登録</va-button>
                            </div>
                            <div class="item flex w-[120px]">
                                <va-button @click="handleRegisterBtnClicked" color="info" size="medium">{{ registerBtnText }}</va-button>
                            </div>
                            <div class="item flex w-[120px]">
                                <va-button color="secondary" size="medium" @click="handleCloseModal">閉じる</va-button>
                            </div>
                        </div>
                    </div>
                </va-inner-loading>
            </template>
        </va-modal>

        <!-- ImageListResultModal -->
        <va-modal :modelValue="showImageModal" @update:modelValue="handleCloseImageModal" class="p-0" size="large" no-outside-dismiss hide-default-actions no-padding>
            <template #header>
                <div class="flex justify-between items-center w-full pl-4 p-3">
                    <span class="m-0 w-fit flex-grow-0">イメージ登録</span>
                    <va-icon name="va-clear" class="flex-grow-0" @click="handleCloseImageModal" />
                </div>
                <div class="bg-slate-200 h-[1px]"></div>
            </template>
            <template #default>
                <va-inner-loading :loading="isLoading" :size="60">
                    <div class="w-[900px] px-4 pb-4">
                        <div class="row justify-start items-center">
                            <div class="flex items-center pt-3 xs2">
                                <span style="">イメージ名</span>
                            </div>
                            <div class="flex items-center pt-3 xs8">
                                <va-input v-model.trim="currentFile.name" maxlength="60" />
                            </div>
                            <div class="flex items-center pt-3 xs2">
                                <input ref="fileUpload" type="file" hidden @change="handleChooseFile" @click="handleClickUploadBtn" accept=".pdf" />
                                <va-button color="info" size="medium" @click="handleUploadBtnClicked">選択</va-button>
                            </div>
                        </div>

                        <div class="row justify-end items-center">
                            <div class="flex items-center py-3 xs2">
                                <va-button color="info" size="medium" @click="handleRegisterImageBtnClicked">登録</va-button>
                            </div>
                            <div class="flex items-center py-3 xs2">
                                <va-button color="danger" size="medium" :disabled="isSelectedFileEmpty" @click="onModalDeletePdfBtnClicked">削除</va-button>
                            </div>
                        </div>
                        <div class="row justify-start items-center">
                            <div class="flex items-center py-3 xs12">
                                <ImageListResult :resultRowData="vendorImageInfo.listImageTraderNoMasterDto" @onTableRowSelection="onTableRowSelection" @handleViewPdf="handleViewPdf" />
                            </div>
                        </div>
                        <div class="row justify-end items-center">
                            <div class="flex items-center py-3 xs2">
                                <va-button color="info" size="medium" @click="handleCloseImageModal">閉じる</va-button>
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
import ImageListResult from './ImageListResult.vue'
import { notificationSuccess, notificationError } from '../../components/Notification/NotificationApi.vue'
import { requiredRules, fieldMaxLength, fieldInputTextValidate, ONE_BYTE_REGEX, NUMBER_REGEX } from '@/utils/validator'
import { NOTIFICATION_DURATION } from '@/utils/utilities'
import {
    SYSTEM_ERROR_MESSAGE,
    UPDATE_ERROR_MESSAGE,
    DELETE_ERROR_MESSAGE,
    DELETE_SUCCESS_MESSAGE,
    DATA_NOT_EXIST_MESSAGE,
    REGISTER_ERROR_MESSAGE,
    INVALID_INPUT_MESSAGE,
    IMAGE_UPLOAD_SUCCESS_MESSAGE,
    IMAGE_UPLOAD_ERROR_MESSAGE,
    IMAGE_SIZE_LIMIT_MESSAGE,
    IMAGE_FILE_TYPE_PDF_MESSAGE,
    IMAGE_NOT_NULL_MESSAGE,
    IMAGE_NOT_EXIST_MESSAGE,
    getDetailSuccessMessage,
    DELETE_CONFIRM_ACTION_MESSAGE,
} from '@/utils/messages'

export default {
    components: {
        ImageListResult,
    },
    props: {
        showModal: Boolean,
        modalAction: String,
        modalDropdownOptions: Object,
        vendorQueryDetail: Object,
    },
    data() {
        return {
            v$: useVuelidate(),
            TOGGLE: {
                TRUE: '1',
                FALSE: '',
            },
            isLoading: false,
            showImageModal: false,
            conditions: [],

            vendorInfo: {
                customerNo: '',
                deptCd: '',
                impExpFlag: '',
                shiyoBashoFlag: '',
                seq: '',
                customerNamee: '',
                zipCd: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeblAnket: '',
                customerNamej: '',
                customerAddej: '',
                division: '',
                charger: '',
                telNo: '',
                faxNo: '',
                emailAdd: '',
                remarks: '',
                conditionComment: '',
                deliveryDateExtCd: '',
                paymethodDisc: '',
                conditionBankAccountNo: '',
                conditionCollateralNo1: '',
                conditionCollateralNo2: '',
                estimationFlgCd: '',
                insuranceClassCd: '',
                incinsuregNo: '',
                incestrepreNo: '',
                incestrepreNo2: '',
                incestrepreNo3: '',
                customsOfficeJanitorCd: '',
                customsOfficeJanitorreNo: '',
                customsOfficeJanitorName: '',
                shipperssecCd: '',
                shippersrefNo: '',
                reguserCd: '',
                regDate: '',

                conditionFlg01: '',
                conditionFlg02: '',
                conditionFlg03: '',
                conditionFlg04: '',
                conditionFlg05: '',
                conditionFlg06: '',
                conditionFlg07: '',
                conditionFlg08: '',
                conditionFlg09: '',
                conditionFlg10: '',
                conditionFlg11: '',
                conditionFlg12: '',
                conditionFlg13: '',
                conditionFlg14: '',
                conditionFlg15: '',
                conditionFlg16: '',
                conditionFlg17: '',
                conditionFlg18: '',
                conditionFlg19: '',
                conditionFlg20: '',
                conditionFlg21: '',
                conditionFlg22: '',
                conditionFlg23: '',
                conditionFlg24: '',
                conditionFlg25: '',
            },
            vendorImageInfo: {
                listImageTraderNoMasterDto: [],
            },
            currentFile: {
                name: '',
            },
            choseFile: null,
            currentSelectedRow: undefined,
        }
    },
    computed: {
        isUpdating() {
            return this.modalAction === 'update'
        },
        registerBtnText() {
            return this.isUpdating ? '新規登録' : '登録'
        },
        modalTitle() {
            return this.isUpdating ? '明細' : '新規登録'
        },
        isSelectedFileEmpty() {
            return this.currentSelectedRow === undefined
        },
        ocsdeptCd() {
            return this.vendorInfo.impExpFlag + this.vendorInfo.shiyoBashoFlag + this.vendorInfo.seq
        },
    },
    watch: {
        'vendorInfo.impExpFlag': function (newVal) {
            // 0:Original | 1:Import | 2:Export
            if (newVal === '0') this.conditions = []
            else if (newVal === '1') this.conditions = this.modalDropdownOptions?.listConditionImport
            else this.conditions = this.modalDropdownOptions?.listConditionExport
        },
        showModal(currentVal) {
            if (currentVal === true && this.showImageModal === false) {
                if (!this.isUpdating) this.clearData()
                else this.initCurrentUser(this.vendorQueryDetail)
            }
        },
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        handleimpExpFlagChangeEvent(e) {
            this.vendorInfo.impExpFlag = e
            this.clearCondition()
        },

        handleCloseModal() {
            this.$emit('closeDetailModal')
            this.clearData()
            this.v$.$reset()
        },

        handleOpenImageModal() {
            this.initImageInfo()
        },

        handleCloseImageModal() {
            this.$emit('openDetailModal', 'update', this.vendorQueryDetail)
            this.currentFile.name = ''
            this.showImageModal = false
        },

        // Check if file size is greater than 10mb
        fileSizeCheck(fileSize) {
            return fileSize < 10000000
        },

        // Check if file type is PDF
        fileTypeCheck(fileType) {
            return fileType === 'application/pdf'
        },

        // File Validations
        fileValidationCheck(file) {
            if (!this.fileSizeCheck(file?.size)) {
                notificationError(IMAGE_SIZE_LIMIT_MESSAGE)
                return false
            }
            if (!this.fileTypeCheck(file?.type)) {
                notificationError(IMAGE_FILE_TYPE_PDF_MESSAGE)
                return false
            }

            return true
        },

        // This allow handleChooseFile action to perform when user choose to select the same file again
        handleClickUploadBtn(e) {
            e.target.value = ''
        },

        // User select file they want to upload
        handleChooseFile() {
            const fileInputElement = this.$refs.fileUpload
            if (this.fileValidationCheck(fileInputElement.files[0])) {
                this.currentFile.name = fileInputElement.files[0].name
                this.choseFile = fileInputElement.files[0]
            }
        },

        // File upload
        handleUploadBtnClicked() {
            this.$refs.fileUpload.click()
        },

        // Get current selected row
        onTableRowSelection(selectedRow) {
            this.currentSelectedRow = selectedRow
        },

        resetFileInput() {
            const fileInputElement = this.$refs.fileUpload
            fileInputElement.value = ''
            this.choseFile = null
            this.currentFile.name = ''
        },

        // E007: Register new user
        async handleRegisterBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.changeLoading()
                this.axios
                    .post('api/occm001/trader/regist', this.vendorInfo)
                    .then((res) => {
                        notificationSuccess(`業者情報：${this.vendorInfo.customerNo}の登録が成功しました。`, '', NOTIFICATION_DURATION)
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

        // E008: Get a specific vendor info
        initCurrentUser(vendorQueryDetail) {
            this.changeLoading()
            this.axios
                .get(`api/occm001/trader/detail`, { params: vendorQueryDetail })
                .then((res) => {
                    this.vendorInfo = res.data
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
                .delete(`api/occm001/trader/delete`, {
                    data: {
                        customerNo: this.vendorInfo.customerNo,
                        deptCd: this.vendorInfo.deptCd,
                        ocsdeptCd: this.ocsdeptCd,
                    },
                })
                .then((res) => {
                    notificationSuccess(getDetailSuccessMessage('delete', '業者No', this.vendorInfo.customerNo), '', NOTIFICATION_DURATION)
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
                    .put('api/occm001/trader/update', {
                        ...this.vendorInfo,
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

        // E012: Init Image Modal
        initImageInfo() {
            this.changeLoading()
            this.axios
                .get(`api/occm001/trader/image/list`, { params: { customerNo: this.vendorInfo.customerNo } })
                .then((res) => {
                    this.showImageModal = true
                    this.vendorImageInfo = res.data
                    this.$emit('closeDetailModal')
                })
                .catch((error) => {
                    if (error.response.data.message) notificationError(error.response.data.message)
                    else notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                    this.handleCloseImageModal()
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // E014: Upload PDF Images to AWS
        handleUploadImage() {
            this.changeLoading()
            const myFormData = new FormData()
            myFormData.append('customerNo', this.vendorInfo.customerNo)
            myFormData.append('fileName', `${this.currentFile.name}_${this.vendorInfo.seq}`)
            myFormData.append('file', this.choseFile)
            this.axios
                .post('/api/occm001/trader/image/upload', myFormData, {
                    headers: { 'content-type': 'multipart/form-data' },
                })
                .then((res) => {
                    notificationSuccess(IMAGE_UPLOAD_SUCCESS_MESSAGE, '', NOTIFICATION_DURATION)
                    this.initImageInfo()
                    this.resetFileInput()
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(IMAGE_UPLOAD_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        handleRegisterImageBtnClicked() {
            if (this.choseFile === null) {
                notificationError(IMAGE_NOT_NULL_MESSAGE)
            } else this.handleUploadImage()
        },

        // Ask for confirmation before delete pdf file
        async onModalDeletePdfBtnClicked() {
            const warningText = DELETE_CONFIRM_ACTION_MESSAGE
            if (confirm(warningText)) this.handleFileDelete()
        },

        // E015: View selected PDF file
        handleViewPdf(rowData) {
            this.changeLoading()
            this.axios
                .get('/api/occm001/trader/image/view', {
                    params: {
                        imageName: rowData.imageName,
                        imagePath: rowData.imagePath,
                    },
                })
                .then((res) => {
                    window.open(res.request.responseURL, '_blank', 'noreferrer')
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(IMAGE_NOT_EXIST_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // E016: Delete pdf file from DB
        handleFileDelete() {
            this.changeLoading()
            this.axios
                .delete('/api/occm001/trader/image/delete', {
                    data: {
                        customerNo: this.currentSelectedRow.customerNo,
                        seq: this.currentSelectedRow.seq,
                        imagePath: this.currentSelectedRow.imagePath,
                    },
                })
                .then(() => {
                    notificationSuccess(DELETE_SUCCESS_MESSAGE, '', NOTIFICATION_DURATION)
                    this.initImageInfo()
                    this.currentSelectedRow = undefined
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(DELETE_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // Reset Modal data fields
        clearData() {
            this.vendorInfo = {
                customerNo: '',
                deptCd: '',
                impExpFlag: '',
                shiyoBashoFlag: '',
                seq: '',
                customerNamee: '',
                zipCd: '',
                customerAdde1: '',
                customerAdde2: '',
                customerAdde3: '',
                customerAdde4: '',
                customerAddeblAnket: '',
                customerNamej: '',
                customerAddej: '',
                division: '',
                charger: '',
                telNo: '',
                faxNo: '',
                emailAdd: '',
                remarks: '',
                conditionComment: '',
                deliveryDateExtCd: '',
                paymethodDisc: '',
                conditionBankAccountNo: '',
                conditionCollateralNo1: '',
                conditionCollateralNo2: '',
                estimationFlgCd: '',
                insuranceClassCd: '',
                incinsuregNo: '',
                incestrepreNo: '',
                incestrepreNo2: '',
                incestrepreNo3: '',
                customsOfficeJanitorCd: '',
                customsOfficeJanitorreNo: '',
                customsOfficeJanitorName: '',
                shipperssecCd: '',
                shippersrefNo: '',
                reguserCd: '',
                regDate: '',

                conditionFlg01: '',
                conditionFlg02: '',
                conditionFlg03: '',
                conditionFlg04: '',
                conditionFlg05: '',
                conditionFlg06: '',
                conditionFlg07: '',
                conditionFlg08: '',
                conditionFlg09: '',
                conditionFlg10: '',
                conditionFlg11: '',
                conditionFlg12: '',
                conditionFlg13: '',
                conditionFlg14: '',
                conditionFlg15: '',
                conditionFlg16: '',
                conditionFlg17: '',
                conditionFlg18: '',
                conditionFlg19: '',
                conditionFlg20: '',
                conditionFlg21: '',
                conditionFlg22: '',
                conditionFlg23: '',
                conditionFlg24: '',
                conditionFlg25: '',
            }
            this.conditions = []
        },

        // Clear Condition when switch Flg
        clearCondition() {
            this.vendorInfo = {
                ...this.vendorInfo,
                conditionFlg01: '',
                conditionFlg02: '',
                conditionFlg03: '',
                conditionFlg04: '',
                conditionFlg05: '',
                conditionFlg06: '',
                conditionFlg07: '',
                conditionFlg08: '',
                conditionFlg09: '',
                conditionFlg10: '',
                conditionFlg11: '',
                conditionFlg12: '',
                conditionFlg13: '',
                conditionFlg14: '',
                conditionFlg15: '',
                conditionFlg16: '',
                conditionFlg17: '',
                conditionFlg18: '',
                conditionFlg19: '',
                conditionFlg20: '',
                conditionFlg21: '',
                conditionFlg22: '',
                conditionFlg23: '',
                conditionFlg24: '',
                conditionFlg25: '',
            }
        },
    },
    validations() {
        return {
            vendorInfo: {
                customerNo: { required: requiredRules('業者No'), regex: fieldInputTextValidate('業者No', ONE_BYTE_REGEX) },
                deptCd: { required: requiredRules('N枝番'), regex: fieldInputTextValidate('N枝番', ONE_BYTE_REGEX) },
                impExpFlag: { required: requiredRules('輸出入フラグ') },
                shiyoBashoFlag: { required: requiredRules('使用場所') },
                customerNamee: { required: requiredRules('英文会社名') },
                incestrepreNo: { regex: fieldInputTextValidate('包括評価番号', ONE_BYTE_REGEX) },
                incestrepreNo2: { regex: fieldInputTextValidate('包括評価番号', ONE_BYTE_REGEX) },
                incestrepreNo3: { regex: fieldInputTextValidate('包括評価番号', ONE_BYTE_REGEX) },
                telNo: { required: requiredRules('Tel No'), maxLength: fieldMaxLength('Tel No', 15), regex: fieldInputTextValidate('Tel No', NUMBER_REGEX) },
                faxNo: { maxLength: fieldMaxLength('Fax No', 15), regex: fieldInputTextValidate('Fax No', NUMBER_REGEX) },
            },
        }
    },
}
</script>

<style >
.va-modal--size-large,
.va-modal--size-large .va-modal__inner {
    max-width: 1400px;
}
</style>
