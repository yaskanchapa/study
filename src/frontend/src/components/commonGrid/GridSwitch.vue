<template>
  <div class="switch-center">
    <va-switch @click="btnClickedHandler()" v-model="value" :true-inner-label="SwitchAgreeLabel" :false-inner-label="SwitchDisAgreeLabel" />
  </div>
</template>

<script>
import { setSingleCellValue } from './CmnGridApi.vue'

export default {
  data() {
    return {
      SwitchAgreeLabel: '', // Agree label
      SwitchDisAgreeLabel: '', // DisAgree label
      gridApi: null, // Grid Api
      rowIndex: -1, // Grid row index : int
      id: '', // Grid row ID : string
      field: '',
      value: false // boolean
    }
  },
  mounted() {
    this.gridApi = this.params.api
    this.rowIndex = this.params.rowIndex
    this.id = this.params.node.id
    this.value = this.params.value
    this.field = this.params.colDef.field

    if (this.provideGridSwitchAgreeLabel) {
      this.SwitchAgreeLabel = this.provideGridSwitchAgreeLabel
    }
    if (this.provideGridSwitchDisAgreeLabel) {
      this.SwitchDisAgreeLabel = this.provideGridSwitchDisAgreeLabel
    }
  },
  methods: {
    refresh(params) {
      this.value = params.value
    },
    setToGrid() {
      setSingleCellValue(this.gridApi, this.id, this.field, this.value)
    },
    btnClickedHandler() {
      // 変更されたvalueをGridへ適応
      this.setToGrid()
      console.log('ToDo Override GridSwitch btnClickedHandler', this.value)
    }
  },
  inject: ['provideGridSwitchAgreeLabel', 'provideGridSwitchDisAgreeLabel']
}
</script>

<style>
.switch-center {
  margin-top: 4px;
  display: flex;
  justify-content: center;
}
</style>