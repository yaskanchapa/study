<template>
    <va-card style="padding: 0.5rem; overflow: hidden">
        <div class="row justify-start" style="height: 400px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue style="width: 100%; height: 100%" :columnDefs="columnDefs" :rowData="resultRowData" @grid-ready="onGridReady" @selection-changed="onSelectionChanged" />
            </div>
        </div>
        <div class="row justify-around px-20 pt-3">
            <div class="item flex xs2">
                <va-button color="info" size="medium" @click="handleUpdateData">登録</va-button>
            </div>
            <div class="item flex xs2">
                <va-button color="info" size="medium" @click="handleCloseModal">閉じる</va-button>
            </div>
        </div>
    </va-card>
</template>

<script>
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import { notificationSuccess, notificationError, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line
import FlagCheck from './FlagCheck.vue'
import { NOTIFICATION_DURATION } from '@/utils/utilities'
import { UDPATE_NOT_NULL_MESSAGE, UPDATE_ERROR_MESSAGE, UPDATE_SUCCESS_MESSAGE } from '@/utils/messages'

const arrangementDetailUpdate = 'api/oait002/cs-optional-service/update'

export default {
    props: {
        isLoading: Boolean,
        resultRowData: Array,
        hawbNo: String,
    },
    components: {
        AgGridVue,
        FlagCheck, // eslint-disable-line
    },
    data() {
        return {
            columnDefs: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
            },
            gridApi: null,
            columnApi: null,
            rowSelection: null,
            updatedFields: {
                listCsOptionalServiceDto: [],
            },
        }
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: '確',
                field: 'aposManagementFlag',
                minWidth: 100,
                maxWidth: 100,
                resizable: false,
                cellRenderer: 'FlagCheck',
                cellRendererParams: {
                    handleEditData: this.handleEditData.bind(this),
                },
            },
            {
                headerName: '特別手配No',
                field: 'optionalServiceNo',
                resizable: true,
                flex: 1,
            },
            {
                headerName: 'OPコード',
                field: 'optionalServiceCd',
                resizable: true,
                flex: 1,
            },
            {
                headerName: '手配内容',
                field: 'comment',
                resizable: true,
                flex: 1,
            },
        ]
    },
    created() {
        this.rowSelection = 'single'
    },
    methods: {
        onGridReady(params) {
            this.gridApi = params.api
            this.gridColumnApi = params.columnApi
        },
        onSelectionChanged() {},
        handleEditData(data, updatedValue) {
            const newData = {
                importExportClass: 1,
                optionalServiceNo: data.optionalServiceNo,
                cwbNo: this.hawbNo,
                optionalServiceCd: data.optionalServiceCd,
                aposManagementFlag: Number(updatedValue),
            }

            const index = this.updatedFields.listCsOptionalServiceDto.findIndex((item) => item.optionalServiceNo === newData.optionalServiceNo && item.optionalServiceCd === newData.optionalServiceCd)
            index === -1 ? this.updatedFields.listCsOptionalServiceDto.push(newData) : (this.updatedFields.listCsOptionalServiceDto[index] = newData)
        },

        handleUpdateData() {
            const updateData = this.updatedFields
            if (updateData.listCsOptionalServiceDto.length === 0) notificationWarn(UDPATE_NOT_NULL_MESSAGE, '', NOTIFICATION_DURATION)
            else {
                this.axios
                    .put(arrangementDetailUpdate, updateData)
                    .then((res) => {
                        notificationSuccess(UPDATE_SUCCESS_MESSAGE, '', NOTIFICATION_DURATION)
                    })
                    .catch((error) => {
                        console.log(error)
                        notificationError(UPDATE_ERROR_MESSAGE, error.message)
                    })
                    .finally(() => {
                        this.handleCloseModal()
                    })
            }
        },
        handleCloseModal() {
            this.$emit('closeFourthModal')
        },
    },
}
</script>

