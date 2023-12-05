<template>
  <div style="align-items: center; justify-content: center;">
    <va-button color="info" @click="btnClickedHandler()">{{provideGridButtonLabel}}</va-button>
  </div>
</template>

<script>
// import { getSingleRowData } from './CmnGridApi.vue'

export default {
  data() {
    return {
      gridButtonLabel: '確認',
      gridApi: null, // Grid Api
      rowIndex: -1, // Grid row index : int
      id: '', // Grid row ID : string
      value: false // boolean
    }
  },
  mounted() {
    this.gridApi = this.params.api
    this.rowIndex = this.params.rowIndex
    this.id = this.params.node.id
    this.value = this.params.value

    if (this.provideGridButtonLabel) {
      this.gridButtonLabel = this.provideGridButtonLabel
    }
  },
  methods: {
    refresh(params) {
      this.value = params.value
    },
    btnClickedHandler() {
      console.log('ToDo Override GridButton btnClickedHandler', this.value)
    }
  },
  inject: ['provideGridButtonLabel']
}
</script>
