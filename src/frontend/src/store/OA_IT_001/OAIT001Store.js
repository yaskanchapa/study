import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const oait001store = createStore({

  state: {
    micCwbList: [],
    idaCwbList: []
  },
  getters: {
    getMicCwbList(state) {
      return state.micCwbList
      },
    getIdaCwbList(state) {
        return state.idaCwbList
    }
  },
  mutations: {
    setMicCwbList(state,cwbNo) {
      state.micCwbList.push(cwbNo)
    },
    setIdaCwbList(state,cwbNo) {
        state.idaCwbList.push(cwbNo)
    },
    clearMicCwbList(state) {
      state.micCwbList = []
     },
    clearIdaCwbList(state) { 
      state.idaCwbList = []
    },
    deleteMicCwbList(state, cwbNo) { 
      const micList = state.micCwbList
      const target = micList.findIndex(item => item.cwbNo === cwbNo)
      if (target !== -1) { 
        micList.splice(target,1)
      }
      state.micCwbList = micList
    },
    deleteIdaCwbList(state, cwbNo) { 
      const idaList = state.idaCwbList
      const target = idaList.findIndex(item => item.cwbNo === cwbNo)
      if (target !== -1) { 
        idaList.splice(target,1)
      }
      state.idaCwbList = idaList
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState({
      paths: ['oait001store']
    })
  ]
})

export default oait001store
