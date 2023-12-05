<script>

async function _lock(axios, lockReqInfo) {
  const lockResInfo = {
    result: false,
    errMessage: "",
    awbNo: lockReqInfo.awbNo,
    cwbNo: lockReqInfo.cwbNo,
    userCd: "",
    lockDateTime: ""
  }
  await axios.post('/api/logicalLock/getLock', {
        awbNo: lockReqInfo.awbNo,
        cwbNo: lockReqInfo.cwbNo,
        userCd: lockReqInfo.userCd,
        override: lockReqInfo.override
  }).then(res => {
    lockResInfo.result = res.data.result
    lockResInfo.errMessage = res.data.errMessage
    lockResInfo.userCd = res.data.userCd
    lockResInfo.lockDateTime = res.data.lockDateTime
  }).catch(() => {
    lockResInfo.result = false
    lockResInfo.errMessage = "ロック取得処理でエラーが発生しました。"
  })  
  return lockResInfo
}

async function _release(axios, lockReqInfo) { // eslint-disable-line
  const lockResInfo = {
    result: false,
    errMessage: "",
    awbNo: lockReqInfo.awbNo,
    cwbNo: lockReqInfo.cwbNo,
    userCd: "",
    lockDateTime: ""
  }
  await axios.post('/api/logicalLock/releaseLock', {
        awbNo: lockReqInfo.awbNo,
        cwbNo: lockReqInfo.cwbNo,
        userCd: lockReqInfo.userCd,
        override: lockReqInfo.override
  }).then(res => {
    lockResInfo.result = res.data.result
    lockResInfo.errMessage = res.data.errMessage
    lockResInfo.userCd = res.data.userCd
    lockResInfo.lockDateTime = null
  }).catch(() => {
    lockResInfo.result = false
    lockResInfo.errMessage = "ロック解除処理でエラーが発生しました。"
  })  
  return lockResInfo
}

async function _onOk(axios, lockReqInfo, okCallback, callBackParam) {
  // オーバーライドON
  lockReqInfo.override = true
  const lockResInfo = await _lock(axios, lockReqInfo)
  // 指定したコールバックメソッドを実行する。
  okCallback(lockResInfo, callBackParam)
}

/* getLogicalLock : HAWBの論路ロックを取得する。
  パラメータ1: axios ※呼び出し元のaxios
  パラメータ2：lockReqInfo
    ・String awbNo; // MAWB No.
    ・String cwbNo; // HAWB No.
    ・String userCd; // ユーザコード
    ・boolean override; // オーバーライドフラグ、Trueの場合ロックを上書きする。基本的にはFalseを指定すること。
  戻り値：lockResInfo
      ・boolean result; // 処理結果
      ・String errMessage; // エラーメッセージ
      ・String awbNo; // MAWB No.
      ・String cwbNo; // HAWB No.
      ・String userCd; // ユーザコード
      ・String lockDateTime; // ロックした日時(YYYY-MM-DD hh:mm:ss)
 */
export async function getLogicalLock(axios, lockReqInfo) {
  return await _lock(axios, lockReqInfo)
}

/* releaseLogicalLock : HAWBの論路ロックを取得する。
  パラメータ1: axios ※呼び出し元のaxios
  パラメータ2：lockReqInfo
    ・String awbNo; // MAWB No.
    ・String cwbNo; // HAWB No.
    ・String userCd; // ユーザコード
    ・boolean override; // オーバーライドフラグ、Trueの場合ロックを上書きする。基本的にはFalseを指定すること。
  戻り値：lockResInfo
      ・boolean result; // 処理結果
      ・String errMessage; // エラーメッセージ
      ・String awbNo; // MAWB No.
      ・String cwbNo; // HAWB No.
      ・String userCd; // ユーザコード
 */
export async function releaseLogicalLock(axios, lockReqInfo) {
  return await _release(axios, lockReqInfo)
}
 
 /* releaseAllLockByUserCd : ユーザコードと紐づく全ロックを解除する。
  パラメータ: axios ※呼び出し元のaxios (this.axios)
  パラメータ： _userCd キーを削除するユーザコード
  戻り値： なし
 */
 export async function releaseAllLockByUserCd(axios, _userCd) {
  await axios.post('/api/logicalLock/releaseAllLockByUserCd', {
        awbNo: null,
        cwbNo: null,
        userCd: _userCd,
        override: false
  })
}

/* getLogicalLockWithModal : HAWBの論路ロックを取得する。取得失敗時はダイアログを表示しOKボタン押下時はロックをオーバーライドする。
  パラメータ: axios ※呼び出し元のaxios (this.axios)
  パラメータ： vaModal ※呼び出し元のvaModal (this.$vaModal)
  パラメータ：lockReqInfo
    ・String awbNo; // MAWB No.
    ・String cwbNo; // HAWB No.
    ・String userCd; // ユーザコード
    ・boolean override; // オーバーライドフラグ、Trueの場合ロックを上書きする。
  パラメータ：okCallback ロック取得後のコールバックメソッド
  パラメータ：callBackParam コールバックメソッドのパラメータ
  戻り値：lockResInfo
      ・boolean result; // 処理結果
      ・String errMessage; // エラーメッセージ
      ・String awbNo; // MAWB No.
      ・String cwbNo; // HAWB No.
      ・String userCd; // ユーザコード
 */
export async function getLogicalLockWithModal(axios, vaModal, lockReqInfo, okCallback, callBackParam) {
  const lockResInfo = await _lock(axios, lockReqInfo)
  if(!lockResInfo.result) {    
    const _title = `ロックユーザ:${lockResInfo.userCd}、  HAWB No:${lockReqInfo.cwbNo}、  日時: ${lockResInfo.lockDateTime}`
    const _message = "上記のユーザが該当データを利用しています。このデータの変更処理を継続しますか？"
     // Modal表示
     vaModal.init({          
          title: _title,
          message: _message,
          noDismiss: true, // Disable both close on overlay click and close on Esc
          okText: 'はい',
          cancelText: 'いいえ',
          onOk: () => {
            // ModalのOKボタン押下時に_onOkCallbackメソッドを実行する。
            _onOk(axios, lockReqInfo, okCallback, callBackParam)
          },         
        })
  } else {
    // ロックを正常に取得したら、指定したコールバックメソッドを実行する。
    okCallback(lockResInfo, callBackParam)
  }
}

export default { getLogicalLock , releaseLogicalLock, getLogicalLockWithModal, releaseAllLockByUserCd }
</script>