import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const oaet001store = createStore({

  state: {
    mecCwbList: [],
    edaCwbList: []
  },
  getters: {
    getMecCwbList(state) {
      return state.mecCwbList
      },
    getEdaCwbList(state) {
        return state.edaCwbList
    }
  },
  mutations: {
    setMecCwbList(state,cwbNo) {
      state.mecCwbList.push(cwbNo)
    },
    setEdaCwbList(state,cwbNo) {
        state.edaCwbList.push(cwbNo)
    },
    clearMecCwbList(state) {
      state.mecCwbList = []
     },
    clearEdaCwbList(state) { 
      state.edaCwbList = []
    },
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState({
      paths: ['oaet001store']
    })
  ]
})

export default oaet001store
