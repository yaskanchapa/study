// COMMON ERROR MESSAGES
const SYSTEM_ERROR_MESSAGE                  = 'システムエラーが発生しました。'
const SEARCH_ERROR_MESSAGE                  = '検索処理でエラーが発生しました。'
const UPDATE_ERROR_MESSAGE                  = '更新処理でエラーが発生しました。'
const DELETE_ERROR_MESSAGE                  = '削除処理でエラーが発生しました。'
const REGISTER_ERROR_MESSAGE                = '登録処理でエラーが発生しました。'
const MISSING_DATE_MESSAGE                  = '日付を入力してください。'
const DELETE_NOT_NULL_MESSAGE               = '削除データを選択して下さい。'
        
// COMMON WARNING MESSAGES             
const NO_RESULT_FOUND_MESSAGE               = '対象データが存在しません。'
const UDPATE_NOT_NULL_MESSAGE               = '更新データが無い'     
        
// COMMON SUCCESS MESSAGES             
const UPDATE_SUCCESS_MESSAGE                = 'データ更新が成功しました。'
const DELETE_SUCCESS_MESSAGE                = 'データ削除が成功しました。'
const REGISTER_SUCCESS_MESSAGE              = '登録が完了しました。'        

// ASK TO CONFIRM PROMPT        
const DELETE_CONFIRM_ACTION_MESSAGE         = '本当にデータを削除しますか？'
        
// OA_IT_002_02            
const DUPLICATE_HAWBNO_MESSAGE              = '同じHAWBが既に存在します。'

// OA_ET_002
const DIFFERENT_DATA_ERROR_MESSAGE          = '選択データと削除データが違います。'
const EMPTY_SELECTION_ERROR_MESSAGE         = '削除したいステータスを選択してください。'
const EMPTY_FIELD_INPUT_ERROR_MESSAGE       = '必要データを入力してください'
     
// OC_CM_001 
const DATA_NOT_EXIST_MESSAGE                = '該当のデータが存在しない。ご確認ください。'
const INVALID_INPUT_MESSAGE                 = '入力されたデータが不正。'
const REQUIRED_INPUT_MESSAGE                = '検索項目を指定下さい。'
             
const IMAGE_SIZE_LIMIT_MESSAGE              = '10Mb以下のファイルを選択して下さい。'
const IMAGE_FILE_TYPE_PDF_MESSAGE           = 'PDFファイルを選択してください。'
const IMAGE_NOT_NULL_MESSAGE                = 'イメージファイルを選択してください。'
const IMAGE_UPLOAD_SUCCESS_MESSAGE          = 'イメージの登録が成功しました。'
const IMAGE_UPLOAD_ERROR_MESSAGE            = 'イメージアップロード処理でエラーが発生しました。'
const IMAGE_NOT_EXIST_MESSAGE               = 'イメージファイルが存在しません。'
        
// HELPER FUNCTIONS
// GET DETAIL SUCCESS MESSAGE
const getDetailSuccessMessage = (type, fieldName, value) => {
  let typeString
  switch (type) {
    case "register":
      typeString = "の登録が成功"
      break;
    case "update":
      typeString = "を更新"
      break;
    case "delete":
      typeString = "を削除"
      break;
  } 
  return `${fieldName}:${value}${typeString}しました。`
}
export {
  SYSTEM_ERROR_MESSAGE, SEARCH_ERROR_MESSAGE, UPDATE_ERROR_MESSAGE, DELETE_ERROR_MESSAGE, REGISTER_ERROR_MESSAGE, MISSING_DATE_MESSAGE, DELETE_NOT_NULL_MESSAGE,
  NO_RESULT_FOUND_MESSAGE, UDPATE_NOT_NULL_MESSAGE,
  UPDATE_SUCCESS_MESSAGE, DELETE_SUCCESS_MESSAGE, REGISTER_SUCCESS_MESSAGE,
  DELETE_CONFIRM_ACTION_MESSAGE,
  DUPLICATE_HAWBNO_MESSAGE, EMPTY_SELECTION_ERROR_MESSAGE, EMPTY_FIELD_INPUT_ERROR_MESSAGE,
  DIFFERENT_DATA_ERROR_MESSAGE,
  DATA_NOT_EXIST_MESSAGE, INVALID_INPUT_MESSAGE, REQUIRED_INPUT_MESSAGE,
  IMAGE_SIZE_LIMIT_MESSAGE, IMAGE_FILE_TYPE_PDF_MESSAGE, IMAGE_NOT_NULL_MESSAGE, IMAGE_UPLOAD_SUCCESS_MESSAGE, IMAGE_UPLOAD_ERROR_MESSAGE, IMAGE_NOT_EXIST_MESSAGE,
  getDetailSuccessMessage
}