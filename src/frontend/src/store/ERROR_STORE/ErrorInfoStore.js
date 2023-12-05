import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const loginStore = createStore({

  state: {
    // Error情報
    ErrorInfo: {
      status: '',
      errorCode: '',
      message: '',
    }
  },
  getters: {
    getErrorInfo(state) {
      return state.ErrorInfo
    }
  },
  mutations: {
    setErrorInfo(state, errorResponse) {
      state.ErrorInfo.status = errorResponse.status
      state.ErrorInfo.errorCode = errorResponse.data.code
      state.ErrorInfo.message = errorResponse.data.message
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState({
      paths: ['']
    })
  ]
})

export default loginStore
