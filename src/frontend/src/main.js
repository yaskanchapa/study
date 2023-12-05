import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
// import { createVuesticEssential, VaButton } from 'vuestic-ui'
import 'vuestic-ui/styles/essential.css'
import 'vuestic-ui/styles/grid.css'
import 'vuestic-ui/styles/reset.css'
import 'vuestic-ui/styles/typography.css'
import { createVuestic } from 'vuestic-ui'
import 'vuestic-ui/css'
import './main.css'
import axios from 'axios'
import mitt from 'mitt'
import vueAwesomeSidebar from 'vue-awesome-sidebar'
import 'vue-awesome-sidebar/dist/vue-awesome-sidebar.css'

import { releaseAllLockByUserCd } from '@/components/HawbLogicalLock/HawbLogicalLock.vue'
import loginStore from '@/store/OC_CS_003/loginUserInfoStore'
// createApp(App).use(store).use(router).use(createVuesticEssential({ components: { VaButton } })).mount('#app')

// Set ag-grid License
import { LicenseManager } from 'ag-grid-enterprise'
LicenseManager.setLicenseKey('CompanyName=Kokusai Express Co., Ltd.,LicensedGroup=Kokusai Express IT Dept.,LicenseType=MultipleApplications,LicensedConcurrentDeveloperCount=1,LicensedProductionInstancesCount=0,AssetReference=AG-036623,SupportServicesEnd=28_February_2024_[v2]_MTcwOTA3ODQwMDAwMA==ca57bb90444609cf4017ce6933687744')

// axiosのdefaults設定
axios.defaults.baseURL = 'http://localhost:8080'
axios.defaults.headers.common.Authorization = localStorage.getItem('access_token')

const emitter = mitt()
const app = createApp({
  extends: App,
  created() {
    // 画面リフレッシュ(F5)又は画面を閉じた時のハンドラー登録
    window.addEventListener("beforeunload", this.leaving);
  },
  methods: {
    leaving() {
      const userCd = loginStore.getters.getAuthPermission.userCd
      if(userCd) {
        // ログインユーザコード全ロックを解除
        releaseAllLockByUserCd(axios, userCd)
      }      
    }
  }
});

app.config.globalProperties.emitter = emitter
app.config.globalProperties.axios = axios

app.use(store).use(router).use(createVuestic()).use(vueAwesomeSidebar).mount('#app')
