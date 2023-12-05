<template>
    <va-card class="p-2">
        <div class="row justify-start h-[500px]">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="resultData.listUserMasterDto"
                    :defaultColDef="defaultColDef"
                    :suppressRowClickSelection="true"
                    :stopEditingWhenCellsLoseFocus="true"
                    :suppressExcelExport="true"
                    :pagination="true"
                    :paginationPageSize="paginationPageSize"
                    @grid-ready="onGridReady"
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
                        <va-button color="info" size="medium" :disabled="!resultData.csvEnable" @click="onCSVBtn">CSV</va-button>
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
import GridButton from './OCCM001GridButton.vue'

export default {
    components: {
        AgGridVue,
        GridButton, // eslint-disable-line
    },
    props: {
        resultData: Object,
    },
    data() {
        return {
            columnDefs: null,
            gridApi: null,
            columnApi: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' },
                sortable: true,
                comparator: gridColumnSortMethod,
            },
            paginationPageSize: 100,
        }
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: '',
                field: '',
                minWidth: 100,
                maxWidth: 100,
                cellRenderer: 'GridButton',
                cellRendererParams: {
                    openDetailModal: this.openDetailModal,
                },
            },
            {
                headerName: 'ユーザーコード',
                field: 'userCd',
            },
            {
                headerName: 'ユーザー名(漢字)',
                field: 'username',
            },
            {
                headerName: 'ユーザー名(カナ)',
                field: 'usernameSyllabary',
            },
            {
                headerName: 'ユーザー名(英)',
                field: 'usernameEng',
            },
            {
                headerName: '管理権限',
                field: 'usermanagementAuthorityCd',
            },
            {
                headerName: '業務権限',
                field: 'userAuthorityCd',
            },
            {
                headerName: '所属会社',
                field: 'userCompanyCd',
            },
            {
                headerName: '所属部署',
                field: 'departmentCd',
            },
        ]
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        onPageSizeChanged() {
            const value = document.getElementById('page-size').value
            this.gridApi.paginationSetPageSize(Number(value))
        },
        getParams() {
            const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
            return {
                fileName: 'ユーザー検索結果_' + currentDate,
            }
        },
        onCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },
        // This will emit back to function in OCCM00101Main
        openDetailModal(action, params) {
            this.$emit('openDetailModal', action, params)
        },
    },
    provide: {
        provideGridButtonLabel: '',
    },
}
</script>

