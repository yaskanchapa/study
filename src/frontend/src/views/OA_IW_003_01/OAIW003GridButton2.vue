<template>
  <!-- 終了ボタン -->
  <div style="align-items: center; justify-content: center;">
    <va-button color="info" @click="btnClickedHandler()">{{gridButtonLabel}}</va-button>
  </div>
</template>

<script>
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'
import { reactive } from 'vue'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
  props: {
    changeLoading: Function,
    isLoading: Boolean,
    resultRowData: Array,
  },
  data() {
    return {
      // gridButtonLabel: '確認',
      gridButtonLabel: '',
      gridApi: null, // Grid Api
      rowIndex: -1, // Grid row index : int
      id: '', // Grid row ID : string
      value: false // boolean
    }
  },
  mounted() {
    this.gridApi = this.params.api
    this.rowIndex = this.params.rowIndex
    this.id = this.params.node.id
    this.value = this.params.value
    const rowData = getSingleRowData(this.gridApi, this.id)
    if(rowData.endFlg === '0') {
      this.gridButtonLabel = reactive('未終了')
    } else {
      this.gridButtonLabel = reactive('終了済')
    }
  },
  methods: {
    btnClickedHandler() {
      const rowData = getSingleRowData(this.gridApi, this.id)

      // 選択データ有無チェック
      if(rowData.endFlg === '1') {
        notificationError('終了済みのデータです。')
        return
      }
      this.axios.post('/api/oaiw003/end', rowData)
      .then(res => {
        console.log("result: ", res.data)
        if(!res.data.result){
          notificationError("エラーが発生しました。", res.data.errorMessage)
        }
      }).catch(error => {
        console.log(error)
        notificationError("対象表示処理でエラーが発生しました。", error.message)
      }).finally(() => {

      })


      rowData.endFlg = '1'
      this.gridButtonLabel = '終了済'

    }
  },
}
</script>
