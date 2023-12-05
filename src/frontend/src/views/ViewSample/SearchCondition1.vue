<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">登録年月日</span>
          <va-date-input placeholder="Text Input" style="width: 130px;"/>
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">蔵置場所</span>
          <va-input style="width: 150px;" placeholder="Text Input" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">到着便名</span>
          <va-input style="width: 110px;" placeholder="Text Input" />
          <span style="padding: 0.3rem;">/</span>
          <va-input style="width: 110px;" placeholder="Text Input" />
      </div>
      <div class="item flex xs2">
          <span style="padding: 0.3rem;">ORIGIN</span>
          <va-input style="width: 150px;" placeholder="Text Input" />
      </div>
      <div class="item flex xs3">
          <span style="padding: 0.3rem;">MAWB No.</span>
          <va-select style="width: 260px;" v-model="mawbNo" :options="mawbNoOption" placeholder="--Please choose an option--"/>
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs3">
          <span style="padding: 0.3rem; padding-left: 38px;">バッチ</span>
          <va-switch v-model="switch1Value" true-inner-label="対象" false-inner-label="対象" size="large"/>
          <va-switch v-model="switch2Value" true-inner-label="対象外" false-inner-label="対象外" size="large"/>
      </div>
    </div>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs5">
          <span style="padding: 0.3rem;">アンマッチ</span>
          <va-switch v-model="switch3Value" true-inner-label="対象" false-inner-label="対象" size="large"/>
          <va-switch v-model="switch4Value" true-inner-label="対象外" false-inner-label="対象外" size="large"/>
          <span style="padding: 0.3rem; ">アンマッチ</span>
          <va-switch v-model="switch5Value" true-inner-label="対象外" false-inner-label="対象外" size="large"/>
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div class="item flex xs1">
        <va-button color="info" size="medium"> 対象表示(V) </va-button>
      </div>
      <div class="item flex xs1">
        <va-button color="info" size="medium"> クリア(C) </va-button>
      </div>
   </div>

  </va-card>
</template>

<script>
export default {
  data () {
    return {
      mawbNo: '',
      mawbNoOption: ['one', 'two', 'three', 'four', 'five', 'six', 'seven', 'eight', 'nine'],
      switch1Value: true,
      switch2Value: true,
      switch3Value: true,
      switch4Value: true,
      switch5Value: true
    }
  }
}
</script>

<style>
</style>
