<script setup>
import SearchConditionMain from './SearchConditionMain.vue'
import SearchResult from './SearchResult.vue'
</script>

<template>
    <div class="container">
        <va-inner-loading :loading="isLoading" :size="60">
            <div class="w-90%">
                <!--検索条件-->
                <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
                    <div>
                        <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索条件</div>
                        <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                            <va-tabs v-model="currentView">
                                <template #tabs>
                                    <va-tab v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
                                </template>
                            </va-tabs>
                            <component :is="currentView" :initData="initData" @searchImportData="searchImportData"></component>
                        </div>
                    </div>
                </div>
                <!-- 検索結果 -->
                <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
                    <div>
                        <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">
                            検索結果 <span class="ml-5 font-bold text-lg">{{ countRecord }} 件</span>
                        </div>
                        <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                            <SearchResult :resultData="resultData" :currentTruck="currentTruck" @openHistoryStatusModal="openHistoryStatusModal" ref="searchResult" />
                        </div>
                    </div>
                </div>
            </div>
        </va-inner-loading>
        <HistoryStatusModal :showModal="showHistoryStatusModal" @closeHistoryStatusModal="closeHistoryStatusModal" :historyStatusProps="historyStatusProps" />
    </div>
</template>

<script>
import { notificationError, notificationWarn } from '@/components/Notification/NotificationApi'
import { dateFormat, NOTIFICATION_DURATION } from '@/utils/utilities'
import { NO_RESULT_FOUND_MESSAGE, SEARCH_ERROR_MESSAGE, SYSTEM_ERROR_MESSAGE } from '@/utils/messages'
import HistoryStatusModal from './HistoryStatusModal.vue'

const TABS = [{ title: '検索', name: 'searchCondition', color: 'success' }]
export default {
    components: {
        searchCondition: SearchConditionMain,
        HistoryStatusModal,
    },
    data: () => ({
        tabs: TABS,
        currentView: 'searchCondition',
        initData: null,
        isLoading: false,
        currentTruck: 'トラック',
        countRecord: 0,
        resultData: {
            listAirExportDto: [],
        },
        searchCondition: {},
        showHistoryStatusModal: false,
        historyStatusProps: {},
    }),
    computed: {
        currentTab() {
            return this.tabs.find(({ title }) => title === this.tabValue)
        },
    },
    mounted() {
        this.setDefaultValue()
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        setDefaultValue() {
            this.changeLoading()
            this.axios({
                url: 'api/oaet002/init',
                method: 'get',
            })
                .then((res) => {
                    this.initData = res.data
                    const arrayObjectDate = [res.data.arrivalDate, res.data.shuttleDate, res.data.tracDate, res.data.permitDate, res.data.reportDate]
                    this.initData.objectDate = arrayObjectDate
                })
                .catch((error) => {
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        searchImportData(searchCondition) {
            this.changeLoading()
            const {
                objectDate,
                arrival01,
                arrival02,
                depPort,
                linkTruckNOFrom,
                linkTruckNOTo,
                possible01,
                possible02,
                destination,
                declara01,
                declara02,
                reserveDeclara01,
                reserveDeclara02,
                permission01,
                permission02,
                permitClassCd,
                consolidation01,
                consolidation02,
                largeSmallFlg01,
                largeSmallFlg02,
                permitClass,
                agent,
                bondedWareHouseCd,
                cdb01,
                cdb02,
                cdbre01,
                cdbre02,
                bil01,
                bil02,
                eda01,
                eda02,
                mecedc01,
                mecedc02,
                cwbno,
                esaflg01,
                esaflg02,
                awbno,
                hdf01,
                hdf02,
                ulm01,
                ulm02,
                la01,
                la02,
            } = searchCondition

            this.searchCondition = {
                objectDate,
                truckNoDate: dateFormat(searchCondition.truckNoDate),
                arrival01,
                arrival02,
                depPort,
                linkTruckNOFrom,
                linkTruckNOTo,
                possible01,
                possible02,
                destination,
                declara01,
                declara02,
                reserveDeclara01,
                reserveDeclara02,
                permission01,
                permission02,
                permitClassCd,
                consolidation01,
                consolidation02,
                largeSmallFlg01,
                largeSmallFlg02,
                permitClass,
                agent,
                bondedWareHouseCd,
                cDB01: cdb01,
                cDB02: cdb02,
                cDBre01: cdbre01,
                cDBre02: cdbre02,
                bIL01: bil01,
                bIL02: bil02,
                eDA01: eda01,
                eDA02: eda02,
                mECEDC01: mecedc01,
                mECEDC02: mecedc02,
                cWBNo: cwbno,
                eSAFlg01: esaflg01,
                eSAFlg02: esaflg02,
                aWBNo: awbno,
                hDF01: hdf01,
                hDF02: hdf02,
                uLM01: ulm01,
                uLM02: ulm02,
                lA01: la01,
                lA02: la02,
            }

            // Change Header display according to radio button selection
            if (this.searchCondition.objectDate === 1) this.currentTruck = 'シャトル'
            else this.currentTruck = 'トラック'

            this.axios({
                url: '/api/oaet002/list',
                method: 'get',
                params: this.searchCondition,
                enctype: 'multipart/form-data',
            })
                .then((res) => {
                    this.resultData = res.data
                    // Update record number
                    this.countRecord = res.data.countRecord

                    if (this.countRecord === 0) notificationWarn(NO_RESULT_FOUND_MESSAGE, '', NOTIFICATION_DURATION)
                })
                .catch((error) => {
                    notificationError(SEARCH_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        openHistoryStatusModal(params) {
            this.historyStatusProps = { ...params, cwbnoDeptCd: '000' }
            this.showHistoryStatusModal = true
        },

        closeHistoryStatusModal() {
            this.showHistoryStatusModal = false
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
