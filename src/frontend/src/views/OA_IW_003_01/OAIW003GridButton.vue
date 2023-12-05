<script setup>
import SearchStatus from '../OA_IW_003_02/OA_IW_003_02.vue'
</script>
<template>
  <!-- ステータスボタン -->
  <div style="align-items: center; justify-content: center;">
    <va-button color="info" @click="btnClickedHandler()">{{gridButtonLabel}}</va-button>
    <va-modal v-model="viewFlg" :title="title" fixed-layout>
        <va-card>
          <searchStatus
          v-bind:resultMainRowData="resultMainRowData"
          />
        </va-card>
    </va-modal>
  </div>
</template>

<script>
import { getSingleRowData } from '../../components/commonGrid/CmnGridApi.vue'
// import { reactive } from 'vue'
import { notificationError } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

export default {
  components: {
    searchStatus: SearchStatus
  },
  data() {
    return {
      gridApi: null, // Grid Api
      rowIndex: -1, // Grid row index : int
      id: '', // Grid row ID : string
      value: false, // boolean

      gridButtonLabel: 'ステータス',
      viewFlg: false,
      title: '履歴',
      columnDefs: null,
      rowSelection: null,

      resultMainRowData:[]
    }
  },
  created() {
    this.rowSelection = 'multiple'
  },
  mounted() {
    this.gridApi = this.params.api
    this.rowIndex = this.params.rowIndex
    this.id = this.params.node.id
    this.value = this.params.value
  },
  methods: {
    btnClickedHandler() {
      const rowData = getSingleRowData(this.gridApi, this.id)
      this.viewFlg = !this.viewFlg
      this.resultMainRowData = rowData
      console.log('対象データ: ',this.resultMainRowData);
    }
  },
}
</script>
