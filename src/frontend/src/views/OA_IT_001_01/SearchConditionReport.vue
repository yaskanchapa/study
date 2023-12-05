<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable vue/no-mutating-props -->
<template>
  <va-card>
        <div class="row justify-start" style="padding: 0.75rem;">
          <div class="item flex xs3">
            <span style="padding: 0.3rem;">MAWB</span>
            <va-input style="width: 150px;" v-model="searchCondition.awbNo" placeholder="Text Input" @change="selectFltList"/>
          </div>
          <div class="item flex xs4">
            <span style="padding: 0.3rem;">FLT</span>
            <va-select style="width: 260px;" placeholder="--Please choose an option--" v-model="searchCondition.currentArrFlt1" :options="currentArrFlt1List"/>
          </div>
        </div>
        <div class="row justify-start" style="padding: 0.75rem;">
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.hchFlg" :label="hchLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.hdmFlg" :label="hdmLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.chaFlg" :label="chaLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.micFlg" :label="micLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">
            <va-checkbox v-model="searchCondition.reCreate" :label="reCreateLabel" right-label :disabled="disabledFlg == 1"/>
          </div>
          <div class="item flex xs1">

          </div>
          <div class="item flex xs2">
            <va-button color="info" size="medium" @click="writeReport"> 作成 </va-button>
          </div>
        </div>
        <va-divider />
        <div>
          <div class="row justify-start">
            <div class="item flex xs12">
              <span style="padding: 0.3rem;">電文作成結果</span>
            </div>
          </div>
        <div class="row justifiy-start" style="padding: 0.3rem">
          <div class="item flex xs1">
            <span style="padding: 0.3rem;">HCH</span>
          </div>
          <div class="item flex xs1">
            <va-input  v-model="cntReportResult.cntHCH" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">件</span>
          </div>
          <div class="item flex xs2">
            <va-input v-model="cntReportResult.cntFileHCH" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">ファイル</span>
          </div>
          <div class="item flex xs1">
              <span style="padding: 0.3rem;">HDM</span>
            </div>
            <div class="item flex xs1">
              <va-input v-model="cntReportResult.cntHDM" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">件</span>
            </div>
            <div class="item flex xs2">
              <va-input v-model="cntReportResult.cntFileHDM" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">ファイル</span>
            </div>
        </div>
        <div class="row justifiy-start" style="padding: 0.3rem">
          <div class="item flex xs1">
            <span style="padding: 0.3rem;">CHA</span>
          </div>
          <div class="item flex xs1" >
            <va-input v-model="cntReportResult.cntCHA" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">件</span>
          </div>
          <div class="item flex xs2">
            <va-input v-model="cntReportResult.cntFileCHA" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
            <span style="padding: 0.3rem;">ファイル</span>
          </div>
          <div class="item flex xs1">
              <span style="padding: 0.3rem;">MIC</span>
            </div>
            <div class="item flex xs1">
              <va-input v-model="cntReportResult.cntMIC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">件</span>
            </div>
            <div class="item flex xs2">
              <va-input v-model="cntReportResult.cntFileMIC" style="width: 150px;" placeholder="0" :disabled="true" input-class="text-right"/>
              <span style="padding: 0.3rem;">ファイル</span>
            </div>
        </div>
        </div>
  </va-card>
</template>
<script>
export default {
  props: {
    writeReport: Function,
    searchCondition: Object,
    currentArrFlt1List: Array,
    searchImportData: Function,
    clearResult: Function,
    selectFltList: Function,
    cntReportResult: Object
  },
  data() {
    return {
      idaLabel: 'IDA',
      micLabel: 'MIC',
      chaLabel: 'CHA',
      edcLabel: 'EDC',
      hchLabel: 'HCH',
      hdmLabel: 'HDM',
      reCreateLabel: '再作成',
    }
  },
}
</script>
