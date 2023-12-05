<template>
    <va-inner-loading :loading="isLoading" :size="60">
        <va-modal :modelValue="showModal" @update:modelValue="closeFourthModal" no-outside-dismiss class="p-0" size="large">
            <template #content>
                <div class="w-[800px]">
                    <div class="flex justify-between items-center bg-[#538ce2] p-3 text-white">
                        <p class="m-0">特別手配内容詳細</p>
                        <va-icon name="va-clear" class="flex-grow-0" @click="closeFourthModal" />
                    </div>
                    <div class="p-3">
                        <p>HAWB No: {{ hawbNo }}</p>
                        <p class="text-right">表示：{{ resultRowData.length }} 件</p>
                    </div>
                    <DetailResult :isLoading="isLoading" :resultRowData="resultRowData" @closeFourthModal="closeFourthModal" :hawbNo="hawbNo" />
                </div>
            </template>
        </va-modal>
    </va-inner-loading>
</template>

<script>
import { SYSTEM_ERROR_MESSAGE } from '@/utils/messages'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import DetailResult from './DetailResult.vue'

export default {
    props: {
        showModal: Boolean,
        hawbNo: String,
    },
    data() {
        return {
            isLoading: false,
            currentItem: '',
            resultRowData: [],
        }
    },
    components: {
        DetailResult,
    },

    watch: {
        // Trigger when modal open
        showModal(currentVal) {
            if (currentVal === true) {
                this.getArrangementDetailData(this.queryUserCd)
            }
        },
    },
    methods: {
        changeLoading() {
            this.isLoading = !this.isLoading
        },

        // GET special arrangement details based on selected row's hawbNo
        getArrangementDetailData() {
            this.changeLoading()
            this.axios
                .get('api/oait002/arrangement-detail/init', {
                    params: {
                        importExportClass: '1',
                        displayClass: '1',
                        actionFlg: '1',
                        cwbNo: this.hawbNo,
                    },
                })
                .then((res) => {
                    this.resultRowData = res.data.arrangementDetailDto
                })
                .catch((error) => {
                    console.log(error)
                    notificationError(SYSTEM_ERROR_MESSAGE, error.message)
                })
                .finally(() => {
                    this.changeLoading()
                })
        },
        closeFourthModal() {
            this.$emit('closeFourthModal')
        },
    },
}
</script>

<style>
.va-modal__inner {
    padding: 0;
}
</style>
