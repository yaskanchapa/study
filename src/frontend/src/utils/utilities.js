import moment from 'moment'

/* ------------------------------ CONSTANTS ------------------------------ */
const NOTIFICATION_DURATION = 3
const DATE_PICKER_FORMAT = 'yyyy/MM/dd'

/* ------------------------------ HELPER FUNCTIONS ------------------------------ */

// Get current date time
const currentDateTime = () => {
  const currentTime = new Date()
  const offset = currentTime.getTimezoneOffset()
  return new Date(currentTime.getTime() + (offset * 60 + 1000)).toISOString().substring(0, 10)
}

// Get the last day of the month
const lastDayOfCurrentMonth = () => {
  const today = new Date()
  const offset = new Date().getTimezoneOffset() / 60
  return new Date(today.getFullYear(), today.getMonth() + 1, 0, 0 - offset).toISOString().substring(0, 10);
}

// Convert date to matching format
const DEFAULT_DATE_FORMAT = 'yyyy/MM/DD'
const dateFormat = (value, format = DEFAULT_DATE_FORMAT) => {
  return moment(value).format(format)
}

// Check if object contain any data or not
const isObjectEmpty = (obj) => {
  for (const key in obj) {
    if (obj[key] !== '') return false // eslint-disable-line
  }
  return true
}

// Find object in array
function containsObject(obj, list) {
  for (let i = 0; i < list.length; i++) {
    if (list[i].value === obj) {
      return true;
    }
  }
  return false;
}

/* 
  Params: HTML Event Object
  Usage: Validate if user input key is a number or not.

  Example: <va-input @keypress="isNumberKey"/>
*/
const isNumberKey = (event) => {
  const characters = String.fromCharCode(event.which);
  // console.log(characters)
  if (!(/[0-9]/.test(characters))) {
    return event.preventDefault()
  }
  return true
}

/* 
  Params: HTML Event Object
  Usage: Validate if the paste value is a string of number or not. If not, prevent user from paste the value

  Example: <va-input :onpaste="checkPasteValue" />
*/
const checkPasteValue = (event) => {
  const text = event.clipboardData.getData('text')
  if (isNaN(Number(text))) event.preventDefault()
}

// GRID COLUMN SORT
const gridColumnSortMethod = (valueA, valueB) => {
  if (valueA == null) return -1;
  if (valueB == null) return 1;
  if (!valueA.substring || !valueB.substring) return valueA - valueB;
  if (valueA.length < 1 || valueB.length < 1) return valueA - valueB;
  if (!isNaN(valueA) && !isNaN(valueB)) {
    // values will come in as strings
    return Number(valueA) - Number(valueB);
  }
  return valueA < valueB ? -1 : valueA > valueB ? 1 : 0;
}
/* ------------------------------------------------------------------------------------------ */

export { DATE_PICKER_FORMAT, NOTIFICATION_DURATION, currentDateTime, lastDayOfCurrentMonth, dateFormat, isObjectEmpty, containsObject, isNumberKey, checkPasteValue, gridColumnSortMethod }

