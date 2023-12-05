import { required, minLength, maxLength, helpers } from '@vuelidate/validators'

const NUMBER_REGEX = /^\+?[0-9-]+$/
const ONE_BYTE_REGEX = /^[\x00-\x7F]+$/  // eslint-disable-line
const ALL_CHAR_REGEX = /.*/ // eslint-disable-line

// Custom validations text and rules
const requiredRules = (param) => {
  return helpers.withMessage(() => `${param} を入力してください。`, required)
}
const fieldMinLength = (param, length) => {
  return helpers.withMessage(() => `${param}は${length}文字以上を設定してください。`, minLength(length))
}
const fieldMaxLength = (param, length) => {
  return helpers.withMessage(() => `${param}は${length}文字以下を設定してください。`, maxLength(length))
}
const fieldInputTextValidate = (param, validationRegex) => {
  let inputType = ''
  switch (validationRegex) {
    case NUMBER_REGEX:
      inputType = '数字'
      break;
    case ONE_BYTE_REGEX:
      inputType = '半角英数'
      break;
    default:
      inputType = ''
      break;
  }
  return helpers.withMessage(() => `${inputType}だけを入力して下さい。`, helpers.regex(validationRegex))
}

export { requiredRules, fieldMinLength, fieldMaxLength, fieldInputTextValidate, NUMBER_REGEX, ONE_BYTE_REGEX, ALL_CHAR_REGEX }