<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
    <div>
      <div class="ripple py-2 px-5 bar-color1-green text-white" align="left"
           style="width:100%;height:60px;font-size: 30px">
        WMS - 輸出保税業務
      </div>
    </div>
    <div style="text-align: center;font-size:30px" >
<router-link to="/airExportHT/oaew003portselect">
      <va-button  class="button-large" color="#0F008FFF">
        <div style="text-align: center;font-size:30px">
        対査確認
        </div>
      </va-button>
      </router-link>
    </div>
    <div style="text-align: center">
      <router-link to="/airExportHT/oaew003flightselect">
      <va-button class="button-large" color = "#15803BFF">
        <div style="text-align: center;font-size:30px">
        積付確認
        </div>
      </va-button>
      </router-link>
    </div>
  </div>
</template>

<style>
.bar-color1-green {
  --tw-bg-opacity: 1;
  background-color: #15803BFF;
}
.button-large {
  --tw-bg-opacity: 1;
  width : 98%;
  height : 100px;
}
</style>
<script>
</script>
