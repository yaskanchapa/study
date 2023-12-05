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
                    <div class="w-[1000px] p-4">
                        <div class="row items-center my-2">
                            <div class="flex items-center">
                                <div class="grow-0 w-[80px]">条件</div>
                                <div v-for="option in hawbInitData.listCondition" :key="option.value" class="justify-start w-[200px]">
                                    <input name="rdoCondition" type="radio" v-model.trim="searchCondition.rdoCondition" :value="option.value" :id="'rdoCondition-' + option.value" />
                                    <label class="mx-1" :for="'rdoCondition-' + option.value">
                                        {{ option.name }}
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="row items-center my-2">
                            <div class="flex items-center">
                                <div class="grow-0 w-[80px]">対象</div>
                                <div v-for="option in radioFirstRow" :key="option.value" class="justify-start w-[200px]">
                                    <input name="secondRadio" type="radio" v-model.trim="secondRadio" :value="option.value" :id="'secondRadio-' + option.value" />
                                    <label class="mx-1" :for="'secondRadio-' + option.value">
                                        {{ option.name }}
                                    </label>
                                </div>
                            </div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center w-full">
                                <div class="grow-0 w-[80px]"></div>
                                <div v-for="(option, index) in radioSecondRow" :key="index" class="justify-start w-[200px]">
                                    <div v-if="option !== null">
                                        <input name="secondRadio" type="radio" v-model.trim="secondRadio" :value="option.value" :id="'secondRadio-' + option.value" />
                                        <label class="mx-1" :for="'secondRadio-' + option.value">
                                            {{ option.name }}
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center w-full">
                                <div class="grow-0 w-[80px]"></div>
                                <div v-for="(option, index) in radioThirdRow" :key="index" class="xs2 justify-start w-[200px]">
                                    <div v-if="option !== null">
                                        <input name="secondRadio" type="radio" v-model.trim="secondRadio" :value="option.value" :id="'secondRadio-' + option.value" />
                                        <label class="mx-1" :for="'secondRadio-' + option.value">
                                            {{ option.name }}
                                        </label>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center xs1"></div>
                            <div class="flex items-center xs6 pl-[30px]">
                                <span class="min-w-[25%]">確認項目</span>
                                <va-select
                                    v-model.trim="searchCondition.confirmationItem"
                                    :options="hawbInitData.listConfirm"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                    searchable
                                />
                            </div>
                            <div class="flex items-center xs1"></div>
                            <div class="flex items-center xs4 pl-[40px]">
                                <span class="min-w-[25%]">通関業者</span>
                                <va-select
                                    v-model.trim="searchCondition.customsBroker"
                                    :options="hawbInitData.listCustomsBroker"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    searchable
                                />
                            </div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center xs1"></div>
                            <div class="flex items-center xs6 pl-[30px]">
                                <span class="min-w-[25%]">詳細</span>
                                <va-select
                                    v-model.trim="searchCondition.detail"
                                    :options="listDataList"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                    searchable
                                />
                            </div>
                            <div class="flex items-center xs1"></div>
                            <div class="flex items-center xs4 pl-[40px]">
                                <span class="min-w-[25%]">通関場所</span>
                                <va-input v-model="searchCondition.customsClearancePlace" class="min-w-[25%]" />
                            </div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center xs1"></div>
                            <div class="relative flex items-center xs6 pl-[30px]">
                                <div class="absolute left-[10px]">
                                    <input
                                        name="secondRadio"
                                        type="radio"
                                        v-model.trim="secondRadio"
                                        :value="this.hawbInitData.listSubject[6].value"
                                        :id="'secondRadio-' + this.hawbInitData.listSubject[6].value"
                                    />
                                </div>
                                <label class="max-w-[25%]" :for="'secondRadio-' + this.hawbInitData.listSubject[6].value">
                                    {{ this.hawbInitData.listSubject[6].name }}
                                </label>
                                <va-select
                                    v-model.trim="searchCondition.cmbCustomsClearanceSituation"
                                    :options="hawbInitData.listCustomsClearanceStatus"
                                    :text-by="(option) => option.value + ' : ' + option.name"
                                    :track-by="(option) => option.value + ' : ' + option.name"
                                    :value-by="(option) => option.value"
                                    clearable
                                    searchable
                                />
                            </div>
                        </div>
                        <div class="row items-center my-2">
                            <div class="flex items-center xs2">HAWB No.</div>
                        </div>

                        <div class="row items-center my-2">
                            <div class="flex items-center xs1"></div>
                            <div class="flex items-center xs6">
                                <HawbScanTable @updateListHawbNo="updateListHawbNo" :rdoCondition="searchCondition.rdoCondition" />
                            </div>
                            <div class="flex flex-col items-center gap-4 xs5">
                                <div class="item flex w-[120px]">
                                    <va-button color="info" size="medium" @click="handleRegisterButtonClicked">登録</va-button>
                                </div>
                                <div class="item flex w-[120px]">
                                    <va-button color="danger" size="medium" @click="handleReleaseButtonClicked" :disabled="!canRelease">解除</va-button>
                                </div>

                                <div class="item flex w-[120px]">
                                    <va-button color="secondary" size="medium" @click="handleCloseModal">閉じる</va-button>
                                </div>
                            </div>
                        </div>
                    </div>
                </va-inner-loading>
            </template>
        </va-modal>
    </div>
