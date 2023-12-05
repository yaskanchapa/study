<template>
  <div class="header-checkbox-center">
    <span>{{ params.displayName }}</span>
    <va-checkbox @click="btnClickedHandler()" v-model="value" />
  </div>
</template>

<script>
export default {
  data() {
    return {
      gridApi: null, // Grid Api
      value: false // boolean
    }
  },
  mounted() {
    this.gridApi = this.params.api
  },
  methods: {
    btnClickedHandler() { // チェック時にhtDisplayFlg行の値を切り替える。
      this.gridApi.forEachNode((rowNode, index) => {
        rowNode.setDataValue('htDisplayFlg', this.value)
      })
    }
  }
}
</script>

<style>
.header-checkbox-center {
  display: flex;
  align-items: center;
  gap: 5px;
}
</style>