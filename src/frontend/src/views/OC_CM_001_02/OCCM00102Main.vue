<!-- eslint-disable vue/no-parsing-error -->
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
                <component :is="currentView" :initData="initData" @searchImportData="searchImportData" @openDetailModal="openDetailModal" :isModal="isModal" :parentProps="parentProps"></component>
            </div>
            <!-- 検索結果 -->
            <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                <SearchResult
                    :resultData="resultData"
                    @searchImportData="searchImportData"
                    @openDetailModal="openDetailModal"
                    :enableClickToSearch="enableClickToSearch"
                    :isModal="isModal"
                    @handleClickToSearchAction="handleClickToSearchAction"
                />
            </div>
            <OCCM00102Modal
                :showModal="showModal"
                :vendorQueryDetail="vendorQueryDetail"
                :modalAction="modalAction"
                :modalDropdownOptions="modalDropdownOptions"
                @openDetailModal="openDetailModal"
                @closeDetailModal="closeDetailModal"
                @searchImportData="searchImportData"
            />
        </va-inner-loading>
    </div>
</template>

<script>
import SearchResult from './SearchResult.vue'
import SearchComponent from './SearchComponent.vue'
import OCCM00102Modal from './OCCM00102Modal.vue'
import { notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue'
import { NOTIFICATION_DURATION } from '@/utils/utilities'
import { NO_RESULT_FOUND_MESSAGE, SEARCH_ERROR_MESSAGE, SYSTEM_ERROR_MESSAGE } from '@/utils/messages'

const TABS = [{ title: '検索', name: 'searchComponent', color: 'success' }]

export default {
    components: {
        SearchComponent,
        SearchResult,
        OCCM00102Modal,
    },
    props: {
        enableClickToSearch: Boolean,
        isModal: Boolean,
        parentProps: Object,
    },
    data: () => ({
        tabs: TABS,
        currentView: 'searchComponent',
        initData: null,
        numberArrHawb: 0,
        isLoading: false,
        searchCondition: null,
        resultData: {
            listTraderNoMasterDto: [],
        },

        // Modal's props values
        showModal: false,
        vendorQueryDetail: {},
        modalAction: 'register',
        modalDropdownOptions: [],
    }),
    mounted() {
        this.setDefaultValue()
        this.initDropdownOptions()
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        // E003: Search vendor info based on conditions
        searchImportData(searchCondition) {
            this.changeLoading()

            // This is to prevent api call when user register before search
            if (searchCondition === null && this.searchCondition === null) {
                this.changeLoading()
                return
            }

            if (searchCondition !== null) {
                this.searchCondition = searchCondition
            }

            this.axios({
                url: '/api/occm001/trader/list',
                method: 'get',
                params: this.searchCondition,
                enctype: 'multipart/form-data',
            })
                .then((res) => {
                    this.resultData = res.data
                    if (res.data.countRecord === 0) notificationWarn(NO_RESULT_FOUND_MESSAGE, '', NOTIFICATION_DURATION)
                })
                .catch((error) => {
                    notificationError(SEARCH_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        openDetailModal(action, params) {
            if (JSON.stringify(params) !== JSON.stringify(this.vendorQueryDetail)) {
                this.vendorQueryDetail = params
            }
            this.modalAction = action
            this.showModal = true
        },

        closeDetailModal() {
            this.showModal = false
        },

        // E001: Set init dropdown options
        setDefaultValue() {
            this.changeLoading()
            this.axios({
                url: 'api/occm001/trader/init',
                method: 'get',
            })
                .then((res) => {
                    this.initData = res.data
                })
                .catch((error) => {
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // E006: Init OCCM00102Modal default dropdown options
        initDropdownOptions() {
            this.changeLoading()
            this.axios
                .get('api/occm001/trader/form/init')
                .then((res) => {
                    this.modalDropdownOptions = res.data
                })
                .catch((error) => {
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },
        handleClickToSearchAction(selectedRowData) {
            this.$emit('handleClickToSearchAction', selectedRowData)
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
