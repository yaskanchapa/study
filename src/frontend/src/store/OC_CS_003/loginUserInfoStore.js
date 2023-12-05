import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const loginStore = createStore({

  state: {
    // LoginUser情報
    authPermission: {
      isLogin: '',
      userCd: '',
      userName: '',
      userNameSyllabary: '',
      userNameEng: '',
      userManagementAuthorityCd: '',
      userAuthorityCd: '',
      userCompanyCd: '',
      departmentCd: '',
      userAuthorityGroupCd: '',
      printUserCompanyNm: '',
      printUserCompanyCd: '',
      printUserName: '',
      departmentName: ''
    }
  },
  getters: {
    getAuthPermission(state) {
      return state.authPermission
    }
  },
  mutations: {
    setAuthPermission(state, authPermission) {
      if (authPermission) {
        state.authPermission.isLogin = 'true'
      }
      state.authPermission.userCd = authPermission.userCd
      state.authPermission.userName = authPermission.userName
      state.authPermission.userNameSyllabary = authPermission.userNameSyllabary
      state.authPermission.userNameEng = authPermission.userNameEng
      state.authPermission.userManagementAuthorityCd = authPermission.userManagementAuthorityCd
      state.authPermission.userAuthorityCd = authPermission.userAuthorityCd
      state.authPermission.userCompanyCd = authPermission.userCompanyCd
      state.authPermission.departmentCd = authPermission.departmentCd
      state.authPermission.userAuthorityGroupCd = authPermission.userAuthorityGroupCd
      state.authPermission.printUserCompanyNm = authPermission.printUserCompanyNm
      state.authPermission.printUserCompanyCd = authPermission.printUserCompanyCd
      state.authPermission.printUserName = authPermission.printUserName
      state.authPermission.departmentName = authPermission.departmentName

    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState({
      paths: ['authPermission']
    })
  ]
})

export default loginStore
