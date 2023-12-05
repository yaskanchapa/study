<script setup>
import SearchCondition from './SearchCondition.vue'
</script>

<template>  
  <div class="container">
    <va-inner-loading :loading="isLoading" :size="60">
      <!--検索条件-->
      <div class="ripple w-1340 py-2 px-5 bar-color1-red text-white" align="left">PDFアップロード</div>
      <div class="screen-1340-210 col-span-12 xl:col-span-12 md:col-span-6" style="height: fit-content">
        <va-tabs v-model="currentView">
          <template #tabs>
            <va-tab  v-for="tab in tabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
          </template>
        </va-tabs>
        <component :is="currentView"
          v-bind:onFolderSelect="onFolderSelect"
          v-bind:uploadFile="uploadFile"
          v-bind:isLoading="isLoading"
          v-bind:files="files"
          v-bind:impExpArray="impExpArray"
          ref="schCondition">
        </component>
      </div>
    </va-inner-loading>
  </div>
</template>

<script>
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const TABS = [
  { title: 'アップロード', name: 'searchCondition', color: 'success' }
]

export default {
  components: {
    searchCondition: SearchCondition, // eslint-disable-line
  },
  data: () => ({
    tabs: TABS,
    currentView: 'searchCondition',
    isLoading: false,
    files: [],
    impExpArray: [
      '輸入' ,
      '輸出'
    ],
  }),
  computed: {
    currentTab () {
      return this.tabs.find(({ title }) => title === this.tabValue)
    }
  },
  methods: {
    changeLoading () {
      this.isLoading = !this.isLoading
    },
    onFolderSelect(e) {
      // 選択フォルダ内のファイル取得
      this.files = Array.from(e.target.files);
    },
    uploadFile(impExpClass) {
      this.changeLoading()

      // 輸入/輸出区分入力値チェック
      if(!impExpClass){
        notificationError("輸入/輸出区分を入力してください。")
        this.changeLoading()
        return false;
      }

      // formDataへ取得ファイル格納
      const formData = new FormData();

      if(this.files.length > 0){
        for (let i = 0; i < this.files.length; i++) {
          formData.append('file', this.files[i]);
        }
        // formDataサーバー送信
        this.axios.post('/api/occs007/pdfUpload', formData, { params: { impExpClassName: impExpClass } })
        .then(res => {
          if(res.data.result) {
            notificationSuccess(res.data.message)
          } else {
            notificationError(res.data.message, res.data.errorMessage)
          }  
        }).catch(error => {
          console.log(error)
          notificationError("アップロード処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
      } else {
        notificationError("アップロードデータを選択してください。")
        this.changeLoading()
      }
    },
  }
}
</script>

<style>
.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}
.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #538ce2;
}
</style>