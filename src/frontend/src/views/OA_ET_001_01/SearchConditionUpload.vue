<script setup>
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
</script >
<!-- eslint-disable vue/no-mutating-props -->
<!-- eslint-disable indent -->
<template>
  <va-card style="height:fit-content;">
    <div class="flex w-full">
      <div class="grid flex-grow card place-items-left" style="border-radius: revert; max-width: 50%;">
        <div class="row justify-start" >
          <div class="item flex xs12">
            <span style="padding: 0.3rem;">デフォルト値</span>
          </div>
        </div>

        <div class="row justify-start" >
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">部署</span>
            <va-select style="padding: 0.3rem;" v-model="defaultSearchCond.currentDeptCd" 
              :options="deptCdList" 
              :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :value-by="(option) => option.VALUE"
              highlight-matched-text
            />
          </div>
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">代理店コード</span>
            <va-select style="padding: 0.3rem;" v-model="defaultSearchCond.currentAgency" 
              :options="agencyList" 
              :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :value-by="(option) => option.VALUE"
              highlight-matched-text
            />
          </div>
        </div>

        <div class="row justify-start">
          <div class="item flex xs6">
            <span style="padding: 0.3rem;">通販</span>
            <va-select style="padding: 0.3rem;" v-model="defaultSearchCond.currentMailOrder" 
              :options="mailOrderList" 
              :text-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :track-by="(option) => option.VALUE + ' : ' + option.OPTION"
              :value-by="(option) => option.VALUE" 
              />
          </div>
        </div>
        <div class="row justify-end" style="padding: 0.5rem;">
            <div class="item flex xs2">
              <va-button color="info" type="submit" size="medium" @click="searchDefault"  >検索</va-button>
            </div>
          </div>

        <va-divider />

        <div class="row justify-start">
          <va-tabs  v-model="defaultSearchCond.currentTabValue" >
            <template #tabs>
              <va-tab v-for="tab in defaultTabs" :color="tab.color" :key="tab.name" :name="tab.name">{{ tab.title }}</va-tab>
            </template>
          </va-tabs>
        </div>

        <div class="row justify-start">
          <div class="ag-theme-alpine grid-wrapper">
              <ag-grid-vue style="width: 100%; height: 200px" class="ag-theme-alpine" :columnDefs="columnDefs" :rowData="defaultRowData" >
              </ag-grid-vue>
          </div>
        </div>
      </div>

      <div class="grid flex-grow card place-items-left" style="border-radius: revert;">
          <div class="row justify-start">
            <div class="item flex xs12">
              <h1 style="padding: 0.3rem;">取込</h1>
            </div>
          </div>
      
          <div class="row justify-start place-items-center">
            <div class="item flex xs6">
              <span style="padding: 0.3rem;">MAWB</span>
              <va-input v-model="searchCondition.awbNo" style="padding: 0.3rem;" />
            </div>
            <div class="item flex xs6">
                <input type="file" ref="uploadExcel" @change="selectFile" />
              </div>
          </div>

        <!-- <div class="row justify-start place-items-center">
          <div class="item flex xs6">
              <input type="file" ref="uploadExcel" style="padding: 0.3rem;" @change="selectFile" />
              </div>
          </div> -->
          <div class="row justify-end"  style="padding: 0.5rem;">
            <!-- <div class="item flex xs3">
              <va-button size="medium" color="info" type="submit" @click="resetDefVal" >
                デフォルト値再設定
              </va-button>
            </div>  -->
            <div class="item flex xs3">
              <va-button size="medium" color="info" type="submit" @click="insertExcelFile">
                取込
              </va-button>
            </div>
          </div>
          <div class="row justify-end"  style="padding: 7.5rem;">            
            <div class="item flex xs1">
            </div>
          </div>       
        </div>
    </div>
  </va-card>
</template>
<script>
// eslint-disable-next-line no-unused-vars
import axios from 'axios'
// eslint-disable-next-line no-unused-vars
import Datepicker from '@vuepic/vue-datepicker'
import '@vuepic/vue-datepicker/dist/main.css'
import moment from 'moment' // eslint-disable-line
import _ from 'lodash' // eslint-disable-line
import { AgGridVue } from 'ag-grid-vue3'
import 'ag-grid-enterprise'
import 'ag-grid-community/styles/ag-grid.css'
import 'ag-grid-community/styles/ag-theme-alpine.css'
import '../../components/commonGrid/GridCss.css'
import NumericGridEditor from '../../components/commonGrid/NumericGridEditor.vue'
import { notificationSuccess, notificationError, notificationInfo, notificationWarn } from '../../components/Notification/NotificationApi.vue' // eslint-disable-line

