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
                <component :is="currentView" :initData="initData" @searchImportData="searchImportData" @openDetailModal="openDetailModal"></component>
            </div>
            <!-- 検索結果 -->
            <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                <SearchResult :resultData="resultData" @searchImportData="searchImportData" @openDetailModal="openDetailModal" />
            </div>

            <!-- OC_CM_001: ユーザーマスタ  -->
            <OCCM001Modal
                :showModal="showModal"
                :queryUserCd="queryUserCd"
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
import OCCM001Modal from './OCCM001Modal.vue'
import { notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue'
import { NOTIFICATION_DURATION } from '@/utils/utilities'
import { NO_RESULT_FOUND_MESSAGE, SEARCH_ERROR_MESSAGE, SYSTEM_ERROR_MESSAGE } from '@/utils/messages'

const TABS = [{ title: '検索', name: 'searchComponent', color: 'success' }]

export default {
    components: {
        SearchComponent,
        SearchResult,
        OCCM001Modal,
    },
    data: () => ({
        tabs: TABS,
        currentView: 'searchComponent',
        initData: null,
        numberArrHawb: 0,
        isLoading: false,
        searchCondition: null,
        resultData: {
            listUserMasterDto: [],
        },

        // Modal's props values
        showModal: false,
        queryUserCd: '',
        modalAction: '',
        modalDropdownOptions: null,
    }),
    mounted() {
        this.setDefaultValue()
        this.initDropdownOptions()
    },
    methods: {
        openDetailModal(action, params) {
            this.modalAction = action
            this.queryUserCd = params
            this.showModal = true
        },
        closeDetailModal() {
            this.showModal = false
            this.modalAction = ''
            this.queryUserCd = ''
        },
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        // ■E001: Set init dropdown options
        setDefaultValue() {
            this.changeLoading()
            this.axios({
                url: 'api/occm001/user/init',
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

        // ■E003: Search for users based on conditions
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
                url: '/api/occm001/user/list',
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

        // E009: Init OCCM001Modal default dropdown options
        initDropdownOptions() {
            this.changeLoading()
            this.axios
                .get('api/occm001/user/form/init')
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
    },
}
</script>


<style scoped>
.bar-color1-blue {
    --tw-bg-opacity: 1;
    background-color: #2b89e7;
}
</style>
