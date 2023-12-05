<script>

import GridSwitch from '../../components/commonGrid/GridSwitch.vue'

export default {
  extends: GridSwitch,
  methods: {
    btnClickedHandler() {
      // 変更されたvalueをGridへ適応
      this.setToGrid()
      console.log('GridButton. Write business code here', this.value)
    }
  }
}

</script>