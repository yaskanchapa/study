<template>
    <div style="align-items: center; justify-content: center">
        <va-button color="info" size="medium">
            <va-icon name="search" size="medium" @click="btnClickedHandler" />
        </va-button>
    </div>
</template>


<script>
import GridButton from '../../components/commonGrid/GridButton.vue'
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'


export default {
  extends: GridButton,
  methods: {
    btnClickedHandler() {
      const RowData = getSingleRowData(this.gridApi, this.id);
      console.log(RowData);
    }
  }
}

</script>
