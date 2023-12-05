<template>
  <div class="checkbox-center">
    <va-checkbox @click="btnClickedHandler()" v-model="value" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      gridApi: null, // Grid Api
      rowIndex: -1, // Grid row index : int
      id: '', // Grid row ID : string
      field: '', // Grid column field
      value: false // boolean
    }
  },
  mounted() {
    this.gridApi = this.params.api
    this.rowIndex = this.params.rowIndex
    this.id = this.params.node.id
    this.value = this.params.value
    this.field = this.params.colDef.field
  },
  methods: {
    refresh(params) {
      this.value = params.value
    },
    setToGrid() {// 変更されたvalueをGridへ適応
      this.params.node.setDataValue(this.field, this.value);
    },
    btnClickedHandler() {
      // 変更されたvalueをGridへ適応
      this.setToGrid()
      // console.log('ToDo Override btnClickedHandler', this.value)
    }
  }  
}
</script>

<style>
.checkbox-center {
  margin-top: 10px;
  display: flex;
  justify-content: center;
}
</style>