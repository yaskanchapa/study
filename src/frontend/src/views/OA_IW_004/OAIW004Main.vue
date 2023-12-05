<!-- eslint-disable vue/multi-word-component-names -->
<template>
  <div class="grid md:grid-cols-1 sm:grid-cols-1 gap-4">
    <div>
      <div class="ripple py-2 px-5 bar-color1-green text-white" align="left"
           style="width:100%;height:60px;font-size: 30px">
        WMS - 輸入保税業務
      </div>
    </div>
    <div style="text-align: center">
      <router-link to="/airImportHT/oaiw004In">
        <va-button class="button-large" color="#0F008FFF">
          <div style="font-size: 30px">搬入作業</div>
        </va-button>
      </router-link>
    </div>
    <div style="text-align: center">
      <router-link to="/airImportHT/oaiw004Out">
        <va-button class="button-large" color = "#15803BFF">
          <div style="font-size: 30px">搬出作業</div>
        </va-button>
      </router-link>
    </div>
    <div style="text-align: center">
      <router-link to="/airImportHT/oaiw004Inv">
        <va-button class="button-large" color = "#FF5733">
          <div style="font-size: 30px">インベントリー</div>
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
import {VaButton} from "vuestic-ui";
export default {
  components: {VaButton},
  methods: {
    gotoIn(){}
  }
}
</script>
