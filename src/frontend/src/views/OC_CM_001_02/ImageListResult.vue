<template>
    <va-card style="overflow: hidden">
        <div class="row justify-start" style="height: 300px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="resultRowData"
                    @grid-ready="onGridReady"
                    @selection-changed="onSelectionChanged"
                    :suppressRowClickSelection="true"
                    :stopEditingWhenCellsLoseFocus="true"
                    :rowSelection="rowSelection"
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
import GridButton from './OCCM00102GridButton_ViewPdf.vue' // eslint-disable-line
import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'
import { gridColumnSortMethod } from '@/utils/utilities'

export default {
    components: {
        AgGridVue,
        GridButton, // eslint-disable-line
    },
    props: {
        resultRowData: Object,
    },
    data() {
        return {
            columnDefs: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
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
                headerName: 'select',
                field: '',
                minWidth: 55,
                maxWidth: 55,
                resizable: false,
                headerCheckboxSelection: true,
                headerCheckboxSelectionFilteredOnly: true,
                checkboxSelection: true,
                cellClass: 'cellCenter',
            },
            {
                headerName: '表示',
                field: '表示',
                resizable: true,
                minWidth: 100,
                maxWidth: 100,
                cellRenderer: 'GridButton',
                cellRendererParams: {
                    onViewBtnClicked: this.onViewBtnClicked,
                },
            },
            {
                headerName: 'イメージ名',
                field: 'imageName',
                resizable: true,
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
        onViewBtnClicked(rowData) {
            this.$emit('handleViewPdf', rowData)
        },
    },
}
</script>

<style>
</style>
