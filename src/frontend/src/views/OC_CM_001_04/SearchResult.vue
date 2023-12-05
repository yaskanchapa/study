<template>
    <va-card style="padding: 0.5rem">
        <div class="row justify-start" style="height: 400px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="resultData.listRateMasterDto"
                    :defaultColDef="defaultColDef"
                    :suppressExcelExport="true"
                    :suppressRowClickSelection="true"
                    @grid-ready="onGridReady"
                    :stopEditingWhenCellsLoseFocus="true"
                >
                </ag-grid-vue>
            </div>
        </div>
        <div class="row items-center">
            <div class="flex items-center py-2 xs3">
                <span style="min-width: 10%">登録者</span>
                <va-input readonly v-model="regUser" />
            </div>
            <div class="flex items-center py-2 xs3">
                <span style="min-width: 10%">登録日時</span>
                <va-input readonly v-model="regDate" />
            </div>
        </div>
        <div class="row items-center">
            <div class="flex items-center py-2 xs3">
                <span style="min-width: 10%">変更者</span>
                <va-input readonly v-model="updUser" />
            </div>
            <div class="flex items-center py-2 xs3">
                <span style="min-width: 10%">変更日時</span>
                <va-input readonly v-model="updateDate" />
            </div>
        </div>
        <div class="row justify-end" style="padding: 0.5rem">
            <div class="item flex xs1">
                <va-button color="info" :disabled="!resultData.deleteEnable" size="medium" @click="this.$emit('handleUpdateBtnClicked', generateNewRate(resultData.listRateMasterDto))">更新</va-button>
            </div>
            <div class="item flex xs1">
                <va-button color="danger" :disabled="!resultData.deleteEnable" size="medium" @click="this.$emit('handleDelBtnClicked')">削除</va-button>
            </div>
            <div class="item flex xs1">
                <va-button color="info" :disabled="!resultData.csvEnable" size="medium" @click="onCSVBtn">CSV</va-button>
            </div>
        </div>
    </va-card>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import { dateFormat } from '@/utils/utilities'

export default {
    components: {
        AgGridVue,
    },
    props: {
        resultData: Object,
        searchCondition: [Object, String],
    },
    watch: {
        resultData() {
            this.regUser = this.resultData.listRateMasterDto[0].regUser
            this.regDate = this.resultData.listRateMasterDto[0].regDate
            this.updUser = this.resultData.listRateMasterDto[0].updUser
            this.updateDate = this.resultData.listRateMasterDto[0].updateDate
        },
    },
    data() {
        return {
            columnDefs: null,
            gridApi: null,
            columnApi: null,
            defaultColDef: {
                flex: 1,
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
            },
            regUser: '',
            regDate: '',
            updUser: '',
            updateDate: '',
        }
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: '通貨コード',
                field: 'name',
            },
            {
                headerName: '通貨名称',
                field: 'nameCd',
                flex: 2,
            },
            {
                headerName: 'レート',
                field: 'rate',
                valueParser: this.numberParser,
                editable: true,
            },
        ]
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        getParams() {
            const currentDate = dateFormat(Date.now())
            return {
                fileName: 'レート情報_' + currentDate,
            }
        },
        onCSVBtn() {
            this.gridApi.exportDataAsCsv(this.getParams())
        },
        numberParser(params) {
            return Number.isNaN(Number(params.newValue)) ? params.oldValue : Number(params.newValue)
        },
        generateNewRate() {
            const newRate = this.resultData.listRateMasterDto.map((rate) => {
                return {
                    startDate: dateFormat(this.searchCondition),
                    nameCd: rate.nameCd,
                    rate: rate.rate,
                    regUserCd: rate.regUserCd,
                    regDate: rate.regDate,
                }
            })
            return newRate
        },
        clearData() {
            this.regUser = ''
            this.regDate = ''
            this.updUser = ''
            this.updateDate = ''
        },
    },
}
</script>

