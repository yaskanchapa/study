<template>
  <va-card>
    <div class="row justify-start" style="padding: 0.75rem;">
      <div class="item flex xs2">
          <span style="padding: 0.3rem; width: 70px;">輸入/輸出区分</span>
          <va-select ref="impExpArray" style="width: 100px;" v-model="impExpClass" :options="impExpArray"/>
      </div>
      <div class="item flex xs5">
        <span style="padding: 0.3rem; width: 40px;">フォルダ名</span>
        <va-input style="width: 200px;" :disabled="isLoading" v-model="folderName" readonly/>
        <input type="file" webkitdirectory directory @change="onFolderSelect"  style="padding: 0.3rem;">
      </div>
    </div>
    <div class="row justify-end" style="padding: 0.5rem;">
      <div align = right>
        <va-button :disabled="isLoading" color="info" size="medium" @click="uploadFile(impExpClass)" style="width: 80px; margin-left: 20px;"> UPLOAD </va-button>
      </div>
   </div>
  </va-card>
</template>

<script>
export default {
  props: {        
    isLoading: Boolean,
    onFolderSelect: Function,
    uploadFile: Function,
    files: Array,
    impExpArray: Array
  },
  data () {
    return {
      folderName: '',
      impExpClass: ''
    }
  },
  watch: {
    files(newVal) {
      if (newVal.length > 0) {
        console.log('test', newVal)
        this.folderName = newVal[0].webkitRelativePath.split("/")[0]
      }
    }
  },
  mounted() {
    console.log('확인중:',this.files)
    if(this.files.length > 0){
      console.log('test', this.files)
      this.folderName = this.files[0].webkitRelativePath.split("/")[0]
    }
  },
  methods: {
  }
}
</script>
