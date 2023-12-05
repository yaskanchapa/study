<script>

import GridSwitch from '../../components/commonGrid/GridSwitch.vue'
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
  extends: GridSwitch,
  methods: {
    btnClickedHandler() {
      // 変更されたvalueをGridへ適応
      this.setToGrid()
      console.log('GridButton. Write business code here', this.value)
      const rowData = getSingleRowData(this.gridApi, this.id)
      console.log("rowData", rowData)
      if(this.value) { // switchがONの場合、onBatchメソッドをコールする。
        console.log("call onBatch")
        this.params.context.onBatch(rowData, this.id)                
      } else { // switchがOFFの場合、offBatchメソッドをコールする。
        console.log("call offBatch")
        this.params.context.offBatch(rowData, this.id)
      }
    }
  }
}

</script>
