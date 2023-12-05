import router from '@/router'
import errorStore from '@/store/ERROR_STORE/ErrorInfoStore.js'
import axios from 'axios'

const error = (error) => {
  errorStore.commit('setErrorInfo',error.response)
  if(error.response.status/100 === 4){
    router.push('/errorrst')
  } else if(error.response.status === 500) {
    console.log('【Redis初期化】')
    axios.get('/api/logout').then((res) => {
    })
    console.log('【localStorage初期化】')
    localStorage.clear()
    console.log('【ヘッダトークン情報初期化】')
    delete this.axios.defaults.headers.Authorization
    router.push('/500error')
  } else if(error.response.status/100 === 5) {
    router.push('/errorrst')
  }
}
export default error
