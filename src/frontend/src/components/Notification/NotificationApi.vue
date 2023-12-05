<script>
import { notification } from 'ant-design-vue';
import 'ant-design-vue/dist/antd.css';

const PLACEMENT = "topRight"
const TOP = "80px"

export function notificationSuccess( _message, _description = '', _duration = 0) {
  notification.success({
    message: _message,
    description: _description,
    duration: _duration,
    placement: PLACEMENT,
    top: TOP
  })  
}

export function notificationError(_message, _description = '', _duration = 0) {
  notification.error({
    message: _message,
    description: _description,
    duration: _duration,
    placement: PLACEMENT,
    top: TOP
  })  
}

export function notificationInfo(_message, _description = '', _duration = 0) {
  notification.info({
    message: _message,
    description: _description,
    duration: _duration,
    placement: PLACEMENT,
    top: TOP
  })  
}

export function notificationWarn(_message, _description = '', _duration = 0) {
  notification.warn({
    message: _message,
    description: _description,
    duration: _duration,
    placement: PLACEMENT,
    top: TOP
  })  
}

export function closeAllNotification() {
  notification.destroy()
}

export default { notificationSuccess, notificationError, notificationInfo, notificationWarn, closeAllNotification }
</script>
