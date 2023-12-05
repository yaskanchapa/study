<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs2">          
          <va-input style="width: 150px;" :disabled="isLoading" v-model="awbNoCnt" readonly/>
          <span style="padding: 0.3rem;">件</span>
      </div>
      <div class="item flex xs2">          
          <va-input style="width: 150px;" :disabled="isLoading" v-model="outFileCnt" readonly/>
          <span style="padding: 0.3rem;">ファイル</span>
      </div>
      <div class="item flex xs1">
        <va-button :disabled="isLoading" color="info" size="medium" @click="onOut()"> OUT </va-button>
      </div>
    </div>
  </va-card>
</template>

<script>
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
export default {
  props: {        
    isLoading: Boolean,
    createOutFile: Function,
  },
  data () {
    return {      
        awbNoCnt: '',
        outFileCnt: ''
    }
  },
  mounted() {    
  },
  methods: {
    onOut() {
      this.createOutFile()
    },
    setOutResult(_awbNoCnt, _outFileCnt) {
      console.log("setOutResult", _awbNoCnt, _outFileCnt)
      this.awbNoCnt = _awbNoCnt
      this.outFileCnt = _outFileCnt
    }
  }
}
</script>

<style>
</style>
