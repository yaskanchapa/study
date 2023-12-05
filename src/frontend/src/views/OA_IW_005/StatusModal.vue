<template>
    <va-button style="width:100%;" class="mr-4 my-1 center" @click="showstatusModal"> {{ data }} </va-button>
    <va-modal v-model="showModalWithFixedLayout" :title="title" fixed-layout>
        <va-card>
            <table class="va-table">
                <thead>
                    <tr>
                        <th>実行日時</th>
                        <th>ステータス</th>
                        <th>実行者</th>
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="sts in rowData"  :key="sts.cwbNo">
                        <td>{{ sts.regDate }}</td>
                        <td>{{ sts.statusCd }}</td>
                        <td>{{ sts.regUserCd }}</td>
                    </tr>
                </tbody>
            </table>
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
                cwbNo: this.params.data.cwbNo,
            },
            showModalWithFixedLayout: false,
            title: '履歴',
        };
    },
    methods: {
        showstatusModal() {
            this.axios({
                url: '/api/oaiw005/historyStatus',
                method: 'get',
                params: this.searchCond,
                enctype: 'multipart/form-data'
            })
                .then(res => {
                    this.rowData = res.data
                    console.log(res.data)
                }).finally(() => {
                    this.showModalWithFixedLayout = !this.showModalWithFixedLayout
                })
            
        },
        invokeParentMethod() {
            this.params.context.componentParent.methodFromParent(
                `Row: ${this.params.node.rowIndex}, Col: ${this.params.colDef.headerName}`
            );
        },
    },
};

</script>