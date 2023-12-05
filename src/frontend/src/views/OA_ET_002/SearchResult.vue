<script setup>
</script>
<template>
    <va-card style="padding: 0.5rem">
        <div class="row justify-start h-[450px]">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :rowHeight="55"
                    :columnDefs="columnDefs"
                    :rowData="resultData.listAirExportDto"
                    @grid-ready="onGridReady"
                    :defaultColDef="defaultColDef"
                    :suppressRowClickSelection="true"
                    :enableRangeSelection="true"
                    :rowSelection="rowSelection"
                    :stopEditingWhenCellsLoseFocus="true"
                >
                </ag-grid-vue>
            </div>
        </div>
        <div class="row justify-end p-2">
            <div class="item flex xs2">
                <va-button color="info" size="medium" :disabled="!resultData?.outCsvEnabled" @click="onExportCSVBtn">CSV(K)</va-button>
            </div>
        </div>
    </va-card>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import { dateFormat, gridColumnSortMethod } from '@/utils/utilities'
import GridButton from './OAET002GridButton.vue'

export default {
    name: 'App',
    props: {
        resultData: Object,
        currentTruck: String,
    },
    components: {
        AgGridVue,
        GridButton, // eslint-disable-line
    },
    data() {
        return {
            columnDefs: null,
            rowData: null,
            gridApi: null,
            columnApi: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted', display: 'flex ', 'justify-content': 'center', 'align-items': 'center' },
                suppressMovable: true,
                sortable: true,
                comparator: gridColumnSortMethod,
            },
            rowSelection: 'multiple',
        }
    },
    watch: {
        currentTruck: {
            handler(newValue) {
                this.columnDefs[0].headerName = newValue + '便日付'
                this.columnDefs[1].headerName = newValue + '便'
            },
        },
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: 'select',
                field: 'select',
                minWidth: 55,
                maxWidth: 55,
                resizable: false,
                headerCheckboxSelection: true,
                headerCheckboxSelectionFilteredOnly: true,
                checkboxSelection: true,
            },
            {
                headerName: this.currentTruck + '便日付',
                field: 'truckNoDate',
            },
            {
                headerName: 'トラック便',
                field: 'linkTruckNoName',
                cellStyle: { 'justify-content': 'flex-end' },
                width: 150,
            },
            {
                headerName: 'MAWB No.',
                field: 'awbno',
            },
            {
                headerName: 'HAWB No.',
                field: 'cwbno',
                width: 300,

                cellStyle: { 'text-align': 'center' },
                cellRenderer: 'GridButton',
                cellRendererParams: {
                    openHistoryStatusModal: this.openHistoryStatusModal,
                },
            },
            {
                headerName: '仕向地',
                field: 'destination',
                width: 120,
            },
            {
                headerName: '総個数',
                field: 'carryingPiece',
                cellStyle: { 'justify-content': 'flex-end' },
                width: 120,
            },
            {
                headerName: '総重量',
                field: 'carryingWeight',
                cellStyle: { 'justify-content': 'flex-end' },
                width: 120,
            },
            {
                headerName: '対査日時',
                field: 'arrivalDate',
            },
            {
                headerName: '対査個数',
                field: 'arrivalPiece',
                cellStyle: { 'justify-content': 'flex-end' },
                width: 140,
            },
            {
                headerName: 'CDB作成日時',
                field: 'cdbcreateDate',
            },
            {
                headerName: 'CDB戻り日時',
                field: 'cdbreturnDate',
            },
            {
                headerName: 'BIL作成日時',
                field: 'bilcreateDate',
            },
            {
                headerName: 'EDA作成日時',
                field: 'edacreateDate',
            },
            {
                headerName: 'ハード済',
                field: 'hard',
                width: 140,
            },
            {
                headerName: 'MEC/EDC作成日時',
                field: 'mecedccreateDate',
            },
            {
                headerName: '搬入前申告日時',
                field: 'reserveDeclarationDate',
            },
            {
                headerName: '区分',
                field: 'permitClassCd',
                cellStyle: { 'justify-content': 'flex-end' },
                width: 100,
            },
            {
                headerName: '搬入後申告日時',
                field: 'declarationDate',
            },
            {
                headerName: '許可日時',
                field: 'permissionDate',
            },
            {
                headerName: '積付日時',
                field: 'loadingDate',
            },
            {
                headerName: 'HDF作成日時',
                field: 'hdfcreationDate',
            },
            {
                headerName: 'コンテナNo',
                field: 'uldNo',
                cellStyle: { 'justify-content': 'flex-start' },
                width: 150,
            },
            {
                headerName: 'ULM作成日時',
                field: 'ulmcreateDate',
            },
        ]
        this.rowData = []
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        onExportCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },
        getParams() {
            const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
            return {
                fileName: '輸出申告データ照会_' + currentDate,
            }
        },
        openHistoryStatusModal(rowData) {
            this.$emit('openHistoryStatusModal', rowData)
        },
    },
    provide: {
        provideGridButtonLabel: '',
    },
}
</script>
