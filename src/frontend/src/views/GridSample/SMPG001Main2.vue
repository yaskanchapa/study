<template>
    <div class="ag-theme-alpine grid-wrapper">
      <ag-grid-vue
          style="width: 100%; height: 100%;"
          :columnDefs="columnDefs"
          :rowData="rowData"
          @grid-ready="onGridReady"
          :defaultColDef="defaultColDef"
          :suppressRowClickSelection="true"
          :rowSelection="rowSelection"
          :stopEditingWhenCellsLoseFocus="true"
          @cell-value-changed="onCellValueChanged"
          @selection-changed="onSelectionChanged">
      </ag-grid-vue>
    </div>
</template>

<script>
// import { reactive, onMounted, ref } from 'vue'
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'

import '../../components/commonGrid/GridCss.css'
import { setAllRowData, getSelectdRowsData, getSelectedNodes } from '../../components/commonGrid/CmnGridApi.vue'
import GridButton from './SMPG001GridButton.vue'
import GridSwitch from './SMPG001GridSwitch.vue'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'

export default {
  name: 'App',
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor, // eslint-disable-line
    gridSwitch: GridSwitch, // eslint-disable-line
    gridButton: GridButton // eslint-disable-line
  },
  data() {
    return {
      columnDefs: null,
      rowData: null,
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        resizable: true,
        cellStyle: {'border-right': '1px dotted'} // eslint-disable-line
      },
      rowSelection: null
    }
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
        cellClass: 'cellCenter'
      },
      {
        field: 'switch',
        cellRenderer: 'gridSwitch',
        minWidth: 120,
        maxWidth: 120,
        resizable: false
      },
      {
        field: 'numInput',
        editable: true,
        minWidth: 200,
        cellEditor: 'numericGridEditor'
      },
      { field: 'country', minWidth: 150 },
      {
        field: 'btn',
        minWidth: 200,
        cellRenderer: 'gridButton'
      },
      { field: 'date', minWidth: 150 },
      { field: 'sport', minWidth: 150 },
      { field: 'gold' },
      { field: 'silver' },
      { field: 'bronze' },
      { field: 'total' }
    ]
    this.rowData = [
      { switch: false, numInput: 1, country: 'United States', btn: '', date: '24/08/2008', sport: 'Swimming', gold: 8, silver: 0, bronze: 0, total: 8 },
      { switch: false, numInput: 2, country: 'United States', btn: '', date: '24/08/2008', sport: 'Swimming', gold: 8, silver: 0, bronze: 0, total: 8 },
      { switch: true, numInput: 3, country: 'United States', btn: '', date: '24/08/2008', sport: 'Swimming', gold: 8, silver: 0, bronze: 0, total: 8 },
      { switch: true, numInput: 4, country: 'United States', btn: '2008', date: '24/08/2008', sport: 'Swimming', gold: 8, silver: 0, bronze: 0, total: 8 }
    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  methods: {
    onChgAllRowBtnClicked() {
      const allData = [
        { switch: false, select: '', numInput: 23, country: 'United Kingdom', btn: '', date: '24/08/11', sport: 'Running', gold: 1, silver: 2, bronze: 3, total: 4 },
        { switch: true, select: '', numInput: 24, country: 'United Kingdom', btn: '', date: '24/08/33', sport: 'Running', gold: 5, silver: 6, bronze: 7, total: 8 },
        { switch: false, select: '', numInput: 24, country: 'United Kingdom', btn: '', date: '24/08/11', sport: 'Running', gold: 9, silver: 10, bronze: 11, total: 12 },
        { switch: true, select: '', numInput: 23, country: 'United Kingdom', btn: '', date: '24/08/33', sport: 'Running', gold: 8, silver: 0, bronze: 14, total: 13 }
      ]
      setAllRowData(this.gridApi, allData)
    },
    onGridReady(params) {
      this.gridApi = params.api
      this.gridColumnApi = params.columnApi
    },
    onCellValueChanged(params) {
      const columnId = params.colDef.field
      const rowData = params.data
      const newValue = params.newValue
      const oldValue = params.oldValue
      console.log('onCellValueChanged columnId:', columnId)
      console.log('onCellValueChanged newValue:', newValue)
      console.log('onCellValueChanged oldValue:', oldValue)
      console.log('onCellValueChanged rowData:', rowData)
    },
    onSelectionChanged(params) {
      console.log(params)
      const selectedRows = getSelectdRowsData(this.gridApi)
      console.log('selectedRows: ', selectedRows)

      const selectedNodes = getSelectedNodes(this.gridApi)
      console.log('selectedNodes: ', selectedNodes)
    }
  },
  provide: {
    providedValue: 'testValue', // provide this value to grid components
    provideGridSwitchAgreeLabel: '対象',
    provideGridSwitchDisAgreeLabel: '対象外',
    provideGridButtonLabel: 'ConsoleOutRowData'
  }
}
</script>
