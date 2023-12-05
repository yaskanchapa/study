<!-- eslint-disable vue/no-unused-components -->
<template>
    <va-card style="padding: 0.5rem">
        <div class="row justify-start" style="height: 400px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="resultData.airImportDto"
                    :defaultColDef="defaultColDef"
                    :suppressRowClickSelection="true"
                    :rowSelection="rowSelection"
                    :stopEditingWhenCellsLoseFocus="true"
                    :suppressExcelExport="true"
                    @grid-ready="onGridReady"
                    @selection-changed="onSelectionChanged"
                >
                </ag-grid-vue>
            </div>
        </div>
        <div class="row justify-end" style="padding: 0.5rem">
            <div class="item flex xs1">
                <a :href="pathPDF" target="_blank">
                    <va-button color="info" size="medium" :disabled="checkPdfEnable" class="w-full" @click="onPDFBtn">PDF</va-button>
                </a>
            </div>
            <div class="item flex xs1">
                <va-button color="info" size="medium" :disabled="!resultData?.csvKEnable" class="w-full" @click="onCSVBtn">CSV</va-button>
            </div>
            <div class="item flex xs1">
                <a :href="pathCSVPermit">
                    <va-button color="info" size="medium" :disabled="!resultData?.csvLEnable" class="w-full" @click="onPermissionCSVBtn">許可後CSV</va-button>
                </a>
            </div>
        </div>
    </va-card>
</template>
<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import { dateFormat, gridColumnSortMethod } from '@/utils/utilities'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import GridButtonHistory from './OAIT002GridButton_History.vue'
import GridButtonFourth from './OAIT002GridButton_Fourth.vue'
import { getSelectdRowsData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
    props: {
        resultData: Object,
        searchCondition: Object,
        agentAuthCheck: Boolean,
    },
    components: {
        AgGridVue,
        GridButtonHistory, // eslint-disable-line
        GridButtonFourth, // eslint-disable-line
    },
    data() {
        return {
            columnDefs: null,
            rowData: null,
            gridApi: null,
            columnApi: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
                sortable: true,
                comparator: gridColumnSortMethod,
            },
            rowSelection: 'multiple',
            pathCSVPermit: null,
            pathPDF: null,
        }
    },

    beforeMount() {
        this.columnDefs = [
            {
                headerName: '',
                field: '',
                minWidth: 55,
                maxWidth: 55,
                resizable: false,
                sortable: false,
                checkboxSelection: true,
            },
            {
                headerName: 'MAWB No.',
                field: 'mawbNo',
            },
            {
                headerName: 'HAWB No.',
                field: 'hawbNo',
            },
            {
                headerName: 'ORIGIN',
                field: 'org',
            },
            {
                headerName: '業者',
                field: 'customer',
            },
            {
                headerName: '場所',
                field: 'place',
            },
            {
                headerName: '便名',
                field: 'arrName',
            },
            {
                headerName: '申告番号',
                field: 'reportNo',
            },
            {
                headerName: '確認/内点',
                field: 'naitenKakunIn',
            },
            {
                headerName: '通関STS',
                field: 'nameCustomsStatus',
            },
            {
                headerName: '',
                field: 'mawbNo',
                minWidth: 70,
                maxWidth: 70,
                cellRenderer: 'GridButtonHistory',
                cellRendererParams: {
                    openHistoryModal: this.openHistoryModal,
                    businessClass: '02',
                },
            },
            {
                headerName: '書類STS',
                field: 'namedocStatus',
            },
            {
                headerName: '',
                field: 'mawbNo',
                minWidth: 70,
                maxWidth: 70,
                cellRenderer: 'GridButtonHistory',
                cellRendererParams: {
                    openHistoryModal: this.openHistoryModal,
                    businessClass: '03',
                },
            },
            {
                headerName: '貨物STS',
                field: 'nameCarGoStatus',
            },
            {
                headerName: '',
                field: 'mawbNo',
                minWidth: 70,
                maxWidth: 70,
                cellRenderer: 'GridButtonHistory',
                cellRendererParams: {
                    openHistoryModal: this.openHistoryModal,
                    businessClass: '01',
                },
            },
            {
                headerName: '',
                field: 'mawbNo',
                minWidth: 70,
                maxWidth: 70,
                cellRenderer: 'GridButtonFourth',
                cellRendererParams: {
                    openFourthModal: this.openFourthModal,
                },
            },
            {
                headerName: '到着日',
                field: 'arrivedDate',
            },
            {
                headerName: '搬入日',
                field: 'impDate',
            },
            {
                headerName: '申告日',
                field: 'reportdate',
            },
            {
                headerName: '許可日',
                field: 'permitDate',
            },
            {
                headerName: '搬出年月日',
                field: 'carryOutDate',
            },
            {
                headerName: '品名',
                field: 'item',
            },
            {
                headerName: '許可区分',
                field: 'permitClassCd',
            },
            {
                headerName: '輸入者コード1',
                field: 'impCustomerNo',
            },
            {
                headerName: '輸入者コード2',
                field: 'impCustomerDeptCd',
            },
            {
                headerName: '輸入者コード3',
                field: 'impCustomerOcsDeptCd',
            },
            {
                headerName: '輸入者名',
                field: 'impCustomerName',
            },
            {
                headerName: '仕出人名',
                field: 'expCustomerName',
            },
            {
                headerName: '個数',
                field: 'cargoPiece',
            },
            {
                headerName: '重量',
                field: 'cargoWeight',
            },
            {
                headerName: '編集者',
                field: 'editUser',
            },
            {
                headerName: 'チェック者',
                field: 'checkUser',
            },
        ]
    },

    computed: {
        checkPdfEnable() {
            if (!this.agentAuthCheck) return !this.resultData?.pdfEnable
            else return true
        },
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },

        getParams() {
            const currentDate = dateFormat(Date.now(), 'yyyyMMDDhhmmss')
            return {
                fileName: '輸入申告データ照会_' + currentDate,
            }
        },

        // Get data and convert them to URL params string
        convertDataIntoString(object) {
            const { listHawb, itemPerPage, page, ...params } = object
            return params
                ? Object.keys(params)
                      .map((key) => `${key}=${encodeURIComponent(params[key])}`)
                      .join('&')
                : ''
        },

        onPermissionCSVBtn() {
            const params = this.convertDataIntoString(this.searchCondition)
            window.open(`${this.axios.defaults.baseURL}/api/oait002/csv/after-permission-download?${params}`, '_self')
        },

        onPDFBtn() {
            const selectedRows = getSelectdRowsData(this.gridApi)
            this.$emit('getPdfUrl', selectedRows)
        },

        onCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },

        // These will emit back to function in OAIT002Main
        openHistoryModal(params) {
            this.$emit('openHistoryModal', params)
        },

        openFourthModal(params) {
            this.$emit('openFourthModal', params)
        },
    },
    provide: {
        provideGridButtonLabel: '',
    },
}
</script>
