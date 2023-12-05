<script>

import GridButton from '../../components/commonGrid/GridButton.vue'
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'

export default {
  extends: GridButton,
  methods: {
    btnClickedHandler() {
      console.log(this.params)
      console.log('GridButton. Write business code here', this.value)
      const RowData = getSingleRowData(this.gridApi, this.id)
      console.log('RowData:', RowData)
    }
  }
}

</script>
