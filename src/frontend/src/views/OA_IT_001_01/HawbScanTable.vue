<template>
    <va-card style="overflow: hidden">
        <div class="row justify-start" style="height: 300px">
            <div class="ag-theme-alpine grid-wrapper">
                <ag-grid-vue
                    style="width: 100%; height: 100%"
                    :columnDefs="columnDefs"
                    :rowData="listHawbDisplay"
                    @grid-ready="onGridReady"
                    @selection-changed="onSelectionChanged"
                    :suppressRowClickSelection="true"
                    :stopEditingWhenCellsLoseFocus="true"
                    :rowSelection="rowSelection"
                    @cell-value-changed="onCellValueChanged"
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

export default {
    components: {
        AgGridVue,
    },
    props: {
        rdoCondition: String,
    },
    watch: {
        rdoCondition(newVal) {
            if (newVal === '3') {
                this.columnDefs[0].editable = false
                this.columnDefs[0].cellStyle = { background: '#f2f2f2', opacity: 0.5 }
            } else {
                this.columnDefs[0].editable = true
                this.columnDefs[0].cellStyle = { border: '1px dotted black', 'border-top': 'none', background: '#fff', opacity: 1 }
            }
        },
    },
    data() {
        return {
            listHawbDisplay: [{ hawb: '', eventFlg: 0 }],
            columnDefs: null,
            defaultColDef: {
                resizable: true,
                cellStyle: { 'border-right': '1px dotted' }, // eslint-disable-line
            },
            gridApi: null,
            columnApi: null,
            rowSelection: 'single',
        }
    },
    beforeMount() {
        this.columnDefs = [
            {
                headerName: 'HAWB No.',
                field: 'hawb',
                flex: 1,
                suppressMovable: true,
                editable: true,
                cellStyle: { border: '1px dotted black', 'border-top': 'none' },
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
            console.log(selectedRow)
        },
        onCellValueChanged(event) {
            const editedRow = event.rowIndex
            const newValue = event.newValue
            const arrayLength = this.listHawbDisplay.length

            if (editedRow === arrayLength - 1) {
                if (this.listHawbDisplay[arrayLength - 1].hawb !== '') {
                    this.listHawbDisplay = [
                        ...this.listHawbDisplay,
                        {
                            hawb: '',
                            eventFlg: 0,
                        },
                    ]
                }
            } else {
                if (newValue === '') {
                    const newListHawbDisplay = this.listHawbDisplay
                    newListHawbDisplay.splice(editedRow, 1)
                    this.listHawbDisplay = [...newListHawbDisplay]
                }
            }

            this.$emit('updateListHawbNo', this.listHawbDisplay)
        },
    },
}
</script>

