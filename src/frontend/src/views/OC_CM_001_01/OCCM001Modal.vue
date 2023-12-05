<template>
    <div>
        <va-modal :modelValue="showModal" @update:modelValue="handleCloseModal" class="p-0" size="large" no-outside-dismiss hide-default-actions no-padding>
            <template #header>
                <div class="flex justify-between items-center w-full pl-4 p-3">
                    <span class="m-0 text-lg w-fit flex-grow-0">{{ modalTitle }}</span>
                    <va-icon name="va-clear" class="flex-grow-0" @click="handleCloseModal" />
                </div>
                <div class="bg-slate-200 h-[1px] mb-2"></div>
            </template>

            <template #default>
                <va-inner-loading :loading="isLoading" :size="60">
                    <div class="min-w-[1400px] px-4 pb-4 max-h-[850px] overflow-auto">
                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">ユーザーコード</span>
                                <va-input ref="userCd" v-model.trim="userInfo.userCd" :error="v$.userInfo.userCd.$error" :maxlength="20" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">管理権限</span>
                                <va-select
                                    v-model.trim="userInfo.usermanagementAuthorityCd"
                                    :options="modalDropdownOptions?.listAdministrativeAuthority"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                />
                            </div>
                        </div>

                        <div class="row items-center">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%"></span>
                                <span v-show="v$.userInfo.userCd.$error" class="text-red-600">{{ v$.userInfo.userCd.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">ユーザー名(漢字)</span>
                                <va-input ref="username" v-model.trim="userInfo.username" :error="v$.userInfo.username.$error" :maxlength="30" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">業務権限</span>
                                <va-select
                                    v-model.trim="userInfo.userAuthorityCd"
                                    :options="modalDropdownOptions?.listBusinessAuthority"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                />
                            </div>
                        </div>

                        <div class="row items-center">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%"></span>
                                <span v-show="v$.userInfo.username.$error" class="text-red-600">{{ v$.userInfo.username.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">ユーザー名(カナ)</span>
                                <va-input v-model.trim="userInfo.usernameSyllabary" :maxlength="30" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">所属会社</span>
                                <va-select
                                    v-model.trim="userInfo.userCompanyCd"
                                    :options="modalDropdownOptions?.listAffiliatedCompany"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                />
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">ユーザー名(英)</span>
                                <va-input v-model.trim="userInfo.usernameEng" :maxlength="60" :error="v$.userInfo.usernameEng.$error" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">所属部署</span>
                                <va-select
                                    v-model.trim="userInfo.departmentCd"
                                    :options="modalDropdownOptions?.listBelongingDepartment"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                />
                            </div>
                        </div>

                        <div class="row items-center">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%"></span>
                                <span v-show="v$.userInfo.usernameEng.$error" class="text-red-600">{{ v$.userInfo.usernameEng.$errors[0]?.$message }}</span>
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">パスワード</span>
                                <va-input ref="password" v-model="userInfo.password" :type="isPasswordVisible01 ? 'text' : 'password'" :error="v$.userInfo.password.$error" :maxlength="20">
                                    <template #appendInner>
                                        <va-icon :name="isPasswordVisible01 ? 'visibility_off' : 'visibility'" size="small" color="--va-primary" @click="isPasswordVisible01 = !isPasswordVisible01" />
                                    </template>
                                </va-input>
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">業務グループ</span>
                                <va-select
                                    v-model.trim="userInfo.userAuthorityGroupCd"
                                    :options="modalDropdownOptions?.listBusinessGroup"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                />
                            </div>
                        </div>

                        <div class="row items-center">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%"></span>
                                <span v-show="v$.userInfo.password.$error" class="text-red-600">
                                    {{ v$.userInfo.password.$errors[0]?.$message }}
                                </span>
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">印刷用ユーザー名</span>
                                <va-input v-model.trim="userInfo.printUsername" :maxlength="30" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">使用開始日</span>
                                <Datepicker
                                    v-model.trim="userInfo.startDate"
                                    :auto-apply="true"
                                    :format="DatePickerFormat"
                                    :enable-time-picker="false"
                                    :clearable="false"
                                    style="width: 100px"
                                    :state="v$.userInfo.startDate.$error ? false : null"
                                />
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">印刷用会社名</span>
                                <va-input v-model.trim="userInfo.printUserCompanyNm" :maxlength="30" />
                            </div>
                            <div class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">使用終了日</span>
                                <Datepicker
                                    v-model.trim="userInfo.endDate"
                                    :auto-apply="true"
                                    :format="DatePickerFormat"
                                    :enable-time-picker="false"
                                    :clearable="false"
                                    style="width: 100px"
                                    :state="v$.userInfo.endDate.$error ? false : null"
                                />
                            </div>
                        </div>

                        <div class="row items-center py-2">
                            <div class="flex items-center xs6">
                                <span style="max-width: 25%">印刷用会社</span>
                                <va-input v-model.trim="userInfo.printUserCompanyCd" :maxlength="10" />
                            </div>
                            <div v-show="isUpdating" class="flex items-center xs6 pl-10">
                                <span style="max-width: 20%">前回パスワード</span>
                                <va-input :type="isPasswordVisible02 ? 'text' : 'password'" v-model="userInfo.prePassword" readonly>
                                    <template #appendInner>
                                        <va-icon :name="isPasswordVisible02 ? 'visibility_off' : 'visibility'" size="small" color="--va-primary" @click="isPasswordVisible02 = !isPasswordVisible02" />
                                    </template>
                                </va-input>
                            </div>
                        </div>

                        <div v-show="isUpdating">
                            <div class="row items-center py-2">
                                <div class="flex items-center xs6">
                                    <span style="max-width: 25%">登録者</span>
                                    <va-input readonly v-model.trim="userInfo.regUsername" />
                                </div>
                                <div class="flex items-center xs6 pl-10">
                                    <span style="max-width: 20%">登録日時</span>
                                    <va-input readonly v-model.trim="userInfo.regDate" />
                                </div>
                            </div>
                            <div class="row items-center py-2">
                                <div class="flex items-center xs6">
                                    <span style="max-width: 25%">変更者</span>
                                    <va-input readonly v-model.trim="userInfo.updateUsername" />
                                </div>
                                <div class="flex items-center xs6 pl-10">
                                    <span style="max-width: 20%">変更日時</span>
                                    <va-input readonly v-model.trim="userInfo.updateDate" />
                                </div>
                            </div>
                        </div>

                        <div class="row justify-end pt-4">
                            <div v-if="isUpdating" class="item flex xs1">
                                <va-button color="danger" size="medium" @click="onModalDeleteUserBtnClicked">削除</va-button>
                            </div>
                            <div v-if="isUpdating" class="item flex xs1">
                                <va-button color="info" size="medium" @click="onUpdateBtnClicked">更新</va-button>
                            </div>
                            <div class="item flex xs1">
                                <va-button color="info" size="medium" @click="onRegisterBtnClicked">{{ registerBtnText }}</va-button>
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
import Datepicker from '@vuepic/vue-datepicker'
import { useVuelidate } from '@vuelidate/core'
import { dateFormat, DATE_PICKER_FORMAT, NOTIFICATION_DURATION, currentDateTime, lastDayOfCurrentMonth } from '@/utils/utilities'
import '@vuepic/vue-datepicker/dist/main.css'
import { notificationSuccess, notificationError } from '../../components/Notification/NotificationApi.vue'
import { requiredRules, fieldMaxLength, fieldInputTextValidate, ONE_BYTE_REGEX } from '@/utils/validator'
import { DELETE_ERROR_MESSAGE, SYSTEM_ERROR_MESSAGE, REGISTER_ERROR_MESSAGE, getDetailSuccessMessage, DELETE_CONFIRM_ACTION_MESSAGE } from '@/utils/messages'

export default {
    props: {
        params: Object,
        showModal: Boolean,
        modalAction: String,
        queryUserCd: String,
        modalDropdownOptions: Object,
    },
    components: {
        Datepicker,
    },
    data() {
        return {
            v$: useVuelidate(),
            isPasswordVisible01: false,
            isPasswordVisible02: false,
            isLoading: false,
            userInfo: {
                userCd: '',
                username: '',
                usernameSyllabary: '',
                usernameEng: '',
                usermanagementAuthorityCd: '',
                userAuthorityCd: '',
                userCompanyCd: '',
                departmentCd: '',
                userAuthorityGroupCd: '',
                printUsername: '',
                printUserCompanyNm: '',
                printUserCompanyCd: '',
                password: '',
                startDate: currentDateTime(),
                endDate: lastDayOfCurrentMonth(),
                prePassword: '',
                regUserCd: '',
                regUsername: '',
                regDate: '',
                updateUserCd: '',
                updateUsername: '',
                updateDate: '',
            },
            DatePickerFormat: DATE_PICKER_FORMAT,
        }
    },
    watch: {
        // Trigger when modal open
        showModal(currentVal) {
            if (currentVal === true) {
                if (!this.isUpdating) this.clearData()
                else this.initCurrentUser(this.queryUserCd)
            }
        },
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

        unwrap({
            userCd,
            username,
            usernameSyllabary,
            usernameEng,
            usermanagementAuthorityCd,
            userAuthorityCd,
            userCompanyCd,
            departmentCd,
            userAuthorityGroupCd,
            printUsername,
            printUserCompanyNm,
            printUserCompanyCd,
            password,
            startDate,
            endDate,
        }) {
            return {
                userCd,
                username,
                usernameSyllabary,
                usernameEng,
                usermanagementAuthorityCd,
                userAuthorityCd,
                userCompanyCd,
                departmentCd,
                userAuthorityGroupCd,
                printUsername,
                printUserCompanyNm,
                printUserCompanyCd,
                password,
                startDate: dateFormat(startDate),
                endDate: dateFormat(endDate),
            }
        },

        // Ask for confirmation before delete user
        async onModalDeleteUserBtnClicked() {
            const warningText = DELETE_CONFIRM_ACTION_MESSAGE
            if (confirm(warningText)) this.handleDeleteUser()
        },

        // E004: Delete selected user
        handleDeleteUser() {
            this.changeLoading()
            this.axios
                .delete(`api/occm001/user/delete/${this.userInfo.userCd}`)
                .then((res) => {
                    notificationSuccess(getDetailSuccessMessage('delete', 'ユーザー', this.userInfo.userCd), '', NOTIFICATION_DURATION)
                    this.changeLoading()
                    this.handleCloseModal()
                    this.$emit('searchImportData', null)
                })
                .catch((error) => {
                    this.changeLoading()
                    notificationError(DELETE_ERROR_MESSAGE, error.message)
                })
        },

        // E007: Get a specific user info
        initCurrentUser(userCd) {
            this.changeLoading()
            this.axios
                .get(`api/occm001/user/info/${userCd}`)
                .then((res) => {
                    this.userInfo = res.data
                })
                .catch((error) => {
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                    this.handleCloseModal()
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // E008: Update user info
        async onUpdateBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.changeLoading()
                const putData = this.unwrap(this.userInfo)
                this.axios
                    .put('api/occm001/user/update', putData)
                    .then((res) => {
                        notificationSuccess(res.data.message)
                        this.changeLoading()
                        this.handleCloseModal()
                        this.$emit('searchImportData', null)
                    })
                    .catch((error) => {
                        console.log(error)
                        notificationError(error.response.data.message)
                        this.changeLoading()
                    })
            } else {
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        // E010: Register new user
        async onRegisterBtnClicked() {
            const isFormCorrect = await this.v$.$validate()
            if (isFormCorrect) {
                this.changeLoading()
                const putData = this.unwrap(this.userInfo)
                this.axios
                    .post('api/occm001/user/regist', putData)
                    .then((res) => {
                        notificationSuccess(getDetailSuccessMessage('register', 'ユーザー', this.userInfo.userCd), '', NOTIFICATION_DURATION)
                        this.changeLoading()
                        this.handleCloseModal()
                        if (this.isUpdating) this.$emit('searchImportData', null)
                    })
                    .catch((error) => {
                        console.log(error)
                        if (error.request.status === 400) notificationError(error.response.data.message)
                        else notificationError(REGISTER_ERROR_MESSAGE)
                        this.changeLoading()
                    })
            } else {
                this.$refs[this.v$.$errors[0].$property].focus()
            }
        },

        // Reset Modal data fields
        clearData() {
            this.userInfo = {
                userCd: '',
                username: '',
                usernameSyllabary: '',
                usernameEng: '',
                usermanagementAuthorityCd: '',
                userAuthorityCd: '',
                userCompanyCd: '',
                departmentCd: '',
                userAuthorityGroupCd: '',
                printUsername: '',
                printUserCompanyNm: '',
                printUserCompanyCd: '',
                password: '',
                startDate: currentDateTime(),
                endDate: lastDayOfCurrentMonth(),
                prePassword: '',
                regUsername: '',
                regDate: '',
                updateUserCd: '',
                updateUsername: '',
                updateDate: '',
            }
            this.isPasswordVisible01 = false
            this.isPasswordVisible02 = false
        },
    },
    validations() {
        return {
            userInfo: {
                userCd: { required: requiredRules('ユーザーコード'), maxLength: fieldMaxLength('ユーザーコード', 20), regex: fieldInputTextValidate('ユーザーコード', ONE_BYTE_REGEX) },
                username: { required: requiredRules('ユーザー名(漢字)'), maxLength: fieldMaxLength('ユーザー名(漢字)', 30) },
                usernameEng: { regex: fieldInputTextValidate('ユーザー名(英)', ONE_BYTE_REGEX) },
                password: {
                    required: requiredRules('パスワード'),
                    regex: fieldInputTextValidate('パスワード', ONE_BYTE_REGEX),
                },
                startDate: { required: requiredRules('使用開始日') },
                endDate: { required: requiredRules('使用終了日') },
            },
        }
    },
}
</script>
