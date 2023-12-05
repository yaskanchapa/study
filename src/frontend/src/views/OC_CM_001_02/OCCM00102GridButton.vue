<template>
    <div style="align-items: center; justify-content: center">
        <va-button color="info" @click="btnClickedHandler()">明細</va-button>
    </div>
</template>

<script>
import GridButton from '../../components/commonGrid/GridButton.vue'
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
    extends: GridButton,
    methods: {
        btnClickedHandler() {
            const rowData = getSingleRowData(this.gridApi, this.id)
            const vendorQueryDetail = {
                customerNo: rowData.customerNo,
                deptCd: rowData.deptCd,
                ocsdeptCd: rowData.impExpFlag + rowData.shiyoBashoFlag + rowData.seq,
            }
            this.params.openDetailModal('update', vendorQueryDetail)
        },
    },
}
</script>