const DEFAULTTABS = [
  { title: '01', name: '0046', color: 'success' },
  { title: '02', name: '0400', color: 'success' },
  { title: '03', name: '0401', color: 'success' },
  { title: '04', name: '0402', color: 'success' }
]

export default {
  name: 'App',
  props: {
    searchCondition: Object,
    resettingDefaultValue: Function,
    changeLoading: Function,
    checkAwbNo: Function,
    deptCdList: Array,
    agencyList: Array
  },
  components: {
    AgGridVue,
    numericGridEditor: NumericGridEditor // eslint-disable-line
  },
  data() {
    return {
      defaultSearchCond: {
        currentDeptCd: loginStore.getters.getAuthPermission.departmentCd,   
        currentMailOrder: '0',   
        currentAgency: '', 
        currentTabValue: '0046',
      },  
      awbNo:'',
      mailOrderList: [{ VALUE: '0', OPTION: '通常' }, { VALUE: '1', OPTION: '通販' }],
      defaultTabs: DEFAULTTABS,
      testArr: [],
      value: '01',
      inputvalue1: '',
      inputvalue2: '',
      selectFiles: '',
      columnDefs: null,
      defaultRowData: [],
      gridApi: null,
      columnApi: null,
      defaultColDef: {
        resizable: true,
        cellStyle: { 'border-right': '1px dotted' } // eslint-disable-line
      },
      rowSelection: null
    }
  },
  beforeMount() {
    this.columnDefs = [
      {
        headerName: '番号',
        field: 'nameCd',
        minWidth: 55,
        maxWidth: 150,
        resizable: false,

        cellClass: 'cellCenter'
      },
      {
        headerName: 'デフォルト項目',
        field: 'name',
        minWidth: 120,
        maxWidth: 200,
        resizable: false
      },
      {
        headerName: 'デフォルト設定値',
        field: 'characterItem',
        editable: true,
        minWidth: 200
        // cellEditor: 'numericGridEditor'
      }
    ]
    this.rowData = [

    ]
  },
  created() {
    this.rowSelection = 'multiple'
  },
  computed: {
    currentTab() {
      return this.defaultTabs.find(({ title }) => title === this.currentTabValue)
    }
  },
  methods: {
    selectFile() {
      this.selectFiles = this.$refs.uploadExcel.files
    },
    searchDefault() { 
      this.changeLoading()
      this.axios({
        url: '/api/oaet001/getDefaultList',
        method: 'get',
        params: this.defaultSearchCond,
        enctype: 'multipart/form-data'
      })
        .then(res => {
          this.defaultRowData = res.data
          // this.defaultRowData = res.data
        }).catch(error => {
          notificationError("検索処理でエラーが発生しました。", error.message)
        }).finally(() => {
          this.changeLoading()
        })
    },
    insertExcelFile() {
      // eslint-disable-next-line prefer-const
      let frm = new FormData()
      
      frm.append('awbNo', this.searchCondition.awbNo)
      frm.append('uploadFile', this.selectFiles.item(0))
      frm.append('currentDeptCd', this.defaultSearchCond.currentDeptCd)
      frm.append('currentTabValue', this.defaultSearchCond.currentTabValue)
      frm.append('currentMailOrder', this.defaultSearchCond.currentMailOrder)
      frm.append('currentAgency', this.defaultSearchCond.currentAgency)
      frm.append('currentDefaultList', JSON.stringify(Array.from(this.getDefaultList().entries())))
      this.checkAwbNo(frm)
    },
    resetDefVal() {
      this.resettingDefaultValue(this.defaultSearchCond.defaultRowData, this.searchCondition.awbNo)
    },
    getDefaultList() {
      const defaultList = new Map()
      for (let i = 0; i < this.defaultRowData.length; i++) {
        defaultList.set(this.defaultRowData[i].nameCd, this.defaultRowData[i].characterItem)
      }
      return defaultList
    }
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

.ant-notification-top {
  margin-top: 30%;
}

.bar-color1-red {
  --tw-bg-opacity: 1;
  background-color: #2b89e7;
}

.bar-color2-red {
  --tw-bg-opacity: 1;
  background-color: #337ecc
}

.bar-color3 {
  --tw-bg-opacity: 1;
  background-color: #858d96
}
</style>