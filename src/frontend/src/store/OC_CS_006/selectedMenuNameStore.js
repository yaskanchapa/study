import { createStore } from 'vuex'
import createPersistedState from 'vuex-persistedstate'

const menuNameStore = createStore({

  state: {
    menuName: ''
  },
  getters: {
    getMenuName(state) {
      return state.menuName
    }
  },
  mutations: {
    setMenuName(state, menuName) {
      state.menuName = menuName
    }
  },
  actions: {
  },
  modules: {
  },
  plugins: [
    createPersistedState('menuName')
  ]
})

export default menuNameStore
