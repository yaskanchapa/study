<template>
    <va-card style="overflow: hidden">
        <div class="row justify-start" style="height: 300px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :defaultColDef="defaultColDef"
                    :rowData="resultRowData"
                    @grid-ready="onGridReady"
                    @selection-changed="onSelectionChanged"
                    :rowSelection="rowSelection"
                    :suppressScrollOnNewData="true"
                />
            </div>
        </div>
    </va-card>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import { gridColumnSortMethod } from '@/utils/utilities'

export default {
    components: {
        AgGridVue,
    },
    props: {
        resultRowData: Object,
    },
    data() {
        return {
            columnDefs: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' },
                sortable: true,
                comparator: gridColumnSortMethod,
            },
            gridApi: null,
            columnApi: null,
            rowSelection: 'single',
        }
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: '実施日付',
                field: 'regDate',
            },
            {
                headerName: 'ステータス',
                field: 'stsName',
                flex: 2,
            },
            {
                headerName: '業務',
                field: 'businessClassName',
                width: 150,
            },
            {
                headerName: '実施者',
                field: 'userName',
                flex: 1,
            },
        ]
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        onSelectionChanged() {
            const selectedRow = getSelectdRowsData(this.gridApi)
            this.$emit('onTableRowSelection', selectedRow[0])
        },
        clearSelection() {
            this.gridApi.deselectAll()
        },
    },
}
</script>

<style>
</style>
