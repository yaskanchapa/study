<template>
    <va-button style="width:100%;" class="mr-4 my-1" @click="showstatusModal"> {{ gridButtonLabel }} </va-button>
    <va-modal v-model="showModalWithFixedLayout" fixed-layout>
        <div class="flex justify-between items-center bg-[#538ce2] p-3 text-white">
            <p class="m-0">{{ title }}</p>
        </div>
        <va-card>
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
                        <tr v-for="sts in rowData"  :key="sts.awbNo">
                            <td>{{ sts.regDate }}</td>
                            <td>{{ sts.statusCd }}</td>
                            <td>{{ sts.regUserCd }}</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </va-card>
    </va-modal>
</template>
<script>
export default {
    data: function () {
        return {
            rowData: [
                {
                    regDate: '',
                    statusCd: '',
                    regUserCd: ''
                }
            ],
            data: this.params.value,
            searchCond: {
                awbNo: this.params.data.dataAwbNo,
                cwbNo: this.params.data.cwbNo,
            },
            showModalWithFixedLayout: false,
            title: '輸出ステータス照会',
            gridButtonLabel: 'ステータス照会'
        };
    },
    methods: {
        showstatusModal() {
            this.axios({
                url: '/api/oaet001/searchAllStatus',
                method: 'get',
                params: this.searchCond,
                enctype: 'multipart/form-data'
            })
                .then(res => {
                    this.rowData = res.data
                }).finally(() => {
                    this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                })
        },
    }
};
</script>