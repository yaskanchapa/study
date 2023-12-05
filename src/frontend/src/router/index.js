import { createRouter, createWebHistory } from 'vue-router'
// import loginStore from '../store/OC_CS_003/loginUserInfoStore'
// import axios from 'axios'
import WinPop from '../views/OC_CS_001_01/childWinPop.vue'


const routes = [
  // Home
  {
    path: '/',
    name: 'Home',
    component: () => import('@/layout/MainPage.vue'),
    redirect: '/login',
    meta: {
      title: 'Home'
    },
    // Body部の画面
    children: [
      // 未作成ページ
      {
        path: '/nonepage',
        name: 'NonePage',
        component: () => import('../views/NonePage.vue')
      },
      {
        path: '/viewSample2',
        name: 'ViewSample2',
        component: () => import('../views/ViewSample/ViewSampleMain.vue')
      },
      { // 搬入データ編集
        path: '/oaiw001Main',
        name: 'OAIW001Main',
        component: () => import('../views/OA_IW_001/OAIW001Main.vue')
      },
      { // 搬出データ編集
        path: '/oaiw002Main',
        name: 'OAIW002Main',
        component: () => import('../views/OA_IW_002/OAIW002Main.vue')
      },
      { // コンテナ登録
        path: '/oaew002Main',
        name: 'OAEW002Main',
        component: () => import('../views/OA_EW_002/OAEW002Main.vue')
      },
      { // 輸入保税(PC) - インベントリー指示
        path: '/oaiw003Main',
        name: 'OAIW003Main',
        component: () => import('../views/OA_IW_003_01/OAIW003Main.vue')
      },
      {   // 輸入-通関-申告データ編集
        path: '/airimport001',
        name: 'AirImport001',
        component: () => import('../views/OA_IT_001_01/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oait001main',
            name: 'OAIT001Main',
            component: () => import('../views/OA_IT_001_01/OAIT001Main.vue')
          }
        ]
      },
      {   // 輸入申告データ照会
        path: '/airimport002',
        name: 'AirImport002',
        component: () => import('../views/OA_IT_002_01/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oait002main',
            name: 'OAIT002Main',
            component: () => import('../views/OA_IT_002_01/OAIT002Main.vue')
          }
        ]
      },
      // 輸入情報検索
      {
        path: '/airImport003',
        name: 'AirImport003',
        component: () => import('../views/OA_IT_003/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oait003Main',
            name: 'OAIT003Main',
            component: () => import('../views/OA_IT_003/OAIT003Main.vue')
          }
        ]
      },
      // 輸入情報検索
      {
        path: '/airImport004',
        name: 'AirImport004',
        component: () => import('../views/OA_IT_004/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oait004Main',
            name: 'OAIT004Main',
            component: () => import('../views/OA_IT_004/OAIT004Main.vue')
          }
        ]
      },
      // 上屋データ照会
      {
        path: '/airImportW005',
        name: 'AirImportW005',
        component: () => import('../views/OA_IW_005/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oaiw005Main',
            name: 'OAIW005Main',
            component: () => import('../views/OA_IW_005/OAIW005Main.vue')
          }
        ]
      },
      {   // 輸入-通関-申告データ編集
        path: '/airexport001',
        name: 'AirExport001',
        component: () => import('../views/OA_ET_001_01/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oaet001main',
            name: 'OAET001Main',
            component: () => import('../views/OA_ET_001_01/OAET001Main.vue')
          }
        ]
      },
      // 搬入伝票照会
      {
        path: '/airExportW001',
        name: 'AirExportW001',
        component: () => import('../views/OA_EW_001/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oaew001Main',
            name: 'OAEW001Main',
            component: () => import('../views/OA_EW_001/OAEW001Main.vue')
          }
        ]
      },

      {   // Air Export
        path: '/airexport002',
        name: 'AirExport002',
        component: () => import('../views/OA_ET_002/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oaet002main',
            name: 'OAET002Main',
            component: () => import('../views/OA_ET_002/OAET002Main.vue')
          }
        ]
      },

      // 輸出情報検索
      {
        path: '/exportSearch',
        name: 'ExportSearch',
        component: () => import('../views/OA_ET_003/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'oaet003main',
            name: 'OAET003main',
            component: () => import('../views/OA_ET_003/OAET003main.vue')
          }
        ]
      },
      // ユーザーマスタ
      {
        path: '/usermaster',
        name: 'UserMaster',
        component: () => import('../views/OC_CM_001_01/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'occm00101main',
            name: 'OCCM00101Main',
            component: () => import('../views/OC_CM_001_01/OCCM00101Main.vue')
          },
          {
            path: 'occm00102main',
            name: 'OCCM00102Main',
            component: () => import('../views/OC_CM_001_02/OCCM00102Main.vue')
          },
          {
            path: 'occm00103main',
            name: 'OCCM00103Main',
            component: () => import('../views/OC_CM_001_03/OCCM00103Main.vue')
          },
          {
            path: 'occm00104main',
            name: 'OCCM00104Main',
            component: () => import('../views/OC_CM_001_04/OCCM00104Main.vue')
          }
        ]
      },
      // 共通機能
      {
        path: '/common',
        name: 'Common',
        component: () => import('../views/OC_CS_007/index.vue'),
        meta: {
          title: 'KNACCS'
        },
        children: [
          {
            path: 'occs007main',
            name: 'OCCS007main',
            component: () => import('../views/OC_CS_007/OCCS007Main.vue')
          }
        ]
      },
    ]
  },
  // 輸入保税HT
  {
    path: '/airImportHT',
    name: 'AirImportHT',
    component: () => import('../views/OA_IW_004/index.vue'),
    children: [
      {
        path: 'oaiw004Main',
        name: 'OAIW004Main',
        component: () => import('../views/OA_IW_004/OAIW004Main.vue')
      },
      {
        path: 'oaiw004In',
        name: 'OAIW004In',
        component: () => import('../views/OA_IW_004/OAIW004In.vue')
      },
      {
        path: 'oaiw004Out',
        name: 'OAIW004Out',
        component: () => import('../views/OA_IW_004/OAIW004Out.vue')
      },
      {
        path: 'oaiw004Inv',
        name: 'OAIW004Inv',
        component: () => import('../views/OA_IW_004/OAIW004Inv.vue')
      }
    ]
  },
  // 輸出保税HT
  {
    path: '/airExportHT',
    name: 'AirExportHT',
    component: () => import('../views/OA_EW_003/index.vue'),
    children: [
      {
        path: 'oaew003Main',
        name: 'OAEW003Main',
        component: () => import('../views/OA_EW_003/OAEW003Main.vue')
      }, {
        path: 'oaew003portselect',
        name: 'OAEW003portselect',
        component: () => import('../views/OA_EW_003/OAEW003portselect.vue')
      },
      {
        path: 'oaew003flightselect',
        name: 'OAEW003flightselect',
        component: () => import('../views/OA_EW_003/OAEW003flightselect.vue')
      },
      {
        path: 'oaew003containerregist:flightName',
        name: 'OAEW003containerregist',
        component: () => import('../views/OA_EW_003/OAEW003containerregist.vue')
      },
      {
        path: 'oaew003examination:portName',
        name: 'OAEW003examination',
        component: () => import('../views/OA_EW_003/OAEW003examination.vue')
      },
      {
        path: 'oaew003stowage:flightName:containerName',
        name: 'OAEW003stowage',
        component: () => import('../views/OA_EW_003/OAEW003stowage.vue')
      }
    ]
  },

  // 404Error
  {
    path: '/404',
    name: '404',
    component: () => import('@/layout/error/error404.vue')
  },
  {
    path: "/:pathMatch(.*)*",
    redirect: "/404"
  },
  // Login
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/OC_CS_003/LogIn.vue')
  },
  {
    // Main画面作成後Main画面にパス修正予定
    path: '/main',
    name: 'Main',
    component: () => import('../layout/MainPage.vue'),
    meta: { authorization: ['01'] }
  },
  // 権限がなかった場合
  {
    path: '/noneauth',
    name: 'NoneAuth',
    component: () => import('../views/OC_CS_003/NoneAuth.vue')
  },
  // Ag-Grid Sample
  {
    path: '/gridSample',
    name: 'GridSample',
    component: () => import('../views/GridSample/SMPG001Main.vue')
  },
  // View Sample
  {
    path: '/viewSample',
    name: 'ViewSample',
    component: () => import('../views/ViewSample/ViewSampleMain.vue')
  },
  // errortest
  {
    path: '/errortest',
    name: 'errortest',
    component: () => import('../views/errorSample/error.vue')
  },
  // errortest
  {
    path: '/errorrst',
    name: 'errorrst',
    component: () => import('../layout/error/errorBase.vue')
  },
  {
    path: '/500error',
    name: 'InternalServerError',
    component: () => import('../layout/error/error500.vue')
  },
  {
    path: '/pdfViewer',
    component: WinPop
  },
]

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
})

