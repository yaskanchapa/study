<template>
    <va-modal style="width: 100%" class="mr-4 my-1" :modelValue="showModal" @update:modelValue="closeHistoryModal">
        <template #content>
            <va-inner-loading :loading="isLoading" :size="60">
                <div class="flex justify-between items-center bg-[#538ce2] p-3 text-white">
                    <p class="m-0">{{ title }}</p>
                    <va-icon name="va-clear" class="flex-grow-0" @click="closeHistoryModal" />
                </div>
                <div class="min-h-[100px] max-h-[800px] p-3 overflow-auto">
                    <table class="va-table">
                        <thead>
                            <tr>
                                <th>実行日時</th>
                                <th>ステータス</th>
                                <th>実行者</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr v-for="(status, index) in rowData" :key="index">
                                <td>{{ status.updateDate }}</td>
                                <td>{{ status.stsName }}</td>
                                <td>{{ status.userName }}</td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </va-inner-loading>
        </template>
    </va-modal>
</template>

<script>
import { SYSTEM_ERROR_MESSAGE } from '@/utils/messages'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
    props: {
        showModal: Boolean,
        searchConditionHistoryModal: Object,
    },
    data() {
        return {
            rowData: [],
            title: 'ステータス履歴',
            isLoading: false,
        }
    },
    watch: {
        // Trigger when modal open
        showModal(currentVal) {
            if (currentVal === true) {
                this.getStatusModalData(this.queryUserCd)
            }
        },
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        getStatusModalData() {
            this.changeLoading()
            this.axios({
                url: '/api/oait002/history/list',
                method: 'get',
                params: this.searchConditionHistoryModal,
                enctype: 'multipart/form-data',
            })
                .then((res) => {
                    this.rowData = res.data.listAiStatusHistoryDto
                })
                .catch((error) => {
                    console.log('error' + error)
                    notificationError(SYSTEM_ERROR_MESSAGE)
                    this.closeHistoryModal()
                })
                .finally(() => {
                    this.changeLoading()
                })
        },

        closeHistoryModal() {
            this.rowData = []
            this.$emit('closeHistoryModal')
        },
    },
}
</script>
