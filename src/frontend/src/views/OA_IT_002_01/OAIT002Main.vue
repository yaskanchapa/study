<!-- eslint-disable vue/no-parsing-error -->
<script setup>
import SearchResult from './SearchResult.vue'
import SearchComponent from './SearchComponent.vue'
import HawbNoComponent from '../OA_IT_002_02/OA_IT_002_02.vue'
</script>

<template>
    <div class="container">
        <va-inner-loading :loading="isLoading" :size="60">
            <!--検索条件-->
            <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索条件</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                <va-tabs v-model="currentView">
                    <template #tabs>
                        <va-tab v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                    </template>
                </va-tabs>
                <component
                    v-for="tab in tabs"
                    :key="'comp' + tab.name"
                    :is="tab.name"
                    v-show="tab.name === currentView"
                    @searchImportData="searchImportData"
                    :changeLoading="changeLoading"
                    :clearResult="clearResult"
                    :initData="initData"
                    :updateHawb="updateHawb"
                    :numberArrHawb="numberArrHawb"
                    :resultData="resultData"
                    :agentAuthCheck="agentAuthCheck"
                >
                </component>
            </div>

            <!-- 検索結果 -->
            <div v-if="currentView === 'searchComponent'">
                <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索結果</div>
                <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                    <SearchResult
                        :resultData="resultData"
                        :searchCondition="searchCondition"
                        :agentAuthCheck="agentAuthCheck"
                        @openFourthModal="openFourthModal"
                        @openHistoryModal="openHistoryModal"
                        @getPdfUrl="getPdfUrl"
                    />
                </div>
            </div>
        </va-inner-loading>
        <OAIT00203 :showModal="showFourthModal" :hawbNo="currentHawbNo" @closeFourthModal="closeFourthModal" />
        <HistoryModal :showModal="showHistoryModal" :searchConditionHistoryModal="searchConditionHistoryModal" @closeHistoryModal="closeHistoryModal" />
    </div>
</template>