// // router実行前の処理
// router.beforeEach((to, from, next) => {
//   // 接続権限の設定値取得
//   const { authorization } = to.meta
//   console.log('接続権限の設定値:', authorization)
//   // LoginUserの権限情報取得
//   const authPermission = loginStore.getters.getAuthPermission
//   console.log('LoginUserの情報:', authPermission)
//   console.log('LoginUserの接続権限:', authPermission?.userManagementAuthorityCd)
//   // 接続権限を設定したパスであれば以下の処理を実行する
//   if (authorization) {
//     // Loginの有無判断 Loginされていなければログインページに移動
//     if (!authPermission?.isLogin) {
//       return next({ path: '/' })
//     }
//     // 接続権限設定値のLoginUserの権限一致確認
//     if (
//       authorization.length &&
//       !authorization.includes(authPermission?.userManagementAuthorityCd)
//     ) {
//       // 接続権限ないユーザーの場合は、権限不一致ページに移動
//       return next({ path: '/noneauth' })
//     }
//   }
//   // ページ更新の際にヘッダーのトークン情報セット
//   axios.get('/api/refresh').then(res => {
//   })
//   // 接続権限設定がされてないパスに関しては、誰でも接続可能である為下記のnext()よりそのまま通過される
//   next()
// })

export default router
