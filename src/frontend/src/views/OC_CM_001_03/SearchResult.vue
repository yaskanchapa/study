<template>
    <va-card class="p-2">
        <div class="row justify-start h-[450px]">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="resultData.listCatererSenderMasterDto"
                    :defaultColDef="defaultColDef"
                    :stopEditingWhenCellsLoseFocus="true"
                    :suppressExcelExport="true"
                    :pagination="true"
                    :paginationPageSize="paginationPageSize"
                    :rowSelection="rowSelection"
                    @grid-ready="onGridReady"
                    @selection-changed="onSelectionChanged"
                >
                </ag-grid-vue>
                <div class="flex justify-between">
                    <div>
                        <select class="w-40 border-2 border-slate-200" v-on:change="onPageSizeChanged()" id="page-size">
                            <option value="10" selected>10/page</option>
                            <option value="30">30/page</option>
                            <option value="50">50/page</option>
                            <option value="100">100/page</option>
                        </select>
                    </div>
                    <div class="flex xs1 mt-3">
                        <va-button v-show="!isModal" color="info" size="medium" :disabled="!resultData.csvEnable" @click="onCSVBtn">CSV</va-button>
                    </div>
                </div>
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
import GridButton from './OCCM00103GridButton.vue' // eslint-disable-line
import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
    components: {
        AgGridVue,
        GridButton, // eslint-disable-line
    },
    props: {
        resultData: Object,
        enableClickToSearch: Boolean,
        isModal: Boolean,
    },
    data() {
        return {
            columnDefs: null,
            gridApi: null,
            columnApi: null,
            rowSelection: 'single',
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
                sortable: true,
                comparator: gridColumnSortMethod,
            },
            paginationPageSize: 10,
        }
    },
    beforeMount() {
        if (this.isModal) {
            this.columnDefs = [
                {
                    headerName: '業者No',
                    field: 'customerNo',
                },
                {
                    headerName: 'N枝番',
                    field: 'deptCd',
                },
                {
                    headerName: '仕出/仕向',
                    field: 'csrCseFlag',
                    valueGetter: (params) => this.csrCseFlagTableDisplay(params.data.csrCseFlag),
                },
                {
                    headerName: '使用場所',
                    field: 'shiyoBashoFlag',
                    valueGetter: (params) => this.shiyoBashoFlagTableDisplay(params.data.shiyoBashoFlag),
                },
                {
                    headerName: 'K枝番',
                    field: 'seq',
                },
                {
                    headerName: '国',
                    field: 'countryCd',
                },
                {
                    headerName: '英文会社名',
                    field: 'customerNamee',
                },
                {
                    headerName: '和文部署名',
                    field: 'division',
                },
                {
                    headerName: '郵便番号',
                    field: 'zipCd',
                },
                {
                    headerName: '英文住所1',
                    field: 'customerAdde1',
                },
                {
                    headerName: '英文住所2',
                    field: 'customerAdde2',
                },
                {
                    headerName: '英文住所3',
                    field: 'customerAdde3',
                },
                {
                    headerName: '英文住所4',
                    field: 'customerAdde4',
                },
                {
                    headerName: '英文住所(一括)',
                    field: 'customerAddeblAnket',
                },
                {
                    headerName: '担当者名',
                    field: 'charger',
                },
                {
                    headerName: '登録者',
                    field: 'reguserCd',
                },
                {
                    headerName: '登録日時',
                    field: 'regDate',
                },
                {
                    headerName: '変更者',
                    field: 'updateUserCd',
                },
                {
                    headerName: '変更日時',
                    field: 'updateDate',
                },
            ]
        } else {
            this.columnDefs = [
                {
                    headerName: '',
                    field: '',
                    minWidth: 100,
                    maxWidth: 100,
                    cellRenderer: 'GridButton',
                    cellRendererParams: {
                        updateTableData: this.updateTableData,
                        openDetailModal: this.openDetailModal,
                    },
                },
                {
                    headerName: '業者No',
                    field: 'customerNo',
                },
                {
                    headerName: 'N枝番',
                    field: 'deptCd',
                },
                {
                    headerName: '仕出/仕向',
                    field: 'csrCseFlag',
                    valueGetter: (params) => this.csrCseFlagTableDisplay(params.data.csrCseFlag),
                },
                {
                    headerName: '使用場所',
                    field: 'shiyoBashoFlag',
                    valueGetter: (params) => this.shiyoBashoFlagTableDisplay(params.data.shiyoBashoFlag),
                },
                {
                    headerName: 'K枝番',
                    field: 'seq',
                },
                {
                    headerName: '国',
                    field: 'countryCd',
                },
                {
                    headerName: '英文会社名',
                    field: 'customerNamee',
                },
                {
                    headerName: '和文部署名',
                    field: 'division',
                },
                {
                    headerName: '郵便番号',
                    field: 'zipCd',
                },
                {
                    headerName: '英文住所1',
                    field: 'customerAdde1',
                },
                {
                    headerName: '英文住所2',
                    field: 'customerAdde2',
                },
                {
                    headerName: '英文住所3',
                    field: 'customerAdde3',
                },
                {
                    headerName: '英文住所4',
                    field: 'customerAdde4',
                },
                {
                    headerName: '英文住所(一括)',
                    field: 'customerAddeblAnket',
                },
                {
                    headerName: '担当者名',
                    field: 'charger',
                },
                {
                    headerName: '登録者',
                    field: 'reguserCd',
                },
                {
                    headerName: '登録日時',
                    field: 'regDate',
                },
                {
                    headerName: '変更者',
                    field: 'updateUserCd',
                },
                {
                    headerName: '変更日時',
                    field: 'updateDate',
                },
            ]
        }
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        csrCseFlagTableDisplay(csrCseFlag) {
            if (csrCseFlag === '0') return `0 : オリジナル`
            if (csrCseFlag === '1') return `1 : 仕出`
            if (csrCseFlag === '2') return `2 : 仕向`
        },
        shiyoBashoFlagTableDisplay(shiyoBashoFlag) {
            if (shiyoBashoFlag === '0') return `0 : オリジナル`
            if (shiyoBashoFlag === '1') return `1 : 編集`
        },
        onPageSizeChanged() {
            // eslint-disable-next-line no-var
            var value = document.getElementById('page-size').value
            this.gridApi.paginationSetPageSize(Number(value))
        },
        getParams() {
            const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
            return {
                fileName: '仕出人仕向人検索結果_' + currentDate,
            }
        },
        onCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },

        // This will emit back to function in OCCM00103Main
        openDetailModal(action, params) {
            this.$emit('openDetailModal', action, params)
        },

        // Able to use if enableClickToSearch = true
        onSelectionChanged() {
            if (this.enableClickToSearch) {
                const selectedRows = getSelectdRowsData(this.gridApi)
                this.$emit('handleClickToSearchAction', selectedRows[0])
            }
        },
    },
    provide: {
        provideGridButtonLabel: '',
    },
}
</script>