<script>
import { notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue'
import OAIT00203 from '../OA_IT_002_03/OA_IT_002_03.vue'
import HistoryModal from './HistoryModal.vue'
import { dateFormat, NOTIFICATION_DURATION } from '@/utils/utilities'
import { SYSTEM_ERROR_MESSAGE, SEARCH_ERROR_MESSAGE, NO_RESULT_FOUND_MESSAGE } from '@/utils/messages'

const TABS = [
    { title: '検索', name: 'searchComponent', color: 'success' },
    { title: 'HAWBNo選択', name: 'hawbNoComponent', color: 'success' },
]

export default {
    components: {
        searchComponent: SearchComponent, // eslint-disable-line
        hawbNoComponent: HawbNoComponent,
        OAIT00203,
        HistoryModal,
    },
    data: () => ({
        tabs: TABS,
        currentView: 'searchComponent',
        initData: null,

        numberArrHawb: 0,
        hawbNoCmb: '',
        arrHawb: '',

        searchCondition: {},
        resultData: {
            airImportDto: [],
        },
        isLoading: false,

        agentAuthCheck: false,

        // For History Modal
        searchConditionHistoryModal: {},
        showHistoryModal: false,

        // For Fourth Modal
        currentHawbNo: '',
        showFourthModal: false,
    }),

    mounted() {
        this.setDefaultValue()
    },

    beforeMount() {
        const localStorageData = localStorage.getItem('vuex')
        const authInfo = JSON.parse(localStorageData)?.authPermission

        if (!authInfo) return

        if (authInfo.userAuthorityCd === '51') {
            this.agentAuthCheck = true
        }
    },

    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        setDefaultValue() {
            this.changeLoading()
            this.axios({
                url: 'api/oait002/init',
                method: 'get',
            })
                .then((res) => {
                    this.initData = res.data
                    const arrayObjectDate = [
                        res.data.textObjectDateArrival,
                        res.data.textOobjectDateCarrying,
                        res.data.textObjectDateReport,
                        res.data.textObjectDatePermission,
                        res.data.textObjectDateCarry,
                    ]
                    this.initData.objectDate = arrayObjectDate
                })
                .catch((error) => {
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        searchImportData(searchCondition, refocusAfterSearch) {
            this.changeLoading()
            searchCondition.objectDateTo = dateFormat(searchCondition.objectDateTo)
            searchCondition.objectDateFrom = dateFormat(searchCondition.objectDateFrom)
            this.searchCondition = searchCondition
            this.axios({
                url: '/api/oait002/list',
                method: 'get',
                params: { ...this.searchCondition, arrHawb: this.arrHawb, hawbNoCmb: this.hawbNoCmb },
                enctype: 'multipart/form-data',
            })
                .then((res) => {
                    const {
                        numberOfIDA,
                        numberOfMIC,
                        numberOfPending,
                        numberOfUndeclared,
                        theTotalNumberOfCase,
                        totalNumber,
                        searchEnable,
                        clearEnable,
                        agencySelectionEnable,
                        hawbNoFocus,
                        csvKEnable,
                        csvLEnable,
                        pdfEnable,
                        airImportDto,
                        cbuttonEnable,
                    } = res.data
                    this.resultData = {
                        numberOfIDA,
                        numberOfMIC,
                        numberOfPending,
                        numberOfUndeclared,
                        theTotalNumberOfCase,
                        totalNumber,
                        searchEnable,
                        clearEnable,
                        agencySelectionEnable,
                        hawbNoFocus,
                        csvKEnable,
                        csvLEnable,
                        pdfEnable,
                        airImportDto,
                        cbuttonEnable,
                    }
                    if (Number(this.resultData.totalNumber) === 0) notificationWarn(NO_RESULT_FOUND_MESSAGE, '', NOTIFICATION_DURATION)
                })
                .catch((error) => {
                    notificationError(SEARCH_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                    refocusAfterSearch()
                })
        },

        updateHawb(arrHawb, hawbNoCmb) {
            if (arrHawb.length > 0) {
                let queryStr = ''
                for (const item of arrHawb) {
                    const itemString1 = item.split('/')[0]
                    const itemString2 = item.split('/')[1]
                    queryStr += itemString1 + ',' + itemString2 + ';'
                }
                this.arrHawb = queryStr
            } else {
                this.arrHawb = ''
            }
            this.hawbNoCmb = hawbNoCmb
            this.numberArrHawb = arrHawb.length
            this.currentView = 'searchComponent'
        },

        clearResult() {
            this.resultData = []
        },

        getPdfUrl(selectedRows) {
            this.changeLoading()
            const promises = selectedRows.map((rowData) => {
                return this.axios.get('/api/oait001/getPdfUrl', {
                    params: {
                        awbNo: rowData.mawbNo,
                        cwbNo: rowData.cwbNo,
                        cwbNoDeptCd: rowData.cwbDeptCd,
                    },
                })
            })
            Promise.allSettled(promises)
                .then((results) => {
                    const errorArray = []
                    results.forEach((result) => {
                        const { status, value } = result
                        if (status === 'fulfilled' && value.data.errorFlag === 'false') {
                            this.openWinPop(value.data.pdfUrl1, value.data.pdfUrl2)
                        } else {
                            errorArray.push(value.config.params.cwbNo)
                        }
                    })
                    if (errorArray.length > 0) {
                        const errorList = (
                            <>
                                {errorArray.map((error) => (
                                    <span>
                                        ・{error}
                                        <br />
                                    </span>
                                ))}
                            </>
                        )
                        notificationError('エラーが発生しました:', errorList)
                    }
                })
                .finally(this.changeLoading())
        },
        openWinPop(param1, param2) {
            const uri = '/pdfViewer?param1=' + param1 + '&param2=' + param2
            // this.winPopup.openWinPop(uri, 1560, 700);
            window.open(uri)
        },

        // E010: 通関STS - BUSINESSCLASS = "02"
        // E011: 書類STS - BUSINESSCLASS = "03"
        // E012: 貨物STS - BUSINESSCLASS = "01"
        openHistoryModal(params) {
            this.searchConditionHistoryModal = params
            this.showHistoryModal = true
        },
        closeHistoryModal() {
            this.showHistoryModal = false
            this.searchConditionHistoryModal = {}
        },

        // E013: Open/Close 特別手配内容詳細 Modal
        openFourthModal(params) {
            this.currentHawbNo = params
            this.showFourthModal = true
        },
        closeFourthModal() {
            this.showFourthModal = false
            this.currentHawbNo = ''
        },
    },
}
</script>

<style scoped>
.bar-color1-blue {
    --tw-bg-opacity: 1;
    background-color: #2b89e7;
}
</style>
