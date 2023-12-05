<template>
  <div style="align-items: center; justify-content: center;">
    <va-button color="info" size="medium" :disabled="isLoading" @click="onPrint()" style="width: 110px;">印刷</va-button>
  </div>
</template>

<script>
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
  props: {
    changeLoading: Function,
    isLoading: Boolean,
    printData: Object,
  },
  components: {
  },
  data() {
    return {
      pdfUrl: '',
    }
  },
  methods: {
    onPrint () {
      // 画面ロック
      this.changeLoading()
      // 搬入伝票作成開始
      this.axios.post('/api/occs006/printBondInBill', this.printData)
      .then(res => {
        // 作成結果正常の場合
        console.log('【印刷結果】:',  res.data.result)
        if(res.data.result){
          notificationSuccess("印刷処理が完了致しました。")
          // S3保存先Path取得
          for (let i = 0; i < res.data.filePathList.length; i++) {
            this.pdfUrl = res.data.filePathList[i]
            this.axios({
              method: 'post',
              url: '/api/oait001/getPdf',
              data: { pdfUrl: this.pdfUrl },
              responseType: 'arraybuffer',
            })
            .then(response => {
              // Popより作成した搬入伝票PDF出力
              const file = new Blob([(response.data)], { type: 'application/pdf' })
              const fileUrl = URL.createObjectURL(file)
              window.open(fileUrl, '_blank', 'width=1560,height=800');
            })
          }
        } else {
          notificationError(res.data.message)
        }
      }).catch(error => {
        console.log(error)
        notificationError("サーバ通信でエラーが発生しました。",error.message)
      }).finally(() => {
        this.changeLoading()
      })
    },
  },
}
</script>
