<script setup>
import GridSample from '../GridSample/SMPG001Main2.vue'
</script>

<template>
  <va-card style="padding: 0.5rem;">
    <div class="row justify-start" style="height: 500px;">
      <GridSample />
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs2">
        <va-button color="info" size="medium"> バッチクリア </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium"> 登録 </va-button>
      </div>
      <div class="item flex xs2">
        <va-button color="info" size="medium"> CSVダウンロード </va-button>
      </div>
   </div>
  </va-card>
</template>

<script>
export default {
  data () {
    return {
      mawbNo: '',
      mawbNoOption: ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine']
    }
  }
}
</script>

<style>
</style>
