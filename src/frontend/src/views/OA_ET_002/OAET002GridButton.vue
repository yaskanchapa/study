<template>
    <div class="items-center justify-center">
        <va-button color="info" @click="btnClickedHandler()"
            ><span class="text-2xl">{{ buttonText }}</span></va-button
        >
    </div>
</template>

<script>
import GridButton from '../../components/commonGrid/GridButton.vue'
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
    extends: GridButton,
    data() {
        return {
            buttonText: '',
        }
    },
    mounted() {
        const rowData = getSingleRowData(this.gridApi, this.id)
        this.buttonText = rowData.cwbno
    },
    methods: {
        btnClickedHandler() {
            const rowData = getSingleRowData(this.gridApi, this.id)
            this.params.openHistoryStatusModal(rowData)
        },
    },
}
</script>
