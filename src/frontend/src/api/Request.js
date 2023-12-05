import axios from 'axios'

const setAuthHeader = (token) => {
  // console.log('【ヘッダにトークンを設定します】')
  if(token){
    // console.log('【トークン値有の処理を実行】:', token)
    axios.defaults.headers = {
      Authorization: token
    }
    // console.log('【ヘッダにトークン設定完了】')
  } else {
    // console.log('【トークン値無し】')
    // console.log('【ヘッダにトークン未設定】')
  }
}
export default setAuthHeader
