<template>
    <div style="align-items: center; justify-content: center">
        <va-button color="info" size="medium">
            <va-icon name="search" size="medium" @click="btnClickedHandler()" />
        </va-button>
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
            const searchConditionHistoryModal = {
                businessClass: this.params.businessClass,
                cwbNo: rowData.cwbNo,
                cwbNoDeptCd: rowData.cwbDeptCd,
                statusCd: '',
            }
            this.params.openHistoryModal(searchConditionHistoryModal)
        },
    },
}
</script>
