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
                <component :is="currentView" @searchImportData="searchImportData"></component>
            </div>
            <!-- 検索結果 -->
            <div class="ripple w-1340 py-2 px-5 bar-color1-blue text-white">検索結果</div>
            <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
                <SearchResult
                    :resultData="resultData"
                    @searchImportData="searchImportData"
                    @handleDelBtnClicked="handleDelBtnClicked"
                    @handleUpdateBtnClicked="handleUpdateBtnClicked"
                    :searchCondition="searchCondition"
                    ref="searchResult"
                />
            </div>

            <va-modal v-model="showConfirmationModal" no-padding hide-default-actions>
                <template #header>
                    <div class="pt-3 px-3">
                        <h2 class="">確認</h2>
                    </div>
                    <div class="bg-slate-200 h-[1px] my-3"></div>
                </template>
                <template #default>
                    <div class="px-4 py-2">
                        <h2 class="">{{ deleteConfirmMessage }}</h2>
                    </div>
                </template>
                <template #footer>
                    <div class="flex justify-between gap-6 pb-3">
                        <va-button @click="handleDeleteData">OK</va-button>
                        <va-button @click="onModalCancelBtnClicked">キャンセル</va-button>
                    </div>
                </template>
            </va-modal>
        </va-inner-loading>
    </div>
</template>

<script>
import SearchResult from './SearchResult.vue'
import SearchComponent from './SearchComponent.vue'
import { dateFormat, NOTIFICATION_DURATION } from '@/utils/utilities'
import { notificationSuccess, notificationError } from '../../components/Notification/NotificationApi.vue'
import { DELETE_CONFIRM_ACTION_MESSAGE, DELETE_ERROR_MESSAGE, DELETE_SUCCESS_MESSAGE, SEARCH_ERROR_MESSAGE, UPDATE_ERROR_MESSAGE, UPDATE_SUCCESS_MESSAGE } from '@/utils/messages'

const TABS = [{ title: '検索', name: 'searchComponent', color: 'success' }]

export default {
    components: {
        SearchComponent,
        SearchResult,
    },
    data: () => ({
        tabs: TABS,
        currentView: 'searchComponent',
        isLoading: false,
        searchCondition: null,
        resultData: {
            listRateMasterDto: [],
            countRecord: 0,
            csvEnable: false,
            deleteEnable: false,
        },
        deleteConfirmMessage: DELETE_CONFIRM_ACTION_MESSAGE,
        showConfirmationModal: false,
    }),
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },
        // ■E002: Search for export rate informations based on inputted date
        searchImportData(searchCondition) {
            this.changeLoading()
            this.searchCondition = searchCondition
            const queryDate = dateFormat(searchCondition)
            this.axios
                .get(`/api/occm001/rate/list?startDate=${queryDate}`)
                .then((res) => {
                    this.resultData = res.data
                    this.csvEnable = res.data.csvEnable
                    this.deleteEnable = res.data.deleteEnable
                })
                .catch((error) => {
                    notificationError(SEARCH_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // Ask to confirm before deletion
        handleDelBtnClicked() {
            if (this.searchCondition === null) {
                notificationError(DELETE_ERROR_MESSAGE)
                return
            }
            this.showConfirmationModal = true
        },
        onModalCancelBtnClicked() {
            this.showConfirmationModal = false
        },

        // ■E003: Update export rates
        handleUpdateBtnClicked(newRate) {
            this.changeLoading()
            this.axios
                .put(`/api/occm001/rate/update`, {
                    startDate: dateFormat(this.searchCondition),
                    listRateMasterDto: newRate,
                })
                .then((res) => {
                    notificationSuccess(UPDATE_SUCCESS_MESSAGE, '', NOTIFICATION_DURATION)
                    this.searchImportData(this.searchCondition)
                })
                .catch((error) => {
                    notificationError(UPDATE_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        // ■E004: Delete rates of the current displaying date
        handleDeleteData() {
            this.showConfirmationModal = false
            this.changeLoading()
            const queryDate = {
                startDate: dateFormat(this.searchCondition),
            }
            this.axios
                .delete(`api/occm001/rate/delete`, {
                    data: queryDate,
                })
                .then((res) => {
                    notificationSuccess(DELETE_SUCCESS_MESSAGE, '', NOTIFICATION_DURATION)

                    // Reload table data
                    this.searchImportData(this.searchCondition)
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(DELETE_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        clearData() {
            this.searchCondition = null
            this.resultData = {
                listRateMasterDto: [],
                countRecord: 0,
                csvEnable: false,
                deleteEnable: false,
            }
            this.$refs.searchResult.clearData()
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
