<template>
    <div style="align-items: center; justify-content: center">
        <va-button color="info" @click="btnClickedHandler()">View</va-button>
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
            this.params.onViewBtnClicked(rowData)
        },
    },
}
</script>