</template>

<script>
import { notificationError, notificationSuccess } from '../../components/Notification/NotificationApi.vue'
import { NOTIFICATION_DURATION } from '../../utils/utilities'
import { SYSTEM_ERROR_MESSAGE } from '../../utils/messages'
import HawbScanTable from './HawbScanTable.vue'

export default {
    props: {
        showModal: Boolean,
        hawbInitData: Object,
        hawbScanProps: Object,
    },
    components: {
        HawbScanTable,
    },
    watch: {
        showModal(currentVal) {
            if (currentVal) {
                // if (!this.isUpdating) this.clearData()
                this.radioFirstRow = [this.hawbInitData.listSubject[0], this.hawbInitData.listSubject[1], this.hawbInitData.listSubject[2]]
                this.radioSecondRow = [this.hawbInitData.listSubject[7], this.hawbInitData.listSubject[3], null]
                this.radioThirdRow = [this.hawbInitData.listSubject[4], null, this.hawbInitData.listSubject[5]]
            }
        },
        secondRadio(newVal) {
            const defaultConditions = {
                docCheckAndIDA: false, // 1
                ida: false, // 2
                docCheck: false, // 3
                mic: false, // 4
                reservation: false, // 5
                clearancePlaceChange: false, // 6
                rdoClearanceSituation: false, // 7
                docCheckAndMIC: false, // 8
            }
            switch (newVal) {
                case '1':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, docCheckAndIDA: true }
                    this.canRelease = true
                    break
                case '2':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, ida: true }
                    this.canRelease = false
                    break
                case '3':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, docCheck: true }
                    this.canRelease = true
                    break
                case '4':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, mic: true }
                    this.canRelease = false
                    break
                case '5':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, reservation: true }
                    this.canRelease = false
                    break
                case '6':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, rdoClearanceSituation: true }
                    this.canRelease = false
                    break
                case '7':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, rdoClearanceSituation: true }
                    this.canRelease = false
                    break
                case '8':
                    this.searchCondition = { ...this.searchCondition, ...defaultConditions, docCheckAndMIC: true }
                    this.canRelease = true
                    break
            }
            this.searchCondition.idaFlg = this.getIdaFlag
        },
        'searchCondition.confirmationItem': function (newVal) {
            this.searchCondition.detail = ''
            if (newVal !== '') this.fetchDataList()
        },
    },
    computed: {
        getIdaFlag() {
            return !this.searchCondition.mic && !this.searchCondition.docCheck && !this.searchCondition.docCheckAndMIC
        },
    },
    data: () => ({
        isLoading: false,
        // Modal's props values
        radioFirstRow: [],
        radioSecondRow: [],
        radioThirdRow: [],
        listDataList: [],
        modalTitle: 'HAWBスキャン',
        secondRadio: '1',
        searchCondition: {
            rdoCondition: '1',
            docCheckAndIDA: true, // 1
            ida: false, // 2
            docCheck: false, // 3
            mic: false, // 4
            reservation: false, // 5
            clearancePlaceChange: false, // 6
            rdoClearanceSituation: false, // 7
            docCheckAndMIC: false, // 8
            idaFlg: false,
            updateFlg: '',
            controlCheckFlag: true,
            confirmationItem: '',
            detail: '',
            customsBroker: 'KKJ',
            customsClearancePlace: '4BKKJ',
            cmbCustomsClearanceSituation: 'ID00000',
        },
        listHawbNo: [],
        canRelease: true,
    }),
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        generateMessageList(array) {
            return array.reduce((accumulator, currentValue) => {
                return accumulator + `${currentValue} \n`
            }, '')
        },

        fetchDataList() {
            this.changeLoading()
            this.axios
                .get(`api/oait001/hawbScan/dataList`, {
                    params: {
                        confirmationItem: this.searchCondition.confirmationItem,
                    },
                })
                .then((res) => {
                    this.listDataList = res.data.listDataList
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        updateListHawbNo(listHawbNo) {
            this.listHawbNo = listHawbNo
        },

        callCheckHawbScanAPI(postData) {
            return this.axios.post('api/oait001/hawbScan/checkRegist', postData)
        },

        callRegistHawbScanAPI(postData) {
            return this.axios.post('api/oait001/hawbScan/applyRegist', postData)
        },

        async handleRegisterButtonClicked() {
            const postData = {
                ...this.hawbScanProps,
                ...this.searchCondition,
                updateFlg: this.secondRadio,
                listHawbNo: [...this.listHawbNo.slice(0, -1)],
            }
            this.changeLoading()
            try {
                // CHECK API CALL
                const checkResponse = await this.callCheckHawbScanAPI(postData)
                if (!checkResponse.data.checkResult) {
                    notificationError(checkResponse.data.messageList[0])
                } else {
                    // REGIST API CALL
                    if (checkResponse.data.messageList.length > 0) {
                        const confirmMessage = this.generateMessageList(checkResponse.data.messageList)
                        if (confirm(confirmMessage)) {
                            const registResponse = await this.callRegistHawbScanAPI({ ...postData, listHawbNo: checkResponse.data.listHawbRowInfoDto })
                            notificationSuccess(registResponse.data.messageList[0], '', NOTIFICATION_DURATION)
                        }
                    } else {
                        const registResponse = await this.callRegistHawbScanAPI({ ...postData, listHawbNo: checkResponse.data.listHawbRowInfoDto })
                        notificationSuccess(registResponse.data.messageList[0], '', NOTIFICATION_DURATION)
                    }
                }
            } catch (error) {
                console.log(error)
            } finally {
                this.changeLoading()
            }
        },

        callReleaseHawbScanAPI() {
            const postData = {
                ...this.hawbScanProps,
                ...this.searchCondition,
                updateFlg: this.secondRadio,
                listHawbNo: [...this.listHawbNo.slice(0, -1)],
            }
            return this.axios.post('api/oait001/hawbScan/releaseHawb', postData)
        },

        handleReleaseButtonClicked() {
            this.changeLoading()
            this.callReleaseHawbScanAPI()
                .then((res) => {
                    const responseMessage = this.generateMessageList(res.data.messageList)
                    if (!res.data.checkResult) notificationError(responseMessage)
                    else notificationSuccess(responseMessage)
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        clearData() {
            this.searchCondition = {
                rdoCondition: '1',
                docCheckAndIDA: true,
                ida: false,
                docCheck: false,
                mic: false,
                reservation: false,
                clearancePlaceChange: false,
                rdoClearanceSituation: false,
                docCheckAndMIC: false,
                idaFlg: false,
                controlCheckFlag: true,
                updateFlg: '',
                confirmationItem: '',
                detail: '',
                customsBroker: 'KKJ',
                customsClearancePlace: '4BKKJ',
                cmbCustomsClearanceSituation: 'ID00000',
            }
            this.listDataList = []
            this.secondRadio = '1'
            this.listHawbNo = []
        },

        handleCloseModal() {
            this.$emit('closeHawbScanModal')
            this.clearData()
            // this.v$.$reset()
        },
    },
}
</script>
